package com.jwatson.omnigame;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jwatson.omnigame.graphics.CustomBatch;

public class ProjectileObj extends WorldObj {


	InvObject ProjItem;
	TextureRegion ProjTexture;
	Sprite ProjSprite;
	Vector2 lastPos;
	float scanTimer;
	float scanDelay;
	float stuckTimer;
	
	public ProjectileObj(float x, float y, float width, float height, int id, float angle, float vel) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		ProjItem = Item.Items[id];
		ProjTexture = ProjItem.thumbnail;
		ProjSprite = new Sprite(ProjTexture);
		ID = id;
		this.vel.x = vel*MathUtils.cosDeg(angle);
		this.vel.y = vel*MathUtils.sinDeg(angle);
		rotation = angle;
		lastPos = new Vector2(-1,-1);
		is_Projectile = true;
		MapRenderer.CurrentRenderer.checkBounds = this;
		bounds.setWidth(0.01f);
		bounds.setHeight(0.01f);
		this.MaxSpeed = 20f;
	}
	
	@Override
	public void onCollision(boolean x, boolean y) {
		// TODO Auto-generated method stub
		super.onCollision(x, y);
		
		removalflag = true;
	}
	
	@Override
	public void OnCollision(Bob bob) {
		// TODO Auto-generated method stub
		super.OnCollision(bob);
		if(!stuckOnBob) {
		bob.OnDamage(null, 20, 5);
		stuckOnBob = true;
		stuck_offset = new Vector2(Bob.CurrentBob.pos.x - pos.x,Bob.CurrentBob.pos.y - pos.y );
		bounds.x = -1;
		bounds.y = -1;
		}
		}
	
	
	@Override
	void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		if(stuckOnBob) {
			stuckTimer += deltaTime;
			if(stuckTimer > 4f || Bob.CurrentBob.state == Bob.DYING) {
				removalflag = true;
				stuckTimer = 0;
			}
		}
		if(stuckOn != null) {
			stuckTimer += deltaTime;
		if(stuckOn.removalflag || stuckTimer > 4f) {
			removalflag = true;
			stuckTimer = 0;
		}
		}
		if(stuckOn == null && !stuckOnBob) {
		if(lastPos.x > 0)
			if(!removalflag) {
		rotation = (float) Math.toDegrees(MathUtils.atan2((pos.y-lastPos.y),(pos.x-lastPos.x)));
		
			}
		
		lastPos.set(pos.x,pos.y);
		
		
		for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
			if(!obj.isAI)
				continue;
			
			if(bounds.overlaps(obj.bounds)) {
				bounds.setX(1);
				bounds.setY(1);
				obj.onDamaged(this, ProjItem, 16);
				stuckOn = obj;
				stuck_offset = new Vector2(obj.pos.x - pos.x,obj.pos.y - pos.y );
			}
		}
		}
		
		
	}
	
	@Override
	public void render(CustomBatch batch) {
		// TODO Auto-generated method stub
		super.render(batch);
	}

}
