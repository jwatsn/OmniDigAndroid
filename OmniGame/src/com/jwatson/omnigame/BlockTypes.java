package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;

public class BlockTypes {
	
	
	//First field is armor 2 is regen
	static int[][] BlockStats = {
			//0 is nothing
			{0,0,0},
			//1 BrownDirt
			{1,0,0},
			//2 StoneBlock
			{1,0,0}
	};
	
	static int GetHP(int id) {
		
		return BlockStats[id][0];
	}
	
}
