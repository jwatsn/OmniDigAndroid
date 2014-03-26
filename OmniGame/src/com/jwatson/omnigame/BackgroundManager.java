package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jwatson.omnigame.graphics.CustomBatch;

public class BackgroundManager {

	
	CustomBatch batch;
	Camera cam;
	int[] plx_id;
	public Matrix4 m_transform;
	public float scroll;
	public float speed = 0;
	Color col;
	Vector2 bob_last_pos;
	public float deltaX;
	Texture plx_undertexture_BG;
	Texture plx_undertexture_FG;
	public static BackgroundManager BGManager;
	public boolean manualControl;
	public float manualSkyTimer;
	public float manualSky;
	public boolean manualSkyFlag;
	ShaderProgram bg_shader;
	TextureRegion[] plx_background;
	
	public BackgroundManager(Camera cam) {
		// TODO Auto-generated constructor stub
		
		if(BGManager != null)
			BGManager = null;
		
		BGManager = this;
		
		bg_shader = new ShaderProgram(ShaderManager.BG_Shader_Vert, ShaderManager.bg_Frag_Shader);
		
	    TextureAtlas atlas = new TextureAtlas("data/background/tiles.atlas");
		plx_background = new TextureRegion[6];
		plx_background[0] = atlas.findRegion("dayGradient");
		plx_background[1] = atlas.findRegion("plx_DayAboveBG");
		plx_background[2] = atlas.findRegion("plx_DayAboveFG");
		plx_background[3] = atlas.findRegion("nightGradient");
		plx_background[4] = atlas.findRegion("plx_NightAboveBG");
		plx_background[5] = atlas.findRegion("plx_NightAboveFG");
		
//		plx_undertexture_BG = new Texture("data/background/plx_DayBelowBG.png");
//		plx_undertexture_FG = new Texture("data/background/plx_DayBelowFG.png");
//		plx_background_under = new TextureRegion[2];
//		plx_background_under[0] = atlas.findRegion("plx_DayBelowBG");
//		plx_background_under[1] = atlas.findRegion("plx_DayBelowFG");
		
		this.batch = new CustomBatch(10,bg_shader);

		
		m_transform = new Matrix4();
		plx_id = new int[3];
		this.cam = cam;

		col = new Color(Color.WHITE);
		//col.mul(0.5f);
			
	}
	Vector3 project = new Vector3();
	private float scroll2;
	public void render() {
		this.batch.setProjectionMatrix(cam.combined);
		batch.begin();

		
		
		//scroll  += (delta*speed*10f)/5f;
		if(!manualControl) {
		if(Bob.CurrentBob.state != Bob.DYING && Bob.CurrentBob.state != Bob.DEAD) {
		scroll += deltaX/5;
		scroll2 += deltaX/3f;
		}
		}
		else {
			scroll += deltaX/5;
			scroll2 += deltaX/3f;
		}
		if(Math.abs(scroll) >= 17) {
			scroll = 0;
		}
		if(Math.abs(scroll2) >= 17) {
			scroll2 = 0;
		}
		
		if(!manualControl) {
		int y = (int) (((Terrain.Height * Terrain.chunkHeight)/2f)+20f);
//		cam.project(project);
		
		if(World.sky_transition > 0) {
			float time = World.sky_transition/13f;
			if(time > 1)
				time = 1;
			
			bg_shader.setUniformf("u_time", time);
			

			if(cam.position.y-5f > ((Terrain.Height * Terrain.chunkHeight)/2f)+20f)
				batch.draw(plx_background[0],plx_background[3],1, (cam.position.x-8.5f), cam.position.y-5f,17,10);
			else
				batch.draw(plx_background[0],plx_background[3],1, (cam.position.x-8.5f),((Terrain.Height * Terrain.chunkHeight)/2f)+20f,17,10);
			batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)+scroll, y,17,10);
			if(scroll <= 0)
			batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)+17+scroll, y,17,10);
			else
				batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)-17+scroll, y,17,10);
			batch.draw(plx_background[2],plx_background[5],1, (cam.position.x-8.5f)+scroll2, y,17,10);
			if(scroll2 <= 0)
			batch.draw(plx_background[2],plx_background[5],1,(cam.position.x-8.5f)+17+scroll2, y,17,10);
			else
				batch.draw(plx_background[2],plx_background[5],1,(cam.position.x-8.5f)-17+scroll2, y,17,10);
		}
		else {

			if(cam.position.y-5f > ((Terrain.Height * Terrain.chunkHeight)/2f)+20f)
				batch.draw(plx_background[0],1, (cam.position.x-8.5f), cam.position.y-5,17,10);
			else
				batch.draw(plx_background[0],1, (cam.position.x-8.5f),((Terrain.Height * Terrain.chunkHeight)/2f)+20f,17,10);
		batch.draw(plx_background[1],1, (cam.position.x-8.5f)+scroll, y,17,10);
		if(scroll <= 0)
		batch.draw(plx_background[1],1, (cam.position.x-8.5f)+17+scroll, y,17,10);
		else
			batch.draw(plx_background[1],1, (cam.position.x-8.5f)-17+scroll, y,17,10);
		batch.draw(plx_background[2],1, (cam.position.x-8.5f)+scroll2, y,17,10);
		if(scroll2 <= 0)
		batch.draw(plx_background[2],1,(cam.position.x-8.5f)+17+scroll2, y,17,10);
		else
			batch.draw(plx_background[2],1,(cam.position.x-8.5f)-17+scroll2, y,17,10);
		}
		
		}
		else {
			
			manualSkyTimer += Gdx.graphics.getDeltaTime();
			
			if(manualSkyTimer > 5) {
			if(!manualSkyFlag)
			manualSky += Gdx.graphics.getDeltaTime();
			else
				manualSky -= Gdx.graphics.getDeltaTime();
			float time = manualSky/13f;
			if(time > 1)
				time = 1;
			if(time < 0)
				time = 0;
			
			bg_shader.setUniformf("u_time", time);
			}
			if(manualSkyTimer > 20) {
				manualSkyTimer = 0;
				manualSkyFlag = !manualSkyFlag;
			}
			
			batch.draw(plx_background[0],plx_background[3],1, (cam.position.x-8.5f),-5,17,10);
			batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)+scroll, -5,17,10);
			if(scroll < 0)
			batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)+17+scroll, -5,17,10);
			else
				batch.draw(plx_background[1],plx_background[4],1, (cam.position.x-8.5f)-17+scroll, -5,17,10);
			batch.draw(plx_background[2],plx_background[5],1, (cam.position.x-8.5f)+scroll2, -5,17,10);
			if(scroll2 < 0)
			batch.draw(plx_background[2],plx_background[5],1,(cam.position.x-8.5f)+17+scroll2, -5,17,10);
			else
				batch.draw(plx_background[2],plx_background[5],1,(cam.position.x-8.5f)-17+scroll2, -5,17,10);
		}
		
		

		batch.end();
		
	}
	


}
