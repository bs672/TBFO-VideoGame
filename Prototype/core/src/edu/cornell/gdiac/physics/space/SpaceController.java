package edu.cornell.gdiac.physics.space;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class SpaceController extends WorldController implements ContactListener {
    /** The texture file for the character avatar (no animation) */
    private static final String OOB_FILE  = "space/Oob_normal.png";
    /** The texture file for the planets */
    private static final String BLUE_P = "space/blue_planet_102.png";
    /** The texture file for the planets */
    private static final String GREEN_P = "space/green_planet_102.png";
    /** The texture file for the planets */
    private static final String ORANGE_P = "space/orange_planet_102.png";
    /** The texture file for the planets */
    private static final String ORNG_RED_P = "space/orange_red_planet_102.png";
    /** The texture file for the planets */
    private static final String RED_P = "space/red_planet_102.png";
    /** Texture file for background image */
    private static final String BACKG_FILE = "space/background_720.png";

    /** The sound file for a jump */
    private static final String JUMP_FILE = "platform/jump.mp3";
    /** The sound file for a bullet fire */
    private static final String PEW_FILE = "platform/pew.mp3";
    /** The sound file for a bullet collision */
    private static final String POP_FILE = "platform/plop.mp3";
    /** The initial position of Oob */
    private static Vector2 OOB_POS = new Vector2(8f, 5.5f);
    /** Oob's initial radius */
    private static float OOB_RADIUS = 1.0f;

    private static final float SIPHON = 0.02f;

    private static final float MIN_RADIUS = 1f;

    private static final float EPSILON = 0.01f;

    //control = 0 is keyboard, control = 1 is mouse
    private int control = 1;
    /** Texture asset for character avatar */
    private TextureRegion avatarTexture;
    /** Planet texture */
    private TextureRegion blue_P_Texture;
    /** Planet texture */
    private TextureRegion green_P_Texture;
    /** Planet texture */
    private TextureRegion orange_P_Texture;
    /** Planet texture */
    private TextureRegion orange_red_P_Texture;
    /** Planet texture */
    private TextureRegion red_P_Texture;
    /** Texture asset for background image */
    private TextureRegion backgroundTexture;

    private boolean jumping = false;

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
        manager.load(BLUE_P, Texture.class);
        assets.add(BLUE_P);
        manager.load(GREEN_P, Texture.class);
        assets.add(GREEN_P);
        manager.load(ORANGE_P, Texture.class);
        assets.add(ORANGE_P);
        manager.load(ORNG_RED_P, Texture.class);
        assets.add(ORNG_RED_P);
        manager.load(RED_P, Texture.class);
        assets.add(RED_P);
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
        blue_P_Texture = createTexture(manager,BLUE_P,false);
        green_P_Texture = createTexture(manager,GREEN_P,false);
        orange_P_Texture = createTexture(manager,ORANGE_P,false);
        orange_red_P_Texture = createTexture(manager,ORNG_RED_P,false);
        red_P_Texture = createTexture(manager,RED_P,false);
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
            {0.0f,0.0f, 0.0f, 18.0f, 0.1f, 18.0f, 0.1f,  0.0f},

            {0.0f,18.0f, 32.0f, 18.0f, 32.0f, 17.9f, 0.0f, 17.9f},

            {31.9f,18.0f, 32.0f, 18.0f, 32.0f, 0.0f, 31.9f, 0.0f},

            {0.0f,0.0f, 0.0f, 0.1f, 32.0f, 0.1f, 32.0f, 0.0f},
    };

    private static final float[][] PLANETS = {
            {8.0f, 4.5f, 1.8f},
            {5.0f, 12.5f, 1.2f},
            {27.0f, 4.5f, 2.1f},
            {25.0f, 12.5f, 1.6f},
            {18.0f, 4.0f, 1.9f}
    };

    // Physics objects for the game
    /** Reference to the character avatar */
    private OobModel avatar;
    /** Reference to current planet Oob's on */
    private WheelObstacle currentPlanet;
    /** List of all live planets */
    private Array<WheelObstacle> planets;

    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture> sensorFixtures;
    /** the font for the mass text on each object */
    private BitmapFont massFont;

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
        planets = new Array<WheelObstacle>();
        massFont = new BitmapFont();
        massFont.getData().setScale(2);
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
        planets.clear();
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

