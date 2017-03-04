package edu.cornell.gdiac.physics.space;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class SpaceController extends WorldController implements ContactListener {
    /** The texture file for the character avatar (no animation) */
    private static final String OOB_FILE  = "space/oob.png";
    /** The texture file for the spinning barrier */
    private static final String PLANET_FILE = "space/planet.png";
    /** The texture file for the bullet */
    /** Texture file for background image */
    private static final String BACKG_FILE = "space/space-background2.png"; //https://images4.alphacoders.com/106/106826.jpg

    /** The sound file for a jump */
    private static final String JUMP_FILE = "platform/jump.mp3";
    /** The sound file for a bullet fire */
    private static final String PEW_FILE = "platform/pew.mp3";
    /** The sound file for a bullet collision */
    private static final String POP_FILE = "platform/plop.mp3";
    /** The initial position of Oob */
    private static Vector2 OOB_POS = new Vector2(2.5f, 5.0f);
    /** Oob's initial radius */
    private static float OOB_RADIUS = 1.0f;

    /** Texture asset for character avatar */
    private TextureRegion avatarTexture;
    /** Texture asset for the spinning barrier */
    private TextureRegion planetTexture;
    /** Texture asset for background image */
    private TextureRegion backgroundTexture;


    /** Track asset loading from all instances and subclasses */
    private AssetState platformAssetState = AssetState.EMPTY;

    /**
     * Preloads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        if (platformAssetState != AssetState.EMPTY) {
            return;
        }

        platformAssetState = AssetState.LOADING;
        manager.load(OOB_FILE, Texture.class);
        assets.add(OOB_FILE);
        manager.load(PLANET_FILE, Texture.class);
        assets.add(PLANET_FILE);
        manager.load(BACKG_FILE, Texture.class);
        assets.add(BACKG_FILE);

        manager.load(JUMP_FILE, Sound.class);
        assets.add(JUMP_FILE);
        manager.load(PEW_FILE, Sound.class);
        assets.add(PEW_FILE);
        manager.load(POP_FILE, Sound.class);
        assets.add(POP_FILE);

        super.preLoadContent(manager);
    }

    /**
     * Load the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        if (platformAssetState != AssetState.LOADING) {
            return;
        }

        avatarTexture = createTexture(manager,OOB_FILE,false);
        planetTexture = createTexture(manager,PLANET_FILE,false);
        backgroundTexture = createTexture(manager,BACKG_FILE,false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

    // Physics constants for initialization
    /** The new heavier gravity for this world (so it is not so floaty) */
    private static final float  DEFAULT_GRAVITY = -14.7f;
    /** The density for most physics objects */
    private static final float  BASIC_DENSITY = 0.0f;
    /** The density for a bullet */
    private static final float  HEAVY_DENSITY = 10.0f;
    /** Friction of most platforms */
    private static final float  BASIC_FRICTION = 0.4f;
    /** The restitution for all physics objects */
    private static final float  BASIC_RESTITUTION = 0.1f;
    /** The width of the rope bridge */
    private static final float  BRIDGE_WIDTH = 14.0f;
    /** Offset for bullet when firing */
    private static final float  BULLET_OFFSET = 0.2f;
    /** The speed of the bullet after firing */
    private static final float  BULLET_SPEED = 20.0f;
    /** The volume for sound effects */
    private static final float EFFECT_VOLUME = 0.8f;

    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.
    // Wall vertices
    private static final float[][] WALLS = {
            {0.0f,0.0f, 0.0f, 18.0f, 0.2f, 18.0f, 0.2f,  0.0f},

            {0.0f,18.0f, 32.0f, 18.0f, 32.0f, 17.8f, 0.0f, 17.8f},

            {31.8f,18.0f, 32.0f, 18.0f, 32.0f, 0.0f, 31.8f, 0.0f},

            {0.0f,0.0f, 0.0f, 0.2f, 32.0f, 0.2f, 32.0f, 0.0f},
    };

    /** The outlines of all of the platforms */
//    private static final float[][] PLATFORMS = {
//            { 1.0f, 3.0f, 6.0f, 3.0f, 6.0f, 2.5f, 1.0f, 2.5f},
//            { 6.0f, 4.0f, 9.0f, 4.0f, 9.0f, 2.5f, 6.0f, 2.5f},
//            {23.0f, 4.0f,31.0f, 4.0f,31.0f, 2.5f,23.0f, 2.5f},
//            {26.0f, 5.5f,28.0f, 5.5f,28.0f, 5.0f,26.0f, 5.0f},
//            {29.0f, 7.0f,31.0f, 7.0f,31.0f, 6.5f,29.0f, 6.5f},
//            {24.0f, 8.5f,27.0f, 8.5f,27.0f, 8.0f,24.0f, 8.0f},
//            {29.0f,10.0f,31.0f,10.0f,31.0f, 9.5f,29.0f, 9.5f},
//            {23.0f,11.5f,27.0f,11.5f,27.0f,11.0f,23.0f,11.0f},
//            {19.0f,12.5f,23.0f,12.5f,23.0f,12.0f,19.0f,12.0f},
//            { 1.0f,12.5f, 7.0f,12.5f, 7.0f,12.0f, 1.0f,12.0f}
//    };

    // Other game objects
    /** The goal door position */
    private static Vector2 GOAL_POS = new Vector2(4.0f,14.0f);
    /** The position of the spinning barrier */
    private static Vector2 SPIN_POS = new Vector2(13.0f,12.5f);
    /** The initial position of the dude */
    private static Vector2 DUDE_POS = new Vector2(2.5f, 5.0f);
    /** The position of the rope bridge */
    private static Vector2 BRIDGE_POS  = new Vector2(9.0f, 3.8f);

    // Physics objects for the game
    /** Reference to the character avatar */
    private OobModel avatar;
    /** Reference to current planet Oob's on */
    private WheelObstacle currentPlanent;
    /** Reference to the goalDoor (for collision detection) */
    private BoxObstacle goalDoor;

    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture> sensorFixtures;

    /**
     * Creates and initialize a new instance of the platformer game
     *
     * The game has default gravity and other settings
     */
    public SpaceController() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
        sensorFixtures = new ObjectSet<Fixture>();
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );

        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        world.dispose();

        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
