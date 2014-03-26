package com.jwatson.omnigame;

import com.badlogic.gdx.math.Rectangle;

public class ClimbableObj extends WorldObj {

	
	Rectangle bounds;
	public ClimbableObj(World p, float x, float y, float width, float height,
			int id, int amount) {
		super(p, x, y, width, height, id, amount);
		// TODO Auto-generated constructor stub
		
		bounds = new Rectangle(x+0.25f,y,0.5f,height);
		World.CurrentWorld.climbableBounds.add(bounds);
		
		
	}
	
	

}
