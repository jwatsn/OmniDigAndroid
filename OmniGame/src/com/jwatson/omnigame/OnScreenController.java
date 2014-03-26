package com.jwatson.omnigame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class OnScreenController extends ClickListener {

	
	Rectangle joystickArea;
	TextureRegion joystick_Outer,joystick_Inner;
	TextureRegion d_pad;
	SpriteBatch batch;
	Vector2 j_outer_pos,j_inner_pos,center;
	public static Stage stage;
	float dX,dY;
	public static int touchPointer = -1;
	public static OnScreenController Controller;
	public static boolean draw_joystick;
	boolean joystick_enabled;
	public Actor act;
	public Actor gamescreen;
	public static boolean down,up,left,right,up_right,down_right,up_left,down_left;
	
	
	public int ControlScheme = 2;
	float dpad_scale = 5f;
	float dpad_width,dpad_height;
	
	
	class d_padListener extends ClickListener {
		
		public d_padListener() {
			
		}
		
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			// TODO Auto-generated method stub
			int x2 = (int)(x/(dpad_scale*10));
			int y2 = (int)(y/(dpad_scale*10));
			up = false;
			down = false;
			left = false;
			right = false;
			up_right = false;
			down_right = false;
			up_left = false;
			down_left = false;
			
			
			Gdx.app.debug("", "HELLO");
			
			if(x2 == 0 && y2 == 1) {
				left = true;
			}
			else if(x2 == 2 && y2 == 1) {
				right = true;
			}
			else if(x2 == 1 && y2 == 2)
				up = true;
			else if(x2 == 1 && y2 == 0)
				down = true;
			else if(x2 == 2 && y2 == 2)
				up_right = true;
			else if(x2 == 2 && y2 == 0)
				down_right = true;
			else if(x2 == 0 && y2 == 2)
				up_left = true;
			else if(x2 == 0 && y2 == 0)
				down_left = true;
	
			return super.touchDown(event, x, y, pointer, button);
		}
		
		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			// TODO Auto-generated method stub
			
			int x2 = (int)(x/(dpad_scale*10));
			int y2 = (int)(y/(dpad_scale*10));
			up = false;
			down = false;
			left = false;
			right = false;
			up_right = false;
			down_right = false;
			up_left = false;
			down_left = false;
			
			
			if(x2 == 0 && y2 == 1) {
				left = true;
			}
			else if(x2 == 2 && y2 == 1) {
				right = true;
			}
			else if(x2 == 1 && y2 == 2)
				up = true;
			else if(x2 == 1 && y2 == 0)
				down = true;
			else if(x2 == 2 && y2 == 2)
				up_right = true;
			else if(x2 == 2 && y2 == 0)
				down_right = true;
			else if(x2 == 0 && y2 == 2)
				up_left = true;
			else if(x2 == 0 && y2 == 0)
				down_left = true;
			
			super.touchDragged(event, x, y, pointer);
		}
		
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			// TODO Auto-generated method stub
			super.touchUp(event, x, y, pointer, button);
			up = false;
			down = false;
			left = false;
			right = false;
			up_right = false;
			down_right = false;
			up_left = false;
			down_left = false;
		}
		
	};
	
	
	public OnScreenController() {
		// TODO Auto-generated constructor stub
		
		if(Controller != null)
			Controller = null;
		
		Controller = this;
		
		stage = new Stage(DisplayManager.Width,DisplayManager.Height,false);
		
		j_outer_pos = new Vector2();
		j_inner_pos = new Vector2();
		center = new Vector2();
		
		joystickArea = new Rectangle();
		
		joystickArea.set(0, 62, 100, 150);
		
		gamescreen = new Actor();
		gamescreen.setBounds(0, 0, DisplayManager.Width, 264);
		gamescreen.setPosition(0, 56);
		
		joystick_Outer = MapRenderer.Texture_Atlas_Objs.findRegion("joystick_outer");
		joystick_Inner = MapRenderer.Texture_Atlas_Objs.findRegion("joystick_inner");
		d_pad = MapRenderer.Texture_Atlas_Objs.findRegion("GUI_DPad");
		
		dpad_width = d_pad.getRegionWidth()*dpad_scale;
		dpad_height = d_pad.getRegionHeight()*dpad_scale;
		
		
		act = new Actor();
		act.setBounds(0,0,dpad_width, dpad_height);
		stage.addActor(gamescreen);
		stage.addActor(act);
		act.setVisible(true);
		act.setPosition(5, 14);
		act.setTouchable(Touchable.enabled);
		
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, DisplayManager.Width,DisplayManager.Height);

		
		stage.addListener(this);
		Gdx.input.setInputProcessor(stage);
		act.addListener(new d_padListener());
	
	}
	
	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		// TODO Auto-generated method stub

		super.drag(event, x, y, pointer);
	}
	
	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		// TODO Auto-generated method stub
		
		TouchPos.set(x,y);
		super.touchDragged(event, x, y, pointer);
	}
	
	Vector2 TouchPos = new Vector2();
	Vector2 TouchAngle = new Vector2();
	public void render(float delta) {
		
		//stage.act(delta);
		stage.draw();
		
		
		

//		if(draw_joystick) {
//			x0 = (Gdx.input.getX(touchPointer) / (float)Gdx.graphics.getWidth()) * 480;
//			y0 = ((Gdx.graphics.getHeight()-Gdx.input.getY(touchPointer)) / (float)Gdx.graphics.getHeight()) * 320;
//		}
//		
		
		
		
		if(Inventory.CurrentInventory.BagActive)
			return;
		
		if(draw_joystick) {
			
			TouchAngle.set(TouchPos);
			TouchAngle.sub(j_outer_pos.x+32,j_outer_pos.y+32);
			
			if(TouchPos.dst(j_outer_pos.x+32, j_outer_pos.y+32) < 32)
				j_inner_pos.set(TouchPos.x-10,TouchPos.y-10);
			else {
				

				float Angle = TouchAngle.angle();
				j_inner_pos.x = (j_outer_pos.x+22) + 32 * MathUtils.cosDeg(Angle);
				j_inner_pos.y =(j_outer_pos.y+22) + 32 * MathUtils.sinDeg(Angle);
				
			}
			
			if(TouchPos.dst(center) > 12f) {
			float movex = ((j_inner_pos.x+10)-(j_outer_pos.x+32))/32;
			float movey = ((j_inner_pos.y+10)-(j_outer_pos.y+32))/32;
			if(Math.abs(movey) > 0.5f) {
				if(movey < 0) {
					down = true;
					up = false;
				}
				else {
					down = false;
					up = true;
				}
			}
			

			
			if(Math.abs(movex) > 0.3f) {
			Bob.CurrentBob.moveX = movex;
			if(movex > 0) {
				left = false;
				right = true;
				Bob.CurrentBob.dir = Bob.RIGHT;
				//MapRenderer.CurrentRenderer.bobRight.frameDuration = 1 - movex;
			}
			else {
				left = true;
				right = false;
				Bob.CurrentBob.dir = Bob.LEFT;
				//MapRenderer.CurrentRenderer.bobLeft.frameDuration = 1 - movex;
			}
			
			
			
			Bob.CurrentBob.accel.x = Bob.ACCELERATION * movex;
//			if(Bob.CurrentBob.accel.x > 0)
//			Bob.CurrentBob.state = Bob.RUN;
			}
			}
			else {
				up = false;
				down = false;
				left = false;
				right = false;
			}
		}
		else if(ControlScheme == 1) {
			up = false;
			down = false;
			left = false;
			right = false;
		}
		
		if(draw_joystick) {
		batch.begin();
		batch.draw(joystick_Outer, j_outer_pos.x,j_outer_pos.y);
		batch.draw(joystick_Inner, j_inner_pos.x, j_inner_pos.y);
		batch.end();
		}
		
		if(ControlScheme == 2) {
			batch.begin();
			batch.draw(d_pad,5, 14,dpad_width,dpad_height);
			batch.end();
		}
		
	
		
	}
	
	public static int getNumTouched() {
		
		for(int i=0; i<10; i++) {
			
			if(Gdx.input.isTouched(i) == false) {
				if(i==0)
					return 1;
				return i;
			}
			
		}
		return 10;
		
	}
	
	
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		// TODO Auto-generated method stub
		
		if(ControlScheme == 1) {
		
		if(!joystickArea.contains(x, y))
			return false;
		TouchPos.set(x,y);

		j_outer_pos.set(x-32,y-32);
		j_inner_pos.set(TouchPos.x-10,TouchPos.y-10);
		
		if(x < 32)
			j_outer_pos.x = 0;
		draw_joystick = true;
		
		center.set(j_outer_pos.x + 16,j_outer_pos.y + 16);
		
		touchPointer = pointer;
		
		}
		else if(ControlScheme == 2) {
			
			
		}
		return super.touchDown(event, x, y, pointer, button);
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		// TODO Auto-generated method stub
		draw_joystick = false;
		touchPointer = -1;
		super.touchUp(event, x, y, pointer, button);
	}

}
