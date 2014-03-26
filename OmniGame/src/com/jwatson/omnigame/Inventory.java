package com.jwatson.omnigame;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jwatson.omnigame.InvObject.Type;
import com.jwatson.omnigame.InventoryObjects.*;

public class Inventory extends ClickListener {
	
	//STRINGS
	static float TextHeight;
	
	static String c_tab = "Crafting";
	static String b_tab = "Bag";
	
	static String buy_tab = "Items";
	static String blueprint_tab = "Blueprints";
	
	
	
	
	
	
	//merchant stuff
	int NumItemsSale = 0;
	int NumBlueprintsSale = 0;
	
	
	
	public static int MERCHANT_BUY = 1;
	public static int MERCHANT_SELL = 2;
	
	public static int FILTER_ITEMS = 1;
	public static int FILTER_BLUEPRINTS = 2;
	
	ScrollPane scroll_pane;
	Actor blueprint_list;
	
	//inventory bar stuff
	TextureRegion InvBar;
	TextureRegion InvBag;
	TextureRegion selectedBox;
	TextureRegion CraftTexture;
	TextureRegion merchantTexture;
	TextureRegion chestTexture;;
	TextureRegion hp_bar;
	TextureRegion hp_colour;
	TextureRegion DropButton;
	TextureRegion attackbutton_texture;
	TextureRegion jumpbutton_texture;

	TextureRegion[] border;
	TextureRegion[] arrows;
	Label HPText1;
	Label HPText2;
	Label HPText3;
	float totalExplored = 0;
	int SelectedAmount = 0;
	int[] Requirements;
	boolean holding_chest_item;
	boolean canCraft;
	String currentChest = "";
	int chestX,chestY;
	int merchantType;
	int totalCoins;
	int tapCounter1;
	float tapCounter2;
	float tapSpeed = 0.5f;
	boolean[][] swap;
	boolean touchHold;
	float touchCounter;
	
	//Button bounds
	Rectangle[] quickbar_bounds;
	Rectangle jumpbutton_bounds;
	Rectangle invbag_bounds;
	Rectangle attackbutton_bounds;
	Rectangle tap_bounds = new Rectangle();
	Rectangle close_button_bounds;
	Rectangle drop_button_bounds;
	Rectangle tab_bounds;
	public Rectangle tab1_bounds;
	Rectangle tab2_bounds;
	Rectangle bag_nextpg;
	Rectangle bag_prevpg;
	
	Actor quickbar_act;
	Actor action_act;
	//Chest area bounds
	Rectangle inventoryBounds;
	Rectangle chestBounds;
	Rectangle chest_nextpg;
	Rectangle chest_prevpg;
	Matrix4 projMatrix;
	
	int fps,count;
	float fpscounter;
	
	public HashMap<String, String> Chests;
	int[][][] chestItems;
	int[][][] merchantItems;
	
	int curChestPage;
	int curPage = 1;
	int maxChestPage = 6;
	int maxMerchantPage = 6;
	Stage stage;
	float dX,dY;
	
	int MaxHP;
	
	int currSelected;
	
	TextureRegion Bag;
	TextureRegion[] numbers;
	
	public Bob owner;	
	public static Inventory CurrentInventory;
	
	//Item list
	public InvObject[] Items;
	
	public float inv_scale = 4;
	
	InvObject selectedItem;
	int selectedBagId;
	
	Rectangle[][] itembounds;
	
	boolean HasTouched;
	boolean FirstTouch = true;
	boolean quickBarActive = true;
	public BitmapFont Font;
	public BitmapFont Font2;
	public BitmapFont Font3;
	public BitmapFont Font4;
	
	List<dmgText> dmgTxts = new ArrayList<dmgText>();
	List<dmgText> dmgTxtsRemove = new ArrayList<dmgText>();
	
	//Bag Stuff
	int BagWidth = 6;
	int BagHeight = 4;
	
	int ChestWidth = 2;
	int ChestHeight = 3;
	
	int MaxItems = 100;
	
	InvObject Held;
	int HeldAmt;
	float HeldX;
	float HeldY;
	
	int PrevX;
	int PrevY;
	AI CurrentMerchant;
	Pixmap map;
	Texture mapText;
	
	int InvBarWidth,InvBarHeight;
	
	public boolean BagActive;
	Vector2 BagPos;
	int[][] BagItem;
	int[][] Armor;
	List<InvObject> CraftingItems;
	SpriteBatch batch;
	int x;
	int hpbar_x;
	private float stateTime;
	float touchTime;
	private boolean CraftActive;
	public boolean ChestActive;
	public boolean MerchantActive;
	private float totalExploredTimer;
	public int filter;

	private String InfoText;
	public Inventory() {
		
		if(CurrentInventory != null)
			CurrentInventory = null;
		CurrentInventory = this;
		
		attackbutton_texture = MapRenderer.Texture_Atlas_Objs.findRegion("GUI_Button_Attack");
		jumpbutton_texture = MapRenderer.Texture_Atlas_Objs.findRegion("GUI_Button_Jump");
		InvBar = MapRenderer.Texture_Atlas_Objs.findRegion("GUI_Quickbar_Box");
		InvBag = MapRenderer.Texture_Atlas_Objs.findRegion("GUI_Button_Inventory");
		selectedBox = MapRenderer.Texture_Atlas_Objs.findRegion("selected");
		Bag = MapRenderer.Texture_Atlas_Objs.findRegion("bag");
		hp_bar = MapRenderer.Texture_Atlas_Objs.findRegion("HUD_Health");
		hp_colour = MapRenderer.Texture_Atlas_Objs.findRegion("HUD_Health_Overlay");
		DropButton = MapRenderer.Texture_Atlas_Objs.findRegion("invbutton");
		CraftTexture  = MapRenderer.Texture_Atlas_Objs.findRegion("crafting");
		chestTexture = MapRenderer.Texture_Atlas_Objs.findRegion("chestScreen");
		merchantTexture = MapRenderer.Texture_Atlas_Objs.findRegion("merchant");
		MaxHP = hp_bar.getRegionWidth()*4;
		swap = new boolean[BagWidth*ChestWidth][BagHeight*ChestHeight];
		CraftingItems = new ArrayList<InvObject>();
		
		
		
		//scroll_pane = new ScrollPane(widget)
		
		border = MapRenderer.Texture_Atlas_Objs.findRegion("border").split(3, 3)[0];
		arrows = MapRenderer.Texture_Atlas_Objs.findRegion("inv_arrows").split(10, 10)[0];
		Chests = new HashMap<String, String>();
		
		projMatrix = new Matrix4();
		projMatrix.setToOrtho2D(0, 0, 480, 320);
		
		//hp text stuff
		LabelStyle tfs = new LabelStyle();
		Font = new BitmapFont(Gdx.files.internal("data/dmgnumbers.fnt"),false);
		BitmapFont font = new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		Font2 = new BitmapFont(Gdx.files.internal("data/smallfnt.fnt"),false);
		Font3 = font;
		Font4 = new BitmapFont(Gdx.files.internal("data/invnumbers.fnt"),false);
		tfs.font = font;
		tfs.fontColor = new Color(0, 0, 255, 255);
		HPText1 = new Label("", tfs);
		HPText1.setText("10");
		HPText1.setAlignment(Align.right);
		
		HPText2 = new Label("", tfs);
		HPText2.setText("/");
		
		HPText3 = new Label("", tfs);
		HPText3.setText("10");
		HPText3.setAlignment(Align.left);
		//HPText.setX(HPText.getText().length() * 16);
		BagPos = new Vector2((DisplayManager.Width/2)-((Bag.getRegionWidth()*inv_scale)/2),(DisplayManager.Height/2)-((Bag.getRegionHeight()*inv_scale)/2)+20);
		
		
		jumpbutton_bounds = new Rectangle(DisplayManager.Width - 60 - 60,10,60,60);
		invbag_bounds = new Rectangle(DisplayManager.Width - 46,10,40,40);
		attackbutton_bounds = new Rectangle(DisplayManager.Width - 66,66,60,60);
		close_button_bounds = new Rectangle(BagPos.x + 432, BagPos.y + 3,40,40);
		drop_button_bounds = new Rectangle(BagPos.x + 388, BagPos.y + 3,40,40);
		tab_bounds = new Rectangle(BagPos.x, BagPos.y +232, 276, 48);
		tab1_bounds = new Rectangle(BagPos.x, BagPos.y +232, 136, 48);
		tab2_bounds = new Rectangle(BagPos.x+140, BagPos.y +232, 136, 48);
		
		inventoryBounds = new Rectangle(BagPos.x, BagPos.y, 276, 256);
		chestBounds = new Rectangle(BagPos.x+277, BagPos.y, 204, 256);
		chest_prevpg = new Rectangle(BagPos.x + 280,BagPos.y + 8,40,40);
		chest_nextpg = new Rectangle(BagPos.x +324,BagPos.y + 8,40,40);
		bag_prevpg = new Rectangle(BagPos.x + 8,BagPos.y + 8,40,40);
		bag_nextpg = new Rectangle(BagPos.x + 228,BagPos.y + 8,40,40);
		
		TextHeight = Font.getCapHeight();
		
		BagItem = new int[BagWidth * BagHeight][2];
		Armor = new int[BagHeight][2];
		merchantItems = new int[BagWidth * BagHeight][2][maxMerchantPage];
		chestItems = new int[ChestWidth * ChestHeight][2][maxChestPage];
//		LoadNumbers();
		batch = new SpriteBatch(400);
		

		//Auto center
		
		Items = Item.Items;
		
		itembounds = new Rectangle[BagWidth][BagHeight];

		batch.getProjectionMatrix().setToOrtho2D(0, 0, DisplayManager.Width, DisplayManager.Height);
		
		InvBarWidth = InvBar.getRegionWidth()*4;
		InvBarHeight = InvBar.getRegionHeight()*4;
		
		x = (DisplayManager.Width/2) - 92;
		hpbar_x = 240 - (hp_bar.getRegionWidth()/2);
		
		HPText1.setPosition((hpbar_x + hp_bar.getRegionWidth()/2), 61);
		HPText1.setVisible(true);
		HPText2.setPosition(hpbar_x + hp_bar.getRegionWidth()/2, 61);
		HPText2.setVisible(true);
		HPText3.setPosition((hpbar_x + hp_bar.getRegionWidth()/2)+8, 61);
		HPText3.setVisible(true);
		//MapRenderer.CurrentRenderer.stage.addActor(HPText1);
		//MapRenderer.CurrentRenderer.stage.addActor(HPText2);
		//MapRenderer.CurrentRenderer.stage.addActor(HPText3);
		//Initialize items
		
		
		for(InvObject invobj : Items) {
			if(invobj == null)
				continue;
			invobj.parentinv = this;
		}
		
		action_act = new Actor();
		action_act.setBounds(0, 0, 134, 120);
		action_act.setPosition(DisplayManager.Width-134, 0);
		action_act.addListener(new actionListener());
		OnScreenController.stage.addActor(action_act);
		
		quickbar_act = new Actor();
		quickbar_act.setBounds(0, 0, 184, 40);
		quickbar_act.setPosition(x, 14);
		quickbar_act.addListener(new quickbarListener());
		OnScreenController.stage.addActor(quickbar_act);
		quickbar_bounds = new Rectangle[5];
		
		for(int i=0; i<4; i++) {
				quickbar_bounds[i] = new Rectangle(x+(i*48),10,40,40);
		}
		quickbar_bounds[4] = new Rectangle(x+216,14,44,44);
		
		
		OnScreenController.stage.addListener(this);
		
		for(int i = 0; i < Items.length; i++) {
			
			if(Items[i] == null)
				continue;
		
			
		}
		
		
	}

	
	
