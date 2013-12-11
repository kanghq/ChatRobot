package com.hqkang.ChatRobot.Sequence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hqkang.Mysql.MysqlConnect;


public class ChnSeq {
	private static TireNode tire = init();

	/*
	 * 初始化分词Tire node 数据结构
	 */
	private static TireNode init() {

		System.out.println("分词初始化。。时间较长");
		ResultSet rs =null;
		Statement statement=null;
	    try {
			statement = MysqlConnect.connection.createStatement();
		} catch (SQLException e2) {
			System.err.println("数据库连接失败");
		}

		
		String SQL=null;
		SQL="select * from words_dic;";

		try {
			rs = statement.executeQuery(SQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("词典数据库读取失败");
		}
		
		int totalFreq = 900000000;

		tire = new TireNode();

		try {
			while (rs.next()) {
			
				String word = rs.getString(2);
				int freq = Integer.parseInt(rs.getString(4));
				String pos = rs.getString(3).equals("")?"N.A.":rs.getString(3);
				
				TireNode root = tire;
				for (int i = 0; i < word.length(); i++) {
					String c = "" + word.charAt(i);
					TireNode node = root.getChild(c);
					if (node == null) {
						node = new TireNode();
						node.setCharacter(c);
						root.addChild(node);
					}
					root = node;
				}
				
				root.setFrequency(freq);
				root.setAntilog(Math.log((double)totalFreq / freq));
				root.setPart(pos);
			}
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("单词转换为tire节点失败");
		}  finally{
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
		return tire;
	}

	public TireNode getTire() {
		return tire;
	}
	
	public static TireNode getNodeByWord(String word) {
		if (tire == null) {
			System.err.println("需要先初始化ChnSeq对象！");
		}
		
		TireNode node = tire;
		for (int i = 0; i < word.length(); i++) {
			String ch = word.charAt(i) + "";
			if (node == null) {
				break;
			} else {
				node = node.getChild(ch);
			}
		}
		
		return node;
	}
	

	
	private static List<Segment> preSegment(String sentence) {
		List<Segment> segs = new ArrayList<Segment>();
		
		Segment terminal = new Segment();
		terminal.word = Segment.START_SIGN;
		terminal.endChar = Segment.START_SIGN;
		terminal.lastChar = null;
		segs.add(terminal);
		for (int i = 0; i < sentence.length(); i++) {
			for (int j = i + 1; j <= sentence.length(); j++) {
				String word = sentence.substring(i, j);
				TireNode tnode = getNodeByWord(word);
				
				if(j==i+1) {
					Segment seg = new Segment();
					seg.word = word;
					seg.endChar = word.substring(word.length() - 1, word.length());
					seg.endChar = String.valueOf(j-1);
					if (i == 0) {
						seg.lastChar = Segment.START_SIGN;
					} else {
						seg.lastChar = sentence.substring(i - 1, i);
						seg.lastChar = String.valueOf(i-1);
					}
					seg.cost = 100; //单字cost
					seg.part = "CH";
					segs.add(seg);
				}
				
				if (tnode == null) {
					break; //word太长，或无登记
				}
				if (tnode.getFrequency() <= 0) {
					continue; //word太短
				}
				
				
				Segment seg = new Segment();
				seg.word = word;
				seg.endChar = word.substring(word.length() - 1, word.length());
				seg.endChar = String.valueOf(j-1);
				if (i == 0) {
					seg.lastChar = Segment.START_SIGN;
				} else {
					seg.lastChar = sentence.substring(i - 1, i);
					seg.lastChar = String.valueOf(i-1);
				}
				seg.cost = tnode.getAntilog();
				seg.part = tnode.getPart();
				segs.add(seg);
			}
		}
		terminal = new Segment();
		terminal.word = Segment.END_SIGN;
		terminal.endChar = Segment.END_SIGN;
		//terminal.lastChar = sentence.substring(sentence.length() - 1, sentence.length());
		terminal.lastChar = String.valueOf(sentence.length()-1);
		segs.add(terminal);
		
		return segs;
	}
	
	private static MulVHashMap dynamicSegment(List<Segment> segs) {
		final double INFINITE = 9E10;
		
		if (segs == null || segs.size() == 0) {
			return null;
		}
			
		int n = segs.size();
		
		double[][] costs = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				costs[i][j] = INFINITE;
			}
		}
		
		for (int i = 0; i < n; i++) {
			String endChar = segs.get(i).endChar;
			for (int j = 0; j < n; j++) {
				String lastChar = segs.get(j).lastChar;
				
				if (lastChar != null && lastChar.equals(endChar)) {
					costs[i][j] = segs.get(j).cost;
				}
			}
		}
		
		int sp = 0; // starting point
		int fp = n - 1; // finishing point
		
		double[] dist = new double[n];
		List<List<Integer>> sPaths = new ArrayList<List<Integer>>();
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			dist[i] = costs[sp][i];
			if (sp != i) {
				list.add(i);
			}
			if (dist[i] < INFINITE) {
				List<Integer> spa = new ArrayList<Integer>();
				sPaths.add(spa);
			} else {
				sPaths.add(null);
			}
		}
		
		while (!list.isEmpty()) {		
			Integer minIdx = list.get(0);	
			for (int i: list) {
				if (dist[i] < dist[minIdx]) { //行P最大词
					minIdx = i;
				}
			}
			
			list.remove(minIdx);
			
			for (int i = 0; i < n; i++) {
				if (dist[i] > dist[minIdx] + costs[minIdx][i]) { //如果两个词组合P存在，更新表
					dist[i] = dist[minIdx] + costs[minIdx][i];
					List<Integer> tmp = new ArrayList<Integer>(sPaths.get(minIdx)); //取出路径
					tmp.add(minIdx);
					sPaths.set(i, tmp);
				}
			}
		}
		
		String[] result = new String[sPaths.get(fp).size()];
		for (int i = 0; i < sPaths.get(fp).size(); i++) {
			result[i] = segs.get(sPaths.get(fp).get(i)).word+"/"+segs.get(sPaths.get(fp).get(i)).part;
		}
		MulVHashMap res = new MulVHashMap();
		for (int i = 0; i < sPaths.get(fp).size(); i++) {
			res.putAdd(segs.get(sPaths.get(fp).get(i)).part, segs.get(sPaths.get(fp).get(i)).word);
		}
		
		
		return res;
	}
	
	public static MulVHashMap segment(String sentence) {
		return dynamicSegment(preSegment(sentence));
	}
	
}



