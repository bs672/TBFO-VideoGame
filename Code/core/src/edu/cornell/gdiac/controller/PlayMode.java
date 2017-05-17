package edu.cornell.gdiac.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.*;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.model.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.audio.Music;


/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class PlayMode extends WorldController implements ContactListener {

    protected static final TextureRegion[][] WIN_TEXTURES = new TextureRegion[4][2];
    protected static final TextureRegion[][] LOSE_TEXTURES = new TextureRegion[3][2];
    protected Array<PlanetModel> winPlanets;

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
    protected static final String OOB_NORMAL_FILE =   "space/animations/OobNeutral.png";
    protected static final String OOB_GROWING_FILE = "space/animations/OobGrowing.png";
    protected static final String OOB_COMMAND_FILE = "space/animations/OobGrowing.png";
    protected static final String OOB_FLYING_FILE = "space/animations/OobBlink.png";
    protected static final String OOB_TELEPORTING_FILE = "space/animations/OobSurprised.png";
    protected static final String OOB_HURTING_FILE = "space/animations/OobSad.png";
    protected static final String OOB_DYING_FILE = "space/animations/OobDying.png";
    protected static final String OOB_MAX_FILE = "space/animations/OobFull.png";

    /** The texture file for the planets */
    protected static final String BLUE_P_1 = "space/planets/blue.png";
    protected static final String BLUE_P_2 = "space/planets/blue2.png";
    protected static final String BLUE_P_3 = "space/planets/blue3.png";

    /** The texture file for the planets */
    protected static final String PURPLE_P_1 = "space/planets/purple.png";
    protected static final String PURPLE_P_2 = "space/planets/purple2.png";
    protected static final String PURPLE_P_3 = "space/planets/purple3.png";

    /** The texture file for the planets */
    protected static final String ORANGE_P_1 = "space/planets/orange.png";
    protected static final String ORANGE_P_2 = "space/planets/orange2.png";
    protected static final String ORANGE_P_3 = "space/planets/orange3.png";

    /** The texture file for the planets */
    protected static final String SKY_P_1 = "space/planets/sky.png";
    protected static final String SKY_P_2 = "space/planets/sky2.png";
    protected static final String SKY_P_3 = "space/planets/sky3.png";

    /** The texture file for the planets */
    protected static final String GREEN_P_1 = "space/planets/green.png";
    protected static final String GREEN_P_2 = "space/planets/green2.png";
    protected static final String GREEN_P_3 = "space/planets/green3.png";

    /** The texture file for the planets */
    protected static final String PINK_P_1 = "space/planets/pink.png";
    protected static final String PINK_P_2 = "space/planets/pink2.png";
    protected static final String PINK_P_3 = "space/planets/pink3.png";

    /** The texture file for the planets */
    protected static final String RED_P_1 = "space/planets/red.png";
    protected static final String RED_P_2 = "space/planets/red2.png";
    protected static final String RED_P_3 = "space/planets/red3.png";
    protected static final String ASTEROID = "space/planets/asteroidBelt.png";

    /** The texture file for the planet animations */
    protected static final String SUN_P = "space/animations/sunAnim.png";
    protected static final String BLACK_HOLE = "space/animations/blackHoleAnim.png";
    protected static final String WARNING = "space/animations/planetWarning.png";
    protected static final String EXPLOSION = "space/animations/explosionAnim.png";
    protected static final String SHIP_TEXTURE = "space/animations/standardShip.png";
    protected static final String G_SHIP_TEXTURE = "space/animations/guardShip.png";
    protected static final String MOTHERSHIP_TEXTURE = "space/animations/big_ship_animation.png";
    protected static final String SHIP_EXPLOSION = "space/animations/Ship_exp.png";

    /** The texture file for the planets */
    protected static final String COMMAND_P = "space/planets/command.png";

    /** The texture file for the planets */
    protected static final String NEUTRAL_P = "space/planets/neutral.png";


    /** The texture file for the planets */
    protected static final String MOUSE= "space/planets/mouse.png";

    protected static final String WASD= "space/planets/wasd.png";

    protected static final String SPACEBAR= "space/planets/spacebar.png";

    protected static final String RESET= "space/background/reset.png";

    protected static final String ARROW= "space/background/arrow.png";

    protected static final String PAUSE= "space/background/pause.png";

    protected static final String GROW_P= "space/planets/growPlanet.png";

    protected static final String SHRINK_P= "space/planets/shrinkPlanet.png";

    protected String LEVEL;
    protected int LV_NUMBER;

    /** The texture file for the planets */
    protected static final String QUIT = "space/menus/quit.png";
    protected static final String QUIT_HOVER = "space/menus/quit_hover.png";
    protected static final String RETRY = "space/menus/retry.png";
    protected static final String RETRY_HOVER = "space/menus/retry_hover.png";
    protected static final String REPLAY = "space/menus/replay.png";
    protected static final String REPLAY_HOVER = "space/menus/replay_hover.png";
    protected static final String LOST_TEXT = "space/menus/lost_text.png";
    protected static final String WIN_TEXT = "space/menus/win_text.png";
    protected static final String SETTINGS_TEXTURE = "space/menus/settings_planet.png";
    protected static final String PLAY_TEXTURE = "space/menus/play_planet.png";
    protected static final String LEVELS_TEXTURE = "space/menus/levels_planet.png";
    protected static final String SETTINGS_HOVER_TEXTURE = "space/menus/settings_planet_hover.png";
    protected static final String PLAY_HOVER_TEXTURE = "space/menus/play_planet_hover.png";
    protected static final String LEVELS_HOVER_TEXTURE = "space/menus/levels_planet_hover.png";
    protected static final String CRED = "space/background/soob_studios.png";
    protected static final String TITLE = "space/menus/title.png";
    protected static final String PAUSETITLE = "space/menus/pause.png";
    protected static final String LEVELSTITLE = "space/menus/levels.png";
    protected static final String SETTINGSTITLE = "space/menus/settings.png";
    protected static final String RESUME_TEXTURE = "space/menus/resume_planet.png";
    protected static final String RESUME_HOVER_TEXTURE = "space/menus/resume_planet_hover.png";
    protected static final String BACK_TEXTURE = "space/menus/back_planet.png";
    protected static final String BACK_HOVER_TEXTURE = "space/menus/back_hover_planet.png";
    protected static final String BACK_TEXT_TEXTURE = "space/menus/back.png";
    protected static final String BACK_TEXT_HOVER_TEXTURE = "space/menus/back_hover.png";
    protected static final String MAIN_MENU_TEXTURE = "space/menus/exit_to_menu.png";
    protected static final String MAIN_MENU_HOVER_TEXTURE = "space/menus/exit_to_menu_hover.png";
    protected static final String NEXT_PAGE_TEXTURE = "space/menus/next_page.png";
    protected static final String NEXT_PAGE_HOVER_TEXTURE = "space/menus/next_page_hover.png";
    protected static final String NEXT_PAGE_LOCK_TEXTURE = "space/menus/next_page_locked.png";
    protected static final String PREV_PAGE_TEXTURE = "space/menus/prev_page.png";
    protected static final String PREV_PAGE_HOVER_TEXTURE = "space/menus/prev_page_hover.png";
    protected static final String PREV_PAGE_LOCK_TEXTURE = "space/menus/prev_page_locked.png";
    protected static final String NEXT_LEVEL = "space/menus/next_level.png";
    protected static final String NEXT_LEVEL_HOVER = "space/menus/next_level_hover.png";
    protected static final String LEVEL1_TEXTURE = "space/menus/level1.png";
    protected static final String LEVEL1_LOCK_TEXTURE = "space/menus/level1_locked.png";
    protected static final String LEVEL1_HOVER_TEXTURE = "space/menus/level1_hover.png";
    protected static final String LEVEL2_TEXTURE = "space/menus/level2.png";
    protected static final String LEVEL2_LOCK_TEXTURE = "space/menus/level2_locked.png";
    protected static final String LEVEL2_HOVER_TEXTURE = "space/menus/level2_hover.png";
    protected static final String LEVEL3_TEXTURE = "space/menus/level3.png";
    protected static final String LEVEL3_LOCK_TEXTURE = "space/menus/level3_locked.png";
    protected static final String LEVEL3_HOVER_TEXTURE = "space/menus/level3_hover.png";
    protected static final String LEVEL4_TEXTURE = "space/menus/level4.png";
    protected static final String LEVEL4_LOCK_TEXTURE = "space/menus/level4_locked.png";
    protected static final String LEVEL4_HOVER_TEXTURE = "space/menus/level4_hover.png";
    protected static final String LEVEL5_TEXTURE = "space/menus/level5.png";
    protected static final String LEVEL5_LOCK_TEXTURE = "space/menus/level5_locked.png";
    protected static final String LEVEL5_HOVER_TEXTURE = "space/menus/level5_hover.png";
    protected static final String LEVEL6_TEXTURE = "space/menus/level6.png";
    protected static final String LEVEL6_LOCK_TEXTURE = "space/menus/level6_locked.png";
    protected static final String LEVEL6_HOVER_TEXTURE = "space/menus/level6_hover.png";
    protected static final String LEVEL7_TEXTURE = "space/menus/level7.png";
    protected static final String LEVEL7_LOCK_TEXTURE = "space/menus/level7_locked.png";
    protected static final String LEVEL7_HOVER_TEXTURE = "space/menus/level7_hover.png";
    protected static final String LEVEL8_TEXTURE = "space/menus/level8.png";
    protected static final String LEVEL8_LOCK_TEXTURE = "space/menus/level8_locked.png";
    protected static final String LEVEL8_HOVER_TEXTURE = "space/menus/level8_hover.png";
    protected static final String LEVEL9_TEXTURE = "space/menus/level9.png";
    protected static final String LEVEL9_LOCK_TEXTURE = "space/menus/level9_locked.png";
    protected static final String LEVEL9_HOVER_TEXTURE = "space/menus/level9_hover.png";
    protected static final String LEVEL10_TEXTURE = "space/menus/level10.png";
    protected static final String LEVEL10_LOCK_TEXTURE = "space/menus/level10_locked.png";
    protected static final String LEVEL10_HOVER_TEXTURE = "space/menus/level10_hover.png";
    protected static final String LEVEL11_TEXTURE = "space/menus/level11.png";
    protected static final String LEVEL11_LOCK_TEXTURE = "space/menus/level11_locked.png";
    protected static final String LEVEL11_HOVER_TEXTURE = "space/menus/level11_hover.png";
    protected static final String LEVEL12_TEXTURE = "space/menus/level12.png";
    protected static final String LEVEL12_LOCK_TEXTURE = "space/menus/level12_locked.png";
    protected static final String LEVEL12_HOVER_TEXTURE = "space/menus/level12_hover.png";
    protected static final String LEVEL13_TEXTURE = "space/menus/level13.png";
    protected static final String LEVEL13_LOCK_TEXTURE = "space/menus/level13_locked.png";
    protected static final String LEVEL13_HOVER_TEXTURE = "space/menus/level13_hover.png";
    protected static final String LEVEL14_TEXTURE = "space/menus/level14.png";
    protected static final String LEVEL14_LOCK_TEXTURE = "space/menus/level14_locked.png";
    protected static final String LEVEL14_HOVER_TEXTURE = "space/menus/level14_hover.png";
    protected static final String LEVEL15_TEXTURE = "space/menus/level15.png";
    protected static final String LEVEL15_LOCK_TEXTURE = "space/menus/level15_locked.png";
    protected static final String LEVEL15_HOVER_TEXTURE = "space/menus/level15_hover.png";
    protected static final String LEVEL16_TEXTURE = "space/menus/level16.png";
    protected static final String LEVEL16_LOCK_TEXTURE = "space/menus/level16_locked.png";
    protected static final String LEVEL16_HOVER_TEXTURE = "space/menus/level16_hover.png";
    protected static final String LEVEL17_TEXTURE = "space/menus/level17.png";
    protected static final String LEVEL17_LOCK_TEXTURE = "space/menus/level17_locked.png";
    protected static final String LEVEL17_HOVER_TEXTURE = "space/menus/level17_hover.png";
    protected static final String LEVEL18_TEXTURE = "space/menus/level18.png";
    protected static final String LEVEL18_LOCK_TEXTURE = "space/menus/level18_locked.png";
    protected static final String LEVEL18_HOVER_TEXTURE = "space/menus/level18_hover.png";
    protected static final String LEVEL19_TEXTURE = "space/menus/level19.png";
    protected static final String LEVEL19_LOCK_TEXTURE = "space/menus/level19_locked.png";
    protected static final String LEVEL19_HOVER_TEXTURE = "space/menus/level19_hover.png";
    protected static final String LEVEL20_TEXTURE = "space/menus/level20.png";
    protected static final String LEVEL20_LOCK_TEXTURE = "space/menus/level20_locked.png";
    protected static final String LEVEL20_HOVER_TEXTURE = "space/menus/level20_hover.png";
    protected static final String LEVEL21_TEXTURE = "space/menus/level21.png";
    protected static final String LEVEL21_LOCK_TEXTURE = "space/menus/level21_locked.png";
    protected static final String LEVEL21_HOVER_TEXTURE = "space/menus/level21_hover.png";
    protected static final String LEVEL22_TEXTURE = "space/menus/level22.png";
    protected static final String LEVEL22_LOCK_TEXTURE = "space/menus/level22_locked.png";
    protected static final String LEVEL22_HOVER_TEXTURE = "space/menus/level22_hover.png";
    protected static final String LEVEL23_TEXTURE = "space/menus/level23.png";
    protected static final String LEVEL23_LOCK_TEXTURE = "space/menus/level23_locked.png";
    protected static final String LEVEL23_HOVER_TEXTURE = "space/menus/level23_hover.png";
    protected static final String LEVEL24_TEXTURE = "space/menus/level24.png";
    protected static final String LEVEL24_LOCK_TEXTURE = "space/menus/level24_locked.png";
    protected static final String LEVEL24_HOVER_TEXTURE = "space/menus/level24_hover.png";
    protected static final String LEVEL25_TEXTURE = "space/menus/level25.png";
    protected static final String LEVEL25_LOCK_TEXTURE = "space/menus/level25_locked.png";
    protected static final String LEVEL25_HOVER_TEXTURE = "space/menus/level25_hover.png";
    protected static final String LEVEL26_TEXTURE = "space/menus/level26.png";
    protected static final String LEVEL26_LOCK_TEXTURE = "space/menus/level26_locked.png";
    protected static final String LEVEL26_HOVER_TEXTURE = "space/menus/level26_hover.png";
    protected static final String LEVEL27_TEXTURE = "space/menus/level27.png";
    protected static final String LEVEL27_LOCK_TEXTURE = "space/menus/level27_locked.png";
    protected static final String LEVEL27_HOVER_TEXTURE = "space/menus/level27_hover.png";
    protected static final String[][] LEVELS_TEXTURES = {
            {LEVEL1_TEXTURE, LEVEL1_HOVER_TEXTURE, LEVEL1_LOCK_TEXTURE},
            {LEVEL2_TEXTURE, LEVEL2_HOVER_TEXTURE, LEVEL2_LOCK_TEXTURE},
            {LEVEL3_TEXTURE, LEVEL3_HOVER_TEXTURE, LEVEL3_LOCK_TEXTURE},
            {LEVEL4_TEXTURE, LEVEL4_HOVER_TEXTURE, LEVEL4_LOCK_TEXTURE},
            {LEVEL5_TEXTURE, LEVEL5_HOVER_TEXTURE, LEVEL5_LOCK_TEXTURE},
            {LEVEL6_TEXTURE, LEVEL6_HOVER_TEXTURE, LEVEL6_LOCK_TEXTURE},
            {LEVEL7_TEXTURE, LEVEL7_HOVER_TEXTURE, LEVEL7_LOCK_TEXTURE},
            {LEVEL8_TEXTURE, LEVEL8_HOVER_TEXTURE, LEVEL8_LOCK_TEXTURE},
            {LEVEL9_TEXTURE, LEVEL9_HOVER_TEXTURE, LEVEL9_LOCK_TEXTURE},
            {LEVEL10_TEXTURE, LEVEL10_HOVER_TEXTURE, LEVEL10_LOCK_TEXTURE},
            {LEVEL11_TEXTURE, LEVEL11_HOVER_TEXTURE, LEVEL11_LOCK_TEXTURE},
            {LEVEL12_TEXTURE, LEVEL12_HOVER_TEXTURE, LEVEL12_LOCK_TEXTURE},
            {LEVEL13_TEXTURE, LEVEL13_HOVER_TEXTURE, LEVEL13_LOCK_TEXTURE},
            {LEVEL14_TEXTURE, LEVEL14_HOVER_TEXTURE, LEVEL14_LOCK_TEXTURE},
            {LEVEL15_TEXTURE, LEVEL15_HOVER_TEXTURE, LEVEL15_LOCK_TEXTURE},
            {LEVEL16_TEXTURE, LEVEL16_HOVER_TEXTURE, LEVEL16_LOCK_TEXTURE},
            {LEVEL17_TEXTURE, LEVEL17_HOVER_TEXTURE, LEVEL17_LOCK_TEXTURE},
            {LEVEL18_TEXTURE, LEVEL18_HOVER_TEXTURE, LEVEL18_LOCK_TEXTURE},
            {LEVEL19_TEXTURE, LEVEL19_HOVER_TEXTURE, LEVEL19_LOCK_TEXTURE},
            {LEVEL20_TEXTURE, LEVEL20_HOVER_TEXTURE, LEVEL20_LOCK_TEXTURE},
            {LEVEL21_TEXTURE, LEVEL21_HOVER_TEXTURE, LEVEL21_LOCK_TEXTURE},
            {LEVEL22_TEXTURE, LEVEL22_HOVER_TEXTURE, LEVEL22_LOCK_TEXTURE},
            {LEVEL23_TEXTURE, LEVEL23_HOVER_TEXTURE, LEVEL23_LOCK_TEXTURE},
            {LEVEL24_TEXTURE, LEVEL24_HOVER_TEXTURE, LEVEL24_LOCK_TEXTURE},
            {LEVEL25_TEXTURE, LEVEL25_HOVER_TEXTURE, LEVEL25_LOCK_TEXTURE},
            {LEVEL26_TEXTURE, LEVEL26_HOVER_TEXTURE, LEVEL26_LOCK_TEXTURE},
            {LEVEL27_TEXTURE, LEVEL27_HOVER_TEXTURE, LEVEL27_LOCK_TEXTURE},
    };
    protected static final String FONT_FILE = "space/fonts/Suess.ttf";

    /** Texture file for background image */
    protected static final String BACKG_FILE_MAIN = "space/background/blue-background.png";
    //protected static final String BACKG_FILE_WHITE_STAR = "space/background/white-stars.png";
    protected static final String BACKG_FILE_WHITE_STAR = "space/background/small-stars.png";
    protected static final String BACKG_FILE_LG_STAR = "space/background/large-stars.png";
    protected static final String BACKG_FILE_MED_STAR = "space/background/medium-stars.png";
    //protected static final String BACKG_FILE_SM_STAR = "space/background/small-stars.png";
    protected static final String BACKG_FILE_SM_STAR = "space/background/white-stars.png";


    /** The texture file for the bullets */
    protected static final String BULLET_TEXTURE = "space/ships/bullet.png";
    /** The texture file for mass expulsion */
    protected static final String EXPULSION_TEXTURE = "space/Oob/expulsion.png";

    //Music for convert
    protected static Music convert;
    /** The sound file for a jump */
    protected static final String JUMP_SOUND = "audio/jump.wav";
    /** The sound file for a planet explosion */
    protected static final String EXPLOSION_SOUND = "audio/explosion.wav";
    //Mothership sound
    protected static final String MOTHERSHIP_SOUND = "audio/motherShip.wav";
    //Sound for bullet fire
    protected static final String SHOOTING_SOUND = "audio/shooting.wav";

    protected static final String EXPULSION_SOUND = "audio/expulsion.wav";

    //protected static final String CONVERT_SOUND = "audio/convert.wav";

    public static final float SCROLL_SPEED = 0.5f;

    /** The initial position of Oob */
    protected static Vector2 OOB_POS = new Vector2(16f, 12f);
    /** Oob's initial radius */
    protected  float OOB_RADIUS = 1f; //0.2 scale in overlap2d is standard
    protected static final float SIPHON = 0.03f;
    protected static final float POISON = -0.03f;
    protected static final float MIN_RADIUS = 1f;
    protected static final float DEATH_RADIUS = MIN_RADIUS*2/3;
    protected static final float OOB_DEATH_RADIUS = 0.56f;
    protected static final float OOB_WARNING_RADIUS = .85f;
    protected static final float OOB_MAX_RADIUS = 2.2f;
    protected static final float EPSILON = 0.1f;
    protected static final int THRESHOLD = 4;
    protected static final int ADJUST_COOLDOWN = 60;
    protected static final float CONVERT_TIME = 480;

    // A variable for tracking elapsed time for the animation

    private float stateTime=0f;


    protected int inPause = 0;

    //control = 0 is keyboard, control = 1 is mouse
    protected int control = 1;


    protected TextureRegion blackHoleTexture;

    protected BitmapFont displayFont;

    protected int FONT_SIZE = 80;

    /** Planet texture */
    protected TextureRegion blue_P_1_Texture;   protected TextureRegion blue_P_2_Texture;   protected TextureRegion blue_P_3_Texture;
    protected TextureRegion purple_P_1_Texture; protected TextureRegion purple_P_2_Texture; protected TextureRegion purple_P_3_Texture;
    protected TextureRegion orange_P_1_Texture; protected TextureRegion orange_P_2_Texture; protected TextureRegion orange_P_3_Texture;
    protected TextureRegion sky_P_1_Texture;    protected TextureRegion sky_P_2_Texture;    protected TextureRegion sky_P_3_Texture;
    protected TextureRegion green_P_1_Texture;  protected TextureRegion green_P_2_Texture;  protected TextureRegion green_P_3_Texture;
    protected TextureRegion pink_P_1_Texture;   protected TextureRegion pink_P_2_Texture;   protected TextureRegion pink_P_3_Texture;
    protected TextureRegion red_P_1_Texture;    protected TextureRegion red_P_2_Texture;    protected TextureRegion red_P_3_Texture;
    protected TextureRegion mouse_Texture;    protected TextureRegion wasd_Texture;    protected TextureRegion spacebar_Texture;
    protected TextureRegion reset_Texture;    protected TextureRegion pause_Texture;
    protected TextureRegion arrow_Texture;
    protected TextureRegion grow_P_Texture;    protected TextureRegion shrink_P_Texture;
    protected TextureRegion asteroid_Texture;

    /** Animation texture */
    protected Animation<TextureRegion> sunAnimation; // Must declare frame type (TextureRegion)
    protected Texture sunSheet;
    protected Animation<TextureRegion> BH_Animation; // Must declare frame type (TextureRegion)
    protected Texture BH_Sheet;
    protected Animation<TextureRegion> SHIP_Animation; // Must declare frame type (TextureRegion)
    protected Texture SHIP_Sheet;
    protected Animation<TextureRegion> G_SHIP_Animation; // Must declare frame type (TextureRegion)
    protected Texture G_SHIP_Sheet;
    protected Animation<TextureRegion> MOTHERSHIP_Animation; // Must declare frame type (TextureRegion)
    protected Texture MOTHERSHIP_Sheet;
    protected Animation<TextureRegion> SHIP_EXP_Animation; // Must declare frame type (TextureRegion)
    protected Texture SHIP_EXP_Sheet;
    protected Texture Oob_Normal_Sheet;         protected Texture Oob_Growing_Sheet;
    protected Texture Oob_Command_Sheet;        protected Texture Oob_Flying_Sheet;
    protected Texture Oob_Teleporting_Sheet;    protected Texture Oob_Hurting_Sheet;
    protected Texture Oob_Dying_Sheet;          protected Texture Oob_Max_Sheet;
    private Texture EXP_Sheet;
    private Texture WARN_Sheet;

    /** Planet texture */
    protected TextureRegion command_P_Texture;

    /** Planet texture */
    protected TextureRegion neutral_P_Texture;

    /** Expulsion texture */
    protected TextureRegion expulsion_Texture;

    protected TextureRegion settings_Texture;       protected TextureRegion levels_Texture;
    protected TextureRegion play_Texture;           protected TextureRegion settings_Hover_Texture;
    protected TextureRegion levels_Hover_Texture;   protected TextureRegion play_Hover_Texture;
    protected TextureRegion main_Menu_Texture;      protected TextureRegion main_Menu_Hover_Texture;
    protected TextureRegion resume_Texture;         protected TextureRegion resume_Hover_Texture;
    protected TextureRegion next_Level;                protected TextureRegion next_Level_Hover;
    protected TextureRegion next_page_Texture;         protected TextureRegion next_page_Lock_Texture;
    protected TextureRegion next_page_Hover_Texture;   protected TextureRegion prev_page_Texture;
    protected TextureRegion prev_page_Lock_Texture;    protected TextureRegion prev_page_Hover_Texture;
    protected TextureRegion cred_Texture;

    protected TextureRegion pauseTitleTexture;
    protected TextureRegion titleTexture;           protected TextureRegion levelsTitleTexture;
    protected TextureRegion back_Texture;           protected TextureRegion back_Hover_Texture;
    protected TextureRegion back_Text_Texture;      protected TextureRegion back_Text_Hover_Texture;
    protected TextureRegion quit;                   protected TextureRegion quit_hover;
    protected TextureRegion retry;                  protected TextureRegion retry_hover;
    protected TextureRegion replay;                 protected TextureRegion replay_hover;
    protected TextureRegion lost_text;              protected TextureRegion win_text;

    /** Background texture */
    protected TextureRegion backgroundMAIN;
    protected TextureRegion backgroundWHITESTAR;
    protected TextureRegion backgroundLG;
    protected TextureRegion backgroundMED;
    protected TextureRegion backgroundSM;

    /** Texture asset for bullet */
    protected TextureRegion bullet_texture;

    //variables
    protected Vector2 smallestRad;
    protected float rad;
    protected float oldAvatarRad;
    //variables for player controls
    protected boolean jump = false;
    protected float moveDirection = 0f;
    protected Vector2 launchVec;
    // the linked black holes we are interacting with
    protected BlackHoleModel inHole;
    protected BlackHoleModel outHole;
    // says whether we are warping to another black hole
    protected boolean blackHoleWarp;
    // says whether we are coming out of a black hole
    protected boolean comingOut;
    // says whether the player can use controls
    protected boolean playerControl;
    // a counter for displaying win/lose message
    protected int messageCounter;
    // the win/lose state of the game. 0 = regular, 1 = lost, 2 = won, 3 = stuck
    protected int gameState;
    // number of ships converted
    protected int converted;
    // number of clicks
    protected int clicks;

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
        convert = Gdx.audio.newMusic(Gdx.files.internal("audio/convert.wav"));
        manager.load(OOB_NORMAL_FILE, Texture.class);   assets.add(OOB_NORMAL_FILE);
        manager.load(OOB_GROWING_FILE, Texture.class);  assets.add(OOB_GROWING_FILE);
        manager.load(OOB_COMMAND_FILE, Texture.class);  assets.add(OOB_COMMAND_FILE);
        manager.load(OOB_FLYING_FILE, Texture.class);   assets.add(OOB_FLYING_FILE);
        manager.load(OOB_TELEPORTING_FILE, Texture.class);  assets.add(OOB_TELEPORTING_FILE);
        manager.load(OOB_HURTING_FILE, Texture.class);  assets.add(OOB_HURTING_FILE);
        manager.load(OOB_DYING_FILE, Texture.class);    assets.add(OOB_DYING_FILE);
        manager.load(OOB_MAX_FILE, Texture.class);      assets.add(OOB_MAX_FILE);

        manager.load(SETTINGS_TEXTURE, Texture.class);          assets.add(SETTINGS_TEXTURE);
        manager.load(SETTINGS_HOVER_TEXTURE, Texture.class);    assets.add(SETTINGS_HOVER_TEXTURE);
        manager.load(PLAY_TEXTURE, Texture.class);              assets.add(PLAY_TEXTURE);
        manager.load(PLAY_HOVER_TEXTURE, Texture.class);        assets.add(PLAY_HOVER_TEXTURE);
        manager.load(LEVELS_TEXTURE, Texture.class);            assets.add(LEVELS_TEXTURE);
        manager.load(LEVELS_HOVER_TEXTURE, Texture.class);      assets.add(LEVELS_HOVER_TEXTURE);
        manager.load(RESUME_TEXTURE, Texture.class);            assets.add(RESUME_TEXTURE);
        manager.load(RESUME_HOVER_TEXTURE, Texture.class);      assets.add(RESUME_HOVER_TEXTURE);
        manager.load(MAIN_MENU_TEXTURE, Texture.class);         assets.add(MAIN_MENU_TEXTURE);
        manager.load(MAIN_MENU_HOVER_TEXTURE, Texture.class);   assets.add(MAIN_MENU_HOVER_TEXTURE);

        manager.load(NEXT_PAGE_TEXTURE, Texture.class);   assets.add(NEXT_PAGE_TEXTURE);
        manager.load(NEXT_PAGE_HOVER_TEXTURE, Texture.class);   assets.add(NEXT_PAGE_HOVER_TEXTURE);
        manager.load(NEXT_PAGE_LOCK_TEXTURE, Texture.class);   assets.add(NEXT_PAGE_LOCK_TEXTURE);
        manager.load(PREV_PAGE_TEXTURE, Texture.class);    assets.add(PREV_PAGE_TEXTURE);
        manager.load(PREV_PAGE_HOVER_TEXTURE, Texture.class);   assets.add(PREV_PAGE_HOVER_TEXTURE);
        manager.load(PREV_PAGE_LOCK_TEXTURE, Texture.class);   assets.add(PREV_PAGE_LOCK_TEXTURE);

        manager.load(BACK_TEXTURE, Texture.class);              assets.add(BACK_TEXTURE);
        manager.load(BACK_HOVER_TEXTURE, Texture.class);        assets.add(BACK_HOVER_TEXTURE);
        manager.load(BACK_TEXT_TEXTURE, Texture.class);         assets.add(BACK_TEXT_TEXTURE);
        manager.load(BACK_TEXT_HOVER_TEXTURE, Texture.class);   assets.add(BACK_TEXT_HOVER_TEXTURE);

        manager.load(QUIT, Texture.class);                      assets.add(QUIT);
        manager.load(QUIT_HOVER, Texture.class);                assets.add(QUIT_HOVER);
        manager.load(RETRY, Texture.class);                     assets.add(RETRY);
        manager.load(RETRY_HOVER, Texture.class);               assets.add(RETRY_HOVER);
        manager.load(REPLAY, Texture.class);                    assets.add(REPLAY);
        manager.load(REPLAY_HOVER, Texture.class);              assets.add(REPLAY_HOVER);
        manager.load(LOST_TEXT, Texture.class);                 assets.add(LOST_TEXT);
        manager.load(WIN_TEXT, Texture.class);                  assets.add(WIN_TEXT);
        manager.load(NEXT_LEVEL, Texture.class);                assets.add(NEXT_LEVEL);
        manager.load(NEXT_LEVEL_HOVER, Texture.class);          assets.add(NEXT_LEVEL_HOVER);

        manager.load(TITLE, Texture.class);         assets.add(TITLE);
        manager.load(CRED, Texture.class);         assets.add(CRED);
        manager.load(PAUSETITLE, Texture.class);    assets.add(PAUSETITLE);
        manager.load(LEVELSTITLE, Texture.class);   assets.add(LEVELSTITLE);
        manager.load(SETTINGSTITLE, Texture.class); assets.add(SETTINGSTITLE);

        for (int i = 0; i < LEVELS_TEXTURES.length; i++) {
            for (int j = 0; j < LEVELS_TEXTURES[0].length; j++) {
                manager.load(LEVELS_TEXTURES[i][j], Texture.class);
                assets.add(LEVELS_TEXTURES[i][j]);
            }
        }
        manager.load(EXPULSION_TEXTURE, Texture.class); assets.add(EXPULSION_TEXTURE);

        manager.load(BLUE_P_1, Texture.class);  assets.add(BLUE_P_1);
        manager.load(BLUE_P_2, Texture.class);  assets.add(BLUE_P_2);
        manager.load(BLUE_P_3, Texture.class);  assets.add(BLUE_P_3);

        manager.load(PURPLE_P_1, Texture.class);    assets.add(PURPLE_P_1);
        manager.load(PURPLE_P_2, Texture.class);    assets.add(PURPLE_P_2);
        manager.load(PURPLE_P_3, Texture.class);    assets.add(PURPLE_P_3);

        manager.load(ORANGE_P_1, Texture.class);    assets.add(ORANGE_P_1);
        manager.load(ORANGE_P_2, Texture.class);    assets.add(ORANGE_P_2);
        manager.load(ORANGE_P_3, Texture.class);    assets.add(ORANGE_P_3);

        manager.load(SKY_P_1, Texture.class);   assets.add(SKY_P_1);
        manager.load(SKY_P_2, Texture.class);   assets.add(SKY_P_2);
        manager.load(SKY_P_3, Texture.class);   assets.add(SKY_P_3);

        manager.load(GREEN_P_1, Texture.class); assets.add(GREEN_P_1);
        manager.load(GREEN_P_2, Texture.class); assets.add(GREEN_P_2);
        manager.load(GREEN_P_3, Texture.class); assets.add(GREEN_P_3);

        manager.load(PINK_P_1, Texture.class);  assets.add(PINK_P_1);
        manager.load(PINK_P_2, Texture.class);  assets.add(PINK_P_2);
        manager.load(PINK_P_3, Texture.class);  assets.add(PINK_P_3);

        manager.load(RED_P_1, Texture.class);   assets.add(RED_P_1);
        manager.load(RED_P_2, Texture.class);   assets.add(RED_P_2);
        manager.load(RED_P_3, Texture.class);   assets.add(RED_P_3);

        manager.load(MOUSE, Texture.class);         assets.add(MOUSE);
        manager.load(WASD, Texture.class);          assets.add(WASD);
        manager.load(SPACEBAR, Texture.class);      assets.add(SPACEBAR);
        manager.load(PAUSE, Texture.class);         assets.add(PAUSE);
        manager.load(RESET, Texture.class);         assets.add(RESET);
        manager.load(ARROW, Texture.class);         assets.add(ARROW);
        manager.load(GROW_P, Texture.class);         assets.add(GROW_P);
        manager.load(SHRINK_P, Texture.class);         assets.add(SHRINK_P);


        manager.load(SUN_P, Texture.class);             assets.add(SUN_P);

        manager.load(BLACK_HOLE, Texture.class);        assets.add(BLACK_HOLE);

        manager.load(WARNING, Texture.class);           assets.add(WARNING);

        manager.load(EXPLOSION, Texture.class);         assets.add(EXPLOSION);

        manager.load(COMMAND_P, Texture.class);         assets.add(COMMAND_P);

        manager.load(SHIP_TEXTURE, Texture.class);      assets.add(SHIP_TEXTURE);

        manager.load(G_SHIP_TEXTURE, Texture.class);      assets.add(G_SHIP_TEXTURE);

        manager.load(MOTHERSHIP_TEXTURE, Texture.class);      assets.add(MOTHERSHIP_TEXTURE);

        manager.load(SHIP_EXPLOSION, Texture.class);    assets.add(SHIP_EXPLOSION);

        manager.load(NEUTRAL_P, Texture.class);         assets.add(NEUTRAL_P);

        manager.load(ASTEROID, Texture.class);          assets.add(ASTEROID);

        manager.load(BACKG_FILE_MAIN, Texture.class);       assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_MAIN, Texture.class);       assets.add(BACKG_FILE_MAIN);
        manager.load(BACKG_FILE_WHITE_STAR, Texture.class); assets.add(BACKG_FILE_WHITE_STAR);
        manager.load(BACKG_FILE_LG_STAR, Texture.class);    assets.add(BACKG_FILE_LG_STAR);
        manager.load(BACKG_FILE_MED_STAR, Texture.class);   assets.add(BACKG_FILE_MED_STAR);
        manager.load(BACKG_FILE_SM_STAR, Texture.class);    assets.add(BACKG_FILE_SM_STAR);


        manager.load(BULLET_TEXTURE, Texture.class);    assets.add(BULLET_TEXTURE);

        manager.load(JUMP_SOUND, Sound.class);          assets.add(JUMP_SOUND);
        manager.load(EXPLOSION_SOUND, Sound.class);     assets.add(EXPLOSION_SOUND);
        manager.load(MOTHERSHIP_SOUND, Sound.class);    assets.add(MOTHERSHIP_SOUND);
        manager.load(SHOOTING_SOUND, Sound.class);      assets.add(SHOOTING_SOUND);
        manager.load(EXPULSION_SOUND, Sound.class);     assets.add(EXPULSION_SOUND);
        //manager.load(CONVERT_SOUND, Sound.class);       assets.add(CONVERT_SOUND);

        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);

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
        win_text = createTexture(manager, WIN_TEXT, false);
        lost_text = createTexture(manager, LOST_TEXT, false);
        //oobSheet = new Texture(Gdx.files.internal(OOB_FILE));
        Oob_Normal_Sheet = new Texture(Gdx.files.internal(OOB_NORMAL_FILE));
        Oob_Growing_Sheet = new Texture(Gdx.files.internal(OOB_GROWING_FILE));
        Oob_Command_Sheet = new Texture(Gdx.files.internal(OOB_COMMAND_FILE));
        Oob_Flying_Sheet = new Texture(Gdx.files.internal(OOB_FLYING_FILE));
        Oob_Teleporting_Sheet = new Texture(Gdx.files.internal(OOB_TELEPORTING_FILE));
        Oob_Hurting_Sheet = new Texture(Gdx.files.internal(OOB_HURTING_FILE));
        Oob_Dying_Sheet = new Texture(Gdx.files.internal(OOB_DYING_FILE));
        Oob_Max_Sheet = new Texture(Gdx.files.internal(OOB_MAX_FILE));

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

        asteroid_Texture = createTexture(manager, ASTEROID, false);

        mouse_Texture = createTexture(manager, MOUSE, false);
        wasd_Texture = createTexture(manager, WASD, false);
        spacebar_Texture = createTexture(manager, SPACEBAR, false);
        pause_Texture = createTexture(manager, PAUSE, false);
        reset_Texture = createTexture(manager, RESET, false);
        arrow_Texture = createTexture(manager, ARROW, false);
        grow_P_Texture = createTexture(manager, GROW_P, false);
        shrink_P_Texture = createTexture(manager, SHRINK_P, false);



        Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal(EXPLOSION_SOUND));
        Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal(JUMP_SOUND));
        Sound mothershipSound = Gdx.audio.newSound(Gdx.files.internal(MOTHERSHIP_SOUND));
        Sound shootingSound = Gdx.audio.newSound(Gdx.files.internal(SHOOTING_SOUND));
        Sound expulsionSound = Gdx.audio.newSound(Gdx.files.internal(EXPULSION_SOUND));
        //Sound convertSound = Gdx.audio.newSound(Gdx.files.internal(CONVERT_SOUND));

        sunSheet = new Texture(Gdx.files.internal(SUN_P));

        BH_Sheet = new Texture(Gdx.files.internal(BLACK_HOLE));

        WARN_Sheet = new Texture(Gdx.files.internal(WARNING));

        EXP_Sheet = new Texture(Gdx.files.internal(EXPLOSION));

        SHIP_Sheet = new Texture(Gdx.files.internal(SHIP_TEXTURE));

        G_SHIP_Sheet = new Texture(Gdx.files.internal(G_SHIP_TEXTURE));

        MOTHERSHIP_Sheet = new Texture(Gdx.files.internal(MOTHERSHIP_TEXTURE));

        SHIP_EXP_Sheet = new Texture(Gdx.files.internal(SHIP_EXPLOSION));

        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);

        command_P_Texture = createTexture(manager,COMMAND_P,false);

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);

        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

        main_Menu_Texture = createTexture(manager, MAIN_MENU_TEXTURE, false);
        main_Menu_Hover_Texture = createTexture(manager, MAIN_MENU_HOVER_TEXTURE, false);
        levels_Texture = createTexture(manager, LEVELS_TEXTURE, false);
        levels_Hover_Texture = createTexture(manager, LEVELS_HOVER_TEXTURE, false);
        next_Level = createTexture(manager, NEXT_LEVEL, false);
        next_Level_Hover = createTexture(manager, NEXT_LEVEL_HOVER, false);
        retry = createTexture(manager, RETRY, false);
        retry_hover = createTexture(manager, RETRY_HOVER, false);
        replay = createTexture(manager, REPLAY, false);
        replay_hover = createTexture(manager, REPLAY_HOVER, false);

        WIN_TEXTURES[0][0] = main_Menu_Texture;
        WIN_TEXTURES[0][1] = main_Menu_Hover_Texture;
        WIN_TEXTURES[1][0] = levels_Texture;
        WIN_TEXTURES[1][1] = levels_Hover_Texture;
        WIN_TEXTURES[2][0] = replay;
        WIN_TEXTURES[2][1] = replay_hover;
        WIN_TEXTURES[3][0] = next_Level;
        WIN_TEXTURES[3][1] = next_Level_Hover;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                LOSE_TEXTURES[i][j] = WIN_TEXTURES[i][j];
            }
        }
        LOSE_TEXTURES[2][0] = retry;
        LOSE_TEXTURES[2][1] = retry_hover;

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_SOUND);
        sounds.allocate(manager, EXPLOSION_SOUND);
        sounds.allocate(manager, MOTHERSHIP_SOUND);
        sounds.allocate(manager, SHOOTING_SOUND);
        sounds.allocate(manager, EXPULSION_SOUND);
        //sounds.allocate(manager, CONVERT_SOUND);
        displayFont = manager.get(FONT_FILE,BitmapFont.class);
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
    protected Array<Array<Float>> ASTEROIDS = new Array<Array<Float>>();

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
    /** List of all black holes */
    protected  Array<BlackHoleModel> blackHoles;
    // List of command planets
    protected Array<PlanetModel> commandPlanets;

    protected Array<PlanetModel> convertPlanets;
    // List of dying planets
    Array<PlanetModel> planet_explosion;
    // List of exploding ships
    Array<ShipModel> ship_explosion;
    /** list of ships */
    protected Array<ShipModel> ships;

    /** arrays for parallx */
    protected Array<Vector2> text = new Array<Vector2>();
    protected Array<Vector2> stars = new Array<Vector2>();
    protected Array<Vector2> med_stars = new Array<Vector2>();
    protected Array<Vector2> white_stars = new Array<Vector2>();

    /** variables for parllax */
    Vector2 titlecoord = new Vector2();
    Vector2 titlesize = new Vector2();

    Vector2 credcoord = new Vector2();
    Vector2 credsize = new Vector2();

    int LG_S_X; int LG_S_Y; int LG_S_X_START; int LG_S_Y_START; float LG_SPEED = .5f; float LG_SCROLL_SPEED = 6f;
    int med_X; int med_Y; int med_X_START; int med_Y_START; float MED_SPEED = .1f; float MED_SCROLL_SPEED = 2f;
    int white_X; int white_Y; int white_X_START; int white_Y_START; float WHITE_SPEED= .005f; float WHITE_SCROLL_SPEED= .8f;

    Vector2 bg_coord_1 = new Vector2(); Vector2 bg_size_1 = new Vector2(); Vector2 bg_coord_2 = new Vector2();
    Vector2 bg_size_2 = new Vector2(); Vector2 bg_coord_3 = new Vector2(); Vector2 bg_size_3 = new Vector2();
    Vector2 bg_coord_4 = new Vector2(); Vector2 bg_size_4 = new Vector2(); Vector2 bg_coord_5 = new Vector2();
    Vector2 bg_size_5 = new Vector2(); Vector2 bg_coord_6 = new Vector2(); Vector2 bg_size_6 = new Vector2();
    Vector2 bg_coord_7 = new Vector2(); Vector2 bg_size_7 = new Vector2(); Vector2 bg_coord_8 = new Vector2();
    Vector2 bg_size_8 = new Vector2(); Vector2 bg_coord_9 = new Vector2(); Vector2 bg_size_9 = new Vector2();

    Vector2 med_coord_1 = new Vector2(); Vector2 med_size_1 = new Vector2(); Vector2 med_coord_2 = new Vector2();
    Vector2 med_size_2 = new Vector2(); Vector2 med_coord_3 = new Vector2(); Vector2 med_size_3 = new Vector2();
    Vector2 med_coord_4 = new Vector2(); Vector2 med_size_4 = new Vector2(); Vector2 med_coord_5 = new Vector2();
    Vector2 med_size_5 = new Vector2(); Vector2 med_coord_6 = new Vector2(); Vector2 med_size_6 = new Vector2();
    Vector2 med_coord_7 = new Vector2(); Vector2 med_size_7 = new Vector2(); Vector2 med_coord_8 = new Vector2();
    Vector2 med_size_8 = new Vector2(); Vector2 med_coord_9 = new Vector2(); Vector2 med_size_9 = new Vector2();


    Vector2 white_coord_1 = new Vector2(); Vector2 white_size_1 = new Vector2(); Vector2 white_coord_2 = new Vector2();
    Vector2 white_size_2 = new Vector2(); Vector2 white_coord_3 = new Vector2(); Vector2 white_size_3 = new Vector2();
    Vector2 white_coord_4 = new Vector2(); Vector2 white_size_4 = new Vector2(); Vector2 white_coord_5 = new Vector2();
    Vector2 white_size_5 = new Vector2(); Vector2 white_coord_6 = new Vector2(); Vector2 white_size_6 = new Vector2();
    Vector2 white_coord_7 = new Vector2(); Vector2 white_size_7 = new Vector2(); Vector2 white_coord_8 = new Vector2();
    Vector2 white_size_8 = new Vector2(); Vector2 white_coord_9 = new Vector2(); Vector2 white_size_9 = new Vector2();

    /** vector from Oob to center of the screen */
    protected Vector2 vecToCenter = new Vector2();
    /** Mark set to handle more sophisticated collision callbacks */
    protected ObjectSet<Fixture> sensorFixtures;
    /** the font for the mass text on each object */
    protected BitmapFont massFont;

    protected int returnToPlanetTimer;


    protected Vector2 screen_vec = new Vector2();

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
    //Should we play the jump sound?
    protected boolean forceJump;
    //is the screen locked on ship?
    protected boolean shipLock;

    /**
     * Creates and initialize a new instance of the platformer game
     *
     * The game has default gravity and other settings
     */
    public PlayMode(String level, int number) {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
            LEVEL=level;
            LV_NUMBER=number;
        sensorFixtures = new ObjectSet<Fixture>();
        planets = new Array<PlanetModel>();
        winPlanets = new Array<PlanetModel>();
        commandPlanets = new Array<PlanetModel>();
        convertPlanets = new Array<PlanetModel>();
        planet_explosion = new Array<PlanetModel>();
        ship_explosion = new Array<ShipModel>();
        ships = new Array<ShipModel>();
        massFont = new BitmapFont();
        massFont.getData().setScale(2);
        launchVec = new Vector2();
        returnToPlanetTimer = 0;
        clicks = 0;
        adjustCooldown = ADJUST_COOLDOWN;
        FileHandle json = Gdx.files.internal("overlap2d/Testing/scenes/" + level + ".dt");
        String jsonString = json.readString();
        jsonParse(jsonString);
        play = true;
        playerControl = true;
        gameState = 0;
        messageCounter = 0;
        lastHoverPlanet = new boolean[4];
        converted = 0;
        InputController.getInstance().setCenterCamera(true);
//        Gdx.input.setCursorCatched(true);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        InputController.getInstance().setCenterCamera(true);
        playerControl = true;
        blackHoleWarp = false;
        comingOut = false;
        returnToPlanetTimer = 0;
        justLoaded = true;
        Vector2 gravity = new Vector2(world.getGravity() );

        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }

        objects.clear();
        addQueue.clear();
        planets.clear();
        winPlanets.clear();
        commandPlanets.clear();
        convertPlanets.clear();
        planet_explosion.clear();
        ships.clear();
        text.clear();
        stars.clear();
        med_stars.clear();
        white_stars.clear();
        world.dispose();
        clicks = 0;
        convert.stop();
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
        gameState = 0;
        messageCounter = 0;
        jumpTime = 0;
        converted = 0;
        lastHoverPlanet = new boolean[4];

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
        Array<Array<Float>> asteroidArray = new Array<Array<Float>>();
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
                String custom = temp.getString("customVars");
                //It will start with spawn:
                tempArray.add(Float.parseFloat(custom.substring(6)));
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
                String custom = "";
                if (temp.has("customVars")) {
                    custom = temp.getString("customVars");
                }
                tempArray = new Array<Float>();
                tempArray.add((xPos+0.75f)*3);
                tempArray.add((yPos+0.75f)*3);

                //It will start with type:
                if (custom.length()>3) {
                    float shipType = Float.parseFloat((custom.substring(5, 6)));
                    tempArray.add(shipType);
                    if(shipType == 2){
                        tempArray.add(Float.parseFloat(custom.substring(6)));
                    }
                    if(shipType == 1){
                        if (custom.length() > 7) {
                            tempArray.add(Float.parseFloat((custom.substring(6,7))));
                        }
                        else {
                            tempArray.add(-1f);
                        }
                    }
                }
                else{
                    tempArray.add(0.0f);
                }
                shipArray.add(tempArray);
            }
            else if(objectName.equals("oob2")){
                OOB_RADIUS = scale / 0.35f;
            }
            else if(objectName.equals("asteroidBelt")){
                tempArray = new Array<Float>();
                tempArray.add((xPos+3)*3);
                tempArray.add((yPos+3)*3);
                tempArray.add((2.5f*scale/0.4f)*3);
                asteroidArray.add(tempArray);
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
        ASTEROIDS = asteroidArray;
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
            if (obj.getType() == 0f) {

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
                if (LEVEL== "T1" || LEVEL== "T2" || LEVEL== "T3 ") {
                    obj.setTexture(grow_P_Texture);
                }
            }
            //Command Planets
            if (obj.getType() == 1f) {
                obj.setTexture(command_P_Texture);
                commandPlanets.add(obj);
                obj.setCooldown(Math.round(PLANETS.get(ii).get(4)));
            }
            //Poison Planets
            if (obj.getType() == 2f) {
                obj.scalePicScale(new Vector2(1.2f, 1.2f));
            }
            //Neutral Planets
            if (obj.getType() == 3f) {
                if (LEVEL=="T1" && ii==2) {
                    obj.setTexture(mouse_Texture);
                }
                else if (LEVEL=="T2" && ii==0){
                    obj.setTexture(wasd_Texture);
                }
                else if (LEVEL=="T2" && ii==2){
                    obj.setTexture(spacebar_Texture);
                }
                else {
                    obj.setTexture(neutral_P_Texture);
                }
            }
            addObject(obj);
            planets.add(obj);
        }

        // Create black holes
        blackHoles = new Array<BlackHoleModel>();
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

            blackHoles.add(b1);
            blackHoles.add(b2);
            addObject(b1);
            addObject(b2);
        }

        //Create Asteroids
        for (int i = 0; i<ASTEROIDS.size; i++){
            AsteroidModel asteroid = new AsteroidModel(ASTEROIDS.get(i).get(0), ASTEROIDS.get(i).get(1), ASTEROIDS.get(i).get(2),ASTEROIDS.get(i).get(2));
            asteroid.setBodyType(BodyDef.BodyType.StaticBody);
            asteroid.setDensity(BASIC_DENSITY);
            asteroid.setFriction(BASIC_FRICTION);
            asteroid.setRestitution(BASIC_RESTITUTION);
            asteroid.setDrawScale(scale);
            asteroid.setTexture(asteroid_Texture);
            asteroid.scalePicScale(new Vector2(.09f * asteroid.getWidth(), .09f * asteroid.getHeight()));
            asteroid.setName("asteroid");
            addObject(asteroid);
        }

        // Create Ships
        for (int ii = 0; ii <SHIPS.size; ii++) {
            ShipModel sh;
            if (SHIPS.get(ii).get(2)==1) {
                sh = new ShipModel(SHIPS.get(ii).get(0), SHIPS.get(ii).get(1), SHIPS.get(ii).get(2), "g");
            }
            else if (SHIPS.get(ii).get(2)==2) {
                sh = new ShipModel(SHIPS.get(ii).get(0), SHIPS.get(ii).get(1), SHIPS.get(ii).get(2), "m", "m", SHIPS.get(ii).get(3));
            }
            else {
                sh = new ShipModel(SHIPS.get(ii).get(0), SHIPS.get(ii).get(1), SHIPS.get(ii).get(2));
            }
            sh.setBodyType(BodyDef.BodyType.DynamicBody);
            sh.setDensity(BASIC_DENSITY);
            sh.setFriction(BASIC_FRICTION);
            sh.setRestitution(BASIC_RESTITUTION);
            sh.setDrawScale(scale);
            sh.scalePicScale(new Vector2(.2f, .2f));
            int tag = -1;
            if (sh.getType() == 2) {
                sh.scalePicScale(new Vector2(2f, 2f));
            }
            else if (sh.getType() ==1) {
                sh.scalePicScale(new Vector2(1.5f, 1.5f));
                tag = (int) ((float) SHIPS.get(ii).get(3));
            }
            sh.setName("ship");
            sh.setGravityScale(0.0f);
            ships.add(sh);
            addObject(sh);
            if (tag != -1) {commandPlanets.get(tag).addShip(sh);}
        }
        // Create Oob
        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(currentPlanet.getX()+canvas.getWidth()/80f - 0.8f, currentPlanet.getY() + currentPlanet.getRadius()*2+canvas.getHeight()/80f, OOB_RADIUS);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS, .4f*OOB_RADIUS));
        addObject(complexAvatar);

        loadAnim();

        setBG();
        set_med_BG();
        set_white_BG();
        aiController = new AIController(ships, planets, blackHoles, commandPlanets, complexAvatar, scale);
    }

    public void setBG() {
        if ((backgroundLG.getRegionWidth() - canvas.getWidth()) > 0) {
            LG_S_X = 0;
            LG_S_X_START=0;
        } else {
            LG_S_X = -(backgroundLG.getRegionWidth() - canvas.getWidth()) / 2;
            LG_S_X_START = -(backgroundLG.getRegionWidth() - canvas.getWidth()) / 2;
        }

        if ((backgroundLG.getRegionHeight() - canvas.getHeight()) > 0) {
            LG_S_Y = 0;
            LG_S_Y_START = 0;
        } else {
            LG_S_Y = -(backgroundLG.getRegionHeight() - canvas.getHeight()) / 2;
            LG_S_Y_START = -(backgroundLG.getRegionHeight() - canvas.getHeight()) / 2;
        }

        bg_coord_1.set(  LG_S_X, LG_S_Y );
        bg_size_1.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_1);     stars.add (bg_size_1);

        bg_coord_2.set(  LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y );
        bg_size_2.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_2);     stars.add (bg_size_2);

        bg_coord_3.set(  LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y );
        bg_size_3.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_3);     stars.add (bg_size_3);

        bg_coord_4.set(  LG_S_X, LG_S_Y+backgroundLG.getRegionHeight());
        bg_size_4.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_4);         stars.add (bg_size_4);

        bg_coord_5.set(  LG_S_X, LG_S_Y-backgroundLG.getRegionHeight());
        bg_size_5.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_5);         stars.add (bg_size_5);

        bg_coord_6.set(  LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y+backgroundLG.getRegionHeight());
        bg_size_6.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_6);         stars.add (bg_size_6);

        bg_coord_7.set(  LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y-backgroundLG.getRegionHeight());
        bg_size_7.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_7);         stars.add (bg_size_7);

        bg_coord_8.set(  LG_S_X+backgroundLG.getRegionWidth(), LG_S_Y-backgroundLG.getRegionHeight());
        bg_size_8.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_8);         stars.add (bg_size_8);

        bg_coord_9.set(  LG_S_X-backgroundLG.getRegionWidth(), LG_S_Y+backgroundLG.getRegionHeight());
        bg_size_9.set(  backgroundLG.getRegionWidth(), backgroundLG.getRegionHeight()  );
        stars.add (bg_coord_9);         stars.add (bg_size_9);
    }

    public void set_med_BG() {
        if ((backgroundMED.getRegionWidth() - canvas.getWidth()) > 0) {
            med_X = 0;
            med_X_START=0;
        } else {
            med_X = -(backgroundMED.getRegionWidth() - canvas.getWidth()) / 2;
            med_X_START = -(backgroundMED.getRegionWidth() - canvas.getWidth()) / 2;
        }

        if ((backgroundMED.getRegionHeight() - canvas.getHeight()) > 0) {
            med_Y = 0;
            med_Y_START = 0;
        } else {
            med_Y = -(backgroundMED.getRegionHeight() - canvas.getHeight()) / 2;
            med_Y_START = -(backgroundMED.getRegionHeight() - canvas.getHeight()) / 2;
        }

        med_coord_1.set(  med_X, med_Y );
        med_size_1.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_1);        med_stars.add (med_size_1);

        med_coord_2.set(  med_X+backgroundMED.getRegionWidth(), med_Y );
        med_size_2.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_2);        med_stars.add (med_size_2);

        med_coord_3.set(  med_X-backgroundMED.getRegionWidth(), med_Y );
        med_size_3.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_3);        med_stars.add (med_size_3);

        med_coord_4.set(  med_X, med_Y+backgroundMED.getRegionHeight());
        med_size_4.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_4);        med_stars.add (med_size_4);

        med_coord_5.set(  med_X, med_Y-backgroundMED.getRegionHeight());
        med_size_5.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_5);        med_stars.add (med_size_5);

        med_coord_6.set(  med_X+backgroundMED.getRegionWidth(), med_Y+backgroundMED.getRegionHeight());
        med_size_6.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_6);        med_stars.add (med_size_6);

        med_coord_7.set(  med_X-backgroundMED.getRegionWidth(), med_Y-backgroundMED.getRegionHeight());
        med_size_7.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_7);        med_stars.add (med_size_7);

        med_coord_8.set(  med_X+backgroundMED.getRegionWidth(), med_Y-backgroundMED.getRegionHeight());
        med_size_8.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_8);        med_stars.add (med_size_8);

        med_coord_9.set(  med_X-backgroundMED.getRegionWidth(), med_Y+backgroundMED.getRegionHeight());
        med_size_9.set(  backgroundMED.getRegionWidth(), backgroundMED.getRegionHeight()  );
        med_stars.add (med_coord_9);        med_stars.add (med_size_9);
    }


    public void set_white_BG() {
        if ((backgroundWHITESTAR.getRegionWidth() - canvas.getWidth()) > 0) {
            white_X = 0;
            white_X_START=0;
        } else {
            white_X = -(backgroundWHITESTAR.getRegionWidth() - canvas.getWidth()) / 2;
            white_X_START = -(backgroundWHITESTAR.getRegionWidth() - canvas.getWidth()) / 2;
        }

        if ((backgroundWHITESTAR.getRegionHeight() - canvas.getHeight()) > 0) {
            white_Y = 0;
            white_Y_START = 0;
        } else {
            white_Y = -(backgroundWHITESTAR.getRegionHeight() - canvas.getHeight()) / 2;
            white_Y_START = -(backgroundWHITESTAR.getRegionHeight() - canvas.getHeight()) / 2;
        }

        white_coord_1.set(  white_X, white_Y );
        white_size_1.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_1);        white_stars.add (white_size_1);

        white_coord_2.set(  white_X+backgroundWHITESTAR.getRegionWidth(), white_Y );
        white_size_2.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_2);        white_stars.add (white_size_2);

        white_coord_3.set(  white_X-backgroundWHITESTAR.getRegionWidth(), white_Y );
        white_size_3.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_3);        white_stars.add (white_size_3);

        white_coord_4.set(  white_X, white_Y+backgroundWHITESTAR.getRegionHeight());
        white_size_4.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_4);        white_stars.add (white_size_4);

        white_coord_5.set(  white_X, white_Y-backgroundWHITESTAR.getRegionHeight());
        white_size_5.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_5);        white_stars.add (white_size_5);

        white_coord_6.set(  white_X+backgroundWHITESTAR.getRegionWidth(), white_Y+backgroundWHITESTAR.getRegionHeight());
        white_size_6.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_6);        white_stars.add (white_size_6);

        white_coord_7.set(  white_X-backgroundWHITESTAR.getRegionWidth(), white_Y-backgroundWHITESTAR.getRegionHeight());
        white_size_7.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_7);        white_stars.add (white_size_7);

        white_coord_8.set(  white_X+backgroundWHITESTAR.getRegionWidth(),white_Y-backgroundWHITESTAR.getRegionHeight());
        white_size_8.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_8);        white_stars.add (white_size_8);

        white_coord_9.set(  white_X-backgroundWHITESTAR.getRegionWidth(),white_Y+backgroundWHITESTAR.getRegionHeight());
        white_size_9.set(  backgroundWHITESTAR.getRegionWidth(), backgroundWHITESTAR.getRegionHeight()  );
        white_stars.add (white_coord_9);        white_stars.add (white_size_9);
    }

    public void loadAnim() {
        complexAvatar.set_Normal_sheet(Oob_Normal_Sheet);               complexAvatar.createNormaltex();
        complexAvatar.set_Growing_sheet(Oob_Growing_Sheet);             complexAvatar.createGrowingtex();
        complexAvatar.set_Command_sheet(Oob_Command_Sheet);             complexAvatar.createCommandtex();
        complexAvatar.set_Flying_sheet(Oob_Flying_Sheet);               complexAvatar.createFlyingtex();
        complexAvatar.set_Teleporting_sheet(Oob_Teleporting_Sheet);     complexAvatar.createTeleportingtex();
        complexAvatar.set_Hurting_sheet(Oob_Hurting_Sheet);             complexAvatar.createHurtingtex();
        complexAvatar.set_Dying_sheet(Oob_Dying_Sheet);                 complexAvatar.createDyingtex();
        complexAvatar.set_Max_sheet(Oob_Max_Sheet);                     complexAvatar.createMaxtex();
        sunTex();   BHTex(); SHIPTex(); G_SHIPTex(); MOTHERSHIPTex(); SHIPEXPTex();
    }

    public void BHTex() {
        //CREATE BLACK HOLE TEXTURE
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 12, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(BH_Sheet,
                BH_Sheet.getWidth() / FRAME_COLS,
                BH_Sheet.getHeight() / FRAME_ROWS);

        //Reorder array
        TextureRegion[] BH_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                BH_Frames[index++] = tmp[i][j];
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        BH_Animation = new Animation<TextureRegion>(.15f, BH_Frames);
    }

    public void sunTex() {
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(sunSheet,
                sunSheet.getWidth() / FRAME_COLS,
                sunSheet.getHeight() / FRAME_ROWS);

        //Reorder array
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

    public void SHIPTex() {
        //CREATE BLACK HOLE TEXTURE
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(SHIP_Sheet,
                SHIP_Sheet.getWidth() / FRAME_COLS,
                SHIP_Sheet.getHeight() / FRAME_ROWS);

        //Reorder array
        TextureRegion[] SHIP_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                SHIP_Frames[index++] = tmp[i][j];
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        SHIP_Animation = new Animation<TextureRegion>(.05f, SHIP_Frames);
    }


    public void G_SHIPTex() {
        //CREATE BLACK HOLE TEXTURE
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(G_SHIP_Sheet,
                G_SHIP_Sheet.getWidth() / FRAME_COLS,
                G_SHIP_Sheet.getHeight() / FRAME_ROWS);

        //Reorder array
        TextureRegion[] SHIP_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                SHIP_Frames[index++] = tmp[i][j];
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        G_SHIP_Animation = new Animation<TextureRegion>(.05f, SHIP_Frames);
    }

    public void MOTHERSHIPTex() {
        //CREATE BLACK HOLE TEXTURE
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(MOTHERSHIP_Sheet,
                MOTHERSHIP_Sheet.getWidth() / FRAME_COLS,
                MOTHERSHIP_Sheet.getHeight() / FRAME_ROWS);

        //Reorder array
        TextureRegion[] MOTHERSHIP_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                MOTHERSHIP_Frames[index++] = tmp[i][j];
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        MOTHERSHIP_Animation = new Animation<TextureRegion>(.05f, MOTHERSHIP_Frames);
    }

    public void SHIPEXPTex() {
        //CREATE BLACK HOLE TEXTURE
        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 5, FRAME_ROWS = 1;

        //Split up the sheet
        TextureRegion[][] tmp = TextureRegion.split(SHIP_EXP_Sheet,
                SHIP_EXP_Sheet.getWidth() / FRAME_COLS,
                SHIP_EXP_Sheet.getHeight() / FRAME_ROWS);

        //Reorder array
        TextureRegion[] SHIP_EXP_Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                SHIP_EXP_Frames[index++] = tmp[i][j];
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        SHIP_EXP_Animation = new Animation<TextureRegion>(.05f, SHIP_EXP_Frames);
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
                sh = new ShipModel(c.getX()+c.getRadius()*spawnDir.x, c.getY()+c.getRadius()*spawnDir.y, 0);
                sh.setBodyType(BodyDef.BodyType.DynamicBody);
                sh.setDensity(BASIC_DENSITY);
                sh.setFriction(BASIC_FRICTION);
                sh.setRestitution(BASIC_RESTITUTION);
                sh.setDrawScale(scale);
                sh.scalePicScale(new Vector2(.2f, .2f));
                sh.setName("ship");
                sh.setGravityScale(0.0f);
                addObject(sh);
                aiController.addShip(sh, c);
                if(sh.getType() == 2)
                    aiController.findBigPlanet(sh);
                c.addShip(sh);
                for(int i = 0; i < c.getShips().size; i++) {
                    ShipModel s = c.getShips().get(i);
                    if(s.getPosition().cpy().sub(aiController.getShipTarget(s).getPosition()).len() >= s.getOrbitDistance() + aiController.getShipTarget(s).getRadius() + EPSILON) {
                        continue;
                    }
                    
                    if(i <= 2) {
                        s.setOrbitDistance(3.5f);
                        aiController.setTarget(c.getShips().get(i), c);
                    }
                    else if(i <= 6 && i % 2 == 0) {
                        s.setOrbitDistance(3.5f);
                        aiController.setTarget(c.getShips().get(i), c);
                    }
                    else if(i <= 14 && i % 2 == 0) {
                        s.setOrbitDistance(4.5f);
                        aiController.setTarget(c.getShips().get(i), c);
                    }
                    else {
                        int start = c.getLastPlanetSentTo() + 1;
                        Vector2 dist;
                        for(int j = 0; j < planets.size; j++) {
                            dist = c.getPosition().cpy().sub(planets.get((j+start) % planets.size).getPosition());
                            if(dist.len() < 60 && planets.get((j+start) % planets.size).getType() != 1) {
                                c.setLastPlanetSentTo((j + start) % planets.size);
                                aiController.setTarget(s, planets.get((j + start) % planets.size));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void loopConvertPlanet() {
        for (int i = 0; i < planets.size; i++) {
            if (planets.get(i).getType() != 1) {
                if (planets.get(i).getConvert() > CONVERT_TIME) {
                    planets.get(i).setType(1);
                    planets.get(i).setTexture(command_P_Texture);
                    convertPlanets.removeValue(planets.get(i), true);
                    commandPlanets.add(planets.get(i));
                    planets.get(i).setConvert(0);
                    aiController.setPlanets(planets);
                    converted++;
                }
                else if(planets.get(i).getConvert()==1){
                    convertPlanets.add(planets.get(i));
                }
            }
        }
    }

    //Shoot bullet from ship
    public void shootBullet(){
        if(aiController.bulletData.size != 0) {
            for (int i = 0; i < aiController.bulletData.size; i += 5) {
                //0 is normal, 1 is tractor beam
                BulletModel bullet = new BulletModel(aiController.bulletData.get(i), aiController.bulletData.get(i + 1));
                bullet.setBodyType(BodyDef.BodyType.DynamicBody);
                bullet.setDensity(0.0f);
                bullet.setFriction(0.0f);
                bullet.setRestitution(0.0f);
                bullet.setDrawScale(scale);
                bullet.scalePicScale(new Vector2(0.3f, 0.3f));
                bullet.setGravityScale(0);
                bullet.setVX(aiController.bulletData.get(i + 2));
                bullet.setVY(aiController.bulletData.get(i + 3));
                bullet.setAngle((float) (Math.atan2(bullet.getVY(), bullet.getVX()) - Math.PI / 2));
                bullet.setName("bullet");
                if(aiController.bulletData.get(i+4)==0) {
                    bullet.setTexture(bullet_texture);
                    SoundController.getInstance().play(SHOOTING_SOUND, SHOOTING_SOUND, false, EFFECT_VOLUME - 0.6f);
                }
                //tractor beam bullets
                else if(aiController.bulletData.get(i+4)==1){
                    bullet.setTexture(bullet_texture);
                    //SoundController.getInstance().play(CONVERT_SOUND, CONVERT_SOUND, false, EFFECT_VOLUME);
                    if (SoundController.getInstance().getMute()){
                        convert.stop();
                    }
                    else{
                        convert.play();
                    }
                }
                else{
                    bullet.setTexture(bullet_texture);
                }
                addObject(bullet);
            }
            aiController.bulletData.clear();
        }

    }

    public void unlockedScrollScreen() {
        if(InputController.getInstance().getScrollUp()) {
            for(Obstacle o : objects) {
                if(o.equals(complexAvatar))
                    complexAvatar.addToPosition(0, -SCROLL_SPEED);
                else
                    o.setPosition(o.getPosition().x, o.getPosition().y - SCROLL_SPEED);
            }
        }
        else if(InputController.getInstance().getScrollDown()) {
            for(Obstacle o : objects) {
                if(o.equals(complexAvatar))
                    complexAvatar.addToPosition(0, SCROLL_SPEED);
                else
                    o.setPosition(o.getPosition().x, o.getPosition().y + SCROLL_SPEED);
            }
        }
        if(InputController.getInstance().getScrollLeft()) {
            for(Obstacle o : objects) {
                if(o.equals(complexAvatar))
                    complexAvatar.addToPosition(SCROLL_SPEED, 0);
                else
                    o.setPosition(o.getPosition().x + SCROLL_SPEED, o.getPosition().y);
            }
        }
        else if(InputController.getInstance().getScrollRight()) {
            for(Obstacle o : objects) {
                if(o.equals(complexAvatar))
                    complexAvatar.addToPosition(-SCROLL_SPEED, 0);
                else
                    o.setPosition(o.getPosition().x - SCROLL_SPEED, o.getPosition().y);
            }
        }
    }

    public void scrollScreen() {
        screen_vec.add(vecToCenter.cpy().scl(-1f / 35));
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

    public void screenLockShip(ShipModel sh) {
        shipLock = true;
        InputController.getInstance().setCenterCamera(false);
        vecToCenter.set(canvas.getWidth() / 80f - sh.getX(), canvas.getHeight() / 80f - sh.getY());
        for (Obstacle o : objects) {
            if (justLoaded) {
                o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy()));
                justLoaded = false;
            } else {
                if (o.equals(complexAvatar)) {
                    for (Obstacle p : complexAvatar.getBodies()) {
                        p.setPosition(p.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
                    }
                } else
                    o.setPosition(o.getPosition().cpy().add(vecToCenter.cpy().scl(1f / 25)));
            }
        }
    }

    public void scrollText() {
        if (text.size > 0) {
            if (!InputController.getInstance().getCenterCamera()) {
                if (InputController.getInstance().getScrollUp()) {
                    text.get(0).y += -SCROLL_SPEED * 10;
                } else if (InputController.getInstance().getScrollDown()) {
                    text.get(0).y += SCROLL_SPEED * 10;
                }
                if (InputController.getInstance().getScrollLeft()) {
                    text.get(0).x += SCROLL_SPEED * 10;
                } else if (InputController.getInstance().getScrollRight()) {
                    text.get(0).x += -SCROLL_SPEED * 10;
                }
            } else {
                text.get(0).x += (vecToCenter.x);
                text.get(0).y += (vecToCenter.y);
            }
        }
    }

    public void scrollStars(Array<Vector2> starArray, float speed, float scrollspeed,TextureRegion background, int Xstart, int Ystart) {
        if (starArray.size>0) {
            if (!InputController.getInstance().getCenterCamera()) {
                if (InputController.getInstance().getScrollUp()) {
                    for (int i = 0; i < starArray.size; i += 2) {
                        starArray.get(i).y += -SCROLL_SPEED * scrollspeed;
                    }
                }
                else if (InputController.getInstance().getScrollDown()) {
                    for (int i = 0; i < starArray.size; i += 2) {
                        starArray.get(i).y += SCROLL_SPEED * scrollspeed;
                    }
                }

                if (InputController.getInstance().getScrollLeft()) {
                    for (int i = 0; i < starArray.size; i += 2) {
                        starArray.get(i).x += SCROLL_SPEED * scrollspeed;
                    }
                }
                else if (InputController.getInstance().getScrollRight()) {
                    for (int i = 0; i < starArray.size; i += 2) {
                        starArray.get(i).x += -SCROLL_SPEED * scrollspeed;
                    }
                }
            }
            else {
                for (int i = 0; i < starArray.size; i+=2) {
                    starArray.get(i).x += (vecToCenter.x)*speed;
                    starArray.get(i).y += (vecToCenter.y)*speed;
                }
            }
            if (starArray.get(0).x > background.getRegionWidth()||  starArray.get(0).x < -background.getRegionWidth() ) {
                float Ydelt=starArray.get(0).y - Ystart;
                starArray.clear();
                if (starArray==stars) {
                    setBG();
                }
                else if (starArray==med_stars){
                    set_med_BG();
                }
                else if (starArray==white_stars) {
                    set_white_BG();
                }
                for (int i = 0; i < starArray.size; i += 2) {
                    starArray.get(i).y += Ydelt;
                }
            }
            if (starArray.get(0).y > background.getRegionHeight() ||  starArray.get(0).y < -background.getRegionHeight() ) {
                float Xdelt=starArray.get(0).x - Xstart;
                starArray.clear();
                if (starArray==stars) {
                    setBG();
                }
                else if (starArray==med_stars){
                    set_med_BG();
                }
                else if (starArray==white_stars) {
                    set_white_BG();
                }
                for (int i = 0; i < starArray.size; i += 2) {
                    starArray.get(i).x += Xdelt;
                }
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

    public void decreasePlanetMass() {
        float oldPlanMass = currentPlanet.getMass();
        float suckSpeed;
        if(currentPlanet.getType() == 0)
            suckSpeed = SIPHON*2;
        else
            suckSpeed = SIPHON*2;
        currentPlanet.setRadius((float)Math.sqrt((oldPlanMass - suckSpeed)/Math.PI));
        currentPlanet.scalePicScale(new Vector2(currentPlanet.getRadius() / rad, currentPlanet.getRadius() / rad));
    }

    //Siphon closest planet
    public void siphonPlanet(){
        oldAvatarRad = complexAvatar.getRadius();
        float oldOobMass = complexAvatar.getMass();
        decreasePlanetMass();
        float suckSpeed;
        if(currentPlanet.getType() == 0)
            suckSpeed = SIPHON*2;
        else
            suckSpeed = SIPHON*2;
        if(currentPlanet.getType() == 0) {
            complexAvatar.setRadius((float) Math.sqrt((oldOobMass + suckSpeed / 3) / Math.PI));
            complexAvatar.scalePicScale(new Vector2(complexAvatar.getRadius() / oldAvatarRad, complexAvatar.getRadius() / oldAvatarRad));
        }
    }

    // Siphon mothership
    public void siphonShip(ShipModel sh){
        float oldMass = sh.getMass();
        float suckSpeed = SIPHON*2/3;
        float oldWidth = sh.getWidth();
        float oldHeight = sh.getHeight();
        sh.setMass(oldMass - suckSpeed);
        sh.setWidth(oldWidth*(sh.getMass()/oldMass));
        sh.setHeight(oldHeight*(sh.getMass()/oldMass));
        sh.scalePicScale(new Vector2(sh.getWidth()/oldWidth, sh.getHeight()/oldHeight));
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
        if(!forceJump) {
            clicks++;
            SoundController.getInstance().play(JUMP_SOUND, JUMP_SOUND, false, EFFECT_VOLUME - 0.7f);
        }
        Vector2 mouseVec = InputController.getInstance().getCursor(canvas).cpy().sub(complexAvatar.getPosition());
        complexAvatar.setLinearVelocity(mouseVec.cpy().nor().scl(12));
        complexAvatar.setDirection(mouseVec.cpy().nor().scl(12));
        lastPlanet = currentPlanet;
        currentPlanet = null;
        forceJump = false;
    }

    //Determines whether the player is using mouse or keyboard and sets associated variables when Oob is on a planet
    public void groundPlayerControls(){
        if (InputController.getInstance().didReset()) {
            reset();
        }
        if(InputController.getInstance().didPause()){
            if (play) listener.exitScreen(this, 3);
            InputController.getInstance().setCenterCamera(true);
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
            if (play) listener.exitScreen(this, 3);
            InputController.getInstance().setCenterCamera(true);
        }
        if(playerControl) {
            if (control == 1) {
                launchVec = complexAvatar.getPosition().cpy().sub(InputController.getInstance().getCursor(canvas)).scl(-1);
                jump = InputController.getInstance().getMouseJump();
            } else {
                jump = InputController.getInstance().getJump();
                moveDirection = InputController.getInstance().getHorizontal();
            }
        }
    }

    public void handleBlackHole() {
        Vector2 oobToHole = new Vector2(inHole.getX() - complexAvatar.getX(), inHole.getY() - complexAvatar.getY());

        if(oobToHole.len() < complexAvatar.getRadius() + 0.5f) { // transition between black holes
            Vector2 newPos = outHole.getPosition().cpy().add(outHole.getOutVelocity().cpy().nor().scl(complexAvatar.getRadius() + 0.5f));
            complexAvatar.addToPosition(newPos.x - complexAvatar.getX(), newPos.y - complexAvatar.getY());
            complexAvatar.setLinearVelocity(outHole.getOutVelocity());
            inHole.setRadius(inHole.getOldRadius());
            comingOut = true;
        }
        else if(comingOut) { // leaving the other black hole
            InputController.getInstance().setCenterCamera(true);
            oobToHole = new Vector2(outHole.getX() - complexAvatar.getX(), outHole.getY() - complexAvatar.getY());
            complexAvatar.setLinearVelocity(outHole.getOutVelocity().cpy().scl(6f));
            complexAvatar.setDirection(outHole.getOutVelocity().cpy().scl(6f));
            if(oobToHole.len() > outHole.getOldRadius() + complexAvatar.getRadius() + 0.5f) {
                outHole.setRadius(outHole.getOldRadius());
                comingOut = false;
                playerControl = true;
                blackHoleWarp = false;
            }
        }
        else { // entering the first black hole
            complexAvatar.resetForceVec();
            Vector2 newVelocity = oobToHole.cpy().nor().scl(3f).add(new Vector2(oobToHole.cpy().nor().y, -oobToHole.cpy().nor().x).scl(8f));
            complexAvatar.setLinearVelocity(newVelocity);
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
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i < winPlanets.size; i++) {
            float d = (((mouse.x - winPlanets.get(i).getX()) * (mouse.x - winPlanets.get(i).getX())) + ((mouse.y - winPlanets.get(i).getY()) * (mouse.y - winPlanets.get(i).getY())));
            if ((Math.sqrt(d) < winPlanets.get(i).getRadius()*1.5f)) {
                if (lastHoverPlanet[i] == false) {
                    if (gameState == 1) {
                        winPlanets.get(i).setTexture(LOSE_TEXTURES[i][1]);
                    }
                    else {
                        winPlanets.get(i).setTexture(WIN_TEXTURES[i][1]);
                    }
                    winPlanets.get(i).setRadius(winPlanets.get(i).getRadius() * 1.1f);
                    winPlanets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                }
                lastHoverPlanet[i] = true;
            } else if (lastHoverPlanet[i] == true) {
                if (gameState == 1) {
                    winPlanets.get(i).setTexture(LOSE_TEXTURES[i][0]);
                }
                else {
                    winPlanets.get(i).setTexture(WIN_TEXTURES[i][0]);
                }
                winPlanets.get(i).setRadius(winPlanets.get(i).getRadius() * 1 / 1.1f);
                winPlanets.get(i).scalePicScale(new Vector2(1 / 1.2f, 1 / 1.2f));
                lastHoverPlanet[i] = false;
            }
        }

    }

    public boolean clickScreenSwitch() {
        if (gameState != 0) {
            Vector2 mouse = InputController.getInstance().getCursor(canvas);
            for (int i = 0; i < winPlanets.size; i++) {
                float d = (mouse.x-winPlanets.get(i).getX())*(mouse.x-winPlanets.get(i).getX())+(mouse.y-winPlanets.get(i).getY())*(mouse.y-winPlanets.get(i).getY());
                if (Math.sqrt(d) < winPlanets.get(i).getRadius()) {
                    int code = 0;
                    if (gameState == 2) {
                        if (i == 0) {code = 2000;}
                        if (i == 1) {code = 2002;}
                        if (i == 2) {reset(); return false;}
                        if (i == 3) {code = 2003;}
                    }
                    if (gameState == 1) {
                        if (i == 0) {code = 1000;}
                        if (i == 1) {code = 1002;}
                        if (i == 2) {reset(); return false;}
                    }
                    listener.exitScreen(this, code);
                    return true;
                }
            }

        }
        return false;
    }

    public void switchState(int state) {
        float centerX = canvas.getWidth()/80;
        float centerY = canvas.getHeight()/80;
        float[][] WIN_PLANETS = {
                {centerX - 7f, centerY - 2f},  // EXIT
                {centerX - 3.5f, centerY - 4.5f},   // LEVELS
                {centerX + 3.5f, centerY - 4.5f},    // REPLAY LEVEL
                {centerX + 7f, centerY - 2f},    // NEXT LEVEL
        };
        float[][] LOSE_PLANETS = {
                {centerX - 7f, centerY - 3.3f},  // EXIT
                {centerX, centerY - 5.4f},   // LEVELS
                {centerX + 7f, centerY - 3.3f},    // RETRY
        };
        gameState = state;
        complexAvatar.setPosition(centerX, centerY);
        complexAvatar.setLinearVelocity(new Vector2(0f, 0f));
        complexAvatar.setAngle((float) Math.PI/2);
        complexAvatar.setDirection(new Vector2(0,1));
        complexAvatar.setRadius(1.5f);
        if (state == 2) {
            for (int i = 0; i < WIN_PLANETS.length; i++) {
                PlanetModel obj;
                obj = new PlanetModel(WIN_PLANETS[i][0], WIN_PLANETS[i][1], 1.2f, 3f);
                obj.setName("win");
                obj.setTexture(WIN_TEXTURES[i][0]);
                obj.setBodyType(BodyDef.BodyType.StaticBody);
                obj.setDensity(BASIC_DENSITY);
                obj.setFriction(BASIC_FRICTION);
                obj.setRestitution(BASIC_RESTITUTION);
                obj.setDrawScale(scale);
                obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
                addObject(obj);
                winPlanets.add(obj);


            }
            complexAvatar.setFlying(true);
//            complexAvatar.setRadius((float) Math.sqrt((oldOobMass + suckSpeed / 3) / Math.PI));
//            complexAvatar.scalePicScale(new Vector2(complexAvatar.getRadius() / oldAvatarRad, complexAvatar.getRadius() / oldAvatarRad));

        }
        else if (state == 1) {
            for (int i = 0; i < LOSE_PLANETS.length; i++) {
                PlanetModel obj;
                obj = new PlanetModel(LOSE_PLANETS[i][0], LOSE_PLANETS[i][1], 1.2f, 3f);
                obj.setName("lose");
                obj.setTexture(LOSE_TEXTURES[i][0]);
                obj.setBodyType(BodyDef.BodyType.StaticBody);
                obj.setDensity(BASIC_DENSITY);
                obj.setFriction(BASIC_FRICTION);
                obj.setRestitution(BASIC_RESTITUTION);
                obj.setDrawScale(scale);
                obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
                addObject(obj);
                winPlanets.add(obj);
            }
            complexAvatar.setHurting(true);
        }
        complexAvatar.setBodyType(BodyDef.BodyType.StaticBody);
        for(Obstacle b : complexAvatar.getBodies()) {
            b.setBodyType(BodyDef.BodyType.StaticBody);
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
        InputController.getInstance().isPlay(play);
        if (InputController.getInstance().debugJustPressed()) {
            setDebug(!isDebug());
        }
        if (gameState == 0) {
            scrollText();
            if(converted == 0 && LEVEL.equals("Mother")){
                for (ShipModel sh: ships) {
                    if (sh.getType() == 2) {
                        screenLockShip(sh);
                    }
                }
            }
            else {
                if(shipLock){
                    shipLock = false;
                    InputController.getInstance().setCenterCamera(true);
                }
                scrollStars(stars,LG_SPEED, LG_SCROLL_SPEED,backgroundLG,LG_S_X_START,LG_S_Y_START);
                scrollStars(med_stars,MED_SPEED, MED_SCROLL_SPEED, backgroundMED,med_X_START,med_Y_START);
                scrollStars(white_stars,WHITE_SPEED, WHITE_SCROLL_SPEED,backgroundWHITESTAR,white_X_START,white_Y_START);
                if(InputController.getInstance().getCenterCamera())
                    scrollScreen();
                else {
                    unlockedScrollScreen();
                }
            }
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
                jumpTime = 0;
                // smallestRad is the vector from current planet to Oob's center
                smallestRad = new Vector2(complexAvatar.getX() - currentPlanet.getX(), complexAvatar.getY() - currentPlanet.getY());
                if(smallestRad.len() > complexAvatar.getRadius() + currentPlanet.getRadius() + 1f)
                    complexAvatar.setPosition(currentPlanet.getPosition().x, currentPlanet.getPosition().cpy().y + currentPlanet.getRadius() + complexAvatar.getRadius() + 1f);
                if (smallestRad.len() > 0.9f * complexAvatar.getRadius()) {
                    if (smallestRad.len() > complexAvatar.getRadius() + currentPlanet.getRadius())
                        complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17 - 2 * complexAvatar.getMass()));
                    complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17 - complexAvatar.getMass()));
                }
                //determines mouse or keyboard controls
                if (!currentPlanet.isDying() && currentPlanet.getRadius() < MIN_RADIUS && (currentPlanet.getType()!=2&&currentPlanet.getType()!=3)) {
                    currentPlanet.setDying(true);
                    //currentPlanet.setTexture(dying_P_Texture);
                    currentPlanet.set_WARN_sheet(WARN_Sheet);
                    currentPlanet.createWARNtex();
                    planet_explosion.add(currentPlanet);
                }

                //determines Oob's face on planet
                if (currentPlanet.getType() == 0f) {
                    complexAvatar.setGrowing(true);
                } else if (currentPlanet.getType() == 1f) {
                    complexAvatar.setCommand(true);
                } else if (currentPlanet.getType() == 2f) {
                    complexAvatar.setHurting(true);
                } else if (currentPlanet.getType() == 3f) {
                    complexAvatar.setNormal(true);
                }

                //makes sure Oob's face is correctly oriented
                complexAvatar.setAngle((float)Math.atan2(smallestRad.y, smallestRad.x));

                if (screenSwitch()) {
                    return;
                }
                groundPlayerControls();
                if (!play) {
                    hover();
                }

                //forced jump
                if (currentPlanet.getRadius() < DEATH_RADIUS) {
                    currentPlanet.setExploding(true);
                    currentPlanet.set_sheet(EXP_Sheet);
                    currentPlanet.createEXPtex();
                    forceJump = true;
                    jump = true;
                    SoundController.getInstance().play(EXPLOSION_SOUND, EXPLOSION_SOUND, false, EFFECT_VOLUME);
                }
                // checking to make sure he doesn't go inside out
                complexAvatar.checkForInsideOut(currentPlanet.getRadius() + complexAvatar.getRadius(), vecToCenter);

                    if (jump) {
                        if (!play) {
                            if (clickScreenSwitch()) {
                                return;
                            }
                        }
                        if (converted > 0 || !LEVEL.equals("Mother")) {
                            jump();
                        }
                    } else {
                        rad = currentPlanet.getRadius();
                        float Oob_rad = complexAvatar.getRadius();
                        if (rad > DEATH_RADIUS && ((Oob_rad < OOB_MAX_RADIUS && (currentPlanet.getType() == 0f))
                                        || (currentPlanet.getType() == 1f))) {
                            siphonPlanet();
                        } else if (Oob_rad >= OOB_MAX_RADIUS) {
                            complexAvatar.setMax(true);
                            if(currentPlanet.isDying())
                                decreasePlanetMass();
                        }
                        if (currentPlanet.getType() == 2f) {
                            changeMass(POISON);
                        }
                        if (complexAvatar.getLinearVelocity().len() < 7) {
                            moveAroundPlanet();
                        }
                        if (smallestRad.len() < currentPlanet.getRadius() + 0.1f) {
                            lastPlanet = currentPlanet;
                            currentPlanet = null;
                        }
                    }
            } else if (currentPlanet == null) { // we’re floating in space
                complexAvatar.setFlying(true);
                jumpTime++;
                if ((jumpTime > 300) & !play) {
                    reset();
                }
//                if ((jumpTime > 600) & play) {
//                    gameState = 1;
//                }
                if (!play) {
                    gravity();
                }
                if (blackHoleWarp) {
                    handleBlackHole();
                }
                airPlayerControls();
                if (jump && complexAvatar.getRadius() > OOB_DEATH_RADIUS + 0.1 && adjustCooldown == 0) {
                    float expRad = complexAvatar.getRadius() / 2;
                    Vector2 massLoc = complexAvatar.getPosition().cpy().sub(launchVec.cpy().nor().scl(complexAvatar.getRadius() + expRad + 1f));
                    WheelObstacle expulsion = new WheelObstacle(massLoc.x, massLoc.y, expRad);
                    expulsion.setGravityScale(0);
                    expulsion.setName("expulsion");
                    expulsion.setDrawScale(scale);
                    expulsion.setTexture(expulsion_Texture);
                    expulsion.scalePicScale(new Vector2(expRad * 1.3f, expRad * 1.3f));
                    addObject(expulsion);
                    expulsion.setLinearVelocity(launchVec.cpy().nor().scl(-30));
                    changeMass(-expulsion.getMass() / 2);
                    Vector2 velocityChange = launchVec.cpy().nor().scl(1.5f * (complexAvatar.getLinearVelocity().len() + expulsion.getLinearVelocity().len()) / complexAvatar.getMass());
                    complexAvatar.setLinearVelocity(complexAvatar.getLinearVelocity().cpy().set(velocityChange.cpy().scl(complexAvatar.getRadius() / 2f)));
                    complexAvatar.setDirection(complexAvatar.getLinearVelocity().cpy().set(velocityChange.cpy().scl(complexAvatar.getRadius() / 2f)));
                    adjustCooldown = 30;
                    clicks++;
                    SoundController.getInstance().play(EXPULSION_SOUND, EXPULSION_SOUND, false, 1.0f);
                }

                // this part is to account for the weird drifting we're getting
                // basically just scales the direction it should be going to the length of the vector it wants it to go
                if(complexAvatar.getDirection() != null && !blackHoleWarp) {
                    Vector2 projBOnA = complexAvatar.getDirection().cpy().scl(complexAvatar.getLinearVelocity().len());
                    complexAvatar.setLinearVelocity(projBOnA);
                }

                if (complexAvatar.getCenter().getLinearVelocity().len() < 6)
                    complexAvatar.setLinearVelocity(complexAvatar.getCenter().getLinearVelocity().cpy().nor().scl(6));
                findPlanet();
                complexAvatar.checkForInsideOut(0, Vector2.Zero);
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
                        SoundController.getInstance().play(EXPLOSION_SOUND, EXPLOSION_SOUND, false, EFFECT_VOLUME);
                    }
                }
                if (planet_explosion.get(0).get_EXP_ST() > -1) {
                    if ((planet_explosion.get(0).get_EXP_ST()) >= (planet_explosion.get(0).get_EXP_anim().getAnimationDuration())) {
                        if (planet_explosion.get(0).getType() == 1f) {
                            for (ShipModel sh : planet_explosion.get(0).getShips()) {
                                sh.setExploding(true);
                                ship_explosion.add(sh);
                                sh.set_EXP_ST(0f);
                            }
                            commandPlanets.removeValue(planet_explosion.get(0), true);
                        }
                        planet_explosion.get(0).markRemoved(true);
                        planets.removeValue(planet_explosion.get(0), true);
                        planet_explosion.removeIndex(0);
                    }
                }
            }
            if (ships.size>0) {
                for (ShipModel sh : ships) {
                    if (sh.isExploding()) {
                        if (sh.get_EXP_ST() >= SHIP_EXP_Animation.getAnimationDuration()) {
                            sh.setExploding(false);
                            sh.markRemoved(true);
                            ship_explosion.removeValue(sh, true);
                            ships.removeValue(sh, true);
                            aiController.removeShip(sh);
                        }
                    }
                }
            }
            for (Joint j : complexAvatar.getOuterJoints()) {
                Vector2 dist = j.getAnchorA().cpy().sub(j.getAnchorB());
                if (dist.len() > 3 * ((DistanceJoint) j).getLength())
                    complexAvatar.getForceVec().scl(0.5f);
            }
            complexAvatar.applyForce();
            complexAvatar.resetForceVec();
            // If we use sound, we must remember this.
            SoundController.getInstance().update();
            loopCommandPlanets();
            loopConvertPlanet();
            aiController.update(dt);
            shootBullet();
            if (adjustCooldown > 0) {
                adjustCooldown--;
            }
            if (commandPlanets.size == 0 && play) {
                // Won the level
                InputController.getInstance().setCenterCamera(true);
                messageCounter = 0;
                switchState(2);
                for (ShipModel sh : ships) {
                    if (sh.getName().equals("ship")) {
                        sh.setExploding(true);
                        if (!ship_explosion.contains(sh, false)) {
                            ship_explosion.add(sh);
                        }
                        sh.set_EXP_ST(0f);
                    }
                }
            }
            if (complexAvatar.getRadius() <= OOB_DEATH_RADIUS) {
                // Lost the level
                InputController.getInstance().setCenterCamera(true);
                messageCounter = 0;
                switchState(1);
            }
        }
        else {
            if (ships.size>0) {
                for (ShipModel sh : ships) {
                    if (sh.isExploding()) {
                        if (sh.get_EXP_ST() >= SHIP_EXP_Animation.getAnimationDuration()) {
                            sh.setExploding(false);
                            sh.markRemoved(true);
                            ship_explosion.removeValue(sh, true);
                            ships.removeValue(sh, true);
                            aiController.removeShip(sh);
                        }
                    }
                }
            }
            hover();
            if (InputController.getInstance().getMouseJump()) {
                if (clickScreenSwitch()) {
                    return;
                }
            }
            if (gameState == 2) {complexAvatar.setLinearVelocity(new Vector2(0f, 0f));}
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
                }
                else if (bd2.getName().equals("ship")) {
                    if (((ShipModel)bd2).getType() == 2) {
                        siphonShip((ShipModel)bd2);
                        if (bd2.getMass() < 2.5f) {
                            bd2.markRemoved(true);
                            aiController.removeShip((ShipModel)bd2);
                        }
                    }
                    else {
                        bd2.markRemoved(true);
                        aiController.removeShip((ShipModel)bd2);
                    }
                }
                else if(bd2.getName().equals("expulsion")) {
                    bd2.markRemoved(true);
                    changeMass(((WheelObstacle)bd2).getMass()/16);
                }
                else if(bd2.getName().equals("black hole")) {
                    playerControl = false;
                    outHole = ((BlackHoleModel)bd2).getPair();
                    inHole = ((BlackHoleModel)bd2);
                    if(inHole.getRadius() != 0f)
                        inHole.setOldRadius(inHole.getRadius());
                    inHole.setRadius(0f);
                    if(outHole.getRadius() != 0f)
                        outHole.setOldRadius(outHole.getRadius());
                    outHole.setRadius(0);
                    complexAvatar.setLinearVelocity(Vector2.Zero);
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
                }
                else if (bd1.getName().equals("ship")) {
                    if (((ShipModel)bd1).getType() == 2) {
                        siphonShip((ShipModel)bd1);
                        if (bd1.getMass() < 2.5f) {
                            bd1.markRemoved(true);
                            aiController.removeShip((ShipModel)bd1);
                        }
                    }
                    else {
                        bd1.markRemoved(true);
                        aiController.removeShip((ShipModel)bd1);
                    }
                }
                else if(bd1.getName().equals("expulsion")) {
                    bd1.markRemoved(true);
                    changeMass(((WheelObstacle)bd1).getMass()/16);
                }
                else if(bd1.getName().equals("black hole")) {
                    playerControl = false;
                    outHole = ((BlackHoleModel)bd1).getPair();
                    inHole = ((BlackHoleModel)bd1);
                    complexAvatar.setLinearVelocity(Vector2.Zero);
                    if(inHole.getRadius() != 0f)
                        inHole.setOldRadius(inHole.getRadius());
                    inHole.setRadius(0f);
                    if(outHole.getRadius() != 0f)
                        outHole.setOldRadius(outHole.getRadius());
                    outHole.setRadius(0);
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
            if(bd1.getName().equals("asteroid") && bd2.getName().equals("Oob")){
                //LOSE
                listener.exitScreen(this, 0);
                InputController.getInstance().setCenterCamera(true);
                //reset();
            }
            else if(bd2.getName().equals("asteroid") && bd1.getName().equals("Oob")){
                //LOSE
                listener.exitScreen(this, 0);
                InputController.getInstance().setCenterCamera(true);
                //reset();
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

    public void resize() {
        try {
            complexAvatar.getVertexBatch().getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        catch(Exception e) {
        }
    }

    public void drawBackground(){
        canvas.clear();
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        canvas.begin();
//        float x = 255 - ((float) jumpTime/2);
//        Color Tint;
//        if (gameState == 0) {Tint = Color.WHITE;}
//        else {Tint = Color.GRAY;}

        canvas.draw(backgroundMAIN, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.draw(backgroundSM, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());

        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(0).x, white_stars.get(0).y,   white_stars.get(1).x, white_stars.get(1).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(2).x, white_stars.get(2).y,   white_stars.get(3).x, white_stars.get(3).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(4).x, white_stars.get(4).y,   white_stars.get(5).x, white_stars.get(5).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(6).x, white_stars.get(6).y,   white_stars.get(7).x, white_stars.get(7).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(8).x, white_stars.get(8).y,   white_stars.get(9).x, white_stars.get(9).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(10).x, white_stars.get(10).y,   white_stars.get(11).x, white_stars.get(11).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(12).x, white_stars.get(12).y,   white_stars.get(13).x, white_stars.get(13).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(14).x, white_stars.get(14).y,   white_stars.get(15).x, white_stars.get(15).y);
        canvas.draw(backgroundWHITESTAR, Color.WHITE, white_stars.get(16).x, white_stars.get(16).y,   white_stars.get(17).x, white_stars.get(17).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(0).x, med_stars.get(0).y,   med_stars.get(1).x, med_stars.get(1).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(2).x, med_stars.get(2).y,   med_stars.get(3).x, med_stars.get(3).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(4).x, med_stars.get(4).y,   med_stars.get(5).x, med_stars.get(5).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(6).x, med_stars.get(6).y,   med_stars.get(7).x, med_stars.get(7).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(8).x, med_stars.get(8).y,   med_stars.get(9).x, med_stars.get(9).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(10).x, med_stars.get(10).y,   med_stars.get(11).x, med_stars.get(11).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(12).x, med_stars.get(12).y,   med_stars.get(13).x, med_stars.get(13).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(14).x, med_stars.get(14).y,   med_stars.get(15).x, med_stars.get(15).y);
        canvas.draw(backgroundMED, Color.WHITE, med_stars.get(16).x, med_stars.get(16).y,   med_stars.get(17).x, med_stars.get(17).y);

        canvas.draw(backgroundLG, Color.WHITE, stars.get(0).x, stars.get(0).y,   stars.get(1).x, stars.get(1).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(2).x, stars.get(2).y,   stars.get(3).x, stars.get(3).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(4).x, stars.get(4).y,   stars.get(5).x, stars.get(5).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(6).x, stars.get(6).y,   stars.get(7).x, stars.get(7).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(8).x, stars.get(8).y,   stars.get(9).x, stars.get(9).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(10).x, stars.get(10).y,   stars.get(11).x, stars.get(11).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(12).x, stars.get(12).y,   stars.get(13).x, stars.get(13).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(14).x, stars.get(14).y,   stars.get(15).x, stars.get(15).y);
        canvas.draw(backgroundLG, Color.WHITE, stars.get(16).x, stars.get(16).y,   stars.get(17).x, stars.get(17).y);



        canvas.end();
    }

    public void drawObjects(){
        Color Tint = Color.WHITE;
        if (gameState != 0) {
            Tint = new Color(0, 0, 0, 0.6f);
        }
        for (Obstacle obj : objects) {

            if (obj.getName().equals("ComplexOob") && gameState != 2) {

                TextureRegion currentFrame;

                if ( ((ComplexOobModel) obj).isNormal()) {
                    currentFrame =  ((ComplexOobModel) obj).get_Normal_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(8,7);
                }
                else if ( ((ComplexOobModel) obj).isGrowing()) {
                    currentFrame =  ((ComplexOobModel) obj).get_Growing_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }
                else if ( blackHoleWarp ) {
                    currentFrame =  ((ComplexOobModel) obj).get_Teleporting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(3,2);
                }
                else if ( ((ComplexOobModel) obj).isFlying() || gameState == 2) {
                    currentFrame =  ((ComplexOobModel) obj).get_Flying_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(40,1);
                }
                else if ( ((ComplexOobModel) obj).isHurting() || gameState == 1)  {
                    currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(25,1);
                }
                else {
                    currentFrame =  ((ComplexOobModel) obj).get_Command_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }
                if (((ComplexOobModel) obj).get_Shot_Cooldown() > 0 && gameState == 0 ) {
                    currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(25,1);
                    complexAvatar.decCooldown();
                }
                if ( ((ComplexOobModel) obj).isDying() && gameState == 0) {
                    currentFrame =  ((ComplexOobModel) obj).get_Dying_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }

                if ( ((ComplexOobModel) obj).isMax() && gameState == 0) {
                    currentFrame =  ((ComplexOobModel) obj).get_Max_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }

                ((ComplexOobModel) obj).setTexture(currentFrame);
                ((ComplexOobModel) obj).draw();

                canvas.begin();
                obj.draw(canvas);
                canvas.end();
            }

            if (obj.getName().equals("planet") && ((PlanetModel) obj).getType() == 2 ) {
                // Get current frame of animation for the current stateTime
                TextureRegion currentFrame = sunAnimation.getKeyFrame(stateTime, true);
                canvas.begin();
                ((PlanetModel) obj).setTexture(currentFrame);
                if (LEVEL=="Sun" ) {
                    ((PlanetModel) obj).setTexture(shrink_P_Texture);
                }
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
            if (obj.getName().equals("ship") && !((ShipModel) obj).isExploding()) {
                // Get current frame of animation for the current stateTime
                TextureRegion currentFrame;
                if (((ShipModel) obj).getType()==2) {
                    currentFrame=MOTHERSHIP_Animation.getKeyFrame(stateTime, true);
                }
                else if (((ShipModel) obj).getType()==1) {
                    currentFrame=G_SHIP_Animation.getKeyFrame(stateTime, true);
                }
                else {
                    currentFrame=SHIP_Animation.getKeyFrame(stateTime, true);
                }
                canvas.begin();
                ((ShipModel) obj).setTexture(currentFrame);
                obj.draw(canvas);
                canvas.end();
            }
            if (obj.getName().equals("ship") && ((ShipModel) obj).isExploding()) {
                // Get current frame of animation for the current stateTime
                ((ShipModel) obj).update_EXP_ST();
                TextureRegion currentFrame = SHIP_EXP_Animation.getKeyFrame(((ShipModel) obj).get_EXP_ST(), false);
                canvas.begin();
                ((ShipModel) obj).setTexture(currentFrame);
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
            else {
                // This is for level select. ignore it
                if (!obj.getName().equals("prev") && !obj.getName().equals("next")) {
                    canvas.begin();
                    obj.draw(canvas);
                    canvas.end();
                }
            }
        }
        if (play && gameState==0) {
            canvas.begin();
            canvas.draw(reset_Texture, Color.WHITE, 10, .9f*canvas.getHeight() , canvas.getWidth() / 10, canvas.getHeight() / 12);
            canvas.draw(pause_Texture, Color.WHITE, canvas.getWidth() - canvas.getWidth() / 10 - 10, .9f*canvas.getHeight(), canvas.getWidth() / 10, canvas.getHeight() / 12);
            canvas.drawText("Level " +LV_NUMBER , displayFont, 10, 60);

            Vector2 toCommand = new Vector2();
            for(PlanetModel c : commandPlanets) {
                toCommand.set(c.getX() - canvas.getWidth()/80, c.getY() - canvas.getHeight()/80);
                if(Math.abs(toCommand.x) > canvas.getWidth()/80 + c.getRadius() || Math.abs(toCommand.y) > canvas.getHeight()/80 + c.getRadius()) {
                    toCommand.nor();
                    if(((float)canvas.getWidth()/80-0.5f)/Math.abs(toCommand.x) < ((float)canvas.getHeight()/80-0.5f)/Math.abs(toCommand.y))
                        toCommand.scl(((float) canvas.getWidth() / 80 - 0.5f) / Math.abs(toCommand.x));
                    else
                        toCommand.scl(((float)canvas.getHeight()/80-0.5f)/Math.abs(toCommand.y));
                    toCommand.scl(40);
                    float angle = (float)Math.atan2(toCommand.y, toCommand.x);
                    toCommand.add(canvas.getWidth() / 2, canvas.getHeight() / 2);
                    canvas.draw(arrow_Texture, Color.RED, arrow_Texture.getRegionWidth() , arrow_Texture.getRegionHeight() , toCommand.x, toCommand.y, angle - (float) Math.PI / 2, 1f / 10, 1f / 10);

                }
            }
            for(PlanetModel c : convertPlanets){
                toCommand.set(c.getX() - canvas.getWidth()/80, c.getY() - canvas.getHeight()/80);
                if(Math.abs(toCommand.x) > canvas.getWidth()/80 + c.getRadius() || Math.abs(toCommand.y) > canvas.getHeight()/80 + c.getRadius()) {
                    toCommand.nor();
                    if (((float) canvas.getWidth() / 80 - 0.5f) / Math.abs(toCommand.x) < ((float) canvas.getHeight() / 80 - 0.5f) / Math.abs(toCommand.y))
                        toCommand.scl(((float) canvas.getWidth() / 80 - 0.5f) / Math.abs(toCommand.x));
                    else
                        toCommand.scl(((float) canvas.getHeight() / 80 - 0.5f) / Math.abs(toCommand.y));
                    toCommand.scl(40);
                    float angle = (float) Math.atan2(toCommand.y, toCommand.x);
                    toCommand.add(canvas.getWidth() / 2, canvas.getHeight() / 2);
                    if (c.getConvert() != 0) {
                        //the arrow should be new
                        if ((c.getConvert() / 15) % 2 == 1) {
                            canvas.draw(arrow_Texture, Color.RED, arrow_Texture.getRegionWidth(), arrow_Texture.getRegionHeight(), toCommand.x, toCommand.y, angle - (float) Math.PI / 2, 1f / 10, 1f / 10);
                        } else {
                            canvas.draw(arrow_Texture, Color.WHITE, arrow_Texture.getRegionWidth(), arrow_Texture.getRegionHeight(), toCommand.x, toCommand.y, angle - (float) Math.PI / 2, 1f / 10, 1f / 10);
                        }
                    }
                }
            }
            canvas.end();
        }
        if (isDebug()) {
            canvas.beginDebug();
            for (Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }
        canvas.begin();
        if (gameState == 1) {
            canvas.draw(backgroundMAIN, Tint, 0, 0,canvas.getWidth(),canvas.getHeight());
            canvas.draw(backgroundSM, Tint, 0, 0, canvas.getWidth(), canvas.getHeight());
            for (Obstacle o: objects) {
                if (o.getName() == "lose") {
                    o.draw(canvas);
                }
            }
            canvas.draw(lost_text, Color.WHITE, canvas.getWidth()/2 - (lost_text.getRegionWidth()/2) + 10,canvas.getHeight()/2 + 60, lost_text.getRegionWidth(), lost_text.getRegionHeight());
        }
        if (gameState == 2) {
            canvas.draw(backgroundMAIN, Tint, 0, 0,canvas.getWidth(),canvas.getHeight());
            canvas.draw(backgroundSM, Tint, 0, 0, canvas.getWidth(), canvas.getHeight());
            for (Obstacle o: objects) {
                if (o.getName() == "win") {
                    o.draw(canvas);
                }
            }
            GlyphLayout layout = new GlyphLayout();
            layout.setText(displayFont, "Clicks Used:" + clicks);
            canvas.drawText("Clicks Used:" + clicks, displayFont, canvas.getWidth()/2-layout.width/2, canvas.getHeight()/2+layout.height*3);
            canvas.draw(win_text, Color.WHITE, canvas.getWidth()/2 - (win_text.getRegionWidth()/2),canvas.getHeight()*0.8f, win_text.getRegionWidth(), win_text.getRegionHeight());
        }
        canvas.end();
        for (Obstacle obj: objects) {
            if (obj.getName().equals("ComplexOob") && (gameState == 2||gameState==1)) {

                TextureRegion currentFrame;

                if ( ((ComplexOobModel) obj).isNormal()) {
                    currentFrame =  ((ComplexOobModel) obj).get_Normal_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(8,7);
                }
                else if ( ((ComplexOobModel) obj).isGrowing()) {
                    currentFrame =  ((ComplexOobModel) obj).get_Growing_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }
                else if ( blackHoleWarp ) {
                    currentFrame =  ((ComplexOobModel) obj).get_Teleporting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(3,2);
                }
                else if ( ((ComplexOobModel) obj).isFlying() || gameState == 2) {
                    currentFrame =  ((ComplexOobModel) obj).get_Flying_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(40,1);
                }
                else if ( ((ComplexOobModel) obj).isHurting() || gameState == 1)  {
                    currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(25,1);
                }
                else {
                    currentFrame =  ((ComplexOobModel) obj).get_Command_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }
                if (((ComplexOobModel) obj).get_Shot_Cooldown() > 0 && gameState == 0 ) {
                    currentFrame =  ((ComplexOobModel) obj).get_Hurting_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(25,1);
                    complexAvatar.decCooldown();
                }
                if ( ((ComplexOobModel) obj).isDying() && gameState == 0) {
                    currentFrame =  ((ComplexOobModel) obj).get_Dying_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }

                if ( ((ComplexOobModel) obj).isMax() && gameState == 0) {
                    currentFrame =  ((ComplexOobModel) obj).get_Max_anim().getKeyFrame(stateTime, true);
                    ((ComplexOobModel) obj).setAnimDimensions(4,3);
                }

                ((ComplexOobModel) obj).setTexture(currentFrame);
                ((ComplexOobModel) obj).draw();

                canvas.begin();
                obj.draw(canvas);
                canvas.end();
            }
        }
    }

    /**
     * Draw the physics objects together with foreground and background
     *
     * This is completely overridden to support custom background and foreground art.
     *
     * @param dt Timing values from parent loop
     */
    public void draw(float dt) {

        drawBackground();
        drawObjects();
    }

}