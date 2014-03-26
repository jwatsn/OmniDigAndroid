package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class SilverCoin extends InvObject {

	public static int ID;
	
	public SilverCoin(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Silver Coin";
		MaxStack = 200;
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.CURRENCY;
		Delay = 100;
		HP = 4;
		ID = id;
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
		}
		
	}

}