	public Inventory(boolean aiflag, Bob Owner) {
		Items = Item.Items;
		BagItem = new int[5 * 4][2];
		for(InvObject invobj : Items) {
			if(invobj == null)
				continue;
			invobj.parentinv = this;
		}
		owner = Owner;
	}
	
	boolean CloseFlag = false;
	
	public void render() {
		batch.begin();
		
		
		if(Bob.CurrentBob.disableInvGFX)
			return;
		//batch.setProjectionMatrix(projMatrix);
//		Font.draw(batch, ""+fps, DisplayManager.Width-30, DisplayManager.Height - 30);
//		Font.draw(batch, ""+Terrain.CurrentTerrain.bb, DisplayManager.Width-40, DisplayManager.Height - 50);
//		if(!CraftActive) {
//		if(SelectedAmount > BagItem[selectedBagId][1] && selectedItem != null)
//			SelectedAmount = BagItem[selectedBagId][1];
//		if(SelectedAmount == 0)
//			SelectedAmount = 1;
//		}
//		else {
//			
//	
//			
//		}
		DrawQuickBar(batch);
		//if(!BagActive) {
			DrawHealthBar(batch);
//			Color col = batch.getColor();
//			batch.setColor(col);
			batch.draw(jumpbutton_texture, jumpbutton_bounds.x, jumpbutton_bounds.y,jumpbutton_bounds.width,jumpbutton_bounds.height);
			batch.draw(attackbutton_texture, attackbutton_bounds.x, attackbutton_bounds.y,attackbutton_bounds.width,attackbutton_bounds.height);
//			batch.setColor(Color.WHITE);
		//}
		if(!BagActive && !CloseFlag) {
			CloseFlag = true;
			if(MerchantActive) {
				MerchantActive = false;
			}
			
			if(ChestActive) {
				clearChestItems();
				ChestActive = false;
				
				int chx = chestX/Terrain.CurrentTerrain.chunkWidth;
				int chy = chestY/Terrain.CurrentTerrain.chunkHeight;
				int chx2 = chestX%Terrain.CurrentTerrain.chunkWidth;
				int chy2 = chestY%Terrain.CurrentTerrain.chunkHeight;
				
				TerrainChunk ch = Terrain.TerrainChunks[chx + chy*Terrain.CurrentTerrain.Width];
				if(ch != null) {
					
					ch.stateMap[chx2 + chy2*Terrain.CurrentTerrain.chunkWidth] = 0;
					
				}
			}
			
			HPText1.setVisible(true);
			HPText2.setVisible(true);
			HPText3.setVisible(true);
		 }
		if(BagActive && CloseFlag) {
			HPText1.setVisible(false);
			HPText2.setVisible(false);
			HPText3.setVisible(false);
			CloseFlag = false;

		}
		
		if(BagActive)
		{
			
			
			
			DrawInfo(batch);
//			if(!CraftActive) {
				
			if(ChestActive)
				batch.draw(chestTexture, BagPos.x,BagPos.y, chestTexture.getRegionWidth()*4,chestTexture.getRegionHeight()*4);
			else if(MerchantActive) {
				batch.draw(merchantTexture, BagPos.x,BagPos.y,480,256);
//				if(filter == FILTER_ITEMS)
//				DrawButton(batch, "Blueprints", 266, BagPos.y+1, 85, 34);
//				else
//					DrawButton(batch, "Items", 276, BagPos.y+1, 75, 34);
				
			}
			else if(CraftActive) {
				
				
				batch.draw(CraftTexture, BagPos.x,BagPos.y,CraftTexture.getRegionWidth()*4,CraftTexture.getRegionHeight()*4);
				
			}
			else {
				batch.draw(Bag, BagPos.x,BagPos.y,Bag.getRegionWidth()*4,Bag.getRegionHeight()*4);


			}
			
			DrawInfo(batch);
			//}

			if(RightArrowActive()) {
				batch.draw(arrows[1], BagPos.x + 228, BagPos.y+8,40,40);
			}
			if(PrevArrowActive()) {
				batch.draw(arrows[0], BagPos.x + 8, BagPos.y+8,40,40);
			}
			if(CraftActive) {
				
				int counter = 0;
				for(InvObject obj : CraftingItems) {
					float drawX = BagPos.x +12 + ((counter%BagWidth)*44);
					float drawY = BagPos.y+188 - (44*(counter/BagWidth));
					batch.draw(obj.thumbnail, drawX, drawY,32,32);
					DrawNumber(batch,GetCraftableAmount(obj.CraftingRequirements), drawX, drawY);
					counter++;
				}
			
			}
			else if(ChestActive) {
				for(int x=0; x<BagWidth; x++) {
					for(int y=0; y<BagHeight; y++) {
						int i = x + (y*BagWidth);
						if(BagItem[i][1] > 0) {
							if(BagItem[i][0] == 0)
								continue;
							float drawX = BagPos.x + 12 + (x*44);
							float drawY = BagPos.y+188 - (44*y);
							if(Items[BagItem[i][0]].type != InvObject.Type.BLUEPRINT)
							batch.draw(Items[BagItem[i][0]].thumbnail, drawX, drawY,32,32);
							else {
								InvObject item = Items[BagItem[i][0]];
								
								batch.draw(item.thumbnail, drawX, drawY,32,32);
								batch.draw(Items[item.BluePrintID].thumbnail, drawX+8, drawY+8,16,16);
								
							}
							DrawNumber(batch,BagItem[i][1],drawX,drawY);
						}
						
					}
			}
			for(int x=0; x<ChestWidth; x++) {
				for(int y=0; y<ChestHeight; y++) {
					int i = x + (y*ChestWidth);
					if(chestItems[i][1][curChestPage] > 0) {
						if(chestItems[i][0][curChestPage] == 0)
							continue;
						
						float drawX = 284 + ((BagPos.x) + (x)*44);
						float drawY = (BagPos.y +144)-y*44;
						if(Items[chestItems[i][0][curChestPage]].type != InvObject.Type.BLUEPRINT)
						batch.draw(Items[chestItems[i][0][curChestPage]].thumbnail, drawX, drawY,32,32);
						else {
							InvObject item = Items[chestItems[i][0][curChestPage]];
							
							batch.draw(item.thumbnail, drawX, drawY,32,32);
							batch.draw(Items[item.BluePrintID].thumbnail, drawX+8, drawY+8,16,16);
							
						}
						DrawNumber(batch,chestItems[i][1][curChestPage],drawX,drawY);
					
					
				}
			}
			
			}
			}

			else if(MerchantActive) {
				for(int x=0; x<BagWidth; x++) {
					for(int y=0; y<BagHeight; y++) {
						int i = x + (y*BagWidth);
						if(merchantItems[i][1][curPage] > 0) {
							if(merchantItems[i][0][curPage] == 0)
								continue;
							float drawX = BagPos.x + 12 + (x*44);
							float drawY = BagPos.y+188 - (44*y);
							if(Items[merchantItems[i][0][curPage]].type != InvObject.Type.BLUEPRINT)
							batch.draw(Items[merchantItems[i][0][curPage]].thumbnail, drawX, drawY,32,32);
							else {
								InvObject item = Items[merchantItems[i][0][curPage]];
								
								batch.draw(item.thumbnail, drawX, drawY,32,32);
								batch.draw(Items[item.BluePrintID].thumbnail, drawX+8, drawY+8,16,16);
								
							}
							DrawNumber(batch,merchantItems[i][1][curPage],drawX,drawY);
						}
						
					}
				}
				
			} 
			else {
				for(int x=0; x<BagWidth; x++) {
					for(int y=0; y<BagHeight; y++) {
						int i = x + (y*BagWidth);
						if(BagItem[i][1] > 0) {
							if(BagItem[i][0] == 0)
								continue;
							float drawX = BagPos.x + 12 + (x*44);
							float drawY = BagPos.y+188 - (44*y);
							
							if(Items[BagItem[i][0]].type != InvObject.Type.BLUEPRINT)
							batch.draw(Items[BagItem[i][0]].thumbnail, drawX, drawY,32,32);
							else {
								InvObject item = Items[BagItem[i][0]];
								
								batch.draw(item.thumbnail, drawX, drawY,32,32);
								batch.draw(Items[item.BluePrintID].thumbnail, drawX+8, drawY+8,16,16);
								
							}
							DrawNumber(batch,BagItem[i][1],drawX,drawY);
						}
						
					}
			}
				for(int i=0; i<BagHeight; i++) {
					if(Armor[i][1] > 0) {
						float drawX = BagPos.x + 276;
						float drawY = BagPos.y+188 - (44*i);
						batch.draw(Items[Armor[i][0]].thumbnail, drawX, drawY,32,32);
					}
				}
			}
			//DrawButton(batch, "x", 480-26, BagPos.y+(250-26), 20, 20);
			if(Held != null)
			{
			
			batch.draw(Held.thumbnail, HeldX,HeldY,32,32);
			if(Held.type == InvObject.Type.BLUEPRINT)
				batch.draw(Item.Items[Held.BluePrintID].thumbnail, HeldX+8,HeldY+8,16,16);
			DrawNumber(batch,HeldAmt, HeldX, HeldY);

			}
			
				

				
			//}
		}


		
		if(dmgTxts.size() > 0)
			DrawDamageText(batch);
		
		batch.end();
		
	}
	
