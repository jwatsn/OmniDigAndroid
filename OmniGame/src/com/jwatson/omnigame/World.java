package com.jwatson.omnigame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.TimeUtils;
import com.jwatson.omnigame.ai.*;


	

public class World {
	
	public TextureRegion rock_particle;
	
	float LastUpdate;
	
	public int MaxNightMobs;
	public int NightMobCounter;
	
	float tsX;
	float tsY;
	Color skyColor;
	public long MovedTimer;
	float keyTimer;
	Inventory InvBag;
	boolean RespawnFlag;
	public GameClient client;
	public GameServer server;
	public Thread serverThread;
	public HashMap<String, TextureRegion> particle_textures;
	
	boolean isNight = false;
	public ParticleEffect effect;
	ParticleEmitter emitter;
	
	
	public Lighting light;
	
	boolean isClient;
	
	public LinkedBlockingQueue<UpdatableItem> UpdateList;
	public List<UpdatableItem> UpdateListRemove;
	
	LinkedBlockingQueue<MessageInfo> msginfo;
	LinkedBlockingQueue<BobState> bobState;
	
	
	public volatile HashMap<Integer,Bob> ClientBobs;
	ConcurrentHashMap<Integer, Vector3> recentdamagedBlocks;
	HashMap<Integer,Vector3> damagedBlocks;
	
	public static World CurrentWorld;
	MapRenderer Map;
	public List<WorldObj> SpawnedObjects;
	List<WorldObj> ToBeDeleted;
	public List<WorldObj> pendingWorldObjs;
	boolean isLoading;
	boolean newChar;
	int charSlot;
	float animTime = 0;
	
	
	public static float sky_transition;
	public static float sky_transition_time = 14;
	
	List<TerrainChunk> rChunks;
	List<AI> AI;
	
	Bob MainBob;
	
	Terrain terrain;
	
	ChatBox chatBox;
	
	public List<ParticleWorldObj> particles;
	
	public List<Rectangle> climbableBounds;
	
	
	//Growing Stuff
	float GrowTimer = 0;
	GrowableManager GrowManager;
	public Trees TreeManager;
	//Liquid stuff
	public LiquidManager m_LiquidManager;
	
	
	
