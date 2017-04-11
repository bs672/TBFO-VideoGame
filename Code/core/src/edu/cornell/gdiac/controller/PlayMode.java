package edu.cornell.gdiac.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.*;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.model.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.utils.ObjectMap;

import javax.swing.plaf.TextUI;


/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class PlayMode extends WorldController implements ContactListener {


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

    private static final String BLACK_HOLE = "space/planets/blackHole.png";


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


    private static final String POISON_P = "space/animations/sunAnim.png";



   // Animator sun = new Animator(8,1,.33f,"space/planets/sunAnim.png");

   // Animator sun = new Animator();



  // Animation test2= new Animation(.125f,poison_P_Strip);




    // Objects used
    Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;

    // A variable for tracking elapsed time for the animation
    float stateTime;

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

    //0 is not paused, 1 is victory pause, 2 is defeat pause
    private int pauseState = 0;

    private int inPause = 0;

    //control = 0 is keyboard, control = 1 is mouse
    private int control = 1;
    /** Texture asset for character avatar */
    private TextureRegion avatarTexture;

    private TextureRegion blackHoleTexture;

    /** Planet texture */
    private TextureRegion blue_P_1_Texture;
    private TextureRegion blue_P_2_Texture;
    private TextureRegion blue_P_3_Texture;
   // private TextureRegion blue_P_4_Texture;

    /** Planet texture */
    private TextureRegion purple_P_1_Texture;
    private TextureRegion purple_P_2_Texture;
    private TextureRegion purple_P_3_Texture;
   // private TextureRegion purple_P_4_Texture;

    /** Planet texture */
    private TextureRegion orange_P_1_Texture;
    private TextureRegion orange_P_2_Texture;
    private TextureRegion orange_P_3_Texture;
    //private TextureRegion orange_P_4_Texture;

    /** Planet texture */
    private TextureRegion sky_P_1_Texture;
    private TextureRegion sky_P_2_Texture;
    private TextureRegion sky_P_3_Texture;
   // private TextureRegion sky_P_4_Texture;

    /** Planet texture */
    private TextureRegion green_P_1_Texture;
    private TextureRegion green_P_2_Texture;
    private TextureRegion green_P_3_Texture;
    //private TextureRegion green_P_4_Texture;

    /** Planet texture */
    private TextureRegion pink_P_1_Texture;
    private TextureRegion pink_P_2_Texture;
    private TextureRegion pink_P_3_Texture;
    //private TextureRegion pink_P_4_Texture;

    /** Planet texture */
    private TextureRegion red_P_1_Texture;
    private TextureRegion red_P_2_Texture;
    private TextureRegion red_P_3_Texture;
    //private TextureRegion red_P_4_Texture;

    /** Planet texture */
    private TextureRegion poison_P_1_Texture;
    private TextureRegion poison_P_2_Texture;
    private TextureRegion poison_P_3_Texture;
    private TextureRegion poison_P_4_Texture;

    private FilmStrip poison_P_Strip;

    /** Planet texture */
    private TextureRegion command_P_Texture;

    /** Planet texture */
    private TextureRegion neutral_P_Texture;

    /** Planet texture */
    private TextureRegion dying_P_Texture;

    /** Expulsion texture */
    private TextureRegion expulsion_Texture;



    /** Background texture */
    private TextureRegion backgroundMAIN;
    private TextureRegion backgroundWHITESTAR;
    private TextureRegion backgroundLG;
    private TextureRegion backgroundMED;
    private TextureRegion backgroundSM;





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
    private BlackHoleModel outHole;
    private boolean blackHoleWarp;

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

        manager.load(BLACK_HOLE, Texture.class);
        assets.add(BLACK_HOLE);

        manager.load(EXPULSION_TEXTURE, Texture.class);
        assets.add(EXPULSION_TEXTURE);

        manager.load(BLUE_P_1, Texture.class);
        assets.add(BLUE_P_1);
        manager.load(BLUE_P_2, Texture.class);
        assets.add(BLUE_P_2);
        manager.load(BLUE_P_3, Texture.class);
        assets.add(BLUE_P_3);

        manager.load(PURPLE_P_1, Texture.class);
        assets.add(PURPLE_P_1);
        manager.load(PURPLE_P_2, Texture.class);
        assets.add(PURPLE_P_2);
        manager.load(PURPLE_P_3, Texture.class);
        assets.add(PURPLE_P_3);

        manager.load(ORANGE_P_1, Texture.class);
        assets.add(ORANGE_P_1);
        manager.load(ORANGE_P_2, Texture.class);
        assets.add(ORANGE_P_2);
        manager.load(ORANGE_P_3, Texture.class);
        assets.add(ORANGE_P_3);

        manager.load(SKY_P_1, Texture.class);
        assets.add(SKY_P_1);
        manager.load(SKY_P_2, Texture.class);
        assets.add(SKY_P_2);
        manager.load(SKY_P_3, Texture.class);
        assets.add(SKY_P_3);

        manager.load(GREEN_P_1, Texture.class);
        assets.add(GREEN_P_1);
        manager.load(GREEN_P_2, Texture.class);
        assets.add(GREEN_P_2);
        manager.load(GREEN_P_3, Texture.class);
        assets.add(GREEN_P_3);

        manager.load(PINK_P_1, Texture.class);
        assets.add(PINK_P_1);
        manager.load(PINK_P_2, Texture.class);
        assets.add(PINK_P_2);
        manager.load(PINK_P_3, Texture.class);
        assets.add(PINK_P_3);

        manager.load(RED_P_1, Texture.class);
        assets.add(RED_P_1);
        manager.load(RED_P_2, Texture.class);
        assets.add(RED_P_2);
        manager.load(RED_P_3, Texture.class);
        assets.add(RED_P_3);

        manager.load(POISON_P_1, Texture.class);
        assets.add(POISON_P_1);
        manager.load(POISON_P_2, Texture.class);
        assets.add(POISON_P_2);
        manager.load(POISON_P_3, Texture.class);
        assets.add(POISON_P_3);
        manager.load(POISON_P_4, Texture.class);
        assets.add(POISON_P_4);

        manager.load(POISON_P, Texture.class);
        assets.add(POISON_P);




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
        blackHoleTexture = createTexture(manager, BLACK_HOLE, false);
        expulsion_Texture = createTexture(manager,EXPULSION_TEXTURE, false);

        blue_P_1_Texture = createTexture(manager,BLUE_P_1,false);
        blue_P_2_Texture = createTexture(manager,BLUE_P_2,false);
        blue_P_3_Texture = createTexture(manager,BLUE_P_3,false);

        purple_P_1_Texture = createTexture(manager,PURPLE_P_1,false);
        purple_P_2_Texture = createTexture(manager,PURPLE_P_2,false);
        purple_P_3_Texture = createTexture(manager,PURPLE_P_3,false);

        orange_P_1_Texture = createTexture(manager,ORANGE_P_1,false);
        orange_P_2_Texture = createTexture(manager, ORANGE_P_2,false);
        orange_P_3_Texture = createTexture(manager, ORANGE_P_3,false);

        sky_P_1_Texture = createTexture(manager,SKY_P_1,false);
        sky_P_2_Texture = createTexture(manager,SKY_P_2,false);
        sky_P_3_Texture = createTexture(manager,SKY_P_3,false);

        green_P_1_Texture = createTexture(manager, GREEN_P_1,false);
        green_P_2_Texture = createTexture(manager, GREEN_P_2,false);
        green_P_3_Texture = createTexture(manager, GREEN_P_3,false);

        pink_P_1_Texture = createTexture(manager, PINK_P_1,false);
        pink_P_2_Texture = createTexture(manager, PINK_P_2,false);
        pink_P_3_Texture = createTexture(manager, PINK_P_3,false);

        red_P_1_Texture = createTexture(manager, RED_P_1,false);
        red_P_2_Texture = createTexture(manager, RED_P_2,false);
        red_P_3_Texture = createTexture(manager, RED_P_3,false);

        poison_P_1_Texture = createTexture(manager,POISON_P_1,false);
        poison_P_2_Texture = createTexture(manager,POISON_P_2,false);
        poison_P_3_Texture = createTexture(manager,POISON_P_3,false);
        poison_P_4_Texture = createTexture(manager,POISON_P_4,false);

        poison_P_Strip = createFilmStrip(manager,POISON_P,1,8,8);

        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);

        dying_P_Texture = createTexture(manager,DYING_P,false);

        command_P_Texture = createTexture(manager,COMMAND_P,false);

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);

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
    /** Friction of most platforms */
    private static final float  BASIC_FRICTION = 0.4f;
    /** The restitution for all physics objects */
    private static final float  BASIC_RESTITUTION = 0.1f;
    /** The damage of the bullet */
    private static final float  BULLET_DAMAGE = -0.04f;
    /** The volume for sound effects */
    private static final float EFFECT_VOLUME = 0.8f;

    private Array<Array<Float>> PLANETS = new Array<Array<Float>>();
    private Array<Array<Float>> SHIPS = new Array<Array<Float>>();
    private Array<Array<Float>> BLACK_HOLES = new Array<Array<Float>>();
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
    public PlayMode() {
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
        FileHandle json = Gdx.files.internal("overlap2d/Testing/scenes/MainScene.dt");
        String jsonString = json.readString();
        jsonParse(jsonString);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        returnToPlanetTimer = 0;
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

        pauseState = 0;
        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
    }

    //Reads the data from a JSON file and turns it into game data
    //When creating overlap2d levels, make sure the starting purple planet + oob is positioned at (5.33, 2.67)
    private void jsonParse(String data){
        JsonValue full = new JsonReader().parse(data);
        full = full.get(1).get(0);
        JsonValue temp;
        String objectName;
        Array<Array<Float>> planetArray = new Array<Array<Float>>();
        Array<Array<Float>> shipArray = new Array<Array<Float>>();
        Array<Array<Float>> blackHoleArray = new Array<Array<Float>>();
        ObjectMap<Integer, Array<Float>> bhmap = new ObjectMap<Integer, Array<Float>>();
        Array<Float> tempArray;
        float scale = 0f;
        float xPos = 0f;
        float yPos = 0f;
        //starting planet always same
        tempArray = new Array<Float>();
        tempArray.add(0.0f);
        tempArray.add(0.0f);
        tempArray.add(2.5f);
        tempArray.add(3.0f);
        planetArray.add(tempArray);
        for(int i = 0; i<full.size; i++){
            temp = full.get(i);
            objectName = temp.getString("imageName");
            if(temp.has("scaleX"))
                scale = temp.getFloat("scaleX");
            else
                scale = 1f;
            if(temp.has("x"))
                xPos = temp.getFloat("x");
            if(temp.has("y"))
                yPos = temp.getFloat("y");
            if(objectName.equals("neutral")){
                //planets are 6 overlap2d units
                //0.4 scale is good starting
                //2.5 size is good starting in-game
                tempArray = new Array<Float>();
                tempArray.add((xPos+3)*3);
                tempArray.add((yPos+3)*3);
                tempArray.add(2.5f*scale/0.4f);
                tempArray.add(3.0f);
                planetArray.add(tempArray);
            }
            else if(objectName.equals("command")){
                tempArray = new Array<Float>();
                tempArray.add((xPos+3)*3);
                tempArray.add((yPos+3)*3);
                tempArray.add(2.5f*scale/0.4f);
                tempArray.add(1.0f);
                planetArray.add(tempArray);
            }
            else if(objectName.equals("sun")){
                //sun is 8 overlap2d units
                tempArray = new Array<Float>();
                tempArray.add((xPos+4)*3);
                tempArray.add((yPos+4)*3);
                tempArray.add(2.5f*scale/0.3f);
                tempArray.add(2.0f);
                planetArray.add(tempArray);
            }
            else if(objectName.equals("blue")){//We're currently using "BLUE" planets for regular
                tempArray = new Array<Float>();
                tempArray.add((xPos+3)*3);
                tempArray.add((yPos+3)*3);
                tempArray.add(2.5f*scale/0.4f);
                tempArray.add(0.0f);
                planetArray.add(tempArray);
            }
            else if(objectName.equals("ship")){
                //ship is 1.5 overlap2d units
                tempArray = new Array<Float>();
                tempArray.add((xPos+0.75f)*3);
                tempArray.add((yPos+0.75f)*3);
                tempArray.add(0.0f); //cannot search for type of ship yet
                shipArray.add(tempArray);
            }
            else if(objectName.equals("oob2")){
                OOB_RADIUS = scale / 0.2f;
            }
            else if(objectName.equals("blackHole")){
                tempArray = new Array<Float>();
                tempArray.add((xPos+3)*3);
                tempArray.add((yPos+3)*3);
                tempArray.add(2.5f*scale/0.3f);
                String custom = temp.getString("customVars");
                int holePair = 0;
                float direction = 0;
                for(int a = 0; a<custom.length(); a++){
                    if(custom.substring(a, a+5).equals("type:")){
                        while(custom.charAt(a+5)!=';'){
                            holePair = holePair * 10;
                            holePair += Character.getNumericValue(custom.charAt(a+5));
                            a++;
                        }
                    }
                    if(custom.substring(a, a+10).equals("direction:")){
                        direction = Float.parseFloat(custom.substring(a+10));
                        a = custom.length();
                    }
                }
                tempArray.add((float)Math.cos(Math.toRadians(direction)));
                tempArray.add((float)Math.sin(Math.toRadians(direction)));
                if(bhmap.containsKey(holePair)){
                    blackHoleArray.add(tempArray);
                    blackHoleArray.add(bhmap.get(holePair));
                    bhmap.remove(holePair);
                }
                else{
                    bhmap.put(holePair, tempArray);
                }
            }
        }
        PLANETS = planetArray;
        SHIPS = shipArray;
        BLACK_HOLES =blackHoleArray;

    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {

        // Create Planets
        String pname = "planet";
        for (int ii = 0; ii <PLANETS.size; ii++) {
            PlanetModel obj;
            obj = new PlanetModel(PLANETS.get(ii).get(0), PLANETS.get(ii).get(1), PLANETS.get(ii).get(2), PLANETS.get(ii).get(3));
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
            obj.setName(pname);
            if (obj.getType() == 0f || obj.getType() == 3f) {

                //Blue Planets
                if (ii % 7 == 0) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(blue_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(blue_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(blue_P_3_Texture);
                    }
                }

                //Purple Planets
                if (ii % 7 == 1) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(purple_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(purple_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(purple_P_3_Texture);
                    }
                }

                //Orange Planets
                if (ii % 7 == 2) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(orange_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(orange_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(orange_P_3_Texture);
                    }
                }

                //Sky Planets
                if (ii % 7 == 3) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(sky_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(sky_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(sky_P_3_Texture);
                    }
                }

                //Green Planets
                if (ii % 7 == 4) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(green_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(green_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(green_P_3_Texture);
                    }
                }

                //Pink Planets
                if (ii % 7 == 5) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(pink_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(pink_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(pink_P_3_Texture);
                    }
                }

                //Red Planets
                if (ii % 7 == 6) {
                    double rand=Math.random();
                    if (rand <= .33) {
                        obj.setTexture(red_P_1_Texture);
                    }
                    if ((rand <= .66) && (rand > .33)) {
                        obj.setTexture(red_P_2_Texture);
                    }
                    if (rand > .66) {
                        obj.setTexture(red_P_3_Texture);
                    }
                }
            }
            //Command Planets
            if (obj.getType() == 1f) {
                obj.setTexture(command_P_Texture);
                commandPlanets.add(obj);
            }
            //Poison Planets
           if (obj.getType() == 2f) {
//                // Constant rows and columns of the sprite sheet
//                int FRAME_COLS = 8, FRAME_ROWS = 1;
//
//
//                    // Load the sprite sheet as a Texture
//                    walkSheet = new Texture(Gdx.files.internal("space/animations/sunAnim.png"));
//
//                    // Use the split utility method to create a 2D array of TextureRegions. This is
//                    // possible because this sprite sheet contains frames of equal size and they are
//                    // all aligned.
//                    TextureRegion[][] tmp = TextureRegion.split(walkSheet,
//                            walkSheet.getWidth() / FRAME_COLS,
//                            walkSheet.getHeight() / FRAME_ROWS);
//
//                    // Place the regions into a 1D array in the correct order, starting from the top
//                    // left, going across first. The Animation constructor requires a 1D array.
//                    TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
//                    int index = 0;
//                    for (int i = 0; i < FRAME_ROWS; i++) {
//                        for (int j = 0; j < FRAME_COLS; j++) {
//                            walkFrames[index++] = tmp[i][j];
//                        }
//                    }
//
//                    // Initialize the Animation with the frame interval and array of frames
//                    walkAnimation = new Animation<TextureRegion>(.2f, walkFrames);
//
//                    // Instantiate a SpriteBatch for drawing and reset the elapsed animation
//                    // time to 0
//                    stateTime = 0f;


               double rand = Math.random();
               if (rand <= .25) {
                   obj.setTexture(poison_P_1_Texture);
               }
               if ((rand <= .5) && (rand > .25)) {
                   obj.setTexture(poison_P_2_Texture);
               }
               if ((rand <= .75) && (rand > .5)) {
                   obj.setTexture(poison_P_3_Texture);
               }
               if (rand > .75) {
                   obj.setTexture(poison_P_4_Texture);
//                        obj.setFilmStrip(poison_P_Strip);

               }
           }
            //Neutral Planets
            if (obj.getType() == 3f) {
                obj.setTexture(neutral_P_Texture);
            }

            addObject(obj);
            planets.add(obj);

        }

        // Create black holes
        for(int ii = 0; ii < BLACK_HOLES.size; ii = ii+2) {
            BlackHoleModel b1 = new BlackHoleModel(BLACK_HOLES.get(ii).get(0), BLACK_HOLES.get(ii).get(1),
                    BLACK_HOLES.get(ii).get(2), new Vector2(BLACK_HOLES.get(ii).get(3), BLACK_HOLES.get(ii).get(4)));
            BlackHoleModel b2 = new BlackHoleModel(BLACK_HOLES.get(ii+1).get(0), BLACK_HOLES.get(ii+1).get(1),
                    BLACK_HOLES.get(ii+1).get(2), new Vector2(BLACK_HOLES.get(ii+1).get(3), BLACK_HOLES.get(ii+1).get(4)));
            b1.setTexture(blackHoleTexture);
            b1.scalePicScale(new Vector2(0.25f, 0.25f));
            b2.scalePicScale(new Vector2(0.25f, 0.25f));
            b2.setTexture(blackHoleTexture);
            b1.setPair(b2);
            b2.setPair(b1);
            b1.setBodyType(BodyDef.BodyType.StaticBody);
            b2.setBodyType(BodyDef.BodyType.StaticBody);
            b1.setDrawScale(scale);
            b2.setDrawScale(scale);

            addObject(b1);
            addObject(b2);
        }

        // Create Ships

        for (int ii = 0; ii <SHIPS.size; ii++) {
            ShipModel sh = new ShipModel(SHIPS.get(ii).get(0), SHIPS.get(ii).get(1), SHIPS.get(ii).get(2));
            sh.setBodyType(BodyDef.BodyType.DynamicBody);
            sh.setDensity(BASIC_DENSITY);
            sh.setFriction(BASIC_FRICTION);
            sh.setRestitution(BASIC_RESTITUTION);
            sh.setDrawScale(scale);
            sh.scalePicScale(new Vector2(.3f, .3f));
            sh.setTexture(ship_texture);
            sh.setName("ship");
            sh.setGravityScale(0.0f);
            ships.add(sh);
            addObject(sh);
        }

        // Create Oob
        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(16, 12, OOB_RADIUS, 50);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setTexture(avatarTexture);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS, .4f*OOB_RADIUS));
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

    public void loopCommandPlanets(){
        for(PlanetModel c: commandPlanets){
            if (c.canSpawn()){
                Vector2 spawnDir = c.getPosition().cpy().sub(complexAvatar.getPosition()).nor();
                //SPAWN SHIP
                ShipModel sh;
                if (Math.random()<0.5){
                    sh = new ShipModel(c.getX()+c.getRadius()*spawnDir.x, c.getY()+c.getRadius()*spawnDir.y, 1);
//                    sh.setAggroRange(20f);
                }
                else if (Math.random() < 0.75){
                    sh = new ShipModel(c.getX()+c.getRadius()*spawnDir.x, c.getY()+c.getRadius()*spawnDir.y, 0);
                }
                else {
                    // TODO: CHANGE THIS TO TYPE 2 after sorting it out
                    sh = new ShipModel(c.getX()+c.getRadius()*spawnDir.x, c.getY()+c.getRadius()*spawnDir.y, 0);
                }
                sh.setBodyType(BodyDef.BodyType.DynamicBody);
                sh.setDensity(BASIC_DENSITY);
                sh.setFriction(BASIC_FRICTION);
                sh.setRestitution(BASIC_RESTITUTION);
                sh.setDrawScale(scale);
                sh.scalePicScale(new Vector2(.2f, .2f));
                sh.setTexture(ship_texture);
                sh.setName("ship");
                sh.setGravityScale(0.0f);
                addObject(sh);
                aiController.addShip(sh, c);
            }
        }
    }

    public void loopConvertPlanet() {
        for (int i = 0; i < planets.size; i++) {
            if (planets.get(i).getConvert() > 180) {
                planets.get(i).setType(1);
                commandPlanets.add(planets.get(i));
            }
        }
    }

    //Shoot bullet from ship
    public void shootBullet(){
        if(aiController.bulletData.size != 0) {
            for (int i = 0; i < aiController.bulletData.size / 4; i++) {
                BulletModel bullet = new BulletModel(aiController.bulletData.get(i), aiController.bulletData.get(i+1));
                bullet.setBodyType(BodyDef.BodyType.DynamicBody);
                bullet.setDensity(0.0f);
                bullet.setFriction(0.0f);
                bullet.setRestitution(0.0f);
                bullet.setDrawScale(scale);
                bullet.scalePicScale(new Vector2(0.3f, 0.3f));
                bullet.setGravityScale(0);
                bullet.setVX(aiController.bulletData.get(i + 2));
                bullet.setVY(aiController.bulletData.get(i + 3));
                bullet.setAngle((float)(Math.atan2(bullet.getVY(), bullet.getVX()) - Math.PI/2));
                bullet.setTexture(bullet_texture);
                bullet.setName("bullet");
                addObject(bullet);
            }
            aiController.bulletData.clear();
        }
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
            vecToCenter.set(canvas.getWidth()/80f - complexAvatar.getX(), canvas.getHeight()/80f - complexAvatar.getY());
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

    //Siphon closest planet
    public void siphonPlanet(){
        oldAvatarRad = complexAvatar.getRadius();
        float oldOobMass = complexAvatar.getMass();
        float oldPlanMass = currentPlanet.getMass();
        currentPlanet.setRadius((float)Math.sqrt((oldPlanMass - SIPHON)/Math.PI));
        currentPlanet.scalePicScale(new Vector2(currentPlanet.getRadius() / rad, currentPlanet.getRadius() / rad));
        if(currentPlanet.getType() == 0) {
            complexAvatar.setRadius((float) Math.sqrt((oldOobMass + SIPHON / 3) / Math.PI));
            complexAvatar.scalePicScale(new Vector2(complexAvatar.getRadius() / oldAvatarRad, complexAvatar.getRadius() / oldAvatarRad));
        }
    }

    //Make Oob move around the planet
    public void moveAroundPlanet(){
        if (moveDirection == 1) {
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).nor().scl(10 + complexAvatar.getMass()));
        } else if (moveDirection == -1) {
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).nor().scl(-10 - complexAvatar.getMass()));
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
        if(InputController.getInstance().didPause()){
            pauseState = 3;
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
        if(InputController.getInstance().didPause()){
            pauseState = 3;
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
        if(InputController.getInstance().debugJustPressed())
            setDebug(!isDebug());
        if (pauseState == 0) {
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
            if (commandPlanets.size == 0) {
                //We win the game!
                pauseState = 1;
            }
//            if (complexAvatar.getX() < 0 || complexAvatar.getX() > width || complexAvatar.getY() < 0 || complexAvatar.getY() > height)
//                //Off the screen
//                pauseState = 2;
            if (complexAvatar.getRadius() <= OOB_DEATH_RADIUS) {
                //Game Over
                pauseState = 2;
            }
            if (currentPlanet != null) {
                smallestRad = new Vector2(complexAvatar.getX() - currentPlanet.getX(), complexAvatar.getY() - currentPlanet.getY());
                complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17-complexAvatar.getMass()));
                    //determines mouse or keyboard controls
                if (!currentPlanet.isDying() && currentPlanet.getRadius() < MIN_RADIUS) {
                    currentPlanet.setDying(true);
                    currentPlanet.setTexture(dying_P_Texture);
                }


                groundPlayerControls();

                //forced jump
                if (currentPlanet.getRadius() < DEATH_RADIUS) {
                    if (currentPlanet.getType() == 1f) {
                        commandPlanets.removeValue(currentPlanet, true);
                    }
                    currentPlanet.markRemoved(true);
                    planets.removeValue(currentPlanet, true);
                    jump = true;
                    //TODO Play planet explosion sound
                }

                if (jump) {
                    if (currentPlanet.isDying()) {
                        if (currentPlanet.getType() == 1f) {
                            commandPlanets.removeValue(currentPlanet, true);
                        }
                        currentPlanet.markRemoved(true);
                        planets.removeValue(currentPlanet, true);
                        //TODO Play planet explosion sound
                    }
                    jump();
                } else {
                    rad = currentPlanet.getRadius();
                    if (rad > DEATH_RADIUS && (currentPlanet.getType() == 0f || currentPlanet.getType() == 1f)) {
                        siphonPlanet();
                    } else if (currentPlanet.getType() == 2f) {
                        changeMass(POISON);
                    }
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
                if(blackHoleWarp) {
                    Vector2 newPos = outHole.getPosition().cpy().add(outHole.getOutVelocity().cpy().nor().scl(0.2f + outHole.getRadius() + complexAvatar.getRadius()));
                    complexAvatar.setX(newPos.x);
                    complexAvatar.setY(newPos.y);
                    complexAvatar.setLinearVelocity(outHole.getOutVelocity());
                    blackHoleWarp = false;
                }
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


            loopCommandPlanets();

            loopConvertPlanet();

            aiController.update(dt);

            shootBullet();
        }
        else{
            // Go to Pause Menu
            if (pauseState == 3) {
                pauseState = 0;
                listener.exitScreen(this, 3);
                return;
            }
            // Go to Main Menu
            else {
                pauseState = 0;
                listener.exitScreen(this, 0);
                return;
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
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        try {
            Obstacle bd1 = (Obstacle)body1.getUserData();
            Obstacle bd2 = (Obstacle)body2.getUserData();

            if(bd1.getName().equals("bullet"))
                removeBullet(bd1);

            if(bd2.getName().equals("bullet"))
                removeBullet(bd2);

            if(bd1.getName().equals("Oob")) {
                if (bd2.getName().equals("bullet")) {
                    oldAvatarRad = complexAvatar.getRadius();
                    changeMass(BULLET_DAMAGE);
                    if(!mute)
                        SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
                }
                else if (bd2.getName().equals("ship")) {
                    bd2.markRemoved(true);
                    aiController.removeShip((ShipModel)bd2);
                }
                else if(bd2.getName().equals("expulsion")) {
                    bd2.markRemoved(true);
                    changeMass(((WheelObstacle)bd2).getMass());
                }
                else if(bd2.getName().equals("black hole")) {
                    outHole = ((BlackHoleModel)bd2).getPair();
                    blackHoleWarp = true;
                }

            }
            else if(bd2.getName().equals("Oob")) {
                if (bd1.getName().equals("bullet")) {
                    oldAvatarRad = complexAvatar.getRadius();
                    changeMass(BULLET_DAMAGE);
                    if(!mute)
                        SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
                }
                else if (bd1.getName().equals("ship")) {
                    bd1.markRemoved(true);
                    aiController.removeShip((ShipModel)bd1);
                }
                else if(bd1.getName().equals("expulsion")) {
                    bd1.markRemoved(true);
                    changeMass(((WheelObstacle)bd1).getMass());
                }
                else if(bd1.getName().equals("black hole")) {
                    outHole = ((BlackHoleModel)bd1).getPair();
                    blackHoleWarp = true;
                }
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

            //float camera = -carPosition;

            // Draw background unscaled.
            canvas.begin();

            //canvas.drawWrapped(backgroundTextureMAIN,BG_MAIN_PARALLAX,0f);
            // canvas.drawWrapped(backgroundTextureLARGESTAR,BG_RED_PARALLAX,0f);
            // canvas.drawWrapped(backgroundTextureMEDIUMSTAR,BG_WHITE_PARALLAX,0f);

        int LG_S_X;
        int LG_S_Y;

        if ((backgroundLG.getRegionWidth()-canvas.getWidth())>0) {
            LG_S_X = 0;
        }
        else {
            LG_S_X = (backgroundLG.getRegionWidth()-canvas.getWidth())/2;
        }

        if ((backgroundLG.getRegionHeight()-canvas.getHeight())>0) {
            LG_S_Y = 0;
        }
        else {
            LG_S_Y = (backgroundLG.getRegionHeight()-canvas.getHeight())/2;
        }

        canvas.draw(backgroundMAIN, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundSM, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundMED, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundWHITESTAR, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X, LG_S_Y,backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());




            canvas.end();

            for (Obstacle obj : objects) {

//                if (obj.getName().equals("planet") && ((PlanetModel) obj).getType() == 2 ) {
//                    stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
//                    System.out.println("GOT HEREEEEEEEEEEEEEEEEEEE");
//                    System.out.println(obj.getX()+" "+ obj.getY());
//
//
//                    // Get current frame of animation for the current stateTime
//
//                    TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
//                    canvas.begin();
//                    //spriteBatch.draw(currentFrame, obj.getX(), obj.getY()); // Draw current frame at (50, 50)
//                   // spriteBatch.draw(currentFrame,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),picScale.x, picScale.y);
//                    //canvas.draw(currentFrame, obj.getX(), obj.getY());
//                    //canvas.draw(currentFrame,Color.WHITE,currentFrame.getRegionWidth(),currentFrame.getRegionHeight(),obj.getX(),obj.getY(),currentFrame.getRegionWidth()*.4f, currentFrame.getRegionHeight()*.4f);
//                    canvas.end();
//
//                }
                //else {
                    canvas.begin();
                    obj.draw(canvas);
                    canvas.end();
                    //System.out.println("ELSE STATEMENT");
              //  }


            }

            if (isDebug()) {
                canvas.beginDebug();
                for (Obstacle obj : objects) {
                    obj.drawDebug(canvas);
                }
                canvas.endDebug();
            }
            canvas.begin();
//            for (int i = 0; i < planets.size; i++) {
//                canvas.drawText(Integer.toString((int) (Math.pow(planets.get(i).getRadius(), 2) * Math.PI)), massFont, planets.get(i).getX()*40f, planets.get(i).getY() * 40f);
//            }
//            canvas.drawText(Integer.toString((int) (Math.pow(complexAvatar.getRadius(), 2) * Math.PI)), massFont, complexAvatar.getX() * 40f, complexAvatar.getY() * 40f);
            canvas.end();

        }

}