//        String wname = "wall";
//        for (int ii = 0; ii < WALLS.length; ii++) {
//            PolygonObstacle obj;
//            obj = new PolygonObstacle(WALLS[ii], 0, 0);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(BASIC_DENSITY);
//            obj.setFriction(BASIC_FRICTION);
//            obj.setRestitution(BASIC_RESTITUTION);
//            obj.setDrawScale(scale);
//            obj.setName(wname+ii);
//            addObject(obj);
//        }

        String pname = "planet";
        for (int ii = 0; ii <PLANETS.length; ii++){
            WheelObstacle obj;
            obj = new WheelObstacle(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2]);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2((float)(PLANETS[ii][2]/1.6),(float)(PLANETS[ii][2]/1.6)));
            if (ii%5 == 0) {obj.setTexture(blue_P_Texture); }
            if (ii%5 == 1) {obj.setTexture(green_P_Texture); }
            if (ii%5 == 2) {obj.setTexture(orange_P_Texture); }
            if (ii%5 == 3) {obj.setTexture(orange_red_P_Texture); }
            if (ii%5 == 4) {obj.setTexture(red_P_Texture); }

            obj.setName(pname+ii);
            addObject(obj);
            planets.add(obj);
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
        currentPlanet = planets.get(0); //CHANGE THIS FOR EACH LEVEL
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
        if (InputController.getInstance().getChange()){
            if (control == 1){
                control = 0;
            }
            else{
                control = 1;
            }
        }
        // Process actions in object model

        //If Oob is landed on a planet

        if(avatar.getX() < 0 || avatar.getX() > 32 || avatar.getY() < 0 || avatar.getY() > 18)
            reset();

        if(currentPlanet!=null) {


            Vector2 smallestRad = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
            int closestPlanet = 0;
            Vector2 radDir;

            //Loop to get closest planet
            for (int i = 0; i < planets.size; i++) {
                radDir = new Vector2(avatar.getX() - planets.get(i).getX(), avatar.getY() - planets.get(i).getY());
                if (radDir.len() < smallestRad.len() && radDir.len() < planets.get(i).getRadius() + avatar.getRadius() + EPSILON) {
                    smallestRad = radDir.cpy();
                    closestPlanet = i;
                }
            }
            //determines mouse or keyboard controls
            boolean jump = false;
            float moveDirection = 0;
            if (control==1){
                Vector2 mouse = InputController.getInstance().getCursor();
                mouse = mouse.sub(planets.get(closestPlanet).getPosition());
                float angle = mouse.angle();
                Vector2 oob = avatar.getPosition();
                oob.sub(planets.get(closestPlanet).getPosition());
                float angle2 = oob.angle();
                if((angle - angle2+360)%360 <= 180 && (angle - angle2+360)%360 > 1){
                    moveDirection = -1;
                }
                else if((angle - angle2+360)%360 > 180 && (angle - angle2+360)%360<359){
                    moveDirection = 1;
                }
                jump = InputController.getInstance().getMouseJump();
            }
            else{
                jump = InputController.getInstance().getJump();
                moveDirection = InputController.getInstance().getHorizontal();
            }

            if (smallestRad.len() < planets.get(closestPlanet).getRadius() + avatar.getRadius() + EPSILON) {
                avatar.applyForceZero();
                smallestRad.scl((planets.get(closestPlanet).getRadius() + avatar.getRadius()) / smallestRad.len());
                Vector2 mvmtDir = new Vector2(smallestRad.y, -smallestRad.x).scl(1/(20*avatar.getRadius()));
                if (jump) {
                    SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
                    avatar.setMovement(smallestRad.scl((float)(Math.sqrt(avatar.getMass()))/2));
                    currentPlanet = null;
                    avatar.applyForce();
                }
                else {
                    float rad = planets.get(closestPlanet).getRadius();
                    float oldAvatarRad = avatar.getRadius();
                    WheelObstacle cloPl = planets.get(closestPlanet);
                    if(rad > MIN_RADIUS){
                        float oldOobMass = avatar.getMass();
                        float oldPlanMass = cloPl.getMass();
                        cloPl.setRadius((float)Math.sqrt((oldPlanMass - SIPHON)/Math.PI));
                        avatar.setRadius((float)Math.sqrt((oldOobMass + SIPHON)/Math.PI));
                        cloPl.scalePicScale(new Vector2(cloPl.getRadius() / rad, cloPl.getRadius() / rad));
                        avatar.scalePicScale(new Vector2(avatar.getRadius() / oldAvatarRad,avatar.getRadius() / oldAvatarRad));
                    }
                    if (moveDirection == 1) {
                        avatar.setX(cloPl.getX() + smallestRad.x + mvmtDir.x);
                        avatar.setY(cloPl.getY() + smallestRad.y + mvmtDir.y);
                    } else if (moveDirection == -1) {
                        avatar.setX(cloPl.getX() + smallestRad.x - mvmtDir.x);
                        avatar.setY(cloPl.getY() + smallestRad.y - mvmtDir.y);
                    }
                    smallestRad = new Vector2(avatar.getX() - cloPl.getX(), avatar.getY() - cloPl.getY());
                    smallestRad.scl((avatar.getRadius() + cloPl.getRadius())/smallestRad.len());
                    avatar.setX(cloPl.getX() + smallestRad.x);
                    avatar.setY(cloPl.getY() + smallestRad.y);
                    avatar.setMovement(new Vector2(0, 0));
                }
            } else
                avatar.setMovement(new Vector2(0, 0));
        }
        avatar.applyForce();

        // If we use sound, we must remember this.
        SoundController.getInstance().update();

        if (currentPlanet==null) {
            Vector2 smallestRad = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
            int closestPlanet = 0;
            Vector2 radDir;
            for (int i = 0; i < planets.size; i++) {
                radDir = new Vector2(avatar.getX() - planets.get(i).getX(), avatar.getY() - planets.get(i).getY());
                if (radDir.len() < smallestRad.len() && radDir.len() < planets.get(i).getRadius() + avatar.getRadius() + EPSILON) {
                    smallestRad = radDir.cpy();
                    closestPlanet = i;
                }
            }
            if (smallestRad.len() < planets.get(closestPlanet).getRadius() + avatar.getRadius() + EPSILON)
                currentPlanet = planets.get(closestPlanet);
        }
        //need to set currentPlanet to currentPlanetNumber
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
//            if ((bd1 == avatar   && bd2 == goalDoor) ||
//                    (bd1 == goalDoor && bd2 == avatar)) {
//                setComplete(true);
//            }
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
        canvas.begin();
        for(int i = 0; i < planets.size; i++) {
            canvas.drawText(Integer.toString((int)(Math.pow(planets.get(i).getRadius(), 2)*Math.PI)), massFont, planets.get(i).getX()*32 - 8, planets.get(i).getY()*32 + 15);
        }
        canvas.drawText(Integer.toString((int)(Math.pow(avatar.getRadius(), 2)*Math.PI)), massFont, avatar.getX()*32 - 8, avatar.getY()*32 + 15);
        canvas.end();


//        // Draw foreground last.
//        canvas.begin();
//        canvas.draw(foregroundTexture, FORE_COLOR,  0, 0, canvas.getWidth(), canvas.getHeight());
//        selector.draw(canvas);
//        canvas.end();
    }


}
