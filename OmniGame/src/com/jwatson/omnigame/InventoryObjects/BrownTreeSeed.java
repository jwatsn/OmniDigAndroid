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
import com.jwatson.omnigame.World;

public class BrownTreeSeed extends InvObject {

	
	public static int ID;
	public BrownTreeSeed(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Tree Seed";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.INGREDIENT;
		Delay = 100;
		HP = 1;
		collidable = false;
		ID = id;
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			int xx = ((int)x/Terrain.CurrentTerrain.chunkWidth);
			int yy = ((int)y/Terrain.CurrentTerrain.chunkHeight);
			
			int x2 = (int)x%Terrain.CurrentTerrain.chunkWidth;
			int y2 = (int)y%Terrain.CurrentTerrain.chunkHeight;
			
		if(MapRenderer.CurrentRenderer.TreeManager.PlantTree(Trees.BROWN_TREE,xx, yy, x2, y2,false))
		Bob.CurrentBob.inventory.AddToBag(name,-1,true);
		flag=false;
		}
		
	}

}
