package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.TurretObj;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;

public class Turret extends InvObject {

	public static int ID;
	
	public Turret(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.TURRET;
		Delay = 100;
		HP = 1.6f;
		ID = id;
		solid = false;
		collidable = false;
		CraftingRequirements = "MAT_Iron 1 MAT_Tree_Brown 20 MAT_Stone 5";
		CraftingLevel = 1;
		needsSolidPlacement_ground = true;
		Price = 30;
		
	}
	Rectangle touchbounds = new Rectangle();
	@Override
	public void OnUse(Bob bob,float x, float y, float dist) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,dist);
		if(flag)
		{
			
			
			if(Terrain.CurrentTerrain.CreateBlock(parentinv.owner,(int)x,(int)y, InvObjID)) {
				Bob.CurrentBob.inventory.AddToBag(name,-1,true);
			TurretObj ballista = new TurretObj((int)x, (int)y, 1, 1,15, 1.5f);
			World.CurrentWorld.pendingWorldObjs.add(ballista);
			}
		}
		flag=false;
		
	}

}
