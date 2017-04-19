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
public class PauseMenu extends WorldController implements ContactListener {

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
    private static final String SETTINGS_TEXTURE = "space/menus/settings_planet.png";
    private static final String SETTINGS_HOVER_TEXTURE = "space/menus/settings_planet_hover.png";
    private static final String LEVELS_TEXTURE = "space/menus/levels_planet.png";
    private static final String LEVELS_HOVER_TEXTURE = "space/menus/levels_planet_hover.png";
    private static final String RESUME_TEXTURE = "space/menus/resume_planet.png";
    private static final String RESUME_HOVER_TEXTURE = "space/menus/resume_planet_hover.png";

    // Animator sun = new Animator(8,1,.33f,"space/planets/sunAnim.png");

    //Animator sun = new Animator();

    private boolean[] lastInPlanet;

    // Animation test2= new Animation(20,)

    /** The texture file for the planets */
    private static final String COMMAND_P = "space/planets/command.png";

    /** The texture file for the planets */
    private static final String NEUTRAL_P = "space/planets/neutral.png";

    /** The texture file for the planets */
    private static final String PLAY_TEXTURE = "space/menus/play_planet.png";

    private static final String TITLE = "space/menus/pause.png";

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
    private static Vector2 OOB_POS = new Vector2(16f, 12f);
    /** Oob's initial radius */
    private  float OOB_RADIUS = .8f; //0.2 scale in overlap2d is standard

    private static final float SIPHON = 0.02f;

    private static final float POISON = -0.02f;

    private static final float MIN_RADIUS = 1f;

    private static final float DEATH_RADIUS = MIN_RADIUS*2/3;

    private static final float OOB_DEATH_RADIUS = 0.56f;

    private static final float EPSILON = 0.1f;

    private static final int THRESHOLD = 4;

    private Vector2 lastmouse = new Vector2(0, 0);

    //control = 0 is keyboard, control = 1 is mouse
    private int control = 1;
    /** Texture asset for character avatar */
    private TextureRegion avatarTexture;

    /** Timer for resetting the menu */
    private int jumpTime = 0;

    /** Planet texture */
    private TextureRegion neutral_P_Texture;

    /** Settings texture */
    private TextureRegion settings_Texture;

    /** Settings Hover texture */
    private TextureRegion settings_Hover_Texture;

    /** Main Menu texture */
    private TextureRegion main_Menu_Texture;

    /** Main Menu Hover texture */
    private TextureRegion main_Menu_Hover_Texture;

    /** Levels texture */
    private TextureRegion levels_Texture;

    /** Levels Hover texture */
    private TextureRegion levels_Hover_Texture;

    /** Resume texture */
    private TextureRegion resume_Texture;

    /** Resume Hover texture */
    private TextureRegion resume_Hover_Texture;

    /** Play texture */
    private TextureRegion play_Texture;

    /** Expulsion texture */
    private TextureRegion expulsion_Texture;


    /** Background texture */
    private TextureRegion backgroundMAIN;
    private TextureRegion backgroundWHITESTAR;
    private TextureRegion backgroundLG;
    private TextureRegion backgroundMED;
    private TextureRegion backgroundSM;
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

        manager.load(PLAY_TEXTURE, Texture.class);
        assets.add(PLAY_TEXTURE);

        manager.load(SETTINGS_TEXTURE, Texture.class);
        assets.add(SETTINGS_TEXTURE);

        manager.load(SETTINGS_HOVER_TEXTURE, Texture.class);
        assets.add(SETTINGS_HOVER_TEXTURE);

        manager.load(MAIN_MENU_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_TEXTURE);

        manager.load(MAIN_MENU_HOVER_TEXTURE, Texture.class);
        assets.add(MAIN_MENU_HOVER_TEXTURE);

        manager.load(LEVELS_TEXTURE, Texture.class);
        assets.add(LEVELS_TEXTURE);

        manager.load(LEVELS_HOVER_TEXTURE, Texture.class);
        assets.add(LEVELS_HOVER_TEXTURE);

        manager.load(RESUME_TEXTURE, Texture.class);
        assets.add(RESUME_TEXTURE);

