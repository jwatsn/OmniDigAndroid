package com.jwatson.omnigame;

public abstract class UpdatableItem {

	float speed;
	float counter;
	boolean remove;
	public UpdatableItem(float speed) {
		this.speed = speed;
	}
	
	public abstract void update(float delta);
}