	class actionListener extends ClickListener {
		
		public actionListener() {
			
		}
		
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			// TODO Auto-generated method stub
			
			if(MessageBox.MessageBoxActive)
				return false;
			
			float x0 = event.getStageX();
			float y0 = event.getStageY();
			
			boolean jumpButton = jumpbutton_bounds.contains(x0, y0);
			boolean attackButton = attackbutton_bounds.contains(x0,y0);
			boolean bagButton = invbag_bounds.contains(x0, y0);
			

			
			if(jumpButton) {
				Bob.CurrentBob.bobJump();
			}
			if(attackButton)
				Bob.CurrentBob.attack_queue = true;
			if(bagButton)
				BagActive = !BagActive;
			
			
			return super.touchDown(event, x, y, pointer, button);
		}
		
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			// TODO Auto-generated method stub
			
			Bob.CurrentBob.attack_queue = false;
			super.touchUp(event, x, y, pointer, button);
		}
		
		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			// TODO Auto-generated method stub
			
			float x0 = event.getStageX();
			float y0 = event.getStageY();
			
			boolean jumpButton = jumpbutton_bounds.contains(x0, y0);
			boolean attackButton = attackbutton_bounds.contains(x0,y0);
			
			if(jumpButton) {
				Bob.CurrentBob.bobJump();
			}
			if(attackButton)
				Bob.CurrentBob.attack_queue = true;
			else
				Bob.CurrentBob.attack_queue = false;
			
			super.touchDragged(event, x, y, pointer);
		}
		
		
		
		
	}
	
	class quickbarListener extends ClickListener {
		
		public quickbarListener() {
			
		}
		
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			// TODO Auto-generated method stubo
			float x0 = event.getStageX();
			float y0 = event.getStageY();
			

			
			boolean Bag1 = (quickbar_bounds[0].contains(x0, y0) && Held == null);
			boolean Bag2 = (quickbar_bounds[1].contains(x0, y0) && Held == null);
			boolean Bag3 = (quickbar_bounds[2].contains(x0, y0) && Held == null);
			boolean Bag4 = (quickbar_bounds[3].contains(x0, y0) && Held == null);
			boolean Bag5 = (quickbar_bounds[4].contains(x0, y0));
			
			
			
			if(Bag1) {
				
				currSelected = 0;
				
			}
			else if(Bag2) {
				currSelected = 1;
			}
			else if(Bag3) {
				currSelected = 2;
			}
			else if(Bag4) {
				currSelected = 3;
					
			}
			else if(Bag5) {
					BagActive = !BagActive;
					//World.CurrentWorld.CreateClient();
			}
			
			return super.touchDown(event, x, y, pointer, button);
		}
		
		
	}
	
	private void clearChestItems() {
		// TODO Auto-generated method stub
		UpdateChest();
		for(int i=0; i < ChestHeight * ChestWidth; i++) {
			for(int j=0; j<maxChestPage; j++) {
			chestItems[i][0][j] = 0;
			chestItems[i][1][j] = 0;
			}
		}
	}

	void LoadNumbers() {
		

		
		TextureRegion[] split = new TextureRegion(new Texture(Gdx.files.internal("data/invnums.png"))).split(10, 12)[0];
		numbers = split;
	}
	
	public void update(float deltaTime) {
		count++;
		fpscounter += deltaTime;
		if(fpscounter >= 1) {
			fps = count;
			count = 0;
			fpscounter = 0;
		}
		
		if(!BagActive) {
			if(Held != null) {
				AddToBag(Held.InvObjID, HeldAmt);
				Held = null;
					

			}
			if(selectedItem != null)
				selectedItem = null;
		}
		
		

		
		if(MessageBox.MessageBoxActive) {
			touchHold = false;
			touchTime = 0;
			return;
		}
		
//		if(selectedItem == null)
//			touchHold = false;
		
		UpdateInput(deltaTime);
		if(touchHold)
			touchTime += deltaTime;
		else touchTime = 0;
		
		if(tapCounter1 > 0) {
		if(tapCounter2 > tapSpeed) {
			
			tapCounter1 = 0;
			tapCounter2 = 0;
		}
		tapCounter2 += deltaTime;
		}
	}
	boolean trybuy_flag;
	boolean trycraft_flag;
	boolean trydrop_flag;
	InvObject trybuy_obj;
	int trybuy_amt;
	void UpdateInput(float deltaTime) {
		
		if(trybuy_flag) {
			
			TryBuy(trybuy_obj, trybuy_amt);
			
			trybuy_flag = false;
			trybuy_obj = null;
			trybuy_amt = 0;
		}
		else if(trydrop_flag) {
			
			DropItem(trybuy_obj,trybuy_amt);
			trydrop_flag = false;
			trybuy_obj = null;
			trybuy_amt = 0;
			
		}
		else if(trycraft_flag) {
			AddToBag(trybuy_obj.InvObjID, trybuy_amt * trybuy_obj.CraftingMulti);
			for(int i =0; i< trybuy_amt; i++)
			RemoveFromBag(trybuy_obj.CraftingRequirements);
			updateCraftingList();
			trycraft_flag = false;
		}
		InfoText = GenInfoString();
		
		if(BagActive && totalExploredTimer >= 2f) {
			setTotalExplored();
			
//			if(MerchantActive) {
//				
//				if(SelectedAmount > merchantItems[selectedBagId][1]) {
//					
//					SelectedAmount = merchantItems[selectedBagId][1];
//					
//				}
//				
//			}
			totalExploredTimer = 0;
		}
		if(!BagActive)
			touchHold = false;
		
		if(!BagActive && CraftActive) {
			CraftActive = false;
			ChestActive = false;
			selectedItem = null;
		}
		
		
		float x0 = (Gdx.input.getX(0) / (float)Gdx.graphics.getWidth()) * DisplayManager.Width;
		float y0 = DisplayManager.Height - (Gdx.input.getY(0) / (float)Gdx.graphics.getHeight()) * DisplayManager.Height;
		
		float x1 = (Gdx.input.getX(1) / (float)Gdx.graphics.getWidth()) * DisplayManager.Width;
		float y1 = 320 - (Gdx.input.getY(1) / (float)Gdx.graphics.getHeight()) * DisplayManager.Height;
		
		
		boolean BagTouched = (y0 > 50);
		
		//boolean craftButton = (x0 >282 && x0 < 356 && y0 < BagPos.y + 40 && y0 > BagPos.y && BagActive && Gdx.input.justTouched() && !ChestActive);
		boolean closeButton = (close_button_bounds.contains(x0, y0) && BagActive);
		boolean dropButton = (drop_button_bounds.contains(x0,y0) && selectedItem != null && Gdx.input.justTouched());
		//boolean craftButton = (x0 > 379 && x0 < 450 && y0 > BagPos.y + 20 && y0 < BagPos.y + 55 && BagActive && selectedItem != null && Gdx.input.justTouched());
		//boolean invSelected = (x0 < 256 && y0 > BagPos.y +77);
		
		//boolean dec_selectedamt = (x0 > 87 && x0 < 129 && y0 > BagPos.y + 38 && y0 < BagPos.y + 70 && BagActive && Gdx.input.justTouched());
		//boolean inc_selectedamt = (x0 > 151 && x0 < 191 && y0 > BagPos.y + 38 && y0 < BagPos.y + 70 && BagActive && Gdx.input.justTouched());
		if(BagActive)
		if(dropButton) {
			
			if(MerchantActive) {
				
				MessageBoxButton item = new MessageBoxButton(""+selectedItem.InvObjID) {
					
					@Override
					public void onClicked(int...i) {
						
						trybuy_flag = true;
						trybuy_obj = Item.Items[i[1]];
						trybuy_amt = i[0];
					}
	
				};
				item.type = MessageBoxButton.ITEM;
				item.noClose = true;
				item.maxAmt = merchantItems[selectedBagId][1][curPage];
				
				
				MessageBox.CreateInputMessageBox_Buy(""+CurrentMerchant.name, "How many would you like to buy?\n", 90, 270, 220, 0.1f,item);
			}
			else if(CraftActive) {
				MessageBoxButton item = new MessageBoxButton(""+selectedItem.InvObjID) {
					
					@Override
					public void onClicked(int...i) {
						
						trycraft_flag = true;
						trybuy_obj = Item.Items[i[1]];
						trybuy_amt = i[0];
					}
	
				};
				item.type = MessageBoxButton.ITEM;
				item.noClose = true;
				item.maxAmt = GetCraftableAmount(selectedItem.CraftingRequirements);
				
				
				MessageBox.CreateInputMessageBox_Buy("", "How many would you like to craft?\n", 90, 270, 220, 0.1f,item);
			}
			else {
				MessageBoxButton item = new MessageBoxButton(""+selectedItem.InvObjID) {
					
					@Override
					public void onClicked(int...i) {
						
						RemoveFromBag(""+Item.Items[i[1]].name+ " " +i[0]);
						DropItem(Item.Items[i[1]], i[0]);

					}
	
				};
				item.type = MessageBoxButton.ITEM;
				item.noClose = true;
				item.maxAmt = BagItem[selectedBagId][1];
				
				
				MessageBox.CreateInputMessageBox_Buy("", "How many would you like to drop?\n", 90, 270, 220, 0.1f,item);
			}
			
			
		}
		
		
//		if(dec_selectedamt)
//			{
//				SelectedAmount--;
//				if(SelectedAmount < 1)
//					SelectedAmount = 32;
//			}
//		if(inc_selectedamt)
//		{
//			SelectedAmount++;
//			if(SelectedAmount > 32)
//				SelectedAmount = 1;
//		}
		if(Gdx.input.justTouched()) {
		if(tab_bounds.contains(x0, y0)) {
			if(MerchantActive) {
				if(tab1_bounds.contains(x0, y0))
					filter = FILTER_ITEMS;
				else if(tab2_bounds.contains(x0, y0))
					filter = FILTER_BLUEPRINTS;
				
				CreateMerchantScreen(CurrentMerchant, MERCHANT_BUY);
			}
			else if(!ChestActive) {
					if(tab2_bounds.contains(x0, y0))
						CraftActive = true;
					if(tab1_bounds.contains(x0, y0))
						CraftActive = false;
					
			selectedItem = null;
			updateCraftingList();
			}
		}
		}
		if(Gdx.input.justTouched() && closeButton) {
			BagActive = false;
			Bob.CurrentBob.DamagedTimer = 0.5f;
		}
		
		if(Gdx.input.isTouched() && BagActive && BagTouched)
			UpdateBag(x0,y0);
		
		if(!Gdx.input.isTouched() && HasTouched == true)
			HasTouched = false;
		
		FirstTouch = false;
		
//		if(Held != null) {
//			HeldX = x0 - (16);
//			HeldY = y0 - (16);
//		}
		
		if(!Gdx.input.isTouched() && !FirstTouch)
		{
			FirstTouch = true;
			if(Held != null) {
				
				float Rx = x0 - 12;
				float Ry = y0 - BagPos.y - 76;
				
				
				int bagX = (int)(Rx/42);
				int bagY = (BagHeight-1) - (int)(Ry/42);
		}
				

		}
		stateTime += deltaTime;
		touchTime += deltaTime;
		totalExploredTimer += deltaTime;
	}
	
	public Bob getOwnerBob() {
		if(owner == null)
			return Bob.CurrentBob;
		else
			return owner;
	}
	
	public Vector2 getOwnerPos() {
		if(owner == null) {
			return Bob.CurrentBob.pos;
		}
		else {
			return owner.pos;
		}
	}
	
	
	boolean draggingChest,draggingInv;
	
	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		// TODO Auto-generated method stub
		
		if(MessageBox.MessageBoxActive) {
			touchHold = false;
			touchTime = 0;
			return;
		}
				
		dX += Gdx.input.getDeltaX(0);
		dY += Gdx.input.getDeltaY(0);
		
		if(Math.abs(dX) >= 3 || Math.abs(dY) >= 3) {
			touchHold = false;
			touchTime = 0;
		}
		
		
		float Rx = x -BagPos.x - 12;
		float Ry = y - BagPos.y - 50;
		
		
		int bagX = (int)(Rx/44);
		int bagY = (BagHeight-1)-(int)(Ry/44);
		
		boolean itemArea = (inventoryBounds.contains(x,y));
		boolean chestArea = (chestBounds.contains(x,y));
		if(ChestActive) {
			
			if(chestArea) {
				
				Rx = x - BagPos.x- 284;
				Ry = y - BagPos.y - 50;


				
				
				
				bagX = (int)(Rx/44);
				bagY = (ChestHeight-1)-(int)(Ry/44);
				
			}
			else if(itemArea) {
				Rx = x -BagPos.x - 12;
				Ry = y - BagPos.y - 50;


				
				
				
				bagX = (int)(Rx/44);
				bagY = (BagHeight-1)-(int)(Ry/44);
			}
			
		}
		
