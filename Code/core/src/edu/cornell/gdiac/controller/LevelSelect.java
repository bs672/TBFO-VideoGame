package edu.cornell.gdiac.controller;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.model.*;
import edu.cornell.gdiac.model.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.SoundController;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jaiveer on 4/12/17.
 */

public class LevelSelect extends PlayMode {

    protected static final float[][] PLANETS = {
            {12.8f, 15f, 1.1f, 3f},    // NOTHING
            {9.3f, 3.5f, 1.1f, 3f},  // MAIN MENU
            {22.7f, 3.5f, 1.1f, 3f},   // SETTINGS
            {13.7f, 2.2f, 1.1f, 3f},   // PREV LEVELS
            {18.2f, 2.2f, 1.1f, 3f},   // NEXT LEVELS

            {7.8f, 7f, 1.1f, 3f},    // LEVEL 1
            {7.52f, 10.5f, 1.1f, 3f},    // LEVEL 2
            {9.65f, 13.25f, 1.1f, 3f},    // LEVEL 3
            {12.55f, 14.9f, 1.1f, 3f},    // LEVEL 4
            {16f, 15.5f, 1.1f, 3f},    // LEVEL 5
            {19.45f, 14.9f, 1.1f, 3f},    // LEVEL 6
            {22.35f, 13.25f, 1.1f, 3f},    // LEVEL 7
            {24.48f, 10.5f, 1.1f, 3f},    // LEVEL 8
            {24.2f, 7f, 1.1f, 3f},    // LEVEL 9
    };

    protected CapsuleObstacle button1;
    protected Rectangle button2;
    protected TextureRegion back;

    protected static final TextureRegion[][] TEXTURES = new TextureRegion[5][2];
    protected static final TextureRegion[][] LEVELS_TEXTURE_REGIONS = new TextureRegion[27][3];
    protected static int unlocked;
    protected int mode; // 0 = levels 1-9 , 1 = levels 10-18, 2 = levels 19 - 27

    public LevelSelect(int unlocked, int mode) {
        super("MainScene");
        play = false;
        jumpTime = 0;
        lastHoverPlanet = new boolean[PLANETS.length];
        this.unlocked = unlocked;
        this.mode = mode;
    }

