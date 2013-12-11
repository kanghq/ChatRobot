package com.hqkang.ChatRobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hqkang.ChatRobot.Sequence.InsertStopWords;
import com.hqkang.ChatRobot.Sequence.InsertWords;
import com.hqkang.ChatRobot.Spider.TiebaDownloader;
import com.hqkang.ChatRobot.VSM.BuildInvertIndex;
import com.hqkang.ChatRobot.VSM.QuestionRetrieval;
import com.hqkang.Mysql.MysqlConnect;


public class Main {
	public static boolean DEBUG = false;
	
	public static ExecutorService pool=null;
	static String address =null;
	static String classes = null;
	static int pages;
	static int startpage = 0;
	public static void main(String[] args) {
		
		if(!(args.length==1|args.length==4|args.length==5)) {
			System.err.println("参数错误");
			System.exit(0);
		}

		
		
		if (args.length==5&&args[0].equals("DEBUG")) {
			DEBUG = true;
			classes = args[4];
			address = args[1];
			pages = Integer.parseInt(args[3]);
			startpage = Integer.parseInt(args[2]);
			pool=Executors.newCachedThreadPool();
		} 
		

		
		if(args.length==4&&args[0].equals("COLLECT")) {
			
			
			address = args[1];
			startpage = Integer.parseInt(args[2]);
			pages = Integer.parseInt(args[3]);
			if(pages - startpage >15) {
				System.err.println("下载页面太多");
				System.exit(0);
			}
			pages = Integer.parseInt(args[3]);
			pool = Executors.newFixedThreadPool(20);
			
		}

			
		
		
		
		
		if(args.length==1) {
			if(args[0].equals("BUILDINDEX")) {
				System.out.println("开始倒排索引建立");
				new BuildInvertIndex().build();
				System.out.print("倒排索引建立完毕");
			}
			if(args[0].equals("insertStopWords")){
				InsertStopWords.init();
			}
			if(args[0].equals("insertWords")) {
				InsertWords.init();
			}
		} 
			
		

		if(args[0].equals("QUESTION")) {
			QuestionRetrieval q = new QuestionRetrieval();
			System.out.println("请提问");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				if(q.simCal(br.readLine())) {
					System.out.println(q.getAns());
				} else{
					System.out.println("句子太短");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("输入失败");
				System.exit(0);
			}
		}
			
			
		
		
		if(args.length>=3){
			
			CountDownLatch latch=null;
			
			latch=new CountDownLatch(pages-startpage+1);
			for(int i = startpage-1;i<=pages-1;i++) {
				
				TiebaDownloader td= new TiebaDownloader(address,classes,pages,i,latch);
				pool.submit(new Thread(td));
				
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				System.err.println("线程池等待失败");
			}
		}
			if(MysqlConnect.connection!=null)
				try {
					MysqlConnect.connection.close();
				} catch (SQLException e) {
					System.err.println("数据库连接关闭失败");
				}
			if(pool!=null) {
				pool.shutdown();
				System.out.println("线程池关闭");
			}
			
		
		
		
		
	}

}
