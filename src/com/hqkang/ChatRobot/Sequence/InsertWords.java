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


public class InsertWords {
	
	private static void insert(String word,String freq,String attr) throws SQLException
	  {
		String SQL=null;
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
			word = word.replaceAll("'","''");
			word = word.replaceAll("\\\\","\\\\\\\\"); //\反斜杠替换\替换成\\才可插入sql 单引号需要变两个
		  	SQL="insert into words_dic (`id`,`word`,`attr`,`freq`) values (null,'"+word +"','"+attr+"','"+freq+"');";
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
	
		
		File file = new File("data" + File.separator + "dict.txt");
		if (!file.isFile()) {
			System.err.println("词库库不存在！终止程序！");
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
			System.err.println("文件找不到");
		}
		String line = null;

	
		try {
			System.out.println("开始插入分词表");
			while ((line = in.readLine()) != null) {
				try {
					String[] sep = line.split("\t");
					if(sep.length==3) {
						insert(sep[0],sep[1],sep[2]);
					}	else {
						insert(sep[0],sep[1],"");
					}
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.err.println("SQL词语插入错误");
				}
			}
			System.out.println("插入封面词表结束");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("文件读取失败");
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("文件关闭失败");
		}
	}

}
