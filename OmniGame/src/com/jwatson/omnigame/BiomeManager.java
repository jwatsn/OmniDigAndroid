package com.jwatson.omnigame;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jwatson.omnigame.InventoryObjects.BrownDirt;
import com.jwatson.omnigame.InventoryObjects.Grass;

public class BiomeManager {
	
	public static int BIOME_UNDERGROUND = 1;
	public static int BIOME_FOREST = 2;
	public static int BIOME_FOREST_CAMP = 3;
	public static int BIOME_ICE = 4;
	public static int BIOME_DESERT = 5;

	public BiomeManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static byte[][] GetForestBiome(int width, int height,int thickness,byte[] hill_line_item,byte[] under_item,byte tree_item,int hills_height,float hill_roughness, Vector3... items) {
		
		byte[][] ret = new byte[width][height];
		Inventory inv = Inventory.CurrentInventory;
		
		ret = generateHills(ret,width,hills_height,hill_line_item, hill_roughness,Item.getId("FG_Grass1"),Item.getId("FG_Grass2"),Item.getId("FG_Grass3"));
		
		boolean flag = false;
		boolean flag2 = false;
		for(int x=0; x<width; x++) {
			flag = false;
			for(int y=height-1; y>=0; y--) {
				
				for(int i = 0; i<hill_line_item.length; i++)
				if(ret[x][y] == hill_line_item[i]) {
					
					if(x-1 >= 0) {
						if(ret[x-1][y] != tree_item) {
							if(MathUtils.random(100) < thickness)
								ret[x][y] = tree_item;
						}
					}
					
					flag2 = true;
					break;
				}
				if(flag2) {
					flag = true;
					flag2 = false;
					continue;
				}
				
				if(flag) {
					int rand = MathUtils.random(under_item.length-1);
					ret[x][y] = under_item[rand];
					
					for(Vector3 i : items) {
						
						byte id = (byte)i.x;
						float chance = i.y;
						double roll = Math.random()*100f;
						if(roll < chance) {
						
							MakePatch(ret, x,y,id,(int)(i.z*Math.random()));
							
						}
						
					}
				}
				
			}
		}
		
		
		
		
		
		return ret;
		
		
	}
	
public static byte[][] GetForestBiomeSnow(int width, int height,int thickness,byte[] hill_line_item,byte[] under_item,byte tree_item,int hills_height,float hill_roughness, Vector3... items) {
		
		byte[][] ret = new byte[width][height];
		Inventory inv = Inventory.CurrentInventory;
		
		ret = generateHills(ret,width,hills_height,hill_line_item, hill_roughness,Item.getId("FG_Grass1_Snow"),Item.getId("FG_Grass2_Snow"),Item.getId("FG_Grass3_Snow"));
		
		boolean flag = false;
		boolean flag2 = false;
		for(int x=0; x<width; x++) {
			flag = false;
			for(int y=height-1; y>=0; y--) {
				
				for(int i = 0; i<hill_line_item.length; i++)
				if(ret[x][y] == hill_line_item[i]) {
					
					if(x-1 >= 0) {
						if(ret[x-1][y] != tree_item) {
							if(MathUtils.random(100) < thickness)
								ret[x][y] = tree_item;
						}
					}
					
					flag2 = true;
					break;
				}
				if(flag2) {
					flag = true;
					flag2 = false;
					continue;
				}
				
				if(flag) {
					int rand = MathUtils.random(under_item.length-1);
					ret[x][y] = under_item[rand];
					
					for(Vector3 i : items) {
						
						byte id = (byte)i.x;
						float chance = i.y;
						double roll = Math.random()*100f;
						if(roll < chance) {
						
							MakePatch(ret, x,y,id,(int)(i.z*Math.random()));
							
						}
						
					}
				}
				
			}
		}
		
		
		
		
		
		return ret;
		
		
	}
	
public static byte[][] GetTopSideBiome(int width, int height,int thickness,byte[] hill_line_item,byte[] under_item,byte tree_item,int hills_height,float hill_roughness, Vector3... items) {
		
		byte[][] ret = new byte[width][height];
		Inventory inv = Inventory.CurrentInventory;
		
		ret = generateHills(ret,width,hills_height,hill_line_item, hill_roughness);
		
		boolean flag = false;
		boolean flag2 = false;
		for(int x=0; x<width; x++) {
			flag = false;
			for(int y=height-1; y>=0; y--) {
				
				for(int i = 0; i<hill_line_item.length; i++)
				if(ret[x][y] == hill_line_item[i]) {
					
					if(x-1 >= 0) {
						if(ret[x-1][y] != tree_item) {
							if(MathUtils.random(100) < thickness)
								ret[x][y] = tree_item;
						}
					}
					
					flag2 = true;
					break;
				}
				if(flag2) {
					flag = true;
					flag2 = false;
					continue;
				}
				
				if(flag) {
					int rand = MathUtils.random(under_item.length-1);
					ret[x][y] = under_item[rand];
					
					for(Vector3 i : items) {
						
						byte id = (byte)i.x;
						float chance = i.y;
						double roll = Math.random()*100f;
						if(roll < chance) {
						
							MakePatch(ret, x,y,id,(int)(i.z*Math.random()));
							
						}
						
					}
				}
				
			}
		}
		
		
		
		
		
		return ret;
		
		
	}
	

public static byte[][] GetForestBiome_Camp(int width, int height,int thickness,byte[] hill_line_item,byte[] under_item,byte tree_item,int hills_height,float hill_roughness,int tentnum, Vector3... items) {
		
		byte[][] ret = new byte[width][height];
		Inventory inv = Inventory.CurrentInventory;
		
		ret = generateHills(ret,width,3,hill_line_item, 4,Item.getId("FG_Grass1"),Item.getId("FG_Grass2"),Item.getId("FG_Grass3"));
		int counter = 0;
		for(int i=0; i<tentnum; i++) {
			int x = MathUtils.random(3,width-5);
			int y = height-1;
			boolean flag = true;
			while(flag) {
				if(y <= 0) {
					x++;
					y = height-1;
				}
				if(x >= width-1)
					x = 0;
				for(int i2=0; i2<hill_line_item.length; i2++)
				if(ret[x][y] == hill_line_item[i2]) {
					ret[x][y+1] = Item.getId("DEP_EnemyTent");
					flag = false;
					break;
				}
				y--;
				counter++;
				if(counter > 100)
					flag = false;
			}
		}
		
		
		boolean flag = false;
		boolean flag2 = false;
		int count = 0;
		for(int x=0; x<width; x++) {
			flag = false;
			for(int y=height-1; y>=0; y--) {
				
				for(int i = 0; i<hill_line_item.length; i++)
				if(ret[x][y] == hill_line_item[i]) {
					
					if(x-1 >= 0) {
						if(ret[x-1][y] != tree_item) {
							if(MathUtils.random(100) < thickness)
								ret[x][y] = tree_item;
							
						}
					}
					
					flag2 = true;

					break;
				}
				if(flag2) {
					flag = true;
					flag2 = false;
					continue;
				}
				
				if(flag) {
					int rand = MathUtils.random(under_item.length-1);
					ret[x][y] = under_item[rand];
					
					for(Vector3 i : items) {
						
						byte id = (byte)i.x;
						float chance = i.y;
						double roll = Math.random()*100f;
						if(roll < chance) {
						
							MakePatch(ret, x,y,id,(int)(i.z*Math.random()));
							
						}
						
					}
				}
				
			}
		}
		
		
		
		
		
		return ret;
		
		
	}
	

private static byte[][] generateHills(byte[][] ret, int width, int height, byte[] hill_line, float smooth,byte... foliage) {
		// TODO Auto-generated method stub
	
		Vector2[] heights = new Vector2[(int)smooth-1];
		
		for(int i = 1; i< smooth; i++) {
			
			
			heights[i-1] = new Vector2(width * (float)((1/smooth)*i), (MathUtils.random(height-1)));
			
		}
		
		//Gdx.app.debug("", ""+heights[0].x);
	

		Pixmap test = new Pixmap(width, height, Format.RGB888);
		Vector2 LastPos = new Vector2(0,0);
		for(Vector2 pos : heights) {
			test.setColor(Color.GREEN);
			test.drawLine((int)LastPos.x, (int)LastPos.y, (int)pos.x, (int)pos.y);
			LastPos = pos;
		}
		test.drawLine((int)LastPos.x, (int)LastPos.y, width, 0);
		
		for(int x = 0; x<width; x++) {
			for(int y = 0; y<height; y++) {
				if(test.getPixel(x, y) == Color.rgba8888(Color.GREEN)) {
					int hill = MathUtils.random(hill_line.length-1);
					ret[x][y] = hill_line[hill];
					if(foliage.length > 0)
					if(MathUtils.randomBoolean())
					ret[x][y+1] = foliage[MathUtils.random(0, foliage.length-1)];
				}
			}
		}
	
		return ret;
	}

public static byte[][] GetUndergroundBiome(int width, int height,int caves,byte main,Vector3... item) {
		

	
		byte[][] ret = generateCaves(width,height,main,5,caves);
		Inventory inv = Inventory.CurrentInventory;
		int n = 0;
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {

				if(ret[x][y] > 0)
				for(Vector3 i : item) {
					
					byte id = (byte)i.x;
					float chance = i.y;
					double roll = Math.random()*100f;
					if(roll < chance) {
					
						MakePatch(ret, x,y,id,(int)(i.z*Math.random()));
						
					}
					
				}
				
			}
		}
		
