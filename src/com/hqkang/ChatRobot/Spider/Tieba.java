package com.hqkang.ChatRobot.Spider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Tieba {
	
	public static Connection connect(String url) {
		return Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
		
	}
	
	
	public static HashMap<String,String[]> getHomePageHashMap(String homePage) throws IOException{
		HashMap<String,String[]> hs=new HashMap<String,String[]>();
		if(homePage.contains("http://tieba.baidu.com/")){
			
			Document doc = connect(homePage).get();
		     //  Elements links = doc.select("a[href*=/p/]");
			Elements links = doc.select("div[class=threadlist_li_right j_threadlist_li_right]");
		       for (Element link : links) {
		    	   String linkString = link.select("a[href*=/p/]").attr("abs:title");
		    	   String linkAbs = link.select("div[class=threadlist_abs threadlist_abs_onlyline]").first().text();
		    	   if(linkString!=null&&(!linkString.equals(""))&&linkAbs!=null&&(!linkAbs.equals(""))&&linkString.length()>=5){
		    		   String[] value = {link.select("a[href*=/p/]").attr("title").replace("http://tieba.baidu.com/","").replace("/", "").replace("\\", ""),linkAbs}; 
			           hs.put(link.select("a[href*=/p/").attr("abs:href"),value);
		    	   }
		       }
			
			return hs;
		}else{
			return null;
		}
	}
	
	
	
		
		
	
	
	
	public static Map<String,String> getDetailsPageImageList(String detailsPage)  {
		if(detailsPage.contains("http://tieba.baidu.com/p/")){
	        Map<String,String> set=new HashMap<String,String>();
			//读取第一页，查看一共有多少页
			//detailsPage=detailsPage+"?see_lz=1";
			Document doc = null;
			try {
				doc = connect(detailsPage).get();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				System.err.println("获取帖子第一页失败");
			}
			//获得本帖子共有多少页
			Elements totalPage = doc.select("span[class=red]");
			int pageNumber=0;
			 for (Element src : totalPage) {
				 try{
					 if(pageNumber==0){
						 pageNumber=Integer.parseInt(src.text());
					 }
				 }catch(Exception e){
					 System.out.println("总页码数转换失败");
				 }
			 }
	
			try {
				for(int i=1;i<=pageNumber;i++){
					doc = connect(detailsPage+"?pn="+i).get();
					Elements posts = doc.select("div[class~=l_post.]");
		     	  for(Element sentence : posts) {
	        		Elements txt;
	        		String floor;
	        		txt = sentence.select("div[id~=post_content.]");
	        		floor =  MainPageDownloader.getMatcher("\"floor\":[1-9][0-9]{0,}", sentence.attr("data-field"));
	        		floor = floor.substring(8, floor.length());
	        		set.put(floor, txt.text());
		     	  }
		     	  System.out.println(detailsPage+"翻页中，第"+i+"页，共"+pageNumber+"页");
				}
			} catch (IOException e1) {
			// TODO Auto-generated catch block
				System.err.println("多页帖子获取失败");
				if(set.size()!=0) return set;
			} return set;
		}else{
			return null;
		}
	}

}
