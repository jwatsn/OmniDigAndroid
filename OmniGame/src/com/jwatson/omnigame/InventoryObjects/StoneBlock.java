package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class StoneBlock extends InvObject {

	
	public StoneBlock(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub

		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		descName = "Stone Block";
		type = InvObject.Type.BLOCK;
		HP = 2.5f;
		hit_sound = "impactMedium";
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
