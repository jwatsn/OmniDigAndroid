package com.jwatson.omnigame;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.openal.Mp3;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.InventoryObjects.Torch;
import com.jwatson.omnigame.InventoryObjects.Water;

public class Lighting implements Runnable {

	Vector2 LightSource = new Vector2();
	public Stack<recursiveLight> floodStack;
	HashMap<Integer, Boolean> hasChecked;
	int BiomeSpread;
	
	public List<WorldObj> LightSources;	
	int intensity = 2;
	Vector2 m_peakAltitude;
	int LadderID;
	Thread lightThread;
	ByteBuffer CurrentLightMap;
	byte[] CurrentTerrainMap;
	
	public Lighting()  {

		

		hasChecked = new HashMap<Integer, Boolean>();
		floodStack = new Stack<recursiveLight>();

		LightSources = new ArrayList<WorldObj>();
		m_peakAltitude = new Vector2((Terrain.CurrentTerrain.Height/2)+1, 10);
		LadderID = Inventory.CurrentInventory.GetItemID("Ladder");
		lightThread = new Thread();
		
	}
	
	
	
	
	boolean flag = false;
	public boolean paused;
	void renderLight() {
		
		//if(paused)
		//	return;
		int i =0;
		
		Terrain terrain = Terrain.CurrentTerrain;
		
		for(int x = 0; x < terrain.Width; x++) {
			for(int y = terrain.Height-1; y>0; y--) {
				
				if(!terrain.ChunkExists(x, y) )
					continue;
				
				if(y == (int)m_peakAltitude.x) {
//					
//					TerrainChunk tch = Terrain.CurrentTerrain.GetChunkByID(x, y);
//					if(World.CurrentWorld.r[8].overlaps(tch.bounds)) {
					
					for(int x2 = 0; x2 < terrain.chunkWidth; x2++) {
						
						diffuseBottom(terrain, terrain.GetChunkByID(x, y), x, y, x2, (int)m_peakAltitude.y, (byte)15); //sunlight
						
					}
					break;
//				}
					
				}
			}
		}
		//spreadLight();
		Gdx.app.debug("", "SHOULD NOT BE");
	}
	
