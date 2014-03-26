package com.jwatson.omnigame.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.OmniGame;

public class LoadScreen extends OmniScreen {

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
	public LoadScreen(OmniGame gam) {
		super(gam);
		// TODO Auto-generated constructor stub
		batch = new SpriteBatch();
		background = new TextureRegion(new Texture("data/mainmenu.png"));
		button_pressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_pressed");
		button_unpressed = MapRenderer.Texture_Atlas_Objs.findRegion("button_unpressed");
		TextureRegionDrawable button_press = new TextureRegionDrawable(button_pressed);
		TextureRegionDrawable button_unpress = new TextureRegionDrawable(button_unpressed);
		//skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		TextButtonStyle style = new TextButtonStyle();
		style.font =  new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		style.down = button_press;
		style.up = button_unpress;
		style.unpressedOffsetX = -1;
		style.unpressedOffsetY = 1;
		Save1 = new TextButton("Free",style);
		Save2 = new TextButton("Free",style);
		Save3 = new TextButton("Free",style);
		Back = new TextButton("Back",style);
		Back.setWidth(30);
		this.game = gam;
Save1.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				//game.setScreen(new GameScreen(game));
			}
		});
		Save2.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				//game.setScreen(new LoadScreen(game));
			}
		});
		
		Save3.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				//game.setScreen(new LoadScreen(game));
			}
		});
		
		Back.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		Save1.setPosition(190, 200);
		Save2.setPosition(190, 160);
		Save3.setPosition(190, 120);
		Back.setPosition(190, 80);
		Save1.setVisible(true);
		Save2.setVisible(true);
		Save3.setVisible(true);
		Back.setVisible(true);
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		stage.addActor(Save1);
		stage.addActor(Save2);
		stage.addActor(Save3);
		stage.addActor(Back);
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		batch.begin();
		batch.draw(background, 0, 0);
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