        manager.load(RESUME_HOVER_TEXTURE, Texture.class);
        assets.add(RESUME_HOVER_TEXTURE);

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
        play_Texture = createTexture(manager,PLAY_TEXTURE, false);

        main_Menu_Texture = createTexture(manager,MAIN_MENU_TEXTURE,false);
        main_Menu_Hover_Texture = createTexture(manager,MAIN_MENU_HOVER_TEXTURE,false);
        settings_Texture = createTexture(manager,SETTINGS_TEXTURE, false);
        settings_Hover_Texture = createTexture(manager,SETTINGS_HOVER_TEXTURE, false);
        levels_Texture = createTexture(manager,LEVELS_TEXTURE, false);
        levels_Hover_Texture = createTexture(manager,LEVELS_HOVER_TEXTURE, false);
        resume_Texture = createTexture(manager,RESUME_TEXTURE, false);
        resume_Hover_Texture = createTexture(manager,RESUME_HOVER_TEXTURE, false);


        TEXTURES[0][0] = main_Menu_Texture;
        TEXTURES[0][1] = main_Menu_Hover_Texture;
        TEXTURES[1][0] = settings_Texture;
        TEXTURES[1][1] = settings_Hover_Texture;
        TEXTURES[2][0] = levels_Texture;
        TEXTURES[2][1] = levels_Hover_Texture;
        TEXTURES[3][0] = resume_Texture;
        TEXTURES[3][1] = resume_Hover_Texture;


        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);
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
            {23.0f, 4f, 1.3f, 3f},   // MAIN MENU
            {23, 10f, 1.3f, 3f},  //SETTINGS
            {10.0f, 3f, 1.3f, 3f},    //LEVEL SELECT
            {15.0f, 8.5f, 1.5f, 3f},   //PLAY

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
    public PauseMenu() {
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
        launchVec = new Vector2();
        returnToPlanetTimer = 0;
        jumpTime = 0;
        lastInPlanet = new boolean[PLANETS.length];
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        justLoaded = true;
        jumpTime = 0;
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
            obj.setTexture(TEXTURES[ii][0]);
            addObject(obj);
            planets.add(obj);
        }

        currentPlanet = planets.get(3); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(currentPlanet.getX()+canvas.getWidth()/80f - 0.8f, currentPlanet.getY() + currentPlanet.getRadius()*2+canvas.getHeight()/80f, OOB_RADIUS, 50);
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
        System.out.println(smallestRad.len());
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
        if (InputController.getInstance().didPause()) {
            listener.exitScreen(this, 100);
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
        if (InputController.getInstance().didPause()) {
            listener.exitScreen(this, 100);
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
            jumpTime = 0;
            smallestRad = new Vector2(complexAvatar.getX() - currentPlanet.getX(), complexAvatar.getY() - currentPlanet.getY());
            complexAvatar.addToForceVec(smallestRad.cpy().nor().scl(-17-complexAvatar.getMass()));
            //determines mouse or keyboard controls

            for (int i = 0; i <= 2; i++) {
                if (currentPlanet == planets.get(i)) {
                    listener.exitScreen(this, i);
                    return;
                }
            }

            groundPlayerControls();
            // Hover effects
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
                for (int i = 3; i <= 3; i++) {
                    float d = (mouse.x-planets.get(i).getX())*(mouse.x-planets.get(i).getX())+(mouse.y-planets.get(i).getY())*(mouse.y-planets.get(i).getY());
                    if (Math.sqrt(d) < planets.get(i).getRadius()) {
                        listener.exitScreen(this, 100);
                        return;
                    }
                }
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
            jumpTime++;
            if (jumpTime > 180) {
                reset();
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

        canvas.draw(backgroundMAIN, Color.GRAY, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundSM, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundMED, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundWHITESTAR, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundLG, Color.WHITE, LG_S_X, LG_S_Y,backgroundLG.getRegionWidth(),backgroundLG.getRegionHeight());
        canvas.draw(titleTexture, Color.WHITE, canvas.getWidth() / 2 - (titleTexture.getRegionWidth() / 2) + 50, 550, titleTexture.getRegionWidth(), titleTexture.getRegionHeight());
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
    }
}





