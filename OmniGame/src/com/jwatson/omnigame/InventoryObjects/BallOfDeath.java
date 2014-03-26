package com.jwatson.omnigame.InventoryObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.T_ExpWorldObj;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;
import com.jwatson.omnigame.WorldObj.WorldObjUpdate;

public class BallOfDeath extends InvObject {

	
	List<WorldObj> objs;
	UpdatableItem update;
	
	
	public BallOfDeath(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.EXPLOSIVE;
		descName = "Ball Of Death";
		desc = "Big Bang";
		Delay = 500;
		HP = 60;
		Distance = 100;
		Price = 20;
		objs = new ArrayList<WorldObj>();
		DefaultMine = false;
		solid = false;
		CraftingRequirements = "MAT_Coal 10 MAT_Stone 5";
		CraftingLevel = 2;
		
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
			
			
			float x2 = parentinv.getOwnerBob().pos.x;
			float y2 = parentinv.getOwnerBob().pos.y;
			
			Vector3 test = new Vector3(x2,y2,0);
			MapRenderer.CurrentCam.project(test);
			
			float x3 = x0 - test.x;
			float y3 = y0 - test.y;
			
			float angle = dist;
			
			
		final T_ExpWorldObj obj = new T_ExpWorldObj(Bob.CurrentBob,x2, y2, 1, 1, InvObjID);
		obj.timer_length = 2f;
		obj.doesntexplode = true;
		World.CurrentWorld.pendingWorldObjs.add(obj);

		obj.vel.x = (float) (10 * MathUtils.cosDeg(angle));
		obj.vel.y = (float) (10 * MathUtils.sinDeg(angle));
		
		
		Inventory.CurrentInventory.AddToBag(name, -1);
		
		
//		if(!isUpdating)
//		{
//			World.CurrentWorld.UpdateList.add(update);
//			isUpdating = true;
//		}
		
		}
		
		
	}
	
	
	

}
