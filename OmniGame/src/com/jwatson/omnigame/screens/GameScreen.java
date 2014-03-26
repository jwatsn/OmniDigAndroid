package com.jwatson.omnigame.screens;


import java.io.IOException;

import org.lwjgl.LWJGLUtil;

import com.jwatson.Animations.AssetManager;
import com.jwatson.omnigame.DisplayManager;
import com.jwatson.omnigame.Map;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.OmniGame;
import com.jwatson.omnigame.OnScreenController;
import com.jwatson.omnigame.OnscreenControlRenderer;
import com.jwatson.omnigame.GameServer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;

public class GameScreen extends OmniScreen {
	//Map map;
	MapRenderer renderer;
	OnscreenControlRenderer controlRenderer;

	float fps;
	float stateTime;
	OmniGame game;
	OnScreenController controller; 
	Thread WorldThread;
	
	public static OmniScreen CurrentGameScreen;
	
	public GameScreen (OmniGame game) {
		super(game);
		
		this.game = game;
		
		
		DisplayManager.Width = 470;
		DisplayManager.Height = 320;
		
		Gdx.app.debug(""+DisplayManager.Width, ""+DisplayManager.Height);
		
		controller = new OnScreenController();
		

		
		Gdx.graphics.setVSync(true);
		if(CurrentGameScreen != null)
			CurrentGameScreen = null;
		
		CurrentGameScreen = this;
		MapRenderer.UsingShaders = true;
		renderer = new MapRenderer(game,game.SaveSlot,game.isLoading,game.newchar,game.CharSlot);

		
		if(!game.isLoading)
		Terrain.CurrentTerrain.SaveMap(game.SaveSlot);
		//controlRenderer = new OnscreenControlRenderer(map);
	}

	@Override
	public void show () {
		//map = new Map();

		renderer = MapRenderer.CurrentRenderer;
		Gdx.input.setInputProcessor(OnScreenController.stage);
		game.isPaused = false;
	}

	@Override
	public void render (float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.world.update(delta);
		
		renderer.render(delta);
		
		controller.render(delta);
		
//		if(stateTime >= 1) {
//			stateTime = 0;
//			Gdx.app.debug("", ""+fps);
//			fps = 0;
//		}
//		fps++;
//		stateTime += delta;
		//controlRenderer.render();

		//if (map.bob.bounds.overlaps(map.endDoor.bounds)) {
	//		game.setScreen(new GameOverScreen(game));
	//	}

		//if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
		//	game.setScreen(new MainMenu(game));
	//	}
	
		
	}

	@Override
	public void hide () {
		

	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		if(World.CurrentWorld.serverThread != null)
			World.CurrentWorld.serverThread.stop();
		if(World.CurrentWorld.client != null)
			try {
				if(World.CurrentWorld.client.connection != null)
				World.CurrentWorld.client.connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Terrain.CurrentTerrain.SaveMap(game.SaveSlot);
		Gdx.app.debug("OmniGame", "dispose game screen");
		
		renderer.dispose();
	}
}