package com.jwatson.omnigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.jwatson.omnigame.graphics.CustomBatch;



public class WorldObj {
	
	
	
	World parent;
	
	public Sprite sprite;

	
	public abstract class WorldObjUpdate {
		public abstract void update();
		public abstract void onRemove(WorldObj obj);
		public abstract void OnCollision(float x, float y);
	}
	
	
	public Map<String, Float> flags;
	
	
	static final float ACCELERATION = 20f;
	static final float JUMP_VELOCITY = 10;
	static final float GRAVITY = 30.0f;
	static final float MAX_VEL = 5f;
	static final float DAMP = 0.75f;
	
	public float mass = 1f;
	

	
	
	protected float y_offset = 0;
	
	boolean grounded;
	
	public boolean HasCollided;
	protected boolean isMoving;
	
	float accelx;
	Vector2 accel;
	public Vector2 pos;
	public Vector2 vel;
	
	int amount;
	protected int ID;

	float Width;
	float Height;

	protected float MaxSpeed = 4.7f;
	protected boolean Touchable;

	protected boolean is_Projectile;
	
	public boolean deletable = true;
	public boolean isAI;

	
	protected boolean ignore_collision;
	protected boolean isTurret;
	

	protected float healthBarTimer;
	protected boolean isActive = true;
	float stateTime;
	
	
	protected WorldObj stuckOn;
	protected Vector2 stuck_offset;
	protected float rotation;
	
	public void SetWidth(float width) { Width = width; }
	public void SetHeight(float height) { Height = height; }
	
	public boolean collision = true;
	protected boolean damageable;
	
	protected String name;
	public boolean removalflag;
	public Rectangle bounds;
	
	
	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

	public AI ai;

	protected boolean HPBarActive;
	protected boolean custom_render;

	protected boolean stuckOnBob;
	
	public WorldObj(World p, float x, float y, float width, float height, int id,int amount) {
		
		parent = p;
		healthBarTimer = 20f;
		pos = new Vector2(x,y);
		vel = new Vector2(0,0);
		Width = width;
		Height = height;
		this.amount = amount;
		ID = id;
		if(id>0)
		if(Item.Items[id].AltDropId > 0)
			id = Item.Items[id].AltDropId;
		name = parent.MainBob.inventory.GetItemNameByID(id);
		bounds = new Rectangle(x,y,Width*0.95f,Height*0.95f);
		accel = new Vector2(0,0);
		
		if(Inventory.CurrentInventory.Items[id].type == InvObject.Type.CURRENCY) {
			
			Width = 0.35f;
			Height = 0.35f;
			bounds.setWidth(0.35f);
			bounds.setHeight(0.35f);
			
		}
		else		if(Inventory.CurrentInventory.Items[id].type == InvObject.Type.BLOCK) {
			
			Width = 0.35f;
			Height = 0.35f;
			bounds.setWidth(0.35f);
			bounds.setHeight(0.35f);
			
		}
		
		flags = new HashMap<String, Float>();

		
	}
	
	public WorldObj(float x, float y, float width, float height) {
		
		pos = new Vector2(x,y);
		vel = new Vector2(0,0);
		healthBarTimer = 20f;
		//sprite = ai.sprite;
		//sprite.setPosition(x, y);
		//sprite.setSize(ai.Width, ai.Height);
		//Width = ai.Width;
		//Height = ai.Height;
		Width = width;
		Height = height;
		if(is_Projectile) {
			bounds = new Rectangle(x,y,0.01f,0.01f);
		}
		else
		bounds = new Rectangle(x,y,Width,Height);
		accel = new Vector2(0,0);
		parent = World.CurrentWorld;
		collision = false;
		
		
//		LabelStyle tfs = new LabelStyle();
//		tfs.font = Inventory.CurrentInventory.Font;
//		tfs.fontColor = new Color(0, 0, 0, 1);
//		DmgText = new Label("", tfs);
	}
	
	public void setFlag(String name, float num) { flags.put(name, num); }
	public float getFlag(String name) { return flags.get("name"); }
	
  	void update(float deltaTime) {
		
		if(!isActive || Inventory.CurrentInventory.BagActive && isAI)
			return;
		
		
		
		
		if(mass > 0) {
			
			if(isMoving && healthBarTimer > 0.6f) {
				accel.x = accelx;
				
			}
		
		accel.y = -GRAVITY;
		accel.mul(deltaTime);
		
		vel.add(accel.x, accel.y);
		

		
			if(grounded)
				if (accel.x == 0) vel.x *= DAMP;
			
			if (vel.x > MaxSpeed) vel.x = MaxSpeed;
			if (vel.x < -MaxSpeed) vel.x = -MaxSpeed;
			
		vel.mul(deltaTime);
		trymove();
		vel.mul(1.0f / deltaTime);
		}
		
		
		if(bounds.overlaps(parent.MainBob.bounds) && collision && stateTime > 0.75f) {
			//Collided. Quick addtobag hack for now
			if(name.equals("MSC_Coin_Silver"))
				SoundManager.PlaySound("pickupCoin", 1,true);
			
			Bob.CurrentBob.inventory.AddToBag(name, amount);
			removalflag = true;
		}
		
		
		healthBarTimer += deltaTime;
		stateTime += deltaTime;
	}
  	