	public World(MapRenderer map, boolean isLoading, boolean newChar, int charslot) {
		Map = map;
		if(World.CurrentWorld != null)
			World.CurrentWorld = null;
		World.CurrentWorld = this;
		this.isLoading = isLoading;
		this.newChar = newChar;
		this.charSlot = charslot;
		climbableBounds = new ArrayList<Rectangle>();
		UpdateList = new LinkedBlockingQueue<UpdatableItem>();
		UpdateListRemove = new ArrayList<UpdatableItem>();
		InvBag = new Inventory();
		
		SpawnedObjects = new ArrayList<WorldObj>();
		ToBeDeleted  = new ArrayList<WorldObj>();
		pendingWorldObjs = new ArrayList<WorldObj>();
		ClientBobs = new HashMap<Integer,Bob>();
		damagedBlocks = new HashMap<Integer, Vector3>();
		recentdamagedBlocks = new ConcurrentHashMap<Integer, Vector3>();
		
		terrain = new Terrain(map,300,300,20,20);
		particles = new ArrayList<ParticleWorldObj>();
		//rChunks = new ArrayList<TerrainChunk>(4);
		
		AI = new ArrayList<AI>();
		
		
		msginfo = new LinkedBlockingQueue<MessageInfo>();
		bobState = new LinkedBlockingQueue<BobState>();
		
		GrowManager = new GrowableManager();
		TreeManager = new Trees(GrowManager);
		m_LiquidManager = new LiquidManager();
		
		CountDownLatch latch = new CountDownLatch(1);
		//Thread lightthread = new Thread(Terrain.CurrentTerrain.light);
		//lightthread.start();
		particle_textures = new HashMap<String, TextureRegion>();
		
		//CreateServer();

		//CreateClient();
		
//		effect = new ParticleEffect();
//		effect.load(Gdx.files.internal("data/test.p"), Gdx.files.internal("data"));
		
	}
	
	
	public void MineBlock(InvObject item,int x, int y, int atk) {
		
		
		//Gdx.app.debug("bobpos x"+MainBob.pos.x+ " y"+MainBob.pos.y, "SLOPE: "+newX + " "+newY);

		
		boolean hitflag = false;
		
		//int slope = newY/newX;
		int id = Terrain.CurrentTerrain.GetTile(x, y);
		if(id > 0) {
		InvObject blk = Inventory.CurrentInventory.GetItem(id);
		
		
		if(item.isStrongAgainst(blk) || blk.type == InvObject.Type.BREAKABLE || blk.type == InvObject.Type.TURRET || blk.Breakable && blk.type != InvObject.Type.BLOCK && blk.type != InvObject.Type.WOOD && blk.type != InvObject.Type.LEAF)
			{
			hitflag = true;
//			if(hit_sound_timer > 0.3f) {
			SoundManager.PlaySound(blk.hit_sound, 3);
//			hit_sound_timer = 0;
			TextureRegion p = GetParticleTexture(blk.name);
			if(p != null) {
			ParticleWorldObj particle = new ParticleWorldObj(p,x+0.5f, y+0.5f, 0.2f, 0.2f);
			ParticleWorldObj particle2 = new ParticleWorldObj(p,x+0.5f, y+0.5f, 0.2f, 0.2f);
			pendingWorldObjs.add(particle);
			pendingWorldObjs.add(particle2);
			particles.add(particle);
			particles.add(particle2);
			}
//			}
		}
		if(blk.MineLevel > 0) {
			if(item.MineLevel < blk.MineLevel)
				hitflag = false;
		}
		}
		
		
		int distance = (int)MainBob.pos.dst(x, y);
		
		
		
		if(hitflag) {
			Bob.CurrentBob.lastDamaged.set(x,y);
		int destroyed = terrain.DestroyBlock(x, y, 0.3f);
		
		
		if(destroyed > 0)
		{
				int aboveTile = Terrain.GetTile(x, y+1);
				if(aboveTile > 0) {
					InvObject block = Item.Items[aboveTile];
					if(block.needsSolidPlacement || block.needsSolidPlacement_ground) {
						Terrain.SetTile(x,y+1,0);
						WorldObj obj = new WorldObj(this, x+0.035f, y+1, 1, 1, block.InvObjID, 1);
						pendingWorldObjs.add(obj);
					}
				}
		}
		}
		
	}
	
	public WorldObj AddToWorld(float x,float y,float width, float height,int ID) {
		WorldObj obj = new WorldObj(this,x, y,width,height,ID, 1);
		SpawnedObjects.add(obj);
		return obj;
	}
	
	List<WorldObj> background = new ArrayList<WorldObj>();
	
	
	Rectangle[] r = { new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(),new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle() };
	
	
	public void fetchChunkRect(int x, int y) {
		
		int X = x/terrain.chunkWidth;
		int Y = y/terrain.chunkHeight;
		
		try {
		//r[0].set((X+1)*terrain.chunkWidth,Y*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[0] = Terrain.TerrainChunks[(X+1)+Y*Terrain.Width].bounds;
		
		//r[1].set((X-1)*terrain.chunkWidth,Y*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[1] = Terrain.TerrainChunks[(X-1)+Y*Terrain.Width].bounds;
//		r[2].set(X*terrain.chunkWidth,(Y+1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[2] = Terrain.TerrainChunks[(X)+(Y+1)*Terrain.Width].bounds;
//		r[3].set(X*terrain.chunkWidth,(Y-1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[3] = Terrain.TerrainChunks[(X)+(Y-1)*Terrain.Width].bounds;
//		r[4].set((X+1)*terrain.chunkWidth,(Y+1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[4] = Terrain.TerrainChunks[(X+1)+(Y+1)*Terrain.Width].bounds;
//		r[5].set((X-1)*terrain.chunkWidth,(Y-1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[5] = Terrain.TerrainChunks[(X-1)+(Y-1)*Terrain.Width].bounds;
//		r[6].set((X+1)*terrain.chunkWidth,(Y-1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[6] = Terrain.TerrainChunks[(X+1)+(Y-1)*Terrain.Width].bounds;
//		r[7].set((X-1)*terrain.chunkWidth,(Y+1)*terrain.chunkHeight,terrain.chunkWidth,terrain.chunkHeight);
		r[7] = Terrain.TerrainChunks[(X-1)+(Y+1)*Terrain.Width].bounds;
		}
		catch(NullPointerException e) {
			int xx = x/Terrain.chunkWidth;
			int yy = y/Terrain.chunkHeight;
			for(int tx=-2;tx<2; tx++)
				for(int ty=-2;ty<2; ty++) {
					terrain.GetChunkByID(tx+xx, ty+yy);
				}
		}
		
		
	}
	
