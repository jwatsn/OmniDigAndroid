package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class StoneAnvil extends InvObject {

	
	public StoneAnvil(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub

		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		descName = "Stone Anvil";
		desc = "Used for crafting";
		type = InvObject.Type.ANVIL;
		HP = 1f;
		needsSolidPlacement = true;
		CraftingRequirements = "MAT_Stone 10 MAT_Tree_Brown 10";
		CraftingLevel = 1;
		Price = 15;
		solid = false;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x, y,dist);
		
		if(flag)
		{
		if(Terrain.CurrentTerrain.CreateBlock(bob,(int)x,(int)y, InvObjID))
		Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
	}

}
