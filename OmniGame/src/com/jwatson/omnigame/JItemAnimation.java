package com.jwatson.omnigame;

import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X4;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y4;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.graphics.CustomBatch;

public class JItemAnimation {

	Vector2 pos;
	Vector2 origin;
	float rotation;
	TextureRegion tex;
	Rectangle bounds;
	
	public JItemAnimation(Sprite activeSprite) {
		// TODO Auto-generated constructor stub
		tex = new TextureRegion(activeSprite);
		pos = new Vector2();
		origin = new Vector2();
		bounds = new Rectangle();
	}

	public JItemAnimation(TextureRegion thumbnail) {
		tex = new TextureRegion(thumbnail);
		pos = new Vector2();
		origin = new Vector2();
		bounds = new Rectangle();
	}

	public void setOrigin(float f, float g) {
		// TODO Auto-generated method stub
		origin.set(f,g);
	}

	public void setPosition(float f, float g) {
		// TODO Auto-generated method stub
		pos.set(f, g);
	}

	public void flip(boolean b, boolean c) {
		// TODO Auto-generated method stub
		tex.flip(b, c);
	}

	public void setRotation(float f) {
		// TODO Auto-generated method stub
		rotation = f;
	}

	public float getRotation() {
		// TODO Auto-generated method stub
		return rotation;
	}

	public void draw(CustomBatch batch2) {
		// TODO Auto-generated method stub
		batch2.draw(tex, pos.x, pos.y, origin.x, origin.y, 1, 1, 1, 1, rotation);
	}

	public void setX(float f) {
		// TODO Auto-generated method stub
		//pos.x = f;
	}

//	public Rectangle getBoundingRectangle() {
//		// TODO Auto-generated method stub
//	}

}
