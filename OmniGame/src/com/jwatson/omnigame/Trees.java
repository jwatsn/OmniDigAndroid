package com.jwatson.omnigame;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.InventoryObjects.BrownTree;
import com.jwatson.omnigame.InventoryObjects.Cactus;
import com.jwatson.omnigame.InventoryObjects.CactusTop;
import com.jwatson.omnigame.InventoryObjects.Grass;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.LeafSnow;



class Tree {
	static Growable GetBrownTree(GrowableManager man) {
		Growable BrownTree = new Growable(3, man) {
			
			@Override
			public void OnTick() {
				m_CurrentTick++;
				

				
				
				this.y2++;		
				
				if(this.y2 >= Terrain.CurrentTerrain.chunkHeight) {
					this.y++;
					this.y2 = 0;
				}
				
				
				if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_DESERT)					
					Terrain.CurrentTerrain.SetTileMap(this.x, this.y, this.x2, this.y2, (byte)Cactus.ID);
				else
					Terrain.CurrentTerrain.SetTileMap(this.x, this.y, this.x2, this.y2, (byte)Trees.BROWN_TREE);
				
				if(this.hasLeaf) {
				if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_FOREST)
					Terrain.CurrentTerrain.SetTileMap(this.x, this.y, this.x2, this.y2+1, (byte)LeafSnow.ID);
				else if(Terrain.Biomes[this.x][this.y] == BiomeManager.BIOME_DESERT)
					Terrain.CurrentTerrain.SetTileMap(this.x, this.y, this.x2, this.y2+1, (byte)CactusTop.ID);
				else {
					Terrain.CurrentTerrain.SetTileMap(this.x, this.y, this.x2, this.y2+1, (byte)Leaf.ID);
				}
				}
				
				Terrain.CurrentTerrain.light.refreshMap();
			}
			
			@Override
			public void OnRemove() {
				// TODO Auto-generated method stub
				this.manager.leafCounter = 0;
				this.manager.nextLeafPos = MathUtils.random(3, 5);
				if(Terrain.Biomes[this.x][this.y] != BiomeManager.BIOME_DESERT)
				this.manager.MakeLeaf(this,x, y, x2, y2+1, y2, 3, true);
				LeafPos.add(new Vector2(x*Terrain.chunkWidth+x2,y*Terrain.chunkHeight+(y2+1)));
				Terrain.CurrentTerrain.light.refreshMap();
			}
		};
		return BrownTree;
	}
}



public class Trees {

	
	GrowableManager manager;
	
	Growable BrownTree;
	public static int BROWN_TREE;
	//public static Texture BROWN_TREE_TEXTURE;
	
	public Trees(GrowableManager manager) {
		
		
		
		
		this.manager = manager;

		
		//manager.Growables.add(tree);
	}
	
	
	
	public boolean PlantTree(int id,int x, int y, int x2, int y2, boolean instant) {
		
		

		TerrainChunk ch = Terrain.CurrentTerrain.GetChunkByID(x, y);
		
		
		if(ch.TerrainMap[x2+y2*ch.Width] == Grass.ID) {
			
			if(id == Trees.BROWN_TREE) {
			Growable tree = Tree.GetBrownTree(manager);
			tree.set(x,y,x2,y2);
			tree.instant = instant;
			tree.id = manager.Growables.size();
			if(Terrain.Biomes[x][y] == BiomeManager.BIOME_DESERT) {
				tree.m_Size = (int) (2+Math.random()*4);
				tree.hasLeaf= false;
				if(MathUtils.random(100) < 40)
					tree.hasLeaf = true;
					
			}
			else
				tree.m_Size = (int) (5+Math.random()*6);
			if(instant)
				tree.speed = 0.01f;
			World.CurrentWorld.UpdateList.add(tree);
			return true;
			}
			
		}
		return false;
	}
	
	
	
}
