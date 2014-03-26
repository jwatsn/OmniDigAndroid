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
import com.jwatson.omnigame.BackgroundManager;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.OmniGame;
import com.jwatson.omnigame.Terrain;

public class NewGameScreen extends OmniScreen {

	boolean f_world1,f_world2,f_world3;
	TextureRegion background;
	TextureRegion button_pressed;
	TextureRegion button_unpressed;
	SpriteBatch batch;
	Skin skin;
	Stage stage;
	TextButton Save1;
	TextButton Save2;
	TextButton Save3;
	TextButton Back;
	final OmniGame game;
	private float pressTimer;
	BitmapFont font;
	
	FileHandle world1 = Gdx.files.external("OmniDig/world1.map");
	FileHandle world2 = Gdx.files.external("OmniDig/world2.map");
	FileHandle world3 = Gdx.files.external("OmniDig/world3.map");
	
	int bg1_width,bg1_height,bg2_width,bg2_height;
	
	public NewGameScreen(OmniGame gam) {
		super(gam);
		// TODO Auto-generated constructor stub
		batch = new SpriteBatch();
		background = MapRenderer.Texture_Atlas_Objs.findRegion("LOGO_OmniDig_Shadow");
		bg1_width = background.getRegionWidth()*6;
		bg1_height = background.getRegionHeight()*6;
		
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
		Save1 = new TextButton("New World",style);
		Save2 = new TextButton("New World",style);
		Save3 = new TextButton("New World",style);
		Back = new TextButton("Back",style);
		
		

		
		if(world1.exists()) {
			Save1.setText("World 1");
			f_world1 = true;
		}
		if(world2.exists()) {
			Save2.setText("World 2");
			f_world2 = true;
		}
		if(world3.exists()) {
			Save3.setText("World 3");
			f_world3 = true;
		}
		

		
		
		this.game = gam;
		Save1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
			game.SaveSlot = 1;
			if(f_world1)
				game.isLoading = true;
			
			if(f_world1)
			game.setScreen(new GameScreen(game));
			else
				game.setScreen(new GameScreen(game));
			}
		});
		Save2.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.SaveSlot = 2;
				if(f_world2)
					game.isLoading = true;
				if(f_world2)
					game.setScreen(new GameScreen(game));
					else
						game.setScreen(new GameScreen(game));
			}
		});
		
		Save3.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.SaveSlot = 3;
				if(f_world3)
					game.isLoading = true;
				if(f_world3)
					game.setScreen(new GameScreen(game));
					else
						game.setScreen(new GameScreen(game));
			}
		});
		
		Back.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(new CharacterScreen(game));
			}
		});
		
		Save1.setPosition(175, 200);
		Save2.setPosition(175, 160);
		Save3.setPosition(175, 120);
		Back.setPosition(175, 80);
		Save1.setVisible(true);
		Save2.setVisible(true);
		Save3.setVisible(true);
		Back.setVisible(true);
		
		stage = new Stage(480, 320, false);
		stage.addActor(Save1);
		stage.addActor(Save2);
		stage.addActor(Save3);
		stage.addActor(Back);
		Gdx.input.setInputProcessor(stage);
	
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		BackgroundManager.BGManager.render();
		batch.begin();
		batch.draw(background, 240-(bg1_width/2), 240,bg1_width,bg1_height);
		font.drawWrapped(batch, "Press and hold to delete", 0, 20, 480,BitmapFont.HAlignment.CENTER);
		batch.end();
		stage.act(delta);
		stage.draw();
		
		
		
		if(Save1.isPressed() || Save2.isPressed() || Save3.isPressed()) {
			pressTimer += delta;
		}
		else
		pressTimer = 0;
		
		if(pressTimer > 2) {
			
			if(Save1.isPressed()) {
				
				Save1.setDisabled(true);
				
				pressTimer = 0;
				world1.delete();
				Save1.setText("New World");
				f_world1 = false;
			}
			
			if(Save2.isPressed()) {
				
				Save2.setDisabled(true);
				
				pressTimer = 0;
				world2.delete();
				Save2.setText("New World");
				f_world2 = false;
			}
			
			if(Save3.isPressed()) {
				
				Save3.setDisabled(true);
				
				pressTimer = 0;
				world3.delete();
				Save3.setText("New World");
				f_world3 = false;
			}
			
		}
		else {
			
			if(!Save1.isPressed())
				if(Save1.isDisabled())
					Save1.setDisabled(false);
			
			if(!Save2.isPressed())
				if(Save2.isDisabled())
					Save2.setDisabled(false);
			
			if(!Save3.isPressed())
				if(Save3.isDisabled())
					Save3.setDisabled(false);
			
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
