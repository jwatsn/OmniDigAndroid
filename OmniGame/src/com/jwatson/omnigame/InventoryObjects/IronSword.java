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

public class IronSword extends InvObject {

	
	Sprite ActiveSprite;
	
	
	public IronSword(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Iron Sword";
		desc = "A sword made from iron";
		
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.HELD;
		Distance = 6;
		ATK = 30;
		Delay = 100;
		DefaultMine = false;
		CraftingRequirements = "MAT_Tree_Brown 5 MAT_Stone 3 MAT_Iron 10";
		CraftingLevel = 1;
		ActiveSprite = new Sprite(thumbnail);
		//ActiveSprite.setOrigin(0.46875f, 1-0.90625f);
		ActiveSprite.setSize(1, 1);
		Price = 25;
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float angle) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,angle);
		if(flag)
		{
			if(!parentinv.owner.firstUse())
				return;
			
			if(MapRenderer.CurrentRenderer.ItemAnimation == null) {
				Sprite copy = new Sprite(ActiveSprite);
				copy.setOrigin(0.5f, 0.25f);				
			parentinv.owner.Animate(new JItemAnimation(ActiveSprite), ANIMATE_STYLE_THRUST, 0.25f);
			}
		
		}
		
	}

}
