package com.jwatson.omnigame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;



public class AIBob {
	
	static final int IDLE = 0;
	static final int RUN = 1;
	static final int JUMP = 2;
	static final int SPAWN = 3;
	static final int DYING = 4;
	static final int DEAD = 5;
	static final int LEFT = -1;
	static final int RIGHT = 1;
	static float ACCELERATION = 20f;
	static final float JUMP_VELOCITY = 6f;
	static final float GRAVITY = 10.0f;
	static final float MAX_VEL = 5f;
	static final float DAMP = 0.90f;
	
	static final int PACKET_UPDATE = 0x001;
	static final int PACKET_MOVELEFT = 0x002;
	static final int PACKET_MOVERIGHT = 0x003;
	static final int PACKET_JUMP = 0x004;
	static final int PACKET_STOP = 0x005;
	static final int PACKET_ONUSE = 0x006;
	static final int PACKET_INVMOVE = 0x007;
	static final int PACKET_MOVEDOWN = 0x008;
	
	
	int BagItem[][];
	
	Vector2 firstPos;
	Vector2 secondPos;
	Vector2 ThirdPos;
	
	public long LastUsed;
	
	
	boolean isMoving;
	int direction;
	
	boolean isFirstTouch = true;
	boolean pressFlag;
	boolean touchFlag;
	boolean needsupdate = false;
	
	int ID;
	
	Queue<Vector2> PosList;
	
	Queue<Vector2> InterpolatePos;
	
	public InvHookObject Grabber;
	public boolean Grabbed;
	public boolean Swinging;
	public Vector2 Grabpos;
	public Vector2 LastPos = new Vector2();
	Vector2 grabpos2 = new Vector2();
	public float Grablen;
	
	float acel;
	public float omega = 0;
	public float E;
	public float alpha = 0;
	public float Xspeed;
	public float Yspeed;
	
	
	byte[] tiles;
	int LastX = -1;
	int LastY = -1;
	
	boolean isMining;
	boolean isBuilding;
	
	public static Bob CurrentBob;
	
	public Inventory inventory;
	InvObject HeldItem;
	
	public float angle;
	
	
	public int GetATK() { if(inventory.BagItem[inventory.currSelected][1] <= 0) return 10; else return 10+inventory.Items[inventory.BagItem[inventory.currSelected][0]].ATK; }
	

	public Vector2 pos = new Vector2();
	public Vector2 newPos = new Vector2();
	Vector2 accel = new Vector2();
	public Vector2 vel = new Vector2();
	public Rectangle bounds = new Rectangle();
	
	

	int state = SPAWN;
	float stateTime = 0;
	int dir = LEFT;
	Map map;
	boolean grounded = false;
	
	TextureRegion tile;
	//Animations
	Animation bobLeft;
	Animation bobRight;
	Animation bobJumpLeft;
	Animation bobJumpRight;
	Animation bobIdleLeft;
	Animation bobIdleRight;
	Animation bobDead;
	Animation spawn;
	Animation dying;
	private float interpTime;
	private boolean InterpStart;
	
	public AIBob(float x, float y) {
		this.pos = new Vector2(x,y);
		this.bounds.width = 0.6f;
		this.bounds.height = 0.8f;
		this.bounds.x = pos.x + 0.2f;
		this.bounds.y = pos.y;
		createAnimations();
		PosList = new LinkedList<Vector2>();
		InterpolatePos = new LinkedList<Vector2>();
		
		//inventory = new Inventory(true);
		//inventory.owner = this;
	}
	
