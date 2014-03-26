package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class Water extends InvObject {

	
	public static int ID;
	public static TextureRegion WaterTexture;
	public static TextureRegion WaterTexRegion;
	public Water(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		
		
		//WaterTexture = thumbnail;
		type = InvObject.Type.BLOCK;
		Delay = 100;
		HP = 1;
		InvObject.GRASS_ID = id;
		ID = id;
		Liquid = true;
		//Animated = true;
		solid = false;
		Breakable = false;
		WaterTexture = thumbnail;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
		if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
		//Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
		
	}

}
