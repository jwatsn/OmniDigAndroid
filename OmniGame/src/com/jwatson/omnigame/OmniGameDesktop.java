
package com.jwatson.omnigame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class OmniGameDesktop {
	public static void main (String[] argv) {
		new LwjglApplication(new OmniGame(), "OmniGame", 480, 320, true);

		// After creating the Application instance we can set the log level to
		// show the output of calls to Gdx.app.debug
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}