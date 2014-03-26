package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class Pipe extends InvObject {

	public static int ID;
	
	public Pipe(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		thumbnail_array = new TextureRegion[4];
		
		//left
		thumbnail_array[0] = MapRenderer.Texture_Atlas.findRegion("DEP_Pipe2");
		//right
		thumbnail_array[1] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("DEP_Pipe2"));
		thumbnail_array[1].flip(true, false);
		//up
		thumbnail_array[2] = MapRenderer.Texture_Atlas.findRegion("DEP_Pipe3");
		//down
		thumbnail_array[3] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("DEP_Pipe3"));
		thumbnail_array[3].flip(false, true);
		
		type = InvObject.Type.PIPE;
		Delay = 100;
		HP = 1.6f;
		ID = id;
		solid = false;
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
