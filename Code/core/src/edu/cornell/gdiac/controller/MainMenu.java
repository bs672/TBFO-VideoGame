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
public class MainMenu extends WorldController implements ContactListener {

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


    private static final String OOB_FILE  = "space/Oob/oob_2.png";


    /** The texture file for the planets */
    private static final String BLUE_P_1 = "space/planets/blue.png";
    private static final String BLUE_P_2 = "space/planets/blue2.png";
    private static final String BLUE_P_3 = "space/planets/blue3.png";
    //private static final String BLUE_P_4 = "space/planets/blue.png";

    /** The texture file for the planets */
    private static final String PURPLE_P_1 = "space/planets/purple.png";
    private static final String PURPLE_P_2 = "space/planets/purple2.png";
    private static final String PURPLE_P_3 = "space/planets/purple3.png";
    //private static final String PURPLE_P_4 = "space/planets/purple.png";

    /** The texture file for the planets */
    private static final String ORANGE_P_1 = "space/planets/orange.png";
    private static final String ORANGE_P_2 = "space/planets/orange2.png";
    private static final String ORANGE_P_3 = "space/planets/orange3.png";
    //private static final String ORANGE_P_4 = "space/planets/orange.png";

    /** The texture file for the planets */
    private static final String SKY_P_1 = "space/planets/sky.png";
    private static final String SKY_P_2 = "space/planets/sky2.png";
    private static final String SKY_P_3 = "space/planets/sky3.png";
    //private static final String SKY_P_4 = "space/planets/sky.png";


    /** The texture file for the planets */

    /** The texture file for the planets */
    private static final String GREEN_P_1 = "space/planets/green.png";
    private static final String GREEN_P_2 = "space/planets/green2.png";
    private static final String GREEN_P_3 = "space/planets/green3.png";
    //private static final String GREEN_P_4 = "space/planets/green.png";

    /** The texture file for the planets */
    private static final String PINK_P_1 = "space/planets/pink.png";
    private static final String PINK_P_2 = "space/planets/pink2.png";
    private static final String PINK_P_3 = "space/planets/pink3.png";
    //private static final String PINK_P_4 = "space/planets/pink.png";

    /** The texture file for the planets */
    private static final String RED_P_1 = "space/planets/red.png";
    private static final String RED_P_2 = "space/planets/red2.png";
    private static final String RED_P_3 = "space/planets/red3.png";
    //private static final String RED_P_4 = "space/planets/red.png";

    /** The texture file for the planets */
    private static final String POISON_P_1 = "space/planets/sun.png";
    private static final String POISON_P_2 = "space/planets/sun.png";
    private static final String POISON_P_3 = "space/planets/sun.png";
    private static final String POISON_P_4 = "space/planets/sun.png";

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
    /** Texture file for background image */
    private static final String BACKG_FILE_LARGE_STARS = "space/background/large-stars.png";
    /** Texture file for background image */
    private static final String BACKG_FILE_MEDIUM_STARS = "space/background/medium-stars.png";
    /** Texture file for background image */
    private static final String BACKG_FILE_SMALL_STARS = "space/background/small-stars.png";

    private static final String TITLE = "space/menus/title.png";



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

    /** Expulsion texture */
    private TextureRegion expulsion_Texture;


    /** Texture asset for background image */
    private TextureRegion backgroundTextureMAIN;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureLARGESTAR;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureMEDIUMSTAR;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureSMALLSTAR;
    /** Texture asset for background image */
    private TextureRegion titleTexture;



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

        manager.load(EXPULSION_TEXTURE, Texture.class);
        assets.add(EXPULSION_TEXTURE);

        manager.load(BLUE_P_1, Texture.class);
        assets.add(BLUE_P_1);
        manager.load(BLUE_P_2, Texture.class);
        assets.add(BLUE_P_2);
        manager.load(BLUE_P_3, Texture.class);
        assets.add(BLUE_P_3);
        // manager.load(BLUE_P_4, Texture.class);
        //  assets.add(BLUE_P_4);

        manager.load(PURPLE_P_1, Texture.class);
        assets.add(PURPLE_P_1);
        manager.load(PURPLE_P_2, Texture.class);
        assets.add(PURPLE_P_2);
        manager.load(PURPLE_P_3, Texture.class);
        assets.add(PURPLE_P_3);
        //  manager.load(PURPLE_P_4, Texture.class);
        //  assets.add(PURPLE_P_4);

        manager.load(ORANGE_P_1, Texture.class);
        assets.add(ORANGE_P_1);
        manager.load(ORANGE_P_2, Texture.class);
        assets.add(ORANGE_P_2);
        manager.load(ORANGE_P_3, Texture.class);
        assets.add(ORANGE_P_3);
        //   manager.load(ORANGE_P_4, Texture.class);
        //   assets.add(ORANGE_P_4);