  	void newmove() {
  		
  	}
	
	void trymove() {

		if(stuckOn == null && !stuckOnBob) {
		if(grounded && ai == null)
		{
			vel.y = 0;
			if(!Terrain.CurrentTerrain.isCollidable(pos.x, pos.y-1))
				grounded = false;
			return;
		}
		
		if(ai != null && grounded)
			vel.x *= 0.9;
		
		boolean x=false,y=false;
		if(is_Projectile)
		pos.x += vel.x;
			else
		bounds.x += vel.x;
		if(!ignore_collision)
			if(bounds.x > 0 && bounds.y > 0)
		fetchCollidableRects2();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				bounds.x = pos.x+0.0999f;
				x=true;
				HasCollided = true;
				
				if(!isAI)
				vel.x = 0;
				//CollisionX = (int) r[i].x;
				//CollisionY = (int) bounds.y;
			}
		}
		if(is_Projectile)
			pos.y += vel.y;
		else
		bounds.y += vel.y;
		if(!ignore_collision)
		fetchCollidableRects2();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.y < 0) {
					bounds.y = pos.y;
					grounded = true;
				}
				else if(vel.y > 0)
					bounds.y = pos.y;
				vel.y = 0;
				y=true;
				HasCollided = true;
				
			}
		}
		
		if(is_Projectile) {
			bounds.x = pos.x + 0.5f + 0.45f*MathUtils.cosDeg(rotation);
			bounds.y = pos.y + 0.5f + 0.45f*MathUtils.sinDeg(rotation);
			//pos.x = (bounds.x-0.0999f);
			//pos.y = (bounds.y);
			//- (0.8f *MathUtils.cosDeg(rotation+45)
		}
		else {
		pos.x = (bounds.x - 0.0999f);
		pos.y = bounds.y;
		}
		
		if(HasCollided)
			onCollision(x, y);
		}
		else {
			if(stuckOnBob) {
				pos.x = Bob.CurrentBob.pos.x - stuck_offset.x;
				pos.y =  Bob.CurrentBob.pos.y - stuck_offset.y;
			}
			else {
			pos.x = stuckOn.pos.x - stuck_offset.x;
			pos.y = stuckOn.pos.y - stuck_offset.y;
			}
		}
		
		if(sprite != null)
			sprite.setPosition(pos.x, pos.y);
		

	}
	
	public void onTouch() { 
		
	}
	
