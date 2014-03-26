package com.jwatson.omnigame;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.files.FileHandleStream;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.ChunkWriter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import com.jwatson.omnigame.InventoryObjects.BrownTree;
import com.jwatson.omnigame.InventoryObjects.BrownTreeSeed;
import com.jwatson.omnigame.InventoryObjects.Chest;
import com.jwatson.omnigame.InventoryObjects.Grass;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.StoneBlock;
import com.jwatson.omnigame.ai.Pig;
import com.jwatson.omnigame.graphics.CustomBatch;


//terrain..take 4




public class Terrain {
	
	
	public static TerrainChunk[] TerrainChunks;
	public static int[][] Biomes;
	
	Vector2 nextTile = new Vector2();
	
	Animation tileAnim;
	public static Terrain CurrentTerrain;
	
	public boolean SaveFlag;
	
	public TextureRegion[] TerrainTile;
	public TextureRegion breakAnim[];
	
	public TextureRegion[] breakWoodAnim;

	public TextureRegion CaveWall;
	public TextureRegion[] mineAnim;
	static byte[] TerrainMap;
	Stack<Vector3> draw_infront;
	
	Lock TerrainLock;
	
	TextureRegion Sky;
	
	List<Integer> FreeID;
	
	boolean isLoading;
	
	List<TerrainChunk> ActiveChunks;
	
	
	static int DirtBlock;
	static int StoneBlock;
	
	int sky = -1;
	
	int Dark = -1;
	public TextureRegion darkness;
	static int Width;
	static int Height;
	
	public static int chunkWidth;
	public static int chunkHeight;
	
	CustomBatch batch;
	
	Stack<Vector2> toBeDestroyed;
	
	MapRenderer map;

	//TerrainChunk[] tChunks;
	
	int lastX;
	int lastY;
	int tileHP;
	int tileMaxHP;
	
	byte[][] CollisionData;

	
	public Lighting light;
	TextureRegion dark;
	
	public float blankLightMap[];
	
	public float animTime = 0;

	ShaderProgram shader;
	ShaderProgram shader2;
	public Terrain(MapRenderer map, int width, int height,int chunkWidt,int chunkHeigh) {
		
		if(Terrain.CurrentTerrain != null)
			Terrain.CurrentTerrain = null;
		
		CurrentTerrain = this;
		
		Width = width;
		Height = height;
		
		draw_infront = new Stack<Vector3>();
		
		
		TerrainChunks = new TerrainChunk[width*height];
		
		
		
		
		toBeDestroyed = new Stack<Vector2>();
		
		chunkWidth = chunkWidt;
		chunkHeight = chunkHeigh;
		CaveWall = MapRenderer.Texture_Atlas.findRegion("cave_wall");
		blankLightMap = new float[chunkWidth*chunkHeight];
		for(int i =0; i<blankLightMap.length; i++)
			blankLightMap[i] = 1;
		
		
		this.map = map;
		FreeID = new ArrayList<Integer>();
		ActiveChunks = new ArrayList<TerrainChunk>();
		LoadTiles();

		
//		cache.beginCache();
//		cache.add(new TextureRegion(Sky),0, ((Height+2)*chunkHeight)/2, (Width*chunkWidth),(Height*chunkHeight)/2);
//		sky = cache.endCache();
		//tChunks = new TerrainChunk[width*height];
		
		//tempChunk = new ArrayList<TerrainChunk>();
		
		//CollisionData = SimpleRandomMap(chunkWidth*Width, chunkHeight*Height);
				
		/*
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				GetChunkByID(X,Y) = new TerrainChunk(x, y, chunkWidth, chunkHeight,this);
				GetChunkByID(X,Y).LoadTileMap(SimpleRandomMap(chunkWidth,chunkHeight));
			}
		}
		*/
		
		
		GenerateTerrain();
		
		light = new Lighting();
		ShaderProgram.pedantic = false;
		//map.cache.setShader(shader);
		breakAnim = new TextureRegion[7];
		breakWoodAnim = new TextureRegion[8];
		for(int i=0; i<7; i++) {
		breakAnim[i] = MapRenderer.Texture_Atlas.findRegion("breakanim"+i);
		}
		for(int i=0; i<8; i++) {
		breakWoodAnim[i] = MapRenderer.Texture_Atlas.findRegion("wbreakanim"+i);
		}
		
		
	}
	
	void LoadTiles() {
		
		
	}
	