        manager.load(SKY_P_1, Texture.class);
        assets.add(SKY_P_1);
        manager.load(SKY_P_2, Texture.class);
        assets.add(SKY_P_2);
        manager.load(SKY_P_3, Texture.class);
        assets.add(SKY_P_3);
        //   manager.load(SKY_P_4, Texture.class);
        //   assets.add(SKY_P_4);

        manager.load(GREEN_P_1, Texture.class);
        assets.add(GREEN_P_1);
        manager.load(GREEN_P_2, Texture.class);
        assets.add(GREEN_P_2);
        manager.load(GREEN_P_3, Texture.class);
        assets.add(GREEN_P_3);
        //  manager.load(GREEN_P_4, Texture.class);
        //   assets.add(GREEN_P_4);

        manager.load(PINK_P_1, Texture.class);
        assets.add(PINK_P_1);
        manager.load(PINK_P_2, Texture.class);
        assets.add(PINK_P_2);
        manager.load(PINK_P_3, Texture.class);
        assets.add(PINK_P_3);
        // manager.load(PINK_P_4, Texture.class);
        // assets.add(PINK_P_4);

        manager.load(RED_P_1, Texture.class);
        assets.add(RED_P_1);
        manager.load(RED_P_2, Texture.class);
        assets.add(RED_P_2);
        manager.load(RED_P_3, Texture.class);
        assets.add(RED_P_3);
        // manager.load(RED_P_4, Texture.class);
        //assets.add(RED_P_4);

        manager.load(POISON_P_1, Texture.class);
        assets.add(POISON_P_1);
        manager.load(POISON_P_2, Texture.class);
        assets.add(POISON_P_2);
        manager.load(POISON_P_3, Texture.class);
        assets.add(POISON_P_3);
        manager.load(POISON_P_4, Texture.class);
        assets.add(POISON_P_4);

        manager.load(COMMAND_P, Texture.class);
        assets.add(COMMAND_P);

        manager.load(NEUTRAL_P, Texture.class);
        assets.add(NEUTRAL_P);

        manager.load(DYING_P, Texture.class);
        assets.add(DYING_P);