	int bb = 0;

	private boolean Updating;

	public float worldTime;

	private float timeStep;

	int counter;
	boolean keyPressed;
	Vector3 culling = new Vector3();
	List<WorldObj> NightMobs = new ArrayList<WorldObj>(); 
	private float hit_sound_timer;
	public void update(float delta) {
		


		
		hit_sound_timer += delta;
		
		MaxNightMobs = Map.Difficulty + 2;
		
		if(Map.Difficulty == 4) {
			MaxNightMobs = 15;
		}
		
		if(isNight) {
			if(NightMobCounter < MaxNightMobs) {
				pendingWorldObjs.add(new WeakSkeleton(this, Bob.CurrentBob.pos.x, Bob.CurrentBob.pos.y, 1, 1));
				NightMobCounter++;
				Gdx.app.debug("", ""+NightMobCounter);
			}
		}

			
		if(Gdx.input.isKeyPressed(Keys.F1) && !keyPressed) {
			worldTime = 1000;
			keyTimer = 0;
		}
		else if(keyTimer > 1f)
			keyPressed = false;
		
		m_LiquidManager.update2(delta);
		
		animTime += delta;
//		if(client.IsActive()) {
//			client.updateInput(delta);
//		}
		if(GrowTimer >= 1) {
		GrowManager.update();
		GrowTimer = 0;
		}
		GrowTimer+=delta;
		
		int updateTime = 300;
		if(isNight)
			updateTime = 150;
		
		if(worldTime > updateTime) {
		timeStep += delta;
		if(!isNight)
		sky_transition += delta;
		else
			sky_transition -= delta;
		if(timeStep > 0.5f) {
				
		timeStep = 0;
		float bright = sky_transition / sky_transition_time;
		if(bright > 1)
			bright = 1;
		if(bright < 0)
			bright = 0;
		terrain.globalBrightness = 4 + 11-(11 * bright);
		counter++;
		Terrain.CurrentTerrain.updateBrightness();
		Terrain.CurrentTerrain.light.spreadLight(null);
		}
		if(counter >= sky_transition_time/0.5f) {
			
			worldTime = 0;
			isNight = !isNight;
			counter = 0;
			

			if(!isNight) {
				for(WorldObj obj : SpawnedObjects) {
					if(!obj.isAI)
						continue;
					
					if(obj.ai.NightOnly)
					obj.ai.onDamaged(100);
				}
			}
		}
		//worldTime = 0;
		}
		
//		if(client.serverBob != null)
//			if(client.serverBob.active) {
//				client.serverBob.update(delta);
//			}
		
//		if(SpawnedObjects.size() > 30) {
//			
//			SpawnedObjects.remove(GetFirstItem());
//		}
		keyTimer += delta;
		if(pendingWorldObjs.size() > 0) {
			SpawnedObjects.addAll(pendingWorldObjs);
		pendingWorldObjs.clear();
		}
		
		
			
		Updating = true;
		if(!MapRenderer.CurrentRenderer.NewMapSpawn)
		for(WorldObj obj : SpawnedObjects) {
			if(obj.removalflag)
			{
				particles.remove(obj);
				ToBeDeleted.add(obj);
				continue;
			}
			if(Inventory.CurrentInventory.BagActive)
			if(obj.isAI || obj.isTurret)
				continue;
			obj.update(delta);
		}
		Updating = false;
		
		
		if(ToBeDeleted.size() > 0) {
			SpawnedObjects.removeAll(ToBeDeleted);
			ToBeDeleted.clear();
		}
		

		if(MainBob != null)
			MainBob.update(delta);
		
//		for(Bob b : ClientBobs.values()) {
//			b.update(delta);
//		}
//		
//		if(terrain.toBeDestroyed.size() > 0) {
//			Vector2 pos = terrain.toBeDestroyed.pop();
//	      	   int cX = (int) (pos.x/Terrain.CurrentTerrain.chunkWidth);
//        	   int cY = (int) (pos.y/Terrain.CurrentTerrain.chunkHeight);
//        	   
//        	   int cX2 = (int) (pos.x%Terrain.CurrentTerrain.chunkWidth);
//        	   int cY2 = (int) (pos.y%Terrain.CurrentTerrain.chunkHeight);
//        	   
//        	   TerrainChunk tchunk = Terrain.CurrentTerrain.GetChunkByID(cX, cY);
//		
//        	   tchunk.DeleteBlock2(cX2, cY2);
//		}
				
		
		
		
		//r[9].set(MapRenderer.CurrentCam.position.x-12,MapRenderer.CurrentCam.position.y-8,24,16);
//		r[8].set(MapRenderer.CurrentCam.position.x-12,MapRenderer.CurrentCam.position.y-8,24,16);
		if(LastUpdate > 0.2f) {
			
			if(terrain.ActiveChunks.size() > 10) {
				int r = terrain.recycle();
				Gdx.app.debug("recycled", ""+r);
			}
			
			
			
			
			}
		
		fetchChunkRect((int)MainBob.pos.x, (int)MainBob.pos.y);
		
		for(int i=0; i<8; i++) {
			int x = (int)((r[i].x)/terrain.chunkWidth);
			int y = (int)((r[i].y)/terrain.chunkHeight);
			int x2 = (int)((r[i].x)+(10));
			int y2 = (int)((r[i].y)+(10));
			culling.set(x2, y2, 0);
			if(MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 15)) {

				
				
				TerrainChunk ch = Terrain.TerrainChunks[x+y*terrain.Width];
				if(ch == null) {
					
					Terrain.TerrainChunks[x+y*terrain.Width] = new TerrainChunk(x, y, terrain.chunkWidth, terrain.chunkHeight, terrain);
					
				}
				else if(!ch.isActive)
					ch.Activate();
				
			}
			else {
				TerrainChunk ch = Terrain.TerrainChunks[x+y*terrain.Width];
				if(ch != null) {
					if(ch.isActive)
						ch.DeActivate();
				}
			}
			
		}
			

				
			