void fetchCollidableRects2() {
		
		
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		int bobX = (int)bounds.x;
		int bobY = (int)bounds.y;
		int x = bobX/cWidth;
		int y = bobY/cHeight;
		int bobX2 = (int)((bounds.x)%cWidth);
		int bobY2 = (int)((bounds.y)%cHeight);
		
		
		
			if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2, bobY2))  {
			if(Item.Items[Terrain.GetTile(bobX, bobY)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[0].set(bobX+0.40625f,bobY,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX, bobY)].type == InvObject.Type.PLATFORM) {
				if(vel.y < 0 && pos.y > bobY+0.48f) {
				r[0].set(bobX,bobY+0.48f,1,0.1f);
				
				}
				else
					r[0].set(-1,-1,1,1);
			}
			else
			r[0].set(bobX,bobY,1,1);
			}
			else
				r[0].set(-1,-1,1,1);

		
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2+1, bobY2)) {
			if(Item.Items[Terrain.GetTile(bobX+1, bobY)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[1].set(bobX+1+0.40625f,bobY,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX+1, bobY)].type == InvObject.Type.PLATFORM) {
//				if(vel.y < 0)
//				r[1].set(bobX+1,bobY+0.48f,1,0.1f);
//				else
					r[1].set(-1,-1,1,1);
			}
			else
		r[1].set(bobX+1,bobY,1,1);
		}
		else
			r[1].set(-1,-1,1,1);
		
		
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2+1, bobY2+1)) {
			if(Item.Items[Terrain.GetTile(bobX+1, bobY+1)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[2].set(bobX+1+0.40625f,bobY+1,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX+1, bobY+1)].type == InvObject.Type.PLATFORM) {
//				if(vel.y < 0)
//				r[2].set(bobX+1,bobY+1+0.48f,1,0.1f);
//				else
					r[2].set(-1,-1,1,1);
			}
			else
		r[2].set(bobX+1,bobY+1,1,1);
		}
		else
			r[2].set(-1,-1,1,1);

		
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2, bobY2+1)) {
			if(Item.Items[Terrain.GetTile(bobX, bobY+1)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[3].set(bobX+0.40625f,bobY+1,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX, bobY+1)].type == InvObject.Type.PLATFORM) {
//				if(vel.y < 0)
//				r[3].set(bobX,bobY+1+0.48f,1,0.1f);
//				else
					r[3].set(-1,-1,1,1);
			}
			else
		r[3].set(bobX,bobY+1,1,1);
		}
		else
			r[3].set(-1,-1,1,1);

		boolean changed;
		

	}
	
	private void fetchCollidableRects () {
		
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		
		int tWidth = Terrain.CurrentTerrain.Width * cWidth;
		int tHeight = Terrain.CurrentTerrain.Height * cHeight;
		
		int idX = (int)bounds.x/cWidth;
		int idY = (int)bounds.y/cHeight;
		
		int p1x = (int)bounds.x - (idX*cWidth);
		int p1y = (int)Math.floor(bounds.y - (idY*cHeight));
		int p2x = (int)(p1x + bounds.width);
		int p2y = (int)Math.floor(p1y);
		int p3x = (int)(p1x + bounds.width);
		int p3y = (int)(p1y + bounds.height);
		int p4x = (int)p1x;
		int p4y = (int)(p1y + bounds.height);
		
		
		byte[] tiles = Terrain.CurrentTerrain.GetCollisionData(idX,idY);
		int tile1 = tiles[p1x + (p1y*cWidth)];
		int tile2 = tiles[p2x + (p2y*cWidth)];
		int tile3 = tiles[p3x + (p3y*cWidth)];
		int tile4 = tiles[p4x + (p4y*cWidth)];


		if (tile1 > 0)
			r[0].set((idX*cWidth)+p1x, (idY*cHeight)+p1y, 1, 1);
		else
			r[0].set(-1, -1, 0, 0);
		if (tile2 > 0)
			r[1].set((idX*cWidth)+p2x, (idY*cHeight)+p2y, 1, 1);
		else
			r[1].set(-1, -1, 0, 0);
		if (tile3 > 0)
		{

			r[2].set((idX*cWidth)+p3x, (idY*cHeight)+p3y, 1, 1);
		}
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 > 0)
			r[3].set((idX*cWidth)+p4x, (idY*cHeight)+p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);
	}
	
	Vector3 dmgProjection = new Vector3();
	public void render(CustomBatch batch2) {
		
		if(!isActive)
			return;
		if(!custom_render) {
		if(!isAI) {
		InvObject item = Inventory.CurrentInventory.Items[ID];
		
		if(item.type == InvObject.Type.PROJECTILE) {
			batch2.draw(Item.Items[ID].thumbnail, pos.x, pos.y, 0.5f, 0.5f, 1, 1, 0.6f, 0.6f, rotation-45);
		}
		else if(item.type == InvObject.Type.CURRENCY) {
			batch2.draw(Item.Items[ID].thumbnail,1, pos.x, pos.y-0.375f, 1, 1);
		}
		else
		{
		if(item.alt_worldobj_texture == null)
		batch2.draw(item.thumbnail,1,pos.x,pos.y+y_offset,Width,Height);
		else
			batch2.draw(item.alt_worldobj_texture,1,pos.x,pos.y,Width,Height);
		}
		}
		}
		DrawStatusBar(batch2);
		
	}
	public void OnCollision(Bob bob) {
		// TODO Auto-generated method stub
		
	}
	
	
	public boolean isInActiveChunk() {
		
		int x = (int)(pos.x/Terrain.CurrentTerrain.chunkWidth);
		int y = (int)(pos.y/Terrain.CurrentTerrain.chunkHeight);
		
		TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(ch == null)
			return false;
		if(!ch.isActive)
			return false;
		
		return true;
	}
	
	Vector3 culling = new Vector3();
	public void DrawStatusBar(CustomBatch batch2) {
		
		if(healthBarTimer < 3.5f)
		if(isAI)
			if(ai.type != AI.TYPE_STATIC) {
				culling.set(pos.x+0.5f, pos.y+0.5f, 0);
		if(MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 0.5f)) {
			
			batch2.draw(Inventory.CurrentInventory.hp_bar,1, pos.x-0.577f, pos.y+1f,2,0.277f);
			float percent = ai.HP / ai.MaxHP;
			batch2.draw(Inventory.CurrentInventory.hp_colour,1, pos.x-0.577f, pos.y+1f,percent*2f,0.277f);

		}
			}
		
	}
	
	public void onCollision(boolean x, boolean y) {
		
	}
	public void onDamaged(Bob bob, InvObject usedItem, int atk) {
		// TODO Auto-generated method stub
		
	}
	public void onDamaged(WorldObj obj, InvObject usedItem, int atk) {
		// TODO Auto-generated method stub
		
	}
}
