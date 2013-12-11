package com.hqkang.Mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class MysqlConnect {
	public static Connection connection = init();
	
	
	private static Connection init() {
		Connection tempCon = connection;
		boolean web = false;
		StackTraceElement[] elements = new Throwable().getStackTrace();
		for (StackTraceElement ele:elements)
		{
		   if(ele.toString().contains("Servlet")) web = true;
		}
		if(!web) {
		String username = null;
		String passwd = null;
		File file = new File("data" + File.separator + "conf.txt");
		if (!file.isFile()) {
			System.err.println("数据库用户名密码不存在！终止程序！");
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

		try{
			username = in.readLine();
			passwd = in.readLine();
			if(passwd == null) passwd = "";
			in.close();
		} catch (IOException e) {
			System.err.println("用户名密码读取错误");
		} 
		return connect(username,passwd);
		} else return tempCon;
	}
	
	public static Connection connect(String user,String passwd) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("找不到MySql驱动程序包");
		}
		String connUrl="jdbc:mysql://localhost:3306/tieba?useUnicode=true&characterEncoding=UTF-8&user="+user+"&password="+passwd+"";
		try {
			connection= DriverManager.getConnection(connUrl);
		      Statement statement = connection.createStatement();
		      //statement.executeUpdate("create table  if not exists `tieba`  (id int(10) primary key AUTO_INCREMENT, pageId varchar(20), floor int(10),content text)engine=MyISAM default charset=utf8");
		      //statement.executeUpdate("ALTER TABLE tieba ADD INDEX (pageId) if not exists");
		      if(statement!=null) statement.close();
		} catch (SQLException e) {
			System.err.println("Tieba数据库连接失败，请检查数据库及连接字符串");
		}
		return connection;
	} 

}
