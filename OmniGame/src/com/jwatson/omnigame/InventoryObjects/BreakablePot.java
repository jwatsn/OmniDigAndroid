package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.AnimatedWorldObj;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.ContainerWorldObj;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Pot;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;
import com.jwatson.omnigame.WorldObj.WorldObjUpdate;

public class BreakablePot extends InvObject {

	TextureRegion[] split;
	public static int ID;
	UpdatableItem update;
	public BreakablePot(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		
		
		
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		type = InvObject.Type.BREAKABLE_CONTAINER;
		Animated = true;
		AnimSpeed = 1;
		HP = 3;
		collidable = false;
		ID = id;
		//CraftingRequirements = "MAT_Tree_BrownSeed 1";
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
		super.OnUse(bob,x, y,dist);
		
		if(flag)
		{
			if(!World.CurrentWorld.UpdateList.contains(this)) {
				isUpdating = true;
				World.CurrentWorld.UpdateList.add(update);
			}
			
			int X = (int)x/Terrain.CurrentTerrain.chunkWidth;
			int Y = (int)y/Terrain.CurrentTerrain.chunkHeight;
			int x2 = (int)x%Terrain.CurrentTerrain.chunkWidth;
			int y2 = (int)x%Terrain.CurrentTerrain.chunkWidth;
			
			if(parentinv.owner.firstUse()) {

				WorldObj obj = new Pot(x,y,1,1,BreakablePot.ID,"DEP_Torch 70 4 MSC_Coin_Silver 50 10");
				World.CurrentWorld.pendingWorldObjs.add(obj);
					//Bob.CurrentBob.inventory.AddToBag(name,-1,true);
			}
		//Terrain.CurrentTerrain.light.floodStack.add(new Vector3(x,y,0));
		
		
		flag=false;
		}
	}
	

}
