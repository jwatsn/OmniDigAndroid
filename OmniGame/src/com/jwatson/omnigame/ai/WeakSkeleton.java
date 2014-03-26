package com.jwatson.omnigame.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.AI;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldObj;

public class WeakSkeleton extends AI {
	
	public WeakSkeleton(World p, float x, float y, float width, float height) {
		super(x, y, width, height);
		

		
		TextureRegion[] mirror = MapRenderer.Texture_Atlas_Objs.findRegion("bskeleton").split(8, 8)[0];
		for (TextureRegion region : mirror)
			region.flip(true, false);
		mirrorAnim = new Animation(0.3f, mirror[0],mirror[1]);
		
		TextureRegion[] split2 = MapRenderer.Texture_Atlas_Objs.findRegion("bskeleton").split(8, 8)[0];
		sprite = new Sprite(split2[1]);
		jumpFrame = split2[1].getTexture();
		TextureRegion split[] = MapRenderer.Texture_Atlas_Objs.findRegion("greendeath").split(8, 8)[0];
		anim = new Animation(0.3f,split2[0],split2[1]);
		deathAnim = new Animation(0.1f, split[0],split[1],split[2],split[3],split[4],split[5]);
		usesAnim = true;
		sprite.setSize(width, height);
		Width = 1;
		Height = 1;
		MaxHP = 60;
		HP = 60;
		ai = this;
		Armor = 1.2f;
		Items = "DEP_Torch 50 1 MSC_Coin_Silver 50 4";
		ATK = 7;
		type = AI.TYPE_WALK;
		bounds.width = 0.6f;
		bounds.height = 0.8f;
		spawnTimer = 20;
		randomSpawn = true;
		NightOnly = true;
		Respawnable = false;
		spawn_offset = new Vector2(15,10);
		spawnTimer = (float) (MathUtils.random(10,30));

		state=STATE_SPAWNING;
	}
	

}
