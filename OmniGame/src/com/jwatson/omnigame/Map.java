package com.jwatson.omnigame;

import com.jwatson.omnigame.Bob;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

public class Map {
	
	int[][] tiles;
	Bob bob;
	Vector2 BobSpawn;
	//tiles
	static int TILE = 0xffffff;
	static int START = 0xed1c24;
	
	public Map () {
		loadBinary();
	}
	
	private void loadBinary () {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/levels2.png"));
		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
				if(match(pix,START))
				{
				
					BobSpawn = new Vector2(x,pixmap.getHeight() - 1 - y);
					//bob = new Bob(this, x,pixmap.getHeight() - 1 - y);
					bob.state = Bob.SPAWN;
					Gdx.app.debug("SPAWNED","BRO");
				}
				
				tiles[x][y] = pix;
			}
		}
	}
	
	public void update (float deltaTime) {
		bob.update(deltaTime);
		//if (bob.state == Bob.DEAD) bob = new Bob(this, BobSpawn.x, BobSpawn.y);
	}
	boolean match (int src, int dst) {
		return src == dst;
	}
	
	boolean isDeadly(int tile)
	{
		return false;
	}
}
