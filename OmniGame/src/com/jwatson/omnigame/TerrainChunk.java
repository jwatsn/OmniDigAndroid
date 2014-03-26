package com.jwatson.omnigame;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.ChunkWriter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.InventoryObjects.BreakablePot;
import com.jwatson.omnigame.InventoryObjects.BrownTree;
import com.jwatson.omnigame.InventoryObjects.BrownTreeSeed;
import com.jwatson.omnigame.InventoryObjects.EnemyTent;
import com.jwatson.omnigame.InventoryObjects.Grass;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.LeafSnow;
import com.jwatson.omnigame.InventoryObjects.Snow;
import com.jwatson.omnigame.InventoryObjects.SpiderLeaf;
import com.jwatson.omnigame.InventoryObjects.SpiderLeafSnow;
import com.jwatson.omnigame.InventoryObjects.TopSand1;
import com.jwatson.omnigame.InventoryObjects.Water;
import com.jwatson.omnigame.ai.Pig;
import com.jwatson.omnigame.ai.weak_raider;
import com.jwatson.omnigame.graphics.CustomBatch;

public class TerrainChunk {
	int x;
	int y;
	public int Width;
	public int Height;
	int ID = -1;
	
	static int TotalChunks;
	
	Terrain parent;
	public boolean isActive;
	public byte[] TerrainMap;
	public float[][] LiquidMap;
	public byte[][] FlowMap;
	public byte[][] PipeMap;
	public byte[][] DirectionMap;
	public byte[] stateMap;
	public float[] damageMap;
	
	public int biome;
	public Vector2 biome_spread;
	
	public byte[] BakedLightMap;
	public byte[] origBakedLightMap;
	byte[] LightMap;
	byte[] LightMap2;
	//ByteBuffer LightMap;
	Rectangle bounds;
	
	
	int tileHP;
	int lastX;
	int lastY;
	
	int CenterX;
	int CenterY;
	
	boolean isUpdating;
	private ShaderProgram shader;
	Color col;
	public TerrainChunk(int x, int y, int Width, int Height, boolean isLoading) {
		this.x = x;
		this.y = y;
		
		CenterX = (Width*x) + (Width/2);
		CenterY = (Height*y) + (Height/2);
		
		parent = Terrain.CurrentTerrain;
		
		this.Width = Width;
		this.Height = Height;
		
		
		TerrainMap = new byte[Width*Height];
		LiquidMap = new float[Width*2][Height*2];
		
		FlowMap = new byte[Width*2][Height*2];
		PipeMap = new byte[Width][Height];
		DirectionMap = new byte[Width*2][Height*2];
		//LightMap = ByteBuffer.allocate(Width * Height);
		LightMap2 = new byte[Width*Height];
		LightMap = new byte[Width*Height];
		BakedLightMap = new byte[Width*Height];
		origBakedLightMap = new byte[Width*Height];
		stateMap = new byte[Width*Height];
		damageMap = new float[Width*Height];
		bounds = new Rectangle(x*Width,y*Height,Width,Height);
		col = new Color(Color.WHITE);
		Terrain.TerrainChunks[this.x+this.y*parent.Width] = this;

		
	}
	
	
	public TerrainChunk(int x, int y, int Width, int Height, Terrain p) {
		this.x = x;
		this.y = y;
		
		CenterX = (Width*x) + (Width/2);
		CenterY = (Height*y) + (Height/2);
		
		col = new Color(Color.WHITE);
		
		this.Width = Width;
		this.Height = Height;
		
		TerrainMap = new byte[Width*Height];
		PipeMap = new byte[Width][Height];
		LiquidMap = new float[Width*2][Height*2];
		FlowMap = new byte[Width*2][Height*2];
		DirectionMap = new byte[Width*2][Height*2];
		LightMap2 = new byte[Width*Height];
		LightMap = new byte[Width*Height];
		//LightMap = ByteBuffer.allocate(Width * Height);
		BakedLightMap = new byte[Width*Height];
		origBakedLightMap = new byte[Width*Height];
		parent = p;
		bounds = new Rectangle(x*Width,y*Height,Width,Height);
		stateMap = new byte[Width*Height];
		damageMap = new float[Width*Height];
		MapSetup();
		Terrain.TerrainChunks[this.x+this.y*parent.Width] = this;
		if(y > parent.Height/2)
		for(int i = 0; i < Width; i++) {
			parent.light.diffuseBottom(parent, this, x, y, i, Height-1, (byte)15);
		}
		
		//Activate();
		//parent.light.MakeLightMap();
		PlantTrees();
		shader = MapRenderer.CurrentRenderer.shader;
	}
	