	private void createAnimations () {
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("data/tile.png")), 0, 0, 32, 32);
		Texture bobTexture = new Texture(Gdx.files.internal("data/bob.png"));
		TextureRegion[] split = new TextureRegion(bobTexture).split(20, 20)[0];
		TextureRegion[] mirror = new TextureRegion(bobTexture).split(20, 20)[0];
		for (TextureRegion region : mirror)
			region.flip(true, false);
		bobRight = new Animation(0.1f, split[0], split[1]);
		bobLeft = new Animation(0.1f, mirror[0], mirror[1]);
		bobJumpRight = new Animation(0.1f, split[2], split[3]);
		bobJumpLeft = new Animation(0.1f, mirror[2], mirror[3]);
		bobIdleRight = new Animation(0.5f, split[0], split[4]);
		bobIdleLeft = new Animation(0.5f, mirror[0], mirror[4]);
		bobDead = new Animation(0.2f, split[0]);
		spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
		dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
	}
	
	public void render (SpriteBatch batch) {
		Animation anim = null;
		boolean loop = true;
		if (state == Bob.RUN) {
			if (dir == Bob.LEFT)
				anim = bobLeft;
			else
				anim = bobRight;
		}
		if (state == Bob.IDLE) {
			if (dir == Bob.LEFT)
				anim = bobIdleLeft;
			else
				anim = bobIdleRight;
		}
		if (state == Bob.JUMP) {
			if (dir == Bob.LEFT)
				anim = bobJumpLeft;
			else
				anim = bobJumpRight;
		}
		if (state == Bob.SPAWN) {
			anim = spawn;
			loop = false;
		}
		if (state == Bob.DYING) {
			anim = dying;
			loop = false;
		}
		batch.draw(anim.getKeyFrame(stateTime, loop), pos.x, pos.y, 1, 1);
	}
	double lastUpdate2 = 0;
	public void update (float deltaTime) {
		
		


		if(World.CurrentWorld.server == null) {
			pos = pos.lerp(newPos, 0.3f);
			
			
		lastUpdate2 = System.currentTimeMillis();

		//PosList.clear();
		interpTime = 0;
		}
		
		if(isMoving) {
			accel.x = direction * (Bob.ACCELERATION);
		}
		else {
			accel.x = 0;
		}
		
		accel.y = -GRAVITY;
		accel.mul(deltaTime);

			if(!Swinging)
				vel.add(accel.x, accel.y);
			else {
			
				vel.y += accel.y;
			}
		
		if(vel.y>15)
			vel.y = 10;
		
		if(Grabbed) {
			
			float x2 = pos.x+0.5f;
			float y2 = pos.y+0.5f;
			float vx= (float)(Grablen * Math.sin(alpha));
			float vy= (float)(Grablen * Math.cos(alpha));
			//if(Grabpos.dst(pos) > Grablen) {
				float X3 = x2 - Grabpos.x;
				float Y3 = y2 - Grabpos.y;
				float angle2 = (float) Math.toDegrees(MathUtils.atan2(Y3, X3));
				float newX = Grabpos.x + Grablen*MathUtils.cosDeg((float)angle2);
				float newY = Grabpos.y + Grablen*MathUtils.sinDeg((float) angle2);			

				float rads = (float) Math.atan2(Y3, X3);
				acel = -0.2f * MathUtils.sin((float) (rads + Math.toRadians(90))) * deltaTime;
				omega += acel;
				omega *= 0.98f;
				alpha += omega;
				
				//Gdx.app.debug("", ""+omega);
				omega +=accel.x * 0.006f;
				

				
				
				//vel.x += -(x2 - newX);
				//vel.y += -(y2 - newY);
				Vector2 newvec = new Vector2(-(x2 - newX), -(y2 - newY));

				newvec.mul(deltaTime);
				grabpos2.x = Grabpos.x + (float) (Grablen * Math.cos(alpha));
				grabpos2.y = Grabpos.y + (float) (Grablen * Math.sin(alpha));
			
				Xspeed = grabpos2.x - bounds.x;
				Yspeed = grabpos2.y - bounds.y;
				vel.x = Xspeed * 100;
				vel.y = Yspeed * 100;
				//vel.y =  M

			//}
		}
		if(!Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
			ACCELERATION = 20f;
		if (accel.x == 0) vel.x *= DAMP;
		if (vel.x > MAX_VEL && !Swinging) vel.x = MAX_VEL;
		if (vel.x < -MAX_VEL && !Swinging) vel.x = -MAX_VEL;
		}
		vel.mul(deltaTime);
		
		
		

		
		if(World.CurrentWorld.server != null)
		tryMove();
		vel.mul(1.0f / deltaTime);

		if (state == SPAWN) {
			if (stateTime > 0.4f) {
				state = IDLE;
			}
		}

		if (state == DYING) {
			if (stateTime > 0.4f) {
				state = DEAD;
			}
		}

		
		stateTime += deltaTime;
		interpTime += deltaTime;
		
		if(LastPos.dst(bounds.x,bounds.y) > 0.1f || state == SPAWN)
			needsupdate = true;
	}
	
	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
	
	private void tryMove () {
		
		
		if(Grabbed)
			bounds.x = grabpos2.x;
		else
			bounds.x += vel.x;
		fetchCollidableRects2();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.x < 0)
					bounds.x = rect.x + rect.width + 0.01f;
				else
					bounds.x = rect.x - bounds.width - 0.01f;
				
				if(Grabbed) {
					omega = 0;
					
				}
				vel.x = 0;
			}
		}
		if(Grabbed)
			bounds.y = grabpos2.y;
		else
			bounds.y += vel.y;
		fetchCollidableRects2();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.y < 0) {
					bounds.y = rect.y + rect.height + 0.01f;
					grounded = true;
					if (state != DYING && state != SPAWN) state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
				} else
					bounds.y = rect.y - bounds.height - 0.01f;
				Swinging = false;
				if(Grabbed) {
					omega = 0;
				}
				
				vel.y = 0;
			}
		}

		pos.x = bounds.x - 0.1999f;
		pos.y = bounds.y;
		

	}
	
	void fetchCollidableRects2() {
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		int bobX = (int)bounds.x;
		int bobY = (int)bounds.y;
		int bobX2 = (int)(bounds.x%cWidth);
		int bobY2 = (int)(bounds.y%cHeight);
		
		if(LastX != (bobX/cWidth) || LastY != (bobY/cHeight))
		tiles = Terrain.CurrentTerrain.GetCollisionData(bobX/cWidth,bobY/cHeight);
		
		//Gdx.app.debug(""+LastX + " " +LastY, ""+(bobX/cWidth)+" "+(bobY/cHeight));
		//long nano = TimeUtils.nanoTime();
		
		if(bobX2 + (bobY2*cWidth) < cWidth*cHeight)
		{
			if(tiles[bobX2 + (bobY2*cWidth)] > 0)
			r[0].set(bobX,bobY,1,1);
			else
				r[0].set(-1,-1,1,1);
		}
		else
		{
			if(Terrain.CurrentTerrain.GetTile(bobX, bobY) > 0) {
				Gdx.app.debug("WTF", "BRO");
			}
		}
		
		if((bobX2+1) < cWidth && (bobY2) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[(bobX2+1) + (bobY2*cWidth)] > 0)
		r[1].set(bobX+1,bobY,1,1);
		else
			r[1].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX+1, bobY) > 0)
			r[1].set(bobX+1,bobY,1,1);
		
		if((bobX2+1) < cWidth && (bobY2+1) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[(bobX2+1) + ((bobY2+1)*cWidth)] > 0)
		r[2].set(bobX+1,bobY+1,1,1);
		else
			r[2].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX+1, bobY+1) > 0)
			r[2].set(bobX+1,bobY+1,1,1);
		
		if((bobX2) < cWidth && (bobY2+1) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[bobX2 + ((bobY2+1)*cWidth)] > 0)
		r[3].set(bobX,bobY+1,1,1);
		else
			r[3].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX, bobY+1) > 0)
			r[3].set(bobX,bobY+1,1,1);
		//Gdx.app.debug("TOOKNANO", ""+bobX+ " " +bobY);	
		boolean changed;
		
		LastX = bobX/cWidth;
		LastY = bobY/cHeight;

	}
	
	synchronized void Move(int direction) {
		if(direction != JUMP) {
		state = RUN;
		this.dir = direction;
		this.direction = direction;
		isMoving = true;
		}
		else {
			Jump();
		}
	}
	
	void Jump() {
		state = JUMP;
		vel.y = JUMP_VELOCITY;
		grounded = false;
	}
	
	void AddToPos(float x, float y) {
		
		newPos = new Vector2(x,y);
		
	}
	long StateDelay = 0;
	public void SetState(int state2) {
		// TODO Auto-generated method stub
		state = state2;
		if(System.currentTimeMillis() - StateDelay > 100) {
			
		}
	}
	
	
	