//		for(int x2=0; x2<BagWidth; x2++)
//			for(int y2=0; y2<BagHeight; x2++)
//				if(itembounds[x2][y2].)
		if(bagX < 0 || bagY < 0)
			return;
		
		
		if(bagY < BagHeight && bagY >= 0) {
		if(Held == null)
		{
			//if(Math.abs(dX) > 3 || Math.abs(dY) > 3) {
			if(CraftActive) {
				if(bagX + (bagY*BagWidth) < CraftingItems.size()) {
				 Held = CraftingItems.get(bagX + (bagY*BagWidth));
				 HeldAmt = GetCraftableAmount(Held.CraftingRequirements);
				 
				 if(HeldAmt <= 0) {
					 Held = null;
					 return;
				 }
					
				}
			}
			else if(MerchantActive) {
				if(bagX <BagWidth && bagY < BagHeight) {
					if(merchantItems[bagX + (bagY*BagWidth)][1][curPage] > 0) {
					Held = Items[merchantItems[bagX + (bagY*BagWidth)][0][curPage]];
					HeldAmt = merchantItems[bagX + (bagY*BagWidth)][1][curPage];
					}
				}
			}
			else {
			if(bagX > 5) {
				if(Armor[bagY][1] > 0)
				{
					Held = Items[Armor[bagY][0]];
					HeldAmt = Armor[bagY][1];
					
					Armor[bagY][1] = 0;
					Armor[bagY][0] = 0;
					return;
				}
				
			}
			if(itemArea) {
			if(BagItem[bagX + (bagY*BagWidth)][1] > 0)
			{
				Held = Items[BagItem[bagX + (bagY*BagWidth)][0]];
				HeldAmt = BagItem[bagX + (bagY*BagWidth)][1];
				
				BagItem[bagX + (bagY*BagWidth)][1] -= HeldAmt;
				if(BagItem[bagX + (bagY*BagWidth)][1] <= 0)
					draggingInv = true;
				else
					draggingInv = false;
				PrevX = bagX;
				PrevY = bagY;
			}
			}
			
			if(ChestActive)
			if(chestArea) {
				if(chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage] > 0) {
				Held = Items[chestItems[(bagX) + (bagY*ChestWidth)][0][curChestPage]];
				if(SelectedAmount <= 0)
				HeldAmt = chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage];
				else
					HeldAmt = SelectedAmount;
				if(HeldAmt > chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage])
					HeldAmt = chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage];
				
				chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage] -= HeldAmt;
				if(chestItems[(bagX) + (bagY*ChestWidth)][1][curChestPage] <= 0)
					draggingChest = true;
				else
					draggingChest = false;
				
				
				PrevX = bagX;
				PrevY = bagY;
				
				UpdateChest();
				}
				
			}

		
		}
		
		//}
		
		}
		}
		else {
			selectedItem = Held;
		}
		
		if(Held != null) {
			HeldX = event.getStageX() - 16;
			HeldY = event.getStageY() - 16;
		}
		
		
		super.dragStart(event, x, y, pointer);
	}
	
	void UpdateBag(float x, float y) {
		
		
		

		
		if(touchTime > 1.5f || tapCounter1 >= 2) { 
			tapCounter1 = 0;
			tapCounter2 = 0;
			float Rx = x - BagPos.x - 12;
			float Ry = y - BagPos.y - 50;


			
			
			
			int bX = (int)(Rx/44);
			int bY = (BagHeight-1)-(int)(Ry/44);
			
			final boolean itemArea = (inventoryBounds.contains(x,y));
			final boolean chestArea = (chestBounds.contains(x,y));
			if(ChestActive) {
				
				if(chestArea) {
					
					Rx = x - BagPos.x - 284;
					Ry = y - BagPos.y - 50;


					
					
					
					bX = (int)(Rx/44);
					bY = (ChestHeight-1)-(int)(Ry/44);
					
				}
				else if(itemArea) {
					Rx = x - BagPos.x - 12;
					Ry = y - BagPos.y - 50;


					
					
					
					bX = (int)(Rx/44);
					bY = (BagHeight-1)-(int)(Ry/44);
				}
				
			}
			
			final int bagX = bX;
			final int bagY = bY;
			
			
			
			if(itemArea || chestArea) {
			
				if(!ChestActive)
					if(chestArea)
						return;
			
			int id = 0;
			int maxNum = 0;
			if(CraftActive) {
				if(bagX >= 0 && bagY >= 0) {
				if(bagX + bagY*BagWidth < CraftingItems.size()) {
					InvObject obj = CraftingItems.get(bagX + bagY*BagWidth);
					id = obj.InvObjID;
					maxNum = GetCraftableAmount(obj.CraftingRequirements);
				}
				}
			}
			else if(MerchantActive) {
				if(bagX + bagY*BagWidth < merchantItems[0].length)
				if(merchantItems[bagX + bagY*BagWidth][1][curPage] > 0) {
					id = merchantItems[bagX + bagY*BagWidth][0][curPage];
					maxNum = merchantItems[bagX + bagY*BagWidth][1][curPage];
				}
				
			}
			else {
				if(chestArea) {
					if(bagX >= 0 && bagY >= 0 && bagX < ChestWidth && bagY < ChestHeight) {
					id = chestItems[(bagX)+(bagY)*ChestWidth][0][curChestPage];
					maxNum = chestItems[(bagX)+(bagY)*ChestWidth][1][curChestPage];
					if(chestItems[(bagX)+(bagY)*ChestWidth][1][curChestPage] <= 0) {
						touchHold = false;
						touchTime = 0;
						return;
					}
					}
					
				}
				else if(itemArea) {
					if(bagX >= 0 && bagY >= 0 && bagX < BagWidth && bagY < BagHeight) {
					id = BagItem[bagX+bagY*BagWidth][0];
					maxNum = BagItem[bagX+bagY*BagWidth][1];
					if(BagItem[(bagX)+(bagY)*BagWidth][1] <= 0) {
						touchHold = false;
						touchTime = 0;
						return;
					}
					}
				}
			}
			if(id <= 0 || maxNum <= 0) {
				touchHold = false;
				touchTime = 0;
				return;
			}

			
			MessageBoxButton item = new MessageBoxButton(""+id) {
				
				@Override
				public void onClicked(int...i) {
					// TODO Auto-generated method stub
					if(i[0] > 0) {
						
						float x0 = (Gdx.input.getX(0) / (float)Gdx.graphics.getWidth()) * DisplayManager.Width;
						float y0 = DisplayManager.Height - (Gdx.input.getY(0) / (float)Gdx.graphics.getHeight()) * DisplayManager.Height;
						
						if(CraftActive) {
							Held = CraftingItems.get(bagX + bagY*BagWidth);
									
							HeldAmt = i[0];
							HeldX = x0 - (Held.thumbnail.getRegionWidth()/2);
							HeldY = y0 - (Held.thumbnail.getRegionHeight()/2);
						}
						else if(MerchantActive) {
							Held = Items[merchantItems[bagX + bagY * BagWidth][0][curPage]];
							HeldAmt = i[0];
							HeldX = x0 - (Held.thumbnail.getRegionWidth()/2);
							HeldY = y0 - (Held.thumbnail.getRegionHeight()/2);
						}
						else {
						if(chestArea) {
							Held = Items[chestItems[bagX + bagY * ChestWidth][0][curChestPage]];
							HeldAmt = i[0];
							HeldX = x0 - (Held.thumbnail.getRegionWidth()/2);
							HeldY = y0 - (Held.thumbnail.getRegionHeight()/2);
							chestItems[bagX + bagY * ChestWidth][1][curChestPage] -= i[0];
							if(chestItems[bagX + bagY * ChestWidth][1][curChestPage] <= 0)
								draggingChest = true;
							else
								draggingChest = false;
							
						}
						else if(itemArea) {
						Held = Items[BagItem[bagX + bagY * BagWidth][0]];
						HeldAmt = i[0];
						HeldX = x0 - 16;
						HeldY = y0 - 16;
						BagItem[bagX + bagY * BagWidth][1] -= i[0];
						if(BagItem[bagX + bagY * BagWidth][1] <= 0)
							draggingInv = true;
						else
							draggingInv = false;
						}
						}

					}
					touchHold = false;
				}
			};
			item.type = MessageBoxButton.ITEM;
			item.maxAmt = maxNum;
			//item.BagPos = bagX + bagY * BagWidth;
			if(bagX > 6) {
				
				item.isChestItem = true;
				//item.BagPos = bagX-7 + bagY * ChestWidth;
				
			}
			
			
			
			
			MessageBox.CreateInputMessageBox("", "How many would you like to take?\n", 90, 270, 220, 0.1f,item);
			touchHold = false;	
			}
		}
		
	}
	
	InvObject GetItemByName(String name) {
		for(InvObject obj : Items) {
			if(obj == null)
				continue;
		
			if(obj.name.equals(name))
				return obj;
		}
		
		return null;
	}
	
	InvObject GetItem(int id) {
		return Items[id];
		

	}
	
	float GetHP(int id) {
		return Items[id].HP;
	}
	
	float GetHP(byte id) {
		return Items[id].HP;
	}
	
	
	
	void AddToBag(int id, int amt) {
		
		
		for(int y=0; y<BagHeight; y++)
		{
			for(int x=0; x<BagWidth; x++) {
				
				int i = x + (y*BagWidth);
				
				if(BagItem[i][1] != 0 && id != BagItem[i][0] || BagItem[i][1] >= Items[id].MaxStack && amt > 0)
					continue;
				
				
				if(BagItem[i][1] + amt > Items[id].MaxStack) {
					amt -=  Items[id].MaxStack-BagItem[i][1];
					BagItem[i][1] = Items[id].MaxStack;
					BagItem[i][0] = id;
					continue;
				}
				
				if(BagItem[i][1] + amt < 0) {
					amt +=  BagItem[i][1];
					BagItem[i][1] = 0;
					BagItem[i][0] = id;
					continue;
					
				}
				
				BagItem[i][0] = id;
				BagItem[i][1] += amt;
				
				
				return;
			}
		}
	}
	
	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		
		return super.isPressed();
		
	}
	
	public void AddToBag(String name, int amt) {
		
		int id = 0;
		for(InvObject i : Items) {
			if(i == null)
				continue;
			if(i.name.equals(name)) {
				id = i.InvObjID;
				break;
			}
		}
		AddToBag(id, amt);
	}
	
	public void AddToBag(String name, int amt, int slot) {
		
		
		int id = 0;
		for(InvObject i : Items) {
			if(i == null)
				continue;
			if(i.name == name) {
				id = i.InvObjID;
				break;
			}
		}
		BagItem[slot][0] = id;
		BagItem[slot][1] += amt;
		return;
	}
	
	public void AddToBag(String name, int amt, boolean used) {
		
		
		int id = 0;
		for(InvObject i : Items) {
			if(i == null)
				continue;
			if(i.name == name) {
				id = i.InvObjID;
				break;
			}
		}
		BagItem[currSelected][0] = id;
		BagItem[currSelected][1] += amt;
		return;
	}
	
