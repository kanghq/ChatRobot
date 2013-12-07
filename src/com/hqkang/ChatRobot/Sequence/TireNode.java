package com.hqkang.ChatRobot.Sequence;

import java.util.HashMap;
import java.util.Map;


public class TireNode {
	private String character;
	private int frequency = -1;
	private double antilog = -1;
	private String pos;
	private Map<String, TireNode> children;
	
	public String getCharacter() {
		return character;
	}
	
	
	public void setCharacter(String character) {
		this.character = character;
	}
	
	public String getPart() {
		return pos;
	}
	
	public void setPart(String pos) {
		this.pos = pos;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public double getAntilog() {
		return antilog;
	}
	
	public void setAntilog(double antilog) {
		this.antilog = antilog;
	}
	
	public void addChild(TireNode node) {
		if (children == null) {
			children = new HashMap<String, TireNode>();
		}
		
		if (!children.containsKey(node.getCharacter())) {
			children.put(node.getCharacter(), node);
		}
	}
	
	public TireNode getChild(String ch) {
		if (children == null || !children.containsKey(ch)) {
			return null;
		}
		
		return children.get(ch);
	}
	
	public void removeChild(String ch) {
		if (children == null || !children.containsKey(ch)) {
			return;
		}
		
		children.remove(ch);
	}

}