//        // Add level goal
//        float dwidth  = goalTile.getRegionWidth()/scale.x;
//        float dheight = goalTile.getRegionHeight()/scale.y;
//        goalDoor = new BoxObstacle(GOAL_POS.x,GOAL_POS.y,dwidth,dheight);
//        goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
//        goalDoor.setDensity(0.0f);
//        goalDoor.setFriction(0.0f);
//        goalDoor.setRestitution(0.0f);
//        goalDoor.setSensor(true);
//        goalDoor.setDrawScale(scale);
//        goalDoor.setTexture(goalTile);
//        goalDoor.setName("goal");
//        addObject(goalDoor);

        String wname = "wall";
        for (int ii = 0; ii < WALLS.length; ii++) {
            PolygonObstacle obj;
            obj = new PolygonObstacle(WALLS[ii], 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            obj.setName(wname+ii);
            addObject(obj);
        }
//
//        String pname = "platform";
//        for (int ii = 0; ii < PLATFORMS.length; ii++) {
//            PolygonObstacle obj;
//            obj = new PolygonObstacle(PLATFORMS[ii], 0, 0);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(BASIC_DENSITY);
//            obj.setFriction(BASIC_FRICTION);
//            obj.setRestitution(BASIC_RESTITUTION);
//            obj.setDrawScale(scale);
//            obj.setTexture(earthTile);
//            obj.setName(pname+ii);
//            addObject(obj);
//        }

        // Create Oob
        avatar = new OobModel(OOB_POS.x, OOB_POS.y, OOB_RADIUS);
        avatar.setDrawScale(scale);
        avatar.setTexture(avatarTexture);
        avatar.setBodyType(BodyDef.BodyType.DynamicBody);
        addObject(avatar);

        // Create spinning platform
//        dwidth  = barrierTexture.getRegionWidth()/scale.x;
//        dheight = barrierTexture.getRegionHeight()/scale.y;
//        Spinner spinPlatform = new Spinner(SPIN_POS.x,SPIN_POS.y,dwidth,dheight);
//        spinPlatform.setDrawScale(scale);
//        spinPlatform.setTexture(barrierTexture);
//        addObject(spinPlatform);
    }

    /**
     * Returns whether to process the update loop
     *
     * At the start of the update loop, we check if it is time
     * to switch to a new game mode.  If not, the update proceeds
     * normally.
     *
     * @param dt Number of seconds since last animation frame
     *
     * @return whether to process the update loop
     */
    public boolean preUpdate(float dt) {
        if (!super.preUpdate(dt)) {
            return false;
        }

        if (!isFailure() && avatar.getY() < -1) {
            setFailure(true);
            return false;
        }

        return true;
    }

    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Process actions in object model
        Vector2 mvmtDir = new Vector2(InputController.getInstance().getHorizontal() *avatar.getForce(), 0 *avatar.getForce());
        avatar.setMovement(mvmtDir);
        avatar.setJumping(InputController.getInstance().getJump());

        avatar.applyForce();
        if (avatar.isJumping()) {
            SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }
