package com.jwatson.omnigame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jwatson.omnigame.InventoryObjects.BallOfDeath;
import com.jwatson.omnigame.InventoryObjects.BlueprintItem;
import com.jwatson.omnigame.InventoryObjects.Blueprints;
import com.jwatson.omnigame.InventoryObjects.Bomb;
import com.jwatson.omnigame.InventoryObjects.BreakablePot;
import com.jwatson.omnigame.InventoryObjects.BrownDirt;
import com.jwatson.omnigame.InventoryObjects.BrownDirt2;
import com.jwatson.omnigame.InventoryObjects.BrownTree;
import com.jwatson.omnigame.InventoryObjects.BrownTreeSeed;
import com.jwatson.omnigame.InventoryObjects.Cactus;
import com.jwatson.omnigame.InventoryObjects.CactusTop;
import com.jwatson.omnigame.InventoryObjects.Chest;
import com.jwatson.omnigame.InventoryObjects.Coal;
import com.jwatson.omnigame.InventoryObjects.Copper;
import com.jwatson.omnigame.InventoryObjects.CopperAxe;
import com.jwatson.omnigame.InventoryObjects.CopperBP;
import com.jwatson.omnigame.InventoryObjects.CopperBoots;
import com.jwatson.omnigame.InventoryObjects.CopperHelm;
import com.jwatson.omnigame.InventoryObjects.CopperLeggings;
import com.jwatson.omnigame.InventoryObjects.CopperPickAxe;
import com.jwatson.omnigame.InventoryObjects.CopperSword;
import com.jwatson.omnigame.InventoryObjects.EnemyTent;
import com.jwatson.omnigame.InventoryObjects.FG_Grass;
import com.jwatson.omnigame.InventoryObjects.GrapplingHook;
import com.jwatson.omnigame.InventoryObjects.Grass;
import com.jwatson.omnigame.InventoryObjects.Grass1;
import com.jwatson.omnigame.InventoryObjects.Iron;
import com.jwatson.omnigame.InventoryObjects.IronAxe;
import com.jwatson.omnigame.InventoryObjects.IronBP;
import com.jwatson.omnigame.InventoryObjects.IronBoots;
import com.jwatson.omnigame.InventoryObjects.IronHelm;
import com.jwatson.omnigame.InventoryObjects.IronLeggings;
import com.jwatson.omnigame.InventoryObjects.IronPickAxe;
import com.jwatson.omnigame.InventoryObjects.IronSword;
import com.jwatson.omnigame.InventoryObjects.Ladder;
import com.jwatson.omnigame.InventoryObjects.Leaf;
import com.jwatson.omnigame.InventoryObjects.LeafSnow;
import com.jwatson.omnigame.InventoryObjects.PickAxe;
import com.jwatson.omnigame.InventoryObjects.Pipe;
import com.jwatson.omnigame.InventoryObjects.Pump;
import com.jwatson.omnigame.InventoryObjects.RedBird;
import com.jwatson.omnigame.InventoryObjects.Sand;
import com.jwatson.omnigame.InventoryObjects.Sand2;
import com.jwatson.omnigame.InventoryObjects.SilverCoin;
import com.jwatson.omnigame.InventoryObjects.Snow;
import com.jwatson.omnigame.InventoryObjects.Snow1;
import com.jwatson.omnigame.InventoryObjects.SpiderLeaf;
import com.jwatson.omnigame.InventoryObjects.SpiderLeafSnow;
import com.jwatson.omnigame.InventoryObjects.SpiderSilk;
import com.jwatson.omnigame.InventoryObjects.StoneAnvil;
import com.jwatson.omnigame.InventoryObjects.StoneAxe;
import com.jwatson.omnigame.InventoryObjects.StoneBlock;
import com.jwatson.omnigame.InventoryObjects.StonePickAxe;
import com.jwatson.omnigame.InventoryObjects.StoneSword;
import com.jwatson.omnigame.InventoryObjects.Tent;
import com.jwatson.omnigame.InventoryObjects.Tombstone;
import com.jwatson.omnigame.InventoryObjects.TopSand1;
import com.jwatson.omnigame.InventoryObjects.TopSand2;
import com.jwatson.omnigame.InventoryObjects.Torch;
import com.jwatson.omnigame.InventoryObjects.Turret;
import com.jwatson.omnigame.InventoryObjects.Water;
import com.jwatson.omnigame.InventoryObjects.WoodArrow;
import com.jwatson.omnigame.InventoryObjects.WoodAxe;
import com.jwatson.omnigame.InventoryObjects.WoodPlatform;
import com.jwatson.omnigame.InventoryObjects.WoodSword;
import com.jwatson.omnigame.InventoryObjects.WoodWall;
import com.jwatson.omnigame.InventoryObjects.WoodenDoor;



