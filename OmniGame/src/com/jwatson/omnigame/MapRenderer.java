package com.jwatson.omnigame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.jar.JarException;

import com.jwatson.Animations.AssetManager;
import com.jwatson.omnigame.Map;
import com.jwatson.omnigame.Bob;
import com.jwatson.omnigame.InventoryObjects.Torch;
import com.jwatson.omnigame.ai.Pig;
import com.jwatson.omnigame.ai.WeakSkeleton;
import com.jwatson.omnigame.ai.basic_merchant;
import com.jwatson.omnigame.graphics.CustomBatch;
import com.jwatson.omnigame.screens.DifficultyScreen;
import com.jwatson.omnigame.screens.MainMenuScreen;
import com.jwatson.omnigame.screens.PauseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
//import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;


public class MapRenderer {
	
	public static int Version = 1;
	
	public static int Difficulty = 2;
	
	public static boolean JustTouched;
	
	public static boolean UsingShaders;
	
	public boolean ShowTutorial;
	
	
	OmniGame game;


	Item items;
	public World world;
	public Stage stage;
	
	public static OrthographicCamera CurrentCam;
	public static MapRenderer CurrentRenderer;
	OrthographicCamera cam;
	
	CustomBatch batch;
	
	public WorldObj checkBounds;
	int[][] blocks;
	Vector3 camPos;
	int WidthSection = 0;
	int HeightSection = 0;
	boolean NewMapSpawn;
	public Vector2 SpawnPos;
	public JItemAnimation ItemAnimation;
	public ShaderProgram shader;
	OnScreenNumpad NumPad;
	public int save_slot;
	public int char_slot;
	Color col;
	
	//tutorial stuff
	public boolean TutorialNextPage;
	public int TutorialPage;
	
	public List<WorldSprites> ToDraw;
	public List<WorldSprites> ToDrawRemove;
	TexturePacker2 t_packer;
	public static TextureAtlas Texture_Atlas = new TextureAtlas("data/tiles/tiles.atlas");
	public static TextureAtlas Texture_Atlas_Objs = new TextureAtlas("data/objs/tiles.atlas");
	TextureRegion tile;
	//Animations
	Animation bobLeft;
	Animation bobRight;
	Animation bobJumpLeft;
	Animation bobJumpRight;
	Animation bobIdleLeft;
	Animation bobIdleRight;
	Animation bobDead;
	Animation spawn;
	Animation dying;
	TextureRegion bobArm;
	TextureRegion bobArmFlip;
	SoundManager sound_manager;
	
	//background
	BackgroundManager plx_manager;
	
	MessageBox msgBox;
	
	GrowableManager GrowManager;
	public Trees TreeManager;
	ShapeRenderer shape;
	
