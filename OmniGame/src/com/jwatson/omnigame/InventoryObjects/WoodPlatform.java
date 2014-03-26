package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class WoodPlatform extends InvObject {

	
	public WoodPlatform(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		descName = "Wood Platform";
		type = InvObject.Type.PLATFORM;
		solid = true;
		HP = 2f;
		opaque = true;
		CraftingRequirements = "MAT_Tree_Brown 2";
		Price = 10;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y,float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x, y, dist);
		
		if(flag)
		{
		if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
		Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
	}

}