public class Item {
	
	static Item ItemList;
	
	public static int b;
	public static InvObject[] Items;
	public ByteBuffer buf;					 
						
	
	public Item() {
		
			b = 1;
		
		 Items = new InvObject[1024];
		
		 
		 Item.Items[b] = new Snow(b++, "MAT_Ice_Top1"); 
		 Item.Items[b-1].descName = "Snow";
		 Item.Items[b] = new Snow1(b++, "MAT_Ice_Top2"); 
		 Item.Items[b-1].descName = "Snow";
		 Item.Items[b] = new BrownDirt(b++, "MAT_Dirt"); 
		 Item.Items[b-1].descName = "Dirt";
		 Item.Items[b] = new BrownDirt2(b++, "MAT_Dirt2"); 
		 Item.Items[b-1].descName = "Dirt";
		 Item.Items[b] = new Grass(b++, "MAT_Grass");
		 Item.Items[b-1].descName = "Grass";
		 Item.Items[b] = new Grass1(b++, "MAT_Grass2");
		 Item.Items[b-1].descName = "Grass";
		 Item.Items[b] = new Sand(b++, "MAT_Sand");
		 Item.Items[b-1].descName = "Sand";
		 Item.Items[b] = new Sand2(b++, "MAT_Sand2");
		 Item.Items[b-1].descName = "Sand";
		 Item.Items[b] = new TopSand2(b++, "MAT_Sand_Top1");
		 Item.Items[b-1].descName = "Sand";
		 Item.Items[b] = new TopSand1(b++, "MAT_Sand_Top2");
		 Item.Items[b-1].descName = "Sand";
		 Item.Items[b] = new StoneBlock(b++, "MAT_Stone");
		 Item.Items[b-1].descName = "Stone";
		 Item.Items[b] = new Coal(b++, "MAT_Coal");
		 Item.Items[b-1].descName = "Coal";
		 Item.Items[b] = new Iron(b++, "MAT_Iron");
		 Item.Items[b-1].descName = "Iron";
		 Item.Items[b] = new GrapplingHook(b++, "TOOL_GrapplingHook");
		 Item.Items[b-1].descName = "GrapplingHook";

		 Item.Items[b] = new BrownTree(b++,  "MAT_Tree_Brown");
		 Item.Items[b-1].descName = "Wood";
		 Item.Items[b] = new BrownTreeSeed(b++, "DEP_Seed_BrownTree");
		 Item.Items[b-1].descName = "Tree Seeds";
		 Item.Items[b] = new Cactus(b++, "MAT_Cactus_Column");
		 Item.Items[b-1].descName = "Cactus";
		 Item.Items[b] = new CactusTop(b++, "MAT_Cactus_Column_Top");
		 Item.Items[b-1].descName = "Cactus Flower";
		 Item.Items[b] = new Leaf(b++, "MAT_Leaves");
		 Item.Items[b-1].descName = "Leaves";
		 Item.Items[b] = new SpiderLeaf(b++, "MAT_LeavesSpider");
		 Item.Items[b-1].descName = "Leaves";
		 Item.Items[b] = new SpiderLeafSnow(b++, "MAT_LeavesSpider_snow");
		 Item.Items[b-1].descName = "Leaves";
		 Item.Items[b] = new LeafSnow(b++, "MAT_Leaves_Snow");
		 Item.Items[b-1].descName = "Leaves";
	     Item.Items[b] = new Water(b++, "MAT_Water");
	     Item.Items[b-1].descName = "Water";
	     Item.Items[b] = new Tombstone(b++,  "MSC_Tombstone");
	     Item.Items[b-1].descName = "Tombstone";
	     Item.Items[b] = new BreakablePot(b++,  "PROP_BreakablePot");
		 Item.Items[b] = new PickAxe(b++, "TOOL_PickAxe_Wood");
		 Item.Items[b-1].descName = "Wood Pickaxe";
	     Item.Items[b] = new WoodAxe(b++, "TOOL_Axe_Wood");
	     Item.Items[b-1].descName = "Wood Axe";
	     Item.Items[b] = new WoodSword(b++,  "WEP_Sword_Wood");
	     Item.Items[b-1].descName = "Wood Sword";
	     Item.Items[b] = new StonePickAxe(b++,  "TOOL_PickAxe_Stone");
	     Item.Items[b-1].descName = "Stone Pickaxe";
		 Item.Items[b] = new StoneAxe(b++,  "TOOL_Axe_Stone");
		 Item.Items[b-1].descName = "Stone Axe";
	     Item.Items[b] = new StoneSword(b++,  "WEP_Sword_Stone");
	     Item.Items[b-1].descName = "Stone Sword";
		 Item.Items[b] = new CopperPickAxe(b++,  "TOOL_PickAxe_Copper");
		 Item.Items[b-1].descName = "Copper Pickaxe";
		 Item.Items[b] = new CopperAxe(b++,  "TOOL_Axe_Copper");
		 Item.Items[b-1].descName = "Copper Axe";
		 Item.Items[b] = new CopperSword(b++,  "WEP_Sword_Copper");
		 Item.Items[b-1].descName = "Copper Sword";
		 Item.Items[b] = new IronPickAxe(b++,  "TOOL_PickAxe_Iron");
		 Item.Items[b-1].descName = "Iron Pickaxe";
		 Item.Items[b] = new IronAxe(b++,  "TOOL_Axe_Iron");
		 Item.Items[b-1].descName = "Iron Axe";
		 Item.Items[b] = new IronSword(b++,  "WEP_Sword_Iron");
		 Item.Items[b-1].descName = "Iron Sword";
		 Item.Items[b] = new Copper(b++, "MAT_Copper");
		 Item.Items[b-1].descName = "Copper";
		 Item.Items[b] = new Torch(b++, "DEP_Torch");
		 Item.Items[b-1].descName = "Torch";
//		 Item.Items[b] = new Ladder(b++, "DEP_Ladder");
//		 Item.Items[b-1].descName = "Ladder";
		 Item.Items[b] = new Chest(b++,  "DEP_Chest");
		 Item.Items[b-1].descName = "Chest";
		 Item.Items[b] = new SilverCoin(b++, "MSC_Coin_Silver");
		 Item.Items[b-1].descName = "Silver Coin";
		 Item.Items[b] = new Blueprints(b++, "MSC_Blueprints");
		 Item.Items[b] = new StoneAnvil(b++, "DEP_Anvil_Stone");
		 Item.Items[b-1].descName = "Stone Anvil";
		 Item.Items[b] = new WoodenDoor(b++, "DEP_Door_Wood");
		 Item.Items[b-1].descName = "Wood Door";
		 Item.Items[b] = new WoodPlatform(b++, "DEP_Platform_Wood");
		 Item.Items[b-1].descName = "Platform";
		 Item.Items[b] = new BallOfDeath(b++, "GEAR_BallOfDeath");
		 Item.Items[b-1].descName = "Ball Of Death";
		 Item.Items[b] = new WoodWall(b++, "DEP_Wall_Wood");
		 Item.Items[b-1].descName = "Wood Wall";
		 Item.Items[b] = new SpiderSilk(b++, "MAT_SpiderSilk");
		 Item.Items[b-1].descName = "Spider Silk";
		 Item.Items[b] = new Tent(b++, "DEP_Tent");
		 Item.Items[b-1].descName = "Tent";
		 Item.Items[b-1].desc = "Set your home or save your game.";
		 Item.Items[b] = new Turret(b++, "TUR_BallistaTurret");
		 Item.Items[b-1].descName = "Ballista Turret";
		 Item.Items[b-1].desc = "Automatically attacks enemies.";
		 Item.Items[b] = new WoodArrow(b++, "WEP_Arrow_Wood");
		 Item.Items[b-1].descName = "Wood Arrow";
		 Item.Items[b] = new EnemyTent(b++, "DEP_EnemyTent");
		 Item.Items[b-1].descName = "Enemy Tent";
		 Item.Items[b] = new CopperBP(b++, "ARMOR_BP_Copper");
		 Item.Items[b-1].descName = "Copper Breastplate";
		 Item.Items[b] = new CopperHelm(b++, "ARMOR_Helm_Copper");
		 Item.Items[b-1].descName = "Copper Helmet";
		 Item.Items[b] = new CopperLeggings(b++, "ARMOR_Legs_Copper");
		 Item.Items[b-1].descName = "Copper Leggings";
		 Item.Items[b] = new CopperBoots(b++, "ARMOR_Boots_Copper");
		 Item.Items[b-1].descName = "Copper Boots";
		 Item.Items[b] = new IronBP(b++, "ARMOR_BP_Iron");
		 Item.Items[b-1].descName = "Iron Breastplate";
		 Item.Items[b] = new IronHelm(b++, "ARMOR_Helm_Iron");
		 Item.Items[b-1].descName = "Iron Helmet";
		 Item.Items[b] = new IronLeggings(b++, "ARMOR_Legs_Iron");
		 Item.Items[b-1].descName = "Iron Leggings";
		 Item.Items[b] = new IronBoots(b++, "ARMOR_Boots_Iron");
		 Item.Items[b-1].descName = "Iron Boots";
		 Item.Items[b] = new Bomb(b++, "GEAR_Bomb");
		 Item.Items[b-1].descName = "Bomb";
		 Item.Items[b] = new Pipe(b++, "DEP_Pipe");
		 Item.Items[b-1].descName = "Pipe";
		 Item.Items[b] = new Pump(b++, "DEP_Pump");
		 Item.Items[b-1].descName = "Pump";
		 
		 
		 CreateBluePrints();
		 CreateFoliage();
		 CreateSnowFoliage();
		 
		
	}
	

	
	public void CreateBluePrints() {
		for(InvObject item : Items) {
			
			if(item == null)
				continue;
			
			if(item.type == InvObject.Type.BLUEPRINT)
				continue;
			int counter = 0;
			if(item.CraftingRequirements != null) {
				
				Item.Items[b] = new BlueprintItem(b++, "BP_"+item.name);
				String name = item.name;
				if(item.descName != null)
					name = item.descName;
				
				String[] split = item.CraftingRequirements.split(" ");
				//Gdx.app.debug("", ""+item.name);
				for(int i=0; i<split.length; i+=2) {
					
					InvObject obj2 = Item.Items[getId(split[i])];
					String name2 = null;
					if(obj2.descName != null)
						name2 = obj2.descName;
					else
						name2 = obj2.name;
					
					Item.Items[b-1].desc += ""+name2+ " x"+split[i+1]+"\n";
				}
				Item.Items[b-1].Price = (int)((float)item.Price*0.7f);
				Item.Items[b-1].descName = name +" Blueprints";
				Item.Items[b-1].BluePrintID = item.InvObjID;
				
			}
			
			
		}
		
	}
	