			void diffuseTop(Terrain terrain, TerrainChunk tch, int x, int y, int x2, int y2,byte len) {
				
				TerrainChunk ch3 = tch;
				
				while(true) {
					
					if(len < 0.2f)
						break;
					if(y2 >= terrain.chunkHeight) {
						y++;
						if(terrain.ChunkExists(x, y)) {
						ch3 = terrain.GetChunkByID(x, y);
						y2 = 0;
						}
						else
							break;
						
					}
					if(ch3.TerrainMap[x2+y2*terrain.chunkWidth] > 0 && ch3.TerrainMap[x2+y2*terrain.chunkWidth] != LadderID) {
						if(ch3.LightMap2[x2+y2*terrain.chunkWidth] < len)
						ch3.LightMap2[x2+y2*terrain.chunkWidth] = len;
						
						len *= 0.5f;
					}
					else {
						len *=0.8f;
					}
					
					y2++;
					
					
				}	
		
			}
			Vector2 lastLightPos1 = new Vector2();
			Vector2 lastLightPos2 = new Vector2();
			void diffuseBottom(Terrain terrain, TerrainChunk tch, int x, int y, int x2, int y2,byte len) {

				
				TerrainChunk ch3 = tch;
				boolean flag = false;
				while(true) {
					
					if(len <= 0) {
						
						break;
					}
					if(y2 < 0) {
						break;
					}
					
					ch3.BakedLightMap[x2 + y2 * ch3.Width] = len;
					ch3.origBakedLightMap[x2 + y2 * ch3.Width] = len;
					//diffuseRight(terrain, ch3, x, y, x2, y2, len);
					//diffuseLeft(terrain, ch3, x, y, x2, y2, len);
					//ch3.LightMap2[x2+y2*terrain.chunkWidth] = len
					if(!flag) {
							
						
						
						
						if(Terrain.CurrentTerrain.isSolid(x, y, x2, y2) && !isOpaque(x, y, x2, y2)) {
							
							
							flag = true;
							len -= 5;
							
							
						}
						
					}
					else {
						
						len -= 5;
						//ch3.LightMap.put(x2+y2*terrain.chunkWidth,len);
					
					}
					
					y2--;
					
					
				}

			}
			
			
			boolean enabled = false;
			Vector3 culling = new Vector3();
	void spreadLight(TerrainChunk ch2) {

//		for(int x=0; x<Terrain.CurrentTerrain.Width; x++) {
//			for(int y = Terrain.CurrentTerrain.Height-1; y>0; y--) {
//				
//				if(!Terrain.CurrentTerrain.ChunkExists(x, y))
//					continue;
//				
//				
//				
//				TerrainChunk ch = Terrain.CurrentTerrain.GetChunkByID(x, y);
		
		for(TerrainChunk ch : Terrain.CurrentTerrain.ActiveChunks) {
			culling.set(ch.x*Terrain.chunkWidth+10,ch.y*Terrain.chunkHeight+10, 0);
			
			
			if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 20))
				continue;
//			for(int i=0; i<ch.LightMap2.length; i++) {
//				ch.LightMap2[i] = ch.BakedLightMap[i];
//				
//			}
			System.arraycopy(ch.BakedLightMap, 0, ch.LightMap2, 0, ch.LightMap2.length);
		}
			
				for(TerrainChunk ch : Terrain.CurrentTerrain.ActiveChunks) {
				
//					if(!ch.bounds.overlaps(World.CurrentWorld.r[8]))
					culling.set(ch.x*Terrain.chunkWidth+10,ch.y*Terrain.chunkHeight+10, 0);
						
					if(!MapRenderer.CurrentCam.frustum.sphereInFrustum(culling, 15))
						continue;
					
//				ch.LightMap = ByteBuffer.allocate(Terrain.CurrentTerrain.chunkWidth * Terrain.CurrentTerrain.chunkWidth);
//				
					

					for(int x2=0; x2<Terrain.CurrentTerrain.chunkWidth; x2++) {
						for(int y2 = 0; y2<Terrain.CurrentTerrain.chunkHeight; y2++) {
							
							

							byte mybright = (byte) (GetBrightness(ch.x*Terrain.chunkWidth + x2,ch.y*Terrain.chunkHeight + y2)-1);
							
							byte bright_right = GetBrightness((ch.x*Terrain.chunkWidth + x2)+1,ch.y*Terrain.chunkHeight + y2);
							byte bright_left = GetBrightness((ch.x*Terrain.chunkWidth + x2)-1,ch.y*Terrain.chunkHeight + y2);
							byte bright_up = GetBrightness((ch.x*Terrain.chunkWidth + x2),(ch.y*Terrain.chunkHeight + y2)+1);
							byte bright_down = GetBrightness((ch.x*Terrain.chunkWidth + x2),(ch.y*Terrain.chunkHeight + y2)-1);
							


							
//							if(!Terrain.CurrentTerrain.isSolid(ch.x, ch.y, x2, y2)) {
								

								

								

								if(mybright > bright_right && bright_right > 0)
									recursiveLight2((ch.x*Terrain.chunkWidth + x2)+1,ch.y*Terrain.chunkHeight + y2, mybright);
									
									//floodStack.push(new recursiveLight(ch.x, ch.y,x2+1,y2,mybright));
								//recursiveSpreadLights(ch);
								
								
								if(mybright > bright_left && bright_right > 0) {
									recursiveLight2((ch.x*Terrain.chunkWidth + x2)-1,ch.y*Terrain.chunkHeight + y2, mybright);
								}
								if(mybright > bright_up && bright_up > 0)
									recursiveLight2((ch.x*Terrain.chunkWidth + x2),(ch.y*Terrain.chunkHeight + y2)+1,mybright);
									//floodStack.push(new recursiveLight(ch.x, ch.y,x2,y2+1,mybright));
								//recursiveSpreadLights(ch);
								
								
								
								if(mybright > bright_down) {
									
									recursiveLight2((ch.x*Terrain.chunkWidth + x2),(ch.y*Terrain.chunkHeight + y2)-1,mybright);
								}
								

								
								
								

							if(GetTile(ch.x, ch.y, x2, y2) == Torch.ID) {
//								floodStack.push(new recursiveLight(ch.x, ch.y,x2,y2+1,(byte)15));
//								floodStack.push(new recursiveLight(ch.x, ch.y,x2+1,y2,(byte)15));
//								floodStack.push(new recursiveLight(ch.x, ch.y,x2,y2-1,(byte)15));
//								floodStack.push(new recursiveLight(ch.x, ch.y,x2-1,y2,(byte)15));
								recursiveLight2((ch.x*Terrain.chunkWidth + x2),(ch.y*Terrain.chunkHeight + y2),17);
							}
							
							if((int)((Bob.CurrentBob.pos.x+0.5f)/Terrain.CurrentTerrain.chunkWidth) == ch.x)
							if((int)((Bob.CurrentBob.pos.y+0.5f)/Terrain.CurrentTerrain.chunkHeight) == ch.y)
							if((int)((Bob.CurrentBob.pos.x+0.5f)%Terrain.CurrentTerrain.chunkWidth) == x2)
							if((int)((Bob.CurrentBob.pos.y+0.5f)%Terrain.CurrentTerrain.chunkHeight) == y2)
							{
								if(Inventory.CurrentInventory.BagItem[Inventory.CurrentInventory.currSelected][0] == Torch.ID)
									recursiveLight2((int)((ch.x*Terrain.chunkWidth + x2)+0.5f),(int)((ch.y*Terrain.chunkHeight + y2)+0.5f),17);
								else {
//									recursiveLight2((ch.x*Terrain.chunkWidth + x2)-1,(ch.y*Terrain.chunkHeight + y2),13);
									for(int xx=-3; xx<3; xx++)
										for(int yy=-3; yy<3; yy++) {
											float dst = Bob.CurrentBob.pos.dst((Bob.CurrentBob.pos.x)+xx, (Bob.CurrentBob.pos.y)+yy);
											float br = (0.9f - (dst/4f));
											//Gdx.app.debug("", ""+br);
											if(br < 0)
												br = 0;
											if(br > 1)
												br = 1;
											br *= 12;
											if(GetBrightness((int)(Bob.CurrentBob.pos.x+xx+0.5f), (int)(Bob.CurrentBob.pos.y+yy+0.5f)) < br)
												SetLightMap((int)(Bob.CurrentBob.pos.x+xx+0.5f), (int)(Bob.CurrentBob.pos.y+yy+0.5f) ,(int)br);
										}
									}
								}
							
						}
						
					}
					
					
					
				}
				b=0;
				//recursiveSpreadLights();
				
		
	}
	
	
	byte GetBrightness(int x3, int y3) {
		
		int x = x3/Terrain.chunkWidth;
		int y = y3/Terrain.chunkHeight;
		int x2 = x3%Terrain.chunkWidth;
		int y2 = y3%Terrain.chunkHeight;
		try {
		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.Width];
		if(chk != null) {
		byte bright = chk.LightMap2[x2+y2*Terrain.chunkWidth];
		
	
		
			return bright;
		}
		}
		catch(Exception e) {
			
			return -1;
			
		}
		return -1;

}
	boolean SetLightMap(int x3, int y3, int value) {
		
		int x = x3/Terrain.chunkWidth;
		int y = y3/Terrain.chunkHeight;
		int x2 = x3%Terrain.chunkWidth;
		int y2 = y3%Terrain.chunkHeight;
		
		try {
		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.Width];
		if(chk != null) {
			
		chk.LightMap2[x2+y2*Terrain.chunkWidth] = (byte) value;
			
			return true;
		}
		}
		catch(Exception e) {
			
			return false;
		}
		
		return false;

	}
	
