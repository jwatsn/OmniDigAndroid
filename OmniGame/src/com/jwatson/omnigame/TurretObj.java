package com.jwatson.omnigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.jwatson.omnigame.graphics.CustomBatch;

public class TurretObj extends WorldObj {
	
	static int GEAR = 1;
	static int BARREL = 2;
	static int MAINGEAR = 3;
	
	
	class TurretPart {
		int rotation_direction;
		float lastrot;
		float rotation;
		float width;
		float height;
		int type = GEAR;
		Sprite sprite;
		Vector2 pos;
		
		
		public TurretPart(float x,float y,float width,float height, int rotation_dir) {
			//sprite = new Sprite(tex, 1, 1);
			//sprite.setOrigin(width/2, height/2);
			rotation_direction = rotation_dir;
			this.width = width;
			this.height = height;
			pos = new Vector2(x,y);
		}
		
	}

	float deltaRotation;
	TextureRegion gear_texture;
	TextureRegion barrel_texture;
	TurretPart[] gears;
	WorldObj target;
	float target_angle;
	boolean barrel_active;
	public int[] LoadedProjectile;
	int range;
	Vector2 pos;
	float reloadTime;
	float lockOnTime;
	public TurretObj(float x, float y, float width, float height, int range, float reloadTime) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		custom_render = true;
		gear_texture = MapRenderer.Texture_Atlas.findRegion("DEP_BallistaTurret");
		barrel_texture = MapRenderer.Texture_Atlas.findRegion("DEP_BallistaBase");

