package com.jwatson.omnigame;

import com.badlogic.gdx.math.Vector2;

public class dmgText {
	
	Vector2 pos;
	Vector2 lerpTarget;
	float amt;
	float a;
	float stateTime = -1;
	
	public dmgText(float x, float y, float amt) {
		pos = new Vector2(x,y);
		lerpTarget = new Vector2(x,y+1);
		this.amt = amt;
		this.a = 1;
	}
	
}