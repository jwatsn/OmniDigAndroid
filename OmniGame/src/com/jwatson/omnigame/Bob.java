package com.jwatson.omnigame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.jwatson.omnigame.Lighting.recursiveLight;
import com.jwatson.omnigame.InventoryObjects.Chest;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.Tombstone;
import com.jwatson.omnigame.InventoryObjects.Torch;



public class Bob extends ClickListener {
	static final int IDLE = 0;
	static final int RUN = 1;
	static final int JUMP = 2;
	static final int SPAWN = 3;
	static final int DYING = 4;
	static final int DEAD = 5;
	static final int CLIMB = 6;
	static final int LEFT = -1;
	static final int RIGHT = 1;
	public static float ACCELERATION = 20f;
	static final float JUMP_VELOCITY = 7f;
	public static final float GRAVITY = 10.0f;
	static final float MAX_VEL = 5f;
	static final float DAMP = 0.75f;
	
	float Light_time_delay = 0.08f;
	
	
	int ControlScheme;
	
	
	boolean attack_queue;
	boolean screen_attack;
	
	int AnimationStyle;
	public InvObject UsedItem;
	
	int LastDirection;
	boolean firstPressed;
	boolean deadFlag;
	public int ID;
	
	public int touchPointer = -1;
	
	public int firstUsed;
	
	boolean justMoved;
	
	public long LastUsed;
	public long lastUpdated;
	
	Vector2 ignore_collision;
	boolean isAI;
	
	boolean isFirstTouch = true;
	boolean pressFlag;
	boolean touchFlag;
	boolean isMoving;
	boolean ForceVelocity = false;
	
	boolean needsupdate;
	
	public InvHookObject Grabber;
	public boolean Grabbed;
	public boolean Swinging;
	public boolean isSwimming;
	public Vector2 Grabpos;
	public Vector2 LastPos;
	public Vector2 newPos = new Vector2();
	Vector2 grabpos2 = new Vector2();
	public float Grablen;
	
	float acel;
	public float omega = 0;
	public float E;
	public float alpha = 0;
	public float Xspeed;
	public float Yspeed;
	
	OmniInput input = new OmniInput();
	
	
	byte[] tiles;
	int LastX = -1;
	int LastY = -1;
	
	boolean isMining;
	boolean isBuilding;
	
	public static Bob CurrentBob;
	
	public Inventory inventory;
	InvObject HeldItem;
	
	//STATS
	public int GetATK() { if(inventory.BagItem[inventory.currSelected][1] <= 0) return 10; else return 10+inventory.Items[inventory.BagItem[inventory.currSelected][0]].ATK; }
	public float MaxHP = 60;
	public float HP = 60;

	public Vector2 pos = new Vector2();
	Vector2 accel = new Vector2();
	public Vector2 vel = new Vector2();
	public Rectangle bounds = new Rectangle();

	int state = SPAWN;
	float stateTime = 0;
	float lightTime = 0;
	int dir = LEFT;
	Map map;
	boolean grounded = false;
	private boolean Tapping;

	ByteBuffer test;
	boolean isClimbing;
	Vector2 lastDamaged = new Vector2();
	
	float ambient_light_update;
	ClickListener jumpListener;
	private Actor gamescreen;
	private Actor jump_button;
	private Actor attack_button;
	
	public Bob (float x, float y, Inventory inv) {
		pos.x = x;
		pos.y = y;
		inventory = inv;
		bounds.width = 0.6f;
		bounds.height = 0.8f;
		bounds.x = pos.x + 0.2f;
		bounds.y = pos.y;
		state = SPAWN;
		stateTime = 0;
		if(Bob.CurrentBob != null) Bob.CurrentBob = null;
		Bob.CurrentBob = this;
		test = ByteBuffer.allocate(10);
		test.put(5, (byte)3);
		//alpha = (float) (4 * MathUtils.atan2(1, 1) / 2.1);
		E = (float) (1 - Math.cos(alpha));
		//Gdx.input.setInputProcessor(input);
		LastPos = new Vector2();
		//craftingManager = new CraftingManager(this);
		ignore_collision = new Vector2();
		

		//gamescreen.setOrigin(240, 160);
		
//		jump_button = new Actor();
//		jump_button.setBounds(398,0,90,60);
//		jump_button.addListener(new jumpListener());
//		
//		
//		attack_button = new Actor();
//		attack_button.setBounds(398,64,90,60);
//		attack_button.addListener(new attackListener());
		

		
//		OnScreenController.Controller.stage.addActor(jump_button);
//		OnScreenController.Controller.stage.addActor(attack_button);

		OnScreenController.Controller.gamescreen.addListener(this);
		

		
	}
	
	public Bob(float x, float y) {
		pos.x = x;
		pos.y = y;
		inventory = new Inventory(true, this);
		bounds.width = 0.6f;
		bounds.height = 0.8f;
		bounds.x = pos.x + 0.2f;
		bounds.y = pos.y;
		state = SPAWN;
		stateTime = 0;

		//alpha = (float) (4 * MathUtils.atan2(1, 1) / 2.1);
		E = (float) (1 - Math.cos(alpha));
		
		LastPos = new Vector2();
		
		isAI = true;
	}
	
	
	void bobJump() {
		if(Inventory.CurrentInventory.BagActive)
			return;
		
		if(Grabbed) {
			Grablen -= Gdx.graphics.getDeltaTime()* 1.5f;
			if(Grablen < 0)
				Grablen = 0;
		}
		
		if(grounded || isSwimming || isFlying) {
			if(!isSwimming && !Swinging)
		SoundManager.PlaySound("playerJump", 3);
		state = JUMP;
		if(!isSwimming)
		vel.y = JUMP_VELOCITY;
		else
			vel.y = JUMP_VELOCITY*0.8f;
		grounded = false;
		
		
		// TODO Auto-generated method stub
		
	}
	}
	
	
	
