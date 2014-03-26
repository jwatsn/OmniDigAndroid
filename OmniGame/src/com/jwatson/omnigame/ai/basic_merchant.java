package com.jwatson.omnigame.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.AI;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.Item;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.MessageBoxButton;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;

public class basic_merchant extends AI {
	
	
	
	public basic_merchant(World p, float x, float y, float width, float height) {
		super(x, y, width, height);
		
		
		
		TextureRegion[] mirror = new TextureRegion[3];
		mirror[0] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("ai_red01"));
		mirror[1] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("ai_red02"));
		mirror[2] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("ai_red03"));
		for (TextureRegion region : mirror)
			region.flip(true, false);
		mirrorAnim = new Animation(0.3f, mirror[0],mirror[1]);
		
		TextureRegion[] split2 = new TextureRegion[3];
		split2[0] = MapRenderer.Texture_Atlas.findRegion("ai_red01");
		split2[1] = MapRenderer.Texture_Atlas.findRegion("ai_red02");
		split2[2] = MapRenderer.Texture_Atlas.findRegion("ai_red03");
		TextureRegion split[] = MapRenderer.Texture_Atlas_Objs.findRegion("greendeath").split(8, 8)[0];
		anim = new Animation(0.3f,split2[0],split2[1]);
		deathAnim = new Animation(0.1f, split[0],split[1],split[2],split[3],split[4],split[5]);
		usesAnim = true;
		Width = 1;
		name = "Item Vendor";
		Height = 1;
		MaxHP = 60;
		HP = 60;
		ai = this;
		invincible = true;
		Armor = 1.2f;
		Items = "DEP_Torch 20 WEP_Sword_Stone 10 GEAR_Bomb 10";
		
		for(InvObject obj : Item.Items) {
			if(obj != null)
			if(obj.type == InvObject.Type.BLUEPRINT)
				if(!obj.name.equals("MSC_Blueprints"))
				Items += " "+obj.name + " 1";
			
		}
		
		Dialog = "Welcome! Feel free to browse my wares. ";
		ATK = 7;
		type = AI.TYPE_FRIENDLY_MERCHANT;
		bounds.width = 0.6f;
		bounds.height = 0.8f;
		spawnTimer = 0;
		spawn_offset = new Vector2(5,2);
		friendly = true;
		Touchable = true;
		state = STATE_SPAWNING;
		
		buttons = new MessageBoxButton[2];
		
		MessageBoxButton buy = new MessageBoxButton("Buy") {
			
			@Override
			public void onClicked(int...i) {
				// TODO Auto-generated method stub
				Inventory.CurrentInventory.filter = Inventory.FILTER_ITEMS;
				Inventory.CurrentInventory.CreateMerchantScreen(ai,Inventory.MERCHANT_BUY);
			}
		};
		
		MessageBoxButton guide = new MessageBoxButton("Guide") {
			
			@Override
			public void onClicked(int...i) {
				// TODO Auto-generated method stub
				MapRenderer.CurrentRenderer.TutorialPage = 0;
				MapRenderer.CurrentRenderer.TutorialNextPage = true;
			}
		};
		
		buttons[0] = guide;
		buttons[1] = buy;
		
	}
	

}
