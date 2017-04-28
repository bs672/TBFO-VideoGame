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
 * Created by Jaiveer on 4/12/17.
 */

public class LevelSelect extends PlayMode {

    protected static final float[][] PLANETS = {
            {15f, 6.5f, 1.1f, 3f},    // BACK
            {10f, 3.5f, 1.1f, 3f},  // MAIN MENU
            {22.8f, 3.5f, 1.1f, 3f},   // SETTINGS

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

    protected static final TextureRegion[][] TEXTURES = new TextureRegion[3][2];
    protected static final TextureRegion[][] LEVELS_TEXTURE_REGIONS = new TextureRegion[9][3];
    protected static int unlocked;

    public LevelSelect(int unlocked) {
        super("MainScene");
        play = false;
        jumpTime = 0;
        lastHoverPlanet = new boolean[PLANETS.length];
        this.unlocked = unlocked;
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
        back_Texture = createTexture(manager, BACK_TEXTURE, false);
        back_Hover_Texture = createTexture(manager, BACK_HOVER_TEXTURE, false);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                LEVELS_TEXTURE_REGIONS[i][j] = createTexture(manager, LEVELS_TEXTURES[i][j], false);
            }
        }

        TEXTURES[0][0] = back_Texture;
        TEXTURES[0][1] = back_Hover_Texture;
        TEXTURES[1][0] = main_Menu_Texture;
        TEXTURES[1][1] = main_Menu_Hover_Texture;
        TEXTURES[2][0] = settings_Texture;
        TEXTURES[2][1] = settings_Hover_Texture;

        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);

        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
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
    }

    protected void populateLevel() {

        // Create Planets
        String pname = "planet";
        for (int ii = 0; ii < 3; ii++) {
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
        for (int ii = 3; ii < unlocked + 3; ii++) {
            PlanetModel obj;
            obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
            obj.setName(pname + ii);
            obj.setTexture(LEVELS_TEXTURE_REGIONS[ii-3][0]);
            addObject(obj);
            planets.add(obj);
        }
        for (int ii = unlocked + 3; ii < PLANETS.length; ii++) {
            PlanetModel obj;
            obj = new PlanetModel(PLANETS[ii][0], PLANETS[ii][1], PLANETS[ii][2], PLANETS[ii][3]);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(BASIC_DENSITY);
            obj.setFriction(BASIC_FRICTION);
            obj.setRestitution(BASIC_RESTITUTION);
            obj.setDrawScale(scale);
            obj.scalePicScale(new Vector2(.2f * obj.getRadius(), .2f * obj.getRadius()));
            obj.setName(pname + ii);
            obj.setTexture(LEVELS_TEXTURE_REGIONS[ii-3][2]);
            addObject(obj);
            planets.add(obj);
        }

        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(OOB_POS.x, OOB_POS.y, OOB_RADIUS/2);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS/2, .4f*OOB_RADIUS/2));
        addObject(complexAvatar);

        loadAnim();
        setBG();
        set_med_BG();
        set_white_BG();

        titlecoord.set(   3*canvas.getWidth() /9 , 2*canvas.getHeight()/8  );
        titlesize.set(  3*canvas.getWidth()/9, 2*canvas.getHeight()/8  );



        text.add (titlecoord);
        text.add (titlesize);

        aiController = new AIController(ships, planets, commandPlanets, complexAvatar, scale);
    }

    public void setUnlocked(int newUnlock) {
        if (newUnlock > unlocked) {
            unlocked = newUnlock;
            for (int i = 3; i < unlocked + 3; i++) {
                planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[i-3][0]);
            }
        }
    }

    public boolean screenSwitch() {
        for (int i = 1; i < 3; i++) {
            if (currentPlanet == planets.get(i)) {
                listener.exitScreen(this, i-1);
                return true;
            }
        }
        for (int i = 3; i < unlocked + 3; i++) {
            if (currentPlanet == planets.get(i)) {
                listener.exitScreen(this, i);
                return true;
            }
        }
        for (int i = unlocked + 3; i < PLANETS.length; i++)
            if (currentPlanet == planets.get(i)) {
                reset();
                return false;
            }
        return false;
    }

    public boolean clickScreenSwitch() {
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i <= 0; i++) {
            float d = (mouse.x-planets.get(i).getX())*(mouse.x-planets.get(i).getX())+(mouse.y-planets.get(i).getY())*(mouse.y-planets.get(i).getY());
            if (Math.sqrt(d) < planets.get(i).getRadius()) {
                listener.exitScreen(this, 2);
                return true;
            }
        }
        return false;
    }

    public void hover() {
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i < unlocked + 3; i++) {
            float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
            if ((Math.sqrt(d) < planets.get(i).getRadius())) {
                if (lastHoverPlanet[i] == false) {
                    if (i < TEXTURES.length) {
                        planets.get(i).setTexture(TEXTURES[i][1]);
                        planets.get(i).setRadius(planets.get(i).getRadius() * 1.1f);
                        planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                    }
                    else {
                        planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[i-3][1]);
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
                else {
                    planets.get(i).setTexture(LEVELS_TEXTURE_REGIONS[i-3][0]);
                    planets.get(i).setRadius(planets.get(i).getRadius() * 1 / 1.1f);
                    planets.get(i).scalePicScale(new Vector2(1 / 1.2f, 1 / 1.2f));
                    lastHoverPlanet[i] = false;
                }
            }
        }
    }

    public void draw(float dt) {
        super.drawBackground();


        canvas.begin();
        canvas.draw(levelsTitleTexture, Color.WHITE, text.get(0).x, text.get(0).y,   text.get(1).x, text.get(1).y);
        //canvas.draw(levelsTitleTexture, Color.WHITE, 3*canvas.getWidth() /9 , 2*canvas.getHeight()/8, 3*canvas.getWidth()/9, 2*canvas.getHeight()/8);
        canvas.end();
        super.drawObjects();
    }
}