	class jumpListener extends ClickListener {
		public jumpListener() {
			
		}
		
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			
			

			
			return super.touchDown(event, x, y, pointer, button);
	}
	}

	class attackListener extends ClickListener {
		public attackListener() {
			
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			// TODO Auto-generated method stub
			if(Inventory.CurrentInventory.BagActive)
				return false;
			attack_queue = true;
			
			
			return super.touchDown(event, x, y, pointer, button);
		}
		
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			// TODO Auto-generated method stub
			attack_queue = false;
			super.touchUp(event, x, y, pointer, button);
		}
	}

	boolean moveDownFlag;
	boolean moveUpFlag;
	
	float HitTimer;
	float DamagedTimer;
	boolean holdingTorch;
	Vector3 culling = new Vector3();
	

	
	
	float TreeCheckTimer;
	boolean TreeShakeFlag;
	public void update (float deltaTime) {
		
//		if(Gdx.input.justTouched()) {
//			if(Gdx.input.isKeyPressed(Keys.F3))
//				disableInvGFX = !disableInvGFX;
//				inventory.HPText1.setVisible(false);
//				inventory.HPText2.setVisible(false);
//				inventory.HPText3.setVisible(false);
//		}
		
		if(state == DYING || state == DEAD) {
			if(!deadFlag) {
				World.CurrentWorld.SaveCharacter(MapRenderer.CurrentRenderer.char_slot);
				deadFlag = true;
			}
			Inventory.CurrentInventory.BagActive = false;
		}
		
		if(state == DEAD) {
			
			if(Gdx.input.justTouched()) {
				Vector2 SpawnPos = MapRenderer.CurrentRenderer.SpawnPos;
				World.CurrentWorld.SpawnBob((int)SpawnPos.x, (int)SpawnPos.y);
			}
			
			
			return;
		}
		
		if((int)(LastPos.x+0.5f) != (int)(pos.x+0.5f) || (int)(LastPos.y+0.5f) != (int)(pos.y+0.5f)) {
			
			Terrain.CurrentTerrain.light.spreadLight(null);
			


			
		}
		if(TreeCheckTimer > 0.2f) {
		int tile = Terrain.GetTile(pos.x+0.5f, pos.y+0.5f);
		if(tile>0)
		if(!TreeShakeFlag) {
		if(Item.Items[tile].type == InvObject.Type.WOOD) {
			TreeShakeFlag = true;

			Terrain.TerrainChunks[(int)((pos.x+0.5f)/Terrain.chunkWidth) + (int)((pos.y+0.5f)/Terrain.chunkHeight) * Terrain.Width].ShakeTree((int)((pos.x+0.5f)%Terrain.chunkWidth), (int)((pos.y+0.5f)%Terrain.chunkHeight));
		}

		}


		TreeCheckTimer = 0;
		}
		if(TreeShakeFlag)
		{
			int tile = Terrain.GetTile(pos.x+0.5f, pos.y+0.5f);
			
			if(tile > 0) {
			if(Item.Items[tile].type != InvObject.Type.WOOD)
				TreeShakeFlag = false;
			}
			else
				TreeShakeFlag = false;
		}

		
		LastPos.set(pos.x,pos.y);
		
		int x1 = (int)(pos.x+0.5f)/Terrain.CurrentTerrain.chunkWidth;
		int x3 = (int)(pos.x+0.5f)%Terrain.CurrentTerrain.chunkWidth;
		
		int y1 = (int)(pos.y+0.5f)/Terrain.CurrentTerrain.chunkHeight;
		int y3 = (int)(pos.y+0.6f)%Terrain.CurrentTerrain.chunkHeight;
		

//		for(AbstractBounds<Bob> b2 : World.CurrentWorld.BoundsToCheck) {
//			if(b2.target == getClass())
//				if(b2.overlaps(bounds)) {
//					b2.OnCollision(this);
//					
//				}
//		}
		
		if(HP <= 0 && state != DYING) {
			HP = 0;
			state = Bob.DYING;
			stateTime = 0;
			vel.x = 0;
			vel.y = 0;
			TreeCheckTimer = 0;
		}
		
		if(mineTime > 0.4f)
			if(lastDamaged.x != -1)
				lastDamaged.set(-1, -1);
		
		
		HitTimer += deltaTime;
		DamagedTimer += deltaTime;
		mineTime += deltaTime;
		TreeCheckTimer += deltaTime;
		
		if(Grabber != null)
			if(Grabber.isShooting)
				UpdateGrapple(deltaTime);
		
		if(!isAI) {
			if(DamagedTimer > 0.6f)
			processKeys();
			inventory.update(deltaTime);
			//craftingManager.update(deltaTime);
		}
		else {
			if(World.CurrentWorld.server == null) {
				pos = pos.lerp(newPos, 0.3f);
				
		

			//PosList.clear();

			}
			if(state == Bob.CLIMB) {
				return;
			}
			
			if(isMoving) {
				accel.x = dir * (Bob.ACCELERATION);
			}
			else {
				accel.x = 0;
			}
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
				float dist = Grabpos.dst(Bob.CurrentBob.pos.x+0.5f, Bob.CurrentBob.pos.y+0.5f);
				if(dist < 1)
					dist = 1;
				float rads = (float) Math.atan2(Y3, X3);
				acel = -0.2f * MathUtils.sin((float) (rads + Math.toRadians(90))) * deltaTime;
				omega += acel;
				omega *= 0.98f;
				alpha += omega;
				
				//Gdx.app.debug("", ""+omega);
				omega +=(accel.x/dist) * 0.01f;
				
				
				
				
				//Bob.CurrentBob.vel.x += -(x2 - newX);
				//Bob.CurrentBob.vel.y += -(y2 - newY);
				Vector2 newvec = new Vector2(-(x2 - newX), -(y2 - newY));

				newvec.mul(deltaTime);
				grabpos2.x = Grabpos.x + (float) (Grablen * Math.cos(alpha));
				grabpos2.y = Grabpos.y + (float) (Grablen * Math.sin(alpha));
			
				Xspeed = grabpos2.x - bounds.x;
				Yspeed = grabpos2.y - bounds.y;
				vel.x = Xspeed * 100;
				vel.y = Yspeed * 100;
				//Bob.CurrentBob.vel.y =  M

			//}
		}
		if(DamagedTimer < 0.6f && !grounded)
			accel.x = 1;
			ACCELERATION = 20f;
		
		if(!ForceVelocity)
		if (accel.x == 0 ) vel.x *= DAMP;
		if (vel.x > MAX_VEL && !Swinging) vel.x = MAX_VEL;
		if (vel.x < -MAX_VEL && !Swinging) vel.x = -MAX_VEL;
		
		if(Gdx.input.isKeyPressed(Keys.F9))
			vel.x = 50 * dir;
		
		vel.mul(deltaTime);
		
		
		if(grounded && ForceVelocity)
			ForceVelocity = false;
		
		if(Terrain.CurrentTerrain.GetLiquid(x1, y1, x3*2, y3*2) > 5) {
			isSwimming = true;
			vel.mul(0.9f);
		}
		else isSwimming = false;
		
		if(this.equals(Bob.CurrentBob) || World.CurrentWorld.server != null)
		tryMove();
		vel.mul(1.0f / deltaTime);
		
		CheckWorldCollision();
		
		if (state == SPAWN) {
			if (stateTime > 0.4f) {
				state = IDLE;
			}
			Inventory.CurrentInventory.dmgTxts.clear();
		}

		if (state == DYING) {
			if (stateTime > 0.8f) {
				state = DEAD;
				WorldObj tombstone = new WorldObj(World.CurrentWorld, pos.x, pos.y, 1, 1, Tombstone.ID, 1);
				tombstone.collision = false;
				World.CurrentWorld.SpawnedObjects.add(0, tombstone);
			}
		}
		
		if(!MessageBox.MessageBoxActive)
		stateTime += deltaTime;
		AnimTime += deltaTime;
		if(inventory.BagItem[inventory.currSelected][0] == Torch.ID) {
		if(lightTime == 0)
			Terrain.CurrentTerrain.light.spreadLight(null);
		lightTime += deltaTime;
		holdingTorch = true;
		}
		else
			holdingTorch = false;
		
		
		if(lightTime > 0)
		if(inventory.BagItem[inventory.currSelected][0] != Torch.ID) {
			Terrain.CurrentTerrain.light.spreadLight(null);
			Gdx.app.debug("WTF", "BRO");
			lightTime = 0;
		}
	}
	Rectangle AttackBounds = new Rectangle();
	private void CheckWorldCollision() {
		// TODO Auto-generated method stub
		for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
			if(bounds.overlaps(obj.bounds))
				obj.OnCollision(this);
			if(MapRenderer.CurrentRenderer.ItemAnimation != null) {
				if(obj.ai != null) {
					if(dir == LEFT)
					AttackBounds.set(pos.x+(dir*1.35f)+0.5f,pos.y,1.35f,1);
					else
						AttackBounds.set(pos.x+0.5f,pos.y,1.35f,1);
					if(obj.healthBarTimer > 0.4f)
				if(AttackBounds.overlaps(obj.bounds)) {
					if(!Terrain.CurrentTerrain.isSolid((int)(pos.x+0.5f+(0.3f*dir)), (int)(pos.y+0.5f)))
					obj.ai.onDamaged(this, UsedItem, GetATK());
					HitTimer = 0;
				}
				}
				if(obj.damageable) {
					if(AttackBounds.overlaps(obj.bounds)) {
						obj.onDamaged(this, UsedItem, GetATK());
					}
				}
			}
		}
	}


	long sendDelay;
	private boolean isFlying;
	private boolean useFlag;
	Rectangle touchBounds = new Rectangle();
	Vector3 projection = new Vector3();;
	Vector2 target = new Vector2();;
	private void processKeys () {
		
		
		

	
		if (state == SPAWN || state == DYING || isAI || inventory.BagActive || paused) return;

		
		
		
	
		
		
		
		
		//boolean jumpButton = (x0 > 380 && x0 < 480 && y0 < 100);
		
		
		float useX = 0;
		float useY = 0;
	
//		x0 = (Gdx.input.getX(0) / (float)Gdx.graphics.getWidth()) * 480;
//		y0 = ((Gdx.graphics.getHeight()-Gdx.input.getY(0)) / (float)Gdx.graphics.getHeight()) * 320;
//		
//		x1 = (Gdx.input.getX(1) / (float)Gdx.graphics.getWidth()) * 480;
//		y1 = ((Gdx.graphics.getHeight()-Gdx.input.getY(1)) / (float)Gdx.graphics.getHeight()) * 320;
		
//		boolean onUse1 =  (x0 > 100 && x0 < 400 && y0 > 70);
//		boolean onUse2 =  (x1 > 100 && x1 < 400 && y1 > 70);
//		if(Gdx.input.isKeyPressed(Keys.F1) && !pressFlag || Gdx.input.isKeyPressed(Keys.MENU) && !pressFlag) {
//			GameClient client = World.CurrentWorld.client;
//			client.SetActive(!client.IsActive());
//			pressFlag = true;
//		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)||Gdx.input.isKeyPressed(Keys.W) ) {
			if(!Gdx.input.isKeyPressed(Keys.S))
			if(Terrain.GetTile(pos.x+0.5f, pos.y+0.5f) != Terrain.CurrentTerrain.light.LadderID) {
				
				if(Grabbed) {
					Grablen -= Gdx.graphics.getDeltaTime()* 1.5f;
					if(Grablen < 0)
						Grablen = 0;
				}
				
				if(grounded || isSwimming || isFlying) {
					if(!isSwimming && !Swinging)
				SoundManager.PlaySound("playerJump", 3);
				state = JUMP;
				if(!isSwimming)
				vel.y = JUMP_VELOCITY;
				else
					vel.y = JUMP_VELOCITY*0.8f;
				grounded = false;
//				if(World.CurrentWorld.client.serverBob != null) {
//					if(World.CurrentWorld.client.serverBob.Spawned) {
//						World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_JUMP);
//					}
//				}
				}
			}
			else {
				if(!moveUpFlag) {
//					if(World.CurrentWorld.client.serverBob != null) {
//						if(World.CurrentWorld.client.serverBob.Spawned) {
//							World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_JUMP);
//						}
//					}
					moveUpFlag = true;
				}
				
				
				vel.y = 5;
				//if(System.currentTimeMillis() - sendDelay > 50) {

				//sendDelay = System.currentTimeMillis();
				//}
			}
			if(Gdx.input.isKeyPressed(Keys.S)) {
				

				
				if(grounded) {
					if(Terrain.GetTile(pos.x+0.5f, pos.y+0.5f-1f) > 0)
				if(Item.Items[Terrain.GetTile(pos.x+0.5f, pos.y+0.5f-1f)].type == InvObject.Type.PLATFORM) {
					ignore_collision.set(pos.x+0.5f, pos.y+0.5f-1f);
					grounded = false;
				}
					//Gdx.app.debug("WAT", "USAI");
				}
			}
		}
		if(!Gdx.input.isKeyPressed(Keys.W) && moveUpFlag) {
//			if(World.CurrentWorld.client.serverBob != null)
//					World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_STOP);
			moveUpFlag = false;
		}
		if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN) || OnScreenController.down) {
			
			//Gdx.app.debug("", "WAT");
			if(Grabbed) {
				Grablen += Gdx.graphics.getDeltaTime() * 1.5f;
				if(Grablen > Grabber.MaxLen)
					Grablen = Grabber.MaxLen;
			}
			
			if(Terrain.CurrentTerrain.GetTile(pos.x+0.5f, pos.y+0.5f) == Terrain.CurrentTerrain.light.LadderID || isFlying) {
				
//				if(!moveDownFlag) {
//					if(World.CurrentWorld.client.serverBob != null) {
//						if(World.CurrentWorld.client.serverBob.Spawned) {
//							World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_MOVEDOWN);
//						}
//					}
			
				
				vel.y = -5;
//				moveDownFlag = true;
//				}

			}
			
		}
