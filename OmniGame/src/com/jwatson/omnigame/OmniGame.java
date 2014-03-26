package com.jwatson.omnigame;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.jwatson.Animations.AssetManager;
import com.jwatson.omnigame.screens.GameScreen;
import com.jwatson.omnigame.screens.MainMenuScreen;



public class OmniGame extends Game {
	
	
	public boolean isPaused;
	public int SaveSlot;
	public int CharSlot;
	public boolean isLoading;
	public boolean newchar;
	
	@Override
	public void create () {
		
		AssetManager.init();
		
		AssetManager.gen();
		
		setScreen(new MainMenuScreen(this));
	}
}
