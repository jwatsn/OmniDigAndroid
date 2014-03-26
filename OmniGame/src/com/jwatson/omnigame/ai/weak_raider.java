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
import com.jwatson.omnigame.JItemAnimation;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.MessageBoxButton;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;

public class weak_raider extends AI {
	
	
	
	public weak_raider(World p, float x, float y, float width, float height) {
		super(x, y, width, height);
		
		Respawnable = false;
		Arm = MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak_Arm");
		ArmFlip = new TextureRegion(Arm);
		ArmFlip.flip(true, false);
		TextureRegion[] mirror = new TextureRegion[3];
		mirror[0] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak01"));
		mirror[1] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak02"));
		mirror[2] = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak03"));
		for (TextureRegion region : mirror)
			region.flip(true, false);
		mirrorAnim = new Animation(0.3f, mirror[0],mirror[1]);
		
		TextureRegion[] split2 = new TextureRegion[3];
		split2[0] = MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak01");
		split2[1] = MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak02");
		split2[2] = MapRenderer.Texture_Atlas.findRegion("MOB_Raider_Weak03");
		TextureRegion split[] = MapRenderer.Texture_Atlas_Objs.findRegion("greendeath").split(8, 8)[0];
		anim = new Animation(0.3f,split2[0],split2[1]);
		deathAnim = new Animation(0.1f, split[0],split[1],split[2],split[3],split[4],split[5]);
		usesAnim = true;
		Width = 1;
		name = "Raider";
		Height = 1;
		MaxHP = 60;
		HP = 60;
		ai = this;
		invincible = false;
		ItemAnim = new TextureRegion(MapRenderer.Texture_Atlas.findRegion("TOOL_PickAxe_Wood"));
		AnimationStyle = InvObject.ANIMATE_STYLE_ROTATE;
		Speed = 3f;
		Armor = 1.2f;
		Items = "DEP_Torch 50 2 WEP_Sword_Stone 5 1 MSC_Coin_Silver 50 10";
		
		
		fixedspawn = true;
		ATK = 7;
		type = AI.TYPE_HOSTILE_WANDER;
		bounds.width = 0.6f;
		bounds.height = 0.8f;
		spawnTimer = 0;
		spawn_offset = new Vector2(1,2);
		friendly = false;
		Touchable = true;
		state = STATE_SPAWNING;
		
		
	}
	

}
