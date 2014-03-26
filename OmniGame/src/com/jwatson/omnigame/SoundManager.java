package com.jwatson.omnigame;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

public class SoundManager {

	public static HashMap<String, Sound> Sounds;
	public static int VOLUME = 1;
	public static SoundManager Current_SM;
	public static Random rand;
	
	public SoundManager() {
		Sounds = new HashMap<String, Sound>();
		if(Current_SM != null)
			Current_SM = null;
		
		Current_SM = this;
		rand = new Random();
		
//		if(Gdx.app.getType() == ApplicationType.Android) {
//			
//			FileHandle file = Gdx.files.internal("data/sounds");
//			
//			for(FileHandle f : file.list()) {
//				Sound snd = Gdx.audio.newSound(Gdx.files.internal("data/sounds/"+f.name()));
//				Sounds.put(f.nameWithoutExtension(), snd);
//			}
//			
//			
//		}
		
	}
	
	public static void PlaySound(String soundname, float numSounds) {
		
		int rand = MathUtils.random(1, (int)numSounds);
		
		Sound snd = Sounds.get(soundname);
		if(snd == null) {
			
			snd = Gdx.audio.newSound(Gdx.files.internal("data/sounds/"+soundname+rand+".wav"));
			Sounds.put(soundname, snd);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long id = snd.play(VOLUME);
		float randvolume = MathUtils.random(0.7f, 1.1f);
		float randpitch = MathUtils.random(0.8f, 1.2f);
		snd.setVolume(id, randvolume);
		snd.setPitch(id, randpitch);

		
	}
	
	public static void PlaySound(String soundname, float numSounds,boolean noRandom) {
		
		int rand = MathUtils.random(1, (int)numSounds);
		
		Sound snd = Sounds.get(soundname);
		if(snd == null) {
			
			snd = Gdx.audio.newSound(Gdx.files.internal("data/sounds/"+soundname+rand+".wav"));
			Sounds.put(soundname, snd);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long id = snd.play(VOLUME);

		
	}
	
public static void PlaySound(String soundname, float numSounds, float x,float y) {
		
		int rand = MathUtils.random(1, 3);
		
		Sound snd = Sounds.get(soundname);
		if(snd == null) {
			
			snd = Gdx.audio.newSound(Gdx.files.internal("data/sounds/"+soundname+rand+".wav"));
			Sounds.put(soundname, snd);
			
		}
		float dist = (1 - (Bob.CurrentBob.pos.dst(x, y)/300));
		if(dist<0)
			dist=0;
		long id = snd.play(VOLUME);
		float randvolume = MathUtils.random(0.7f, 1.1f);
		float randpitch = MathUtils.random(0.7f, 1.3f);
		snd.setVolume(id, 1 * dist);
		Gdx.app.debug("", ""+dist);
		snd.setPitch(id, randpitch);
		
		
	}

}