//
//    /**
//     * Add a new bullet to the world and send it in the right direction.
//     */
//    private void createBullet() {
//        float offset = (avatar.isFacingRight() ? BULLET_OFFSET : -BULLET_OFFSET);
//        float radius = bulletTexture.getRegionWidth()/(2.0f*scale.x);
//        WheelObstacle bullet = new WheelObstacle(avatar.getX()+offset, avatar.getY(), radius);
//
//        bullet.setName("bullet");
//        bullet.setDensity(HEAVY_DENSITY);
//        bullet.setDrawScale(scale);
//        bullet.setTexture(bulletTexture);
//        bullet.setBullet(true);
//        bullet.setGravityScale(0);
//
//        // Compute position and velocity
//        float speed  = (avatar.isFacingRight() ? BULLET_SPEED : -BULLET_SPEED);
//        bullet.setVX(speed);
//        addQueuedObject(bullet);
//
//        SoundController.getInstance().play(PEW_FILE, PEW_FILE, false, EFFECT_VOLUME);
//    }

    /**
     * Remove a new bullet from the world.
     *
     * @param  bullet   the bullet to remove
     */
    public void removeBullet(Obstacle bullet) {
        bullet.markRemoved(true);
        SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
    }


    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    public void beginContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle)body1.getUserData();
            Obstacle bd2 = (Obstacle)body2.getUserData();

            // Test bullet collision with world
            if (bd1.getName().equals("bullet") && bd2 != avatar) {
                removeBullet(bd1);
            }

            if (bd2.getName().equals("bullet") && bd1 != avatar) {
                removeBullet(bd2);
            }

            // See if we have landed on the ground.
            if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
                    (avatar.getSensorName().equals(fd1) && avatar != bd2)) {
                avatar.setGrounded(true);
                sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
            }

            // Check for win condition
            if ((bd1 == avatar   && bd2 == goalDoor) ||
                    (bd1 == goalDoor && bd2 == avatar)) {
                setComplete(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.  The main use of this method
     * is to determine when the characer is NOT on the ground.  This is how we prevent
     * double jumping.
     */
    public void endContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        Object bd1 = body1.getUserData();
        Object bd2 = body2.getUserData();

        if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
                (avatar.getSensorName().equals(fd1) && avatar != bd2)) {
            sensorFixtures.remove(avatar == bd1 ? fix2 : fix1);
            if (sensorFixtures.size == 0) {
                avatar.setGrounded(false);
            }
        }
    }

    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {}
    /** Unused ContactListener method */
    public void preSolve(Contact contact, Manifold oldManifold) {}


    /**
     * Draw the physics objects together with foreground and background
     *
     * This is completely overridden to support custom background and foreground art.
     *
     * @param dt Timing values from parent loop
     */
    public void draw(float dt) {
        canvas.clear();

        // Draw background unscaled.
        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.end();

        canvas.begin();
        for(Obstacle obj : objects) {
            obj.draw(canvas);
        }
        canvas.end();

        if (isDebug()) {
            canvas.beginDebug();
            for(Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }

//        // Draw foreground last.
//        canvas.begin();
//        canvas.draw(foregroundTexture, FORE_COLOR,  0, 0, canvas.getWidth(), canvas.getHeight());
//        selector.draw(canvas);
//        canvas.end();
    }


}
