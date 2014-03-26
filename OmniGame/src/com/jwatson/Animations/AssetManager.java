package com.jwatson.Animations;

import java.util.Hashtable;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.jwatson.omnigame.MapRenderer;

public class AssetManager {
	
	public static Map<String, Animation> Animation;
	
	public static AssetManager instance;
	
	public AssetManager() {
		
		genTiles();
		
		initAnims();
		
	}
	
	public static void init() {
		if(instance != null)
			instance = null;
		
		instance = new AssetManager();
	}
	
	
	public void genTiles() {
		
		TexturePacker2.process("data/tiles", "data/tiles", "tiles");
		MapRenderer.Texture_Atlas = new TextureAtlas("data/tiles/tiles.atlas");
		
	}
	
	public static void gen() {
		instance.genTiles();
	}
	
	public void initAnims() {
		
		Animation = new Hashtable<String, Animation>();
		
		AddAnimation(0.3f,"bobrun", "bobRight", 8, 8, false);
		AddAnimation(0.3f,"bobrun", "bobLeft", 8, 8, true);
		AddAnimation(0.3f,"bobjump", "bobJumpRight", 8, 8, false);
		AddAnimation(0.3f,"bobjump", "bobJumpLeft", 8, 8, true);
		
	}
	
	static void AddAnimation(float time, String tex, String name, int width, int height, boolean flip) {
		
		TextureRegion texture = MapRenderer.Texture_Atlas.findRegion(tex);
		
		TextureRegion split[] = new TextureRegion(texture).split(width, height)[0];
		
		if(flip) {
			
			
			for(TextureRegion region : split)
				region.flip(true, false);
		}
		
		
		Animation.put(name, new Animation(time, split));
		
	}
}
