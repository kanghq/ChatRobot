package com.hqkang.ChatRobot.VSM;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.hqkang.Mysql.MysqlConnect;

public class BuildInvertIndex {
	private static HashMap<String,Double> TF = new HashMap<String,Double>();
	private static HashMap<String,Double> IDF = new HashMap<String,Double>();
	public double TF(long tieba_id,String word) {
		
		String mapKey = tieba_id+"\t"+word;
		if(!TF.containsKey(mapKey)) { 
			Statement statement=null;
			double ret = 0.0;
		    try {
				statement = MysqlConnect.connection.createStatement();
			} catch (SQLException e2) {
				System.err.println("数据库连接失败");
			}
	
			ResultSet rs =null;
			String SQL=null;
			try
		    {	
				
	
				SQL="select count(*) from segments where tieba_id = '"+tieba_id+"' and seg like '"+word+"'";
	
				rs = statement.executeQuery(SQL);
				while(rs.next()) ret = rs.getInt(1);
			} catch(SQLException e)
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
	
			TF.put(mapKey, ret);
			return ret;
		} else return TF.get(mapKey);
	}
	
	public static double IDF(String key) {
		
		if(!IDF.containsKey(key)) {
			Statement statement=null;
			double ret = 0.0;
			long N = 0L;
		    try {
				statement = MysqlConnect.connection.createStatement();
			} catch (SQLException e2) {
				System.err.println("数据库连接失败");
			}
	
			ResultSet rs =null;
			ResultSet rs2 =null;
			String SQL=null;
			try
		    {
				SQL = "SELECT COUNT( DISTINCT tieba_id ) FROM segments WHERE seg LIKE  '"+key+"'";
	
				rs = statement.executeQuery(SQL);
				while(rs.next()) ret = rs.getInt(1);
				if(ret==0) ret = 1; 
				SQL ="select count(id) from tieba where floor = 0";
				rs2 = statement.executeQuery(SQL);
				while(rs2.next())N = rs2.getLong(1);
				
			} catch(SQLException e)
		    {
		    	System.err.println("出错语句为:"+SQL);
		    }finally{
		    	if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						System.err.println("关闭ResultSet时出错");
					}
		    	if(rs2!=null)
					try {
						rs2.close();
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
	
			ret = Math.log(N/ret)+1;
			IDF.put(key, ret);
			return ret;
		} else return IDF.get(key);

	}
	
public static double IDF(String key,int freq) {
		
		
		Statement statement=null;
		double ret = 0.0;
		long N = 0L;
	    try {
			statement = MysqlConnect.connection.createStatement();
		} catch (SQLException e2) {
			System.err.println("数据库连接失败");
		}

		ResultSet rs =null;
		ResultSet rs2 =null;
		String SQL=null;
		try
	    {
			SQL = "SELECT COUNT( DISTINCT tieba_id ) FROM segments WHERE seg LIKE  '"+key+"'";

			rs = statement.executeQuery(SQL);
			while(rs.next()) ret = rs.getInt(1);
			if(ret==0) ret = ret+freq; 
			SQL ="select count(id) from tieba where floor = 0";
			rs2 = statement.executeQuery(SQL);
			while(rs2.next())N = rs2.getLong(1);
			
		} catch(SQLException e)
	    {
	    	System.err.println("出错语句为:"+SQL);
	    }finally{
	    	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("关闭ResultSet时出错");
				}
	    	if(rs2!=null)
				try {
					rs2.close();
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

		ret = Math.log(N/ret)+1;

		return ret;

	}
	
	public  double TFIDF(long tieba_id,String word) {
		return TF(tieba_id,word)*IDF(word);
	}
	
	public void build() {
		Statement statement = null;
		 try {
				statement = MysqlConnect.connection.createStatement();
			} catch (SQLException e2) {
				System.err.println("数据库连接失败");
			}

			ResultSet rs =null;

			String SQL=null;
			try
		    {
				SQL = "TRUNCATE invert_index";
				statement.executeUpdate(SQL);
				
				SQL = "SELECT distinct seg, tieba_id FROM segments";

				rs = statement.executeQuery(SQL);
				
				while(rs.next()) {

					insert(rs.getString(1),Long.parseLong(rs.getString(2)));
				}
				
			} catch(SQLException e)
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
			
			
			
	}
	
	private void insert(String word,long tieba_id) throws SQLException
	  {
		String SQL=null;
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
			word = word.replaceAll("'","''");
			word = word.replaceAll("\\\\","\\\\\\\\"); //\反斜杠替换\替换成\\才可插入sql 单引号需要变两个
		  	SQL="insert into invert_index (`id`,`seg`,`tieba_id`,`TFIDF`) values (null,'"+word +"','"+tieba_id+"','"+TFIDF(tieba_id,word)+"');";
	    	statement.executeUpdate(SQL);
	    }
	    catch(SQLException e)
	    {
	      System.err.println("插入数据库时出错");
	      System.err.println(SQL);
	    }finally{
	    	if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("关闭Statement时出错");
				}
	    }
	  }
	

	
}
