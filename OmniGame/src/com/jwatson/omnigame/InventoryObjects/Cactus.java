package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.Trees;

public class Cactus extends InvObject {

	public static int ID;
	public Cactus(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Cactus";
		desc = "Possibly Pointy";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.WOOD;
		Delay = 100;
		HP = 4.5f;
		solid = false;
		collidable = false;
		ID = id;
		//Trees.BROWN_TREE_TEXTURE = thumbnail;
		Savable = false;
		hit_sound = "impactWood";
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
//		if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
//		Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
		
	}

}