	void PlantTrees() {
		
		// TODO Auto-generated method stub
		for(int x=0; x< Width; x++) {
			for(int y=0; y<Height; y++) {
				if(TerrainMap[x + (y*Width)] == (byte)BrownTreeSeed.ID) {


					TerrainMap[x + (y*Width)] = (byte)Grass.ID;
					MapRenderer.CurrentRenderer.TreeManager.PlantTree((byte)Trees.BROWN_TREE, this.x, this.y, x, y, true);
					MapRenderer.CurrentRenderer.GrowManager.GrowInstant();
					if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_ICE)
						TerrainMap[x + (y*Width)] = (byte)Snow.ID;
					else if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_DESERT) {
						TerrainMap[x + (y*Width)] = (byte) TopSand1.ID;
					}
				}
				
				if(TerrainMap[x + (y*Width)] == (byte)EnemyTent.ID) {
					
					World.CurrentWorld.pendingWorldObjs.add(new weak_raider(World.CurrentWorld, this.x*Width + x, this.y*Height+y, 1, 1));
				}
			}
		}
	}

	public boolean isEmpty(int x, int y) {
		if(TerrainMap[x+(y*Width)] > 0)
			return false;
		else
			return true;
	}
	
	void MapSetup() {
		
		Random rand = new Random();
		int randnum;
		Inventory inv = World.CurrentWorld.InvBag;
		float depth = ((float)this.y / (float)parent.Height) * 100f;
		int sect = (int)depth/5;
		int skyline = (parent.Height/2)+1;
		
		boolean flag = false;
//		TerrainChunk left = Terrain.TerrainChunks[(this.x-1)+this.y*Terrain.Width];
//		TerrainChunk right = Terrain.TerrainChunks[(this.x-1)+this.y*Terrain.Width];
//		TerrainChunk up = Terrain.TerrainChunks[(this.x)+(this.y+1)*Terrain.Width];
//		TerrainChunk down = Terrain.TerrainChunks[(this.x)+(this.y+1)*Terrain.Width];
		if(Terrain.Biomes == null)
			parent.SetUpBiomes();
		if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_FOREST) {
			
			
			
			
			byte[] Grass = new byte[2];
			Grass[0] = (byte)Item.getId("MAT_Grass");
			Grass[1] = (byte)Item.getId("MAT_Grass2");
			
			byte[] Dirt = new byte[2];
			Dirt[0] = (byte)Item.getId("MAT_Dirt");
			Dirt[1] = (byte)Item.getId("MAT_Dirt2");
			
			byte[][] tmap = BiomeManager.GetForestBiome(Width, Height, 20, Grass, Dirt,(byte)inv.GetItemID("DEP_Seed_BrownTree"), 6, 5, new Vector3(inv.GetItemID("MAT_Stone"), 3,3));

			if(MathUtils.random(100) < 50){
				biome = BiomeManager.BIOME_FOREST;
				for(int xx=-2; xx<2; xx++) {
					if(Terrain.TerrainChunks[(this.x+xx)+this.y*Terrain.Width] != null)
					if(Terrain.TerrainChunks[(this.x+xx)+this.y*Terrain.Width].biome == BiomeManager.BIOME_FOREST_CAMP)
						flag = true;
				}
				if(!flag) {
					tmap = BiomeManager.GetForestBiome_Camp(Width, Height, 20, Grass, Dirt,(byte)inv.GetItemID("DEP_Seed_BrownTree"), 6, 5,MathUtils.random(1, 5), new Vector3(inv.GetItemID("MAT_Stone"), 3,3));
					biome = BiomeManager.BIOME_FOREST_CAMP;
				}
			}
			for(int y=0; y<Height; y++) {
				for(int x=0; x<Width; x++) {
					TerrainMap[x+y*Width] = tmap[x][y];
				}
			}
			
			
		}
		else if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_ICE) {
			
			
			
			
			byte[] Grass = new byte[2];
			Grass[0] = (byte)Item.getId("MAT_Ice_Top1");
			Grass[1] = (byte)Item.getId("MAT_Ice_Top2");
			
			byte[] Dirt = new byte[2];
			Dirt[0] = (byte)Item.getId("MAT_Dirt");
			Dirt[1] = (byte)Item.getId("MAT_Dirt2");
			
			byte[][] tmap = BiomeManager.GetForestBiomeSnow(Width, Height, 20, Grass, Dirt,(byte)inv.GetItemID("DEP_Seed_BrownTree"), 6, 5, new Vector3(inv.GetItemID("MAT_Stone"), 3,3));

			
			for(int y=0; y<Height; y++) {
				for(int x=0; x<Width; x++) {
					TerrainMap[x+y*Width] = tmap[x][y];
				}
			}
			
			
		}