boolean HasChecked(int x3, int y3, byte value) {
		
		int x = x3/Terrain.chunkWidth;
		int y = y3/Terrain.chunkHeight;
		int x2 = x3%Terrain.chunkWidth;
		int y2 = y3%Terrain.chunkHeight;
		
		try {
		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.Width];
		if(chk != null) {
			if(chk.LightMap2[x2+y2*Terrain.chunkWidth] >= value)
				return true;
		}
		}
		catch(Exception e) {
			
			return false;
		}
		
		return false;

	}
	byte GetBakedBrightness(int x3, int y3) {
		
		int x = x3/Terrain.chunkWidth;
		int y = y3/Terrain.chunkHeight;
		int x2 = x3%Terrain.chunkWidth;
		int y2 = y3%Terrain.chunkHeight;
		
		try {
		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.Width];
		if(chk != null) {
		byte bright = chk.BakedLightMap[x2+y2*Terrain.chunkWidth];
		if(bright < 0)
			bright = 0;
		
			return bright;
		}
		}
		catch(Exception e) {
			
			return -1;
			
		}
		return -1;

	}
	
	 class recursiveLight {
		int x,y,x2,y2,lastX,lastY;
		byte bright;
		public recursiveLight(int x,int y,int x2,int y2, byte bright) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.bright = bright;
			
		}
		
		public recursiveLight(int x,int y,int x2,int y2, byte bright,int lastX,int lastY) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.bright = bright;
			this.lastX = lastX;
			this.lastY = lastY;
			
		}
	}
	SpriteBatch batch = new SpriteBatch();
	//void recursiveSpreadLights(int x, int y, int x2, int y2, byte bright) {
	void recursiveSpreadLights() {
		boolean flag = false;
		//hasChecked2.clear();
		
		//batch.begin();
		int b = 0;
		while(floodStack.size() > 0) {
		recursiveLight stack = floodStack.pop();	
		int x =  stack.x;
		int y = stack.y;
		int x2 = stack.x2;
		int y2 = stack.y2;
		byte bright = stack.bright;
		
		
		if(x2 >= Terrain.CurrentTerrain.chunkWidth) {
			x2 = 0;
			x++;
			flag = true;
		}
		
		if(x2 < 0) {
			x2 = Terrain.CurrentTerrain.chunkWidth-1;
			x--;
			flag = true;
		}
		if(y2 > Terrain.CurrentTerrain.chunkHeight) {
			y2 = 0;
			y++;
			flag = true;
		}
		if(y2 < 0) {
			y2 = Terrain.CurrentTerrain.chunkHeight-1;
			y--;
			flag = true;
		}
		if(!Terrain.CurrentTerrain.ChunkExists(x, y))
			continue;
		
		
		
		byte tileBrightness = 0;	
		tileBrightness = GetBrightness(x*Terrain.chunkWidth + x2,(y*Terrain.chunkHeight + y2));
		
		
		
		if(tileBrightness >= bright)
			continue;
		
		
		if(bright <= 0)
			continue;
		b++;
		SetLightMap(x*Terrain.chunkWidth + x2,(y*Terrain.chunkHeight + y2), bright);
		bright -=2;
		
		
		if(Terrain.CurrentTerrain.isSolid(x, y, x2, y2) && !Terrain.CurrentTerrain.isOpaque(x, y, x2, y2)) {
			bright -= 5;
		}
		else if(GetTile(x, y, x2, y2) == Water.ID) {
			bright -=1;
		}
		
		
		
		//SetLightMap(x, y, x2, y2, bright);
		//Gdx.app.debug(""+floodStack.size(), ""+stack.x + " " + stack.y + " " + stack.x2 + " " + tileBrightness + " " + stack.bright);
		
		floodStack.push(new recursiveLight(x, y, x2, y2+1, bright,x2,y2));
		//floodStack.push(new recursiveLight(x, y, x2+1, y2+1, bright,x2,y2));
		floodStack.push(new recursiveLight(x, y, x2+1, y2, bright,x2,y2));
		//floodStack.push(new recursiveLight(x, y, x2+1, y2-1, bright,x2,y2));
		floodStack.push(new recursiveLight(x, y, x2, y2-1, bright,x2,y2));
		//floodStack.push(new recursiveLight(x, y, x2-1, y2-1, bright));
		floodStack.push(new recursiveLight(x, y, x2-1, y2, bright,x2,y2));
		
		}
		
	}
	int b = 0;
 void recursiveLight2(int x, int y, int bright) {
		 

	 	 bright -= 2;
		 if(bright <= 0)
			 return;
		 
		 byte tileBrightness = GetBrightness(x,y);
		 
		 
		 if(tileBrightness >= bright) {
			 return;
		 }
		 
		 SetLightMap(x, y,bright);

		 
		 if(Terrain.CurrentTerrain.isSolid(x, y))
			 bright -= 5;
			 
		 
		 b++;
		 
		 if(!HasChecked(x,y+1,(byte)bright))
		 recursiveLight2(x,y+1,bright);
		 if(!HasChecked(x+1,y,(byte)bright))
		 recursiveLight2(x+1,y,bright);
		 if(!HasChecked(x,y-1,(byte)bright))
		 recursiveLight2(x,y-1,bright);
		 if(!HasChecked(x-1,y,(byte)bright))
		 recursiveLight2(x-1,y,bright);

	 }
	
	byte GetTile(int x, int y, int x2, int y2) {
		boolean flag = false;
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 >= terrain.chunkWidth) {		//get lower chunk
			if(terrain.ChunkExists(x+1, y)) {
//				TerrainChunk left = terrain.GetChunkByID(x+1, y);
//				return left.LightMap.get()
				
				x = x + 1;
				x2 = 0;
				flag = true;
			}
			else
			return -1;
		}
		if(x2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x-1, y)) {
				
				x = x - 1;
				x2 = terrain.chunkWidth-1;
				flag = true;
			}
			else
				return -1;
		}
		
		if(y2 >= terrain.chunkHeight) {		//get lower chunk
			if(terrain.ChunkExists(x, y+1)) {
				
				y = y + 1;
				y2 = 0;
				flag = true;
			}
			else return -1;
		}
		
		if(y2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x, y-1)) {
				
				y = y - 1;
				y2 = terrain.chunkHeight-1;
				flag = true;
			}
			 else return -1;
		}
		TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
		if(chk != null)
			return chk.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth];
		
		return -1;
		
