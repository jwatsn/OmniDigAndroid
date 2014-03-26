package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.jwatson.omnigame.InventoryObjects.Water;
import com.jwatson.omnigame.graphics.CustomBatch;

public class LiquidManager {

	SpriteBatch batch;
	float liqmap2[][];
	private float LiquidTimer;
//	UpdatableItem update = new UpdatableItem(0.1f) {
//		
//		@Override
//		public void update(float delta) {
//			update2(delta);
//			
//		}
//	};
	public LiquidManager() {
		// TODO Auto-generated constructor stub
		liqmap2 = new float[Terrain.CurrentTerrain.chunkWidth*2][Terrain.CurrentTerrain.chunkHeight];
		//World.CurrentWorld.UpdateList.add(update);
	}
	
	


	
	void update2(float delta) {
		if(LiquidTimer > 0.2f) {
		for(int ii = 0; ii<Terrain.CurrentTerrain.ActiveChunks.size(); ii++) {
			TerrainChunk ch = Terrain.CurrentTerrain.ActiveChunks.get(ii);
			if(ch == null)
				continue;
			if(!ch.isActive)
				continue;
			
			culling.set(ch.x*ch.Width + 10, ch.y*ch.Width + 10, 0);
			
			if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10))
				return;
			
			for(int x = 0; x < Terrain.CurrentTerrain.chunkWidth*2; x++) {
				for(int y = 0; y < Terrain.CurrentTerrain.chunkHeight*2; y++) {
					
					if(ch.FlowMap[x][y] > 0) {
						//found water
						boolean flowflag = false;
						//check down first
						
						if(!isSolid(ch.x, ch.y, x, y-1)) {
							//success
							if(Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x, y-1) < 5) {
								
								AddLiquid(ch.x,ch.y,x,y-1,2);
								ch.LiquidMap[x][y] -= 2;
								

								
								Terrain.CurrentTerrain.SetLiquidDirection(ch.x, ch.y, x, y-1, (byte)1);
								flowflag = true;
							}
							
						}
						else {
							
						}
						
						
						//Check right
						if(!flowflag) {
							ch.DirectionMap[x][y] = 0;
						if(!isSolid(ch.x, ch.y, x+1, y)) {
							//success
//							if(ch.LiquidMap[x][y]-1 > 0)
							if(ch.LiquidMap[x][y] > Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x+1, y)) {
								if(ch.LiquidMap[x][y] - Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x+1, y) <= 2) {
									float equalizer = (ch.LiquidMap[x][y] - Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x+1, y))/2f;
									ch.LiquidMap[x][y] -= equalizer;
									AddLiquid(ch.x,ch.y,x+1,y,equalizer);
								}else {
								ch.LiquidMap[x][y] -= 2;
								AddLiquid(ch.x,ch.y,x+1,y,2);
								}
								
							}
							else {
								
							}
							
						}
						//check left
						if(!isSolid(ch.x, ch.y, x-1, y)) {
							//success
							
							if(ch.LiquidMap[x][y]-1 > 0)
							if(ch.LiquidMap[x][y] > Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x-1, y)) {
							if(ch.LiquidMap[x][y] - Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x-1, y) <= 2) {
									float equalizer = (ch.LiquidMap[x][y] - Terrain.CurrentTerrain.GetLiquid(ch.x, ch.y, x-1, y))/2f;
									ch.LiquidMap[x][y] -= equalizer;
									AddLiquid(ch.x,ch.y,x-1,y,equalizer);
								}else {
								ch.LiquidMap[x][y] -= 2;
								AddLiquid(ch.x,ch.y,x-1,y,2);
								}
							}
							
						}
						}
						
					}
					
					
				}
			}
			
			
		}
		LiquidTimer = 0;
		}
		LiquidTimer += delta;
	}
	Vector3 culling = new Vector3();
	public void render(CustomBatch batch2) {
		batch2.setColor(Color.WHITE);
		for(TerrainChunk ch : Terrain.CurrentTerrain.ActiveChunks) {
			
			culling.set(ch.x*ch.Width + ch.Width/2, ch.y*ch.Width + ch.Width/2, 0);
			
			if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 10))
				return;
				
				
				for(int x =0; x < Terrain.CurrentTerrain.chunkWidth*2; x++) {
					for(int y = 0; y < Terrain.CurrentTerrain.chunkHeight*2; y++) {
						if(ch.LiquidMap[x][y] > 0) {
							ch.FlowMap[x][y] = 1;
							
						float amt = (float)((ch.LiquidMap[x][y]/5f)*0.5f);
						if(amt > 0.5f) {
							amt = 0.5f;
						}
							
						float a = (float)ch.LightMap2[(x/2)+(y/2)*Terrain.CurrentTerrain.chunkWidth];
						a /= 15f;
						
						

//						Color col = batch2.getColor();
//						col.a = a;
//						batch2.setColor(col);
							if(ch.DirectionMap[x][y] == 0)
							batch2.draw(Water.WaterTexture,a, (ch.x*ch.Width)+(float)(x/2f),(ch.y*ch.Height) + (float)(y/2f),0.5f,amt);
							else
								batch2.draw(Water.WaterTexture,a, (ch.x*ch.Width)+(float)(x/2f),(ch.y*ch.Height) + (float)(y/2f),0.5f,0.5f);
						}
						if(ch.LiquidMap[x][y] < 1 && ch.FlowMap[x][y] > 0) {
							ch.FlowMap[x][y] = 0;
							ch.LiquidMap[x][y] = 0;
						}
					}
				}
				
			}
//		batch2.setColor(Color.WHITE);
	}
	
boolean isSolid(int x, int y, int x2, int y2) {
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth*2) {
			x2 = 0;
			x++;
		}
		
		if(x2 < 0) {
			x2 = (Terrain.CurrentTerrain.chunkWidth*2)-1;
			x--;
		}
		if(y2 >= Terrain.CurrentTerrain.chunkHeight*2) {
			y2 = 0;
			y++;
		}
		if(y2 < 0) {
			y2 = (Terrain.CurrentTerrain.chunkHeight*2)-1;
			y--;
		}
		TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(ch != null)
		{
			if(ch.TerrainMap[(x2/2)+(y2/2)*Terrain.CurrentTerrain.chunkWidth] == 0)
				return false;
			
			return Inventory.CurrentInventory.Items[ch.TerrainMap[(x2/2)+(y2/2)*Terrain.CurrentTerrain.chunkWidth]].solid;
		}
		return false;
	}

public void AddLiquid(int x,int y,int x2, int y2, float amt) {
	
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
		
		TerrainChunk ch = Terrain.CurrentTerrain.GetChunkByID(x,y);
		//if(ch != null)
		ch.LiquidMap[x2][y2] += amt;
//		if(!isSolid(x, y, x2, y2-1))
//			if(Terrain.CurrentTerrain.GetLiquid(x, y, x2, y2-1) <= 0)
//				ch.DirectionMap[x2][y2] = 1;
	}
	
	

}