		return ret;
		
		
	}

private static byte[][] generateCaves(int width, int height, byte main_tile, int smoothness, int percent) {
	
	int ii,jj;
	

	byte[][] map1 = new byte[width][height];
	byte[][] map2 = new byte[width][height];
	
	for(int x=0; x<width; x++) {
		for(int y=0; y<height; y++) {
			if(MathUtils.random(100) <= percent) {
				map1[x][y] = main_tile;
			}
			map2[x][y] = main_tile;
		}
	}
	
	
	
	for(int i=0; i<smoothness; i++) {
	
	for(int x=1; x<width-1; x++) {
		for(int y=1; y<height-1; y++) {
			
			int pass = 0;
			int pass2 = 0;

	 		for(ii=-1; ii<=1; ii++)
			for(jj=-1; jj<=1; jj++)
	 		{
	 			if(map1[x+ii][y+jj] > 0)
	 				pass++;
	 		}
	 		for(ii=x-2; ii<=x+2; ii++)
	 	 		for(jj=y-2; jj<=y+2; jj++)
	 	 		{
	 	 			if(Math.abs(ii-x)==2 && Math.abs(jj-y)==2)
	 	 				continue;
	 	 			if(ii<0 || jj<0 || ii>=width || jj>=height)
	 	 				continue;
	 	 			if(map1[ii][jj] > 0)
	 	 				pass2++;
	 	 		}
	 		if(pass >= 5 || pass2 <= 3) {
	 			map2[x][y] = main_tile;
	 			
	 		}
	 		else {
	 			map2[x][y] = 0;
	 		}
		}
	}
	for(int x2=0; x2<width; x2++)
		for(int y2=0; y2<height; y2++)
			map1[x2][y2] = map2[x2][y2];
	}
	
	
	return map1;
	
}