    public void loadContent(AssetManager manager) {
        if (platformAssetState != AssetState.LOADING) {
            return;
        }
        expulsion_Texture = createTexture(manager,EXPULSION_TEXTURE, false);
        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);
        main_Menu_Texture = createTexture(manager,MAIN_MENU_TEXTURE,false);
        main_Menu_Hover_Texture = createTexture(manager,MAIN_MENU_HOVER_TEXTURE,false);
        settings_Texture = createTexture(manager,SETTINGS_TEXTURE, false);
        settings_Hover_Texture = createTexture(manager,SETTINGS_HOVER_TEXTURE, false);
        levelsTitleTexture = createTexture(manager,LEVELSTITLE, true);
        next_page_Texture = createTexture(manager, NEXT_PAGE_TEXTURE,false);
        next_page_Hover_Texture = createTexture(manager, NEXT_PAGE_HOVER_TEXTURE,false);
        next_page_Lock_Texture = createTexture(manager, NEXT_PAGE_LOCK_TEXTURE,false);
        prev_page_Texture = createTexture(manager, PREV_PAGE_TEXTURE,false);
        prev_page_Hover_Texture = createTexture(manager, PREV_PAGE_HOVER_TEXTURE,false);
        prev_page_Lock_Texture = createTexture(manager, PREV_PAGE_LOCK_TEXTURE,false);
        purple_P_2_Texture =  createTexture(manager, PURPLE_P_2,false);


        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < 3; j++) {
                LEVELS_TEXTURE_REGIONS[i][j] = createTexture(manager, LEVELS_TEXTURES[i][j], false);
            }
        }

        TEXTURES[0][0] = purple_P_2_Texture;
        TEXTURES[0][1] = purple_P_2_Texture;
        TEXTURES[1][0] = main_Menu_Texture;
        TEXTURES[1][1] = main_Menu_Hover_Texture;
        TEXTURES[2][0] = settings_Texture;
        TEXTURES[2][1] = settings_Hover_Texture;
        TEXTURES[3][0] = prev_page_Texture;
        TEXTURES[3][1] = prev_page_Hover_Texture;
        TEXTURES[4][0] = next_page_Texture;
        TEXTURES[4][1] = next_page_Hover_Texture;

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);

        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_SOUND);
        sounds.allocate(manager, EXPLOSION_SOUND);
        sounds.allocate(manager, MOTHERSHIP_SOUND);
        sounds.allocate(manager, SHOOTING_SOUND);
        sounds.allocate(manager, EXPULSION_SOUND);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

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
        ship_explosion.clear();
        ships.clear();
        text.clear();
        stars.clear();
        white_stars.clear();
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
        jumpTime = 0;
        lastHoverPlanet = new boolean[PLANETS.length];
        play = false;
        switchMode(mode);
    }

    public void changeMass(float massChange){
        //don't lose mass please
    }

    protected void populateLevel() {
        // Create Planets
        String pname = "planet";
        for (int ii = 0; ii < 5; ii++) {
            PlanetModel obj;
            obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
            obj.setName(pname + ii);
            if (ii == 3) {
                obj.setName("prev");
            }
            if (ii == 4) {
                obj.setName("next");
            }
            obj.setTexture(TEXTURES[ii][0]);
            addObject(obj);
            planets.add(obj);
        }

        for (int ii = 5; ii < PLANETS.length; ii++) {
            int temp = ii - 4 + (mode*9);
            if (temp <= unlocked) {
                PlanetModel obj;
                obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
                obj.setBodyType(BodyDef.BodyType.StaticBody);
                obj.setDensity(BASIC_DENSITY);
                obj.setFriction(BASIC_FRICTION);
                obj.setRestitution(BASIC_RESTITUTION);
                obj.setDrawScale(scale);
                obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
                obj.setName(pname + ii);
                obj.setTexture(LEVELS_TEXTURE_REGIONS[temp - 1][0]);
                addObject(obj);
                planets.add(obj);
            }
            else {
                PlanetModel obj;
                obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
                obj.setBodyType(BodyDef.BodyType.StaticBody);
                obj.setDensity(BASIC_DENSITY);
                obj.setFriction(BASIC_FRICTION);
                obj.setRestitution(BASIC_RESTITUTION);
                obj.setDrawScale(scale);
                obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
                obj.setName(pname + ii);
                obj.setTexture(LEVELS_TEXTURE_REGIONS[temp-1][2]);
                addObject(obj);
                planets.add(obj);
            }
        }

//        button1 = new CapsuleObstacle(20f,12f, 1f, 0.5f);
//        button1.setFixedRotation(true);
//        button1.setBodyType(BodyDef.BodyType.StaticBody);
//        button1.setDensity(BASIC_DENSITY);
//        button1.setFriction(BASIC_FRICTION);
//        button1.setRestitution(BASIC_RESTITUTION);
//        button1.setDrawScale(scale);
//        button1.scalePicScale(new Vector2(0.3f, 0.3f));
//        button1.setName("button");
//        button1.setTexture(back);
//        addObject(button1);

//        button2 = new Rectangle(15f, 12f, 2f, 1f);
//        button2.setFixedRotation(true);
//        button2.setBodyType(BodyDef.BodyType.StaticBody);
//        button1.setDensity(BASIC_DENSITY);
//        button1.setFriction(BASIC_FRICTION);
//        button1.setRestitution(BASIC_RESTITUTION);
//        button1.setDrawScale(scale);
//        button1.scalePicScale(new Vector2(0.3f, 0.3f));
//        button1.setName("button");
//        button1.setTexture(back);

        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(25f, 10f, OOB_RADIUS/2);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS/2, .4f*OOB_RADIUS/2));
        addObject(complexAvatar);

        complexAvatar.setPosition(25f, 20f);

        loadAnim();
        setBG();
        set_med_BG();
        set_white_BG();

        titlecoord.set(   (canvas.getWidth() /2)-(levelsTitleTexture.getRegionWidth()*.8f/2) , (canvas.getHeight() /2)-2.7f*(levelsTitleTexture.getRegionHeight()*.8f/2)  );
        titlesize.set(  levelsTitleTexture.getRegionWidth()*.8f, levelsTitleTexture.getRegionHeight()*.8f );

        text.add (titlecoord);
        text.add (titlesize);

        aiController = new AIController(ships, planets, blackHoles, commandPlanets, complexAvatar, scale);
    }

    public void screenScroll(){
        if (currentPlanet!=null){

        }
    }

    public void unlockedScrollScreen(){
        //let's not move
    }

    public void scrollStars(Array<Vector2> starArray, float speed, float scrollspeed,TextureRegion background, int Xstart, int Ystart) {
        for (int i = 0; i < starArray.size; i += 2) {
            starArray.get(i).x += (vecToCenter.x) * speed;
            starArray.get(i).y += (vecToCenter.y) * speed;
        }
        if (starArray.get(0).x > background.getRegionWidth() || starArray.get(0).x < -background.getRegionWidth()) {
            float Ydelt = starArray.get(0).y - Ystart;
            starArray.clear();
            if (starArray == stars) {
                setBG();
            } else if (starArray == med_stars) {
                set_med_BG();
            } else if (starArray == white_stars) {
                set_white_BG();
            }
            for (int i = 0; i < starArray.size; i += 2) {
                starArray.get(i).y += Ydelt;
            }
        }
        if (starArray.get(0).y > background.getRegionHeight() || starArray.get(0).y < -background.getRegionHeight()) {
            float Xdelt = starArray.get(0).x - Xstart;
            starArray.clear();
            if (starArray == stars) {
                setBG();
            } else if (starArray == med_stars) {
                set_med_BG();
            } else if (starArray == white_stars) {
                set_white_BG();
            }
            for (int i = 0; i < starArray.size; i += 2) {
                starArray.get(i).x += Xdelt;
            }
        }
    }
    public void scrollText(){
        text.get(0).x += (vecToCenter.x);
        text.get(0).y += (vecToCenter.y);
    }


    public void setUnlocked(int newUnlock) {
        if (newUnlock > unlocked) {
            unlocked = newUnlock;
            mode = (unlocked-1)/9;
            switchMode(mode);
        }
    }

    public void switchMode(int m) {
        mode = m;
        for (int i = 5; i < PLANETS.length; i++) {
            int temp = i - 4 + (mode*9);
            if (temp <= unlocked) {
                planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[temp-1][0]);
            }
            else {
                planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[temp-1][2]);
            }
        }
        planets.get(3).setTexture(prev_page_Texture);
        planets.get(4).setTexture(next_page_Texture);
        if (mode == 0){
            planets.get(3).setTexture(prev_page_Lock_Texture);
        }
        else if (mode == 2){
            planets.get(4).setTexture(next_page_Lock_Texture);
        }
    }

    public boolean screenSwitch() {
        for (int i = 1; i < 3; i++) {
            if (currentPlanet == planets.get(i)) {
                listener.exitScreen(this, i-1);
                return true;
            }
        }
            if (currentPlanet == planets.get(3)) {
                mode = Math.max(mode - 1, 0);
                switchMode(mode);
                reset();
                return true;
            }
            if (currentPlanet == planets.get(4)) {
                mode = Math.min(mode + 1, 2);
                switchMode(mode);
                reset();
                return true;
            }
        for (int i = 5; i < planets.size; i++) {
            int temp = i - 4 + (mode * 9);
            if (currentPlanet == planets.get(i)) {
                if (temp <= unlocked) {
                    listener.exitScreen(this, i + (mode * 9) - 2);
                    return true;
                } else {
                    reset();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean clickScreenSwitch() {
//        Vector2 mouse = InputController.getInstance().getCursor(canvas);
//        for (int i = 0; i <= 0; i++) {
//            float d = (mouse.x-planets.get(i).getX())*(mouse.x-planets.get(i).getX())+(mouse.y-planets.get(i).getY())*(mouse.y-planets.get(i).getY());
//            if (Math.sqrt(d) < planets.get(i).getRadius()) {
//                listener.exitScreen(this, 2);
//                return true;
//            }
//        }
//        for (int i = 3; i <= 4; i++) {
//            float d = (mouse.x-planets.get(i).getX())*(mouse.x-planets.get(i).getX())+(mouse.y-planets.get(i).getY())*(mouse.y-planets.get(i).getY());
//            if (Math.sqrt(d) < planets.get(i).getRadius()*1.2f) {
//                if (i == 3) {
//                    mode = Math.max(mode - 1, 0);
//                    switchMode(mode);
//                    return true;
//                }
//                if (i == 4) {
//                    mode = Math.min(mode + 1, 2);
//                    switchMode(mode);
//                    return true;
//                }
//                return true;
//            }
//        }
//        return false;
        return false;
    }

    public void hover() {
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i < PLANETS.length; i++) {
            int temp = i - 4 + (mode*9);
            float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
            if ((Math.sqrt(d) < planets.get(i).getRadius()*1.5f)) {
                if (lastHoverPlanet[i] == false) {
                    if (i < TEXTURES.length) {
                        planets.get(i).setTexture(TEXTURES[i][1]);
                        planets.get(i).setRadius(planets.get(i).getRadius() * 1.1f);
                        planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                    }
                    else if (temp <= unlocked) {
                        planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[temp-1][1]);
                        planets.get(i).setRadius(planets.get(i).getRadius() * 1.1f);
                        planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                    }
                }
                lastHoverPlanet[i] = true;
            } else if (lastHoverPlanet[i] == true) {
                if (i < TEXTURES.length) {
                    planets.get(i).setTexture(TEXTURES[i][0]);
                    planets.get(i).setRadius(planets.get(i).getRadius() * 1 / 1.1f);
                    planets.get(i).scalePicScale(new Vector2(1 / 1.2f, 1 / 1.2f));
                    lastHoverPlanet[i] = false;
                }
                else if (temp <= unlocked) {
                    planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[temp-1][0]);
                    planets.get(i).setRadius(planets.get(i).getRadius() * 1 / 1.1f);
                    planets.get(i).scalePicScale(new Vector2(1 / 1.2f, 1 / 1.2f));
                    lastHoverPlanet[i] = false;
                }
            }
            if (mode == 0) {
                planets.get(3).setTexture(prev_page_Lock_Texture);
            }
            if (mode == 2) {
                planets.get(4).setTexture(next_page_Lock_Texture);
            }
        }
    }

    public void draw(float dt) {
        super.drawBackground();
        canvas.begin();
        canvas.draw(levelsTitleTexture, Color.WHITE, text.get(0).x, text.get(0).y, text.get(1).x, text.get(1).y);
        canvas.end();
        super.drawObjects();
        canvas.begin();
        for (Obstacle obj: objects) {
            if (obj.getName().equals("prev") && mode != 0) {
                obj.draw(canvas);
            }
            if (obj.getName().equals("next") && mode != 2) {
                obj.draw(canvas);
            }
        }
        canvas.end();
    }
}