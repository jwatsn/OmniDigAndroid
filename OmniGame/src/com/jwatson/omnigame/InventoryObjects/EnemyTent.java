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
import com.jwatson.omnigame.MessageBox;
import com.jwatson.omnigame.MessageBoxButton;
import com.jwatson.omnigame.Pot;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.TerrainChunk;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;
import com.jwatson.omnigame.WorldObj.WorldObjUpdate;

public class EnemyTent extends InvObject {

	TextureRegion[] split;
	public static int ID;
	UpdatableItem update;
	MessageBoxButton save;
	MessageBoxButton home;
	public EnemyTent(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		
		
		
		
		thumbnail = MapRenderer.Texture_Atlas.findRegion("DEP_Tent");
		name = nme;
		type = InvObject.Type.BREAKABLE;
		HP = 3;
		collidable = false;
		ID = id;
		AltDropId = Tent.ID;
		touchable = true;
		Breakable = true;
		needsGround = true;
		needsSolidPlacement = true;
		solid = false;
		Distance = 3;
		
		
		save = new MessageBoxButton("Save") {
			
			@Override
			public void onClicked(int... args) {
				// TODO Auto-generated method stub
				Terrain.CurrentTerrain.SaveMap(MapRenderer.CurrentRenderer.save_slot);

			}
		};
		

		
		
		
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
	

	

}
