package com.jwatson.omnigame.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.jwatson.omnigame.BackgroundManager;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.OmniGame;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.World;

public class MainMenuScreen extends OmniScreen {

	TextureRegion background;
	TextureRegion background2;
	TextureRegion button_pressed;
	TextureRegion button_unpressed;
	SpriteBatch batch;
	//Skin skin;
	Stage stage;
	TextButton ReturnGame;
	TextButton SaveGame;
	TextButton StartGame;
	TextButton LoadGame;
	TextButton Options;
	BitmapFont font;
	BackgroundManager bg;
	final OmniGame game;
	
	int bg1_width,bg1_height,bg2_width,bg2_height;
	
	public MainMenuScreen(OmniGame gam) {
		super(gam);
//		Terrain.TerrainChunks = null;
//		MapRenderer.CurrentRenderer = null;
		MapRenderer.Texture_Atlas = new TextureAtlas("data/tiles/tiles.atlas");
		MapRenderer.Texture_Atlas_Objs = new TextureAtlas("data/objs/tiles.atlas");
		
		
		// TODO Auto-generated constructor stub
		batch = new SpriteBatch();
		background = MapRenderer.Texture_Atlas_Objs.findRegion("LOGO_OmniDig");
		background2 = MapRenderer.Texture_Atlas_Objs.findRegion("LOGO_OmniDig_Shadow");
		button_pressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_pressed");
		button_unpressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_unpressed");
		TextureRegionDrawable button_press = new TextureRegionDrawable(button_pressed);
		TextureRegionDrawable button_unpress = new TextureRegionDrawable(button_unpressed);
		//skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		TextButtonStyle style = new TextButtonStyle();
		font =  new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		style.font = font;
		style.down = button_press;
		style.up = button_unpress;
		style.unpressedOffsetX = -1;
		style.unpressedOffsetY = 1;
		StartGame = new TextButton("New Game",style);
		ReturnGame = new TextButton("Return",style);
		LoadGame = new TextButton("Quit",style);
		Options = new TextButton("Options",style);
		Gdx.app.debug("HELLOJOE2", "");
		this.game = gam;
		
		bg1_width = background.getRegionWidth()*6;
		bg1_height = background.getRegionHeight()*6;
		
		bg2_width = background2.getRegionWidth()*6;
		bg2_height = background2.getRegionHeight()*6;
		
		bg = new BackgroundManager(new OrthographicCamera(17, 10));
		bg.deltaX = 0.1f;
		bg.manualControl = true;
		ReturnGame.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(GameScreen.CurrentGameScreen);
			}
		});
		StartGame.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(new CharacterScreen(game));
			}
		});
		LoadGame.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				if(GameScreen.CurrentGameScreen != null) {
					World.CurrentWorld.SaveCharacter(game.CharSlot);
					Terrain.CurrentTerrain.SaveMap(game.SaveSlot);
				}
				Gdx.app.exit();
			}
		});
		
		Options.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				Gdx.input.vibrate(10);
			}
		});
		
		ReturnGame.setPosition(175, 200);
		StartGame.setPosition(175, 160);
		LoadGame.setPosition(175, 120);
		Options.setPosition(175, 120);
		StartGame.setVisible(true);
		LoadGame.setVisible(true);
		Options.setVisible(false);
		
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		stage.addActor(StartGame);
		stage.addActor(LoadGame);
		stage.addActor(Options);
		if(game.isPaused)
		stage.addActor(ReturnGame);
		stage.setViewport(480, 320, false);
		Gdx.input.setInputProcessor(stage);
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		bg.render();
		batch.begin();
		batch.draw(background2, 240-(bg1_width/2), 240,bg2_width,bg2_height);
		//batch.draw(background, 240-(bg1_width/2), 240,bg1_width,bg1_height);

		batch.end();
		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.app.debug("HELLOJOE1", "");
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