//		else if(!Gdx.input.isKeyPressed(Keys.S)) {
//			if(World.CurrentWorld.client.serverBob != null) {
//					World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_STOP);
//			}
//			moveDownFlag = false;
//					
//		}
		
//		if(!OnScreenController.down &&!Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Keys.DOWN) && !Gdx.input.isKeyPressed(Keys.S) && !Gdx.input.isKeyPressed(Keys.W) && !inventory.jumpbutton_bounds.contains(x0, y0) && Terrain.CurrentTerrain.GetTile(pos.x+0.5f, pos.y+0.5f) == Terrain.CurrentTerrain.light.LadderID) {
//			vel.y = 0;
//		}
//		
//		if(!Gdx.input.isKeyPressed(Keys.W) && !Gdx.input.isKeyPressed(Keys.S) && isFlying) {
//			vel.y = 0;
//		}
		
//		if(Gdx.input.isKeyPressed(Keys.ENTER) && !pressFlag) {
//			ChatBox chatBox = World.CurrentWorld.chatBox;
//			String input = chatBox.GetInputText();
//			if(!input.equals("")) {
//				GameClient client = World.CurrentWorld.client;
//				if(client.connection !=null)
//				if(client.connection.isConnected()) {
//					client.serverBob.Chat(input);
//				}
//				chatBox.ClearInputText();
//			}
//			else
//			chatBox.SetActive(!chatBox.isActive);
//			
//			
//			pressFlag = true;
//		}
//		
//		if(!Gdx.input.isKeyPressed(Keys.F1) && !Gdx.input.isKeyPressed(Keys.ENTER)) {
//			pressFlag = false;
//		}
//		
		
		//Debug stuf
		if(attack_queue && !inventory.BagActive) {
	
			
			if(MapRenderer.CurrentRenderer.ItemAnimation == null) {
				
				if(!screen_attack) {
				Vector3 proj = new Vector3();
				proj.x = pos.x + 0.5f + dir*1.5f;
				

					
		
				proj.y = pos.y + 0.5f;
				proj.z = 0;
				
				if(OnScreenController.up) {
					proj.x = pos.x + 0.5f;
					proj.y = pos.y + 0.5f + 1;
				}
				else if(OnScreenController.down) {
					proj.x = pos.x + 0.5f;
					proj.y = pos.y + 0.5f - 1;
				}
				else if(OnScreenController.up_right) {
					proj.x = pos.x + 0.5f + 1;
					proj.y = pos.y + 0.5f + 1;
				}
				else if(OnScreenController.down_right) {
					proj.x = pos.x + 0.5f + 1;
					proj.y = pos.y + 0.5f - 1;
				}
				else if(OnScreenController.up_left) {
					proj.x = pos.x + 0.5f - 1;
					proj.y = pos.y + 0.5f + 1;
				}
				else if(OnScreenController.down_left) {
					proj.x = pos.x + 0.5f - 1;
					proj.y = pos.y + 0.5f - 1;
				}
				
				MapRenderer.CurrentCam.project(proj);
				target.set(proj.x,proj.y);
				}
			
				DoMining(target.x,target.y);
				

					}
			
		}
		else {
			attack_queue = false;
		}

		
		
		if(!inventory.BagActive) {
		
			
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT) || OnScreenController.left) {
	

			
			if (state != JUMP) state = RUN;
			dir = LEFT;
			accel.x = ACCELERATION * dir;

//			if(World.CurrentWorld.client.serverBob != null)
			if(LastDirection != LEFT || !firstPressed ) {
//				if(World.CurrentWorld.client.serverBob.Spawned) {
				firstPressed = true;
				if(Swinging)
					Swinging = false;
				lastUpdated = System.currentTimeMillis();
//				World.CurrentWorld.MovedTimer = lastUpdated;
//				World.CurrentWorld.client.serverBob.direction = LEFT;
//				World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_MOVELEFT);
//				World.CurrentWorld.client.serverBob.isMoving = true;
				firstPressed = true;
//				}
			}
			
			LastDirection = dir;
			//Gdx.app.debug("WTF", ""+(System.currentTimeMillis() - lastUpdated));

		} else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT) || OnScreenController.right) {
			
			//Gdx.app.debug(""+(int)(pos.x/Terrain.CurrentTerrain.chunkWidth), ""+(int)(pos.y/Terrain.CurrentTerrain.chunkHeight));

			
			if (state != JUMP) state = RUN;
			dir = RIGHT;
			accel.x = ACCELERATION * dir;


//			if(World.CurrentWorld.client.serverBob != null)
			if(LastDirection != RIGHT || !firstPressed ) {
//				if(World.CurrentWorld.client.serverBob.Spawned) {
					if(Swinging)
						Swinging = false;
				firstPressed = true;
//				World.CurrentWorld.client.serverBob.direction = RIGHT;
//				World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_MOVERIGHT);
//				World.CurrentWorld.client.serverBob.isMoving = true;
				}
//			}
			
			LastDirection = dir;
		} else {
			if(!OnScreenController.draw_joystick) {
			if (state != JUMP) state = IDLE;
			
			accel.x = 0;
			firstPressed = false;
			}
		}

		

		
		if(!Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) ) {
			if(null != null)
			if(World.CurrentWorld.client.serverBob.isMoving) {
//				World.CurrentWorld.client.serverBob.SendUpdate(AIBob.PACKET_STOP);
				World.CurrentWorld.client.serverBob.isMoving = false;
				//World.CurrentWorld.client.serverBob.udpthread.interrupt();
			}
		}
		
		
		}
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
			ACCELERATION = 22;
		}
		
		if(!Gdx.input.isTouched())
		{
			isMining = false;
			isBuilding = false;
			isFirstTouch = true;
			touchFlag = false;
		}
		else	
			isFirstTouch = false;
		
		
		if(Gdx.input.justTouched()) {
			
			if(stateTime > 0.1f) {
			
			Ray projectCoordinates =  MapRenderer.CurrentCam.getPickRay(Gdx.input.getX(0), Gdx.input.getY(0));
			float x2 = (projectCoordinates.origin.x);
			float y2 = (projectCoordinates.origin.y);
			
			
			int x = (int) (x2/Terrain.CurrentTerrain.chunkWidth);
			int y = (int) (y2/Terrain.CurrentTerrain.chunkHeight);
			int x3 = (int) (x2%Terrain.CurrentTerrain.chunkWidth);
			int y3 = (int) (y2%Terrain.CurrentTerrain.chunkHeight);
			
			TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
			//Gdx.app.debug("", ""+x2+ " " +y2 + " " + Terrain.CurrentTerrain.light.GetBrightness((int)x2, (int)y2));
			if(ch != null) {
				touchBounds.set((int)x2,(int)y2,1,1);
				int id = ch.TerrainMap[x3+y3*ch.Width];
				if(id > 0) {
				InvObject obj = Inventory.CurrentInventory.Items[id];
				if(obj.touchable)
					if(pos.dst(x2, y2) <= obj.Distance)
					obj.onTouch(x, y, x3, y3);
				}
			
				if(Gdx.input.isKeyPressed(Keys.F2)) {
				ch.TerrainMap[x3+y3*ch.Width] = (byte) Chest.ID;
				int posId = (int)x2 + (int)y2 * ch.Width * ch.parent.Width;
				Inventory.CurrentInventory.Chests.put(""+posId, "PROP_BreakablePot 1 MSC_Coin_Silver 30 MAT_Iron 20 MAT_Tree_Brown 30 MAT_Stone 30 TUR_BallistaTurret 10");
				
				}
				
			}
			
			for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
				
				if(obj.Touchable)
					if(touchBounds.overlaps(obj.bounds))
						obj.onTouch();
					
				
			}
			
			}
			
			
		}
	}

	public int getDEF() {
		int DEF = 0;
		for(int i=0; i<4; i++) {
			if(Inventory.CurrentInventory.Armor[i][1] > 0) {
				DEF += Item.Items[Inventory.CurrentInventory.Armor[i][0]].DEF;
			}
			
		}
		return DEF;
		
	}
	
	
	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

	private void tryMove () {
		
		
		if(Grabbed)
			bounds.x = grabpos2.x;
		else
			bounds.x += vel.x;
		
		fetchCollidableRects2(bounds.x,bounds.y);
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			
			if (bounds.overlaps(rect) &&  Terrain.CurrentTerrain.isSolid((int)(r[i].x+0.5f), (int)(r[i].y+0.5f))) {
//				if (vel.x < 0)
//					bounds.x = rect.x + rect.width + 0.02f;
//				else
//					bounds.x = rect.x - bounds.width - 0.02f;
				
				bounds.x = pos.x+0.1999f;
				
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
		fetchCollidableRects2(bounds.x,bounds.y);
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)&& Terrain.CurrentTerrain.isSolid((int)(r[i].x+0.5f), (int)(r[i].y+0.5f))) {
				if (vel.y < 0) {
					bounds.y = pos.y;
					grounded = true;
					
				} 
				if(vel.y > 0) {
					bounds.y = pos.y;
				}
				if (state != DYING && state != SPAWN) state = Math.abs(accel.x) > 0.01f ? RUN : IDLE;	
				Swinging = false;
				//bounds.y -=   rect.y - bounds.y;
				if(Grabbed) {
					omega = 0;
				}
				
				vel.y = 0;
			}
		}

		pos.x = bounds.x - 0.1999f;
		pos.y = bounds.y;
		if(BackgroundManager.BGManager.bob_last_pos == null)
			BackgroundManager.BGManager.bob_last_pos = new Vector2(pos);
		else {
			BackgroundManager.BGManager.deltaX = BackgroundManager.BGManager.bob_last_pos.x - pos.x;
			BackgroundManager.BGManager.bob_last_pos.set(pos);
		}
		
		
		
		if(ignore_collision.x > 0 && ignore_collision.y > 0) {
			testBounds.set((int)ignore_collision.x,(int)ignore_collision.y,1,1);
			if(!bounds.overlaps(testBounds))
				ignore_collision.set(0,0);
			
		}
	}
	
