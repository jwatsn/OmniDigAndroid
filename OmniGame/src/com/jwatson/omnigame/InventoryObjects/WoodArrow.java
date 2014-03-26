package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class WoodArrow extends InvObject {

	
	public WoodArrow(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub

		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		desc = "Used for bows and ballista turrets";
		type = InvObject.Type.PROJECTILE;
		HP = 13f;
		MaxStack = 100;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x, y,dist);
		
		if(flag)
		{
		//if(Terrain.CurrentTerrain.CreateBlock(x,y, InvObjID))
		//Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
	}

}
