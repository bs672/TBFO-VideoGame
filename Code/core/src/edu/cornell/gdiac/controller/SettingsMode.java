package edu.cornell.gdiac.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.*;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.model.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Files;



/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class SettingsMode extends WorldController implements ContactListener {

    private ScreenListener listener;

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
    /** The texture file for the character avatar (no animation) */


    private static final String OOB_FILE  = "space/Oob/oob2.png";

    private static final String MAIN_MENU_TEXTURE  = "space/menus/exit_to_menu_planet.png";
    private static final String MAIN_MENU_HOVER_TEXTURE  = "space/menus/exit_to_menu_planet_hover.png";
    private static final String BACK_TEXTURE  = "space/menus/back.png";
    private static final String BACK_HOVER_TEXTURE  = "space/menus/back_hover.png";
    private static final String MUTE_TEXTURE  = "space/menus/mute.png";

    // Animator sun = new Animator(8,1,.33f,"space/planets/sunAnim.png");

    //Animator sun = new Animator();

    // Animation test2= new Animation(20,)

    /** The texture file for the planets */
    private static final String COMMAND_P = "space/planets/command.png";

    /** The texture file for the planets */
    private static final String NEUTRAL_P = "space/planets/neutral.png";

    /** The texture file for the planets */
    private static final String DYING_P = "space/planets/dying.png";

    /** Texture file for background image */
    private static final String BACKG_FILE_MAIN = "space/background/blue-background.png";
    private static final String BACKG_FILE_WHITE_STAR = "space/background/white-stars.png";
    private static final String BACKG_FILE_LG_STAR = "space/background/large-stars.png";
    private static final String BACKG_FILE_MED_STAR = "space/background/medium-stars.png";
    private static final String BACKG_FILE_SM_STAR = "space/background/small-stars.png";

    private static final String SETTINGS = "space/menus/settings.png";



    /** Texture file for ship */
    private static final String SHIP_TEXTURE = "space/ships/ship.png";
    /** The texture file for the bullets */
    private static final String BULLET_TEXTURE = "space/ships/bullet.png";
    /** The texture file for mass expulsion */
    private static final String EXPULSION_TEXTURE = "space/Oob/expulsion.png";


    /** Parallax values */
    private static final float BG_MAIN_PARALLAX    = 0;  	// Parallax = 0 means we're infinitely far away
    private static final float BG_WHITE_PARALLAX = 0.4f;
    private static final float BG_RED_PARALLAX   = 0.9f;
    private static final float PLANET_PARALLAX      = 1.0f;	// Put focus of scene at parallax 1
    private static final float FOREGROUND_PARALLAX   = 2.0f;	// Parallax > 1 is a foreground object

    /** The sound file for a jump */
    private static final String JUMP_FILE = "space/audio/jump.mp3";
    /** The sound file for a bullet fire */
    private static final String PEW_FILE = "space/audio/pew.mp3";
    /** The sound file for a bullet collision */
    private static final String POP_FILE = "space/audio/plop.mp3";
    /** The initial position of Oob */
    private static Vector2 OOB_POS = new Vector2(0f, 0f);
    /** Oob's initial radius */
    private  float OOB_RADIUS = 1f; //0.2 scale in overlap2d is standard

    private static final float SIPHON = 0.02f;

    private static final float POISON = -0.02f;

    private static final float MIN_RADIUS = 1f;

    private static final float DEATH_RADIUS = MIN_RADIUS*2/3;

    private static final float OOB_DEATH_RADIUS = 0.56f;

    private static final float EPSILON = 0.1f;

    private static final int THRESHOLD = 4;

    //control = 0 is keyboard, control = 1 is mouse
    private int control = 1;
    /** Texture asset for character avatar */
    private TextureRegion avatarTexture;

    /** Planet texture */
    private TextureRegion neutral_P_Texture;

    /** Main Menu Texture */
    private TextureRegion main_Menu_Texture;

    /** Main Menu Hover Texture */
    private TextureRegion main_Menu_Hover_Texture;

    private TextureRegion back_Texture;

    private TextureRegion back_hover_Texture;

    private TextureRegion mute_Texture;

    /** Expulsion texture */
    private TextureRegion expulsion_Texture;


    /** Background texture */
    private TextureRegion backgroundMAIN;
    private TextureRegion backgroundWHITESTAR;
    private TextureRegion backgroundLG;
    private TextureRegion backgroundMED;
    private TextureRegion backgroundSM;

    private TextureRegion settingsTexture;

    private boolean[] lastInPlanet;

    /** Texture asset for ship */
    private TextureRegion ship_texture;
    /** Texture asset for bullet */
    private TextureRegion bullet_texture;

    //variables
    private Vector2 smallestRad;
    private float rad;
    private float oldAvatarRad;
    //variables for player controls
    boolean jump = false;
    private float moveDirection = 0f;
    private boolean mute = true;
    private Vector2 launchVec;

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

        manager.load(MAIN_MENU_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_TEXTURE);

        manager.load(MAIN_MENU_HOVER_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_HOVER_TEXTURE);

        manager.load(BACK_HOVER_TEXTURE, Texture.class);
        assets.add(BACK_HOVER_TEXTURE);

        manager.load(BACK_TEXTURE, Texture.class);
        assets.add(BACK_TEXTURE);

        manager.load(MUTE_TEXTURE, Texture.class);
        assets.add(MUTE_TEXTURE);

        manager.load(EXPULSION_TEXTURE, Texture.class);
        assets.add(EXPULSION_TEXTURE);

        manager.load(COMMAND_P, Texture.class);
        assets.add(COMMAND_P);

        manager.load(NEUTRAL_P, Texture.class);
        assets.add(NEUTRAL_P);

        manager.load(DYING_P, Texture.class);
        assets.add(DYING_P);

        manager.load(BACKG_FILE_MAIN, Texture.class);
        assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_MAIN, Texture.class);
        assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_WHITE_STAR, Texture.class);
        assets.add(BACKG_FILE_WHITE_STAR);
        manager.load(BACKG_FILE_LG_STAR, Texture.class);
        assets.add(BACKG_FILE_LG_STAR);
        manager.load(BACKG_FILE_MED_STAR, Texture.class);
        assets.add(BACKG_FILE_MED_STAR);
        manager.load(BACKG_FILE_SM_STAR, Texture.class);
        assets.add(BACKG_FILE_SM_STAR);
        manager.load(SETTINGS, Texture.class);
        assets.add(SETTINGS);

        manager.load(SHIP_TEXTURE, Texture.class);
        assets.add(SHIP_TEXTURE);
        manager.load(BULLET_TEXTURE, Texture.class);
        assets.add(BULLET_TEXTURE);

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
        expulsion_Texture = createTexture(manager,EXPULSION_TEXTURE, false);

        main_Menu_Texture = createTexture(manager,MAIN_MENU_TEXTURE,false);
        main_Menu_Hover_Texture = createTexture(manager,MAIN_MENU_HOVER_TEXTURE,false);
        mute_Texture = createTexture(manager,MUTE_TEXTURE,false);
        back_Texture = createTexture(manager,BACK_TEXTURE,false);
        back_hover_Texture = createTexture(manager,BACK_HOVER_TEXTURE,false);

        TEXTURES[0][0] = main_Menu_Texture;    // MAIN MENU
        TEXTURES[0][1] = main_Menu_Hover_Texture;
        TEXTURES[1][0] = mute_Texture;    // MUTE
        TEXTURES[1][1] = mute_Texture; //TODO: FIX THIS
        TEXTURES[2][0] = back_Texture;    // BACK
        TEXTURES[2][1] = back_hover_Texture;

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);
        settingsTexture = createTexture(manager,SETTINGS, true);

        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

    /**
     * Helper to initialize a texture after loading.
     *
     * @param manager Reference to global asset manager
     * @param key The key identifying the texture in the loader
     *
     * @return the texture newly initialized
     */
    private static Texture loadTexture(AssetManager manager, String key) {
        Texture result = null;
        if (manager.isLoaded(key)) {
            result = manager.get(key, Texture.class);
            result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return result;
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
    /** The damage of the bullet */
    private static final float  BULLET_DAMAGE = -0.00f;
    /** The volume for sound effects */
    private static final float EFFECT_VOLUME = 0.8f;

    private static final float[][] PLANETS = {
            {0.0f, 4f, 1.1f, 3f},   // MAIN MENU
            {25, 8f, 0.9f, 3f},  // MUTE
            {15.0f, 11f, 1.5f, 3f},    // BACK
    };

    private boolean jumpedOnce;

    private static final TextureRegion[][] TEXTURES = new TextureRegion[PLANETS.length][2];

    private Array<Array<Float>> SHIPS = new Array<Array<Float>>();
    // Physics objects for the game
    /** Reference to the character avatar */
    private OobModel avatar;
    private ComplexOobModel complexAvatar;
    /** Reference to current planet Oob's on */
    private PlanetModel currentPlanet;
    /** last planet on */
    private PlanetModel lastPlanet;
    /** List of all live planets */
    private Array<PlanetModel> planets;
    //List of command planets
    private Array<PlanetModel> commandPlanets;
    /** list of ships */
    private Array<ShipModel> ships;
    /** vector from Oob to center of the screen */
    private Vector2 vecToCenter = new Vector2();
    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture> sensorFixtures;
    /** the font for the mass text on each object */
    private BitmapFont massFont;

    private int returnToPlanetTimer;

    private float width;
    private float height;
    /** if we've just loaded */
    private boolean justLoaded = true;
    /** AIController */
    private AIController aiController;

    //private String jsonString = "{\"sceneName\":\"MainScene\",\"composite\":{\"sImages\":[{\"uniqueId\":4,\"tags\":[],\"customVars\":\"Type:1\",\"x\":6.6625,\"y\":3.9375,\"scaleX\":0.3,\"scaleY\":0.3,\"originX\":0.75,\"originY\":1.6875,\"layerName\":\"Default\",\"imageName\":\"ship\"},{\"uniqueId\":5,\"tags\":[],\"customVars\":\"Type:3\",\"x\":5.55,\"y\":1.7000003,\"scaleX\":0.2,\"scaleY\":0.2,\"originX\":3,\"originY\":3,\"zIndex\":1,\"layerName\":\"Default\",\"imageName\":\"command\"},{\"uniqueId\":6,\"tags\":[],\"x\":-3.375,\"y\":0.42499995,\"scaleX\":0.2,\"scaleY\":0.2,\"originX\":4.06875,\"originY\":4.06875,\"zIndex\":2,\"layerName\":\"Default\",\"imageName\":\"sun\"},{\"uniqueId\":7,\"tags\":[],\"x\":-3.1125002,\"y\":-2.9875,\"scaleX\":0.2,\"scaleY\":0.2,\"originX\":3,\"originY\":3,\"zIndex\":3,\"layerName\":\"Default\",\"imageName\":\"neutral\"},{\"uniqueId\":8,\"tags\":[],\"x\":2.1125,\"y\":-0.4375,\"scaleX\":0.3,\"scaleY\":0.3,\"originX\":3,\"originY\":3,\"zIndex\":4,\"layerName\":\"Default\",\"imageName\":\"purple\"},{\"uniqueId\":9,\"tags\":[],\"x\":0.7750001,\"y\":-0.76250005,\"scaleX\":0.3,\"scaleY\":0.3,\"originX\":4.375,\"originY\":4.375,\"zIndex\":5,\"layerName\":\"Default\",\"imageName\":\"oob2\"}],\"layers\":[{\"layerName\":\"Default\",\"isVisible\":true}]},\"physicsPropertiesVO\":{}}";
    /**
     * Creates and initialize a new instance of the platformer game
     *
     * The game has default gravity and other settings
     */
    public SettingsMode() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
        sensorFixtures = new ObjectSet<Fixture>();
        planets = new Array<PlanetModel>();
        commandPlanets = new Array<PlanetModel>();
        ships = new Array<ShipModel>();
        massFont = new BitmapFont();
        massFont.getData().setScale(2);
        launchVec = new Vector2();
        returnToPlanetTimer = 0;
        jumpedOnce = false;
        lastInPlanet = new boolean[PLANETS.length];
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        jumpedOnce = false;
        justLoaded = true;
        Vector2 gravity = new Vector2(world.getGravity() );

        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        planets.clear();
        commandPlanets.clear();
        ships.clear();
        world.dispose();

        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
        lastInPlanet = new boolean[PLANETS.length];
        for(Obstacle o: objects){
            if(!o.equals(complexAvatar) &&  !o.equals(planets.get(0))){
                o.setPosition(o.getPosition().cpy().add(new Vector2 (canvas.getWidth()/80f - 16f, canvas.getHeight()/80f - 9f)));
            }
        }
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {

        PLANETS[0][0] = canvas.getWidth()/80f;
        PLANETS[1][0] = canvas.getWidth()/80f;
        PLANETS[2][0] = canvas.getWidth()/80f;

        // Create Planets
        String pname = "planet";
        for (int ii = 0; ii <PLANETS.length; ii++) {
            PlanetModel obj;
            obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
            obj.setName(pname + ii);
            obj.setTexture(TEXTURES[ii][0]);
            addObject(obj);
            planets.add(obj);
        }

        currentPlanet = planets.get(1); //The first planet is always the starting planet

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

        if (!isFailure()) {
            setFailure(true);
            return false;
        }

        return true;
    }


    public void scrollScreen() {
    }



    //Determines whether the player is using mouse or keyboard and sets associated variables when Oob's on a planet
    public void groundPlayerControls(){
        if (InputController.getInstance().didReset()) {
            reset();
        }
        if (control==1){
            jump = InputController.getInstance().getMouseJump();
        }
        else{
            jump = InputController.getInstance().getJump();
        }
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
        scrollScreen();
        width = canvas.getWidth() / 32;
        height = canvas.getHeight() / 18;
        if (InputController.getInstance().getChange()) {
            if (control == 1) {
                control = 0;
            } else {
                control = 1;
            }
        }
        groundPlayerControls();
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i < PLANETS.length; i++) {
            float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
            if ((Math.sqrt(d) < planets.get(i).getRadius())) {
                if (lastInPlanet[i] == false) {
                    planets.get(i).setTexture(TEXTURES[i][1]);
                    planets.get(i).setRadius(planets.get(i).getRadius()*1.1f);
                    planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                }
                lastInPlanet[i] = true;
            }
            else if (lastInPlanet[i] == true) {
                planets.get(i).setTexture(TEXTURES[i][0]);
                planets.get(i).setRadius(planets.get(i).getRadius()*1/1.1f);
                planets.get(i).scalePicScale(new Vector2(1/1.2f, 1/1.2f));
                lastInPlanet[i] = false;
            }
        }
        if (jump) {
            for (int i = 0; i < planets.size; i++) {
                float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
                if (Math.sqrt(d) < planets.get(i).getRadius()) {
                    listener.exitScreen(this, i);
                    System.out.println(i);
                    return;
                }
            }
        }
    }

    /**
     * Remove a new bullet from the world.
     *
     * @param  bullet   the bullet to remove
     */
    public void removeBullet(Obstacle bullet) {
        bullet.markRemoved(true);
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
    }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.  The main use of this method
     * is to determine when the characer is NOT on the ground.  This is how we prevent
     * double jumping.
     */
    public void endContact(Contact contact) {
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

        canvas.begin();

        int LG_S_X;
        int LG_S_Y;

        if ((backgroundLG.getRegionWidth()-canvas.getWidth())>0) {
            LG_S_X = 0;
        }
        else {
            LG_S_X = -(backgroundLG.getRegionWidth()-canvas.getWidth())/2;
        }

        if ((backgroundLG.getRegionHeight()-canvas.getHeight())>0) {
            LG_S_Y = 0;
        }
        else {
            LG_S_Y = -(backgroundLG.getRegionHeight()-canvas.getHeight())/2;
        }


        canvas.draw(backgroundMAIN, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundSM, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundMED, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundWHITESTAR, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X, LG_S_Y,backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(settingsTexture, Color.WHITE, canvas.getWidth() / 2 - (settingsTexture.getRegionWidth() / 2) - 50, 400, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.end();
        canvas.begin();
        for (Obstacle obj : objects) {
            if (obj.getName() != "ComplexOob") {
                obj.draw(canvas);
            }
        }
        canvas.end();
        if (isDebug()) {
            canvas.beginDebug();
            for (Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }
    }
}