private static void MakePatch(byte[][] ret, int x, int y, byte i,int len) {
	
	if(x<0)
		return;
	if(y<0)
		return;
	if(x >= Terrain.CurrentTerrain.chunkWidth)
		return;
	if(y >= Terrain.CurrentTerrain.chunkHeight)
		return;
	
	if( len <= 0)
		return;
	
	if(ret[x][y] == i)
		return;
	
	if(ret[x][y] <= 0)
		return;
	
	len--;
	
	ret[x][y] = i;
	
	MakePatch(ret,x,y+1,i,len);
	MakePatch(ret,x+1,y,i,len);
	MakePatch(ret,x,y-1,i,len);
	MakePatch(ret,x-1,y,i,len);
	
}

public static void MakeLiquidPatch(float[][] ret,byte[][]tmap, int x, int y, float i,int len) {
	
	if(x<0)
		return;
	if(y<0)
		return;
	if(x >= Terrain.CurrentTerrain.chunkWidth)
		return;
	if(y >= Terrain.CurrentTerrain.chunkHeight)
		return;
	
	if( len <= 0)
		return;
	
	if(ret[x][y] == i)
		return;
	
	if(tmap[x/2][y/2] > 0)
		return;
	len--;
	
	ret[x][y] = i;
	
	MakeLiquidPatch(ret,tmap,x,y+1,i,len);
	MakeLiquidPatch(ret,tmap,x+1,y,i,len);
	MakeLiquidPatch(ret,tmap,x,y-1,i,len);
	MakeLiquidPatch(ret,tmap,x-1,y,i,len);
	
}



}