void AddToChest(int id, int amt) {
		
	for(int j=0; j<maxChestPage; j++) {	
	
		for(int y=0; y<ChestHeight; y++)
		{
			for(int x=0; x<ChestWidth; x++) {
				
				
				int i = x + (y*ChestWidth);
				//
				if(chestItems[i][1][j] != 0 && id != chestItems[i][0][j] || chestItems[i][1][j] >= Items[id].MaxStack && amt > 0)
					continue;
				
				
				if(chestItems[i][1][j] + amt > Items[id].MaxStack) {
					amt -=  Items[id].MaxStack-chestItems[i][1][j];
					chestItems[i][1][j] = Items[id].MaxStack;
					chestItems[i][0][j] = id;
					continue;
				}
				
				if(chestItems[i][1][j] + amt < 0) {
					amt +=  chestItems[i][1][j];
					chestItems[i][1][j] = 0;
					chestItems[i][0][j] = id;
					continue;
					
				}
				
				chestItems[i][0][j] = id;
				chestItems[i][1][j] += amt;
				
				
				return;
			}
		}
	}
	}

void AddToMerchant(int id, int amt) {
	
	
	for(int j=1; j<maxMerchantPage; j++) {
	for(int y=0; y<BagHeight; y++)
	{
		for(int x=0; x<BagWidth; x++) {
			
			int i = x + (y*BagWidth);
			
			
			if(merchantItems[i][1][j] != 0 && id != merchantItems[i][0][j] || merchantItems[i][1][j] >= Items[id].MaxStack && amt > 0)
				continue;
			
			
			if(merchantItems[i][1][j] + amt > Items[id].MaxStack) {
				amt -=  Items[id].MaxStack-merchantItems[i][1][j];
				merchantItems[i][1][j] = Items[id].MaxStack;
				merchantItems[i][0][j] = id;
				continue;
			}
			
			if(merchantItems[i][1][j] + amt < 0) {
				amt +=  merchantItems[i][1][j];
				merchantItems[i][1][j] = 0;
				merchantItems[i][0][j] = id;
				continue;
				
			}
			
			merchantItems[i][0][j] = id;
			merchantItems[i][1][j] += amt;
			
			
			return;
		}
	}
	}
}
	
	void DrawNumber(SpriteBatch batch,int number, float x, float y) {
		
		
		
		Font4.drawWrapped(batch, ""+number, x-3, y+13, 35, BitmapFont.HAlignment.RIGHT);
		
			
	}
	
	void DrawNumber(int number, SpriteBatch batch, float x, float y) {
		
		
		
		Font4.drawWrapped(batch, ""+number, x-3, y+13, 35, BitmapFont.HAlignment.RIGHT);
		
			
	}
	
	public String GetItemNameByID(int id) {
		for(InvObject e : Bob.CurrentBob.inventory.Items) {
			if(e == null)
				continue;
			if(id == e.InvObjID)
				return e.name;
		}
		return "";
	}
	
	void DrawQuickBar(SpriteBatch batch) {
		for(int i=0; i<4; i++) {
		batch.draw(InvBar,quickbar_bounds[i].x,quickbar_bounds[i].y,quickbar_bounds[i].width,quickbar_bounds[i].height);
		}
		batch.draw(selectedBox, quickbar_bounds[currSelected].x, quickbar_bounds[currSelected].y,40,40);
		batch.draw(InvBag, invbag_bounds.x, invbag_bounds.y,invbag_bounds.width,invbag_bounds.height);
		for(int i=0; i<4; i++) {
			if(BagItem[i][1] > 0) {
				InvObject obj = Items[BagItem[i][0]];
				batch.draw(obj.thumbnail,quickbar_bounds[i].x+4,quickbar_bounds[i].y+4,32,32);
				DrawNumber(batch,BagItem[i][1],quickbar_bounds[i].x+4,quickbar_bounds[i].y+4);
			}
		}
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		

		
		if(!BagActive)
			return false;
		
		if(ChestActive) {
			if(chest_nextpg.contains(x, y)) {
				curChestPage++;
				if(curChestPage>= maxChestPage)
					curChestPage = 0;
				return false;
			}
			else if(chest_prevpg.contains(x, y)) {
				curChestPage--;
				if(curChestPage < 0)
					curChestPage = maxChestPage-1;
				return false;
				
			}
		}
		else {
			if(bag_nextpg.contains(x, y)) {
				NextArrowPressed();
			}
			else if(bag_prevpg.contains(x, y)) {
				PrevArrowPressed();
			}
		}
		
		
		
		if(tapCounter1 <= 0)
			tap_bounds.set(x-5, y-5, 10, 10);
		
		if(tap_bounds.contains(x, y))
		tapCounter1++;
		else {
			tapCounter1 = 0;
			tapCounter2 = 0;
		}
		//Gdx.app.debug("WOT", "BRO");
		touchHold = true;
		touchTime = 0;
		dX=0;
		dY=0;
//		if(ChestActive)
//			return true;
		
		
		
		if(CraftActive) {
			
			float Rx =   x -BagPos.x - 12;
			float Ry = y - BagPos.y - 50;

			
			
			int bagX = (int)(Rx/44);
			int bagY = (BagHeight-1)-(int)(Ry/44);
			
			if(bagX >= BagWidth || bagY >=  BagHeight || bagX < 0 || bagY < 0)
				return false;
			
			int i = bagX + bagY*BagWidth;
			
			
			
			
			if(i < CraftingItems.size()) {
				selectedItem = CraftingItems.get(i);
				selectedBagId = i;
			}
			if(selectedItem != null) {
				
				if(GetCraftableAmount(selectedItem.CraftingRequirements) > 0)
					canCraft = true;
				else
					canCraft = false;
			String[] split = selectedItem.CraftingRequirements.split(" ");
			Requirements = new int[split.length];
			
			for(int i2=0; i2<split.length; i2+=2) {
				Requirements[i2] = GetItemID(split[i2]);
				Requirements[i2+1] = Integer.valueOf(split[i2+1]);
			}
			}
			
		}
		else if(MerchantActive) {
			float Rx = x -BagPos.x - 12;
			float Ry = y - BagPos.y - 50;
			
			
			int bagX = (int)(Rx/44);
			int bagY = (BagHeight-1)-(int)(Ry/44);

			if(bagX >= BagWidth || bagY >=  BagHeight || bagX < 0 || bagY < 0)
				return false;
			int i = bagX + bagY*BagWidth;
			if(i < BagWidth*BagHeight)
			if(merchantItems[i][1][curPage] > 0) {
				
				selectedItem = Items[merchantItems[i][0][curPage]];
				selectedBagId = i;
				
			}
			else selectedItem = null;
			
			updateTotalCoins();
		}
		else if(ChestActive) {
			
			float Rx = x -BagPos.x - 12;
			float Ry = y - BagPos.y - 50;
			
			
			int bagX = (int)(Rx/44);
			int bagY = (BagHeight-1)-(int)(Ry/44);
			boolean itemArea = (inventoryBounds.contains(x,y));
			boolean chestArea = (chestBounds.contains(x,y));
			if(ChestActive) {
				
				if(chestArea) {
					
					Rx = x - 284;
					Ry = y - BagPos.y - 50;


					
					
					
					bagX = (int)(Rx/44);
					bagY = (ChestHeight-1)-(int)(Ry/44);
					
				}
				else if(itemArea) {
					Rx = x - 12;
					Ry = y - BagPos.y - 50;


					
					
					
					bagX = (int)(Rx/44);
					bagY = (BagHeight-1)-(int)(Ry/44);
				}
				
			}
			
			
			
			if(itemArea) {
				
					
					int i = bagX + bagY*BagWidth;
					if(i < BagWidth*BagHeight && i >= 0)
					if(BagItem[i][1] > 0) {
						
						selectedItem = Items[BagItem[i][0]];
						selectedBagId = i;
						
					}else selectedItem = null;
			}
					
				else if(chestArea) {
					
					if(bagX >= ChestWidth || bagY >= ChestHeight)
						return false;
					
					int i = bagX + bagY*ChestWidth;
					
					if(i > 0)
					if(i < chestItems[0].length)
					if(chestItems[i][1][curChestPage] > 0) {
						
						
						
						
						selectedItem = Items[chestItems[i][0][curChestPage]];
						selectedBagId = i;
					
					}else selectedItem = null;
				}
				
			
		}
		else {
			float Rx = x -BagPos.x - 12;
			float Ry = y - BagPos.y - 50;

			
			
			int bagX = (int)(Rx/44);
			int bagY = (BagHeight-1)-(int)(Ry/44);
			
			if(bagX > 5 && bagY < 4 && bagY >= 0) {
				if(Armor[bagY][1] > 0)
					if(Armor[bagY][0] > 0)
						selectedItem =  Items[Armor[bagY][0]];
				return true;
			}
			
			if(bagX >= BagWidth || bagY >=  BagHeight || bagX < 0 || bagY < 0)
				return false;
			
			int i = bagX + bagY*BagWidth;
			if(i < BagWidth*BagHeight)
			if(BagItem[i][1] > 0) {
				
				selectedItem = Items[BagItem[i][0]];
				selectedBagId = i;
			}else selectedItem = null;
		}
		touchTime = 0;
		// TODO Auto-generated method stub
		
		
		return true;
		
		
		
	}
	
	
	void TryBuy(InvObject Held, int HeldAmt) {
		boolean flag = false;
		MessageBoxButton yes = new MessageBoxButton("Yes") {
			@Override
			public void onClicked(int...i) {
				// TODO Auto-generated method stub
				if(txt.equals("Ok"))
					return;
				
				AddToBag(SilverCoin.ID, -held.Price*heldAmt);
				AddToBag(held.InvObjID, heldAmt);
				AddToMerchant(Integer.valueOf(held.InvObjID), -heldAmt);
				updateTotalCoins();
				
			}
		};
		
		MessageBoxButton no = new MessageBoxButton("No") {
			
			@Override
			public void onClicked(int...i) {
				// TODO Auto-generated method stub
				
			}
		};
		
		String msg = "";
		String apos = "";
		String itemname = "";
		if(Held.Price * HeldAmt > GetTotalItemInBag(Item.getId("MSC_Coin_Silver"))) {
			
			int amount = GetCraftableAmount("MSC_Coin_Silver "+Held.Price);
			
			if(amount > 0) {
			if(amount > 1)
				apos = "'s";
			if(Held.descName != null)
				itemname = Held.descName;
			else
				itemname = Held.name;
			
			msg = "Sorry, you can only afford "+amount+" "+itemname+apos+" for  $"+amount * Held.Price + ". Do you still want to buy it?";
			HeldAmt = amount;
			
			
			}
			else {
			flag = true;
			msg = "Sorry, you can't afford this.";
			yes.txt = "Ok";
			}
		}
		else {
			if(HeldAmt > 1)
				apos = "'s";
			String name = "";
			if(Held.descName != null)
				name = Held.descName;
			else
				name = Held.name;
			msg = "Would you like buy " + HeldAmt + " " + name + apos+ " for  $" +HeldAmt * Held.Price + "?";
		}
		
		yes.held = Held;
		yes.heldAmt = HeldAmt;
		
		
		
		if(!flag)
		MessageBox.CreateMessageBox(CurrentMerchant.name,msg, 30, 255, 80, 0.1f,no,yes);
		else
			MessageBox.CreateMessageBox(CurrentMerchant.name,msg, 30, 255, 80, 0.1f,yes);
	
		Held = null;
	return;

	}
	
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		// TODO Auto-generated method stub
		
		
		
		if(!BagActive)
			return;
		
		touchHold = false;
		
		
		boolean dropButton = (drop_button_bounds.contains(x,y) && BagActive && Held != null);
		if(dropButton) {
			
			
			if(MerchantActive) {
				
				touchHold = false;
				touchTime = 0;
				
				TryBuy(Held,HeldAmt);
			}
			else if(CraftActive) {
				
				AddToBag(Held.InvObjID, HeldAmt * Held.CraftingMulti);
				for(int i =0; i< HeldAmt; i++)
				RemoveFromBag(Held.CraftingRequirements);
				touchHold = false;
				updateCraftingList();
				touchTime = 0;
				Held = null;
				return;
			}
			else if(ChestActive) {
				
				touchHold = false;
				touchTime = 0;
				Held = null;
				return;
			}
			else {
			DropItem(Held, HeldAmt);
			Held = null;
			touchHold = false;
			touchTime = 0;
			return;
			}
		}
		
		if(CraftActive || MerchantActive) {
			Held = null;
			touchHold = false;
			touchTime = 0;
			return;
		}
		
		float Rx = x - BagPos.x - 12;
		float Ry = y - BagPos.y - 50;


		
		
		
		int bagX = (int)(Rx/44);
		int bagY = (BagHeight-1)-(int)(Ry/44);
		boolean itemArea = (inventoryBounds.contains(x,y));
		boolean chestArea = (chestBounds.contains(x,y));
		if(ChestActive) {
			
			if(chestArea) {
				
				Rx = x - BagPos.x - 284;
				Ry = y - BagPos.y - 50;


				
				
				
				bagX = (int)(Rx/44);
				bagY = (ChestHeight-1)-(int)(Ry/44);
				
			}
			else if(itemArea) {
				Rx = x - BagPos.x - 12;
				Ry = y - BagPos.y - 50;


				
				
				
				bagX = (int)(Rx/44);
				bagY = (BagHeight-1)-(int)(Ry/44);
			}
			
		}
		

		
		
		
		if(Held != null) {
			
			
			
			
			if(bagY >= BagHeight || bagX < 0 || bagY < 0 || MessageBox.MessageBoxActive) {
				
				AddToBag(Held.InvObjID, HeldAmt);
				
				Held = null;
				touchHold = false;
				touchTime = 0;
				return;
			}
			
			if(!ChestActive && !MerchantActive && !CraftActive) {
				if(bagX > 5) {
					
					if(bagY == 0)
						if(Held.type != InvObject.Type.ARMOR_HELM) {
							AddToBag(Held.InvObjID, HeldAmt);
							Held = null;
							HeldAmt = 0;
							touchHold = false;
							touchTime = 0;
							return;
						}
						if(bagY == 1) {
							if(Held.type != InvObject.Type.ARMOR_BP) {
								AddToBag(Held.InvObjID, HeldAmt);
								Held = null;
								HeldAmt = 0;
								touchHold = false;
								touchTime = 0;
								return;
							}
						}
						if(bagY == 2) {
							if(Held.type != InvObject.Type.ARMOR_LEGS) {
								AddToBag(Held.InvObjID, HeldAmt);
								Held = null;
								HeldAmt = 0;
								touchHold = false;
								touchTime = 0;
								return;
							}
						}
						if(bagY == 3) {
							if(Held.type != InvObject.Type.ARMOR_BOOTS) {
								AddToBag(Held.InvObjID, HeldAmt);
								Held = null;
								HeldAmt = 0;
								touchHold = false;
								touchTime = 0;
								return;
							}
						}
					
					Armor[bagY][0] = Held.InvObjID;
					Armor[bagY][1] = 1;
					
					
					if(HeldAmt - 1 > 0)
						AddToBag(Held.InvObjID, HeldAmt-1);
					Held = null;
					touchHold = false;
					touchTime = 0;
					
					return;
				}
			}
			
			if(itemArea) {
				
				
				
				if(BagItem[bagX + bagY * BagWidth][0] == Held.InvObjID) {
					
					if(BagItem[bagX + bagY * BagWidth][1] + HeldAmt > Items[BagItem[bagX + bagY * BagWidth][0]].MaxStack) {
						int dif = Items[BagItem[bagX + bagY * BagWidth][0]].MaxStack - BagItem[bagX + bagY * BagWidth][1];
						BagItem[bagX + bagY * BagWidth][1] = Items[BagItem[bagX + bagY * BagWidth][0]].MaxStack;
						HeldAmt -= dif;
						if(HeldAmt > 0)
						AddToBag(BagItem[bagX + bagY * BagWidth][0], HeldAmt);
						Held = null;
						return;
					}
					
					BagItem[bagX + bagY * BagWidth][0] = Held.InvObjID;
					BagItem[bagX + bagY * BagWidth][1] += HeldAmt;
					Held = null;
					return;
					
					
				}
				
				else if(BagItem[bagX + bagY * BagWidth][0] <= 0 || BagItem[bagX + bagY * BagWidth][1] <= 0) {
					
					
					BagItem[bagX + bagY * BagWidth][0] = Held.InvObjID;
					BagItem[bagX + bagY * BagWidth][1] += HeldAmt;
					Held = null;
					return;
				}
				else if(draggingChest || draggingInv) {
					
					
					if(draggingInv) {
					int prevID = BagItem[bagX + bagY * BagWidth][0];
					BagItem[PrevX + PrevY*BagWidth][0] = prevID;
					BagItem[PrevX + PrevY*BagWidth][1] = BagItem[bagX + bagY * BagWidth][1];
					}
					else {
						int prevID = BagItem[bagX + bagY * BagWidth][0];
						chestItems[(PrevX) + PrevY*ChestWidth][0][curChestPage] = prevID;
						chestItems[(PrevX) + PrevY*ChestWidth][1][curChestPage] = BagItem[bagX + bagY * BagWidth][1];
					}
					
					BagItem[bagX + bagY * BagWidth][0] = Held.InvObjID;
					BagItem[bagX + bagY * BagWidth][1] = HeldAmt;
					Held = null;
					return;
					
					
					
					
				}
				else if(BagItem[bagX + bagY * BagWidth][0] > 0 || BagItem[bagX + bagY * BagWidth][1] > 0) {
					
					AddToBag(Held.InvObjID, HeldAmt);
					Held = null;
					return;
					
				}

				
				
				
			}
			else if(chestArea && ChestActive && bagX < ChestWidth) {
				
				int bagX2 = bagX;
				int PrevX2 = PrevX;
				
				if(chestItems[bagX + bagY * ChestWidth][0][curChestPage] == Held.InvObjID) {
					
					if(chestItems[bagX + bagY * ChestWidth][1][curChestPage] + HeldAmt > Items[chestItems[bagX + bagY * ChestWidth][0][curChestPage]].MaxStack) {
						int dif = Items[chestItems[bagX + bagY * ChestWidth][0][curChestPage]].MaxStack - chestItems[bagX + bagY * ChestWidth][1][curChestPage];
						chestItems[bagX + bagY * ChestWidth][1][curChestPage] = Items[chestItems[bagX + bagY * ChestWidth][0][curChestPage]].MaxStack;
						HeldAmt -= dif;
						if(HeldAmt > 0)
						AddToChest(chestItems[bagX + bagY * ChestWidth][0][curChestPage], HeldAmt);
						Held = null;
						return;
					}
					
					
					chestItems[bagX + bagY * ChestWidth][1][curChestPage] += HeldAmt;
					Held = null;
					return;
					
					
				}
				
				else if(chestItems[bagX + bagY * ChestWidth][0][curChestPage] <= 0 || chestItems[bagX + bagY * ChestWidth][1][curChestPage] <= 0) {
					
					
					chestItems[bagX + bagY * ChestWidth][0][curChestPage] = Held.InvObjID;
					chestItems[bagX + bagY * ChestWidth][1][curChestPage] += HeldAmt;
					Held = null;
					return;
				}
				else if(draggingChest || draggingInv) {
					if(draggingChest) {
						
					
					int prevID = chestItems[bagX + bagY * ChestWidth][0][curChestPage];
					chestItems[PrevX + PrevY*ChestWidth][0][curChestPage] = prevID;
					chestItems[PrevX + PrevY*ChestWidth][1][curChestPage] = chestItems[bagX + bagY * ChestWidth][1][curChestPage];
					

					}
					else if(draggingInv) {
						int prevID = chestItems[bagX + bagY * ChestWidth][0][curChestPage];
						BagItem[PrevX + PrevY*BagWidth][0] = prevID;
						BagItem[PrevX + PrevY*BagWidth][1] = chestItems[bagX + bagY * ChestWidth][1][curChestPage];
						
					}
					chestItems[bagX + bagY * ChestWidth][0][curChestPage] = Held.InvObjID;
					chestItems[bagX + bagY * ChestWidth][1][curChestPage] = HeldAmt;
					Held = null;
					return;
					
					
					
					
				}
				else if(chestItems[bagX + bagY * ChestWidth][0][curChestPage] > 0 || chestItems[bagX + bagY * ChestWidth][1][curChestPage] > 0) {
					
					AddToChest(Held.InvObjID, HeldAmt);
					Held = null;
					return;
					
				}
				
			
				
				
			}
			swap[bagX][bagY] = false;
			AddToBag(Held.InvObjID, HeldAmt);
			Held = null;

		}
		
		
		
		if(x > 260 || y < BagPos.y + 87)
			return;
		
		
		if(bagX >= BagWidth || bagY >=  BagHeight || bagX < 0 || bagY < 0)
			return;
		
		int i = bagX + bagY*BagWidth;
