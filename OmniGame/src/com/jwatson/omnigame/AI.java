package com.jwatson.omnigame;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.InventoryObjects.BreakablePot;
import com.jwatson.omnigame.graphics.CustomBatch;

public class AI extends WorldObj {
	
	public static int TYPE_JUMPER = 1;
	public static int TYPE_STATIC = 2;
	public static int TYPE_WALK = 3;
	public static int TYPE_FRIENDLY_MERCHANT = 4;
	public static int TYPE_HOSTILE_WANDER = 5;
	
	public static int MAX_SPAWN = 2;
	public static int MAX_SPAWN_DAY = 1;
	
	public final static int STATE_ATTACKING = 10;
	public final static int STATE_WANDERING = 11;
	public final static int STATE_DYING = 12;
	public final static int STATE_DEAD = 13;
	public final static int STATE_SPAWNING = 14;
	public final static int STATE_STATIC = 15;
	
	
	public final static int LEFT = -1;
	public final static int RIGHT = 1;
	
	protected int direction;
	
	TextureRegion MsgBox;
	boolean DrawMsgBox;
	
	boolean playAnimation;
	protected Animation anim;
	protected Animation mirrorAnim;
	protected Texture jumpFrame;
	protected float Speed = 1f;
	protected boolean invincible = false;
	protected boolean friendly = false;
	protected boolean NightOnly = false;
	protected boolean fixedspawn = false;
	protected String Dialog;
	protected Vector2 spawn_offset;
	protected MessageBoxButton buttons[];
	Vector2 waypoint;
	
	protected WorldObj worldObj;
	protected float Width;
	protected float Height;
	protected float Armor = 1f;
	protected float HP;
	protected float MaxHP;
	protected int ATK;
	protected int type;
	protected int state;
	protected boolean Respawnable = true;
	protected boolean usesAnim;
	JItemAnimation ItemAnimation;
	protected TextureRegion ItemAnim;

	protected Animation deathAnim;
	float MoveDelay;
	float stateTime;
	protected float spawnTimer;
	float collisionTimer;
	float collisionCounter;
	Bob target;
	float TargetDist;
	private float ThinkDelay;
	float jump_multiplier = 1;
	int respawnCounter = 0;
	protected String Items;
	float DeadPosX,DeadPosY;
	float animTime,swingTime;
	int frame;
	int stuckCounter;
	private int counter;
	protected boolean randomSpawn;
	float random;
	float checkBoundsTimer;
	
	protected TextureRegion Arm,ArmFlip;
	
	public AI(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		HPBarActive = true;
		state = STATE_SPAWNING;
        //spawnTimer = (float) (10 + 20*Math.random());
        isActive = false;
        MsgBox = MapRenderer.Texture_Atlas.findRegion("MSC_MsgBox");
        pos.x = x;
        pos.y = y;
        bounds.x = -1;
        bounds.y = -1;
        Width = width;
        Height = height;
        if(spawnTimer > 0)
        random = MathUtils.random(10f, 1200f);
        waypoint = new Vector2();
        isAI = true;
        World.CurrentWorld.AI.add(this);
        MaxSpeed -= Math.random()*1.5f;
	}


	public void onDamaged(int atk) {
		// TODO Auto-generated method stub
		if(invincible)
			return;
		
		
		HP -= atk;
		if(HP <= 0) {
			state = STATE_DYING;
			stateTime = 0;
		}
		respawnCounter = 0;
		
		if(type != TYPE_STATIC) {
			SoundManager.PlaySound("impactMob",3);			
		}
	}

