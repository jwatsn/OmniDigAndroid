package com.jwatson.omnigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.graphics.CustomBatch;

public class AnimatedWorldObj extends WorldObj {


	
	protected TextureRegion[] frames;
	Texture animationmap;
	
	float animationTime = 0;
	private int frame;
	
	protected float speed;
	protected boolean looping = true;
	
	
	public AnimatedWorldObj(World world,int x,int y,float width, float height, int id,float speed, TextureRegion[] split) {
		super(world,x,y,width,height,id, 1);
		// TODO Auto-generated constructor stub
		frames = split;
		this.speed = speed;
	}
	
	
	@Override
	public void update(float delta) {
		super.update(delta);

		animationTime += delta;
		
		if(animationTime > speed) {
			frame++;
			animationTime = 0;
			
			if(frame >= frames.length)
				frame = 0;
		}
	}
	
@Override
public void render(CustomBatch batch) {
	// TODO Auto-generated method stub
	batch.draw(frames[frame],1,pos.x,pos.y,1,1);
}

}
