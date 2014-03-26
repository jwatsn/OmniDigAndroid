package com.jwatson.omnigame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class InvHookObject extends InvObject {

	
	protected boolean Grabbed;
	protected Rectangle bounds;
	protected float angle2;
	protected float Grablen;
	protected Vector2 Grabpos;
	protected float MaxLen;
	protected SpriteBatch batch;
	protected WorldSprites grappleGfx;
	protected WorldSprites testSprite;
	protected boolean isShooting;
	protected float angle;
	protected float len;
	
	public InvHookObject(int id) {
		super(id);
	}

}
