package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.jwatson.omnigame.graphics.CustomBatch;

public class Pot extends WorldObj {
	
	boolean broken;
	float brokeTime = 0;
	int frame = 0;
	String Items;
	TextureRegion split[];

	public Pot(float x, float y, float width, float height,int ID, String Items) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
		
		
		sprite = new Sprite(MapRenderer.Texture_Atlas.findRegion("PROP_BreakablePot"));
		split = MapRenderer.Texture_Atlas.findRegion("breakablepotanim").split(8, 8)[0];
		
		//deathAnim = new Animation(0.08f, split[0],split[1],split[2],split[3],split[4],split[5]);
		
		sprite.setSize(width, height);
		this.Items = Items;
		this.ID = ID;
		//ai = this;
		//MaxHP = 1;
		//HP = 1;
		//this.Items = Items;
		//type = TYPE_STATIC;
		//Width = 0.6f;
		//Height = 0.7f;
		//Respawnable = false;
		damageable = true;
		y_offset = -0.2f;
	}
	
	@Override
	public void onDamaged(Bob bob, InvObject usedItem, int atk) {
		// TODO Auto-generated method stub
		super.onDamaged(bob, usedItem, atk);
		
		if(!broken) {
			broken = true;
			custom_render = true;
			DropItems(pos.x, pos.y);
		}
		
	}
	
	@Override
	void update(float deltaTime) {
		super.update(deltaTime);
		// TODO Auto-generated method stub
		
		if(broken) {
			brokeTime += deltaTime;
		}
		
	}
	
	@Override
	public void render(CustomBatch batch2) {
		// TODO Auto-generated method stub
		super.render(batch2);
		
		if(broken) {

			batch2.draw(split[frame],1,pos.x,pos.y+y_offset,1,1);
			if(brokeTime >= 0.1f) {
				frame++;
				brokeTime = 0;
			}
			if(frame >= split.length)
				removalflag = true;
		}
		
	}
	
void DropItems(float x, float y) {
		
		String[] items = Items.split(" ");
		
	for(int i = 0; i < items.length; i++) {
		
		int id = Inventory.CurrentInventory.GetItemID(items[i]);
		float chance = Integer.valueOf(items[i+1]);
		int amt = Integer.valueOf(items[i+2]);
		
		for(int i2=0; i2<amt; i2++) {
		if(MathUtils.random(100) < chance) { 
		WorldObj obj = new WorldObj(World.CurrentWorld, x, y, 1, 1, id, 1);
		if(MathUtils.randomBoolean())
		obj.vel.x = 5;
		else
			obj.vel.x = -5;
		
		obj.vel.y = (float)(3+Math.random()*5);
		World.CurrentWorld.pendingWorldObjs.add(obj);
		}
		}
		i += 2;
	}
		
	}
	
	

}
