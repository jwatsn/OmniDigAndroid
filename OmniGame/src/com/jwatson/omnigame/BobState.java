package com.jwatson.omnigame;

import com.badlogic.gdx.math.Vector2;

public class BobState {
	
	int id;
	int state;
	float x;
	float y;
	int direction;
	float velx;
	float vely;
	Vector2 onUse;
	int used = -1;
	float angle;
	int UsedX;
	int UsedY;
	int firstUsed;
	int[][] bag = new int[Inventory.CurrentInventory.BagWidth*Inventory.CurrentInventory.BagHeight][2];
	public boolean inv;
	public BobState() {
		
	}
	
}
