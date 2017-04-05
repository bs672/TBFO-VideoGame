package edu.cornell.gdiac.controller;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.*;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class PlayMode extends WorldController implements ContactListener {
    /** The texture file for the character avatar (no animation) */
    private static final String OOB_FILE  = "space/oob_2.png";


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

    /** The texture file for the planets */
    private static final String COMMAND_P = "space/planets/command.png";

    /** The texture file for the planets */
    private static final String NEUTRAL_P = "space/planets/neutral.png";

    /** The texture file for the planets */
    private static final String DYING_P = "space/planets/dying.png";

//
//    /** The texture file for the planets */
//    private static final String BLUE_P = "space/blue_planet_480.png";
//    /** The texture file for the planets */
//    private static final String GREEN_P = "space/green_planet_480.png";
//    /** The texture file for the planets */
//    private static final String ORANGE_P = "space/orange_planet_480.png";
//    /** The texture file for the planets */
//    private static final String ORNG_RED_P = "space/orange_red_planet_480.png";
//    /** The texture file for the planets */
//    private static final String RED_P = "space/red_planet_480.png";
//    /** The texture file for the planets */
//    private static final String POISON_P = "space/planet.png";



    /** Texture file for background image */
    private static final String BACKG_FILE_MAIN = "space/background/background_main.png";
    /** Texture file for background image */
    private static final String BACKG_FILE_LARGE_STARS = "space/background/large-stars.png";
    /** Texture file for background image */
    private static final String BACKG_FILE_MEDIUM_STARS = "space/background/medium-stars.png";
    /** Texture file for background image */
    private static final String BACKG_FILE_SMALL_STARS = "space/background/small-stars.png";



    /** Texture file for ship */
    private static final String SHIP_TEXTURE = "space/ship.png";
    /** The texture file for the bullets */
    private static final String BULLET_TEXTURE = "space/bullet.png";


    /** Parallax values */
    private static final float BG_MAIN_PARALLAX    = 0;  	// Parallax = 0 means we're infinitely far away
    private static final float BG_WHITE_PARALLAX = 0.4f;
    private static final float BG_RED_PARALLAX   = 0.9f;
    private static final float PLANET_PARALLAX      = 1.0f;	// Put focus of scene at parallax 1
    private static final float FOREGROUND_PARALLAX   = 2.0f;	// Parallax > 1 is a foreground object

    /** The sound file for a jump */
    private static final String JUMP_FILE = "space/jump.mp3";
    /** The sound file for a bullet fire */
    private static final String PEW_FILE = "space/pew.mp3";
    /** The sound file for a bullet collision */
    private static final String POP_FILE = "space/plop.mp3";
    /** The initial position of Oob */
    private static Vector2 OOB_POS = new Vector2(16f, 20f);
    /** Oob's initial radius */
    private static float OOB_RADIUS = 1f;

    private static final float SIPHON = 0.02f;

    private static final float POISON = 0.02f;

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

    /** Planet texture */
    private TextureRegion command_P_Texture;

    /** Planet texture */
    private TextureRegion neutral_P_Texture;

    /** Planet texture */
    private TextureRegion dying_P_Texture;




//    /** Planet texture */
//    private TextureRegion blue_P_Texture;
//    /** Planet texture */
//    private TextureRegion green_P_Texture;
//    /** Planet texture */
//    private TextureRegion orange_P_Texture;
//    /** Planet texture */
//    private TextureRegion orange_red_P_Texture;
//    /** Planet texture */
//    private TextureRegion red_P_Texture;
//
//    /** Planet texture */
//    private TextureRegion poison_P_Texture;



    /** Texture asset for background image */
    private TextureRegion backgroundTextureMAIN;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureLARGESTAR;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureMEDIUMSTAR;
    /** Texture asset for background image */
    private TextureRegion backgroundTextureSMALLSTAR;



    /** Texture asset for ship */
    private TextureRegion ship_texture;
    /** Texture asset for bullet */
    private TextureRegion bullet_texture;

    //variables
    Vector2 mvmtDir;
    Vector2 smallestRad;
    float rad;
    float oldAvatarRad;
    //variables for playerControls()
    boolean jump = false;
    float moveDirection = 0f;
    private boolean mute = true;

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



//        manager.load(GREEN_P, Texture.class);
//        assets.add(GREEN_P);
//        manager.load(ORANGE_P, Texture.class);
//        assets.add(ORANGE_P);
//        manager.load(ORNG_RED_P, Texture.class);
//        assets.add(ORNG_RED_P);
//        manager.load(RED_P, Texture.class);
//        assets.add(RED_P);
//
//        manager.load(POISON_P, Texture.class);
//        assets.add(POISON_P);


        manager.load(BACKG_FILE_MAIN, Texture.class);
        assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_LARGE_STARS, Texture.class);
        assets.add(BACKG_FILE_LARGE_STARS);
        manager.load(BACKG_FILE_MEDIUM_STARS, Texture.class);
        assets.add(BACKG_FILE_MEDIUM_STARS);
        manager.load(BACKG_FILE_SMALL_STARS, Texture.class);
        assets.add(BACKG_FILE_SMALL_STARS);

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

        blue_P_1_Texture = createTexture(manager,BLUE_P_1,false);
        blue_P_2_Texture = createTexture(manager,BLUE_P_2,false);
        blue_P_3_Texture = createTexture(manager,BLUE_P_3,false);
       // blue_P_4_Texture = createTexture(manager,BLUE_P_4,false);

        purple_P_1_Texture = createTexture(manager,PURPLE_P_1,false);
        purple_P_2_Texture = createTexture(manager,PURPLE_P_2,false);
        purple_P_3_Texture = createTexture(manager,PURPLE_P_3,false);
      // purple_P_4_Texture = createTexture(manager,PURPLE_P_4,false);

        orange_P_1_Texture = createTexture(manager,ORANGE_P_1,false);
        orange_P_2_Texture = createTexture(manager, ORANGE_P_2,false);
        orange_P_3_Texture = createTexture(manager, ORANGE_P_3,false);
      //  orange_P_4_Texture = createTexture(manager, ORANGE_P_4,false);

        sky_P_1_Texture = createTexture(manager,SKY_P_1,false);
        sky_P_2_Texture = createTexture(manager,SKY_P_2,false);
        sky_P_3_Texture = createTexture(manager,SKY_P_3,false);
      //  sky_P_4_Texture = createTexture(manager,SKY_P_4,false);

        green_P_1_Texture = createTexture(manager, GREEN_P_1,false);
        green_P_2_Texture = createTexture(manager, GREEN_P_2,false);
        green_P_3_Texture = createTexture(manager, GREEN_P_3,false);
      //  green_P_4_Texture = createTexture(manager,GREEN_P_4,false);

        pink_P_1_Texture = createTexture(manager, PINK_P_1,false);
        pink_P_2_Texture = createTexture(manager, PINK_P_2,false);
        pink_P_3_Texture = createTexture(manager, PINK_P_3,false);
        //  pink_P_4_Texture = createTexture(manager,PINK_P_4,false);

        red_P_1_Texture = createTexture(manager, RED_P_1,false);
        red_P_2_Texture = createTexture(manager, RED_P_2,false);
        red_P_3_Texture = createTexture(manager, RED_P_3,false);
        //  red_P_4_Texture = createTexture(manager,RED_P_4,false);

        poison_P_1_Texture = createTexture(manager,POISON_P_1,false);
        poison_P_2_Texture = createTexture(manager,POISON_P_2,false);
        poison_P_3_Texture = createTexture(manager,POISON_P_3,false);
        poison_P_4_Texture = createTexture(manager,POISON_P_4,false);

        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);

        dying_P_Texture = createTexture(manager,DYING_P,false);

        command_P_Texture = createTexture(manager,COMMAND_P,false);







