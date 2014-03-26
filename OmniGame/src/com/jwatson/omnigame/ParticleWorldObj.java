package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.graphics.CustomBatch;

public class ParticleWorldObj extends WorldObj {

	
	float lifeTimer;
	TextureRegion tex;
	
	public ParticleWorldObj(TextureRegion p,float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		
		custom_render = true;
		ignore_collision = true;
		vel.y = MathUtils.random(1,5f);
		tex = p;
		vel.x = MathUtils.random(-4f, 4f);
	}
	
	
	@Override
	void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
		if(lifeTimer >= 1) {
			removalflag = true;
		}
		
		lifeTimer += deltaTime;
	}
	
	@Override
	public void render(CustomBatch batch) {
		// TODO Auto-generated method stub
		super.render(batch);
		
		batch.setColor(Color.WHITE);
		batch.draw(tex,pos.x,pos.y,1,Width,Height);
	}



}
