package com.hqkang.ChatRobot.Sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Statement;

import com.hqkang.Mysql.MysqlConnect;


public class InsertStopWords {
	
	
	private static void insert(String content) throws SQLException
	  {
		String SQL=null;
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
			content = content.replaceAll("'","''");
			content = content.replaceAll("\\\\","\\\\\\\\"); //\反斜杠替换\替换成\\才可插入sql 单引号需要变两个
		  	SQL="insert into stopwords (id,word,attr,freq) values (null,'"+content +"',null,null)";
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

	public static void init () {
		File file = new File("data" + File.separator + "stopwords.txt");
		if (!file.isFile()) {
			System.err.println("停用词库库不存在！终止程序！");
			System.exit(0);
		}
		

		
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			System.err.println("不支持的编码方式");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.err.println("未找到文件");
		}
		String line = null;
		
		try {
			System.out.println("开始插入停用词表");
			while ((line = in.readLine()) != null) {
				try {
					insert(line);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.err.println("SQL停用词插入错误");
				}
			}
			System.out.println("停用词表插入结束");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("读取停用词错误");
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("停用词文件关闭错误");
		}
	}

}

