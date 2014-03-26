package com.jwatson.omnigame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.LeafSnow;
import com.jwatson.omnigame.InventoryObjects.SpiderLeaf;
import com.jwatson.omnigame.InventoryObjects.SpiderLeafSnow;

public class GrowableManager {
	
	

	long v_GrowTimer;
	long TicksElapsed = 0;
	
	protected List<Growable> Growables;
	
	
	public GrowableManager() {
		
		
		Growables = new ArrayList<Growable>();
	}
	
	ArrayList<Growable> finished = new ArrayList<Growable>();
	public void update() {
		
		for(Growable grow : Growables) {
			
			if(grow.instant) {
				while(grow.m_CurrentTick < grow.m_Size)
					grow.OnTick();
			
			}
			
			if(TicksElapsed%grow.m_TickSpeed == 0) { 
				if(grow.m_CurrentTick >= grow.m_Size) {		
					grow.OnRemove();
					finished.add(grow);
					continue;
				}
				grow.OnTick();
					
			}

			
		}
		Growables.removeAll(finished);
		TicksElapsed++;
		
	}
public	int leafCounter = 0;
public int nextLeafPos;
public void MakeLeaf(Growable growable, int x,int y, int x2, int y2, int min, int len, boolean isStart) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
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

		
		
		if(Terrain.CurrentTerrain.GetTile(x,y, x2, y2) != 0 && !isStart)
			return;
		if(len == 0)
			return;
		if(Terrain.Biomes[x][y] == BiomeManager.BIOME_ICE) {
			boolean mobFlag = false;
			if(growable.hasMobs) {
				if(growable.mobCounter < MapRenderer.Difficulty) {
					if(leafCounter == nextLeafPos) {
						nextLeafPos = MathUtils.random(nextLeafPos, nextLeafPos+2);
						mobFlag = true;
						growable.mobCounter++;
					}
				}
			}
			if(mobFlag)
			Terrain.CurrentTerrain.SetTileMap(x, y, x2, y2, (byte)SpiderLeafSnow.ID);
			else
				Terrain.CurrentTerrain.SetTileMap(x, y, x2, y2, (byte)LeafSnow.ID);
		}
		else {
			boolean mobFlag = false;
			if(growable.hasMobs) {
				if(growable.mobCounter < MapRenderer.Difficulty) {
					if(leafCounter == nextLeafPos) {
						nextLeafPos = MathUtils.random(nextLeafPos, nextLeafPos+2);
						mobFlag = true;
						growable.mobCounter++;
					}
				}
			}
			if(mobFlag)
			Terrain.CurrentTerrain.SetTileMap(x, y, x2, y2, (byte)SpiderLeaf.ID);
			else
				Terrain.CurrentTerrain.SetTileMap(x, y, x2, y2, (byte)Leaf.ID);
		}
		leafCounter++;
		growable.LeafPos.add(new Vector2((x*Terrain.CurrentTerrain.chunkWidth)+x2,(y*Terrain.CurrentTerrain.chunkHeight)+y2));
		len--;
		
			MakeLeaf(growable, x,y, x2, y2+1, min, len,false);
			MakeLeaf(growable, x,y, x2+1, y2, min, len,false);
			MakeLeaf(growable, x,y, x2, y2-1, min, len,false);
			MakeLeaf(growable, x,y, x2-1, y2, min, len,false);
		
	}

public void GrowInstant() {
	// TODO Auto-generated method stub
	for(Growable grow : Growables) {
		
		if(!grow.instant)
			continue;
		
			while(grow.m_CurrentTick < grow.m_Size)
				grow.OnTick();
		
		
		
		if(TicksElapsed%grow.m_TickSpeed == 0) { 
			if(grow.m_CurrentTick >= grow.m_Size) {		
				grow.OnRemove();
				finished.add(grow);
				continue;
			}
			grow.OnTick();
				
		}

		
	}
}

public Growable GetGrowable(int x,int y,int x2,int y2) {
	
	for(Growable growable : finished) {
		
		if(growable.x == x)
			if(growable.y == y)
				if(growable.x2 == x2)
					if(growable.y2 == y2-1)
						return growable;
		//Gdx.app.debug("", ""+growable.x+" " +growable.y+" " +growable.x2+ " "+growable.y2);
	}
	return null;
	
}

}