		//gear_texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		MakeBallista(x, y);
		LoadedProjectile = new int[2];
		LoadedProjectile[0] = Item.getId("WEP_Arrow_Wood");
		LoadedProjectile[1] = 10;
		this.range = range;
		pos = new Vector2(x,y);
		this.reloadTime = reloadTime;
		isTurret = true;
	}
	
	public void MakeBallista(float x, float y) {
		
		
		gears = new TurretPart[1];
		gears[0] = new TurretPart(x, y, 1, 1, 1);
		gears[0].type = MAINGEAR;


	}
	
	@Override
	public void render(CustomBatch batch) {
		// TODO Auto-generated method stub
		super.render(batch);
		
		if(target != null) {
		if(target.pos.dst(pos) > range) {
			barrel_active = false;
			lockOnTime = 0;
			target = null;
		}
		else {
			if(lockOnTime > 0.5f)
			barrel_active = true;
		}
		}
		else {
			barrel_active = false;
			lockOnTime = 0;
		}
		
		if(!barrel_active) {
			timer = 0;
		}
		
		for(TurretPart tp : gears) {
			if(tp.type == MAINGEAR) {
				if(LoadedProjectile[1] > 0) {
//					if(barrel_active)
					float cos = MathUtils.cosDeg(tp.rotation);
					float sin = MathUtils.sinDeg(tp.rotation);
					float reloadpercent = timer;
					if(reloadpercent>1)
						reloadpercent = 1;
					if(reloadpercent < 0)
						reloadpercent = 0;
					if(timer > 0.6f) {
						if(barrel_active) {
					batch.draw(Item.Items[LoadedProjectile[0]].thumbnail, tp.pos.x-(0.35f*cos)+(0.6f*cos*reloadpercent), (tp.pos.y+0.125f-0.046875f)-(0.35f*sin)+(0.6f*sin*reloadpercent), 0.5f, 0.5f, 1, 1, 0.6f, 0.6f, target_angle-45);
						}
					}
//					
				}
				batch.draw(gear_texture, tp.pos.x, tp.pos.y, (tp.width/2f), (tp.height/2f), tp.width, tp.height, 1, 1, target_angle);
			}
			
		}
		
	}
	float timer;
	private float scanTimer;
	private float scanDelay = 0.5f;
	public Vector2 proj = new Vector2();
	@Override
	void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		int xx = (int)pos.x;
		int yy = (int)pos.y;
		if(Item.Items[Terrain.GetTile(xx, yy)] == null) {
			removalflag = true;
			return;
		}
		if(Item.Items[Terrain.GetTile(xx, yy)].type != InvObject.Type.TURRET) {
			removalflag = true;
			return;
		}

		for(int i=0; i<gears.length; i++) {
			
			TurretPart tp = gears[i];
			
			
			if(tp.type == MAINGEAR || tp.type == BARREL) {
			

			if(tp.type == MAINGEAR) {
				if(target != null) {
			float x2 = (target.pos.x+0.5f) - (tp.pos.x+0.5f);
			float y2 = (target.pos.y+((float)Math.abs(x2)/3f)) - (tp.pos.y+0.5f);
			if((float)Math.abs(x2) > 5)
				y2 = (target.pos.y+((float)Math.abs(x2)/2.5f)) - (tp.pos.y+0.5f);
			
		
			target_angle = (float) Math.toDegrees(MathUtils.atan2(y2, x2));
			if(target_angle < 0)
				target_angle = 360 + target_angle;
			
			tp.rotation = target_angle;
				}
			
			}
			boolean q1 = (target_angle < 90);
			boolean q2 = (target_angle > 90 && target_angle < 180);
			boolean q3 = (target_angle > 180 && target_angle < 270);
			boolean q4 = (target_angle > 270);
			
			deltaRotation = tp.lastrot - tp.rotation;

			tp.lastrot = tp.rotation;
			
//			if(target_angle > 225 && target_angle < 315) {
//				barrel_active = false;
//				continue;
//			}
//			else {
//				
//				barrel_active = true;
//			}

			
			
//			if(q1 || q4)  {
//				

//				float dX = tp.rotation - target_angle;
//				Gdx.app.debug("", ""+dX);
//				tp.rotation -= deltaTime * 50f;
//				if(tp.rotation < 0)
//					tp.rotation = 360;
//			}
//			else if(q2 || q3) {
//			if(tp.rotation > 225 && tp.rotation < 270)
//					continue;
//				tp.rotation += deltaTime * 50f;
//				tp.rotation = tp.rotation%360;
//				
//			}
			
		}
		
		
		}
 
		if(Gdx.input.justTouched() && Gdx.input.isKeyPressed(Keys.Q)) {
			Ray projectCoordinates =  MapRenderer.CurrentCam.getPickRay(Gdx.input.getX(0), Gdx.input.getY(0));
			float x2 = (projectCoordinates.origin.x);
			float y2 = (projectCoordinates.origin.y);
			
			float x3 = (x2 - gears[0].pos.x);
			float y3 = (y2 - gears[0].pos.y);
			
			float angle = MathUtils.atan2(y3, x3);
			target_angle = (float) Math.toDegrees(angle);
			ProjectileObj proj = new ProjectileObj(gears[0].pos.x, gears[0].pos.y, 1, 1, LoadedProjectile[0], target_angle, 20);
			World.CurrentWorld.pendingWorldObjs.add(proj);
		}
		
		if(target != null)
		if(barrel_active) {
			
		if(timer > reloadTime) {
			if(CheckTarget(target)) {
			for(TurretPart part : gears) {
				if(part.type == MAINGEAR) {
					
					
					ProjectileObj proj = new ProjectileObj(gears[0].pos.x, gears[0].pos.y, 1, 1, LoadedProjectile[0], target_angle, 20);
					World.CurrentWorld.pendingWorldObjs.add(proj);

				}
			}
			}
			else {
				target = null;
				barrel_active = false;
			}
			timer = 0;
		} 
			
		timer += deltaTime;
		}
		else
			timer = 0;
		
		float closest = 500;
		if(target == null)
		if(scanTimer > scanDelay) {
			
			for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
				if(!obj.isAI)
					continue;
				
				if(obj.ai.friendly)
					continue;
				
				if(obj.ai.state == AI.STATE_DEAD) 
					continue;
				
				if(!CheckTarget(obj))
					continue;
				
				
				if(obj.ai.state == AI.STATE_SPAWNING)
					continue;
				
				if(obj.pos.dst(pos) < closest) {
					closest = obj.pos.dst(pos);
					target = obj;				
				}
				
				
			}

			scanTimer = 0;
		}
		
		scanTimer += deltaTime;
		lockOnTime += deltaTime;
	
	}

	float lerpf(float a, float b, float t)
	{
	    return a + (b - a) * t;
	}
	
	
	boolean CheckTarget(WorldObj target) {
		
		
		
		float x = pos.x + 0.5f;
		float y = pos.y + 0.5f;
		float angle = MathUtils.atan2((target.pos.y+0.5f)-y, (target.pos.x+0.5f)-x);
		
		
		int dst = (int)pos.dst(target.pos);
		
		for(int i=0; i<dst; i++) { 
			x += MathUtils.cos(angle);
			y += MathUtils.sin(angle);
			if(Terrain.CurrentTerrain.isSolid((int)x, (int)y))
				return false;
	
		}
		
		return true;
	}
}