			LastUpdate=0;
			
	
		
		//ProcessClientBob();
		
		LastUpdate += delta;
		

		
		
		
		for(UpdatableItem e : UpdateList) {
			
			if(e.remove) {
				UpdateListRemove.add(e);
				continue;
			}
			e.counter += delta;
			if(e.counter >= e.speed) {
				e.update(e.counter);
				e.counter = 0;
			}
			
		}
		if(UpdateListRemove.size() > 0) {
			UpdateList.removeAll(UpdateListRemove);
			UpdateListRemove.clear();
		}
		if(RespawnFlag)
			SpawnBob((int)tsX,(int)tsY);
		
		
		
		terrain.light.update(delta);
		worldTime += delta;
		
	
			
	}
	

	
	private  WorldObj GetFirstItem() {
		
		for(WorldObj obj : SpawnedObjects) {
			if(obj.ai == null && obj.deletable)
				return obj;
		}
		return null;
	}


	public void SpawnBob(int x2, int y2) {
		
		InvBag.ClearInventory();
		//terrain.ActiveChunks.clear();
		RespawnFlag = false;
		
		Terrain.CurrentTerrain.GetChunkByID(x2/Terrain.CurrentTerrain.chunkWidth, y2/Terrain.CurrentTerrain.chunkHeight);
		
		if(Terrain.CurrentTerrain.isSolid(x2, y2)) {
			SpawnBob(x2,y2+1);
			return;
		}

		MainBob = new Bob(x2, y2, InvBag);
		InvBag.owner = MainBob;
		if(RespawnFlag)
			MainBob.ID = client.serverBob.ID;
		
		Map.cam.position.x = MainBob.pos.x;
		Map.cam.position.y = MainBob.pos.y;
		//terrain.ActivateChunk(x2, y2, Map.cache);
		//r[8].set(MainBob.pos.x-12,MainBob.pos.y-8,24,16);
		
//		int xx = x2/Terrain.chunkWidth;
//		int yy = y2/Terrain.chunkHeight;
//		for(int tx=-3;tx<4; tx++)
//			for(int ty=-3;ty<4; ty++) {
//				terrain.GetChunkByID(tx+xx, ty+yy);
//			}
		
		fetchChunkRect((int)MainBob.pos.x, (int)MainBob.pos.y);
		
		for(int i=0; i<8; i++) {
			culling.set(r[i].x+10,r[i].y+10,0);
			if(MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10)) {
				int x = (int)(r[i].x)/terrain.chunkWidth;
				int y = (int)(r[i].y)/terrain.chunkHeight;
				
				
				terrain.GetChunkByID(x, y);
				//terrain.CreateChunk(x, y);
				}
			}
		
		if(newChar) {
		InvBag.AddToBag("TOOL_PickAxe_Wood", 1);
		InvBag.AddToBag("TOOL_Axe_Wood", 1);
		//InvBag.AddToBag("ARMOR_BP_Copper", 1);
		//InvBag.AddToBag("ARMOR_Helm_Copper", 1);
		//InvBag.AddToBag("ARMOR_Legs_Copper", 1);
		//InvBag.AddToBag("ARMOR_Boots_Copper", 1);
		InvBag.AddToBag("DEP_Torch", 10);
//		InvBag.AddToBag("TOOL_GrapplingHook", 1);
		InvBag.AddToBag("WEP_Sword_Wood", 1);
		//InvBag.AddToBag("GEAR_Bomb", 20);
		SaveCharacter(charSlot);
		}
		else
			LoadCharacter(charSlot);
		//InvBag.AddToBag("TOOL_GrapplingHook", 1);
		
		Terrain.CurrentTerrain.light.spreadLight(null);

		
	}
	
	public void CreateServer() {
		server = new GameServer();
		serverThread = new Thread(server);
		serverThread.start();
	}
	
	public void CreateClient() {
		client = new GameClient(this);
	}


	public synchronized void AddToWorld(WorldObj obj) {
		SpawnedObjects.add(obj);
	}
	
	public synchronized void AddToWorld(int index, WorldObj obj) {
		SpawnedObjects.add(index,obj);
	}
	
	public void RemoveWorldObject(WorldObj obj) {
		// TODO Auto-generated method stub
		//Gdx.app.debug("", ""+obj.ID);
		SpawnedObjects.remove(obj);
	}
	