        manager.load(BACKG_FILE_MAIN, Texture.class);
        assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_LARGE_STARS, Texture.class);
        assets.add(BACKG_FILE_LARGE_STARS);
        manager.load(BACKG_FILE_MEDIUM_STARS, Texture.class);
        assets.add(BACKG_FILE_MEDIUM_STARS);
        manager.load(BACKG_FILE_SMALL_STARS, Texture.class);
        assets.add(BACKG_FILE_SMALL_STARS);
        manager.load(TITLE, Texture.class);
        assets.add(TITLE);

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

        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);

        TEXTURES[0] = neutral_P_Texture;
        TEXTURES[1] = neutral_P_Texture;
        TEXTURES[2] = neutral_P_Texture;
        TEXTURES[3] = neutral_P_Texture;

        backgroundTextureMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundTextureLARGESTAR = createTexture(manager,BACKG_FILE_LARGE_STARS,true);
        backgroundTextureMEDIUMSTAR = createTexture(manager,BACKG_FILE_MEDIUM_STARS,true);
        backgroundTextureSMALLSTAR = createTexture(manager,BACKG_FILE_SMALL_STARS,true);
        backgroundTextureLARGESTAR = createTexture(manager,BACKG_FILE_LARGE_STARS,false);
        backgroundTextureMEDIUMSTAR = createTexture(manager,BACKG_FILE_MEDIUM_STARS,false);
        backgroundTextureSMALLSTAR = createTexture(manager,BACKG_FILE_SMALL_STARS,false);
        titleTexture = createTexture(manager,TITLE, true);

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
            {0.0f, 3.0f, 1f, 3f},   // NOTHING
            {25, 10f, 1f, 3f},  //SETTINGS
            {15.0f, 3f, 1f, 3f},    //LEVEL SELECT
            {7.0f, 4.5f, 1f, 3f},   //PLAY

    };

    private boolean jumpedOnce;

    private static final TextureRegion[] TEXTURES = new TextureRegion[PLANETS.length];

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
    public MainMenu() {
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
            obj.setTexture(TEXTURES[ii]);
            addObject(obj);
            planets.add(obj);
        }

        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(16, 12, OOB_RADIUS, 50);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setTexture(avatarTexture);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.3f*OOB_RADIUS, .3f*OOB_RADIUS));
        addObject(complexAvatar);

        aiController = new AIController(ships, planets, commandPlanets, complexAvatar, scale);
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

        if (!isFailure() && complexAvatar.getY() < -1) {
            setFailure(true);
            return false;
        }

        return true;
    }


    public void scrollScreen() {
        if(currentPlanet != null) {
            vecToCenter.set(canvas.getWidth()/80f - currentPlanet.getX(), canvas.getHeight()/80f - currentPlanet.getY());
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
            for(Obstacle o : objects) {
                o.setX(o.getX() - complexAvatar.getCenter().getVX() / 60);
                o.setY(o.getY() - complexAvatar.getCenter().getVY() / 60);
            }
        }
    }

    //Finds closest planet
    public void findPlanet(){
        returnToPlanetTimer++;
        Vector2 smallestRad = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        int closestPlanet = 0;
        Vector2 radDir;
        for (int i = 0; i < planets.size; i++) {
            radDir = new Vector2(complexAvatar.getX() - planets.get(i).getX(), complexAvatar.getY() - planets.get(i).getY());
            if (radDir.len() < smallestRad.len() && ((!lastPlanet.equals(planets.get(i)) && returnToPlanetTimer < 60) || returnToPlanetTimer >= 60)) {
                smallestRad = radDir.cpy();
                closestPlanet = i;
            }
        }
        if (smallestRad.len() < planets.get(closestPlanet).getRadius() + complexAvatar.getRadius() + EPSILON) {
            currentPlanet = planets.get(closestPlanet);
            returnToPlanetTimer = 0;
            if(!mute)
                SoundController.getInstance().play(PEW_FILE, PEW_FILE, false, EFFECT_VOLUME);
        }
    }

    //Oob loses mass
    public void changeMass(float massChange){
        oldAvatarRad = complexAvatar.getRadius();
        float oldOobMass = complexAvatar.getMass();
        if(complexAvatar.getRadius()>=OOB_DEATH_RADIUS) {
            complexAvatar.setRadius((float) Math.sqrt((oldOobMass + massChange) / Math.PI));
            complexAvatar.scalePicScale(new Vector2(complexAvatar.getRadius() / oldAvatarRad, complexAvatar.getRadius() / oldAvatarRad));
        }
    }

    //Make Oob move around the planet
    public void moveAroundPlanet(){
        if (moveDirection == 1) {
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).scl(1f * complexAvatar.getMass()));
        } else if (moveDirection == -1) {
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).scl(-1f * complexAvatar.getMass()));
        }
    }

    //Make Oob jump
    public void jump(){
        if(!mute)
            SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
        complexAvatar.setLinearVelocity(complexAvatar.getLinearVelocity().cpy().add(smallestRad.cpy().nor().scl(10)));
        lastPlanet = currentPlanet;
        currentPlanet = null;
    }

    //Determines whether the player is using mouse or keyboard and sets associated variables when Oob's on a planet
    public void groundPlayerControls(){
        if (InputController.getInstance().didReset()) {
            reset();
        }
        if (control==1){
            Vector2 mouse = InputController.getInstance().getCursor();
            mouse = mouse.sub(currentPlanet.getPosition());
            float angle = mouse.angle();
            Vector2 oob = complexAvatar.getPosition();
            oob.sub(currentPlanet.getPosition());
            float angle2 = oob.angle();
            if(Math.abs(angle - angle2) <= THRESHOLD)
                moveDirection = 0;
            else if((angle - angle2+360)%360 <= 180 && (angle - angle2+360)%360 > 0){
                moveDirection = -1;
            }
            else {
                moveDirection = 1;
            }
            jump = InputController.getInstance().getMouseJump();
        }
        else{
            jump = InputController.getInstance().getJump();
            moveDirection = InputController.getInstance().getHorizontal();
        }
    }

    //Determines whether the player is using mouse or keyboard and sets associated variables when Oob's in the air
    public void airPlayerControls() {
        if (InputController.getInstance().didReset()) {
            reset();
        }
        if (control==1){
            launchVec = complexAvatar.getPosition().cpy().sub(InputController.getInstance().getCursor());
            jump = InputController.getInstance().getMouseJump();
        }
        else{
            jump = InputController.getInstance().getJump();
            moveDirection = InputController.getInstance().getHorizontal();
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

        if (currentPlanet != null) {
            smallestRad = new Vector2(complexAvatar.getX() - currentPlanet.getX(), complexAvatar.getY() - currentPlanet.getY());
            complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17-complexAvatar.getMass()));
            //determines mouse or keyboard controls

            for (int i = 1; i <= 2; i++) {
                if (currentPlanet == planets.get(i)) {
                    listener.exitScreen(this, i);
                    return;
                }
            }
            if (currentPlanet == planets.get(3)) {
                listener.exitScreen(this, 4);
                return;
            }

            groundPlayerControls();
            if (jump) {
                jump();
            } else {
                if(complexAvatar.getLinearVelocity().len() < 7) {
                    moveAroundPlanet();
                }
                if(smallestRad.len() < currentPlanet.getRadius() + 0.1f) {
                    lastPlanet = currentPlanet;
                    currentPlanet = null;
                }
            }
        }
        else if(currentPlanet == null) { // we're floating in space
            airPlayerControls();
            if(jump) {
                Vector2 massLoc = complexAvatar.getPosition().cpy().add(launchVec.cpy().nor().scl(complexAvatar.getRadius() + 0.5f));
                WheelObstacle expulsion = new WheelObstacle(massLoc.x, massLoc.y, 0.25f);
                expulsion.setGravityScale(0);
                expulsion.setName("expulsion");
                expulsion.setDrawScale(scale);
                expulsion.setTexture(expulsion_Texture);
                expulsion.scalePicScale(new Vector2(0.3f, 0.3f));
                addObject(expulsion);
                expulsion.setLinearVelocity(launchVec.cpy().scl(2));
                changeMass((float)Math.PI * -0.04f);
                Vector2 velocityChange = launchVec.cpy().scl(-5*expulsion.getMass() / complexAvatar.getMass());
                complexAvatar.setLinearVelocity(complexAvatar.getLinearVelocity().cpy().add(velocityChange));
            }
            if(complexAvatar.getCenter().getLinearVelocity().len() < 4)
                complexAvatar.setLinearVelocity(complexAvatar.getCenter().getLinearVelocity().cpy().nor().scl(4));
            findPlanet();
        }
        complexAvatar.applyForce();
        complexAvatar.resetForceVec();

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
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
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle)body1.getUserData();
            Obstacle bd2 = (Obstacle)body2.getUserData();

            if(bd1.getName().equals("bullet"))
                removeBullet(bd1);

            if(bd2.getName().equals("bullet"))
                removeBullet(bd2);

            if (bd1.getName().equals("bullet") && bd2.getName().equals("Oob")) {
                oldAvatarRad = complexAvatar.getRadius();
                changeMass(BULLET_DAMAGE);
                if(!mute)
                    SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
            }
            else if (bd2.getName().equals("bullet") && bd1.getName().equals("Oob")) {
                oldAvatarRad = complexAvatar.getRadius();
                changeMass(BULLET_DAMAGE);
                if(!mute)
                    SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
            }

            if (bd1.getName().equals("ship") && bd2.getName().equals("Oob")) {
                bd1.markRemoved(true);
                aiController.removeShip((ShipModel)bd1);
            }
            else if (bd2.getName().equals("ship") && bd1.getName().equals("Oob")) {
                bd2.markRemoved(true);
                aiController.removeShip((ShipModel)bd2);
            }

            if(bd1.getName().equals("planet") && bd2.getName().equals("Oob")) {
                currentPlanet = (PlanetModel)bd1;
            }
            else if(bd2.getName().equals("planet") && bd1.getName().equals("Oob")) {
                currentPlanet = (PlanetModel)bd2;
            }

            if(bd1.getName().equals("Oob") && bd2.getName().equals("expulsion")) {
                bd2.markRemoved(true);
                changeMass(((WheelObstacle)bd2).getMass());
            }
            else if(bd1.getName().equals("expulsion") && bd2.getName().equals("Oob")) {
                bd1.markRemoved(true);
                changeMass(((WheelObstacle)bd1).getMass());
            }

            // See if we have landed on the ground.
