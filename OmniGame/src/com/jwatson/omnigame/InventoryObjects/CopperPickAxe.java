package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.JItemAnimation;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.World;

public class CopperPickAxe extends InvObject {

	
	
	
	public CopperPickAxe(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Copper Pickaxe";
		desc = "Used for mining";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.HELD;
		Distance = 6;
		ATK = 5;
		Delay = 100;
		DefaultMine = false;
		CraftingRequirements = "MAT_Tree_Brown 4 MAT_Stone 3 MAT_Copper 4";
		strongAgainst.add(InvObject.Type.BLOCK);
		ActiveSprite = new Sprite(thumbnail);
		ActiveSprite.setOrigin(0.46875f, 1-0.90625f);
		ActiveSprite.setSize(1, 1);
		CraftingLevel = 1;
		Price = 10;
		MineLevel = 2;
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float angle) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,angle);
		if(flag)
		{
			Vector2 pos = bob.pos;
			float x2 = (pos.x+0.5f);
			float y2 = (pos.y+0.5f);

			Vector2 nextBlock = Terrain.CurrentTerrain.GetNextTile(angle,x2,y2);
			if(nextBlock.x >= 0)
			World.CurrentWorld.MineBlock(this,(int)nextBlock.x, (int)nextBlock.y,ATK);
			if(MapRenderer.CurrentRenderer.ItemAnimation == null) {
			parentinv.owner.UsedItem = this;
			parentinv.owner.Animate(new JItemAnimation(ActiveSprite), ANIMATE_STYLE_ROTATE, 0.2f);
			}
		
		}
		
	}

}