Rectangle testBounds = new Rectangle();

void fetchCollidableRects2old() {
		
		int LadderID = Inventory.CurrentInventory.GetItemID("DEP_Ladder");
		
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		int bobX = (int)bounds.x;
		int bobY = (int)bounds.y;
		int bobX2 = (int)((bounds.x)%cWidth);
		int bobY2 = (int)((bounds.y)%cHeight);
		
		
		
		
		tiles = Terrain.CurrentTerrain.GetChunkByID((int)(bobX/cWidth),(int)(bobY/cHeight)).TerrainMap;
		
		//Gdx.app.debug(""+LastX + " " +LastY, ""+(bobX/cWidth)+" "+(bobY/cHeight));
		//long nano = TimeUtils.nanoTime();
		
		if(bobX2 + (bobY2*cWidth) < cWidth*cHeight)
		{
			if(tiles[bobX2 + (bobY2*cWidth)] > 0 && tiles[bobX2 + (bobY2*cWidth)] != LadderID)
			r[0].set(bobX,bobY,1,1);
			else
				r[0].set(-1,-1,1,1);
		}
		else
		{
			if(Terrain.CurrentTerrain.GetTile(bobX, bobY) > 0 && Terrain.CurrentTerrain.GetTile(bobX, bobY) != LadderID) {
			}
		}
		
		if((bobX2+1) < cWidth && (bobY2) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[(bobX2+1) + (bobY2*cWidth)] > 0 && tiles[(bobX2+1) + (bobY2*cWidth)]  != LadderID)
		r[1].set(bobX+1,bobY,1,1);
		else
			r[1].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX+1, bobY) > 0 && Terrain.CurrentTerrain.GetTile(bobX+1, bobY)  != LadderID)
			r[1].set(bobX+1,bobY,1,1);
		
		if((bobX2+1) < cWidth && (bobY2+1) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[(bobX2+1) + ((bobY2+1)*cWidth)] > 0 && tiles[(bobX2+1) + ((bobY2+1)*cWidth)] != LadderID)
		r[2].set(bobX+1,bobY+1,1,1);
		else
			r[2].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX+1, bobY+1) > 0 && Terrain.CurrentTerrain.GetTile(bobX+1, bobY+1) != LadderID)
			r[2].set(bobX+1,bobY+1,1,1);
		
		if((bobX2) < cWidth && (bobY2+1) < cHeight && bobX2 > 0 && bobY2 > 0)
		if(tiles[bobX2 + ((bobY2+1)*cWidth)] > 0 && tiles[bobX2 + ((bobY2+1)*cWidth)] != LadderID)
		r[3].set(bobX,bobY+1,1,1);
		else
			r[3].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(bobX, bobY+1) > 0 && Terrain.CurrentTerrain.GetTile(bobX, bobY+1) != LadderID)
			r[3].set(bobX,bobY+1,1,1);
		//Gdx.app.debug("TOOKNANO", ""+bobX+ " " +bobY);	
		boolean changed;
		
		LastX = bobX/cWidth;
		LastY = bobY/cHeight;

	}
	
	void fetchCollidableRects2(float x2, float y2, int ...collidewidth) {
		
		
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		int bobX = (int)x2;
		int bobY = (int)y2;
		int x = bobX/cWidth;
		int y = bobY/cHeight;
		int bobX2 = (int)((x2)%cWidth);
		int bobY2 = (int)((y2)%cHeight);
		
		boolean flag = false;
		flag = false;
		if(Terrain.GetTile(bobX, bobY) > 0) {
			for(Integer i : collidewidth) {
				if(i==Item.Items[Terrain.GetTile(bobX, bobY)].InvObjID)
					flag = true;
			}
		}
		
			if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2, bobY2) || flag)  {
			if(Item.Items[Terrain.GetTile(bobX, bobY)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[0].set(bobX+0.40625f,bobY,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX, bobY)].type == InvObject.Type.PLATFORM) {
				if(vel.y < 0 && pos.y > bobY+0.48f && (int)ignore_collision.y != bobY) {
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

			flag = false;
			if(Terrain.GetTile(bobX+1, bobY) > 0) {
				for(Integer i : collidewidth) {
					if(i==Item.Items[Terrain.GetTile(bobX+1, bobY)].InvObjID)
						flag = true;
				}
			}
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2+1, bobY2)  || flag) {
			if(Item.Items[Terrain.GetTile(bobX+1, bobY)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[1].set(bobX+1+0.40625f,bobY,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX+1, bobY)].type == InvObject.Type.PLATFORM) {
				if(vel.y < 0 && pos.y > bobY+0.48f && (int)ignore_collision.y != (int)(bobY+0.48f))
				r[1].set(bobX+1,bobY+0.48f,1,0.1f);
				else
					r[1].set(-1,-1,1,1);
			}
			else
		r[1].set(bobX+1,bobY,1,1);
		}
		else
			r[1].set(-1,-1,1,1);
		
		flag = false;
		if(Terrain.GetTile(bobX+1, bobY+1) > 0) {
			for(Integer i : collidewidth) {
				if(i==Item.Items[Terrain.GetTile(bobX+1, bobY+1)].InvObjID)
					flag = true;
			}
		}
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2+1, bobY2+1) || flag) {
			if(Item.Items[Terrain.GetTile(bobX+1, bobY+1)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[2].set(bobX+1+0.40625f,bobY+1,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX+1, bobY+1)].type == InvObject.Type.PLATFORM) {
				if(vel.y < 0 && pos.y > bobY+0.48f + 1 && (int)ignore_collision.y != (int)(bobY+1+0.48f))
				r[2].set(bobX+1,bobY+1+0.48f,1,0.1f);
				else
					r[2].set(-1,-1,1,1);
			}
			else
		r[2].set(bobX+1,bobY+1,1,1);
		}
		else
			r[2].set(-1,-1,1,1);

		flag = false;
		if(Terrain.GetTile(bobX, bobY+1) > 0) {
			for(Integer i : collidewidth) {
				if(i==Item.Items[Terrain.GetTile(bobX, bobY+1)].InvObjID)
					flag = true;
			}
		}
		if(Terrain.CurrentTerrain.isCollidable(x, y, bobX2, bobY2+1) || flag) {
			if(Item.Items[Terrain.GetTile(bobX, bobY+1)].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				r[3].set(bobX+0.40625f,bobY+1,0.2f,1);
			}
			else if(Item.Items[Terrain.GetTile(bobX, bobY+1)].type == InvObject.Type.PLATFORM) {
				if(vel.y < 0 && pos.y > bobY+0.48f+1 && (int)ignore_collision.y != (int)(bobY+1+0.48f))
				r[3].set(bobX,bobY+1+0.48f,1,0.1f);
				else
					r[3].set(-1,-1,1,1);
			}
			else
		r[3].set(bobX,bobY+1,1,1);
		}
		else
			r[3].set(-1,-1,1,1);

		boolean changed;
		
		LastX = bobX/cWidth;
		LastY = bobY/cHeight;

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
		int p2x = (int)((bounds.x - (idX*cWidth)) + bounds.width);
		int p2y = (int)Math.floor(bounds.y - (idY*cHeight));
		int p3x = (int)((bounds.x - (idX*cWidth)) + bounds.width);
		int p3y = (int)((bounds.y - (idY*cHeight)) + bounds.height);
		int p4x = (int)(bounds.x - (idX*cWidth));
		int p4y = (int)((bounds.y - (idY*cHeight)) + bounds.height);
		
		

		
		byte[] tiles = Terrain.CurrentTerrain.GetCollisionData(idX,idY);

		int tile1 = tiles[p1x + (p1y*cWidth)];
		int tile2 = tiles[p2x + (p2y*cWidth)];
		int tile3 = 0;
		int tile4 = 0;

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
			//Gdx.app.debug("NOPE",""+p1x+" "+p1y + " and " + p2x + " " + p2y);
			r[2].set((idX*cWidth)+p3x, (idY*cHeight)+p3y, 1, 1);
		}
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 > 0)
			r[3].set((idX*cWidth)+p4x, (idY*cHeight)+p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);
	}
	
	
	Vector3 prog2 = new Vector3();
	int ItemAnimdir;
	float AnimTime;
	float mineTime;
	private float runAnimTime;
	
	void DoMining(float x, float y) {
		//Quick hack for now. update- turning into less of a quick hack
		World world = World.CurrentWorld;
		
		MapRenderer.CurrentRenderer.ItemAnimation = null;
			mineTime = 0;
			lastDamaged.set(x,y);
		
			InvObject item = null;
			if(inventory.BagItem[inventory.currSelected][1] > 0)
				item = inventory.Items[inventory.BagItem[inventory.currSelected][0]];
			int delay = 100;
			if(item != null)
				delay = item.Delay;
		
//			if(TimeUtils.millis() - LastUsed > delay)
//			{
				if(inventory.BagItem[inventory.currSelected][1] > 0)
				{				
					
					prog2.x = pos.x+0.5f;
					prog2.y = pos.y+0.5f;
					MapRenderer.CurrentCam.project(prog2);
					
					
					
					Ray projectCoordinates =  MapRenderer.CurrentCam.getPickRay(Gdx.input.getX(0), Gdx.input.getY(0));
					float x2 = (projectCoordinates.origin.x);
					float y2 = (projectCoordinates.origin.y);
					
					
					
					float angle = (float) Math.toDegrees(MathUtils.atan2(y-prog2.y, x-prog2.x));
					
					
					
					if(World.CurrentWorld.isClient) {

						World.CurrentWorld.client.serverBob.remoteOnUse((int)x2,(int)y2,angle);
					}
					
					if(World.CurrentWorld.server != null) {
						onServerUse(item,x2,y2,angle);
					}
					if(item.HoldTouch) {
					item.OnUse(this,x2, y2,angle);
					}
					else {
						if(Gdx.input.justTouched())
							item.OnUse(this,x2, y2,angle);
					}
					
					
				}
//				LastUsed = TimeUtils.millis();
//			}
			
			
			

	}
	
	public void onServerUse(InvObject item, float x, float y, float angle) {
		// TODO Auto-generated method stub
		
		ByteBuffer tempbuf = ByteBuffer.allocate(64);
		tempbuf.put(ServerBob.PACKET_ONUSE);
		tempbuf.putInt(ID);
		tempbuf.putInt(item.InvObjID);
		if(ID > 0)
			
		tempbuf.putInt(firstUsed);
		else {
			if(Gdx.input.justTouched())
				tempbuf.putInt(1);
			else
				tempbuf.putInt(0);
		}
		
		
		tempbuf.putFloat(x);
		tempbuf.putFloat(y);
		tempbuf.putFloat(angle);
		tempbuf.flip();
		World.CurrentWorld.server.extraInfo.add(tempbuf);
		
	}
	
	public void unHook() {
		InvHookObject grabber = Grabber;
		
		grabber.Grabbed = false;
		Grabbed = false;
		grabber.grappleGfx.deleted = true;
		grabber.isUpdating = false;
		grabber.isShooting = false;
		grabber.len = 0;
	}
	Vector2 ItemPos = new Vector2();
	Vector2 ItemLerpPos = new Vector2();
	Rectangle WeaponBounds = new Rectangle();
	float totalAnimTime;
	public float moveX;
	public boolean paused;
	public boolean firstShot;
	public boolean disableInvGFX;
	
	void render(SpriteBatch batch) {

	}
	
