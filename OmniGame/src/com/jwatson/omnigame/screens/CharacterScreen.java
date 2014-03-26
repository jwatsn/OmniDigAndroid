package com.jwatson.omnigame.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

public class CharacterScreen extends OmniScreen {

	boolean f_world1,f_world2,f_world3;
	TextureRegion background;
	TextureRegion button_pressed;
	TextureRegion button_unpressed;
	SpriteBatch batch;
	Skin skin;
	Stage stage;
	TextButton Char1;
	TextButton Char2;
	TextButton Char3;
	TextButton Back;
	
	BitmapFont font;
	
	FileHandle world1 = Gdx.files.external("OmniDig/Characters/char1.dat");
	FileHandle world2 = Gdx.files.external("OmniDig/Characters/char2.dat");
	FileHandle world3 = Gdx.files.external("OmniDig/Characters/char3.dat");
	
	
	float pressTimer;
	
	final OmniGame game;
	int bg1_width,bg1_height,bg2_width,bg2_height;
	
	public CharacterScreen(OmniGame gam) {
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
		Char1 = new TextButton("New Character",style);
		Char2 = new TextButton("New Character",style);
		Char3 = new TextButton("New Character",style);
		Back = new TextButton("Back",style);
		
		
		FileHandle world1 = Gdx.files.external("OmniDig/Characters/char1.dat");
		FileHandle world2 = Gdx.files.external("OmniDig/Characters/char2.dat");
		FileHandle world3 = Gdx.files.external("OmniDig/Characters/char3.dat");
		
		if(world1.exists()) {
			Char1.setText("Character 1");
			f_world1 = true;
		}
		if(world2.exists()) {
			Char2.setText("Character 2");
			f_world2 = true;
		}
		if(world3.exists()) {
			Char3.setText("Character 3");
			f_world3 = true;
		}
		

		
		
		this.game = gam;
		Char1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
			game.CharSlot = 1;
			if(!f_world1)
				game.newchar = true;
			
			game.setScreen(new NewGameScreen(game));
			}
		});
		Char2.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.CharSlot = 2;
				if(!f_world2)
					game.newchar = true;
				game.setScreen(new NewGameScreen(game));
			}
		});
		
		Char3.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.CharSlot = 3;
				if(!f_world3)
					game.newchar = true;
				game.setScreen(new NewGameScreen(game));
			}
		});
		
		Back.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		Char1.setPosition(175, 200);
		Char2.setPosition(175, 160);
		Char3.setPosition(175, 120);
		Back.setPosition(175, 80);
		Char1.setVisible(true);
		Char2.setVisible(true);
		Char3.setVisible(true);
		Back.setVisible(true);
		
		stage = new Stage(480, 320, false);
		stage.addActor(Char1);
		stage.addActor(Char2);
		stage.addActor(Char3);
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
		
		if(Char1.isPressed() || Char2.isPressed() || Char3.isPressed()) {
			pressTimer += delta;
		}
		else
		pressTimer = 0;
		
		if(pressTimer > 2) {
			
			if(Char1.isPressed()) {
				
				Char1.setDisabled(true);
				
				pressTimer = 0;
				world1.delete();
				Char1.setText("New Character");
				f_world1 = false;
			}
			
			if(Char2.isPressed()) {
				
				Char2.setDisabled(true);
				
				pressTimer = 0;
				world2.delete();
				Char2.setText("New Character");
				f_world2 = false;
			}
			
			if(Char3.isPressed()) {
				
				Char3.setDisabled(true);
				
				pressTimer = 0;
				world3.delete();
				Char3.setText("New Character");
				f_world3 = false;
			}
			
		}
		else {
			
			if(!Char1.isPressed())
				if(Char1.isDisabled())
					Char1.setDisabled(false);
			
			if(!Char2.isPressed())
				if(Char2.isDisabled())
					Char2.setDisabled(false);
			
			if(!Char3.isPressed())
				if(Char3.isDisabled())
					Char3.setDisabled(false);
			
		}
			
			
		
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
