package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;

public class CopperBP extends InvObject {

	
	public CopperBP(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		desc = "Used for crafting";
		TextureRegion[] texture = MapRenderer.Texture_Atlas.findRegion(nme).split(8, 8)[0];
		worn_texture = new TextureRegion[2];
		thumbnail = texture[0];
		worn_texture[0] = texture[1];
		worn_texture[1] = new TextureRegion(texture[1]);
		worn_texture[1].flip(true, false);
		type = InvObject.Type.ARMOR_BP;
		CraftingLevel = 1;
		CraftingRequirements = "MAT_Copper 20";
		Delay = 100;
		HP = 2f;
		hit_sound = "impactHard";
		DEF = 3;
		Price = 25;
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
