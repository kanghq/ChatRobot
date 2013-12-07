package com.hqkang.Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class MysqlConnect {
	public static Connection connection = init();
	private static Connection init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("找不到MySql驱动程序包");
			System.exit(0);
		}
		String connUrl="jdbc:mysql://localhost:3306/tieba?useUnicode=true&characterEncoding=UTF-8&user=root&password=";
		try {
			connection= DriverManager.getConnection(connUrl);
		      Statement statement = connection.createStatement();
		      statement.executeUpdate("create table  if not exists `tieba`  (id int(10) primary key AUTO_INCREMENT, pageId varchar(20), floor int(10),content text)engine=MyISAM default charset=utf8");
		      //statement.executeUpdate("ALTER TABLE tieba ADD INDEX (pageId) if not exists");
		      if(statement!=null) statement.close();
		} catch (SQLException e) {
			System.err.println("Tieba数据库连接失败，请检查数据库及连接字符串");
			System.exit(0);
		}
		return connection;
	} 

}