	byte[][] SimpleRandomMap(int width, int height) {
		byte[][] temp = new byte[width][height];
		Random rand = new Random();
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				temp[x][y] = 1;
				
				int i = rand.nextInt(20);
				if(i>15)
					temp[x][y]= 2;
				
				if(i>17)
					temp[x][y] = 0;
				
			}
			}
		return temp;
		
		
	}
	
	void ActivateAll(SpriteCache cache) {
		for(int y=0; y<Height; y++) {
			for(int x=0; x<Width; x++) {
				//GetChunkByID(X,Y).Activate(cache);
			}
			}
	}
	Rectangle r = new Rectangle();
	public int bb = 0;
	public void render(CustomBatch batch) {
		
		if(SaveFlag) {
			SaveFlag = false;
			SaveMap(MapRenderer.CurrentRenderer.save_slot);
		}
		
		//map.plx_manager.render();
		
//		batch.begin();
		
//		if(sky >= 0 ) {
//		map.cache.begin();
//		map.cache.draw(sky);
//		map.cache.end();
//		}
		//shader.begin();
		bb = 0;
		for(TerrainChunk c : ActiveChunks) {
			c.render(batch);
		}
		
		//shader.end();
		
//		batch.end();
		

	}
	
	public void DestroyBlocks(Vector2[] blocks) {
		TerrainChunk tchunk = null;
		for(Vector2 vec : blocks) {
			
			if(vec != null) {
				int X = (int) (vec.x/chunkWidth);
				int Y = (int) (vec.y/chunkHeight);
				
				tchunk = GetChunkByID(X,Y);
				
				int X2 = ((int) vec.x%chunkWidth);
				int Y2 = ((int) vec.y%chunkHeight);		
				
				int old = tchunk.TerrainMap[X2 + (Y2*chunkWidth)];
				//Gdx.app.debug("WTF", ""+X + " " + Y);
				tchunk.DeleteBlock((int)vec.x,(int)vec.y,1000);
				
//				if(tchunk.DeleteBlock((int)vec.x,(int)vec.y,1000))
//				World.CurrentWorld.SpawnedObjects.add(new WorldObj(World.CurrentWorld,vec.x+0.5f, vec.y+0.5f,0.4f,0.4f,old, 1));
			}
			
		}
		if(tchunk != null)
		Terrain.CurrentTerrain.light.spreadLight(tchunk);
		
//		MapRenderer.CurrentRenderer.cache.clear();
////		sky = -1;
//		int b =0;
//		for(TerrainChunk c : ActiveChunks.values()) {
//			c.UpdateCache(MapRenderer.CurrentRenderer.cache);
//			b++;
//			
//		}
	}
	
	public int DestroyBlock(int x, int y, float f) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		int X2 = x - (X*chunkWidth);
		int Y2 = y - (Y*chunkHeight);		
				
		TerrainChunk ch = GetChunkByID(X,Y);
		int old = ch.TerrainMap[X2+(Y2*chunkWidth)];
		if(ch.DeleteBlock(x,y,f)) {
			
		
//		if(World.CurrentWorld.client.connection == null) {
//		MapRenderer.CurrentRenderer.cache.clear();
//		sky = -1;
		int b =0;
//		for(TerrainChunk c : ActiveChunks.values()) 
//			c.UpdateCache(MapRenderer.CurrentRenderer.cache);
//			b++;
//			
//		}
		//Terrain.CurrentTerrain.light.spreadLight(ch);
		//if(Inventory.CurrentInventory.Items[old].type == InvObject.Type.BLOCK)
		World.CurrentWorld.SpawnedObjects.add(new WorldObj(World.CurrentWorld,x+0.5f, y+0.5f,0.4f,0.4f,old, 1));
		//else
		//	World.CurrentWorld.SpawnedObjects.add(new WorldObj(World.CurrentWorld,x+0.1f, y+0.1f,1f,1f,old, 1));
	//	}
		return old;
		}
		return 0;
		
	}
	
	public boolean isEmpty(int x,int y) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		int X2 = x - (X*chunkWidth);
		int Y2 = y - (Y*chunkHeight);
		
		return GetChunkByID(X,Y).isEmpty(X2, Y2);
	}
	
	public void ActivateChunk(int x, int y, SpriteCache cache) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		int X2 = x - (X*chunkWidth);
		int Y2 = y - (Y*chunkHeight);
		
		if(!ChunkExists(X,Y))
			new TerrainChunk(X, Y, chunkWidth, chunkHeight,this);
	}
	
	public boolean ChunkExists(int x, int y) {
		
		if(x < 0 || y < 0)
			return false;
		
		TerrainChunk ch = TerrainChunks[x+y*Width];
		
		if(ch != null)
				return true;
		
		return false;
	}
	
	public TerrainChunk GetChunkRight(int x, int y) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		if(X>= Width)
			return null;
		//long i = System.currentTimeMillis();
		//TerrainChunk chunk = GetChunkByID(X+1,Y);
		//Gdx.app.debug("", ""+(System.currentTimeMillis() - i));
		return GetChunkByID(X+1,Y);
	}
	public TerrainChunk GetChunkLeft(int x, int y) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		if(X<=0)
			return null;
		
		return GetChunkByID(X-1,Y);
	}
	public TerrainChunk GetChunkUp(int x, int y) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		if(Y>=Height)
			return null;
		
		return GetChunkByID(X,Y+1);
	}
	public TerrainChunk GetChunkDown(int x, int y) {
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		
		if(Y<=1)
			return null;
		
		return GetChunkByID(X,Y-1);
	}
	
	//public ArrayList<TerrainChunk> retrieveChunks(int x, int y) {
	
	
	public int getTotalActiveChunks() {
		return TerrainChunk.TotalChunks;
	}
	Collection<TerrainChunk> rem2 = new ArrayList<TerrainChunk>();
	
	
	Vector3 culling = new Vector3();
	public int recycle() {
		
		for(int i=0; i<ActiveChunks.size(); i++) {
			TerrainChunk ch = ActiveChunks.get(i);
			culling.set(ch.x*chunkWidth + 10, ch.y*chunkHeight + 10, 0);
			
			if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10))
			{
			
				ch.DeActivate();
				//ActiveChunks.remove(x);
			}
		}
			
