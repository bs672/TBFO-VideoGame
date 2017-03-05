/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

import edu.cornell.gdiac.util.*;

/**
 * Class for reading player input. 
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only 
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class InputController {
	// Sensitivity for moving cursar with gameplay
	private static final float GP_ACCELERATE = 1.0f;
	private static final float GP_MAX_SPEED  = 10.0f;
	private static final float GP_THRESHOLD  = 0.01f;

	/** The singleton instance of the input controller */
	private static InputController theController = null;
	
	/** 
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}
	
	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;

	private boolean pausePressed;
	private boolean pausePrevious;

	private boolean primePressed;
	private boolean primePrevious;
	private boolean debugPressed;
	private boolean debugPrevious;
	private boolean exitPressed;
	private boolean exitPrevious;

	private boolean pressed;
	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private boolean jump;
	/** The cursar position (for raddoll) */
	private Vector2 cursar;
	/** The cursar cache (for using as a return value) */
	private Vector2 crosscache;
	/** For the gamepad cursar control */
	private float momentum;
	
	/** An X-Box controller (if it is connected) */
	XBox360Controller xbox;
	
	/**
	 * Returns the amount of sideways movement. 
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement. 
	 */

	public boolean isPressed() {return pressed;}
	public float getHorizontal() {
		return horizontal;
	}

	//Returns whether Oob is jumping

	public boolean getJump() {return jump; }

	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

	public boolean didPause() {
		return pausePressed && !resetPrevious;
	}

	public boolean didExit(){
		return exitPressed && !exitPrevious;
	}

	public boolean didDebug(){
		return debugPressed && !debugPrevious;
	}

	public boolean didPrimary(){
		return primePressed && !primePrevious;
	}

	/**
	 * Returns the current position of the crosshairs on the screen.
	 *
	 * This value does not return the actual reference to the crosshairs position.
	 * That way this method can be called multiple times without any fair that 
	 * the position has been corrupted.  However, it does return the same object
	 * each time.  So if you modify the object, the object will be reset in a
	 * subsequent call to this getter.
	 *
	 * @return the current position of the crosshairs on the screen.
	 */
	public Vector2 getCrossHair() {
		return crosscache.set(cursar);
	}
	
	/**
	 * Creates a new input controller
	 * 
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */
	public InputController() { 
		// If we have a game-pad for id, then use it.
		xbox = new XBox360Controller(0);
		cursar = new Vector2();
		crosscache = new Vector2();
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the cursar.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the cursar.
	 * @param scale  The drawing scale
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		resetPrevious  = resetPressed;
		pausePrevious = pausePressed;

		exitPrevious = exitPressed;
		pausePrevious = pausePressed;
		primePrevious = primePressed;
		
		// Check to see if a GamePad is connected
		if (xbox.isConnected()) {
			readGamepad(bounds, scale);
			readKeyboard(bounds, scale, true); // Read as a back-up
		} else {
			readKeyboard(bounds, scale, false);
		}
	}

	/**
	 * Reads input from an X-Box controller connected to this computer.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the cursar.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the cursar.
	 * @param scale  The drawing scale
	 */
	private void readGamepad(Rectangle bounds, Vector2 scale) {
		resetPressed = xbox.getStart();
		pausePressed = xbox.getLB();

		// Increase animation frame, but only if trying to move
		horizontal = xbox.getLeftX();
		
		// Move the crosshairs with the right stick.
		crosscache.set(xbox.getLeftX(), xbox.getLeftY());
		if (crosscache.len2() > GP_THRESHOLD) {
			momentum += GP_ACCELERATE;
			momentum = Math.min(momentum, GP_MAX_SPEED);
			crosscache.scl(momentum);
			crosscache.scl(1/scale.x,1/scale.y);
			cursar.add(crosscache);
		} else {
			momentum = 0;
		}
		clampPosition(bounds);
	}

	/**
	 * Reads input from the keyboard.
	 *
	 * This controller reads from the keyboard regardless of whether or not an X-Box
	 * controller is connected.  However, if a controller is connected, this method
	 * gives priority to the X-Box controller.
	 *
	 * @param secondary true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale, boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
		pausePressed = (secondary && pausePressed) || (Gdx.input.isKeyPressed(Input.Keys.P));
		// Directional controls
		horizontal = (secondary ? horizontal : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ) {
			horizontal = 1.0f;
			pressed = true;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			horizontal = -1.0f;
			pressed = true;
		}
		else{
			pressed = false;
		}

		jump = (secondary ? jump : false);
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||(Gdx.input.isButtonPressed(Input.Buttons.LEFT))) {
			jump = true;
		}
		// Mouse results
		cursar.set(Gdx.input.getX(), Gdx.input.getY());
		cursar.scl(1 / scale.x, -1 / scale.y);
		cursar.y += bounds.height;
		clampPosition(bounds);
	}
	
	/**
	 * Clamp the cursor position so that it does not go outside the window
	 *
	 * While this is not usually a problem with mouse control, this is critical 
	 * for the gamepad controls.
	 */
	private void clampPosition(Rectangle bounds) {
		cursar.x = Math.max(bounds.x, Math.min(bounds.x+bounds.width, cursar.x));
		cursar.y = Math.max(bounds.y, Math.min(bounds.y+bounds.height, cursar.y));
	}





}