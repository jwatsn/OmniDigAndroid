package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.Item;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class BrownDirt2 extends InvObject {

	

	
	public BrownDirt2(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Brown Dirt";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.BLOCK;
		AltDropId = Item.getId("MAT_Dirt");
		Delay = 100;
		HP = 1.7f;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
		if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
		Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
		
	}

}
