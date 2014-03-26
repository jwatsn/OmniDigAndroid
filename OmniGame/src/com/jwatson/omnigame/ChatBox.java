package com.jwatson.omnigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ChatBox {
	
	Label label;
	boolean isActive;
	Vector2 pos;
	Stack<String> Log;
	Label[] TF;
	TextField input;
	Texture chatBox;
	Texture chatInput;
	SpriteBatch batch;

	public ChatBox(float x, float y) {
		
		pos = new Vector2(x,y);
		Log = new Stack<String>();
		chatBox = DrawChatBox(250, 64);
		chatInput = DrawChatInput(250, 18);
		TF = new Label[6];
		LabelStyle tfs = new LabelStyle();
		BitmapFont font = new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		tfs.font = font;
		tfs.fontColor = new Color(0, 0, 255, 255);
		
		
		for(int i=0; i<TF.length; i++) {
			
			TF[i] = new Label("",tfs);
			TF[i].setPosition(x+2, y+i*16);
			MapRenderer.CurrentRenderer.stage.addActor(TF[i]);
			TF[i].setVisible(false);
			TF[i].setHeight(16);
			
		}
		TextFieldStyle tfs2 = new TextFieldStyle();
		tfs2.font = font;
		tfs2.fontColor = new Color(0, 0, 255, 255);
		input = new TextField("", tfs2);
		input.setPosition(pos.x+2, pos.y-18);
		input.setText("");
		input.setVisible(false);
		input.setDisabled(true);
		input.setMessageText("Chat Here Bro");
		MapRenderer.CurrentRenderer.stage.addActor(input);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
		



		//MapRenderer.CurrentRenderer.stage.addActor(label);
		
	}
	
	public boolean IsActive() {
		return isActive;
	}
	
	public void SetActive(boolean flag) {
		isActive = flag;
		if(flag) {
			for(int i=0; i<TF.length; i++) {
				TF[i].setVisible(true);
			}
			input.setVisible(true);
			input.setDisabled(false);
			
			MapRenderer.CurrentRenderer.stage.setKeyboardFocus(input);
		}
		else {
			for(int i=0; i<TF.length; i++) {
				TF[i].setVisible(false);
			}
			input.setVisible(false);
			input.setDisabled(true);
		}
	}
	
	public Texture DrawChatBox(int width, int height) {
		
		Pixmap border = new Pixmap(width, height, Format.RGBA4444);
		Color color = new Color();
		color.set(Color.WHITE);
		color.a = 100;
		border.setColor(color);
		border.fill();
		border.setColor(Color.BLACK);
		border.drawRectangle(0, 0, width, height);
		Texture tex = new Texture(border);
		
		return tex;
	}
	
	Texture DrawChatInput(int width, int height) {
		Pixmap border = new Pixmap(width, height, Format.RGBA4444);
		Color color = new Color();
		color.set(Color.WHITE);
		color.a = 100;
		border.setColor(color);
		border.fill();
		border.setColor(Color.BLACK);
		border.drawRectangle(0, 0, width, height);
		Texture tex = new Texture(border);
		
		return tex;		
	}
	
	public void render() {
		if(isActive) {
			batch.begin();
			batch.draw(chatBox, pos.x,pos.y);
			batch.draw(chatInput, pos.x, pos.y-18);
			batch.end();
		}
	}
	
	public void AddLine(String line) {
		Log.add(line);
		RefreshChat();
	}
	
	void RefreshChat() {
		for(int i=0; i<Log.size(); i++) {
			if(i<4) {
				TF[i].setText(Log.get((Log.size()-1)-i));
			}
		}
		SetActive(true);
		
	}
	
	
	public String GetInputText() {
		return input.getText();
	}
	
	public void ClearInputText() {
		input.setText("");
	}

}