//		if(i < BagWidth*BagHeight)
//		if(BagItem[i][1] > 0) {
//			
//			selectedBagId = i;
//		}
		
		
		super.touchUp(event, x, y, pointer, button);
	}
	
	void updateTotalCoins() {
		
		if(selectedItem != null)
		totalCoins = selectedItem.Price;
		else
		totalCoins = 0;
		
	}
	
	void DrawHealthBar(SpriteBatch batch) {
		
		float percent = (Bob.CurrentBob.HP / Bob.CurrentBob.MaxHP);
		if(percent < 0)
			percent = 0;
		batch.draw(hp_bar, DisplayManager.Width/2 - 60,60,120,8);
		
		batch.draw(hp_colour,DisplayManager.Width/2 - 60,60,percent*MaxHP,8);
	}
	
	int GetItemID(String name) {
		
		for(InvObject e : Items) {
			if(e == null)
				continue;
			if(e.name.equals(name))
				return e.InvObjID;
		}
		return -1;		
	}
	
	public void ClearInventory() {
		for(int y=0; y<BagHeight; y++)
		{
			for(int x=0; x<BagWidth; x++) {
				BagItem[x+(y*BagWidth)][1] = 0;
				BagItem[x+(y*BagWidth)][0] = 0;
			}
			}
	}

	public int GetTotalItemInBag(int invObjID) {
		// TODO Auto-generated method stub
		int amt = 0;
		for(int y=0; y<BagHeight; y++)
		{
			for(int x=0; x<BagWidth; x++) {
			if(BagItem[x+(y*BagWidth)][0] == invObjID)
				amt += BagItem[x+(y*BagWidth)][1];
					
			}
		}
		
		
		return amt;
	}
	
	public boolean HasItems(String items) {
		
		
		String[] reqs = items.split(" ");
		for(int i = 0; i<reqs.length; i++) {
			//Gdx.app.debug("wat", ""+reqs[i]);
			InvObject o = Inventory.CurrentInventory.GetItemByName(reqs[i]);
			int amt = Integer.valueOf(reqs[i+1]);
			if(GetTotalItemInBag(o.InvObjID) < amt)
				return false;
			i++;
		}
		return true;
	}

	public boolean RemoveFromBag(String items) {
		
		String[] reqs = items.split(" ");
		for(int i = 0; i<reqs.length; i++) {
			//Gdx.app.debug("wat", ""+reqs[i]);
			InvObject o = Inventory.CurrentInventory.GetItemByName(reqs[i]);
			int amt = Integer.valueOf(reqs[i+1]);
			if(GetTotalItemInBag(o.InvObjID) < amt)
				return false;
			AddToBag(o.InvObjID, -amt);
			
			i++;
		}
		return true;
		
	}
	
	public int GetCraftableAmount(String items) {
		
		int amt2 = 0;
		
		String[] reqs = items.split(" ");
		int j = 0;
		//Gdx.app.debug("", ""+items);
		while(true) {
		for(int i = 0; i<reqs.length; i++) {
			//Gdx.app.debug("wat", ""+reqs[i]);
			InvObject o = Inventory.CurrentInventory.GetItemByName(reqs[i]);
			int amt = Integer.valueOf(reqs[i+1]);
			if(GetTotalItemInBag(o.InvObjID) < amt+amt*j)
				return j;
			
			i++;
		}
		j++;
		}
		
	}
	
	String GenInfoString() { 
		String str = "";
		if(!MerchantActive && !ChestActive) {
		str += "HP:"+(int)Bob.CurrentBob.HP + "/"+(int)Bob.CurrentBob.MaxHP + "\n";
		int def = Bob.CurrentBob.getDEF();
		if(def > 0) {
			str += "DEF:"+def+"\n";
		}
		str += "Explored:"+totalExploredString+ "%\n";
		str += "\n";
		}
		
		if(selectedItem != null) {
			if(selectedItem.descName == null)
		str += ""+selectedItem.name + ""; 
			else
		str += ""+selectedItem.descName + ""; 
			
		if(MerchantActive) {
			str +="\n$ x" + selectedItem.Price + "\n";
		}
			
			if(!CraftActive) {
				str += "\n\n";
				if(selectedItem.desc != null)
					str += selectedItem.desc + "\n\n";
			if(selectedItem.type == InvObject.Type.HELD) {
				str += "ATK:"+selectedItem.ATK + "\n";
			}
			}
			else {
				if(selectedItem.CraftingMulti > 1)
					str += " (makes "+selectedItem.CraftingMulti+")";
				
				str += "\n\n";
				
				str += "Requirements:\n";
				for(int i=0; i<Requirements.length; i+=2) {
					
					InvObject obj = Items[Requirements[i]];
					
					if(obj.descName != null)
					str += obj.descName + " x"+Requirements[i+1]+"";
					else
					str += obj.name + " x"+Requirements[i+1]+"";
					
					
					
					str += "\n";
				}
				
			}
		}
		return str;
	}
	
	public void DrawInfo(SpriteBatch batch) {
	
		
	
	if(MerchantActive) {
		Font2.drawWrapped(batch, buy_tab, tab1_bounds.x, tab1_bounds.y + 15, tab1_bounds.getWidth(), BitmapFont.HAlignment.CENTER);
		Font2.drawWrapped(batch, blueprint_tab, tab2_bounds.x, tab2_bounds.y + 15, tab2_bounds.getWidth(), BitmapFont.HAlignment.CENTER);
	}
	else if(!ChestActive) {
		
		Font2.drawWrapped(batch, b_tab, tab1_bounds.x, tab1_bounds.y + 15, tab1_bounds.getWidth(), BitmapFont.HAlignment.CENTER);
		Font2.drawWrapped(batch, c_tab, tab2_bounds.x, tab2_bounds.y + 15, tab2_bounds.getWidth(), BitmapFont.HAlignment.CENTER);
	}
	
	if(MerchantActive || CraftActive)
		Font2.drawWrapped(batch, InfoText,BagPos.x + 290, BagPos.y + 220, 180);
	else if(ChestActive)
	Font2.drawWrapped(batch, InfoText, BagPos.x + 368, BagPos.y + 220, 110);
	else
		Font2.drawWrapped(batch, InfoText, BagPos.x + 340, BagPos.y + 220, 160);
	
	if(ChestActive) {
		Font2.drawWrapped(batch, "Page: "+curChestPage,BagPos.x + 280, BagPos.y+200,84,BitmapFont.HAlignment.CENTER);
	}

	
	}
	
	
	Vector3 dmgProjection = new Vector3();
	private String totalExploredString;
	public void DrawDamageText(SpriteBatch batch) {
		
		if(BagActive)
			return;
		
		if(Bob.CurrentBob.state == Bob.DEAD) {
			return;
		}
		
		for(dmgText dmg : dmgTxts) {
			
			if(dmg.stateTime < 0)
				dmg.stateTime = stateTime;
			
			if(stateTime - dmg.stateTime > 0.65f) {
				dmgTxtsRemove.add(dmg);
				continue;
			}
	
			dmgProjection.set(dmg.pos.x, dmg.pos.y, 0);
			MapRenderer.CurrentCam.project(dmgProjection,0,0,DisplayManager.Width,DisplayManager.Height);
			Font.setColor(Color.RED);
			Font.draw(batch, ""+(int)dmg.amt, dmgProjection.x, dmgProjection.y);
			dmg.pos.lerp(dmg.lerpTarget, 0.1f);
		}
		
		
		if(dmgTxtsRemove.size() > 0) {
			dmgTxts.removeAll(dmgTxtsRemove);
			dmgTxtsRemove.clear();
		}
	}
	
	public void setTotalExplored() {
		
		float counter = 0;
		for(int x=0; x<Terrain.CurrentTerrain.Width; x++)
			for(int y=0; y<Terrain.CurrentTerrain.Height; y++) {
				
				TerrainChunk ch = Terrain.TerrainChunks[x+y*Terrain.CurrentTerrain.Width];
				if(ch != null)
					counter++;
				
			}
		
		totalExplored = (counter/(float)(Terrain.CurrentTerrain.Width * Terrain.CurrentTerrain.Height)) * 100f;
		
		String str = ""+totalExplored;
		if(str.indexOf(".")+4 <= str.length())
		str = str.substring(0,str.indexOf(".")+4);
			
		totalExploredString = str;
	}
	
	public void DropItem(InvObject item,int amt) {
		
		WorldObj obj = null;

		if(item.type != InvObject.Type.BLOCK)
		obj = new WorldObj(World.CurrentWorld, Bob.CurrentBob.pos.x, Bob.CurrentBob.pos.y, 1, 1, item.InvObjID, amt);
		else
			obj = new WorldObj(World.CurrentWorld, Bob.CurrentBob.pos.x+0.5f, Bob.CurrentBob.pos.y+0.5f, 0.4f, 0.4f, item.InvObjID, amt);
		obj.vel.y = 5;
		obj.vel.x = 5 * Bob.CurrentBob.dir;
		obj.grounded = false;
		
		World.CurrentWorld.pendingWorldObjs.add(obj);
		
	}

	public void OpenChest(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub
		stateTime = 0;
		BagActive = true;
		ChestActive = true;
		MerchantActive = false;
		CraftActive = false;
		int x3 = x*Terrain.CurrentTerrain.chunkWidth + x2;
		int y3 = y*Terrain.CurrentTerrain.chunkHeight + y2;
		
		chestX = x3;
		chestY = y3;
		
		int id = x3 + y3*Terrain.CurrentTerrain.chunkWidth*Terrain.CurrentTerrain.Width;
		currentChest = "" + id;
		String chest = Chests.get(""+id);
		if(chest != null) {
			if(chest.length() >= 2) {
		String[] split = chest.split(" ");
		
		for(int i =0; i< split.length; i+= 2) {
			
			AddToChest(GetItemID(split[i]),Integer.valueOf(split[i+1]));
			
		}
		}
		}
		
	}
	
	public void UpdateChest() {
		
		String str = "";
		
		
		for(int j=0; j<maxChestPage; j++)
		for(int i = 0; i < ChestWidth * ChestHeight; i++) {
			
			if(chestItems[i][1][j] > 0) {
				
				str += Items[chestItems[i][0][j]].name + " " +chestItems[i][1][j] + " ";
			}
			
		}
		
		
		Chests.put(currentChest,str);
	}
	
	public void CreateMerchantScreen(AI merchant, int type) {
		
		curPage = 1;
		
		for(int j=0; j<maxMerchantPage; j++) {
			for(int i=0; i<BagHeight*BagWidth; i++)
			{
				merchantItems[i][0][j] = 0;
				merchantItems[i][1][j] = 0;
			}
		}
		
		CurrentMerchant = merchant;
		
		BagActive = true;
		MerchantActive = true;
		CraftActive = false;
		ChestActive = false;
		String[] split = merchant.Items.split(" ");
		NumItemsSale = 0;
		NumBlueprintsSale = 0;
		for(int i = 0; i<split.length; i+=2) {
			if(filter == FILTER_ITEMS) {
				merchantTexture = MapRenderer.Texture_Atlas_Objs.findRegion("merchant");
			if(Item.Items[GetItemID(split[i])].type != InvObject.Type.BLUEPRINT) {
			AddToMerchant(GetItemID(split[i]), Integer.valueOf(split[i+1]));
			
			NumItemsSale++;
			}
			}
			else {
				merchantTexture = MapRenderer.Texture_Atlas_Objs.findRegion("merchant_bp");
				if(Item.Items[GetItemID(split[i])].type == InvObject.Type.BLUEPRINT) {
					AddToMerchant(GetItemID(split[i]), Integer.valueOf(split[i+1]));
					NumBlueprintsSale++;
				}
				
			}
			
		}
		
		
		merchantType = type;
		updateTotalCoins();
	}
	
	public void updateCraftingList() {
		
		CraftingItems.clear();
		for(int i = 0; i < BagWidth * BagHeight; i++) {
			
			if(BagItem[i][1] > 0) {
				
				InvObject item = Item.Items[BagItem[i][0]];
				if(item.type == Type.BLUEPRINT) {
					InvObject item2 = Item.Items[item.BluePrintID];
					if(item2.CraftingLevel == 0 || item2.type == InvObject.Type.ANVIL) {
						if(GetCraftableAmount(item2.CraftingRequirements) > 0)
					CraftingItems.add(item2);
					}
					else {
						int c_level = 0;
						for(int x=-1; x<=1; x++)
						for(int y=-1; y<=1; y++){
							int id = Terrain.GetTile((Bob.CurrentBob.pos.x+0.5f)+x, (Bob.CurrentBob.pos.y+0.5f)+y);
							if(id > 0) {
							if(Item.Items[id].type == InvObject.Type.ANVIL)
								c_level = Item.Items[id].CraftingLevel;
							
							}
							
						}
						
						
						if(c_level >= item2.CraftingLevel)
							if(GetCraftableAmount(item2.CraftingRequirements) > 0)
							CraftingItems.add(item2);
					}
				}
			}

			
		}
		
	}
	
	void DrawButton(SpriteBatch batch,String txt,float x,float y, float w, float h) {
		float width = w;
		float height = h;

		batch.draw(border[5], x, y,width,5);
		batch.draw(border[5], x, y+height,width,5);
		batch.draw(border[4], x, y,5,height);
		batch.draw(border[4], x+width, y,5,height);
		batch.draw(border[2], x, y,5,5);
		batch.draw(border[0], x, y+height,5,5);
		batch.draw(border[1], x+width, y+height,5,5);
		batch.draw(border[3], x+width, y,5,5);
		Font3.drawWrapped(batch, txt, x+5, y+(h/2)+8, w-5,BitmapFont.HAlignment.CENTER);
	}
	
	boolean RightArrowActive() {
		
		
		if(MerchantActive) {
			
			if(filter == FILTER_BLUEPRINTS) {
				if((NumBlueprintsSale-1)/(BagWidth*BagHeight*curPage) > 0) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	boolean PrevArrowActive() {
		
		
		if(MerchantActive) {
			
			if(curPage > 1) {
				return true;
			}
		}
		
		return false;
		
	}
	
	void NextArrowPressed() {
		if(RightArrowActive()) {
			if(MerchantActive) {
				curPage += 1;
			}
		}
	}
	void PrevArrowPressed() {
		if(PrevArrowActive()) {
			if(MerchantActive) {
				curPage -= 1;
			}
		}
	}



	public void ReleaseHeld() {
		// TODO Auto-generated method stub
		if(Held != null) {
			AddToBag(Held.InvObjID, HeldAmt);
			
			Held = null;
			touchHold = false;
			touchTime = 0;
		}
	}

}
