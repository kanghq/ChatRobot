package com.hqkang.ChatRobot.Bayes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.hqkang.Mysql.MysqlConnect;


/**
* 训练集管理器
*/
public class TrainingDataManager {
	private static Set<String> trainingFileClassifications = init();//训练语料分类集合
	private static int trainingFileCount = getTrainingFileCount();
	private static HashMap<String,Integer> trainingFileCountOfClassification = new HashMap<String,Integer>();
    
	public static Set<String> init() 
    {	
		if(MysqlConnect.connection==null) {
    		System.err.println("请先连接数据库");
    		System.exit(0);
    	}
        
		Set<String> trainingFileClassifications = new HashSet<String>();//训练语料分类集合
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
			SQL="select distinct class from tieba_sample";
			rs = statement.executeQuery(SQL);
			while(rs.next()){
				trainingFileClassifications.add(rs.getString(1));
			}
				
			
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
		
		return trainingFileClassifications;
		
   
    }
    public static Set<String> getTrainingClassifications() {
    	if (trainingFileClassifications!=null) 
    		return trainingFileClassifications;
    	else {
    		return init();
    	}
    }
    

    
    /**
    * 根据训练文本类别返回这个类别下的所有训练文本路径（full path）
    * @param classification 给定的分类
    * @return 给定分类下所有文件的路径（full path）
    */
    /*
    public String[] getFilesPath(String classification) 
    {
        File classDir = null;
        String[] ret = classDir.list();
        for (int i = 0; i < ret.length; i++) 
        {
           // ret[i] = traningTextDir.getPath() +File.separator +classification +File.separator +ret[i];
        }
        return ret;
    }
    */
    /**
    * 返回给定路径的文本文件内容
    * @param filePath 给定的文本文件路径
    * @return 文本内容
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    /*
    public static String getText(String filePath) throws FileNotFoundException,IOException 
    {
        InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"GBK");
        BufferedReader reader = new BufferedReader(isReader);
        String aline;
        StringBuilder sb = new StringBuilder();
        while ((aline = reader.readLine()) != null)
        {
            sb.append(aline + " ");
        }
        isReader.close();
        reader.close();
        return sb.toString();
    }
    */
    
    /**
    * 返回训练文本集中所有的文本数目
    * @return 训练文本集中所有的文本数目
    */
    public static int getTrainingFileCount()
    {	
    	if(trainingFileCount==0) {
        int ret = 0;
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
			SQL="select count(id) from tieba_sample";
			rs = statement.executeQuery(SQL);
			if(rs.next()) {
				
				ret = Integer.parseInt(rs.getString(1));
			}
			
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
		return ret;
    	} else return trainingFileCount;

    }
    /**
    * 返回训练文本集中在给定分类下的训练文本数目
    * @param classification 给定的分类
    * @return 训练文本集中在给定分类下的训练文本数目
    */
    public static int getTrainingFileCountOfClassification(String classification)
    {
    	if (!trainingFileCountOfClassification.containsKey(classification)) {
	    	int ret = 0;
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
				SQL="select count(id) from tieba_sample where class = '"+classification+"'";
				rs = statement.executeQuery(SQL);
				if(rs.next()) {
					
					ret = Integer.parseInt(rs.getString(1));
				}
				
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
			trainingFileCountOfClassification.put(classification, ret);
			return ret;
    	} else 
    		return trainingFileCountOfClassification.get(classification);
       
    }
    /**
    * 返回给定分类中包含关键字／词的训练文本的数目
    * @param classification 给定的分类
    * @param key 给定的关键字／词
    * @return 给定分类中包含关键字／词的训练文本的数目
    */
    public static int getCountContainKeyOfClassification(String classification,String key) {
	    	 int ret = 0;
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
	  			SQL="select count(tieba_sample.id) from tieba_sample,segments_sample where tieba_sample.id=segments_sample.tieba_id and segments_sample.seg='"+key+"' and tieba_sample.class='"+classification+"'";
	  			rs = statement.executeQuery(SQL);
	  			if(rs.next()) {
	  				
	  				ret = Integer.parseInt(rs.getString(1));
	  			}
	  			
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
	  		
	        return ret;
    }
}