package edu.cornell.gdiac.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    private static final String BACK_TEXTURE  = "space/menus/back_planet.png";
    private static final String BACK_HOVER_TEXTURE  = "space/menus/back_hover_planet.png";
    private static final String MUTE_TEXTURE  = "space/menus/muted.png";
    private static final String UNMUTE_TEXTURE = "space/menus/unmute.png";
    private static final String WASD_TEXTURE = "space/menus/wasd.png";
    private static final String OKL_TEXTURE = "space/menus/okl.png";
    private static final String MUTE_TEXT = "space/menus/mute_settings_label.png";
    private static final String SCROLL_TEXT = "space/menus/controls_settings_label.png";

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

    private static final String SETTINGS = "space/menus/settings_text.png";


    /** Texture file for ship */
    private static final String SHIP_TEXTURE = "space/ships/ship.png";
    /** The texture file for the bullets */
    private static final String BULLET_TEXTURE = "space/ships/bullet.png";
    /** The texture file for mass expulsion */
    private static final String EXPULSION_TEXTURE = "space/Oob/expulsion.png";



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


    private TextureRegion back_Texture;

    private TextureRegion back_hover_Texture;

    private TextureRegion mute_Texture;

    private TextureRegion unmute_Texture;

    private TextureRegion wasd_Texture;

    private TextureRegion okl_Texture;

    private TextureRegion muteText;

    private TextureRegion scrollText;


    /** Background texture */
    private TextureRegion backgroundMAIN;
    private TextureRegion backgroundWHITESTAR;
    private TextureRegion backgroundLG;
    private TextureRegion backgroundMED;
    private TextureRegion backgroundSM;

    private TextureRegion settingsTexture;


    private boolean[] lastInPlanet;


    //variables for player controls
    boolean jump = false;



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

        manager.load(BACK_HOVER_TEXTURE, Texture.class);
        assets.add(BACK_HOVER_TEXTURE);

        manager.load(BACK_TEXTURE, Texture.class);
        assets.add(BACK_TEXTURE);

        manager.load(MUTE_TEXTURE, Texture.class);
        assets.add(MUTE_TEXTURE);

        manager.load(UNMUTE_TEXTURE, Texture.class);
        assets.add(UNMUTE_TEXTURE);

        manager.load(WASD_TEXTURE, Texture.class);
        assets.add(WASD_TEXTURE);

        manager.load(OKL_TEXTURE, Texture.class);
        assets.add(OKL_TEXTURE);

        manager.load(MUTE_TEXT, Texture.class);
        assets.add(MUTE_TEXT);

        manager.load(SCROLL_TEXT, Texture.class);
        assets.add(SCROLL_TEXT);

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

        mute_Texture = createTexture(manager,MUTE_TEXTURE,false);
        unmute_Texture = createTexture(manager, UNMUTE_TEXTURE, false);
        back_Texture = createTexture(manager,BACK_TEXTURE,false);
        back_hover_Texture = createTexture(manager,BACK_HOVER_TEXTURE,false);
        wasd_Texture = createTexture(manager, WASD_TEXTURE, false);
        okl_Texture = createTexture(manager, OKL_TEXTURE, false);
        scrollText = createTexture(manager,SCROLL_TEXT, false);
        muteText = createTexture(manager,MUTE_TEXT, false);

        TEXTURES[0][0] = back_Texture;    // BACK
        TEXTURES[0][1] = back_hover_Texture;
        TEXTURES[1][0] = mute_Texture;    // MUTE
        TEXTURES[1][1] = unmute_Texture; // UNMUTE
        TEXTURES[2][0] = wasd_Texture;    // BACK
        TEXTURES[2][1] = okl_Texture;

        TEXTURES[3][0] = muteText;    // BACK
        TEXTURES[3][1] = muteText;    // BACK
        TEXTURES[4][0] = scrollText;    // BACK
        TEXTURES[4][1] = scrollText;    // BACK

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);
        settingsTexture = createTexture(manager,SETTINGS, false);


        SoundController sounds = SoundController.getInstance();
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

    private static float[][] PLANETS = {
            {0f, 0f, 1.1f, 3f},   // BACK
            {10f, 8f, 1.5f, 3f},  // MUTE
            {20f, 8f, 1.5f, 3f}, // left/right handed
            {10f, 10.5f, 1.5f, 3f},  // MUTE TEXT
            {20f, 10.5f, 1.5f, 3f} // left/right handed TEXT
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
            if(ii==1 && SoundController.getInstance().getMute()){
                obj.setTexture(TEXTURES[ii][0]);
            }
            else if(ii==1){
                obj.setTexture(TEXTURES[ii][1]);
            }
            else if(ii==2 && InputController.getInstance().getWASD()){
                obj.setTexture(TEXTURES[ii][0]);
            }
            else if(ii==2) {
                obj.setTexture(TEXTURES[ii][1]);
            }
            else{
                obj.setTexture(TEXTURES[ii][0]);
            }
            addObject(obj);
            planets.add(obj);
        }


        currentPlanet = planets.get(0); //The first planet is always the starting planet

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

    public void scrollScreen() {
        if(currentPlanet != null) {
            vecToCenter.set(canvas.getWidth()/80f - currentPlanet.getX(), canvas.getHeight()/80f - currentPlanet.getY()-5f);
            for (Obstacle o : objects) {
                if (justLoaded) {
                    o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy()));
                    justLoaded = false;
                }
                else {
                    if(o.equals(complexAvatar)) {
                        for(Obstacle p : complexAvatar.getBodies()) {
                            p.setPosition(p.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
                        }
                    }
                    else
                        o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
                }
            }
        }
        else {
            vecToCenter.set(canvas.getWidth()/80f - complexAvatar.getX()-3f, canvas.getHeight()/80f - complexAvatar.getY()-3f);
            for(Obstacle o : objects) {
                if(o.equals(complexAvatar)) {
                    for(Obstacle p : complexAvatar.getBodies()) {
                        p.setPosition(p.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
                    }
                }
                else
                    o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
            }
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
        for (int i = 0; i < PLANETS.length-2; i++) {
            float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
            if ((Math.sqrt(d) < planets.get(i).getRadius())) {
                if (lastInPlanet[i] == false) {
                    if(i!=1 && i!=2)
                        planets.get(i).setTexture(TEXTURES[i][1]);
                    planets.get(i).setRadius(planets.get(i).getRadius()*1.1f);
                    planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                }
                lastInPlanet[i] = true;
            }
            else if (lastInPlanet[i] == true) {
                if (i!=1 && i!=2)
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
                if(i==1 && SoundController.getInstance().getMute()){
                    planets.get(i).setTexture(unmute_Texture);
                }
                else if(i==1){
                    planets.get(i).setTexture(mute_Texture);
                }
                if(i==2 && InputController.getInstance().getWASD()){
                    planets.get(i).setTexture(okl_Texture);
                }
                else if(i==2){
                    planets.get(i).setTexture(wasd_Texture);
                }
                listener.exitScreen(this, i);
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

        canvas.draw(backgroundLG, Color.WHITE, LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y,backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y,backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE,  LG_S_X, LG_S_Y+backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE,  LG_S_X, LG_S_Y-backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y+backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y-backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE,  LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y-backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(backgroundLG, Color.WHITE,LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y+backgroundLG.getRegionHeight(),backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());

        canvas.draw(settingsTexture, Color.WHITE, canvas.getWidth()/2-(settingsTexture.getRegionWidth()/1.5f)/2 , canvas.getHeight()-settingsTexture.getRegionHeight(), settingsTexture.getRegionWidth()/1.5f, settingsTexture.getRegionHeight()/1.5f);
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