//		return  Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width].TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth];

	}
	
void SetLightMap(int x, int y, int x2, int y2, byte value) {
		
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
				x2 = terrain.chunkWidth - 1;
				
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
		tch.LightMap2[x2+y2*terrain.chunkWidth] = value;

	}
	
	boolean checkLeft(TerrainChunk ch,int x, int y, int x2, int y2) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 <= 0) {		//get lower chunk
			if(terrain.ChunkExists(x-1, y)) {
				TerrainChunk left = terrain.GetChunkByID(x-1, y);
				if(left.TerrainMap[(terrain.chunkWidth-1)+(y2)*terrain.chunkWidth] > 0) {
					return true;
				}
			}
		}
		else {
			if(ch.TerrainMap[(x2-1)+y2*terrain.chunkWidth] > 0)
				return true;
		}
		
		
		return false;
	}
	
	byte GetBrightness(int x, int y, int x2, int y2) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 >= terrain.chunkWidth) {		//get lower chunk
			if(terrain.ChunkExists(x+1, y)) {
//				TerrainChunk left = terrain.GetChunkByID(x+1, y);
//				return left.LightMap.get()
				
				x = x + 1;
				x2 = 0;
				flag = true;
			}
			else
			return -1;
		}
		if(x2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x-1, y)) {
				
				x = x - 1;
				x2 = terrain.chunkWidth-1;
				flag = true;
			}
			else
				return -1;
		}
		
		if(y2 >= terrain.chunkHeight) {		//get lower chunk
			if(terrain.ChunkExists(x, y+1)) {
				
				y = y + 1;
				y2 = 0;
				flag = true;
			}
			else return -1;
		}
		
		if(y2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x, y-1)) {
				
				y = y - 1;
				y2 = terrain.chunkHeight-1;
				flag = true;
			}
			 else return -1;
		}
		
			TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
			if(chk != null) {
			byte bright = chk.LightMap2[x2+y2*terrain.chunkWidth];
			if(bright < 0)
				bright = 0;
				return bright;
			}
			
			return -1;

	}
	
