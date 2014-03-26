package com.jwatson.omnigame.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.jwatson.omnigame.AI;
import com.jwatson.omnigame.BackgroundManager;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.OmniGame;
import com.jwatson.omnigame.Terrain;

public class DifficultyScreen extends OmniScreen {

	boolean f_world1,f_world2,f_world3;
	TextureRegion background;
	TextureRegion button_pressed;
	TextureRegion button_unpressed;
	SpriteBatch batch;
	Skin skin;
	Stage stage;
	TextButton EasyButton;
	TextButton MediumButton;
	TextButton HardButton;
	TextButton InsaneButton;
	TextButton Back;
	final OmniGame game;
	private float pressTimer;
	BitmapFont font;
	
	public DifficultyScreen(OmniGame gam) {
		super(gam);
		// TODO Auto-generated constructor stub
		batch = new SpriteBatch();
		background = new TextureRegion(new Texture("data/title.png"));
		button_pressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_pressed");
		button_unpressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_unpressed");
		TextureRegionDrawable button_press = new TextureRegionDrawable(button_pressed);
		TextureRegionDrawable button_unpress = new TextureRegionDrawable(button_unpressed);
		//skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		TextButtonStyle style = new TextButtonStyle();
		style.font =  new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		font = new BitmapFont(Gdx.files.internal("data/info.fnt"),false);
		style.down = button_press;
		style.up = button_unpress;
		style.unpressedOffsetX = -1;
		style.unpressedOffsetY = 1;
		EasyButton = new TextButton("Easy",style);
		MediumButton = new TextButton("Medium",style);
		HardButton = new TextButton("Hard",style);
		InsaneButton = new TextButton("INSANE",style);
		Back = new TextButton("Back",style);
		
		

		

		
		
		this.game = gam;
		EasyButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
			MapRenderer.Difficulty = 1;
			game.setScreen(new GameScreen(game));

			}
		});
		MediumButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				MapRenderer.Difficulty = 2;
				game.setScreen(new GameScreen(game));
			}
		});
		
		HardButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				MapRenderer.Difficulty = 3;
				game.setScreen(new GameScreen(game));
			}
		});
		
		InsaneButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				MapRenderer.Difficulty = 4;
				game.setScreen(new GameScreen(game));
			}
		});
		
		Back.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(new NewGameScreen(game));
			}
		});
		
		EasyButton.setPosition(175, 200);
		MediumButton.setPosition(175, 160);
		HardButton.setPosition(175, 120);
		InsaneButton.setPosition(175, 80);
		Back.setPosition(175, 40);
		EasyButton.setVisible(true);
		MediumButton.setVisible(true);
		HardButton.setVisible(true);
		Back.setVisible(true);
		
		stage = new Stage(480, 320, false);
		stage.addActor(EasyButton);
		stage.addActor(MediumButton);
		stage.addActor(HardButton);
		stage.addActor(InsaneButton);
		stage.addActor(Back);
		Gdx.input.setInputProcessor(stage);
	
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		BackgroundManager.BGManager.render();
		batch.begin();
		batch.draw(background, 240-(background.getRegionWidth()/2), 240);
		font.drawWrapped(batch, "Press and hold to delete", 0, 20, 480,BitmapFont.HAlignment.CENTER);
		batch.end();
		stage.act(delta);
		stage.draw();
		
		
		
		if(EasyButton.isPressed() || MediumButton.isPressed() || HardButton.isPressed() || InsaneButton.isPressed()) {
			pressTimer += delta;
		}
		else
		pressTimer = 0;
		
		if(pressTimer > 2) {
			
//			if(EasyButton.isPressed()) {
//				
//				EasyButton.setDisabled(true);
//				
//				pressTimer = 0;
//				world1.delete();
//				EasyButton.setText("New World");
//				f_world1 = false;
//			}
//			
//			if(MediumButton.isPressed()) {
//				
//				MediumButton.setDisabled(true);
//				
//				pressTimer = 0;
//				world2.delete();
//				MediumButton.setText("New World");
//				f_world2 = false;
//			}
//			
//			if(HardButton.isPressed()) {
//				
//				HardButton.setDisabled(true);
//				
//				pressTimer = 0;
//				world3.delete();
//				HardButton.setText("New World");
//				f_world3 = false;
//			}
			
		}
		else {
			
			if(!EasyButton.isPressed())
				if(EasyButton.isDisabled())
					EasyButton.setDisabled(false);
			
			if(!MediumButton.isPressed())
				if(MediumButton.isDisabled())
					MediumButton.setDisabled(false);
			
			if(!HardButton.isPressed())
				if(HardButton.isDisabled())
					HardButton.setDisabled(false);
			
		}
		
		pressTimer += delta;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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
