package com.jwatson.omnigame;

import java.awt.TextArea;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameClient {
	
	boolean connectionBoxShown;
	TextField IPField;
	TextField NameField;
	TextureRegionDrawable cursor;
	TextureRegionDrawable background;
	ServerBob serverBob;
	int port = 9999;
	World world;
	
	String IP = "";
	
	public Socket connection;
	public TextureRegion connectionBox;
	
	public Vector2 connectionBoxPos;
	
	SpriteBatch batch;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public GameClient(World world) {
		
		
		
		connectionBox = MapRenderer.Texture_Atlas.findRegion("connectionBox");
	
		this.world = world;
		int x= (480/2) - (connectionBox.getRegionWidth()/2);
		int y = (320/2) - (connectionBox.getRegionHeight()/2);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
		connectionBoxPos = new Vector2(x,y);

		//Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		TextFieldStyle tfs = new TextFieldStyle();
		//TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("data/textfield.png"))));
		BitmapFont font = new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		tfs.font = font;
		tfs.fontColor = Color.BLACK;
		//tfs.background = background;
		IPField = new TextField("",tfs);
		NameField = new TextField("",tfs);
		MapRenderer.CurrentRenderer.stage.addActor(IPField);
		MapRenderer.CurrentRenderer.stage.addActor(NameField);
		//IPField.setScale(0.5f);
		IPField.setMessageText("localhost");
		IPField.setX(x+45);
		IPField.setY(y+163);
		IPField.setVisible(true);
		
		//NameField.scale(0.5f);
		NameField.setMessageText("JubJub");
		NameField.setX(x+60);
		NameField.setY(y+103);

		IPField.setVisible(false);
		NameField.setVisible(false);
		

	}
	
	public void connect(String ip, int port) {
		connection = new Socket();
		
		SocketAddress addr = new InetSocketAddress(ip, port);
		Gdx.app.debug("CLIENT",""+addr.toString());
		try {

			connection.connect(addr);
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
			//random
			out.writeUTF("32u4v09238m40c9209ewd0cjqwjmec90132i9ec01k091i2c09d3k");
			out.flush();
			
			int msg = in.readInt();
			if(msg == ClientBob.WELCOME) {
				int ID = in.readInt();
				Gdx.app.debug("GameClient", "Auth successful. ID:"+ID);
				World.CurrentWorld.chatBox.AddLine("Connected.");

				try {
					serverBob = new ServerBob(connection,out,in,NameField.getText(),ID,world);
					serverBob.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				

				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean IsActive() {
		return connectionBoxShown;
	}
	public void SetActive(boolean flag) {
		connectionBoxShown = flag;
		if(flag) {
			IPField.setDisabled(false);
			NameField.setDisabled(false);
			IPField.setVisible(true);
			NameField.setVisible(true);
		}
		else {
			IPField.setDisabled(true);
			NameField.setDisabled(true);
			
			IPField.setVisible(false);
			NameField.setVisible(false);
		}
			
	}
	
	
	public void render() {
	batch.begin();
	batch.draw(connectionBox, connectionBoxPos.x, connectionBoxPos.y);
	batch.end();
	

	}
	
	public void updateInput(float deltaTime) {
		
		float x0 = (Gdx.input.getX(0) / (float)Gdx.graphics.getWidth()) * 480;
		float y0 = (Gdx.input.getY(0) / (float)Gdx.graphics.getHeight()) * 320;
		
		boolean ConnectButton = (x0 > connectionBoxPos.x+15 && x0 < connectionBoxPos.x+82 && y0 > connectionBoxPos.y+170 && y0 < connectionBoxPos.y+190);
		boolean HostButton = (x0 > connectionBoxPos.x+229 && x0 < connectionBoxPos.x+268 && y0 > connectionBoxPos.y+170 && y0 < connectionBoxPos.y+190);
		
		if(Gdx.input.justTouched()) {
			
			if(ConnectButton) {
				Bob.CurrentBob.state = Bob.SPAWN;
				World.CurrentWorld.client.connect(IPField.getText(), port);
				Vector2 vec = null;
				SetActive(false);
			}
			
			if(HostButton) {
				World.CurrentWorld.CreateServer();
				connect("localhost", port);
				SetActive(false);
			}
			
		}
		
		
		
		

		
	}

}
