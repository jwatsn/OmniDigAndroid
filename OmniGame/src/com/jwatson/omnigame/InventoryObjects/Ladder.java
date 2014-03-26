package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.ClimbableObj;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.TerrainChunk;
import com.jwatson.omnigame.World;

public class Ladder extends InvObject {

	
	public Ladder(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub

		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		name = nme;
		desc = "A climbable ladder";
		type = InvObject.Type.BLOCK;
		HP = 0.3f;
		collidable = false;
		solid = false;
		CraftingRequirements = "MAT_Tree_Brown 2";
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y,float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x, y,dist);
		
		if(flag)
		{
			if(Terrain.CurrentTerrain.isEmpty(x, y)) {
				int X = (int) (x/Terrain.CurrentTerrain.chunkWidth);
				int Y = (int) (y/Terrain.CurrentTerrain.chunkHeight);
				
				int X2 = (int) (x - (X*Terrain.CurrentTerrain.chunkWidth));
				int Y2 = (int) (y - (Y*Terrain.CurrentTerrain.chunkHeight));
				TerrainChunk ch = Terrain.CurrentTerrain.GetChunkByID(X, Y);
				ch.TerrainMap[X2+Y2*Terrain.CurrentTerrain.chunkWidth] = (byte)InvObjID;
				parentinv.owner.inventory.AddToBag("DEP_Ladder", -1);
			}
		}
	}

}