//		ActiveChunks.removeAll(rem2);
//		rem2.clear();
		
		return 0;
		
//		for(TerrainChunk c : ActiveChunks.values()) {
//			c.UpdateCache(MapRenderer.CurrentRenderer.cache);
//		}
	}
	Rectangle b_test = new Rectangle();
	public boolean CreateBlock(Bob bob,int x, int y, int type) {
		
		b_test.set(x,y,1,1);
		if(SpaceOccupied(b_test))
			return false;
		
		InvObject blk = Inventory.CurrentInventory.Items[type];
		
		if(blk.needsSolidPlacement_ground) {
			if(!isSolid(x, y-1))
				return false;
		}
		
		if(blk.needsSolidPlacement || blk.type == InvObject.Type.BLOCK) {
		boolean flag = false;
		for(int tx=-1; tx<=1; tx++)
			for(int ty=-1; ty<=1; ty++) {
				if(isSolid(x+tx, y+ty))
					flag = true;
			}
		
		if(!flag)
			return false;
		}
		
		if(blk.needsGround) {
			if(!isSolid(x, y-1))
				return false;
		}
		
		int X = x/chunkWidth;
		int Y = y/chunkHeight;
		int x2 = x%chunkWidth;
		int y2 = y%chunkHeight;
		TerrainChunk ch = GetChunkByID(X,Y);
		if(Inventory.CurrentInventory.Items[type].Liquid) {
			ch.LiquidMap[x2*2][y2*2]= 10;
			return true;
		}
		if(ch.CreateBlock(bob,x2,y2,type))
		{
		Terrain.CurrentTerrain.light.spreadLight(ch);
		bob.stateTime = 0;
		
		
		return true;
		}
		return false;
		
	}
//	private void SetPipe(int x, int y) {
//		
//		int X = x/chunkWidth;
//		int Y = y/chunkHeight;
//		int x2 = x%chunkWidth;
//		int y2 = y%chunkHeight;
//		TerrainChunk ch = GetChunkByID(X,Y);
//		
//		boolean top = false;
//		boolean bottom = false;
//		boolean right = false;
//		boolean left = false;
//		
//		if(Terrain.GetTile(x, y+1) > 0)
//		top = (Item.Items[Terrain.GetTile(x, y+1)].type == InvObject.Type.PIPE);
//		if(Terrain.GetTile(x, y-1) > 0)
//		bottom = (Item.Items[Terrain.GetTile(x, y-1)].type == InvObject.Type.PIPE);
//		if(Terrain.GetTile(x+1, y) > 0)
//		right = (Item.Items[Terrain.GetTile(x+1, y)].type == InvObject.Type.PIPE);
//		if(Terrain.GetTile(x-1, y) > 0)
//		left = (Item.Items[Terrain.GetTile(x-1, y)].type == InvObject.Type.PIPE);
//		
//		
//		if(top ) {
//			ch.TerrainMap[x2 + y2*ch.Width] = Item.getId("DEP_Pipe_UD");
//		}
//		
//	}

	boolean SpaceOccupied(Rectangle rect) {
		if(Bob.CurrentBob.bounds.overlaps(rect))
			return true;
		
		for(Bob b : World.CurrentWorld.ClientBobs.values())
			if(b.bounds.overlaps(rect))
				return true;
		
		return false;
	}