else if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_DESERT) {
			
			
			
			
			byte[] Grass = new byte[2];
			Grass[0] = (byte)Item.getId("MAT_Sand_Top1");
			Grass[1] = (byte)Item.getId("MAT_Sand_Top2");
			
			byte[] Dirt = new byte[2];
			Dirt[0] = (byte)Item.getId("MAT_Dirt");
			Dirt[1] = (byte)Item.getId("MAT_Dirt2");
			
			byte[][] tmap = BiomeManager.GetTopSideBiome(Width, Height, 20, Grass, Dirt,(byte)inv.GetItemID("DEP_Seed_BrownTree"), 5, 5, new Vector3(inv.GetItemID("MAT_Stone"), 3,3));

			
			for(int y=0; y<Height; y++) {
				for(int x=0; x<Width; x++) {
					TerrainMap[x+y*Width] = tmap[x][y];
					//Gdx.app.debug("", ""+tmap[x][y]);
				}
			}
			
			
		}
		if(depth <= 50) {
			byte[][] tmap = null;
			if((skyline - this.y) < 2) {
				
			tmap = BiomeManager.GetUndergroundBiome(Width, Height,40, (byte)inv.GetItemID("MAT_Dirt"), new Vector3(inv.GetItemID("MAT_Stone"),3,5),new Vector3(inv.GetItemID("MAT_Sand"),4,5),new Vector3(inv.GetItemID("MAT_Coal"),1,4));
			}			
			else if((skyline - this.y) < 4)
				tmap = BiomeManager.GetUndergroundBiome(Width, Height,40, (byte)inv.GetItemID("MAT_Dirt"), new Vector3(inv.GetItemID("MAT_Stone"),3,5),new Vector3(inv.GetItemID("MAT_Sand"),4,5),new Vector3(inv.GetItemID("MAT_Coal"),1,4),new Vector3(inv.GetItemID("MAT_Copper"),1,4));
			else {
				tmap = BiomeManager.GetUndergroundBiome(Width, Height,40, (byte)inv.GetItemID("MAT_Dirt"), new Vector3(inv.GetItemID("MAT_Stone"),5,6),new Vector3(inv.GetItemID("MAT_Sand"),4,5),new Vector3(inv.GetItemID("MAT_Coal"),1,4),new Vector3(inv.GetItemID("MAT_Copper"),2,4),new Vector3(inv.GetItemID("MAT_Iron"),1,4));
			}
			for(int y=0; y<Height; y++) {
				for(int x=0; x<Width; x++) {
					
					if(tmap[x][y] == 0)
					if(MathUtils.random(100) < 10) {
						BiomeManager.MakeLiquidPatch(LiquidMap,tmap, x, y, 5, 10);
					}
					TerrainMap[x+y*Width] = tmap[x][y];
				}
			}
			biome = BiomeManager.BIOME_UNDERGROUND;
		}
		
		
	}
	
	public boolean CreateBlock(Bob bob,int x2, int y2, int type) {
		
		parent.map.world.MainBob.touchFlag = true;
//		if(TerrainMap[x2+(y2*Width)] != 0 && Item.Items[TerrainMap[x2+(y2*Width)]].type != InvObject.Type.FOLIAGE)
//			return false;
		
		//Gdx.app.debug(""+x2+ " " + y2, ""+(int)(bob.pos.x+0.5f)+ " " + (int)(bob.pos.y+0.5f));
		if(TerrainMap[x2+(y2*Width)] == 0 || Item.Items[TerrainMap[x2+(y2*Width)]].type == InvObject.Type.FOLIAGE) {
		TerrainMap[x2+(y2*Width)] = (byte)type;
		
		return true;
		}
		else
			return false;

	}
	
	public void DeleteBlock2(int x, int y) {
		
		
//		MapRenderer.CurrentRenderer.cache.clear();
		TerrainMap[x + (y*Width)] = 0;
		parent.sky = -1;
//		for(TerrainChunk c : parent.ActiveChunks.values()) {
//			c.UpdateCache(MapRenderer.CurrentRenderer.cache);
//			
//		}
	}
	
	public boolean DeleteBlock(int x, int y, float f) {
		
		int X = x%Width;
		int Y = y%Height;
		
		if(TerrainMap[X + (Y*Width)]==0)
			return false;
		
		if(!parent.isBreakable(this.x, this.y, X, Y))
			return false;
		
		
		InvObject block = Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]];
		if(f < 200) {
		if(TerrainMap[X + Y*Width] > 0) {
			int tile = parent.GetTile(x,y+1);
			if(tile > 0) {
		InvObject abovebl = Inventory.CurrentInventory.Items[tile];
		if(abovebl.needsGround)
			return false;
		if(abovebl.type == InvObject.Type.FOLIAGE)
			if((damageMap[X+Y*Width]/block.HP)*6 >= 2)
			Terrain.SetTile(x, y+1, 0);
		
		
			}
		}
		
		
		if(Y < Height-1) {
			if(TerrainMap[X + ((Y+1)*Width)] > 0)
			if(Inventory.CurrentInventory.Items[TerrainMap[X + ((Y+1)*Width)]].type == InvObject.Type.WOOD && Inventory.CurrentInventory.Items[TerrainMap[X + ((Y)*Width)]].type != InvObject.Type.WOOD)
				return false;
		}
		}
