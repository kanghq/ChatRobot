package com.hqkang.ChatRobot.VSM;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.hqkang.ChatRobot.Sequence.Sentence2Keywords;
import com.hqkang.Mysql.MysqlConnect;


public class QuestionRetrieval {
	long id = -1;
	public static double[] matrix;
	List<String> merge = new LinkedList<String>();
	public boolean simCal(String questionLine) {
		HashMap<String,Double> que = new HashMap<String,Double>();
		//br.readLine();
		Sentence2Keywords ques = new Sentence2Keywords(questionLine);
		LinkedList<String> qlist = ques.getSegList();
		for(String seg:qlist){
			que.put(seg, Collections.frequency(qlist, seg)*BuildInvertIndex.IDF(seg,Collections.frequency(qlist, seg)));
		}
		
		
		double dist =0.0;
		for(double wei:que.values()){
			dist += wei*wei;
			
		}
		dist = Math.sqrt(dist);
		int maxSenIndex = 0;
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
		} catch (SQLException e2) {
			System.err.println("数据库连接失败");
		}

		ResultSet rs =null;
		String SQL=null;
		try
	    {	HashSet<Long> docSet = new HashSet<Long>();
	    	HashSet<String> segSet = new HashSet<String>();
	    	HashMap<Long,Integer> docMap = new HashMap<Long,Integer>();
	    	HashMap<Integer,Long> docMapRev = new HashMap<Integer,Long>();

	    	ArrayList<HashMap<String,Double>> docList = new ArrayList<HashMap<String,Double>>();
			HashMap<String,List<WordDoc>>inverseDoc = new HashMap<String,List<WordDoc>>();
			int i = 0;
			for(String seg:que.keySet()){ //得到包含问题关键词的知识库文章id
				segSet.add(seg);
				SQL="select distinct tieba_id from invert_index where seg like '"+seg+"';";
				rs = statement.executeQuery(SQL);
				while(rs.next()) {
					docSet.add(rs.getLong(1));
					if(!docMap.containsKey(rs.getLong(1))) {
						docMap.put(rs.getLong(1), i);
						docMapRev.put(i, rs.getLong(1));
						i++;
					}
				}
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						System.err.println("关闭ResultSet时出错");
					}
				
			}
			if(i==0) return false;
			
			for(long tieba_id:docSet){ //得到包含关键词的知识库文章(id)的完整关键词
				HashMap<String,Double> oneDoc = new HashMap<String,Double>();
				SQL="select distinct seg,TFIDF from invert_index where tieba_id = '"+tieba_id+"';";
				rs = statement.executeQuery(SQL);
				while(rs.next())
				{
					segSet.add(rs.getString(1));
					oneDoc.put(rs.getString(1), rs.getDouble(2));
				}
				
				docList.add(oneDoc);
			}

			double maxIndex = 0; 
			for(String seg:segSet){
				boolean newWd = false;
				SQL="select tieba_id,TFIDF from invert_index where seg like '"+seg+"';";
				rs = statement.executeQuery(SQL);
				List<WordDoc> wd = new ArrayList<WordDoc>();
				while(rs.next()) {
					newWd = false;
					if(docSet.contains(rs.getLong(1))) {
						newWd = true;
						WordDoc wde = new WordDoc();
						wde.tieba_id = rs.getLong(1);
						wde.weight = rs.getDouble(2);
						maxIndex = wde.weight>maxIndex?wde.weight:maxIndex;
						wd.add(wde);
					}
				}
				if(newWd) inverseDoc.put(seg, wd);
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						System.err.println("关闭ResultSet时出错");
					}
			}
			matrix = new double[docSet.size()];	
			  	
			for(Entry<String, List<WordDoc>> e:inverseDoc.entrySet()){//entry 是倒排索引的某一个关键词的文章link，wdList,wdList内为某文章的wd.tieba_id,weight
				List<WordDoc> wdList = e.getValue();
				for(int j=0;j<wdList.size();j++){
					WordDoc wd1 = wdList.get(j);
					maxIndex = que.get(e.getKey())!=null&&que.get(e.getKey()).doubleValue()>maxIndex?que.get(e.getKey()).doubleValue():maxIndex;
					matrix[docMap.get(wd1.tieba_id)]+=wd1.weight*(que.get(e.getKey())==null?0:que.get(e.getKey()));
				}
			}
			double ftemp = 0; 
			ArrayList<Double> fmList = new ArrayList<Double>();
			for(HashMap<String,Double> oneDoc:docList){
				for(Entry<String,Double> e:oneDoc.entrySet()){
					ftemp+=e.getValue()*e.getValue();    
				}
				ftemp = Math.sqrt(ftemp);
				fmList.add(ftemp);		
			}
			for(int k=0;k<matrix.length;k++){
				matrix[k]/=fmList.get(k)*dist;
			}
			
			
			for(int l=1;l<matrix.length;l++){
				if(matrix[maxSenIndex]<matrix[l]) maxSenIndex = l;
			}
			
			Long tieba_id = docMapRev.get(maxSenIndex);
			id =  tieba_id;
			SQL = "select  content,abstract from tieba where id ="+tieba_id+";";
			rs = statement.executeQuery(SQL);
			while(rs.next()) System.out.println(rs.getString(1)+rs.getString(2));
						
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
		
		return true;
	}
	
	public String getAns() {
		String answer = "";
		Statement statement=null;
		ResultSet rs = null;
		String SQL = null;
		long pageId = -1;
	    try {
			statement = MysqlConnect.connection.createStatement();
		} catch (SQLException e2) {
			System.err.println("数据库连接失败");
		}
	    SQL = "select pageId,class from tieba where id = "+id+" and floor = 0 ;";
		try {
			rs = statement.executeQuery(SQL);
			String classes=null;
		
			while(rs.next()){
				pageId = rs.getLong(1);
				classes = rs.getString(2);
			}
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("关闭ResultSet时出错");
				}
			
			SQL = "select content from tieba where floor <> 0 and floor<> 1 and pageId="+pageId+" and class like '"+classes+"' order by floor asc ;";
			rs = statement.executeQuery(SQL);
			if(rs.getRow()<1) {
				rs.close();
				SQL = "select content from tieba where floor <> 0 and floor<> 1 and pageId="+pageId+" order by floor asc ;";
				rs = statement.executeQuery(SQL);
			}
			int m = 0;
			while(rs.next()) {
				m++; 
				if(m<6&&answer.length()<rs.getString(1).length())
					answer = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("问题答案检索失败");
		}
		if(!answer.equals("")) return answer;
		else return"not found reply";
	}
	

}
