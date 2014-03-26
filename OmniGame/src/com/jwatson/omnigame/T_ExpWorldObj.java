package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.Lighting.recursiveLight;
import com.jwatson.omnigame.graphics.CustomBatch;

public class T_ExpWorldObj extends WorldObj {

	boolean armed;
	float timer;
	public float timer_length,flash_timer,explode_timer;
	boolean flash;
	public InvObject item;
	public boolean doesntexplode;
	boolean exploded;
	Vector2 explode_pos;
	public T_ExpWorldObj(World p, float x, float y, float width, float height,
			int id, int amount) {
		super(p, x, y, width, height, id, amount);
		// TODO Auto-generated constructor stub
	}

	public T_ExpWorldObj(Bob currentBob, float x, float y, float width, float height, int invObjID) {
		super(World.CurrentWorld,x, y, width, height,invObjID,1);
		collision = false;
		item = Item.Items[invObjID];
		explode_pos = new Vector2();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCollision(boolean x, boolean y) {
		// TODO Auto-generated method stub
		super.onCollision(x, y);
		armed = true;
		custom_render = true;
		//Terrain.CurrentTerrain.Explode(pos.x, pos.y, 3);
//		if(flash_timer > 0.1f) {
//		SoundManager.PlaySound("explode",3,pos.x,pos.y);
//		flash_timer = 0;
//		}
	}
	
	@Override
	void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
		if(!exploded)
		if(armed)
			timer += deltaTime;
		
		if(timer >= timer_length/4f) {
			
			if(flash_timer >= 0.113f - 0.1f*(timer/timer_length)) {
				flash = !flash;
				flash_timer = 0;
			}
			
			
			
			
		}
		flash_timer+=deltaTime;
		
		if(timer > timer_length) {
			
			
			
			removalflag = true;
			timer = 0;
			exploded = true;
			armed = false;
			explode_pos.set(pos);
			Terrain.CurrentTerrain.Explode(pos.x, pos.y, 3);
			if(!exploded)
			{
			SoundManager.PlaySound("explode",3);
			
			for(int x2=-1; x2<1; x2++)
				for(int y2=-1; y2<1; y2++) {
					
					int xx = (int) ((pos.x+x2)/Terrain.CurrentTerrain.chunkWidth);
					int yy = (int) ((pos.y+y2)/Terrain.CurrentTerrain.chunkHeight);
					int x_x = (int) ((pos.x+x2)%Terrain.CurrentTerrain.chunkWidth);
					int y_y = (int) ((pos.y+y2)%Terrain.CurrentTerrain.chunkHeight);
					
					Terrain.CurrentTerrain.light.floodStack.add(Terrain.CurrentTerrain.light.new recursiveLight(xx, yy, x_x, y_y, (byte)15));
				}
			
			Terrain.CurrentTerrain.light.recursiveSpreadLights();
			}
		}
		
		if(exploded) {
			
			if(explode_timer > 0.8)
			{
				Terrain.CurrentTerrain.light.spreadLight(null);
				if(!doesntexplode)
				removalflag = true;
			}
			explode_timer += deltaTime;
			
		}
		
	}
	
	@Override
	public void render(CustomBatch batch) {
		// TODO Auto-generated method stub
		super.render(batch);
		
		if(item.anim == null) {
			batch.draw(item.thumbnail,1, pos.x,pos.y,1,1);
		}
		else {
		
		if(!exploded) {
			if(!flash) {
				batch.draw(item.anim.getKeyFrame(0),1, pos.x, pos.y,1,1);
			}
			else {
				batch.draw(item.anim.getKeyFrame(1),1,pos.x, pos.y,1,1);
			}
		}
		else {
			batch.draw(MapRenderer.CurrentRenderer.dying.getKeyFrame(explode_timer,false),1,explode_pos.x-3, explode_pos.y-3,6,6);
		}
		
		}
	}

}