//		Vector3 hp = parent.map.world.damagedBlocks.get(x + (y*Width*parent.Width));
		damageMap[X+Y*Width] += f;
//		if(parent.map.world.damagedBlocks.get(x + (y*Width*parent.Width)) == null) {
//			hp = new Vector3(x,y,10f*block.HP);
//			if(block.type == InvObject.Type.WOOD) {
//				int treelength = GetTreeLength(X,Y);
//				hp.z += treelength*10;
//			}
			
//		}
		int cursel = Inventory.CurrentInventory.currSelected;
		float pickaxeStr = ((float)Item.Items[Inventory.CurrentInventory.BagItem[cursel][0]].MineLevel * 0.5f);
		if(damageMap[X+Y*Width] >= block.HP - pickaxeStr) {
			
			if(block.type == InvObject.Type.WOOD) {
				BreakTree(X, Y);
			}
			
			TerrainMap[X + (Y*Width)] = 0;
//			MapRenderer.CurrentRenderer.cache.clear();
			parent.map.world.damagedBlocks.remove(x + (y*Width*parent.Width));
			long delay = System.currentTimeMillis();
			//parent.light.FloodLight(x, y, 5);
			//Gdx.app.debug("", "TOOKMS "+(System.currentTimeMillis()-delay));
//		for(TerrainChunk ch : parent.ActiveChunks.values()) {
//			
//			ch.UpdateCache(MapRenderer.CurrentRenderer.cache);
//		}
//			
			damageMap[X+Y*Width]  = 0;
			parent.light.spreadLight(null);
			return true;
		} else {
			
			HitTree(X, Y);
			
		}
		return false;
	}
	
	private int GetTreeLength(int X, int Y) {
		// TODO Auto-generated method stub
		int len = 0;
		 while(true) {
			 
			 if(Y >= Height)
				 return len;
			 if(TerrainMap[X + (Y*Width)] <= 0)
				 return len;
			 if(Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]].type != InvObject.Type.WOOD)
				 return len;
			 
			 len++;
			 
			 Y++;
			 
		 }
	}
	Vector2 LeafPos = new Vector2();
	void HitTree(int X, int Y) {
		while(true) {
			
			if(TerrainMap[X + (Y*Width)] <= 0)
				return;
			
			InvObject block = Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]];
			
			if(block.type != InvObject.Type.WOOD && block.type != InvObject.Type.LEAF)
				return;
			
			if(block.type == InvObject.Type.LEAF) {
				LeafPos.set(X,Y);
				Growable grow = MapRenderer.CurrentRenderer.GrowManager.GetGrowable(this.x, this.y, X, Y);
				if(grow == null)
					return;
				
				if(grow.mobCounter > 0) {
					for(Vector2 pos : grow.LeafPos) {
						int id = Terrain.GetTile(pos.x, pos.y);
						if(id == SpiderLeaf.ID) {
							grow.mobCounter--;
							Terrain.SetTile(pos.x, pos.y, (byte)Leaf.ID);
							Pig spider = new Pig(World.CurrentWorld,(int)pos.x,(int)pos.y,1,1);
							float dX = (Bob.CurrentBob.pos.x+0.5f) - (pos.x+0.5f);
							if(dX > 0)
								spider.direction = Bob.RIGHT;
							else
								spider.direction = Bob.LEFT;
							World.CurrentWorld.pendingWorldObjs.add(spider);
							return;
						}
						else if(id == SpiderLeafSnow.ID) {
							grow.mobCounter--;
							Terrain.SetTile(pos.x, pos.y, (byte)LeafSnow.ID);
							Pig spider = new Pig(World.CurrentWorld,(int)pos.x,(int)pos.y,1,1);
							float dX = (Bob.CurrentBob.pos.x+0.5f) - (pos.x+0.5f);
							if(dX > 0)
								spider.direction = Bob.RIGHT;
							else
								spider.direction = Bob.LEFT;
							World.CurrentWorld.pendingWorldObjs.add(spider);
							return;
						}
					}
				}
					
				return;
				}
				

			
			Y++;
		}
	}
	
	
    public void ShakeTree(int X, int Y) {
		while(true) {
			
			
			if(TerrainMap[X + (Y*Width)] <= 0)
				return;
			
			InvObject block = Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]];
			
			if(block.type != InvObject.Type.WOOD && block.type != InvObject.Type.LEAF)
				return;
			
			if(block.type == InvObject.Type.LEAF) {
				LeafPos.set(X,Y);
				Growable grow = MapRenderer.CurrentRenderer.GrowManager.GetGrowable(this.x, this.y, X, Y);
				if(grow == null)
					return;
				
				
				if(grow.mobCounter > 0) {
					for(Vector2 pos : grow.LeafPos) {
						int id = Terrain.GetTile(pos.x, pos.y);
						if(id == SpiderLeaf.ID) {
							if(MathUtils.random(0,100) <= 16) {
							grow.mobCounter--;
							Terrain.SetTile(pos.x, pos.y, (byte)Leaf.ID);
							Pig spider = new Pig(World.CurrentWorld,(int)pos.x,(int)pos.y,1,1);

							
							spider.vel.x = Bob.CurrentBob.vel.x + Bob.CurrentBob.dir*0.4f;
							
							World.CurrentWorld.pendingWorldObjs.add(spider);
							
							return;
							}
						}
						else if(id == SpiderLeafSnow.ID) {
							if(MathUtils.random(0,100) <= 16) {
								grow.mobCounter--;
								Terrain.SetTile(pos.x, pos.y, (byte)LeafSnow.ID);
								Pig spider = new Pig(World.CurrentWorld,(int)pos.x,(int)pos.y,1,1);

								
								spider.vel.x = Bob.CurrentBob.vel.x + Bob.CurrentBob.dir*0.4f;
								
								World.CurrentWorld.pendingWorldObjs.add(spider);
								
								return;
							}
						}
					}
				}
					
				return;
				}
				

			
			Y++;
		}
	}
	
	void BreakTree(int X, int Y) {
		
		
		
		while(true) {
			
			if(TerrainMap[X + (Y*Width)] <= 0)
				return;
			
			InvObject block = Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]];
			
			if(block.type != InvObject.Type.WOOD && block.type != InvObject.Type.LEAF)
				return;
			
			if(block.type == InvObject.Type.LEAF) {
				LeafPos.set(X,Y);
				Growable grow = MapRenderer.CurrentRenderer.GrowManager.GetGrowable(this.x, this.y, X, Y);
				if(grow == null)
					return;
				for(Vector2 leafpos : grow.LeafPos) {
					Terrain.SetTile((int)leafpos.x, (int)leafpos.y, 0);
					
					
				}
				return;
			}
			else {
				WorldObj obj = new WorldObj(World.CurrentWorld,(this.x*Width+ X)+0.5f, (this.y*Height+Y)+0.5f, 0.5f,0.5f, block.InvObjID, 1);
				
				if(MathUtils.randomBoolean())
					obj.vel.x = MathUtils.random(1,3);
				else
					obj.vel.x = -MathUtils.random(1,3);
				
				World.CurrentWorld.pendingWorldObjs.add(obj);
			}
			TerrainMap[X + (Y*Width)] = 0;
			
			Y++;
		}
		
	}


	private void BreakLeaf(int X, int Y) {
		// TODO Auto-generated method stub
		
		if(TerrainMap[X + (Y*Width)] <= 0)
			return;
		
		InvObject block = Inventory.CurrentInventory.Items[TerrainMap[X + (Y*Width)]];
		
		if(block.InvObjID != Leaf.ID)
			return;
		
		float dst = LeafPos.dst(X,Y);
		
		if(dst > 3)
			return;
		
		TerrainMap[X + (Y*Width)] = 0;
		
		BreakLeaf(X,Y+1);
		BreakLeaf(X+1,Y);
		BreakLeaf(X,Y-1);
		BreakLeaf(X-1,Y);
	}


	public void LoadTileMap(int[][] map) {
		
		for(int y=0; y<Height; y++) {
			for(int x=0; x<Width; x++) {
				
				TerrainMap[x + (y*Width)] = (byte)map[x][y];
			}
		}
	}
	
	public void UpdateCacheOld(SpriteCache cache) {
		if(TerrainMap == null)
			return;
		
		cache.beginCache();
		for(int y=0; y<Height; y++) {
			for(int x=0; x<Width; x++) {
				int i = TerrainMap[x + (y*Width)];
				if(i>0)
				{
					cache.add(new TextureRegion(World.CurrentWorld.InvBag.GetItem(i).thumbnail), this.x*parent.chunkWidth+x,this.y*parent.chunkHeight+y,1,1);
				}
			}
		}
		ID = cache.endCache();
	}
	