//	public void SpawnAI(float x, float y, AI ai) {
//		WorldObj obj = new WorldObj(x, y, ai);
//		SpawnedObjects.add(obj);
//	}
	
//	public void ProcessClientBob() throws NullPointerException {
//		
//		
//			//try {
//				MessageInfo info = msginfo.poll();
//				if(info != null) {
//					
//				
//					//try {
//					
//						if(info.spawn) {
//						
//							Bob bob = new Bob(info.x, info.y);
//							bob.ID = info.ID;
//							
//							if(server != null && info.ID > 0) {
//								server.getClientByID(info.ID).controlled = bob;
//							}
//							ClientBobs.put(info.ID, bob);
//							bob.needsupdate = true;
//							if(!isClient) {
//							bob.AddToBag(InvBag.GetItemID("TOOL_PickAxe_Wood"), 1);
//							bob.AddToBag(InvBag.GetItemID("TOOL_GrapplingHook"), 1);
//							bob.AddToBag(InvBag.GetItemID("DEP_Ladder"), 32);
//							bob.AddToBag(InvBag.GetItemID("BotSpawner"), 1);
//							bob.AddToBag(InvBag.GetItemID("GEAR_Bomb"), 1);
//							bob.AddToBag(InvBag.GetItemID("MAT_Water"), 1);
//							bob.AddToBag(InvBag.GetItemID("MAT_Grass"), 32);
//							bob.AddToBag(InvBag.GetItemID("DEP_Seed_BrownTree"), 32);
//							
//							}
//						}
//						else {
//							Bob bob = ClientBobs.get(info.ID);
//							
//							if(bob == null) {
//								bob = new Bob(info.x,info.y);
//								ClientBobs.put(info.ID,bob);
//							}
//							bob.bounds.x = info.x;
//							bob.bounds.y = info.y;
//							
//							
//						}
//					//}
//					//catch(NullPointerException e) {
//					//	Gdx.app.debug(""+e.toString(), "");
//					//}
//				
//				}
//				
//				//Gdx.app.debug("", ""+bobState.size());
//					
//				BobState bstate = bobState.poll();
//				
//				while(bstate != null) {
//					int id = bstate.id;
//					if(id != client.serverBob.ID) {
//						Bob bob = ClientBobs.get(id);
//						
//						if(bob == null) {
//							bob = new Bob(bstate.x, bstate.y);
//						}
//						
//						if(bstate.inv) {
//							bob.inventory.BagItem = bstate.bag;
//						
//						}
//						else {
//						
//						bob.ID = id;
//						 if(System.currentTimeMillis() - MovedTimer > 50)
//						bob.state = bstate.state;
//						if(server == null)
//						bob.AddToPos(bstate.x,bstate.y);
//						
//						bob.dir = bstate.direction;
//						bob.accel.x = bstate.velx;
//						
//						if(bstate.used >= 0) {
//							float angle = bstate.angle;
//							bob.firstUsed = bstate.firstUsed;
//							float dst = bob.pos.dst(bstate.UsedX,bstate.UsedY);
//							if(dst < 10)
//							bob.onUse(angle,bstate.used,bstate.UsedX,bstate.UsedY);
//						}
//						}
//						//bob.vel.y = bstate.vely;
//						//Gdx.app.debug("HMM", "BRO");
//					}
//					else {
//						if(bstate.inv) {
//							Bob.CurrentBob.inventory.BagItem = bstate.bag;
//							
//						}
//						else {
//						Bob.CurrentBob.state = bstate.state;
//						if(Bob.CurrentBob.pos.dst(bstate.x,bstate.y) > 2 || bstate.state == Bob.IDLE) {
//						Bob.CurrentBob.bounds.x = bstate.x;
//						Bob.CurrentBob.bounds.y = bstate.y;
//						Bob.CurrentBob.dir = bstate.direction;
//
//						//Bob.CurrentBob.vel.x = bstate.velx;
//						//Bob.CurrentBob.vel.y = bstate.vely;
//						}
//						}
//					
//					}
//					bstate = bobState.poll();
//				}
//				
//		//	}
//		//	catch(Exception e) {
//		//		Gdx.app.debug("ogeez", ""+e+toString());
//		//	}
//
//				
//
//	}
//	
public void SaveCharacter(int slot) {
		
		String path = "OmniDig/Characters/char"+slot+".dat";
		OutputStream out = Gdx.files.external(path).write(false);
	
		
		try {
			ObjectOutputStream o = new ObjectOutputStream(out);
			o.writeInt(MapRenderer.Version);
			for(int i = 0; i<Inventory.CurrentInventory.BagItem.length; i++) {
				if(Inventory.CurrentInventory.BagItem[i][0] > 0)
				o.writeUTF(Item.Items[Inventory.CurrentInventory.BagItem[i][0]].name);
				else
					o.writeUTF("blnk");
				o.writeInt(Inventory.CurrentInventory.BagItem[i][1]);
				
			}
			for(int i = 0; i<4; i++) {
				if(Inventory.CurrentInventory.Armor[i][1] > 0)
					o.writeUTF(Item.Items[Inventory.CurrentInventory.Armor[i][0]].name);
				else
					o.writeUTF("blnk");
			}
			
			o.flush();
			}
			catch(IOException e) {
				
			}
			
		}