//	void render (SpriteBatch batch) {
//		Animation anim = null;
//		if(state == DEAD)
//			return;
//		
//		if(ItemAnimation != null || holdingTorch) {
//			
//			if(dir == Bob.LEFT)
//				batch.draw(MapRenderer.CurrentRenderer.bobArmLeft, pos.x, pos.y,1,1);
//			else
//				batch.draw(MapRenderer.CurrentRenderer.bobArmRight, pos.x, pos.y,1,1);
//			
//			if(holdingTorch) {
//				batch.draw(Item.Items[Torch.ID].anim.getKeyFrame(stateTime,true), pos.x+(dir*0.5f), pos.y+0.3f,1,1);
//			}
//			
//		}
//		
//		boolean loop = true;
//		if (state == Bob.RUN) {
//			if (dir == Bob.LEFT)
//				anim = MapRenderer.CurrentRenderer.bobLeft;
//			else
//				anim = MapRenderer.CurrentRenderer.bobRight;
//		}
//		if (state == Bob.IDLE) {
//			if (dir == Bob.LEFT)
//				anim = MapRenderer.CurrentRenderer.bobIdleLeft;
//			else
//				anim = MapRenderer.CurrentRenderer.bobIdleRight;
//		}
//		if (state == Bob.JUMP) {
//			if (dir == Bob.LEFT)
//				anim = MapRenderer.CurrentRenderer.bobJumpLeft;
//			else
//				anim = MapRenderer.CurrentRenderer.bobJumpRight;
//		}
//		if (state == Bob.SPAWN) {
//			anim = MapRenderer.CurrentRenderer.spawn;
//			loop = false;
//		}
//		if (state == Bob.DYING) {
//			anim = MapRenderer.CurrentRenderer.dying;		
//			loop = false;
//		}
//		float a = (float)Terrain.CurrentTerrain.GetLightTile((int)(pos.x+0.5f), (int)(pos.y+0.5f));
//		
//		
//		if(Gdx.input.isKeyPressed(Keys.F3) && Gdx.input.justTouched() ) {
//			//Gdx.app.debug((int)(Bob.CurrentBob.pos.x % Terrain.CurrentTerrain.chunkWidth) + " " +(int)(Bob.CurrentBob.pos.y % Terrain.CurrentTerrain.chunkHeight), ""+(int)(Bob.CurrentBob.pos.x / Terrain.CurrentTerrain.chunkWidth) + " " +(int)(Bob.CurrentBob.pos.y / Terrain.CurrentTerrain.chunkHeight));
//			Inventory.CurrentInventory.quickBarActive = false;
//		}
//		a /= 15f;
//		a = 1 - a;
//
//		
//		a *= 255;
//		
//		if(a <= 0) {
//			a= 1;
//		}
//		if(a>=230f) {
//			a=230;
//		}
//		Color col = new Color(Color.WHITE);
//		col.a = (int)a;
//		batch.setColor(col);
//		
//		
//		batch.draw(anim.getKeyFrame(stateTime, loop), pos.x, pos.y, 1, 1);
//
//		
//		if(ItemAnimation != null) {
//			if(AnimationStyle == InvObject.ANIMATE_STYLE_ROTATE) {
//			if(AnimTime >= totalAnimTime) {
//				ItemAnimation = null;
//				return;
//			}
//			ItemAnimation.setPosition(pos.x+(0.50f*dir), pos.y+0.32f);
//			
//			ItemAnimation.draw(batch);
//			if(dir != ItemAnimdir) {
//				ItemAnimation.flip(true, false);
//				ItemAnimdir = dir;
//			}
//			
//			ItemAnimation.setRotation(-dir*90 * (AnimTime/totalAnimTime));
//			
//		}
//		if(AnimationStyle == InvObject.ANIMATE_STYLE_THRUST_ANGLE) {
//				
//				float cos = MathUtils.cosDeg((ItemAnimation.getRotation()-90))*AnimTime;
//				float sin = MathUtils.sinDeg((ItemAnimation.getRotation()-90))*AnimTime;
//				float newX = ((pos.x) - cos);
//				float newY = ((pos.y) - sin);
//				ItemPos.set(newX,newY);
//				if(ItemPos.dst(pos.x,(pos.y)) < 0.3f)
//				ItemAnimation.setPosition(newX, newY);
//				else
//					ItemAnimation.setPosition(((pos.x)-MathUtils.cosDeg((ItemAnimation.getRotation()-90))*0.3f),(pos.y)-MathUtils.sinDeg((ItemAnimation.getRotation()-90))*0.3f);
//				ItemAnimation.draw(batch);
//				AnimTime += Gdx.graphics.getDeltaTime()*6;
//				if(AnimTime >= 2)
//					ItemAnimation = null;
//			}
//		
//		if(AnimationStyle == InvObject.ANIMATE_STYLE_THRUST) {
//			
////			if(AnimTime < totalAnimTime/2)
////			ItemLerpPos.set(pos.x+(dir*0.5f),pos.y+0.1f);
////			else
////			ItemLerpPos.set(pos.x,pos.y+0.1f);	
////			
////			ItemPos.lerp(ItemLerpPos, 0.4f);
//			
//			ItemAnimation.setRotation(-90*dir);
//			float newX = pos.x + (AnimTime*dir);
//			float dist = Math.abs(pos.x - newX);
////			if(dist < 0.4f)
////			ItemAnimation.setX(newX);
////			else
////				ItemAnimation.setX(pos.x+(0.4f*dir));
//			if(AnimTime < totalAnimTime/2)
//			ItemAnimation.setX((pos.x + 0.2f*dir) + (dir*(AnimTime/totalAnimTime)));
//			else
//				ItemAnimation.setX((pos.x + 0.2f*dir) + (1*dir-(dir*(AnimTime/totalAnimTime))));
//			ItemAnimation.setY(pos.y+0.15f);
//			
//			ItemAnimation.draw(batch);
//			if(AnimTime >= totalAnimTime) {
//				ItemAnimation = null;
//				AnimTime = 0;
//			}
//		}
//		}
//
//	}
	
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
	}
	
	synchronized void Move(int direction) {
		if(direction != JUMP) {
		state = RUN;
		this.dir = direction;
		isMoving = true;
		}
		else {
			Jump();
		}
	}
	
	void Jump() {
		
		if(Terrain.CurrentTerrain.GetTile(pos.x+0.5f, pos.y+0.5f) != Terrain.CurrentTerrain.light.LadderID) {
		state = JUMP;
		vel.y = JUMP_VELOCITY;
		grounded = false;
		}
		else {
			vel.y = 5;
		}
		
	}
	
	void AddToPos(float x, float y) {
		
		newPos = new Vector2(x,y);
		
	}
	