public void MoveInv(int held, int bag) {
	// TODO Auto-generated method stub
	
	if(bag == held) {
		return;
	}
	
	if(inventory.BagItem[bag][1] > 0 && inventory.BagItem[bag][0] != inventory.BagItem[held][0])
	{
		int swapID = inventory.BagItem[bag][0];
		int swapAMT = inventory.BagItem[bag][1];
		
		inventory.BagItem[bag][0] = inventory.BagItem[held][0];
		inventory.BagItem[bag][1] = inventory.BagItem[held][1];
		
		inventory.BagItem[held][0] = swapID;
		inventory.BagItem[held][1] = swapAMT;
		
	}
	else if(inventory.BagItem[bag][0] != inventory.BagItem[held][0])
	{
		inventory.BagItem[bag][0] = inventory.BagItem[held][0];
		inventory.BagItem[bag][1] = inventory.BagItem[held][1];
		
		inventory.BagItem[held][0] = 0;
		inventory.BagItem[held][1] = 0;
	}
	else
	{
		if(inventory.BagItem[bag][1] + inventory.BagItem[held][1] > Inventory.CurrentInventory.MaxItems) {
		
			int diff = Inventory.CurrentInventory.MaxItems - inventory.BagItem[bag][1];
			inventory.BagItem[bag][1] = Inventory.CurrentInventory.MaxItems;
			inventory.BagItem[held][1] -= diff;
				
		}
		else {
		inventory.BagItem[bag][0] = inventory.BagItem[held][0];
		inventory.BagItem[bag][1] += inventory.BagItem[held][1];
		inventory.BagItem[held][1] = 0;
		}
	}
	Gdx.app.debug(""+inventory.BagItem[1][1], "");
}


	
}