public void SetTileMap(int x, int y, int x2, int y2, byte value) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 >= terrain.chunkWidth) {		//get lower chunk
			if(terrain.ChunkExists(x+1, y)) {
//				TerrainChunk left = terrain.GetChunkByID(x+1, y);
//				return left.LightMap.get()
				
				x = x + 1;
				x2 = 0;
				
			}
			else
			return;
		}
		if(x2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x-1, y)) {
				
				x = x - 1;
				x2 = 0;
				
			}
			else
				return;
		}
		
		if(y2 >= terrain.chunkHeight) {		//get lower chunk
			if(terrain.ChunkExists(x, y+1)) {
				
				y = y + 1;
				y2 = 0;
				
			}
			else return;
		}
		
		if(y2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x, y-1)) {
				
				y = y - 1;
				y2 = terrain.chunkHeight-1;
				
			}
			 else return;
		}
		TerrainChunk tch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(tch != null)
		tch.TerrainMap[x2+y2*terrain.chunkWidth]= value;

	}
	
	void GenerateTerrain() {
		
		
		
		
	}
	
	int num = 0;
	public void GenerateWorld() { 
		
		FileHandle handle = new FileHandle("data/test.wat");
		OutputStream out = handle.write(false);
		
		for(int i=0; i<Width*Height; i++) {
			
			for(int x=0; x<chunkWidth; x++) {
				for(int y=0; y<chunkHeight; y++) {
					

					
				}
			}
			
		}
		
//			int x = num%Width;
//			int y = num/Width;
//			
//			float percent = (float)num/(float)(Width*Height);
//			
//			TerrainChunks[num] = new TerrainChunk(x, y, chunkWidth, chunkHeight, this);
//			
//			Gdx.app.debug("", "completed: "+percent*100);
			
			TerrainMap = new byte[Width*Height*chunkWidth * chunkHeight];
		
	}
	
	public TerrainChunk GetChunkByID(int x, int y) {

		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(chk != null) {
			if(!chk.isActive) {
				culling.set(chk.x*chunkWidth+10,chk.y*chunkHeight+10,0);
				if(MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10)) {
				chk.Activate();
				}
			}
		return chk;
		}
		else {
			return new TerrainChunk(x, y, Terrain.CurrentTerrain.chunkWidth, Terrain.CurrentTerrain.chunkHeight, this);
		}
			
	}
	
	public void CreateChunk(int x, int y) {
		if(!ChunkExists(x,y))
		new TerrainChunk(x,y,chunkWidth,chunkHeight,this);
	}
	
	public byte[] GetCollisionData(int x, int y) {
		
		return GetChunkByID(x,y).TerrainMap;
		
	}
	
	
	
	public int GetTile(int x,int y,int x2, int y2) {
		
		if(x2 > Terrain.CurrentTerrain.chunkWidth) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = Terrain.CurrentTerrain.chunkWidth-1;
			x--;
		}
		if(y2 > Terrain.CurrentTerrain.chunkHeight) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = Terrain.CurrentTerrain.chunkHeight-1;
			y--;
		}
		
		
		return GetChunkByID(x,y).TerrainMap[x2+(y2*chunkWidth)];
	}
	

	
	public float GetLiquid(int x,int y,int x2, int y2) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth*2) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = ((Terrain.CurrentTerrain.chunkWidth*2)-1);
			x--;
		}
		if(y2 > Terrain.CurrentTerrain.chunkHeight*2) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = (Terrain.CurrentTerrain.chunkHeight*2)-1;
			y--;
		}
		
		
		TerrainChunk ch =  GetChunkByID(x,y);
		
		//if(ch != null) {
		return ch.LiquidMap[x2][y2];
		//}
		//else return 0;
	}
	
	public void SetLiquidDirection(int x,int y,int x2, int y2,byte amt) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth*2) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = ((Terrain.CurrentTerrain.chunkWidth*2)-1);
			x--;
		}
		if(y2 > Terrain.CurrentTerrain.chunkHeight*2) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = (Terrain.CurrentTerrain.chunkHeight*2)-1;
			y--;
		}
		
		
		TerrainChunk ch =  GetChunkByID(x,y);
		
		//if(ch != null) {
		ch.DirectionMap[x2][y2] = amt;
		//}
		//else return 0;
	}	
	

	
	public byte GetLightTile(int x, int y) {
		int newX = (int)(x / chunkWidth);
		int newY = (int)(y / chunkHeight);
		int newX2 = (int)(x%chunkWidth);
		int newY2 = (int)(y%chunkHeight);
		TerrainChunk t = Terrain.TerrainChunks[newX + newY*Width];
		if(t != null)
		return t.LightMap2[newX2+newY2*chunkWidth];
		else
			return 0;
	}
	
	public void SetLightTile(int x, int y, byte num) {
		int newX = (x / chunkWidth);
		int newY = (y / chunkHeight);
		int newX2 = (x%chunkWidth);
		int newY2 = (y%chunkHeight);
		//ActiveChunks2.get(newX+newY*Width).LightMap[newX2+newY2*chunkWidth] -= num;
		
//		float topLight = GetLightTile(x, y+1);
//		float botLight = GetLightTile(x, y-1);
//		float rightLight = GetLightTile(x+1, y);
//		float leftLight = GetLightTile(x-1, y);
		
		//ActiveChunks2.get(newX+newY*Width).LightMap[newX2+newY2*chunkWidth] = num+((1-botLight)+(1-botLight)+(1-rightLight)+(1-leftLight));
		Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width].LightMap2[newX2+newY2*chunkWidth] = num;
		

	}
	
	public int GetTile2(float x, float y) {
		int newX = (int)(x / chunkWidth);
		int newY = (int)(y / chunkHeight);
		int newX2 = (int)(x%chunkWidth);
		int newY2 = (int)(y%chunkHeight);
		TerrainChunk t = GetChunkByID(newX,newY);
		if(t == null)
		{
			t = new TerrainChunk(newX, newY, chunkWidth, chunkHeight,this);
		}
		if(t.TerrainMap == null)
		{
			Gdx.app.debug("NEWP", "BRA");
			return 0;
		}
		return t.TerrainMap[newX2 + (newY2*chunkWidth)];
	}
	
	public static void SetTile(float x, float y, byte type) {
		int newX = (int)(x / chunkWidth);
		int newY = (int)(y / chunkHeight);
		int newX2 = (int)(x%chunkWidth);
		int newY2 = (int)(y%chunkHeight);
		TerrainChunk t = TerrainChunks[newX + newY * Width];
		if(t == null)
		{
			return;
		}
	
		t.TerrainMap[newX2 + (newY2*chunkWidth)] = type;
	}
	
	public Vector2 GetNextTile(float dist) {
		float x0 = Gdx.input.getX(0);
		float y0 = Gdx.graphics.getHeight() - Gdx.input.getY(0);
		
		
		float x2 = Bob.CurrentBob.pos.x  + 0.5f;
		float y2 = Bob.CurrentBob.pos.y + 0.5f;
		
		Vector3 test = new Vector3(x2,y2,0);
		MapRenderer.CurrentCam.project(test);
		
		float x3 = x0 - test.x;
		float y3 = y0 - test.y;
		
		float angle = MathUtils.atan2(y3, x3);
		
		return GetNextTile(angle, x2,y2);
	}
	
	public Vector2 GetNextTile(float angle, float x, float y) {
		

		
		
		nextTile.x = -1;
		nextTile.y = -1;
		
		
		
		for(int i=0; i<3; i++) {			
			
			int x2 = (int) ((x)+i*Math.cos(Math.toRadians(angle)));
			int y2 = (int) ((y)+i*Math.sin(Math.toRadians(angle)));
			
			if((int)x == x2 && (int)y == y2)
				continue;
			
			if(!isEmpty(x2,y2)) {
				nextTile.set(x2, y2);
				break;
			}
			}
		
		return nextTile;
	}
	
	
	public boolean isEmpty(double d, double e) {
		// TODO Auto-generated method stub
		return isEmpty((int)d, (int)e);
	}


	Vector2[] blocks;
	Rectangle exp = new Rectangle();

	public float globalBrightness = 15;
	public void Explode(float x, float y, float radius) {
		
		World world = World.CurrentWorld;
		Vector2 vec = new Vector2();
		vec.x = x+0.5f;
		vec.y = y+0.5f;
		exp.set(x-radius,y-radius,radius*2,radius*2);
		
		for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
			
			if(!obj.isInActiveChunk())
				continue;
			
			if(exp.overlaps(obj.bounds)) {
				if(obj.isAI) {
					
					obj.onDamaged(Bob.CurrentBob, null, 45);
					
				}
				
			}
			
		}
		
		
		if(exp.overlaps(Bob.CurrentBob.bounds)) {
			
			float pos = Bob.CurrentBob.pos.x - vec.x;
			float dist = vec.dst(Bob.CurrentBob.pos);
			float velx;
			if(pos > 0) {
				velx = (10/dist);
			}
			else
			{
				velx = -(10/dist);
			}
			Bob.CurrentBob.grounded = false;
//			World.CurrentWorld.BoundsToCheck.remove(this);
			Bob.CurrentBob.OnDamage(null, (int)(20 + Math.abs(velx)), velx);
			
		}

		
		
		
		
		if(world.isClient)
			return;
		blocks = new Vector2[(int)(radius*radius*4)];
		int i = 0;
		for(int X=(int) (x-radius); X < x+radius; X++) {
			for(int Y=(int)(y-radius); Y < y+radius; Y++) {
				float dist = vec.dst(X,Y);
				if(dist >= (radius))
					continue;
				
				//Gdx.app.debug("", ""+X + " " + Y);
				blocks[i] = new Vector2(X,Y);
				
				i++;
			}
		}
		
		
		DestroyBlocks(blocks);
		exp.set(0, 0, 0, 0);
	}
	
	
		
		

	
	public void SaveMap(int slot) {


		
		String path = Gdx.files.getExternalStoragePath()+ "OmniDig";


		
		
		

		path = "OmniDig/world"+slot+".map";
		OutputStream out = Gdx.files.external(path).write(false);
	
		
		try {
			ObjectOutputStream o = new ObjectOutputStream(out);
			
			o.writeInt(MapRenderer.Version);
			
			for(int j=0; j<Width; j++)
				for(int l=0; l<Height; l++)
					o.writeInt(Terrain.Biomes[j][l]);
			
			o.writeInt(Item.b);
			for(int i=1; i<Item.b; i++) {
				o.writeInt(i);
				o.writeUTF(Item.Items[i].name);
			}
			
			
			
			int numChunks = GetNumChunks();
			
			o.writeInt(numChunks);
			
			for(int x2=0; x2<Width;x2++) {
				for(int y2=0; y2<Height;y2++) {
					
			TerrainChunk ch = Terrain.TerrainChunks[x2+y2*Width];
			
			if(ch == null)
				continue;
			
			byte[] saveMap = generateSaveMap(ch);
			o.writeInt(ch.x);
			o.writeInt(ch.y);
			for(int x = 0; x< chunkWidth; x++)
				for(int y = 0; y< chunkHeight; y++) {
					if(saveMap[x+y*chunkWidth]>0)
					if(!Inventory.CurrentInventory.Items[saveMap[x+y*chunkWidth]].Savable)
						saveMap[x+y*chunkWidth] = 0;
					o.writeInt(saveMap[x+y*chunkWidth]);
					if(saveMap[x+y*chunkWidth] == Chest.ID) {
						int x3 = ch.x*chunkWidth+x;
						int y3 = ch.y*chunkHeight+y;
						int num = x3+y3*(chunkWidth*Width);
						o.writeUTF(Inventory.CurrentInventory.Chests.get(""+num));
						
					}
					o.writeInt(ch.BakedLightMap[x+y*chunkWidth]);
					o.writeInt(ch.origBakedLightMap[x+y*chunkWidth]);
					
				}
			
			for(int x = 0; x< chunkWidth*2; x++)
				for(int y = 0; y< chunkHeight*2; y++) {
					o.writeFloat(ch.LiquidMap[x][y]);
				}
						
			
			
				}
				

				
			}
			o.writeUTF(BackgroundManager.BGManager.col.toString());
			o.writeFloat(MapRenderer.CurrentRenderer.SpawnPos.x);
			o.writeFloat(MapRenderer.CurrentRenderer.SpawnPos.y);
			o.writeFloat(World.CurrentWorld.worldTime);
			o.writeFloat(Terrain.CurrentTerrain.globalBrightness);
			o.writeBoolean(World.CurrentWorld.isNight);
			o.writeInt(World.CurrentWorld.counter);
			o.writeFloat(World.sky_transition);
			o.flush();
			o.close();
		}
		catch(IOException e) {
			Gdx.app.debug("", ""+e.toString());
		}
		
		World.CurrentWorld.SaveCharacter(World.CurrentWorld.charSlot);
	}
	
	private int GetNumChunks() {
		int c = 0;
		for(TerrainChunk ch : Terrain.TerrainChunks) {
			if(ch == null)
				continue;
			
			c++;
		}
		
		return c;
	}

	void updateBrightness() {
		
		boolean isNight = World.CurrentWorld.isNight;
		
		for(TerrainChunk ch : ActiveChunks) {
		
		for(int i = 0; i < ch.BakedLightMap.length; i++) {
			
			

			ch.BakedLightMap[i] = (byte)Terrain.CurrentTerrain.globalBrightness;

			if(ch.BakedLightMap[i] < 1)
				ch.BakedLightMap[i] = 1;
			
			if(ch.BakedLightMap[i] > ch.origBakedLightMap[i])
				ch.BakedLightMap[i] = ch.origBakedLightMap[i];
			
		}

		}
		
		//Gdx.app.debug("",""+globalBrightness);
		
	}
	
	private byte[] generateSaveMap(TerrainChunk ch) {
		
		byte[] map = ch.TerrainMap.clone();
		for(int x=0; x<chunkWidth; x++) {
			for(int y=0; y<chunkHeight; y++) {
				
				if(map[x+y*chunkWidth] > 0)
					if(Item.Items[map[x+y*chunkWidth]].type == InvObject.Type.WOOD)
						compressTree(map,x,y,ch);

				
			}
		}
		
		return map;
	}

	private void compressTree(byte[] map2, int x, int y, TerrainChunk ch) {
		
		
		while(true) {
			
			if(y < 0)
				return;
//			if(map2[x+y*chunkWidth] != Trees.BROWN_TREE) {
//				if(map2[x+y*chunkWidth] == Grass.ID)
//					map2[x+y*chunkWidth] = (byte)BrownTreeSeed.ID;
				
					if(Item.Items[map2[x+y*chunkWidth]].type != InvObject.Type.WOOD) {

							map2[x+y*chunkWidth] = (byte)BrownTreeSeed.ID;

				return;
			}
			y--;
			
			
		}
		
		
	}


	
	void LoadMap(int slot) {

		String[] names = new String[100];
		InputStream in = Gdx.files.external("OmniDig/world"+slot+".map").read();
		ActiveChunks.clear();
		try {
			ObjectInputStream inn = new ObjectInputStream(in);
			
			int version = inn.readInt();
			
			Terrain.Biomes = new int[Width][Height];
			
			for(int j=0; j<Width; j++)
				for(int l=0; l<Height; l++)
					Terrain.Biomes[j][l] = inn.readInt();
			
			int j = inn.readInt();
			for(int i=1; i<j; i++) {
				int id = inn.readInt();
				names[id] = inn.readUTF();
			}
			
//		for(int i=0; i<numChunks; i++) {
		

		int max = inn.readInt();
		for(int j2=0; j2<max; j2++) {
			
			int x = inn.readInt();
			int y = inn.readInt();

			TerrainChunk chk = new TerrainChunk(x, y, chunkWidth, chunkHeight,true);

			
			for(int x2 = 0; x2< chunkWidth; x2++)
				for(int y2 = 0; y2< chunkHeight; y2++) {
					
					
						byte id = (byte)inn.readInt();
						if(id > 0) {
						String name = names[id];
						
						id = (byte) Item.getId(name);
						if(Item.Items[id].type == InvObject.Type.TURRET) {
							TurretObj turret = new TurretObj(x*chunkWidth+x2, y*chunkHeight+y2, 1, 1, 10,1);
							World.CurrentWorld.pendingWorldObjs.add(turret);
							}
						}
					
					chk.TerrainMap[x2+y2*chunkWidth] = id;
					if(chk.TerrainMap[x2+y2*chunkWidth] == Chest.ID) {
						int x3 = (chk.x*chunkWidth)+x2;
						int y3 = (chk.y*chunkHeight)+y2;
						int num = x3+y3*chunkWidth*Width;
						String str = inn.readUTF();
						Inventory.CurrentInventory.Chests.put(""+num, str);
					}
					
					chk.BakedLightMap[x2+y2*chunkWidth] = (byte)inn.readInt();
					chk.origBakedLightMap[x2+y2*chunkWidth] = (byte)inn.readInt();
				}
			
			for(int x3 = 0; x3< chunkWidth*2; x3++)
				for(int y3 = 0; y3< chunkHeight*2; y3++) {
					chk.LiquidMap[x3][y3] = inn.readFloat();
				}
			
			
			//chk.Activate();
			chk.PlantTrees();
		}
		
		String colour = inn.readUTF();
		Color col = Color.valueOf(colour);
		BackgroundManager.BGManager.col.set(col);
		float xx = inn.readFloat();
		float yy = inn.readFloat();
		MapRenderer.CurrentRenderer.SpawnPos = new Vector2(xx,yy);
		World.CurrentWorld.worldTime = inn.readFloat();
		globalBrightness = inn.readFloat();
		World.CurrentWorld.isNight = inn.readBoolean();
		World.CurrentWorld.counter = inn.readInt();
		World.sky_transition = inn.readFloat();
		}
		catch(Exception e) {
			Gdx.app.debug("", ""+e.toString());
		}
		
	}
	
	void LoadChunk(TerrainChunk ch) {
		FileHandle handle = new FileHandle("data/test.wat");
		InputStream in = handle.read();
	
		
//		try {
//			for(int x = 0; x< chunkWidth; x++)
//				for(int y = 0; y< chunkHeight; y++) {
//					ch.TerrainMap[x+y*chunkWidth] = (byte)in.read();
//					
//				}
//		}
//		catch(IOException e) {
//			Gdx.app.debug("", ""+e.toString());
//		}
		
		
		
		
	}
	
	boolean isCollidable(int x, int y, int x2, int y2) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = Terrain.CurrentTerrain.chunkWidth-1;
			x--;
		}
		if(y2 >= Terrain.CurrentTerrain.chunkHeight) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = Terrain.CurrentTerrain.chunkHeight-1;
			y--;
		}
		if(y<0 || x < 0)
			return true;
		TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(ch != null)
		{
			
			if(ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
				return false;
			
			if(Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				if(ch.stateMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
					return true;
				else
					return false;
			}
			
			return Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].collidable;
		}
		return true;
	}
	