void AddToBag(int id, int amt) {
		
		
		for(int y=0; y<Inventory.CurrentInventory.BagHeight; y++)
		{
			for(int x=0; x<Inventory.CurrentInventory.BagWidth; x++) {
				
				int i = x + (y*Inventory.CurrentInventory.BagWidth);
				
				if(inventory.BagItem[i][1] != 0 && id != inventory.BagItem[i][0] || inventory.BagItem[i][1] >= 32)
					continue;
				
				
				
				inventory.BagItem[i][0] = id;
				inventory.BagItem[i][1] += amt;
				
				
				return;
			}
		}
		
		
	}

public void onClientUse(int wep, float angle, int first, float x, float y) {
	firstUsed = first;
	inventory.Items[wep].OnUse(this,x, y, angle);
}

public void onUse(float angle,int used,float x, float y) {
	
	World world = World.CurrentWorld;
	InvObject item = null;
	if(inventory.BagItem[used][1] > 0)
		item = inventory.Items[inventory.BagItem[used][0]];
	
	
	int delay = 100;
	if(item != null)
		delay = item.Delay;

	if(TimeUtils.millis() - LastUsed > delay)
	{
		if(inventory.BagItem[used][1] > 0)
		{
			
			item.OnUse(this,x, y, angle);
			if(World.CurrentWorld.server != null) {
			onServerUse(item,x,y,angle);
			}
			//if(World.CurrentWorld.server != null)
			
		}
		LastUsed = TimeUtils.millis();
	}
}

