package com.jwatson.omnigame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


public class InvObject {
	
	
	public static int GRASS_ID;
	
	public static int ANIMATE_STYLE_ROTATE = 1;
	public static int ANIMATE_STYLE_THRUST = 2;
	public static int ANIMATE_STYLE_THRUST_ANGLE = 3;

	public static class Type {
		 
		
		public static int BLOCK = 0x01;
		public static int DEPLOYABLE = 0x02;
		public static int HELD = 0x03;
		public static int INGREDIENT = 0x04;
		public static int OBJECT = 0x05;
		public static int WOOD = 0x06;
		
		public static final int BREAKABLE_CONTAINER = 0x07;
		public static final int BREAKABLE = 0x08;
		public static final int CHEST = 0x09;		
		public static int CURRENCY = 0x10;
		public static int EXPLOSIVE = 0x11;
		public static final int BLUEPRINT = 0x12;
		public static final int ANVIL = 0x13;
		public static final int STATE_CONTROLLED_COLLISION = 0x14;
		public static final int PLATFORM = 0x15;
		public static final int FOLIAGE = 0x16;
		public static final int TURRET = 0x17;
		public static final int PROJECTILE = 0x18;
		public static final int LEAF = 0x19;
		public static final int ARMOR_BP = 0x20;
		public static final int ARMOR_HELM = 0x21;
		public static final int ARMOR_LEGS = 0x22;
		public static final int ARMOR_BOOTS = 0x23;
		public static final int PIPE = 0x24;
	}
	
	//Armor stuff
	protected TextureRegion worn_texture[];
	protected int DEF = 0;
	
	protected int CraftingMulti = 1;
	protected long LastUsed;
	protected boolean flag;
	protected int Delay = 100;
	protected int InvObjID;
	public String name;
	protected int AltDropId = 0;
	protected String descName;
	protected String desc;
	public int type;
	protected int Price = 1;
	protected int MineLevel;
	protected boolean DefaultMine = true;
	protected boolean opaque = false;
	protected boolean isUpdating;
	protected boolean firstOnUse = true;
	protected boolean collidable = true;
	protected boolean solid = true;
	protected boolean hasParticle = true;
	protected boolean Animated = false;
	protected boolean Liquid = false;
	protected boolean Breakable = true;
	protected boolean Savable = true;
	protected boolean touchable;
	protected boolean manualAnimControl;
	protected boolean needsGround;
	protected boolean HoldTouch = true;
	protected boolean needsSolidPlacement;
	protected boolean needsSolidPlacement_ground;
	protected int BluePrintID;
	protected int MaxFrames;
	protected int MaxStack = 32;
	protected float AnimSpeed;
	protected String CraftingRequirements;
	protected com.badlogic.gdx.graphics.g2d.Sprite ActiveSprite;
	protected TextureRegion alt_worldobj_texture;
	protected TextureRegion alt_particle;
	protected String hit_sound = "impactSoft";
	//stats
	protected int ATK = 0;
	protected float HP = 1;
	protected int Distance = 4;
	protected ArrayList<Integer> strongAgainst = new ArrayList<Integer>();
	protected Inventory parentinv;
	public int CraftingLevel = 0;
	
	public TextureRegion thumbnail;
	public TextureRegion[] thumbnail_array;
	public Animation anim;
	public Animation breakanimation;
	
	
	public InvObject(int id) {
		
		InvObjID = id;
	}
	
	public void OnUse(Bob bob,float x,float y, float angle) {
		
		
		flag = true;
		
		firstOnUse = false;
	}
	float animTime = 0;
	int frame;
	
	
	public void UpdateItem(float delta) {
		
	}
	
	public boolean isStrongAgainst(InvObject item) {
		
		if(strongAgainst.contains(item.type))
		return true;
		else
			return false;
	}
	
	public void onTouch(int x, int y, int x2, int y2) {
		
		
	}
	

}
