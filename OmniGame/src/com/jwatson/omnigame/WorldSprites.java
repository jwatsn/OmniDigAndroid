package com.jwatson.omnigame;

import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldSprites {
	
	public float x;
	public float y;
	public float Width;
	public float Height;
	public float angle;
	public Texture texture;
	public TextureRegion texRegion;
	public Sprite sprite;
	public boolean deleted;
	
	
	public WorldSprites(Texture tex,float x, float y,float width, float height ) {
		this.x = x;
		this.y = y;
		Width = width;
		Height = height;
		this.texture = tex;
		texRegion = new TextureRegion(tex);
		sprite  = new Sprite(tex);
		
		sprite.setOrigin(x, y);
		
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(texture, x, y, 0, 0, Width, Height, 1, 1, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
	}

}
