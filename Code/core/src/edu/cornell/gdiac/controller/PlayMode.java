package edu.cornell.gdiac.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
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
import com.badlogic.gdx.utils.ObjectMap;


/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class PlayMode extends WorldController implements ContactListener {


    protected ScreenListener listener;

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    /** The texture file for the character avatar (no animation) */


    //protected static final String OOB_FILE  = "space/animations/oobH.png";

//    protected static final String OOB_NORMAL_FILE =   "space/animations/oobH.png";
//    //"space/planets/start.png";
//    protected static final String OOB_GROWING_FILE = "space/animations/oobH.png";
//    protected static final String OOB_COMMAND_FILE = "space/planets/command.png";
//    protected static final String OOB_FLYING_FILE = "space/animations/oobHappy.png";
//    protected static final String OOB_HURTING_FILE = "space/planets/blackHole_old.png";
//    protected static final String OOB_DYING_FILE = "space/planets/dying.png";

    protected static final String OOB_NORMAL_FILE =   "space/animations/OobBlink.png";
    //"space/planets/start.png";
    protected static final String OOB_GROWING_FILE = "space/animations/blackHoleAnim.png";
    protected static final String OOB_COMMAND_FILE = "space/animations/explosionAnim.png";
    protected static final String OOB_FLYING_FILE = "space/animations/OobBlink.png";
    protected static final String OOB_TELEPORTING_FILE = "space/animations/oobHappy.png";
    protected static final String OOB_HURTING_FILE = "space/animations/OobSad.png";
    protected static final String OOB_DYING_FILE = "space/animations/oobDying.png";


    /** The texture file for the planets */
    protected static final String BLUE_P_1 = "space/planets/blue.png";
    protected static final String BLUE_P_2 = "space/planets/blue2.png";
    protected static final String BLUE_P_3 = "space/planets/blue3.png";
    //private static final String BLUE_P_4 = "space/planets/blue.png";

    /** The texture file for the planets */
    protected static final String PURPLE_P_1 = "space/planets/purple.png";
    protected static final String PURPLE_P_2 = "space/planets/purple2.png";
    protected static final String PURPLE_P_3 = "space/planets/purple3.png";
    //private static final String PURPLE_P_4 = "space/planets/purple.png";

    /** The texture file for the planets */
    protected static final String ORANGE_P_1 = "space/planets/orange.png";
    protected static final String ORANGE_P_2 = "space/planets/orange2.png";
    protected static final String ORANGE_P_3 = "space/planets/orange3.png";
    //private static final String ORANGE_P_4 = "space/planets/orange.png";

    /** The texture file for the planets */
    protected static final String SKY_P_1 = "space/planets/sky.png";
    protected static final String SKY_P_2 = "space/planets/sky2.png";
    protected static final String SKY_P_3 = "space/planets/sky3.png";
    //private static final String SKY_P_4 = "space/planets/sky.png";

    /** The texture file for the planets */
    protected static final String GREEN_P_1 = "space/planets/green.png";
    protected static final String GREEN_P_2 = "space/planets/green2.png";
    protected static final String GREEN_P_3 = "space/planets/green3.png";
    //private static final String GREEN_P_4 = "space/planets/green.png";

    /** The texture file for the planets */
    protected static final String PINK_P_1 = "space/planets/pink.png";
    protected static final String PINK_P_2 = "space/planets/pink2.png";
    protected static final String PINK_P_3 = "space/planets/pink3.png";
    //private static final String PINK_P_4 = "space/planets/pink.png";

    /** The texture file for the planets */
    protected static final String RED_P_1 = "space/planets/red.png";
    protected static final String RED_P_2 = "space/planets/red2.png";
    protected static final String RED_P_3 = "space/planets/red3.png";
    //private static final String RED_P_4 = "space/planets/red.png";

    /** The texture file for the planet animations */
    protected static final String SUN_P = "space/animations/sunAnim.png";

    protected static final String BLACK_HOLE = "space/animations/blackHoleAnim.png";

    protected static final String WARNING = "space/animations/planetWarning.png";

    protected static final String EXPLOSION = "space/animations/explosionAnim.png";


    /** The texture file for the planets */
    protected static final String COMMAND_P = "space/planets/command.png";

    /** The texture file for the planets */
    protected static final String NEUTRAL_P = "space/planets/neutral.png";

    /** The texture file for the planets */
    protected static final String DYING_P = "space/planets/dying.png";

    /** The texture file for the planets */
    protected static final String SETTINGS_TEXTURE = "space/menus/settings_planet.png";

    /** The texture file for the planets */
    protected static final String PLAY_TEXTURE = "space/menus/play_planet.png";

    /** The texture file for the planets */
    protected static final String LEVELS_TEXTURE = "space/menus/levels_planet.png";

    /** The texture file for the planets */
    protected static final String SETTINGS_HOVER_TEXTURE = "space/menus/settings_planet_hover.png";

    /** The texture file for the planets */
    protected static final String PLAY_HOVER_TEXTURE = "space/menus/play_planet_hover.png";

    /** The texture file for the planets */
    protected static final String LEVELS_HOVER_TEXTURE = "space/menus/levels_planet_hover.png";

    protected static final String PAUSETITLE = "space/menus/pause.png";
    protected static final String LEVELSTITLE = "space/menus/levels.png";
    protected static final String SETTINGSTITLE = "space/menus/settings.png";
    protected static final String RESUME_TEXTURE = "space/menus/resume_planet.png";
    protected static final String RESUME_HOVER_TEXTURE = "space/menus/resume_planet_hover.png";
    protected static final String MAIN_MENU_TEXTURE = "space/menus/exit_to_menu_planet.png";
    protected static final String MAIN_MENU_HOVER_TEXTURE = "space/menus/exit_to_menu_planet_hover.png";

    protected static final String TITLE = "space/menus/title.png";



    /** Texture file for background image */
    protected static final String BACKG_FILE_MAIN = "space/background/blue-background.png";
    protected static final String BACKG_FILE_WHITE_STAR = "space/background/white-stars.png";
    protected static final String BACKG_FILE_LG_STAR = "space/background/large-stars.png";
    protected static final String BACKG_FILE_MED_STAR = "space/background/medium-stars.png";
    protected static final String BACKG_FILE_SM_STAR = "space/background/small-stars.png";



    /** Texture file for ship */
    protected static final String SHIP_TEXTURE = "space/ships/ship.png";
    /** The texture file for the bullets */
    protected static final String BULLET_TEXTURE = "space/ships/bullet.png";
    /** The texture file for mass expulsion */
    protected static final String EXPULSION_TEXTURE = "space/Oob/expulsion.png";


    /** Parallax values */
    protected static final float BG_MAIN_PARALLAX    = 0;  	// Parallax = 0 means we're infinitely far away
    protected static final float BG_WHITE_PARALLAX = 0.4f;
    protected static final float BG_RED_PARALLAX   = 0.9f;
    protected static final float PLANET_PARALLAX      = 1.0f;	// Put focus of scene at parallax 1
    protected static final float FOREGROUND_PARALLAX   = 2.0f;	// Parallax > 1 is a foreground object

    /** The sound file for a jump */
    protected static final String JUMP_FILE = "space/audio/jump.mp3";
    /** The sound file for a bullet fire */
    protected static final String PEW_FILE = "space/audio/pew.mp3";
    /** The sound file for a bullet collision */
    protected static final String POP_FILE = "space/audio/plop.mp3";
    /** The initial position of Oob */
    protected static Vector2 OOB_POS = new Vector2(16f, 12f);
    /** Oob's initial radius */
    protected  float OOB_RADIUS = 1f; //0.2 scale in overlap2d is standard

    protected static final float SIPHON = 0.02f;

    protected static final float POISON = -0.02f;

    protected static final float MIN_RADIUS = 1f;

    protected static final float DEATH_RADIUS = MIN_RADIUS*2/3;

    protected static final float OOB_DEATH_RADIUS = 0.56f;

    protected static final float OOB_WARNING_RADIUS = .85f;

    protected static final float EPSILON = 0.1f;

    protected static final int THRESHOLD = 4;

    protected static final int ADJUST_COOLDOWN = 60;



    // A variable for tracking elapsed time for the animation

    private float stateTime=0f;

    protected int inPause = 0;

    //control = 0 is keyboard, control = 1 is mouse
    protected int control = 1;

    /** Animation texture */
   // protected Animation<TextureRegion> oobAnimation; // Must declare frame type (TextureRegion)
   // protected Texture oobSheet;

    protected TextureRegion blackHoleTexture;

    /** Planet texture */
    protected TextureRegion blue_P_1_Texture;
    protected TextureRegion blue_P_2_Texture;
    protected TextureRegion blue_P_3_Texture;
   // private TextureRegion blue_P_4_Texture;

    /** Planet texture */
    protected TextureRegion purple_P_1_Texture;
    protected TextureRegion purple_P_2_Texture;
    protected TextureRegion purple_P_3_Texture;
   // private TextureRegion purple_P_4_Texture;

    /** Planet texture */
    protected TextureRegion orange_P_1_Texture;
    protected TextureRegion orange_P_2_Texture;
    protected TextureRegion orange_P_3_Texture;
    //private TextureRegion orange_P_4_Texture;

    /** Planet texture */
    protected TextureRegion sky_P_1_Texture;
    protected TextureRegion sky_P_2_Texture;
    protected TextureRegion sky_P_3_Texture;
   // private TextureRegion sky_P_4_Texture;

    /** Planet texture */
    protected TextureRegion green_P_1_Texture;
    protected TextureRegion green_P_2_Texture;
    protected TextureRegion green_P_3_Texture;
    //private TextureRegion green_P_4_Texture;

    /** Planet texture */
    protected TextureRegion pink_P_1_Texture;
    protected TextureRegion pink_P_2_Texture;
    protected TextureRegion pink_P_3_Texture;
    //private TextureRegion pink_P_4_Texture;

    /** Planet texture */
    protected TextureRegion red_P_1_Texture;
    protected TextureRegion red_P_2_Texture;
    protected TextureRegion red_P_3_Texture;
    //private TextureRegion red_P_4_Texture;

    /** Animation texture */
    protected Animation<TextureRegion> sunAnimation; // Must declare frame type (TextureRegion)
    protected Texture sunSheet;

    protected Animation<TextureRegion> BH_Animation; // Must declare frame type (TextureRegion)
    protected Texture BH_Sheet;


    //private Texture Oob_Sheet;

    protected Texture Oob_Normal_Sheet;
    protected Texture Oob_Growing_Sheet;
    protected Texture Oob_Command_Sheet;
    protected Texture Oob_Flying_Sheet;
    protected Texture Oob_Teleporting_Sheet;
    protected Texture Oob_Hurting_Sheet;
    protected Texture Oob_Dying_Sheet;

    private Texture EXP_Sheet;

    private Texture WARN_Sheet;





    /** Planet texture */
    protected TextureRegion command_P_Texture;

    /** Planet texture */
    protected TextureRegion neutral_P_Texture;

    /** Planet texture */
    protected TextureRegion dying_P_Texture;

    /** Expulsion texture */
    protected TextureRegion expulsion_Texture;

    /** Settings texture */
    protected TextureRegion settings_Texture;

    /** Levels texture */
    protected TextureRegion levels_Texture;

    /** Play texture */
    protected TextureRegion play_Texture;

    /** Settings texture */
    protected TextureRegion settings_Hover_Texture;

    /** Levels texture */
    protected TextureRegion levels_Hover_Texture;

    /** Play texture */
    protected TextureRegion play_Hover_Texture;
    protected TextureRegion main_Menu_Texture;
    protected TextureRegion main_Menu_Hover_Texture;
    protected TextureRegion resume_Texture;
    protected TextureRegion resume_Hover_Texture;
    protected TextureRegion pauseTitleTexture;
    protected TextureRegion titleTexture;

    /** Background texture */
    protected TextureRegion backgroundMAIN;
    protected TextureRegion backgroundWHITESTAR;
    protected TextureRegion backgroundLG;
    protected TextureRegion backgroundMED;
    protected TextureRegion backgroundSM;

    /** Texture asset for ship */
    protected TextureRegion ship_texture;
    /** Texture asset for bullet */
    protected TextureRegion bullet_texture;

    //variables
    protected Vector2 smallestRad;
    protected float rad;
    protected float oldAvatarRad;
    //variables for player controls
    protected boolean jump = false;
    protected float moveDirection = 0f;
    protected boolean mute = true;
    protected Vector2 launchVec;
    protected BlackHoleModel outHole;
    protected boolean blackHoleWarp;

    public void setMute(boolean bool) {mute = bool;}

    /** Track asset loading from all instances and subclasses */
    protected AssetState platformAssetState = AssetState.EMPTY;

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
//        manager.load(OOB_FILE, Texture.class);
//        assets.add(OOB_FILE);

        manager.load(OOB_NORMAL_FILE, Texture.class);
        assets.add(OOB_NORMAL_FILE);
        manager.load(OOB_GROWING_FILE, Texture.class);
        assets.add(OOB_GROWING_FILE);
        manager.load(OOB_COMMAND_FILE, Texture.class);
        assets.add(OOB_COMMAND_FILE);
        manager.load(OOB_FLYING_FILE, Texture.class);
        assets.add(OOB_FLYING_FILE);
        manager.load(OOB_TELEPORTING_FILE, Texture.class);
        assets.add(OOB_TELEPORTING_FILE);
        manager.load(OOB_HURTING_FILE, Texture.class);
        assets.add(OOB_HURTING_FILE);
        manager.load(OOB_DYING_FILE, Texture.class);
        assets.add(OOB_DYING_FILE);

        manager.load(SETTINGS_TEXTURE, Texture.class);
        assets.add(SETTINGS_TEXTURE);

        manager.load(SETTINGS_HOVER_TEXTURE, Texture.class);
        assets.add(SETTINGS_HOVER_TEXTURE);

        manager.load(PLAY_TEXTURE, Texture.class);
        assets.add(PLAY_TEXTURE);

        manager.load(PLAY_HOVER_TEXTURE, Texture.class);
        assets.add(PLAY_HOVER_TEXTURE);

        manager.load(LEVELS_TEXTURE, Texture.class);
        assets.add(LEVELS_TEXTURE);

        manager.load(LEVELS_HOVER_TEXTURE, Texture.class);
        assets.add(LEVELS_HOVER_TEXTURE);

        manager.load(TITLE, Texture.class);
        assets.add(TITLE);

        manager.load(PAUSETITLE, Texture.class);
        assets.add(PAUSETITLE);

        manager.load(LEVELSTITLE, Texture.class);
        assets.add(LEVELSTITLE);

        manager.load(SETTINGSTITLE, Texture.class);
        assets.add(SETTINGSTITLE);

        manager.load(RESUME_TEXTURE, Texture.class);
        assets.add(RESUME_TEXTURE);

        manager.load(RESUME_HOVER_TEXTURE, Texture.class);
        assets.add(RESUME_HOVER_TEXTURE);

        manager.load(MAIN_MENU_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_TEXTURE);

        manager.load(MAIN_MENU_HOVER_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_HOVER_TEXTURE);

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

        manager.load(SUN_P, Texture.class);
        assets.add(SUN_P);

        manager.load(BLACK_HOLE, Texture.class);
        assets.add(BLACK_HOLE);

        manager.load(WARNING, Texture.class);
        assets.add(WARNING);

        manager.load(EXPLOSION, Texture.class);
        assets.add(EXPLOSION);

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



        //oobSheet = new Texture(Gdx.files.internal(OOB_FILE));
        Oob_Normal_Sheet = new Texture(Gdx.files.internal(OOB_NORMAL_FILE));
        Oob_Growing_Sheet = new Texture(Gdx.files.internal(OOB_GROWING_FILE));
        Oob_Command_Sheet = new Texture(Gdx.files.internal(OOB_COMMAND_FILE));
        Oob_Flying_Sheet = new Texture(Gdx.files.internal(OOB_FLYING_FILE));
        Oob_Teleporting_Sheet = new Texture(Gdx.files.internal(OOB_TELEPORTING_FILE));
        Oob_Hurting_Sheet = new Texture(Gdx.files.internal(OOB_HURTING_FILE));
        Oob_Dying_Sheet = new Texture(Gdx.files.internal(OOB_DYING_FILE));

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

        sunSheet = new Texture(Gdx.files.internal(SUN_P));

        BH_Sheet = new Texture(Gdx.files.internal(BLACK_HOLE));

        //Oob_Sheet = new Texture(Gdx.files.internal(OOB_FILE));

        WARN_Sheet = new Texture(Gdx.files.internal(WARNING));

        EXP_Sheet = new Texture(Gdx.files.internal(EXPLOSION));

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
    protected static final float  DEFAULT_GRAVITY = -14.7f;
    /** The density for most physics objects */
    protected static final float  BASIC_DENSITY = 0.0f;
    /** Friction of most platforms */
    protected static final float  BASIC_FRICTION = 0.4f;
    /** The restitution for all physics objects */
    protected static final float  BASIC_RESTITUTION = 0.1f;
    /** The damage of the bullet */
    protected static final float  BULLET_DAMAGE = -0.04f;
    /** The volume for sound effects */
    protected static final float EFFECT_VOLUME = 0.8f;

    protected Array<Array<Float>> PLANETS = new Array<Array<Float>>();
    protected Array<Array<Float>> SHIPS = new Array<Array<Float>>();
    protected Array<Array<Float>> BLACK_HOLES = new Array<Array<Float>>();

    // Physics objects for the game
    /** Reference to the character avatar */
    protected OobModel avatar;
    protected ComplexOobModel complexAvatar;
    /** Reference to current planet Oob's on */
    protected PlanetModel currentPlanet;
    /** last planet on */
    protected PlanetModel lastPlanet;
    /** List of all live planets */
    protected Array<PlanetModel> planets;
    //List of command planets
    protected Array<PlanetModel> commandPlanets;
    //List of dying planets
    Array<PlanetModel> planet_explosion;
    /** list of ships */
    protected Array<ShipModel> ships;
    /** vector from Oob to center of the screen */
    protected Vector2 vecToCenter = new Vector2();
    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture> sensorFixtures;
    /** the font for the mass text on each object */
    protected BitmapFont massFont;

    protected int returnToPlanetTimer;

    protected float width;
    protected float height;
    /** if we've just loaded */
    protected boolean justLoaded = true;
    /** AIController */
    protected AIController aiController;

    protected int adjustCooldown;

    protected boolean[] lastHoverPlanet;
    protected boolean play;
    protected int jumpTime;


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
        planet_explosion = new Array<PlanetModel>();
        ships = new Array<ShipModel>();
        massFont = new BitmapFont();
        massFont.getData().setScale(2);
        launchVec = new Vector2();
        returnToPlanetTimer = 0;
        adjustCooldown = ADJUST_COOLDOWN;
        FileHandle json = Gdx.files.internal("overlap2d/Testing/scenes/MainScene.dt");
        String jsonString = json.readString();
        jsonParse(jsonString);
        play = true;
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
        planet_explosion.clear();
        ships.clear();
        world.dispose();

        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
        for(Obstacle o: objects){
            if(!o.equals(complexAvatar) &&  !o.equals(planets.get(0))){
                o.setPosition(o.getPosition().cpy().add(new Vector2 (canvas.getWidth()/80f - 16f, canvas.getHeight()/80f - 9f)));
            }
        }
        play = true;
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
        float scale;
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
                OOB_RADIUS = scale / 0.35f;
            }
            else if(objectName.equals("blackHole")){
                tempArray = new Array<Float>();
                tempArray.add((xPos+2.5f)*3);
                tempArray.add((yPos+2.5f)*3);
                tempArray.add(2.5f*scale/0.4f);
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
               obj.scalePicScale(new Vector2(.5f * obj.getRadius(), .5f * obj.getRadius()));
                // Constant rows and columns of the sprite sheet
                int FRAME_COLS = 8, FRAME_ROWS = 1;

                    // Use the split utility method to create a 2D array of TextureRegions. This is
                    // possible because this sprite sheet contains frames of equal size and they are
                    // all aligned.
                    TextureRegion[][] tmp = TextureRegion.split(sunSheet,
                            sunSheet.getWidth() / FRAME_COLS,
                            sunSheet.getHeight() / FRAME_ROWS);

                    // Place the regions into a 1D array in the correct order, starting from the top
                    // left, going across first. The Animation constructor requires a 1D array.
                    TextureRegion[] sunFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
                    int index = 0;
                    for (int i = 0; i < FRAME_ROWS; i++) {
                        for (int j = 0; j < FRAME_COLS; j++) {
                            sunFrames[index++] = tmp[i][j];
                        }
                    }

                    // Initialize the Animation with the frame interval and array of frames
                    sunAnimation = new Animation<TextureRegion>(.15f, sunFrames);

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


            b1.scalePicScale(new Vector2(.2f * b1.getRadius(), .2f * b1.getRadius()));
            b2.scalePicScale(new Vector2(.2f * b2.getRadius(), .2f * b2.getRadius()));
            b1.setPair(b2);
            b2.setPair(b1);
            b1.setBodyType(BodyDef.BodyType.StaticBody);
            b2.setBodyType(BodyDef.BodyType.StaticBody);
            b1.setDrawScale(scale);
            b2.setDrawScale(scale);


                    // Constant rows and columns of the sprite sheet
                    int FRAME_COLS = 12, FRAME_ROWS = 1;

                    // Use the split utility method to create a 2D array of TextureRegions. This is
                    // possible because this sprite sheet contains frames of equal size and they are
                    // all aligned.
                    TextureRegion[][] tmp = TextureRegion.split(BH_Sheet,
                            BH_Sheet.getWidth() / FRAME_COLS,
                            BH_Sheet.getHeight() / FRAME_ROWS);

                    // Place the regions into a 1D array in the correct order, starting from the top
                    // left, going across first. The Animation constructor requires a 1D array.
                    TextureRegion[] BH_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
                    int index = 0;
                    for (int i = 0; i < FRAME_ROWS; i++) {
                        for (int j = 0; j < FRAME_COLS; j++) {
                             BH_Frames[index++] = tmp[i][j];
                        }
                    }

                    // Initialize the Animation with the frame interval and array of frames
                    BH_Animation = new Animation<TextureRegion>(.15f, BH_Frames);

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
        complexAvatar = new ComplexOobModel(currentPlanet.getX()+canvas.getWidth()/80f - 0.8f, currentPlanet.getY() + currentPlanet.getRadius()*2+canvas.getHeight()/80f, OOB_RADIUS, 50);
        complexAvatar.setDrawScale(scale);
        //complexAvatar.setTexture(avatarTexture);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS, .4f*OOB_RADIUS));
        addObject(complexAvatar);
        loadAnim();




        aiController = new AIController(ships, planets, commandPlanets, complexAvatar, scale);
    }

    public void loadAnim() {
        complexAvatar.set_Normal_sheet(Oob_Normal_Sheet);
        complexAvatar.createNormaltex();

        complexAvatar.set_Growing_sheet(Oob_Growing_Sheet);
        complexAvatar.createGrowingtex();

        complexAvatar.set_Command_sheet(Oob_Command_Sheet);
        complexAvatar.createCommandtex();

        complexAvatar.set_Flying_sheet(Oob_Flying_Sheet);
        complexAvatar.createFlyingtex();

        complexAvatar.set_Teleporting_sheet(Oob_Teleporting_Sheet);
        complexAvatar.createTeleportingtex();

        complexAvatar.set_Hurting_sheet(Oob_Hurting_Sheet);
        complexAvatar.createHurtingtex();

        complexAvatar.set_Dying_sheet(Oob_Dying_Sheet);
        complexAvatar.createDyingtex();
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
            for (int i = 0; i < aiController.bulletData.size; i += 4) {
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
            if (radDir.len() < smallestRad.len() && ((!lastPlanet.equals(planets.get(i)) && returnToPlanetTimer < 30) || returnToPlanetTimer >= 30)) {
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
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).nor().scl(12 + complexAvatar.getMass()));
        } else if (moveDirection == -1) {
            complexAvatar.addToForceVec(new Vector2(smallestRad.y, -smallestRad.x).nor().scl(-12 - complexAvatar.getMass()));
        }
    }

    //Make Oob jump
    public void jump(){
        if(!mute)
            SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
        Vector2 mouseVec = InputController.getInstance().getCursor(canvas).cpy().sub(complexAvatar.getPosition());
        complexAvatar.setLinearVelocity(mouseVec.cpy().nor().scl(12));
        lastPlanet = currentPlanet;
        currentPlanet = null;
    }

    //Determines whether the player is using mouse or keyboard and sets associated variables when Oob is on a planet
    public void groundPlayerControls(){
        if (InputController.getInstance().didReset()) {

            reset();
        }
        if(InputController.getInstance().didPause()){
            listener.exitScreen(this, 3);
        }
        if (control==1){
            Vector2 mouse = InputController.getInstance().getCursor(canvas);
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
            listener.exitScreen(this, 3);
        }
        if (control==1){
            launchVec = complexAvatar.getPosition().cpy().sub(InputController.getInstance().getCursor(canvas));
            jump = InputController.getInstance().getMouseJump();
        }
        else{
            jump = InputController.getInstance().getJump();
            moveDirection = InputController.getInstance().getHorizontal();
        }
    }

    public boolean screenSwitch() {
        return false;
    }

    public void gravity() {
//        // Gravity
//        Vector2 tempVec1 = new Vector2(0, 0);
//        for (int i = 0; i < planets.size; i++) {
//            //if (planets.get(i) != lastPlanet) {
//            tempVec1.set(complexAvatar.getPosition().cpy().sub(planets.get(i).getPosition()));
//            float r = Math.abs(tempVec1.len() - planets.get(i).getRadius());
//            float k = complexAvatar.getMass()*planets.get(i).getMass();
//            complexAvatar.addToForceVec(new Vector2(-tempVec1.x * 1f*k/(r*r), -tempVec1.y * 1f*k/(r*r)));
//            //}
//        }
    }

    public void hover() {

    }

    public boolean clickScreenSwitch() {
        return false;
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
        if (InputController.getInstance().debugJustPressed()) {
            setDebug(!isDebug());
        }
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
            if (play) listener.exitScreen(this, 0);
        }
        if (complexAvatar.getRadius() <= OOB_DEATH_RADIUS) {
            //Game Over
            listener.exitScreen(this, 0);
        }
        if (currentPlanet != null) {
            smallestRad = new Vector2(complexAvatar.getX() - currentPlanet.getX(), complexAvatar.getY() - currentPlanet.getY());
            if(smallestRad.len() > 3* complexAvatar.getRadius() / 4) {
                if (smallestRad.len() > complexAvatar.getRadius() + currentPlanet.getRadius())
                    complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17 - 2 * complexAvatar.getMass()));
                complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17 - complexAvatar.getMass()));
            }
            //determines mouse or keyboard controls
            if (!currentPlanet.isDying() && currentPlanet.getRadius() < MIN_RADIUS) {
                currentPlanet.setDying(true);
                //currentPlanet.setTexture(dying_P_Texture);
                currentPlanet.set_WARN_sheet(WARN_Sheet);
                currentPlanet.createWARNtex();
                planet_explosion.add(currentPlanet);
            }

            //determines Oob's face on planet
            if (currentPlanet.getType() == 0f) {
                complexAvatar.setGrowing(true);
            }
            else if (currentPlanet.getType() == 1f) {
                complexAvatar.setCommand(true);
            }
            else if (currentPlanet.getType() == 2f) {
                complexAvatar.setHurting(true);
            }
            else if (currentPlanet.getType() == 3f) {
                complexAvatar.setNormal(true);
            }



            if (screenSwitch()) {return;}
            groundPlayerControls();
            if (!play) {
                hover();
            }

            //forced jump
            if (currentPlanet.getRadius() < DEATH_RADIUS) {
                currentPlanet.setExploding(true);
                currentPlanet.set_sheet(EXP_Sheet);
                currentPlanet.createEXPtex();
                jump = true;
                //TODO Play planet explosion sound
            }

            if (jump) {
                if (!play) {
                    if (clickScreenSwitch()) {
                        return;
                    }
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
        else if(currentPlanet == null) { // we’re floating in space
            complexAvatar.setFlying(true);
            jumpTime++;
            if ((jumpTime > 300) & !play) {
                reset();
            }
            if (!play) {
                gravity();
            }
            if(blackHoleWarp) {
                Vector2 newPos = outHole.getPosition().cpy().add(outHole.getOutVelocity().cpy().nor().scl(0.2f + outHole.getRadius() + complexAvatar.getRadius()));
                complexAvatar.setX(newPos.x);
                complexAvatar.setY(newPos.y);
                complexAvatar.setLinearVelocity(outHole.getOutVelocity());
                blackHoleWarp = false;
            }
            airPlayerControls();
            if(jump && complexAvatar.getRadius()>OOB_DEATH_RADIUS + 0.1 && adjustCooldown == 0) {
                float expRad = complexAvatar.getRadius() / 2;
                Vector2 massLoc = complexAvatar.getPosition().cpy().add(launchVec.cpy().nor().scl(complexAvatar.getRadius() + expRad + 1f));
                WheelObstacle expulsion = new WheelObstacle(massLoc.x, massLoc.y, expRad);
                expulsion.setGravityScale(0);
                expulsion.setName("expulsion");
                expulsion.setDrawScale(scale);
                expulsion.setTexture(expulsion_Texture);
                expulsion.scalePicScale(new Vector2(expRad, expRad));
                addObject(expulsion);
                expulsion.setLinearVelocity(launchVec.cpy().nor().scl(30));
                changeMass(-expulsion.getMass()/2);
                Vector2 velocityChange = launchVec.cpy().nor().scl(-1.5f*(complexAvatar.getLinearVelocity().len() + expulsion.getLinearVelocity().len()) / complexAvatar.getMass());
                complexAvatar.setLinearVelocity(complexAvatar.getLinearVelocity().set(velocityChange.scl(complexAvatar.getRadius()/2f)));
                adjustCooldown = 60;
            }
            if(complexAvatar.getCenter().getLinearVelocity().len() < 4)
                complexAvatar.setLinearVelocity(complexAvatar.getCenter().getLinearVelocity().cpy().nor().scl(4));
            findPlanet();
        }

        if (complexAvatar.getRadius() <= OOB_WARNING_RADIUS) {
            complexAvatar.setDying(true);
        }


        if (planet_explosion.size > 0) {
            if (planet_explosion.get(0).isDying()) {
                if ((planet_explosion.get(0).get_WARN_ST()) >= (planet_explosion.get(0).get_WARN_anim().getAnimationDuration())) {
                    planet_explosion.get(0).setDying(false);
                    planet_explosion.get(0).setExploding(true);
                    planet_explosion.get(0).set_sheet(EXP_Sheet);
                    planet_explosion.get(0).createEXPtex();
                    //TODO Play planet explosion sound
                }
            }
            if (planet_explosion.get(0).get_EXP_ST() > -1) {
                if ((planet_explosion.get(0).get_EXP_ST()) >= (planet_explosion.get(0).get_EXP_anim().getAnimationDuration())) {
                    if (planet_explosion.get(0).getType() == 1f) {
                        commandPlanets.removeValue(planet_explosion.get(0), true);
                    }
                    planet_explosion.get(0).markRemoved(true);
                    planets.removeValue(planet_explosion.get(0), true);
                    //TODO Play planet explosion sound
                    planet_explosion.removeIndex(0);
                }
            }
        }
        complexAvatar.applyForce();
        complexAvatar.resetForceVec();
        // If we use sound, we must remember this.
        SoundController.getInstance().update();
        loopCommandPlanets();
        loopConvertPlanet();
        aiController.update(dt);
        shootBullet();
        if(adjustCooldown > 0){
            adjustCooldown--;
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
                    if (complexAvatar.getRadius() > OOB_WARNING_RADIUS) {
                        complexAvatar.setHurting(true);
                    }
                    complexAvatar.set_Shot_Cooldown(10);
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
                    changeMass(((WheelObstacle)bd2).getMass()/16);
                }
                else if(bd2.getName().equals("black hole")) {
                    outHole = ((BlackHoleModel)bd2).getPair();
                    complexAvatar.setTeleporting(true);
                    blackHoleWarp = true;
                }

            }
            else if(bd2.getName().equals("Oob")) {
                if (bd1.getName().equals("bullet")) {
                    oldAvatarRad = complexAvatar.getRadius();
                    if (complexAvatar.getRadius() > OOB_WARNING_RADIUS) {
                        complexAvatar.setHurting(true);
                    }
                    complexAvatar.set_Shot_Cooldown(10);
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
                    changeMass(((WheelObstacle)bd1).getMass()/16);
                }
                else if(bd1.getName().equals("black hole")) {
                    outHole = ((BlackHoleModel)bd1).getPair();
                    complexAvatar.setTeleporting(true);
                    blackHoleWarp = true;
                }
            }
            if(bd1.getName().equals("expulsion") && bd2.getName().equals("ship")) {
                bd2.markRemoved(true);
                aiController.removeShip((ShipModel)bd2);
            }
            else if(bd2.getName().equals("expulsion") && bd1.getName().equals("ship")) {
                bd1.markRemoved(true);
                aiController.removeShip((ShipModel)bd1);
            }
            if(bd1.getName().equals("expulsion") && bd2.getName().equals("black hole")) {
                bd1.markRemoved(true);
            }
            else if(bd2.getName().equals("expulsion") && bd1.getName().equals("black hole")) {
                bd2.markRemoved(true);
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
            stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time



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




            canvas.end();

            for (Obstacle obj : objects) {



                if (obj.getName().equals("ComplexOob")) {
                    // Get current frame of animation for the current stateTime
                    //if ( ((ComplexOobModel) obj).isNormal()) {
                   // TextureRegion currentFrame =  ((ComplexOobModel) obj).get_Normal_anim().getKeyFrame(stateTime, true);
                    //}

                    TextureRegion currentFrame;

                    if ( ((ComplexOobModel) obj).isNormal()) {
                        currentFrame =  ((ComplexOobModel) obj).get_Normal_anim().getKeyFrame(stateTime, true);
                    }
                    else if ( ((ComplexOobModel) obj).isGrowing() ) {
                        currentFrame =  ((ComplexOobModel) obj).get_Growing_anim().getKeyFrame(stateTime, true);
                    }
                    else if ( ((ComplexOobModel) obj).isCommand() ) {
                        currentFrame =  ((ComplexOobModel) obj).get_Command_anim().getKeyFrame(stateTime, true);
                    }
                    else if ( ((ComplexOobModel) obj).isFlying() ) {
                        currentFrame =  ((ComplexOobModel) obj).get_Flying_anim().getKeyFrame(stateTime, true);
                    }
                    else if ( ((ComplexOobModel) obj).isTeleporting() ) {
                        currentFrame =  ((ComplexOobModel) obj).get_Teleporting_anim().getKeyFrame(stateTime, true);
                    }
                    else  {
                        currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                    }

                    if (((ComplexOobModel) obj).get_Shot_Cooldown() > 0) {
                        currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                        complexAvatar.decCooldown();
                    }
                    if ( ((ComplexOobModel) obj).isDying() ) {
                        currentFrame =  ((ComplexOobModel) obj).get_Dying_anim().getKeyFrame(stateTime, true);
                    }



                    ((ComplexOobModel) obj).setTexture(currentFrame);

                    canvas.begin();
                    obj.draw(canvas);
                    canvas.end();
                }
                if (obj.getName().equals("planet") && ((PlanetModel) obj).getType() == 2 ) {
                    // Get current frame of animation for the current stateTime
                    TextureRegion currentFrame = sunAnimation.getKeyFrame(stateTime, true);
                    canvas.begin();
                    ((PlanetModel) obj).setTexture(currentFrame);
                    obj.draw(canvas);
                    canvas.end();
                }
                if (obj.getName().equals("black hole")) {
                    // Get current frame of animation for the current stateTime
                    TextureRegion currentFrame = BH_Animation.getKeyFrame(stateTime, true);
                    canvas.begin();
                    ((BlackHoleModel) obj).setTexture(currentFrame);
                    obj.draw(canvas);
                    canvas.end();
                }

                if (obj.getName().equals("planet") && ((PlanetModel) obj).isDying() && !((PlanetModel) obj).isExploding()) {
                    // Get current frame of animation for the current stateTime
                    ((PlanetModel) obj).update_WARN_ST();
                    TextureRegion currentFrame = ((PlanetModel) obj).get_WARN_anim().getKeyFrame(((PlanetModel) obj).get_WARN_ST(), false);
                    canvas.begin();
                    ((PlanetModel) obj).setTexture(currentFrame);
                    obj.draw(canvas);
                    canvas.end();
                }

                if (obj.getName().equals("planet") && ((PlanetModel) obj).isExploding()) {
                    // Get current frame of animation for the current stateTime
                    ((PlanetModel) obj).update_EXP_ST();
                    TextureRegion currentFrame = ((PlanetModel) obj).get_EXP_anim().getKeyFrame(((PlanetModel) obj).get_EXP_ST(), false);
                    canvas.begin();
                    ((PlanetModel) obj).setTexture(currentFrame);
                    obj.draw(canvas);
                    canvas.end();
                }



//                else if (obj.getName().equals("ComplexOob")) {
//                    ((ComplexOobModel)obj).draw();
//                }

                else {
                    canvas.begin();
                    obj.draw(canvas);
                    canvas.end();
                }


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