boolean isSolid(int x, int y, int x2, int y2) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = Terrain.CurrentTerrain.chunkWidth-1;
			x--;
		}
		if(y2 >= Terrain.CurrentTerrain.chunkHeight) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = Terrain.CurrentTerrain.chunkHeight-1;
			y--;
		}
		TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(ch != null)
		{
			if(ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] <= 0)
				return false;
			

			
			if(Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
				if(ch.stateMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
					return true;
				else
					return false;
			}
			
			return Item.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].solid;
		}
		return false;
	}

boolean isOpaque(int x, int y, int x2, int y2) {
	
	if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
		x2 = 0;
		x++;
	}
	
	if(x2 < 0) {
		x2 = Terrain.CurrentTerrain.chunkWidth-1;
		x--;
	}
	if(y2 >= Terrain.CurrentTerrain.chunkHeight) {
		y2 = 0;
		y++;
	}
	if(y2 < 0) {
		y2 = Terrain.CurrentTerrain.chunkHeight-1;
		y--;
	}
	TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
	if(ch != null)
	{
		if(ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
			return true;
		

		
		
		return Item.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].opaque;
	}
	return false;
}

boolean isBreakable(int x, int y, int x2, int y2) {
	
	if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
		x2 = 0;
		x++;
	}
	
	if(x2 < 0) {
		x2 = Terrain.CurrentTerrain.chunkWidth-1;
		x--;
	}
	if(y2 >= Terrain.CurrentTerrain.chunkHeight) {
		y2 = 0;
		y++;
	}
	if(y2 < 0) {
		y2 = Terrain.CurrentTerrain.chunkHeight-1;
		y--;
	}
	TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
	if(ch != null)
	{
		if(ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
			return false;
		
		
		if(Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].Breakable || Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].type == InvObject.Type.TURRET)
		return true;
	}
	return false;
}