//
//        blue_P_Texture = createTexture(manager,BLUE_P,false);
//        green_P_Texture = createTexture(manager,GREEN_P,false);
//        orange_P_Texture = createTexture(manager,ORANGE_P,false);
//        orange_red_P_Texture = createTexture(manager,ORNG_RED_P,false);
//        red_P_Texture = createTexture(manager,RED_P,false);

//        poison_P_Texture = createTexture(manager,POISON_P,false);


        backgroundTextureMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundTextureLARGESTAR = createTexture(manager,BACKG_FILE_LARGE_STARS,false);
        backgroundTextureMEDIUMSTAR = createTexture(manager,BACKG_FILE_MEDIUM_STARS,false);
        backgroundTextureSMALLSTAR = createTexture(manager,BACKG_FILE_SMALL_STARS,false);

        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

//        Texture L_Tex = new Texture(BACKG_FILE_LARGE_STARS);
//        L_Tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//        TextureRegion L_TexReg = new TextureRegion(L_Tex);
//        L_TexReg.setRegion(0,0,L_Tex.getWidth()*7,L_Tex.getHeight()*7);
//        backgroundTextureLARGESTAR =L_TexReg;
//
//
//        Texture M_Tex = new Texture(BACKG_FILE_MEDIUM_STARS);
//        M_Tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//        TextureRegion M_TexReg = new TextureRegion(M_Tex);
//        M_TexReg.setRegion(0,0,M_Tex.getWidth()*7,M_Tex.getHeight()*7);
//        backgroundTextureMEDIUMSTAR =M_TexReg;
//
//        Texture S_Tex = new Texture(BACKG_FILE_SMALL_STARS);
//        S_Tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//        TextureRegion S_TexReg = new TextureRegion(S_Tex);
//        S_TexReg.setRegion(0,0,S_Tex.getWidth()*7,S_Tex.getHeight()*7);
//        backgroundTextureSMALLSTAR =S_TexReg;

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
    private static final float  BULLET_DAMAGE = 0.00f;
    /** The volume for sound effects */
    private static final float EFFECT_VOLUME = 0.8f;

    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.


    private static final float[][] PLANETS = {
            {0.0f, 0.5f, 2.8f, 3f},
            {5.0f, 12.5f, 1.2f, 2f},
            {27.0f, 4.5f, 2.7f, 0f},
            {25.0f, 12.5f, 1.6f, 1f},
            {18.0f, -4.0f, 1.9f, 0f},

            {17.0f, 22.5f, 2.8f, 0f},
            {-5.0f, -12.5f, 2.2f, 0f},
            {-17.0f, 17.5f, 2.7f, 2f},
            {40.0f, 17.5f, 2f, 1f},
            {-18.0f, 4.0f, 1.9f, 0f},

            {-2.0f, 10.5f, 1.8f, 0f},
            {-5.0f, -22.5f, 2.2f, 2f},
            {44.0f, 1.5f, 1.7f, 0f},
            {-16.0f, -17.5f, 1.2f, 3f},
            {-28.0f, 5.8f, 1.7f, 3f},
    };

    private static final float[][] SHIPS = {
            {5.0f, 14f, 0},
            {-5.0f, 17f, 0},
            {15.0f, 2f, 0}
    };

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
    public PlayMode() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(true);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
        sensorFixtures = new ObjectSet<Fixture>();
        planets = new Array<PlanetModel>();
        commandPlanets = new Array<PlanetModel>();
        ships = new Array<ShipModel>();
        massFont = new BitmapFont();
        massFont.getData().setScale(2);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(blue_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(purple_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(orange_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(sky_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(green_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(green_P_3_Texture);
//                    }
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
//                    if ((rand <= .75) && (rand > .5)) {
//                        obj.setTexture(green_P_3_Texture);
//                    }
                    if (rand > .66) {
                        obj.setTexture(red_P_3_Texture);
                    }
                }


//                if (ii % 5 == 1) {
//                    obj.setTexture(green_P_Texture);
//                }
//                if (ii % 5 == 2) {
//                    obj.setTexture(orange_P_Texture);
//                }
//                if (ii % 5 == 3) {
//                    obj.setTexture(orange_red_P_Texture);
//                }
//                if (ii % 5 == 4) {
//                    obj.setTexture(red_P_Texture);
//                }
            }
            //Command Planets
            if (obj.getType() == 1f) {
                obj.setTexture(command_P_Texture);
                commandPlanets.add(obj);
            }
            if (obj.getType() == 2f) {
                //Poison Planets
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
                    }
            }
            //Neutral Planets
            if (obj.getType() == 3f) {
                obj.setTexture(neutral_P_Texture);
            }

            addObject(obj);
            planets.add(obj);

        }

        // Create Ships
        for (int ii = 0; ii <SHIPS.length; ii++) {
            ShipModel sh = new ShipModel(SHIPS[ii][0], SHIPS[ii][1], SHIPS[ii][2]);
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
        avatar = new OobModel(OOB_POS.x, OOB_POS.y, OOB_RADIUS);
        avatar.setDrawScale(scale);
        avatar.setTexture(avatarTexture);
        avatar.setBodyType(BodyDef.BodyType.DynamicBody);
        avatar.setSensor(true);
        avatar.setName("Oob");
        currentPlanet = planets.get(0); //CHANGE THIS FOR EACH LEVEL
        avatar.scalePicScale(new Vector2(.3f*OOB_RADIUS, .3f*OOB_RADIUS));
        addObject(avatar);

        complexAvatar = new ComplexOobModel(16, 12, OOB_RADIUS, 50);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setTexture(avatarTexture);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.3f*OOB_RADIUS, .3f*OOB_RADIUS));
        addObject(complexAvatar);

        aiController = new AIController(ships, planets, commandPlanets, avatar, scale);
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

    public void loopCommandPlanets(){
        for(PlanetModel c: commandPlanets){
            if (c.canSpawn()){
                //SPAWN SHIP
                ShipModel sh;
                if (Math.random()<0.5){
                    sh = new ShipModel(c.getX()+c.getRadius(), c.getY()+c.getRadius(), 1);
//                    sh.setAggroRange(20f);
                }
                else if (Math.random() < 0.75){
                    sh = new ShipModel(c.getX()+c.getRadius(), c.getY()+c.getRadius(), 0);
                }
                else {
                    sh = new ShipModel(c.getX()+c.getRadius(), c.getY()+c.getRadius(), 2);
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

    //Finds closest planet
    public void findPlanet(){
        if (currentPlanet==null) {
            for(Obstacle o : objects) {
                if(o.equals(avatar)) {
                    o.setX(o.getX() - avatar.getVX() / 60);
                    o.setY(o.getY() - avatar.getVY() / 60);
                }
                else {
                    o.setX(o.getX() - avatar.getVX() / 30);
                    o.setY(o.getY() - avatar.getVY() / 30);
                }
            }
            Vector2 smallestRad = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
            int closestPlanet = 0;
            Vector2 radDir;
            for (int i = 0; i < planets.size; i++) {
                radDir = new Vector2(avatar.getX() - planets.get(i).getX(), avatar.getY() - planets.get(i).getY());
                if (radDir.len() < smallestRad.len() && !lastPlanet.equals(planets.get(i))) {
                    smallestRad = radDir.cpy();
                    closestPlanet = i;
                }
            }
            if (smallestRad.len() < planets.get(closestPlanet).getRadius() + avatar.getRadius() + EPSILON) {
                currentPlanet = planets.get(closestPlanet);
                if(!mute)
                    SoundController.getInstance().play(PEW_FILE, PEW_FILE, false, EFFECT_VOLUME);
                avatar.applyForceZero();
            }
        }
    }

    //Oob loses mass
    public void loseMass(float massLoss){
        float oldOobMass = avatar.getMass();
        if(avatar.getRadius()>=OOB_DEATH_RADIUS) {
            avatar.setRadius((float) Math.sqrt((oldOobMass - massLoss) / Math.PI));
            avatar.scalePicScale(new Vector2(avatar.getRadius() / oldAvatarRad, avatar.getRadius() / oldAvatarRad));
        }
    }

    //Siphon closest planet
    public void siphonPlanet(){
        float oldOobMass = avatar.getMass();
        float oldPlanMass = currentPlanet.getMass();
        currentPlanet.setRadius((float)Math.sqrt((oldPlanMass - SIPHON)/Math.PI));
        avatar.setRadius((float)Math.sqrt((oldOobMass + SIPHON/3)/Math.PI));
        currentPlanet.scalePicScale(new Vector2(currentPlanet.getRadius() / rad, currentPlanet.getRadius() / rad));
        avatar.scalePicScale(new Vector2(avatar.getRadius() / oldAvatarRad,avatar.getRadius() / oldAvatarRad));
    }

    //Make Oob move around the planet
    public void moveAroundPlanet(){
        if (moveDirection == 1) {
            avatar.setX(currentPlanet.getX() + smallestRad.x + mvmtDir.x);
            avatar.setY(currentPlanet.getY() + smallestRad.y + mvmtDir.y);
        } else if (moveDirection == -1) {
            avatar.setX(currentPlanet.getX() + smallestRad.x - mvmtDir.x);
            avatar.setY(currentPlanet.getY() + smallestRad.y - mvmtDir.y);
        }
        avatar.setAngle((float)(Math.atan2(smallestRad.y, smallestRad.x) - Math.PI / 2));
        smallestRad = new Vector2(avatar.getX() - currentPlanet.getX(), avatar.getY() - currentPlanet.getY());
        smallestRad.scl((avatar.getRadius() + currentPlanet.getRadius())/smallestRad.len());
        avatar.setX(currentPlanet.getX() + smallestRad.x);
        avatar.setY(currentPlanet.getY() + smallestRad.y);
        avatar.setMovement(new Vector2(0, 0));
    }

    //Make Oob jump
    public void jump(){
        if(!mute)
            SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
        avatar.setMovement(smallestRad.scl((float)(Math.sqrt(avatar.getMass()))/2));
        lastPlanet = currentPlanet;
        currentPlanet = null;
        avatar.applyForce();
    }

    //Determines whether the player is using mouse or keyboard and sets associated variables
    public void playerControls(){
        if(InputController.getInstance().didPause()){
            pauseState = 3;
        }
        if (control==1){
            Vector2 mouse = InputController.getInstance().getCursor();
            mouse = mouse.sub(currentPlanet.getPosition());
            float angle = mouse.angle();
            Vector2 oob = avatar.getPosition();
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
        complexAvatar.applyForce(new Vector2(0,-10));
        if (pauseState == 0) {
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
            if (avatar.getX() < 0 || avatar.getX() > width || avatar.getY() < 0 || avatar.getY() > height)
                //Off the screen
                pauseState = 2;
            if (avatar.getRadius() <= OOB_DEATH_RADIUS) {
                //Game Over
                pauseState = 2;
            }
            if (currentPlanet != null) {
                vecToCenter.set(canvas.getWidth()/80f - currentPlanet.getX(), canvas.getHeight()/80f - currentPlanet.getY());
                for (Obstacle o : objects) {
                    if (justLoaded) {
                        o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy()));
                        justLoaded = false;
                    } else
                        o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
                }

                smallestRad = new Vector2(avatar.getX() - currentPlanet.getX(), avatar.getY() - currentPlanet.getY());

                //determines mouse or keyboard controls
                if (!currentPlanet.isDying()&&currentPlanet.getRadius() < MIN_RADIUS) {
                    currentPlanet.setDying(true);
                    currentPlanet.setTexture(dying_P_Texture);
                }
                playerControls();

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

                avatar.applyForceZero();
                smallestRad.scl((currentPlanet.getRadius() + avatar.getRadius()) / smallestRad.len());
                mvmtDir = new Vector2(smallestRad.y, -smallestRad.x).scl(1 / (20 * avatar.getRadius()));
                if (jump) {
                    if(currentPlanet.isDying()){
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
                    oldAvatarRad = avatar.getRadius();
                    if (rad > DEATH_RADIUS && (currentPlanet.getType() == 0f || currentPlanet.getType() == 1f)) {
                        siphonPlanet();
                    } else if (currentPlanet.getType() == 2f) {
                        loseMass(POISON);
                    }
                    moveAroundPlanet();
                }
            }
            avatar.applyForce();

            // If we use sound, we must remember this.
            SoundController.getInstance().update();

            findPlanet();

            loopCommandPlanets();

            aiController.update(dt);

            shootBullet();
        }
        else{

        }
    }

    /**
     * Remove a new bullet from the world.
     *
     * @param  bullet   the bullet to remove
     */
    public void removeBullet(Obstacle bullet) {
        bullet.markRemoved(true);
        if(!mute)
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

            if(bd1.getName().equals("bullet"))
                removeBullet(bd1);

            if(bd2.getName().equals("bullet"))
                removeBullet(bd2);

            if (bd1.getName().equals("bullet") && bd2.getName().equals("Oob")) {
                oldAvatarRad = avatar.getRadius();
                loseMass(BULLET_DAMAGE);
            }
            else if (bd2.getName().equals("bullet") && bd1.getName().equals("Oob")) {
                oldAvatarRad = avatar.getRadius();
                loseMass(BULLET_DAMAGE);
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

            // See if we have landed on the ground.
            if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
                    (avatar.getSensorName().equals(fd1) && avatar != bd2)) {
                avatar.setGrounded(true);
                sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
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
        if (pauseState == 0) {
            canvas.clear();

            //float camera = -carPosition;

            // Draw background unscaled.
            canvas.begin();

            //canvas.drawWrapped(backgroundTextureMAIN,BG_MAIN_PARALLAX,0f);
            // canvas.drawWrapped(backgroundTextureLARGESTAR,BG_RED_PARALLAX,0f);
            // canvas.drawWrapped(backgroundTextureMEDIUMSTAR,BG_WHITE_PARALLAX,0f);
            int blueImageWidth = 0;
            Vector2 bottomLeft = new Vector2();
            if (canvas.getWidth() < 1280) {
                blueImageWidth = 1280;
                bottomLeft.x = (1280 - canvas.getWidth())/2;
            } else {
                blueImageWidth = canvas.getWidth();
                bottomLeft.x = 1280 + (1280 - canvas.getWidth())/2;
            }
            canvas.draw(backgroundTextureMAIN, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.draw(backgroundTextureLARGESTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.draw(backgroundTextureMEDIUMSTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.draw(backgroundTextureSMALLSTAR, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());


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
            for (int i = 0; i < planets.size; i++) {
                canvas.drawText(Integer.toString((int) (Math.pow(planets.get(i).getRadius(), 2) * Math.PI)), massFont, planets.get(i).getX()*40f, planets.get(i).getY() * 40f);
            }
            canvas.drawText(Integer.toString((int) (Math.pow(avatar.getRadius(), 2) * Math.PI)), massFont, avatar.getX() * 40f, avatar.getY() * 40f);
            canvas.end();


//        // Draw foreground last.
//        canvas.begin();
//        canvas.draw(foregroundTexture, FORE_COLOR,  0, 0, canvas.getWidth(), canvas.getHeight());
//        selector.draw(canvas);
//        canvas.end();
        }
    else{
            //This is what we draw when the game is "paused"
            canvas.clear();
            displayFont.setColor(Color.GREEN);
            canvas.begin();
            canvas.draw(backgroundTextureMAIN, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
            switch(pauseState){
                case 1:
                    canvas.drawTextCentered("VICTORY! Press p to restart", massFont, 0.0f);
                    break;
                case 2:
                    canvas.drawTextCentered("DEFEAT! Press p to restart", massFont, 0.0f);
                    break;
                case 3:
                    canvas.drawTextCentered("Game is paused. Press p to resume", massFont, 0.0f);
                    break;
            }
            canvas.end();
            if(InputController.getInstance().didPause()&&inPause == 1){
                //Done with pause
                inPause = 0;
                if(pauseState ==1 || pauseState == 2){
                    pauseState = 0;
                    reset();
                }
                else{
                    pauseState = 0;
                }
            }
            else{
                //Stay in pause
                inPause = 1;
            }
        }
    }
}





