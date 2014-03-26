package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.AnimatedWorldObj;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.ContainerWorldObj;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Pot;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.TerrainChunk;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;
import com.jwatson.omnigame.WorldObj.WorldObjUpdate;

public class Chest extends InvObject {

	TextureRegion[] split;
	public static int ID;
	UpdatableItem update;
	public Chest(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		
		
		TextureRegion[] split = MapRenderer.Texture_Atlas.findRegion(nme).split(8, 8)[0];
		anim = new Animation(0.5f, split[0],split[1]);
		manualAnimControl = true;
		thumbnail = anim.getKeyFrame(0);
		name = nme;
		type = InvObject.Type.CHEST;
		Animated = true;
		AnimSpeed = 1;
		HP = 2;
		collidable = false;
		ID = id;
		touchable = true;
		Breakable = true;
		Distance = 3;
		needsGround = true;
		needsSolidPlacement = true;
		CraftingRequirements = "MAT_Tree_Brown 10 MAT_Stone 5";
		solid = false;
		Price = 6;
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x, y,dist);
		
		if(flag)
		{
			
			int X = (int)x/Terrain.CurrentTerrain.chunkWidth;
			int Y = (int)y/Terrain.CurrentTerrain.chunkHeight;
			int x2 = (int)x%Terrain.CurrentTerrain.chunkWidth;
			int y2 = (int)x%Terrain.CurrentTerrain.chunkWidth;
			
			if(parentinv.owner.firstUse()) {

				if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID))
					Bob.CurrentBob.inventory.AddToBag(name,-1,true);
			}
		//Terrain.CurrentTerrain.light.floodStack.add(new Vector3(x,y,0));
		
		
		flag=false;
		}
	}
	
	@Override
	public void onTouch(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub
		super.onTouch(x, y, x2, y2);
		
		TerrainChunk ch = Terrain.CurrentTerrain.GetChunkByID(x, y);
		ch.stateMap[x2+y2*ch.Width] = 1;
		Inventory.CurrentInventory.OpenChest(x,y,x2,y2);
		
	}
	

}