//	public void BakeLightMap() {
//		if(y > parent.Height/2)
//		for(int i = 0; i < Width; i++) {
//			parent.light.diffuseBottom(parent, this, x, y, i, Height-1, (byte)15);
//		}
//		
//			
//			
//			
//
//	}
	
	public void Activate() {
		
		/*
		cache.beginCache();
		for(int y=0; y<Height; y++) {
			for(int x=0; x<Width; x++) {
				int i = TerrainMap[x + (y*Width)];
				if(i>0)
				{
					cache.add(new TextureRegion(World.CurrentWorld.InvBag.GetItem(i).thumbnail), this.x*parent.chunkWidth+x,this.y*parent.chunkHeight+y,1,1);
				}
			}
		}
		ID = cache.endCache();
		*/
		isActive=true;
		TerrainChunk.TotalChunks++;
		parent.ActiveChunks.add(this);
		parent.updateBrightness();
		parent.light.spreadLight(this);
		//parent.CreateSkyCache();
		
	}
	
	public void DeActivate() {
		
		isActive=false;
		parent.ActiveChunks.remove(this);
		
//		TerrainChunk.TotalChunks--;
	}
	Rectangle rect = new Rectangle();
	
	Vector3 culling = new Vector3();
	float lastDamaged;
	public void render(CustomBatch batch) {
//		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		culling.set(this.x*Width + 10, this.y*Width + 10, 0);
		
		if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10))
			return;
		
	int bb = 0;
		
