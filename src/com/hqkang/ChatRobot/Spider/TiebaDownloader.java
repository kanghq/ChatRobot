package com.hqkang.ChatRobot.Spider;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import com.hqkang.ChatRobot.Main;


public class TiebaDownloader implements Runnable{
	CountDownLatch latch = null;
	String address =null;
	String classes = null;
	int pages;
	
	CountDownLatch tDLatch=null;

	int i = 0;
	HashMap<String, String[]> hsmap = null;

	
	public TiebaDownloader(String address,String classes,int pages,int i,CountDownLatch latch) {
		this.address = address;
		this.latch = latch;
		this.classes = classes;
		this.pages = pages;
		this.i = i;

	}
	
	@Override
	/**
	 * 启动子线程，下载贴吧的每一页
	 */
	
	public void run() {
		MainPageDownloader td = null;
		try {
		System.out.println("下载第"+i+"页，"+pages+"是最后一页");
		hsmap = Tieba.getHomePageHashMap(address+"&pn="+i*50);
		tDLatch=new CountDownLatch(hsmap.size());
		System.out.println("hsmapsize"+hsmap.size());
		
		System.out.println("主页下载完毕，准备解析");
		
		for(String s:hsmap.keySet()){ //某一页的主题帖集合页面
			
			String path = null;
			String abs;
			path=hsmap.get(s)[0].trim().replace(".", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
			abs = hsmap.get(s)[1].trim().replace(".", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
			path = path.replace("'","").replace("\\","");
			abs = abs.replace("'","").replace("\\","");
			
			td = new MainPageDownloader(s,path,abs,classes,tDLatch);
			Main.pool.submit(new Thread(td));
			
		}
		
		} catch (IOException e) {
			System.err.println("获取主页数据失败，请检查网络连接");
			
		} finally {
			try {
				if(tDLatch!=null) {
					tDLatch.await();
				}
			} catch (InterruptedException e) {
				System.err.println("线程池等待失败");
			} finally { 
				latch.countDown();
				System.out.println("Main"+latch.getCount());}
			
			
		}

	}
		

}
