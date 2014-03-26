package com.jwatson.omnigame;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public abstract class AbstractBounds<T> extends Rectangle {

	public Class<T> target;
	
	public String name;
	
	
	public AbstractBounds() {
		// TODO Auto-generated constructor stub
	}

	public AbstractBounds(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	public AbstractBounds(Rectangle rect) {
		super(rect);
		// TODO Auto-generated constructor stub
	}
 
	public abstract void OnCollision(T t);
	
	
}

