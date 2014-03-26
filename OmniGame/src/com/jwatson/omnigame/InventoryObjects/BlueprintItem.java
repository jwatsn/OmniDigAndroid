package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.Item;
import com.jwatson.omnigame.Terrain;

public class BlueprintItem extends InvObject {

	static int ID;
	
	public BlueprintItem(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		desc = "";
		thumbnail = Item.Items[Blueprints.ID].thumbnail;
		type = InvObject.Type.BLUEPRINT;
		Delay = 100;
		HP = 16f;
		MaxStack = 1;
		ID = id;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
		//if(Terrain.CurrentTerrain.CreateBlock(x,y, InvObjID))
		//Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
		
	}

}
