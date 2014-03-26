package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class Pump extends InvObject {

	public static int ID;
	
	public Pump(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		
		type = InvObject.Type.BLOCK;
		Delay = 100;
		HP = 1.6f;
		ID = id;
		needsGround = true;
		//CraftingLevel = 1;
		//CraftingRequirements = "MAT_Wood 10 MAT_Iron 5";
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
		if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
		parentinv.owner.inventory.AddToBag(name,-1);
		flag=false;
		}
		
	}

}