boolean isSolid(int x3, int y3) {
	
	int x = x3 / Terrain.CurrentTerrain.chunkWidth;
	int y = y3 / Terrain.CurrentTerrain.chunkHeight;
	int x2 = x3 % Terrain.CurrentTerrain.chunkWidth;
	int y2 = y3 % Terrain.CurrentTerrain.chunkHeight;
	TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
	if(ch != null)
	{
		if(ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0) {
			return false;
		}
		
		if(Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
			if(ch.stateMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
				return true;
			else
				return false;
		}
		
		return Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].solid;
	}
	return false;
}

public boolean isCollidable(float x3, float y3) {
	int x = (int)x3 / Terrain.CurrentTerrain.chunkWidth;
	int y = (int)y3 / Terrain.CurrentTerrain.chunkHeight;
	int x2 = (int)x3 % Terrain.CurrentTerrain.chunkWidth;
	int y2 = (int)y3 % Terrain.CurrentTerrain.chunkHeight;
	
	TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
	if(ch != null)
	{

		
		int id = ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth];
		if(id == 0)
			return false;
		
		if(Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
			if(ch.stateMap[x2+y2*Terrain.CurrentTerrain.chunkWidth] == 0)
				return true;
			else
				return false;
		}
		
	return Inventory.CurrentInventory.Items[id].collidable;
	}
	return false;
}