public boolean firstUse() {
		return Gdx.input.justTouched();
}
public boolean isAttacking;
public void Animate(JItemAnimation activeSprite, int style, float time) {
	// TODO Auto-generated method stub
		MapRenderer.CurrentRenderer.ItemAnimation = activeSprite;
		AnimTime = 0;
		AnimationStyle = style;
		ItemAnimdir = dir;
		ItemPos.set(pos.x,pos.y+0.1f);
		totalAnimTime = time;
		if(ItemAnimdir == RIGHT)
			activeSprite.flip(true, false);
		
		isAttacking = true;
}

public void OnDamage(AI ai, int aTK, float f) {
	// TODO Auto-generated method stub
	if(DamagedTimer < 0.8f)
		return;
	vel.x = f;
	vel.y = 3;
	DamagedTimer = 0;
	grounded = false;
	aTK -= (getDEF()*0.8f);
	if(aTK <= 0)
		aTK = 1;
	HP -= aTK;
	Inventory.CurrentInventory.dmgTxts.add(new dmgText(pos.x, pos.y+1, aTK));
	
	SoundManager.PlaySound("playerHurt",3);
	
	
}

public void onKilled(AI ai) {
	// TODO Auto-generated method stub
	
}


public void UpdateGrapple(float delta) {
	// TODO Auto-generated method stub
	
//	float x0 = Gdx.input.getX(0);
//	float y0 = Gdx.graphics.getHeight() - Gdx.input.getY(0);
	
	float X = ((pos.x+0.5f) + (Grablen*MathUtils.cosDeg(Grabber.angle2)));
	float Y = ((pos.y+0.5f) + (Grablen*MathUtils.sinDeg(Grabber.angle2)));
	
	
	float x2 = pos.x  + 0.5f;
	float y2 = pos.y + 0.5f;
	
	Vector3 test = new Vector3(x2,y2,0);
	MapRenderer.CurrentCam.project(test);
	
//	float x3 = x0 - test.x;
//	float y3 = y0 - test.y;
	
//	if(firstShot) {
//		Pixmap map = new Pixmap(2,2, Format.Alpha);
//		map.setColor(Color.GRAY);
//		map.fill();
//		Texture tex = new Texture(map);
//		Grabber.grappleGfx = new WorldSprites(tex, x2, y2, 3,0.1f);
//		//grappleGfx.Width = 0.1f;
//		MapRenderer.CurrentRenderer.ToDraw.add(Grabber.grappleGfx);
//		firstShot = false;
//		
//		
//	}
	
	//float angle = (float) Math.toDegrees(MathUtils.atan2(y3, x3));
	if(!Grabbed) {
//		Grabber.grappleGfx.angle = Grabber.angle2;
//		Grabber.grappleGfx.x = x2;
//		Grabber.grappleGfx.y = y2;
//		Grabber.grappleGfx.Width = Grablen;
		Grablen += delta * 26f;
		
		if(Grablen > Grabber.MaxLen && !Grabbed) {
//			Grabber.grappleGfx.deleted = true;
			Grabber.isUpdating = false;
			Grabber.isShooting = false;
			Grablen = 0;
			return;
		}
		Grabber.bounds.set(X,Y,1,1);
		fetchCollidableRects2((int)X,(int)Y,(int)Leaf.ID);
		for(int i=0; i<4; i++) {
			if(r[i].contains(X, Y)) {
		
		if(!Grabbed) {
				Grabbed = true;
				Grabpos = new Vector2(X,Y);
				Grablen = pos.dst(X, Y);
				Grabbed = true;
				float x4 = pos.x - X;
				float y4 = pos.y - Y;
				
				alpha = (float) Math.atan2(y4, x4);
				return;
		}
			}
		}
	}
	else
	{
		float X2 = Grabpos.x - x2;
		float Y2 = Grabpos.y - y2;
		Grabber.angle = MathUtils.atan2(Y2, X2);
//		Grabber.grappleGfx.angle = (float) Math.toDegrees(Grabber.angle);
//		Grabber.grappleGfx.x = x2;
//		Grabber.grappleGfx.y = y2;
//		Grabber.grappleGfx.Width = Grabpos.dst(pos.x+0.5f,pos.y+0.5f);
		

		
		
	}
}

