package com.jwatson.omnigame;

public class MessageInfo {
	float x;
	float y;
	int ID;
	boolean spawn;
	
	public MessageInfo(int ID, float x, float y, boolean spawn) {
		this.x = x;
		this.y = y;
		this.ID = ID;
		this.spawn = spawn;
	}
}