	public MapRenderer (OmniGame game,int slot,boolean isLoading, boolean newChar, int charslot) {
		
		//Settings tpsettings = new Settings();
		

		this.game = game;
		//TexturePacker2.process("C:/OmniDig", "C:/OmniDig", "tiles");
		col = new Color(Color.WHITE);
		GrowManager = new GrowableManager();
		TreeManager = new Trees(GrowManager);
		items = new Item();
		
		this.cam = new OrthographicCamera(17, 10);
		camPos = new Vector3();
		createAnimations();
		save_slot = slot;
		char_slot = charslot;
		sound_manager = new SoundManager();
		shape = new ShapeRenderer();
		shape.setColor(Color.BLACK);
		ShaderProgram shader2 = new ShaderProgram(ShaderManager.terrain_vertex, ShaderManager.terrain_frag);
//		if(MapRenderer.UsingShaders)
//			batch = new CustomBatch(600);
//			else
				batch = new CustomBatch(600,shader2);
		//createBlocks();
		//mChunks = new MapChunks(this,5,5);
		//mChunks.LoadMap(new Pixmap(Gdx.files.internal("data/levels2.png")));
		//mChunks.MakeAllVisible();
		SpawnPos = new Vector2();
		if(MapRenderer.CurrentCam != null) MapRenderer.CurrentCam = null;
		if(MapRenderer.CurrentRenderer != null) MapRenderer.CurrentRenderer = null;
		MapRenderer.CurrentCam = cam;
		MapRenderer.CurrentRenderer = this;
		msgBox = new MessageBox();
		world = new World(this,isLoading, newChar, charslot);
		plx_manager = new BackgroundManager(cam);
		if(isLoading)
			world.terrain.LoadMap(slot);
		else {
			Terrain.CurrentTerrain.SetUpBiomes();
			SpawnPos.set((world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + world.terrain.chunkHeight);
			NewMapSpawn = true;
			ShowTutorial();
		}
			
			cam.position.set((world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + world.terrain.chunkHeight, 0);
		ToDraw = new ArrayList<WorldSprites>();
		ToDrawRemove = new ArrayList<WorldSprites>();
		
		
		//Gdx.input.setInputProcessor(new OmniInput());
	
		
		
		
		
		
		int x = (int)(SpawnPos.x/Terrain.CurrentTerrain.chunkWidth);
		int y = (int)(SpawnPos.y/Terrain.CurrentTerrain.chunkHeight);
		
		
		Terrain.CurrentTerrain.GetChunkByID(x+1, y);
		Terrain.CurrentTerrain.GetChunkByID(x, y-1);
		Terrain.CurrentTerrain.GetChunkByID(x-1, y);
		world.SpawnBob((int)SpawnPos.x,(int)SpawnPos.y);
//		int counter = 0;
//		for(int i = 0; i < AI.MAX_SPAWN; i++) {
//		world.SpawnedObjects.add(new WeakSkeleton(world,(world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + 20,1,1));
//		if(counter <= AI.MAX_SPAWN_DAY)
//		world.SpawnedObjects.add(new Pig(world,(world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + 20,1,1));					
//		counter++;
//		}
//		
		world.SpawnedObjects.add(new basic_merchant(world,(world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + 20,1,1));
		

		NumPad = new OnScreenNumpad();

		
		Gdx.input.setCatchBackKey(true);
		
	}

	void ShowTutorial() {
		// TODO Auto-generated method stub
		MessageBoxButton next = new MessageBoxButton("Next") {
			
			@Override
			public void onClicked(int... args) {
				// TODO Auto-generated method stub
				TutorialNextPage = true;
				TutorialPage += 2;
			}
		};
		MessageBox.CreateMessageBox(TutorialManager.Tutorial[0], TutorialManager.Tutorial[1], 30, 320, 115, 0.3f,next);
	}

	
	private void createAnimations () {
		TextureRegion bobTexture2 = MapRenderer.Texture_Atlas_Objs.findRegion("bobrun");
//		bobTexture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion[] split = MapRenderer.Texture_Atlas_Objs.findRegion("greendeath").split(8, 8)[0];
		TextureRegion[] split3 = bobTexture2.split(8, 8)[0];
		TextureRegion[] mirror2 = new TextureRegion(bobTexture2).split(8, 8)[0];
		for (TextureRegion region : mirror2)
			region.flip(true, false);
		bobRight = AssetManager.Animation.get("bobRight");
		bobLeft = AssetManager.Animation.get("bobLeft");
		bobArm = MapRenderer.Texture_Atlas_Objs.findRegion("bobarm");
		bobArmFlip = new TextureRegion(bobArm);
		bobArmFlip.flip(true, false);
		bobJumpRight = AssetManager.Animation.get("bobJumpRight");
		bobJumpLeft = AssetManager.Animation.get("bobJumpLeft");
		bobIdleRight = new Animation(0.5f, split3[0]);
		bobIdleLeft = new Animation(0.5f, mirror2[0]);
		bobDead = new Animation(0.2f, split[0]);
		spawn = new Animation(0.1f, split3[0]);
		dying = new Animation(0.1f, split[0],split[1],split[2],split[3],split[4],split[5]);
}
	Vector3 lerpTarget = new Vector3();
	
	float m_lightUpdateSpeed = 2f; // 300 ms
	float m_updateTime;

	private float keyPressedTime;
	private float touchTime;
	private float animTime;
	
	Vector3 culling = new Vector3();
	float spawnTime;
	public void render(float delta) {
		// TODO Auto-generated method stub
		//if (Gdx.input.isKeyPressed(Keys.A))camPos.x--;
		//if (Gdx.input.isKeyPressed(Keys.D))camPos.x++;
		//if (Gdx.input.isKeyPressed(Keys.W))camPos.y++;
		//if (Gdx.input.isKeyPressed(Keys.S))camPos.y--;
		
		
		
		
		if(ItemAnimation == null)
			animTime += delta;
		else
			animTime = 0;
		 

		if(NewMapSpawn) {
		if(!MessageBox.MessageBoxActive)
			spawnTime += delta;
		else
			spawnTime = 0;
		}
		
		if(NewMapSpawn)
			if(spawnTime > 0.1f) {
				world.SpawnBob((int)SpawnPos.x,(int)SpawnPos.y);
				NewMapSpawn = false;
			}
		if(TutorialNextPage) {
			TutorialNextPage = false;
			
			
			MessageBoxButton next = new MessageBoxButton("Next") {
				
				@Override
				public void onClicked(int... args) {
					// TODO Auto-generated method stub
					TutorialNextPage = true;
					TutorialPage += 2;
				}
			};
			MessageBoxButton back = new MessageBoxButton("Back") {
				
				@Override
				public void onClicked(int... args) {
					// TODO Auto-generated method stub
					TutorialNextPage = true;
					TutorialPage -= 2;
				}
			};
			
			if(TutorialPage < TutorialManager.Tutorial.length-2) {
				if(TutorialPage >= 2)
					MessageBox.CreateMessageBox(TutorialManager.Tutorial[TutorialPage], TutorialManager.Tutorial[TutorialPage+1], 30, 320, 115, 0.3f,next,back);
				else
					MessageBox.CreateMessageBox(TutorialManager.Tutorial[TutorialPage], TutorialManager.Tutorial[TutorialPage+1], 30, 320, 115, 0.3f,next);
			}
			else {
				MessageBox.CreateMessageBox(TutorialManager.Tutorial[TutorialPage], TutorialManager.Tutorial[TutorialPage+1], 30, 320, 115, 0.3f,back);
			}
			}
		
		
		
		if(Gdx.input.isTouched()) {
			
			if(touchTime == 0) {
				JustTouched = true;
			}
			else {
				JustTouched = false;
			}
			touchTime += delta;
		}
		else
		touchTime = 0;
		
	
		if(Bob.CurrentBob != null)
		cam.position.set(Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,0);
		else
			cam.position.set(SpawnPos.x,SpawnPos.y,0);
		cam.update();
		
		
				
		plx_manager.render();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		world.terrain.render(batch);
		renderWorldObjs(batch);
		
		if(Bob.CurrentBob != null)
		renderbob(batch);
		
		
		
		drawFoliage();
		world.m_LiquidManager.render(batch);
		

		
		batch.end();
		if(Bob.CurrentBob != null)
		if(world.InvBag != null)
		world.InvBag.render();

		if(Bob.CurrentBob != null)
		if(Bob.CurrentBob.Grabbed) {
			shape.setProjectionMatrix(cam.combined);
//			shape.setTransformMatrix(cam.);
			shape.begin(ShapeType.Line);
			
			shape.line(Bob.CurrentBob.pos.x + 0.5f, Bob.CurrentBob.pos.y + 0.5f, Bob.CurrentBob.Grabpos.x, Bob.CurrentBob.Grabpos.y);
			shape.end();
		}
		else if(Bob.CurrentBob.Grabber != null) {
			if(Bob.CurrentBob.Grabber.isShooting) {
				shape.setProjectionMatrix(cam.combined);
				shape.begin(ShapeType.Line);
				
				shape.line(Bob.CurrentBob.pos.x + 0.5f, Bob.CurrentBob.pos.y + 0.5f,(Bob.CurrentBob.pos.x + 0.5f) + (Bob.CurrentBob.Grablen * MathUtils.cosDeg(Bob.CurrentBob.Grabber.angle2)), (Bob.CurrentBob.pos.y + 0.5f) +(Bob.CurrentBob.Grablen * MathUtils.sinDeg(Bob.CurrentBob.Grabber.angle2)));
				shape.end();
			}
		}
		



		
		msgBox.render(delta);
		
		if(Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.isPaused = true;
			Inventory.CurrentInventory.ReleaseHeld();
			game.setScreen(new MainMenuScreen(game));
		}
		
		
		
	}
	public void dispose () {
		
		Terrain.CurrentTerrain.SaveMap(save_slot);
		world.SaveCharacter(char_slot);
		
		batch.dispose();
		MapRenderer.CurrentRenderer = null;
		Bob.CurrentBob = null;
		Item.Items = null;
		Item.b = 0;
		Inventory.CurrentInventory = null;
		
		//tile.texture.dispose();
	}
	
	public boolean IsInView(int x, int y) {
		
		//Vector3 temp = Vector3.tmp;
		//temp.x = x;
		//temp.y = y;

		return true;

	}
	
	void renderbob (CustomBatch batch2) {
		boolean flag = false;
		Animation anim = null;
		if(Bob.CurrentBob.state == Bob.CurrentBob.DEAD)
			return;
		

		
		boolean loop = true;
		if (Bob.CurrentBob.state == Bob.RUN) {
			if (Bob.CurrentBob.dir == Bob.LEFT)
				anim = MapRenderer.CurrentRenderer.bobLeft;
			else
				anim = MapRenderer.CurrentRenderer.bobRight;
		}
		if (Bob.CurrentBob.state == Bob.IDLE) {
			if (Bob.CurrentBob.dir == Bob.LEFT)
				anim = MapRenderer.CurrentRenderer.bobIdleLeft;
			else
				anim = MapRenderer.CurrentRenderer.bobIdleRight;
		}
		if (Bob.CurrentBob.state == Bob.JUMP) {
			if (Bob.CurrentBob.dir == Bob.LEFT)
				anim = MapRenderer.CurrentRenderer.bobJumpLeft;
			else
				anim = MapRenderer.CurrentRenderer.bobJumpRight;
		}
		if (Bob.CurrentBob.state == Bob.SPAWN) {
			anim = MapRenderer.CurrentRenderer.spawn;
			loop = false;
		}
		if (Bob.CurrentBob.state == Bob.DYING) {
			anim = MapRenderer.CurrentRenderer.dying;		
			loop = false;
		}
		float a = 0;
		if(Bob.CurrentBob.state != Bob.SPAWN)
		a = (float)Terrain.CurrentTerrain.GetLightTile((int)(Bob.CurrentBob.pos.x+0.5f), (int)(Bob.CurrentBob.pos.y+0.5f));
		
		
		if(Gdx.input.isKeyPressed(Keys.F3) && Gdx.input.justTouched() ) {
			//Gdx.app.debug((int)(Bob.CurrentBob.pos.x % Terrain.CurrentTerrain.chunkWidth) + " " +(int)(Bob.CurrentBob.pos.y % Terrain.CurrentTerrain.chunkHeight), ""+(int)(Bob.CurrentBob.pos.x / Terrain.CurrentTerrain.chunkWidth) + " " +(int)(Bob.CurrentBob.pos.y / Terrain.CurrentTerrain.chunkHeight));
			Inventory.CurrentInventory.quickBarActive = false;
		}
		a /= 15f;

		if(a < 0.6f)
			a = 0.6f;


		
		

		
		
		
		
		if(ItemAnimation != null) {
			
			
			
//			ItemAnimation.setColor(col);
			if(Bob.CurrentBob.AnimationStyle == InvObject.ANIMATE_STYLE_ROTATE) {
			if(Bob.CurrentBob.AnimTime >= Bob.CurrentBob.totalAnimTime) {
				flag = true;
			}
			if(Bob.CurrentBob.dir == Bob.LEFT)
				ItemAnimation.setOrigin(0.25f, 0.25f);
			else
				ItemAnimation.setOrigin(0.75f, 0.25f);
			//ItemAnimation.setScale(0.7f);
			ItemAnimation.setPosition(Bob.CurrentBob.pos.x + 0.25f*Bob.CurrentBob.ItemAnimdir, Bob.CurrentBob.pos.y+0.2f);
			
			
			if(Bob.CurrentBob.dir != Bob.CurrentBob.ItemAnimdir) {
				ItemAnimation.flip(true, false);
				Bob.CurrentBob.ItemAnimdir = Bob.CurrentBob.dir;
			}
			
			ItemAnimation.setRotation(-((Bob.CurrentBob.dir*45)+(Bob.CurrentBob.dir*90 * (Bob.CurrentBob.AnimTime/Bob.CurrentBob.totalAnimTime))));
			}
		if(Bob.CurrentBob.AnimationStyle == InvObject.ANIMATE_STYLE_THRUST_ANGLE) {
				
				float cos = MathUtils.cosDeg((ItemAnimation.getRotation()-90))*Bob.CurrentBob.AnimTime;
				float sin = MathUtils.sinDeg((ItemAnimation.getRotation()-90))*Bob.CurrentBob.AnimTime;
				float newX = ((Bob.CurrentBob.pos.x) - cos);
				float newY = ((Bob.CurrentBob.pos.y) - sin);
				Bob.CurrentBob.ItemPos.set(newX,newY);
				if(Bob.CurrentBob.ItemPos.dst(Bob.CurrentBob.pos.x,(Bob.CurrentBob.pos.y)) < 0.3f)
					ItemAnimation.setPosition(newX, newY);
				else
					ItemAnimation.setPosition(((Bob.CurrentBob.pos.x)-MathUtils.cosDeg((ItemAnimation.getRotation()-90))*0.3f),(Bob.CurrentBob.pos.y)-MathUtils.sinDeg((ItemAnimation.getRotation()-90))*0.3f);
				ItemAnimation.draw(batch2);
				Bob.CurrentBob.AnimTime += Gdx.graphics.getDeltaTime()*6;
				if(Bob.CurrentBob.AnimTime >= 2)
				flag = true;
			}
		
		if(Bob.CurrentBob.AnimationStyle == InvObject.ANIMATE_STYLE_THRUST) {
			
//			if(AnimTime < totalAnimTime/2)
//			ItemLerpPos.set(pos.x+(dir*0.5f),pos.y+0.1f);
//			else
//			ItemLerpPos.set(pos.x,pos.y+0.1f);	
//			
//			ItemPos.lerp(ItemLerpPos, 0.4f);
			if(Bob.CurrentBob.dir != Bob.CurrentBob.ItemAnimdir) {
				ItemAnimation.flip(true, false);
				Bob.CurrentBob.ItemAnimdir = Bob.CurrentBob.dir;
			}
			
			if(Bob.CurrentBob.dir == Bob.LEFT)
				ItemAnimation.setOrigin(0, 0.2f);
			else
				ItemAnimation.setOrigin(1, 0.2f);
			
			ItemAnimation.setRotation(-135*Bob.CurrentBob.dir);
			//float newX = Bob.CurrentBob.pos.x - (0.3f * Bob.CurrentBob) + (Bob.CurrentBob.AnimTime*Bob.CurrentBob.dir);
			//float dist = Math.abs(Bob.CurrentBob.pos.x - newX);
//			if(dist < 0.4f)
//			ItemAnimation.setX(newX);
//			else
//				ItemAnimation.setX(pos.x+(0.4f*dir));
			float x;
			if(Bob.CurrentBob.AnimTime < Bob.CurrentBob.totalAnimTime/2)
				x = (Bob.CurrentBob.pos.x) - Bob.CurrentBob.dir*0.4f + (Bob.CurrentBob.dir*(Bob.CurrentBob.AnimTime/Bob.CurrentBob.totalAnimTime));
			else
				x = (Bob.CurrentBob.pos.x) - (Bob.CurrentBob.dir*0.4f) + (1*Bob.CurrentBob.dir-(Bob.CurrentBob.dir*(Bob.CurrentBob.AnimTime/Bob.CurrentBob.totalAnimTime)));
			
			
			ItemAnimation.setPosition(x - 0.2f*Bob.CurrentBob.dir, Bob.CurrentBob.pos.y+0.1f);
			
			if(Bob.CurrentBob.AnimTime >= Bob.CurrentBob.totalAnimTime) {
				Bob.CurrentBob.AnimTime = 0;
				flag = true;
				Bob.CurrentBob.isAttacking = false;
			}
			
		}
		
		ItemAnimation.draw(batch2);
		
		if(flag)
			ItemAnimation = null;
		
		
		}
		if(animTime == 0) {
			if(Bob.CurrentBob.dir == Bob.LEFT)
				batch2.draw(MapRenderer.CurrentRenderer.bobArmFlip, Bob.CurrentBob.pos.x, Bob.CurrentBob.pos.y+0.375f,0,0,0.25f,0.125f,1,1,0);
			else
				batch2.draw(MapRenderer.CurrentRenderer.bobArm,1, Bob.CurrentBob.pos.x+0.75f, Bob.CurrentBob.pos.y+0.375f,0.25f,0.125f);
		}
		
		if(animTime > 0) {
		if(Bob.CurrentBob.holdingTorch) {
			batch2.setColor(col);
			if(Bob.CurrentBob.dir == Bob.LEFT)
				batch2.draw(MapRenderer.CurrentRenderer.bobArmFlip, Bob.CurrentBob.pos.x, Bob.CurrentBob.pos.y+0.375f,0,0,0.25f,0.125f,1,1,0);
			else
				batch2.draw(MapRenderer.CurrentRenderer.bobArm,1, Bob.CurrentBob.pos.x+0.75f, Bob.CurrentBob.pos.y+0.375f,0.25f,0.125f);
			
			if(Bob.CurrentBob.holdingTorch) {
				batch2.draw(Item.Items[Torch.ID].anim.getKeyFrame(Bob.CurrentBob.stateTime,true),a, Bob.CurrentBob.pos.x+(Bob.CurrentBob.dir*0.5f), Bob.CurrentBob.pos.y+0.3f,1,1);
			}
			
		}
		else if(Bob.CurrentBob.state == Bob.JUMP) {
			if(Bob.CurrentBob.dir == Bob.LEFT)
				batch2.draw(MapRenderer.CurrentRenderer.bobArmFlip, Bob.CurrentBob.pos.x+0.125f, Bob.CurrentBob.pos.y+0.625f,0,0,0.25f,0.125f,1,1,-90);
			else
				batch2.draw(MapRenderer.CurrentRenderer.bobArm, Bob.CurrentBob.pos.x+0.875f, Bob.CurrentBob.pos.y+0.375f,0,0,0.25f,0.125f,1,1,90);
		}
		else {
			if(Bob.CurrentBob.dir == Bob.LEFT)
				batch2.draw(MapRenderer.CurrentRenderer.bobArmFlip, Bob.CurrentBob.pos.x+0.25f, Bob.CurrentBob.pos.y+0.25f,0,0,0.25f,0.125f,1,1,90);
			else
				batch2.draw(MapRenderer.CurrentRenderer.bobArm, Bob.CurrentBob.pos.x+0.75f, Bob.CurrentBob.pos.y+0.5f,0,0,0.25f,0.125f,1,1,-90);
		}
		
		}
		batch2.draw(anim.getKeyFrame(Bob.CurrentBob.stateTime, loop),1, Bob.CurrentBob.pos.x, Bob.CurrentBob.pos.y, 1, 1);
		
		for(int i=0; i<4; i++) {
			if(Inventory.CurrentInventory.Armor[i][1] <= 0)
				continue;
			
			if(Item.Items[Inventory.CurrentInventory.Armor[i][0]].type != InvObject.Type.ARMOR_BOOTS) {
			if(Bob.CurrentBob.dir == Bob.RIGHT)
			batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[0],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
			else
				batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[1],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
			}
			else {
				
				if(Bob.CurrentBob.state == Bob.IDLE) {
					if(Bob.CurrentBob.dir == Bob.RIGHT) {
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[0],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
					}
					else
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[3],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
				}
				else if(Bob.CurrentBob.state == Bob.JUMP) {
					if(Bob.CurrentBob.dir == Bob.RIGHT) {
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[2],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
					}
					else
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[5],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
				}
				else if(Bob.CurrentBob.state == Bob.RUN) {
					if(Bob.CurrentBob.dir == Bob.RIGHT) {
						if(anim.getKeyFrameIndex(Bob.CurrentBob.stateTime) == 0)
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[0],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
						else
							batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[1],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
					}
					else {
						if(anim.getKeyFrameIndex(Bob.CurrentBob.stateTime) == 0)
						batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[3],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
						else
							batch2.draw(Item.Items[Inventory.CurrentInventory.Armor[i][0]].worn_texture[4],a, Bob.CurrentBob.pos.x,Bob.CurrentBob.pos.y,1,1);
					}
				}
				
			}
			
			}

	}
	
	void renderWorldObjs(CustomBatch batch2) {
		
		for(WorldObj obj : World.CurrentWorld.SpawnedObjects) {
			obj.render(batch2);
		}
		
		
	}
	
public void renderobj(SpriteBatch batch, WorldObj obj) {
		
		if(!obj.isActive)
			return;
		if(!obj.custom_render) {
		if(!obj.isAI) {
		InvObject item = Inventory.CurrentInventory.Items[obj.ID];
		if(item.alt_worldobj_texture == null)
		batch.draw(item.thumbnail,obj.pos.x,obj.pos.y,obj.Width,obj.Height);
		else
			batch.draw(item.alt_worldobj_texture,obj.pos.x,obj.pos.y,obj.Width,obj.Height);
		}
		}

		
		if(obj.healthBarTimer < 3.5f)
			if(obj.isAI)
				if(obj.ai.type != AI.TYPE_STATIC)
			if(World.CurrentWorld.r[8].overlaps(obj.bounds)) {
				
				batch.draw(Inventory.CurrentInventory.hp_bar, obj.pos.x-0.577f, obj.pos.y+1f,2,0.277f);
				float percent = obj.ai.HP / obj.ai.MaxHP;
				batch.draw(Inventory.CurrentInventory.hp_colour, obj.pos.x-0.577f, obj.pos.y+1f,percent*2f,0.277f);
			}
		
	}

	void drawFoliage() {
		
		for(TerrainChunk chk : Terrain.CurrentTerrain.ActiveChunks) {
			culling.set(chk.x*Terrain.chunkWidth + 10, chk.y*Terrain.chunkHeight + 10, 0);
			if(!cam.frustum.sphereInFrustum(culling, 10));
			
			for(int i=0; i<chk.TerrainMap.length; i++) {
				if(chk.TerrainMap[i]==0)
					continue;
				
				int b = chk.TerrainMap[i];
				
				if(Item.Items[b].type != InvObject.Type.FOLIAGE)
					continue;
				
				int c = chk.LightMap2[i];
				float a2 = (float)c/15f;

				
				float a = (int)((1-a2)*255f);
				if(a <= 0) {
					chk.LightMap2[i]=(byte)15;
					a= 1;
				}
				if(a>=255f) {
					chk.LightMap2[i]=(byte)0;
					a=255f;
					
					if(chk.y <= Terrain.Height/2)
						continue;
				}
				
//				parent.shader.setUniformf("brightness", 1-a2);
//				col.a = a;
//				batch.setColor(col);
				batch.draw(Item.Items[b].thumbnail,a2, (chk.x * Terrain.chunkWidth) +(i%Terrain.chunkWidth),(chk.y * Terrain.chunkHeight) + (i/Terrain.chunkWidth),1,1);
//				batch.setColor(Color.WHITE);
			}
		}
		
	}
	
}