void jump() {
	
}

Vector2 tempcoords = new Vector2();
@Override
public boolean touchDown(InputEvent event, float x, float y, int pointer,
		int button) {
	// TODO Auto-generated method stub
	
	if(state == SPAWN || state == DYING || state == DEAD || stateTime < 0.5f) {
		return false;
	}

	

	
	tempcoords.set(event.getStageX(),event.getStageY());
	Vector2 coords = OnScreenController.stage.stageToScreenCoordinates(tempcoords);
	x = coords.x;
	y = coords.y;
	screen_attack = true;
	attack_queue = true;
	target.set(x, y);
	
	
//	if(MapRenderer.CurrentRenderer.ItemAnimation == null) {
//		
//		if(Gdx.input.justTouched())
//			attack_queue = true;
//	
//	
//			if(attack_queue) {
//		DoMining(Gdx.input.getX(0),Gdx.graphics.getHeight()-Gdx.input.getY(0));
//		
//
//			}
//
//	//pos.x = x;
//	//pos.y = y;
//	}
	
	
	
	
	
	
	
	
	
	return true;
}


@Override
public void touchDragged(InputEvent event, float x, float y, int pointer) {
	// TODO Auto-generated method stub
	
	tempcoords.set(event.getStageX(),event.getStageY());
	Vector2 coords = OnScreenController.stage.stageToScreenCoordinates(tempcoords);
	x = coords.x;
	y = coords.y;
	
	if(screen_attack)
		target.set(x, y);
	super.touchDragged(event, x, y, pointer);
}

@Override
public void touchUp(InputEvent event, float x, float y, int pointer,
		int button) {
	// TODO Auto-generated method stub
	screen_attack = false;
	attack_queue = false;
	super.touchUp(event, x, y, pointer, button);
}


}