byte GetBakedBrightness(TerrainChunk ch, int x, int y, int x2, int y2) {
		
		boolean flag = false;
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 >= terrain.chunkWidth) {		//get lower chunk
			if(terrain.ChunkExists(x+1, y)) {
//				TerrainChunk left = terrain.GetChunkByID(x+1, y);
//				return left.LightMap.get()
				
				x = x + 1;
				x2 = 0;
				flag = true;
			}
			else
			return -1;
		}
		if(x2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x-1, y)) {
				
				x = x - 1;
				x2 = terrain.chunkWidth-1;
				flag = true;
			}
			else
				return -1;
		}
		
		if(y2 >= terrain.chunkHeight) {		//get lower chunk
			if(terrain.ChunkExists(x, y+1)) {
				
				y = y + 1;
				y2 = 0;
				flag = true;
			}
			else return -1;
		}
		
		if(y2 < 0) {		//get lower chunk
			if(terrain.ChunkExists(x, y-1)) {
				
				y = y - 1;
				y2 = terrain.chunkHeight-1;
				flag = true;
			}
			 else return -1;
		}
		
			TerrainChunk chk = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
			if(chk != null) {
				byte bright = (byte)(chk.BakedLightMap[x2+y2*chk.Width]);
				if(bright < 0)
					bright = 0;
			return bright;
			}
			return -1;

	}
	
	boolean checkRight(TerrainChunk ch,int x, int y, int x2, int y2) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(x2 >= terrain.chunkWidth-1) {		//get lower chunk
			if(terrain.ChunkExists(x+1, y)) {
				TerrainChunk left = terrain.GetChunkByID(x+1, y);
				if(left.TerrainMap[(0)+(y2)*terrain.chunkWidth] > 0) {
					return true;
				}
			}
		}
		else {
			if(ch.TerrainMap[(x2+1)+y2*terrain.chunkWidth] > 0)
				return true;
		}
		
		
		return false;
	}
	
	boolean checkTop(TerrainChunk ch,int x, int y, int x2, int y2) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(y2 >= terrain.chunkHeight) {		//get lower chunk
			if(terrain.ChunkExists(x, y+1)) {
				TerrainChunk top = terrain.GetChunkByID(x, y+1);
				if(top.TerrainMap[(x2)+(0)*terrain.chunkWidth] > 0) {
					return true;
				}
			}
		}
		else {
			if(ch.TerrainMap[(x2)+(y2+1)*terrain.chunkWidth] > 0)
				return true;
		}
		
		
		return false;
	}
	
	boolean checkBottom(TerrainChunk ch,int x, int y, int x2, int y2) {
		
		Terrain terrain = Terrain.CurrentTerrain;
		if(y2 <= 0) {		//get lower chunk
			if(terrain.ChunkExists(x, y-1)) {
				TerrainChunk top = terrain.GetChunkByID(x, y-1);
				if(top.TerrainMap[(x2)+(terrain.chunkHeight-1)*terrain.chunkWidth] > 0) {
					return true;
				}
			}
		}
		else {
			if(ch.TerrainMap[(x2)+(y2-1)*terrain.chunkWidth] > 0)
				return true;
		}
		
		
		return false;
	}
	
	Vector2 pos = new Vector2();
	public void MakeLightSource(int x, int y, int intensity) {
		
//		int x3 = x-intensity;
//		int y3 = y-intensity;
//		pos.set(x,y);
//		
//		for(int y2 = y3; y2 < y+intensity; y2++) {
//			for(int x2 = x3; x2 <x+intensity; x2++) {
//				float dist = pos.dst(x2,y2);
//				float a = dist/intensity;
//				if(a>1)a=0.9f;
//				Terrain.CurrentTerrain.SetLightTile(x2,y2,a);
//			}
//		}
		//renderLight();
		//floodStack.add(new Vector3(x,y,1));
	}
	
	public synchronized void MakeLightMap() {
	
		
		
		
//		for(int i=0; i<LightSources.size(); i++) {
//			
//			Vector3 light = LightSources.get(i);
//			
//			
//			int x2 = (int)(light.x - (light.z));
//			int y2 = (int)(light.y - (light.z));
//			
//			for(int y = y2; y < light.y+(light.z); y++) {
//				for(int x = x2; x < light.x+(light.z); x++) {
//					pos.set(x,y);
//					float dist = pos.dst(light.x,light.y);
//					
//				float a = dist/light.z;
//				
//				if(a > 1)
//					a = 1;
//					if(dist <= light.z) {
//
//					Terrain.CurrentTerrain.SetLightTile(x,y,a);
//					}
//					
//				}
//			}
			
//		}
				
			
				
				
				
			
		
	}


	public void refreshMap() {
		// TODO Auto-generated method stub
//		for(WorldObj obj : LightSources) {
//			MakeLightSource((int)(obj.pos.x+0.5f), (int)obj.pos.y, 4);
//		}
		

		//renderLight();
		
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
				return false;
			
			return Inventory.CurrentInventory.Items[ch.TerrainMap[x2+y2*Terrain.CurrentTerrain.chunkWidth]].opaque;
		}
		return false;
	}
	
	
	//flood take 2
	
	float updateTime;
	public void update(float deltaTime) {
		
		
			//recursiveSpreadLights();
		
			if(updateTime > 0.04f) {
				
				updateTime = 0;
				
			}
		
			updateTime +=deltaTime;
			
	}
	
	
	Vector2 light_left(float x,float y) {
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
		
		}
	}
	
}