	@Override
	public void onDamaged(Bob bob, InvObject usedItem, int atk) {
		// TODO Auto-generated method stub
		if(invincible)
			return;
		
		if(type != TYPE_STATIC) {
		vel.x += 6*bob.dir + Math.random()*bob.dir;
		vel.y += 5;
		grounded = false;
		}
		ThinkDelay -= 0.2f;
		jump_multiplier = 1;
		HP -= atk/Armor;
		healthBarTimer = 0;
		Inventory.CurrentInventory.dmgTxts.add(new dmgText(pos.x, pos.y+1, atk/Armor));
		if(HP <= 0) {
			DropItems(pos.x, pos.y);
			bob.onKilled(this);
			state = STATE_DYING;
			stateTime = 0;
		}
		respawnCounter = 0;
		
		if(type != TYPE_STATIC) {
			SoundManager.PlaySound("impactMob",3);			
		}
	}
	@Override
	public void onDamaged(WorldObj obj, InvObject usedItem, int atk) {
		// TODO Auto-generated method stub
		if(invincible)
			return;
		
		if(type != TYPE_STATIC) {
		vel.x += obj.vel.x;
		vel.y += 5;
		grounded = false;
		}
		ThinkDelay -= 0.2f;
		jump_multiplier = 1;
		HP -= atk/Armor;
		healthBarTimer = 0;
		Inventory.CurrentInventory.dmgTxts.add(new dmgText(pos.x, pos.y+1, atk/Armor));
		if(HP <= 0) {
			DropItems(pos.x, pos.y);
			//bob.onKilled(this);
			state = STATE_DYING;
			stateTime = 0;
		}
		respawnCounter = 0;
		
		if(type != TYPE_STATIC) {
			SoundManager.PlaySound("impactMob",3);			
		}
	}
	
	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);

		
		if(Bob.CurrentBob == null)
			return;
		
