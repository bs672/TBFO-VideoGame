/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.controller;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.util.*;

/**
 * Root class for a LibGDX.  
 *
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	protected static Music music;

	protected static boolean mute;

	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private WorldController[] controllers;

	private int lastScreen = 0;
	private int lastPlayScreen = 4;
	private int unlocked;

	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();

		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	public static boolean getMute(){return mute;}

	public static void setMute(boolean a){mute = a;}

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		music = Gdx.audio.newMusic(Gdx.files.internal("audio/spaceMusic.ogg"));
		mute = false;
		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);
		// Initialize the three game worlds
		controllers = new WorldController[31];
		controllers[0] = new MainMenu();
		controllers[1] = new SettingsMode();
		controllers[2] = new LevelSelect(27, 0);
		controllers[3] = new PauseMenu();
		//Level
		controllers[4] = new PlayMode("T1", 1); //1
		controllers[5] = new PlayMode("T2", 2); //2
		controllers[6] = new PlayMode("T3 ", 3); //3
		controllers[7] = new PlayMode("T4",4); //4
		//Beginner
		controllers[8] = new PlayMode("Ship Square",5); //5
        controllers[9] = new PlayMode("Plus",6); //6
        controllers[10] = new PlayMode("Sun",7); //7
        controllers[11] = new PlayMode("BH",8); //8
		controllers[12] = new PlayMode("Maze",9); //9
		//Intermediate
		controllers[13] = new PlayMode("Mother",10); //10
		controllers[14] = new PlayMode("Mind The Gap", 11); //11
        controllers[15] = new PlayMode("X",12); //12
		controllers[16] = new PlayMode("Circles",13); //13
		controllers[17] = new PlayMode("Grid",14); //14
		controllers[18] = new PlayMode("Sun T",15); //15
		controllers[19] = new PlayMode("Looper",16); //16
		//Advanced
		controllers[20] = new PlayMode("Layers",17); //17
		controllers[21] = new PlayMode("Clam Shell",18); //18
        controllers[22] = new PlayMode("Pinball",19); //19
		controllers[23] = new PlayMode("Tomb", 20); //20
        controllers[24] = new PlayMode("FvS",21); //21
		//Expert
        controllers[25] = new PlayMode("Diamond",22); //22
		controllers[26] = new PlayMode("Race",23); //23
		controllers[27] = new PlayMode("Jump Start",24); //24
		controllers[28] = new PlayMode("Double Trouble", 25); //25
        controllers[29] = new PlayMode("Portal",26); //26
		controllers[30] = new PlayMode("Final", 27); //27

		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].preLoadContent(manager);
		}
		current = 0;
		unlocked = 1;
		loading.setScreenListener(this);

		setScreen(loading);
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].unloadContent(manager);
			controllers[ii].dispose();
		}

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		for(int i = 0; i < controllers.length; i++) {
			if(controllers[i] instanceof PlayMode)
				((PlayMode)controllers[i]).resize();
		}
		super.resize(width,height);
	}

	public void toggleMute() {
		if (music.isPlaying()) {
			SoundController.getInstance().setMute(true);
			music.stop();
		} else {
			SoundController.getInstance().setMute(false);
			music.play();
		}
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		InputController.getInstance().setCenterCamera(true);
		if (screen == loading) {
			for(int ii = 0; ii < controllers.length; ii++) {
				controllers[ii].loadContent(manager);
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
			}
			controllers[current].reset();
			setScreen(controllers[0]);
			loading.dispose();
			loading = null;
		}
		// MAIN MENU
		else if (screen == controllers[0]) {
			lastScreen = 0;
			if (exitCode == WorldController.EXIT_QUIT) {
			    Gdx.app.exit();
			    return;
            }
			current = exitCode;
			controllers[current].reset();
			setScreen(controllers[current]);
		}
		// SETTINGS
		else if (screen == controllers[1]) {
			if (exitCode == 0) {
				current = lastScreen;
				controllers[current].reset();
				setScreen(controllers[current]);
				lastScreen = 1;
			}
			else if (exitCode == 1) {
				toggleMute();
            }
            else if(exitCode == 2) {
				InputController.getInstance().toggleControls();
			}
		}
		// LEVEL SELECT
		else if (screen == controllers[2]) {
			if(current!=12) {
				if (exitCode < 2) {
					current = exitCode;
                    controllers[current].reset();
				} else if (exitCode == 2) {
					current = lastScreen;
					if (lastScreen < 4) {
                        controllers[current].reset();
                    }
				} else {
					current = exitCode + 1;
                    controllers[current].reset();
				}
				lastScreen = 2;
				setScreen(controllers[current]);
			}
			else{
				lastScreen = 2;
				controllers[current].reset();
				setScreen(controllers[2]);
			}
		}
		// PAUSE MENU
		else if (screen == controllers[3]) {
			if (exitCode <= 2) {
				current = exitCode;
				controllers[current].reset();
			}
			else {
				current = lastPlayScreen;
				((PlayMode)controllers[current]).unPauseMusic();
			}
			if(exitCode==2){
				if(lastPlayScreen>=22){
					((LevelSelect)controllers[current]).changeMode(2);
				}
				else if(lastPlayScreen>=13){
					((LevelSelect)controllers[current]).changeMode(1);
				}
				else{
					((LevelSelect)controllers[current]).changeMode(0);
				}
				controllers[current].reset();
			}
			setScreen(controllers[current]);
			lastScreen = 3;
		}
		// PLAY MODE
		else {
			for (int i = 4; i < controllers.length - 1; i++) {
				if (screen == controllers[i]) {
					lastScreen = i;
					lastPlayScreen = i;
					current = exitCode%10;
					if (exitCode >= 2000) {
                        unlocked = Math.max(unlocked, i-2);
                        controllers[2].setUnlocked(unlocked);
                    }
					if (exitCode == 2003) {
					    current = i+1;

					}
					controllers[current].reset();
					setScreen(controllers[current]);
				}
			}
			if (screen == controllers[controllers.length - 1]) {
				lastScreen = controllers.length - 1;
				lastPlayScreen = controllers.length - 1;
				current = exitCode%10;
				if (exitCode == WorldController.EXIT_NEXT || exitCode == 2003) {
				    current = 2;
                }
				controllers[current].reset();
				setScreen(controllers[current]);
			}
		}
//		else if (exitCode == WorldController.EXIT_NEXT) {
//			current = (current+1) % controllers.length;
//			controllers[current].reset();
//			setScreen(controllers[current]);
//		} else if (exitCode == WorldController.EXIT_PREV) {
//			current = (current+controllers.length-1) % controllers.length;
//			controllers[current].reset();
//			setScreen(controllers[current]);
//		} else if (exitCode == WorldController.EXIT_QUIT) {
//			// We quit the main application
//			Gdx.app.exit();
//		}
	}

}