//public void CreateSkyCache() {
//	
//	map.cache.clear();
//	
//	map.cache.beginCache();
//	
//
//	map.cache.add(Sky, 0, ((Width*chunkHeight)-(((Width*chunkHeight)/2)-chunkHeight)),Width*chunkHeight,((Width*chunkHeight)/2)+chunkHeight);
//	map.cache.add(dark, 0,0,Width*chunkHeight,((Width*chunkHeight)/2)+chunkHeight);
//			
//			
//	
//	sky = map.cache.endCache();
//	
//	
//}

public static int GetTile(float x, float y) {
	
	
	int newX = (int)(x/chunkWidth);
	int newY = (int)(y/chunkHeight);
	int newX2 = (int)(x%chunkWidth);
	int newY2 = (int)(y%chunkHeight);
	
	TerrainChunk ch = TerrainChunks[newX+newY*Width];
	if(ch != null)
		return ch.TerrainMap[newX2+newY2*chunkWidth];
	else
		return 0;
	
}

public static void SetTile(int x, int y, int i) {
	int newX = (int)(x/chunkWidth);
	int newY = (int)(y/chunkHeight);
	int newX2 = (int)(x%chunkWidth);
	int newY2 = (int)(y%chunkHeight);
	
	TerrainChunk ch = TerrainChunks[newX+newY*Width];
	if(ch != null)
		ch.TerrainMap[newX2+newY2*chunkWidth] = (byte)i;

	
}

public void SetUpBiomes() {
	// TODO Auto-generated method stub
	
	Terrain.Biomes = new int[Width][Height];
	int counter = MathUtils.random(3,5);
	int rand = 1;
	for(int x=0;x<Width;x++) {
		if(counter <= 0) {
			rand = MathUtils.random(1,3);
			counter = MathUtils.random(3,5);
		}
		if(rand == 1)
		Biomes[x][(Height/2)+1] = BiomeManager.BIOME_FOREST;
		else if(rand == 2)
		Biomes[x][(Height/2)+1] = BiomeManager.BIOME_ICE;
		else if(rand == 3) {
			Biomes[x][(Height/2)+1] = BiomeManager.BIOME_DESERT;
			//Biomes[x][(Height/2)] = BiomeManager.BIOME_DESERT;
			}
		counter--;
	}
	
	
}

}