//		if(state == STATE_STATIC)
//				return;
		

		
		if(state == STATE_SPAWNING) {
			Gdx.app.debug("HELO", "CHILD");
			isActive = false;
			if(stateTime > (spawnTimer + random)) {
				//Gdx.app.debug("SPAWNED", ""+(spawnTimer + random) + " " +stateTime); 
				if(NightOnly)
					if(!World.CurrentWorld.isNight) {
						stateTime = 0;
						return;
					}
				
				random = MathUtils.random(30, 130);
				HP = MaxHP;
				if(type != TYPE_STATIC)
				pos = getBestSpawn();
				waypoint.set(pos);
				bounds.x = pos.x;
				bounds.y = pos.y;
				if(type == TYPE_STATIC)
					state = STATE_STATIC;
				else if(type == TYPE_FRIENDLY_MERCHANT || type == TYPE_HOSTILE_WANDER)
					state = STATE_WANDERING;
				else
				state = STATE_ATTACKING;
				
				
				stateTime = 0;
				isActive = true;
				
				
			}
		}
		else if(state == STATE_ATTACKING) {
		
		sprite.setPosition(pos.x, pos.y);
		
		if(type == TYPE_JUMPER) {
			
			JumperThink();
		
		}
		if(type == TYPE_WALK) {
			
			WalkThink();
			
		}
		checkBoundsTimer += deltaTime;
		
	}
		else if(state == STATE_DYING) {
			DeadPosX = bounds.x + 0.5f;
			DeadPosY = bounds.y + 0.5f;
			isActive = false;
			bounds.x = -1;
			bounds.y = -1;
			pos.x = 0;
			pos.y = 0;
			if(stateTime > 0.6f) {
				if(!Respawnable) {
					removalflag = true;
					if(NightOnly)
					World.CurrentWorld.NightMobCounter--;
					return;
				}
				stateTime = 0;
				state = STATE_SPAWNING;
				//spawnTimer = (float) (10 + 20*Math.random());
				
			}
			
		}
		else if(state == STATE_WANDERING) {
			
				WanderThink();
			
		}
		
		
		
		if(checkBoundsTimer > 0.6f) {
			checkBoundsTimer = 0;
			for(AI ai : World.CurrentWorld.AI) {
				if(ai != this)
				if(ai.state == STATE_ATTACKING)
				if(bounds.overlaps(ai.bounds)) {
					ai.checkBoundsTimer = 0;
					
					float xdif = pos.x - ai.pos.x;
					if(xdif > 0)
					{
						if(Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f) - 1, (int)pos.y))
							xdif = 1;
						else
						xdif = -1;
					}
					else {
						if(Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f) + 1, (int)pos.y))
						xdif = -1;
						else
							xdif = 1;
					}
					
					vel.x += 5 * xdif;
					vel.y = 5;
					
				}
			}
		}
		
		if(ItemAnimation == null)
			swingTime += deltaTime;
		else
			swingTime = 0;
		
		AnimTime += deltaTime;
		animTime += deltaTime;
		stateTime += deltaTime;
		collisionTimer += deltaTime;
	}
	
	float wanderCounter = -1;
	private void WanderThink() {
		

		if(Dialog != null) {
			
			if(Bob.CurrentBob.pos.dst(pos) < 2) {
				waypoint.set(pos);
				float dX = Bob.CurrentBob.pos.x - pos.x;
				if(dX < 0)
					direction = LEFT;
				else
					direction = RIGHT;
				
				DrawMsgBox = true;
			}
			else
				DrawMsgBox = false;
			
		}
		
		if(isMoving) {
			playAnimation = true;
			
			if(!Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f)+direction, (int)pos.y-1))
				if(!Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f)+direction, (int)pos.y-2))
					if(!Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f)+direction, (int)pos.y-3))
						waypoint.set(pos);
			
			if(Terrain.CurrentTerrain.isSolid((int)((pos.x+0.5f)+(direction*0.5f)), (int)pos.y)) {
				vel.y = 10;
				grounded = false;
				isMoving = false;
				waypoint.x += direction;
			}
		}
		else {
			playAnimation = false;
			
		}
		if(target != null) {
			waypoint.set(target.pos);
		}
		float dir =  waypoint.x - pos.x;
		if(grounded)
		if(dir > 0.1f || dir < -0.1f) {
			
			
			if(dir >= 0) {
				//accel.x = Bob.ACCELERATION;
				accelx = Bob.ACCELERATION * RIGHT;
				direction = RIGHT;
			}
			else {

				//accel.x = -Bob.ACCELERATION;
				accelx = Bob.ACCELERATION * LEFT;
				direction = LEFT;
			}
			
			
			
			
			
			isMoving = true;
			
			if(target != null) {
				
				if(target.pos.dst(pos) <= 2) {
					if(ItemAnimation == null)
					SwingWeapon(direction,0.3f);
				}
				
			}
			
		}
		else {
			isMoving = false;
			accel.x = 0;
			accel.y = 0;
			vel.x = 0;
			
		}
		
		if(stateTime > 3) {
			
			if(!isMoving) {
			if(MathUtils.randomBoolean()) {
				
				waypoint.x = waypoint.x + Float.valueOf(MathUtils.random(2, 5));
				
			}
			else {
				
				waypoint.x = waypoint.x - Float.valueOf(MathUtils.random(2, 5));
			}
			}
			stateTime = 0;
			
			if(!friendly)
			if(Bob.CurrentBob.pos.dst(pos) < 5)
				target = Bob.CurrentBob;
			else target = null;
			//Gdx.app.debug("", ""+dist);

			
		}
		
		
		
	}

	
	//animation stuff
	float AnimTime,totalAnimTime;
	int ItemAnimdir;
	protected int AnimationStyle;
	boolean isAttacking = true;
	Rectangle wep_bounds = new Rectangle();
	void SwingUpdate(CustomBatch batch) {
		
if(ItemAnimation != null) {
			
			
			boolean flag = false;
			if(AnimationStyle == InvObject.ANIMATE_STYLE_ROTATE) {
			if(AnimTime >= totalAnimTime) {
				flag = true;
			}
			if(direction == Bob.LEFT)
				ItemAnimation.setOrigin(0.25f, 0.25f);
			else
				ItemAnimation.setOrigin(0.75f, 0.25f);
			//ItemAnimation.setScale(0.7f);
			ItemAnimation.setPosition(pos.x + 0.25f*ItemAnimdir, pos.y+0.2f);
			
			
			if(direction != ItemAnimdir) {
				ItemAnimation.flip(true, false);
				ItemAnimdir = direction;
			}
			
			ItemAnimation.setRotation(-((direction*45)+(direction*90 * (AnimTime/totalAnimTime))));
			}
		
		if(AnimationStyle == InvObject.ANIMATE_STYLE_THRUST) {
			
//			if(AnimTime < totalAnimTime/2)
//			ItemLerpPos.set(pos.x+(dir*0.5f),pos.y+0.1f);
//			else
//			ItemLerpPos.set(pos.x,pos.y+0.1f);	
//			
//			ItemPos.lerp(ItemLerpPos, 0.4f);
			if(direction != ItemAnimdir) {
				ItemAnimation.flip(true, false);
				ItemAnimdir = direction;
			}
			
			if(direction == Bob.LEFT)
				ItemAnimation.setOrigin(0, 0.2f);
			else
				ItemAnimation.setOrigin(1, 0.2f);
			
			ItemAnimation.setRotation(-135*direction);
			//float newX = pos.x - (0.3f * Bob.CurrentBob) + (AnimTime*dir);
			//float dist = Math.abs(pos.x - newX);
//			if(dist < 0.4f)
//			ItemAnimation.setX(newX);
//			else
//				ItemAnimation.setX(pos.x+(0.4f*dir));
			float x;
			if(AnimTime < totalAnimTime/2)
				x = (pos.x) - direction*0.4f + (direction*(AnimTime/totalAnimTime));
			else
				x = (pos.x) - (direction*0.4f) + (1*direction-(direction*(AnimTime/totalAnimTime)));
			
			
			ItemAnimation.setPosition(x - 0.2f*direction, pos.y+0.1f);
			
			if(AnimTime >= totalAnimTime) {
				AnimTime = 0;
				flag = true;
				isAttacking = false;
			}
			
		}
		if(direction == LEFT)
			wep_bounds.set(pos.x+direction*0.7f+0.5f,pos.y,0.7f,1f);
			else
				wep_bounds.set(pos.x+0.5f,pos.y,0.7f,1f);
		
		if(wep_bounds.overlaps(Bob.CurrentBob.bounds)) {
			Bob.CurrentBob.OnDamage(this, ATK, 5f*direction);
		}
		
		ItemAnimation.draw(batch);
		
		if(flag)
			ItemAnimation = null;
		

		
}
if(Arm != null && ArmFlip != null) {
if(swingTime == 0) {
	if(direction == Bob.LEFT)
		batch.draw(ArmFlip, pos.x, pos.y+0.375f,0,0,0.25f,0.125f,1,1,0);
	else
		batch.draw(Arm,1, pos.x+0.75f, pos.y+0.375f,0.25f,0.125f);
}
if(swingTime > 0) {

		if(direction == Bob.LEFT)
			batch.draw(ArmFlip, pos.x+0.25f, pos.y+0.25f,0,0,0.25f,0.125f,1,1,90);
		else
			batch.draw(Arm, pos.x+0.75f, pos.y+0.5f,0,0,0.25f,0.125f,1,1,-90);
	}
}
		
	}
	
	private void SwingWeapon(int dir,float time) {
		// TODO Auto-generated method stub
		ItemAnimation = new JItemAnimation(new TextureRegion(ItemAnim));
		AnimTime = 0;
		ItemAnimdir = dir;
		totalAnimTime = time;
		if(ItemAnimdir == RIGHT)
			ItemAnimation.flip(true, false);
		
		isAttacking = true;
		
	}




	private void WalkThink() {
		
		if(target != null)
		if(pos.dst(target.pos) > 30) {
			isActive = false;
			bounds.x = -1;
			bounds.y = -1;
			Gdx.app.debug("respawning", "");
			state = STATE_SPAWNING;
			//spawnTimer = (float) (10 + 20*Math.random());
			stateTime = 0;
		}
		
		if(Terrain.CurrentTerrain.isSolid((int)((pos.x+0.5f)+(direction*0.5f)), (int)pos.y)) {
			vel.y = 7;

			//isMoving = false;
			waypoint.x += direction;
		}
		
		if(healthBarTimer > 0.6f)
			playAnimation = true;
		else
			playAnimation = false;
		
		if(stateTime > 1f) {
			target = Bob.CurrentBob;
			float dir = target.pos.x - pos.x;
			isMoving = true;
			
			if((int)LastPos.dst(pos) <= 0.05f && healthBarTimer >2f) {
				
				vel.y = jump_multiplier * 10;
				counter++;
				
			}
			else {
				counter = 0;
				jump_multiplier = 1;
			}
			
			if(counter > 3) {
				jump_multiplier += 0.5f;
				
			}
			
			
			
			if(dir > 0) {
				accel.x = 50;
				accelx = 50;
				direction = RIGHT;
			}
			else {

				accel.x = -50;
				accelx = -50;
				direction = LEFT;
			}
			stateTime = 0;
			LastPos.set(pos);
			
		}
		
	}


	Vector2 LastPos = new Vector2();
	
	boolean jumpflag;
	float jumperTimer;
	private void JumperThink() {
		
		if(respawnCounter > 30) {
			isActive = false;
			bounds.x = -1;
			bounds.y = -1;
			Gdx.app.debug("respawning", "");
			state = STATE_SPAWNING;
			//spawnTimer = (float) (10 + 20*Math.random());
			stateTime = 0;
		}
	
		if(grounded) {
		float dX = (Bob.CurrentBob.pos.x+0.5f) - (pos.x+0.5f);
		if(dX > 0)
			direction = Bob.RIGHT;
		else
			direction = Bob.LEFT;
		}
		
		if(stateTime > 0.9f && stateTime < 0.7f + jumperTimer)
			playAnimation = true;
		if(stateTime > 1f + jumperTimer) {
			
			
			
			
			playAnimation = false;
			if((int)LastPos.dst(pos) <= 0.1f)
				jump_multiplier += 0.5f;
			else
				jump_multiplier = 1;
			target = Bob.CurrentBob;
			TargetDist = target.pos.dst(pos);
			for(Bob b : World.CurrentWorld.ClientBobs.values())
				if(b.pos.dst(pos) < TargetDist)
					target = b;
			
			float dir = target.pos.x - pos.x;
			if(dir < 0)
				dir = -1;
			else
				dir = 1;
			
		
			vel.y = (9 * jump_multiplier) + jumperTimer;
			if(dir > 0) {
				vel.x = 7 + jumperTimer;
				direction = RIGHT;
			}
			else {

				vel.x = -6  - jumperTimer;
				direction = LEFT;
			}
			grounded = false;
			jumperTimer = MathUtils.random()*3f;
		stateTime = 0;
		respawnCounter++;
		LastPos.set(pos);
		
		}

		

		
	}
	
	
	private void JumpTo(float x, float y) {
		
		if(x == 0 || y == 0)
			return;
		
		float distance = x - pos.x;
		float angleToPoint = MathUtils.atan2(y - pos.y, x - pos.x);
		float distanceFactor = 1f/10f;
		float angleCorrection = (MathUtils.PI*0.18f) * (distance * distanceFactor);
		vel.x = MathUtils.sin(angleToPoint+angleCorrection) * 15f;
		vel.y = MathUtils.cos(angleToPoint+angleCorrection) * 15f;
		


	}




	private int GetNextHighest_V(int x, int y) {
		// TODO Auto-generated method stub
		
		while(true) {
			
			if(!Terrain.CurrentTerrain.isSolid(x, y))
				return y;
			
			y++;
			
		}
		
		//return null;
	}

	Stack<Vector2> rSpawn;
	
	
	public Vector2 getBestSpawn() {
		Vector2 spawn = new Vector2(Bob.CurrentBob.pos);
	

		if(!randomSpawn)
		spawn.x += spawn_offset.x * Bob.CurrentBob.dir;
		else {
			if(MathUtils.randomBoolean())
				spawn.x += spawn_offset.x;
			else
				spawn.x -= spawn_offset.x;
		}
		
		spawn.y += spawn_offset.y;
		int counter = 0;
		if(spawn.x < 0)
			spawn.x = 0;
		
		if(fixedspawn)
			spawn.set(pos);
		
		if(type == TYPE_FRIENDLY_MERCHANT) {
			spawn.set((Terrain.chunkWidth * Terrain.Width)/2, (Terrain.chunkHeight * Terrain.Height)/2);
		}
		
		while(true) {
			
			
			Terrain.CurrentTerrain.GetChunkByID((int)(spawn.x/Terrain.CurrentTerrain.chunkWidth), (int)(spawn.y/Terrain.CurrentTerrain.chunkHeight));
			
			if(!Terrain.CurrentTerrain.isCollidable(spawn.x, spawn.y))
				return spawn;
			
			if(spawn.y < Bob.CurrentBob.pos.y + Terrain.CurrentTerrain.chunkHeight)
				spawn.y++;
			else {
				spawn.y = Bob.CurrentBob.pos.y;
				spawn.x--;
			}
			
			if(counter > 100) {
				
				Gdx.app.debug("failed", "bro");
				state = STATE_SPAWNING;
				//aaspawnTimer = (float) (10 + 20*Math.random());
				stateTime = 0;
				
			}
				
			
		}
		
		
		
	}

	@Override
	public void OnCollision(Bob bob) {
		// TODO Auto-generated method stub
		super.OnCollision(bob);
		if(type == TYPE_STATIC)
			return;
		respawnCounter = 0;
		if(!friendly)
		bob.OnDamage(this, ATK, (bob.pos.x-pos.x)*400);
	}
	
	
	@Override
	public void render(CustomBatch batch) {
		// TODO Auto-generated method stub
		super.render(batch);

		
		if(!usesAnim) {
			
//			int x = (int)(pos.x/Terrain.CurrentTerrain.chunkWidth);
//			int y = (int)(pos.y/Terrain.CurrentTerrain.chunkHeight);
//			int x2 = (int)(pos.x%Terrain.CurrentTerrain.chunkWidth);
//			int y2 = (int)(pos.y%Terrain.CurrentTerrain.chunkHeight);
			
			float brightness = (float) Terrain.CurrentTerrain.light.GetBrightness((int)pos.x,(int)pos.y);
			
			float a = brightness/15f;

			

		
		}
		else if(state != STATE_DYING) {
			if(state != STATE_SPAWNING) {
			int x = (int)(pos.x/Terrain.CurrentTerrain.chunkWidth);
			int y = (int)(pos.y/Terrain.CurrentTerrain.chunkHeight);
			int x2 = (int)(pos.x%Terrain.CurrentTerrain.chunkWidth);
			int y2 = (int)(pos.y%Terrain.CurrentTerrain.chunkHeight);
			
			float brightness = (float) Terrain.CurrentTerrain.light.GetBrightness(x, y, x2, y2);
			
			SwingUpdate(batch);
			
			float a = brightness/15f;
			
			if(type != TYPE_STATIC) {
			
				if(playAnimation) {
				if(direction == RIGHT)
				batch.draw(anim.getKeyFrame(animTime,true),1, pos.x, pos.y,1,1);
				else
				batch.draw(mirrorAnim.getKeyFrame(animTime,true),1, pos.x, pos.y,1,1);
				}
				
				else {
					if(direction == RIGHT)
						batch.draw(anim.getKeyFrame(1),1, pos.x, pos.y,1,1);
						else
						batch.draw(mirrorAnim.getKeyFrame(1),1, pos.x, pos.y,1,1);
				}
				}
			else {
				batch.draw(anim.getKeyFrame(frame),1, pos.x, pos.y,1,1);
			}
			}

		}
		if(state == STATE_DYING) {
			
			batch.draw(deathAnim.getKeyFrame(stateTime,false),1,pos.x,pos.y,1,1);
			
		}
		
		if(DrawMsgBox) {
			batch.draw(MsgBox,1, pos.x, pos.y+1,1,1);
		}
		
		batch.setColor(Color.WHITE);
	}
	
	void DropItems(float x, float y) {
		
		String[] items = Items.split(" ");
		
	for(int i = 0; i < items.length; i++) {
		
		int id = Inventory.CurrentInventory.GetItemID(items[i]);
		float chance = Integer.valueOf(items[i+1]);
		int amt = Integer.valueOf(items[i+2]);
		
		for(int i2=0; i2<amt; i2++) {
		if(MathUtils.random(100) < chance) { 
		WorldObj obj = new WorldObj(World.CurrentWorld, x, y, 1, 1, id, 1);
		if(MathUtils.randomBoolean())
		obj.vel.x = 5;
		else
			obj.vel.x = -5;
		
		obj.vel.y = (float)(3+Math.random()*5);
		World.CurrentWorld.pendingWorldObjs.add(obj);
		}
		}
		i += 2;
	}
		
	}
	
	
	@Override
	public void onCollision(boolean x, boolean y) {
		// TODO Auto-generated method stub
		super.onCollision(x, y);
		
		if(x) {
			
			if(isMoving) {
			
			if(collisionCounter >= 2) {
				collisionTimer = 0;
				collisionCounter = 0;
				waypoint.set(pos);
				isMoving = false;
			}
			
			if(collisionTimer > 0.5f) {
				collisionCounter++;
			}
			
			}
			
		}
	}
	
	@Override
	public void onTouch() {
		// TODO Auto-generated method stub
		super.onTouch();
		if(DrawMsgBox)
		if(Dialog !=null)
		MessageBox.CreateMessageBox(name,Dialog, 30, 200, 80, 0.1f,buttons);
	}
	
	public void OnButtonPressed(String button) {
		
		
	}
}