//		batch.begin();
		for(int i=0; i <Width*Height; i++) {
			
			
			
			
			
			int x = (i%Width);
			int y = (i/Width);
			
			
			int x2 = this.x * Width + x;
			int y2 = this.y * Height + y;
			
			culling.set(x2+0.5f, y2+0.5f, 0);
			if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 0.5f))
				continue;

			

			
//					if(LightMap.get(x+(y*Width)) != LightMap2[x+y*Width])
//						LightMap2[x+y*Width] = LightMap.get(x+(y*Width));
				
//					if(LightMap[x+(y*Width)] <= 0) {
//					batch.setColor(Color.BLACK);
//					batch.draw(Inventory.CurrentInventory.Items[TerrainMap[i]].thumbnail, (this.x*Width)+x,(this.y*Height) + y,1,1);
//					}
//					else {
					int b = LightMap2[x+(y*Width)];
					float a2 = (float)b/15f;

					
					float a = a2;
//					if(a <= 0) {
//						LightMap2[x+y*Width]=(byte)15;
//						a= 0;
//					}
//					if(a>=1f) {
//						LightMap2[x+y*Width]=(byte)0;
//						a=1f;
//						
//						if(this.y <= parent.Height/2)
//							continue;
//					}
					
					if(a<=0) {
						if(this.y <= parent.Height/2)
						continue;
					}
					
					
