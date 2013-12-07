package com.hqkang.ChatRobot.Sequence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.hqkang.ChatRobot.Main;
import com.hqkang.Mysql.MysqlConnect;


public class Sentence2Keywords {
	Statement statement = null;
	ResultSet rs = null;
	String SQL = null;
	boolean DEBUG = Main.DEBUG;
	private LinkedList<String> segInList = new LinkedList<String>();
	
		
	public LinkedList<String> eliminate(MulVHashMap segs) {
		segs.partRemove("CH");
		segs.partRemove("ADV");
		segs.partRemove("ECHO");
		segs.partRemove("STRU");
		segs.partRemove("AUX");
		segs.partRemove("COOR");
		segs.partRemove("CONJ");
		segs.partRemove("SUFFIX");
		segs.partRemove("PREFIX");
		segs.partRemove("PREP");
		segs.partRemove("PRON");
		LinkedList<String> noStopWordsList = new LinkedList<String>();
		for(LinkedList<String> llsegs:segs.values()) {
			for(String stopword:llsegs) {
				if(!stopWordExist(stopword)) {
					noStopWordsList.add(stopword);
				}
			}
		}
		return noStopWordsList;
	
	}
	
	private static boolean stopWordExist(String stopword){
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
			SQL="select * from stopwords where word='"+stopword+"'";
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
	
	private void InsertSeg(long keyValue,String seg) {
		Statement statement=null;
		String SQL = null;
	    try {
			statement = MysqlConnect.connection.createStatement();
			if(DEBUG) {
				SQL ="insert into segments_sample (id,seg,tieba_id) values (null,'"+seg+"','"+keyValue +"')";
			} else {
				SQL ="insert into segments (id,seg,tieba_id) values (null,'"+seg+"','"+keyValue +"')";
			}
	    	statement.executeUpdate(SQL);
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
		
	}

	
	public Sentence2Keywords(String sentence) {
		
		
		
		//String sentence = "测试<>《》！*(^)$%~!@#$…&%￥—+=、。，；‘’“”：·`文本比如hash、改造的trie树等方法，搜搜更明白！";
		LinkedList<String> pharases = new LinkedList<String>();//保存的 去符号和数字的原始已分段的文本句子
		sentence = sentence.replaceAll("[\\pP‘’“”|\\pS|[A-Za-z0-9]+$]", "\t");
		StringTokenizer st = new StringTokenizer(sentence,"\t");
		while(st.hasMoreTokens()) {
			String sen = st.nextToken();
			if (sen.length()>2)
				pharases.add(sen);
				
		}
		for(String sen:pharases) {
			MulVHashMap segs = ChnSeq.segment(sen);
			segInList.addAll(eliminate(segs));
		}
		
	}
	
	
	public LinkedList<String> getSegList() {
		return segInList;
		
	}
	
	public void addSegs(long keyValue) {
		for(String seg:segInList)
			InsertSeg(keyValue,seg);
		
	}
		

		

}
