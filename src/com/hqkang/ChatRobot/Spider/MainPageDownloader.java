package com.hqkang.ChatRobot.Spider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hqkang.ChatRobot.Main;
import com.hqkang.ChatRobot.Bayes.BayesClassifier;
import com.hqkang.ChatRobot.Sequence.Sentence2Keywords;
import com.hqkang.Mysql.MysqlConnect;


public class MainPageDownloader implements Runnable {
	CountDownLatch latch = null;
	String dPage = null;
	String path = null;
	String abs = null;
	long pageId = 0;
	String classes = null;
	

	/**
	 * 帖子主题类
	 * @param dPage 主题地址
	 * @param path 主题
	 *
	 */
	

	
	
	public MainPageDownloader(String dPage,String path,String abs,String classes,CountDownLatch latch) {
		this.dPage = dPage;
		this.path = path;
		String pageId = getMatcher("p/[1-9][0-9]{0,}",dPage); 
		this.pageId = Long.parseLong(pageId.substring(2));
		this.latch = latch;
		this.abs = abs;
		this.classes = classes;
	} 

	@Override
	public void run() {
		// TODO Auto-generated method stub


	Map<String,String> map;
	
		try {
			if(!urlIsExits(String.valueOf(pageId))) {
	
				Sentence2Keywords keywords = new Sentence2Keywords(path);
				Sentence2Keywords absKeywords = new Sentence2Keywords(abs);
				@SuppressWarnings("unchecked")
				LinkedList<String> merge = (LinkedList<String>) keywords.getSegList().clone();
				merge.addAll(absKeywords.getSegList());
				
				String classes;
				if (!Main.DEBUG) {
					classes = BayesClassifier.classify(merge);//进行分类
				} else {
					classes = this.classes;
				}
				System.out.println("插入数据库，帖子标题为"+path);
				long keyValue = insert(pageId,0,path,abs,classes);
				if(keyValue!=-1) {
					keywords.addSegs(keyValue);
					absKeywords.addSegs(keyValue);
				}
				//二楼及以上插入
				if(!Main.DEBUG) {
					System.out.println("准备下载子页面，标题为："+path);
					map = Tieba.getDetailsPageImageList(dPage);
					System.out.println(path+"：子页面下载完毕，正在解析");
					for(Entry<String,String> sentence:map.entrySet()){
						if(sentence!=null&&sentence.getValue().length()>4){
							try {
								String sentenceVal = sentence.getValue().replace(".", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
								sentenceVal = sentenceVal.replace("'","").replace("\\","");
								Sentence2Keywords secPlusConten = new Sentence2Keywords(sentenceVal);
								String secplusClasses = BayesClassifier.classify(secPlusConten.getSegList());
								insert(pageId,Integer.parseInt(sentence.getKey()),sentenceVal,"",secplusClasses);
							} catch (SQLException e) {
								System.err.println("数据库连接失败，未能将句子保存在数据库中");
							}
							
						}
						System.out.println("-title:-"+path+"\t"+sentence);
					}
				}
			}
		} catch (SQLException sqle) {
			System.err.println("数据库插入失败，未能将帖子标题保存在数据库中");
		}
		finally{
			latch.countDown();
			System.out.println(Thread.currentThread()+"::"+latch.getCount());
		}
	}
	
	/**
	 * 判断是已存在此主题
	 * @param postId
	 * @return
	 * @throws SQLException
	 */
	private static boolean urlIsExits(String postId){
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
		} catch (SQLException e2) {
			System.err.println("数据库连接失败");
		}

		ResultSet rs =null;
		String SQL=null;
		try
	    {
			if(Main.DEBUG) {
				SQL="select * from tieba_sample where pageId='"+postId+"'";
			} else {
				SQL="select * from tieba where pageId='"+postId+"'";
			}
			rs = statement.executeQuery(SQL);
			boolean b=false;
			if(rs!=null){
		      while(rs.next()){
		    	  b= true;
		      }
	      }
	      return b;
	    }
	    catch(SQLException e)
	    {
	    	System.err.println("出错语句为:"+SQL);
	    }finally{
	    	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("关闭ResultSet时出错");
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("关闭Statement时出错");
				}
	    }
		return false;
	}
	
	/**
	 * 插入函数
	 * @param pageId 帖子id号
	 * @param floor 楼层号
	 * @param content 内容
	 * @throws SQLException
	 */
	private static long insert(long pageId,int floor,String content,String abs,String classes) throws SQLException
	  {
		String SQL = null;
		Statement statement=null;
		long keyValue = -1;
	    try {
			statement = MysqlConnect.connection.createStatement();
			if(Main.DEBUG) {
				SQL="insert into tieba_sample (id,pageId,floor,content,abstract,class) values (null,"+pageId+",'"+floor+"','"+content +"','"+abs+"','"+classes+"')";
			} else {
				SQL="insert into tieba (id,pageId,floor,content,abstract,class) values (null,"+pageId+",'"+floor+"','"+content +"','"+abs+"','"+classes+"')";
			}
	    	statement.executeUpdate(SQL);
	    	ResultSet rs = statement.getGeneratedKeys();
	    	
	    	if (rs.next()) {
	    		keyValue = rs.getInt(1);
	    	}
	    	
	    }
	    catch(SQLException e)
	    {
	      System.err.println("插入数据库时出错"+SQL);
	    }finally{
	    	if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("关闭Statement时出错");
				}
	    }
	    return keyValue;
	  }
	
	/**
	 * 正则提取帖子id
	 * @param regex
	 * @param source
	 * @return 
	 */
	public static String getMatcher(String regex, String source) {  
        String result = "";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(source);  
        while (matcher.find()) {  
            result = matcher.group();//只取第一组  
        }  
        return result;  
	}
	
}