//					parent.shader.setUniformf("brightness", 1-a2);
						
						
//						
//	
						
						//Gdx.app.debug("", ""+a2);
						//parent.shader.setUniformf("brightness", (1-a2));
						//parent.shader.setAttributef("brightness", 0.5f, 0, 0, 0);
						//
						
						
						if(this.y <= parent.Height/2) {
							
							if(!parent.isSolid(this.x, this.y, x, y) || Item.Items[Terrain.GetTile((this.x*Width)+x, (this.y*Height) + y)].type == InvObject.Type.PLATFORM || damageMap[x+y*Width] > 0) {
								

								parent.bb++;
								batch.draw(parent.CaveWall,a, (this.x*Width)+x,(this.y*Height) + y,1,1);
							}
						}
						if(TerrainMap[x+y*Width] > 0) {
							InvObject item = Inventory.CurrentInventory.Items[TerrainMap[i]];
							if(item.type == InvObject.Type.FOLIAGE)
								continue;
						int frame = item.frame;
						if(!item.Animated) {
							if(damageMap[x+y*Width] > 0) {
								InvObject blk = Item.Items[Terrain.GetTile(x2, y2)];
								if(blk != null) {
								int dmg =  (int)((damageMap[x+y*Width] / blk.HP) * 7); 
								if(dmg > 7)
									dmg = 7;
								
								
								if(blk.type == InvObject.Type.WOOD) {
									dmg =  (int)((damageMap[x+y*Width] / blk.HP) * 4); 
									if(dmg > 4)
										dmg = 4;
									parent.bb++;
									batch.draw(item.thumbnail,parent.breakWoodAnim[dmg],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
								}
								else {
//								parent.breakAnim[0].bind(1);
//								parent.breakAnim[0].getTextureData().
//								parent.shader2.setUniformi("u_texture2",1);
//								parent.breakAnim[0].bind(0);	
									parent.bb++;
									batch.draw(item.thumbnail,parent.breakAnim[dmg],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
								}
								
								//batch.setShader(parent.shader);
								
									int xx = this.x * Width + x;
									int yy = this.y * Width + y;
									if((int)Bob.CurrentBob.lastDamaged.x != xx || (int)Bob.CurrentBob.lastDamaged.y != yy)
									damageMap[x+y*Width] -= Gdx.graphics.getDeltaTime() * 0.50f;

								}
								
								
							}
							else {
								parent.bb++;
								if(item.type == InvObject.Type.PIPE) {
									
									int xx = this.x * Width + x;
									int yy = this.y * Width + y;
									
									boolean top = false;
									boolean bottom = false;
									boolean right = false;
									boolean left = false;
									
									if(Terrain.GetTile(xx, yy+1) > 0)
									top = (Item.Items[Terrain.GetTile(xx, yy+1)].type == InvObject.Type.PIPE);
									if(Terrain.GetTile(xx, yy-1) > 0)
									bottom = (Item.Items[Terrain.GetTile(xx, yy-1)].type == InvObject.Type.PIPE);
									if(Terrain.GetTile(xx+1, yy) > 0)
									right = (Item.Items[Terrain.GetTile(xx+1, yy)].type == InvObject.Type.PIPE);
									if(Terrain.GetTile(xx-1, yy) > 0)
									left = (Item.Items[Terrain.GetTile(xx-1, yy)].type == InvObject.Type.PIPE);
									
									
									if(!top && !bottom && !right && !left) {
										batch.draw(item.thumbnail_array[0],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
										batch.draw(item.thumbnail_array[1],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
									}
									if(left)
										batch.draw(item.thumbnail_array[0],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
									if(right)
										batch.draw(item.thumbnail_array[1],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
									if(top)
										batch.draw(item.thumbnail_array[2],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
									if(bottom)
										batch.draw(item.thumbnail_array[3],a, (this.x*Width)+x,(this.y*Height) + y,1,1);
								}
								else
								batch.draw(item.thumbnail,a, (this.x*Width)+x,(this.y*Height) + y,1,1);
							}

						}
						else {
							if(!item.manualAnimControl) {
							batch.draw(item.anim.getKeyFrame(World.CurrentWorld.animTime),a, (this.x*Width)+x,(this.y*Height) + y,1,1);
							parent.bb++;
							}
							else {
								float posx = (this.x*Width)+x;
								float posy = (this.y*Height) + y;
								int frame2 = stateMap[i];
								if(item.type == InvObject.Type.STATE_CONTROLLED_COLLISION) {
									if(frame2 > 0) {
									if(frame2 == 2)
										posx -= 0.25f;
									else
										posx += 0.25f;
									
									
									frame2 = 1;
									}
								}

								batch.draw(item.anim.getKeyFrame(frame2),a, posx,posy,1,1);
								parent.bb++;
							}
						}

						
						
//						col.a = 1+a;
//						batch.setColor(col);
//						batch.draw(parent.dark, (this.x*Width)+x,(this.y*Height) + y,1,1);
					//}
				}
			

			}
		
		
		//batch.end();
		
	}
	Rectangle r = new Rectangle();
	public void render2(SpriteCache cache) {
		if(isUpdating)
			return;
		
		
		r.set(this.x*Width,this.y*Height,Width,Height);
		if(World.CurrentWorld.r[8].overlaps(r))
		cache.draw(ID);
		
		if(Bob.CurrentBob.isMining) {
			
			if(tileHP <= 0)
				return;
			
		}
	}
}