//            if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
//                    (avatar.getSensorName().equals(fd1) && avatar != bd2)) {
//                avatar.setGrounded(true);
//                sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
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

//        if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
//                (avatar.getSensorName().equals(fd1) && avatar != bd2)) {
//            sensorFixtures.remove(avatar == bd1 ? fix2 : fix1);
//            if (sensorFixtures.size == 0) {
//                avatar.setGrounded(false);
//            }
//        }
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
        int blueImageWidth = 0;
        Vector2 bottomLeft = new Vector2();
        if (canvas.getWidth() < 1280) {
            blueImageWidth = 1280;
            bottomLeft.x = (1280 - canvas.getWidth()) / 2;
        } else {
            blueImageWidth = canvas.getWidth();
            bottomLeft.x = 1280 + (1280 - canvas.getWidth()) / 2;
        }
        canvas.draw(backgroundTextureMAIN, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundTextureLARGESTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundTextureMEDIUMSTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundTextureSMALLSTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.draw(titleTexture, Color.WHITE, canvas.getWidth() / 2 - (titleTexture.getRegionWidth() / 2) + 50, 400, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.end();
        canvas.begin();
        for (Obstacle obj : objects) {
            obj.draw(canvas);

        }
        canvas.end();
        if (isDebug()) {
            canvas.beginDebug();
            for (Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }

        canvas.begin();
        canvas.drawText("Play", massFont, planets.get(3).getX() * 38f, planets.get(3).getY() * 41f);
        canvas.drawText("Settings", massFont, planets.get(1).getX() * 38f, planets.get(1).getY() * 41f);
        canvas.drawText("Levels", massFont, planets.get(2).getX() * 38f, planets.get(2).getY() * 41f);
        canvas.end();
    }
}





