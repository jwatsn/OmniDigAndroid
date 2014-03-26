package com.jwatson.omnigame.InventoryObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;

public class RedBird extends InvObject {

	
	List<WorldObj> objs;
	List<WorldObj> remove;
	UpdatableItem update;
	
	public RedBird(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Red Bomb";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.HELD;
		Delay = 500;
		HP = 60;
		Distance = 100;
		objs = new ArrayList<WorldObj>();
		remove = new ArrayList<WorldObj>();
		DefaultMine = false;
		
		update = new UpdatableItem(0.01f) {
					
					@Override
					public void update(float delta) {
						// TODO Auto-generated method stub
						UpdateItem(delta);
					}
				};
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
		
			float x0 = Gdx.input.getX(0);
			float y0 = Gdx.graphics.getHeight() - Gdx.input.getY(0);
			
			
			float x2 = Bob.CurrentBob.pos.x;
			float y2 = Bob.CurrentBob.pos.y;
			
			Vector3 test = new Vector3(x2,y2,0);
			MapRenderer.CurrentCam.project(test);
			
			float x3 = x0 - test.x;
			float y3 = y0 - test.y;
			
			float angle = MathUtils.atan2(y3, x3);
			
			
		WorldObj obj = World.CurrentWorld.AddToWorld(x2, y2, 1,1, InvObjID);
		obj.collision = false;

		obj.vel.x = (float) ((10+dist) * Math.cos(angle));
		obj.vel.y = (float) ((15+dist) * Math.sin(angle));
		objs.add(obj);
		if(!isUpdating)
		{
			World.CurrentWorld.UpdateList.add(update);
			isUpdating = true;
		}
		
		}
		
		
	}
	
	public void UpdateItem(float delta) {
		// TODO Auto-generated method stub
		for(WorldObj ob : objs) {
			if(ob.HasCollided && ob.ai == null) {
				
				World.CurrentWorld.RemoveWorldObject(ob);
				remove.add(ob);
				
			}

		}
		if(objs.size() <= 0)
			isUpdating = false;
	}
}