public void LoadCharacter(int slot) {
	
	String path = "OmniDig/Characters/char"+slot+".dat";


	
	try {
		ObjectInputStream in = new ObjectInputStream(Gdx.files.external(path).read());
		int ver = in.readInt();
		for(int i = 0; i<Inventory.CurrentInventory.BagItem.length; i++) {
			String name = in.readUTF();
			if(name.equals("blnk"))
				Inventory.CurrentInventory.BagItem[i][0] = 0;
			else
				Inventory.CurrentInventory.BagItem[i][0] = Item.getId(name);
			Inventory.CurrentInventory.BagItem[i][1] =  in.readInt();
			
		}
		for(int i = 0; i<4; i++) {
			String name = in.readUTF();
			if(name.equals("blnk"))
				Inventory.CurrentInventory.Armor[i][0] = 0;
			else {
				Inventory.CurrentInventory.Armor[i][0] = Item.getId(name);
				Inventory.CurrentInventory.Armor[i][1] = 1;
			}
		}
		
		}
		catch(IOException e) {
			Gdx.app.debug("", ""+e.getMessage());
		}
		
	}


	TextureRegion GetParticleTexture(String name) {
		
		TextureRegion ret = null;
		if(Item.Items[Item.getId(name)].type != InvObject.Type.BREAKABLE)
		ret = MapRenderer.Texture_Atlas.findRegion(name);
		else
			Gdx.app.debug("WTF", "BRO");
		
		
		return ret;
	}
}