	public void CreateFoliage() {
		
		TextureRegion[] split = MapRenderer.Texture_Atlas.findRegion("MSC_Foliage").split(8, 8)[0];
		
		Item.Items[b] = new FG_Grass(b++,"FG_Grass1");
		Item.Items[b-1].thumbnail = split[0];
		Item.Items[b] = new FG_Grass(b++,"FG_Grass2");
		Item.Items[b-1].thumbnail = split[1];
		Item.Items[b] = new FG_Grass(b++,"FG_Grass3");
		Item.Items[b-1].thumbnail = split[2];
	}
	
	public void CreateSnowFoliage() {
		
		TextureRegion[] split = MapRenderer.Texture_Atlas.findRegion("MSC_Foliage_Snow").split(8, 8)[0];
		
		Item.Items[b] = new FG_Grass(b++,"FG_Grass1_Snow");
		Item.Items[b-1].thumbnail = split[0];
		Item.Items[b] = new FG_Grass(b++,"FG_Grass2_Snow");
		Item.Items[b-1].thumbnail = split[1];
		Item.Items[b] = new FG_Grass(b++,"FG_Grass3_Snow");
		Item.Items[b-1].thumbnail = split[2];
	}
	
	public static int test(Class<?> class1) {
		try {
			Items[b] = (InvObject) class1.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1337;
	}
	
	public static byte getId(String name) {
		
		
		for(InvObject obj : Items) {
			if(obj == null)
				continue;
			
			if(obj.name.equals(name))
				return (byte)obj.InvObjID;
		}
		
		
		return -1;
		
	}
	
	
	
	public static <E> Class<? extends E>[] getClasses(Package pkg,
			Class<E> superClass, boolean recursive) throws IOException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = pkg.getName().replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<String> dirs = new ArrayList<String>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(resource.getFile());
		}
		TreeSet<String> classes = new TreeSet<String>();
		for (String directory : dirs) {
			classes.addAll(findClasses(directory, pkg, recursive));
		}
		List<Class<?>> classList = new ArrayList<Class<?>>();
		for (String clazz : classes) {
			Class<?> classz = Class.forName(clazz);
			if (hasSuperclass(classz, superClass)) {
				classList.add(classz);
			}
		}
		return classList.toArray(new Class[classList.size()]);
	}
	
	private static TreeSet<String> findClasses(String directory, Package pkg, boolean recursive)
			throws IOException {
		TreeSet<String> classes = new TreeSet<String>();
		if (directory.startsWith("file:") && directory.contains("!")) {
			String[] split = directory.split("!");
			URL jar = new URL(split[0]);
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry = null;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().replaceAll("[$].*", "")
							.replaceAll("[.]class", "").replace('/', '.');
					if (className.length() >= pkg.getName().length()
							&& className.substring(0, pkg.getName().length())
									.equals(pkg.getName())) {
						classes.add(className);
					}
				}
			}
		}
		File dir = new File(directory);
		if (!dir.exists()) {
			return classes;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory() && recursive) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file.getAbsolutePath(), Package
						.getPackage(pkg.getName() + "." + file.getName()), recursive));
			} else if (file.getName().endsWith(".class")) {
				classes.add(pkg.getName()
						+ "."
						+ file.getName().substring(0,
								file.getName().length() - 6));
			}
		}

		return classes;
	}
	
	private static boolean hasSuperclass(Class<?> c, Class<?> superclass) {

		Class<?> check = c;
		while (check.getSuperclass() != null) {
			if (check.getSuperclass().equals(superclass)) {
				return true;
			}
			check = check.getSuperclass();
		}
		return false;

	}
	
}
