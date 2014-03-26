package com.jwatson.omnigame.InventoryObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InvHookObject;
import com.jwatson.omnigame.InvObject;
import com.jwatson.omnigame.Inventory;
import com.jwatson.omnigame.MapRenderer;
import com.jwatson.omnigame.Terrain;
import com.jwatson.omnigame.UpdatableItem;
import com.jwatson.omnigame.World;
import com.jwatson.omnigame.WorldSprites;

public class GrapplingHook extends InvHookObject {


	float magX = 0;
	float magY = 0;
	byte[] tiles;
//	float angle2;
	UpdatableItem update;

	
	public GrapplingHook(int id,String nme) {
		super(id);
		// TODO Auto-generated constructor stub
		name = nme;
		descName = "Grappling Hook";
		thumbnail = MapRenderer.Texture_Atlas.findRegion(nme);
		type = InvObject.Type.HELD;
		Distance = 7;
		ATK = 10;
		Delay = 60;
		DefaultMine = false;
		HoldTouch = false;
		batch = new SpriteBatch();
		MaxLen = 5;
		Grabpos = new Vector2();
		bounds = new Rectangle();
		Price = 20;
		
	}
	
	@Override
	public void OnUse(Bob bob,float x, float y, float angle) {
		// TODO Auto-generated method stub
		super.OnUse(bob,x,y,angle);
		if(bob.Grabbed && flag && Gdx.input.justTouched()) {
			Grabbed = false;
			parentinv.getOwnerBob().Grabbed = false;
			parentinv.getOwnerBob().Grablen = 0;
//			grappleGfx.deleted = true;
			isUpdating = false;
			isShooting = false;
			len = 0;
			parentinv.getOwnerBob().LastUsed = TimeUtils.millis();
			//Vector2 vel = parentinv.getOwnerBob().LastPos.sub(parentinv.getOwnerBob().bounds.x,parentinv.getOwnerBob().bounds.y);
			//vel.x *= parentinv.getOwnerBob().omega * 100;
			//vel.y *= parentinv.getOwnerBob().omega * 100;
			
			parentinv.getOwnerBob().vel.set(parentinv.getOwnerBob().Xspeed*50,parentinv.getOwnerBob().Yspeed*50);
			parentinv.getOwnerBob().Swinging = true;
			parentinv.getOwnerBob().omega = 0;
			return;
		}
		if(flag && Gdx.input.justTouched())
		{	
			
			float x0 = Gdx.input.getX(0);
			float y0 = Gdx.graphics.getHeight() - Gdx.input.getY(0);
			
			
			float x2 = parentinv.getOwnerBob().pos.x  + 0.5f;
			float y2 = parentinv.getOwnerBob().pos.y + 0.5f;
			
			angle2 = angle;
			

			//Gdx.app.debug("", ""+dist*Math.cos(angle) +  " " +dist*Math.sin(angle));
			if(grappleGfx == null || grappleGfx.deleted) {
				isShooting = true;
				bob.Grabber = this;
				bob.firstShot = true;
				
			}
			else {

				/*
				int i = MapRenderer.CurrentRenderer.ToDraw.indexOf(grappleGfx);
				Vector3 pos = new Vector3();
				pos.x = Gdx.input.getX(0);
				pos.y = Gdx.input.getY(0);
				MapRenderer.CurrentCam.unproject(pos);
				MapRenderer.CurrentRenderer.ToDraw.get(i).x = x2-2;
				MapRenderer.CurrentRenderer.ToDraw.get(i).y = y2-2;
				
				Pixmap map = new Pixmap(32,32, Format.Alpha);
				map.setColor(0,255,0,180);
				map.drawPixel((int)magX, (int)magY);
				magX += Math.cos(angle);
				magY += Math.sin(angle);
				*/
				//Gdx.app.debug("", ""+magX + " "+magY);
				//MapRenderer.CurrentRenderer.ToDraw.get(i).texture = new Texture(map);
				//MapRenderer.CurrentRenderer.ToDraw.get(i).Height += 0.2f;
				//MapRenderer.CurrentRenderer.ToDraw.get(i).Width = 0.3f;
				
				
			}

		}
		
	}

	
	
	
	
	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

	int LastX;
	int LastY;
	
	void fetchCollidableRects2(int X, int Y) {
		int cWidth = Terrain.CurrentTerrain.chunkWidth;
		int cHeight = Terrain.CurrentTerrain.chunkHeight;
		
		int X2 = (int)(X%cWidth);
		int Y2 = (int)(Y%cHeight);
		
		if(LastX != (X/cWidth) || LastY != (Y/cHeight))
		tiles = Terrain.CurrentTerrain.GetCollisionData(X/cWidth,Y/cHeight);
		
		//Gdx.app.debug(""+LastX + " " +LastY, ""+(bobX/cWidth)+" "+(bobY/cHeight));
		//long nano = TimeUtils.nanoTime();
		
		if(X2 + (Y2*cWidth) < cWidth*cHeight)
		{
			if(tiles[X2 + (Y2*cWidth)] > 0)
			r[0].set(X,Y,1,1);
			else
				r[0].set(-1,-1,1,1);
		}
		else
		{
			if(Terrain.CurrentTerrain.GetTile(X, Y) > 0) {
				
			}
		}
		if((X2+1) < cWidth && (Y2) < cHeight && X2 > 0 && Y2 > 0)
		if(tiles[(X2+1) + (Y2*cWidth)] > 0)
		r[1].set(X+1,Y,1,1);
		else
			r[1].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(X+1, Y) > 0)
			r[1].set(X+1,Y,1,1);
		
		if((X2+1) < cWidth && (Y2+1) < cHeight && X2 > 0 && Y2 > 0)
		if(tiles[(X2+1) + ((Y2+1)*cWidth)] > 0)
		r[2].set(X+1,Y+1,1,1);
		else
			r[2].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(X+1, Y+1) > 0)
			r[2].set(X+1,Y+1,1,1);
		
		if((X2) < cWidth && (Y2+1) < cHeight && X2 > 0 && Y2 > 0)
		if(tiles[X2 + ((Y2+1)*cWidth)] > 0)
		r[3].set(X,Y+1,1,1);
		else
			r[3].set(-1,-1,1,1);
		else if(Terrain.CurrentTerrain.GetTile(X, Y+1) > 0)
			r[3].set(X,Y+1,1,1);
		//Gdx.app.debug("TOOKNANO", ""+X+ " " +Y);	
		boolean changed;
		
		LastX = X/cWidth;
		LastY = Y/cHeight;

	}

}
