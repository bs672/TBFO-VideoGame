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

public class MainMenu extends PlayMode {


    protected static float[][] PLANETS = {
            {0.0f, 3.0f, 1.2f, 3f},   // NOTHING
            {25, 10f, 1.2f, 3f},  //SETTINGS
            {15.0f, 3f, 1.2f, 3f},    //LEVEL SELECT
            {7.0f, 4.5f, 1.2f, 3f},   //PLAY

    };

    protected static final TextureRegion[][] TEXTURES = new TextureRegion[PLANETS.length][2];

    public void loadContent(AssetManager manager) {
        if (platformAssetState != AssetState.LOADING) {
            return;
        }
        expulsion_Texture = createTexture(manager, EXPULSION_TEXTURE, false);

        neutral_P_Texture = createTexture(manager, NEUTRAL_P, false);
        settings_Texture = createTexture(manager, SETTINGS_TEXTURE, false);
        levels_Texture = createTexture(manager, LEVELS_TEXTURE, false);
        play_Texture = createTexture(manager, PLAY_TEXTURE, false);

        settings_Hover_Texture = createTexture(manager, SETTINGS_HOVER_TEXTURE, false);
        levels_Hover_Texture = createTexture(manager, LEVELS_HOVER_TEXTURE, false);
        play_Hover_Texture = createTexture(manager, PLAY_HOVER_TEXTURE, false);

        TEXTURES[0][0] = neutral_P_Texture;
        TEXTURES[0][1] = neutral_P_Texture;
        TEXTURES[1][0] = settings_Texture;
        TEXTURES[1][1] = settings_Hover_Texture;
        TEXTURES[2][0] = levels_Texture;
        TEXTURES[3][0] = play_Texture;
        TEXTURES[2][1] = levels_Hover_Texture;
        TEXTURES[3][1] = play_Hover_Texture;

        backgroundMAIN = createTexture(manager, BACKG_FILE_MAIN, false);
        backgroundWHITESTAR = createTexture(manager, BACKG_FILE_WHITE_STAR, false);
        backgroundLG = createTexture(manager, BACKG_FILE_LG_STAR, false);
        backgroundMED = createTexture(manager, BACKG_FILE_MED_STAR, false);
        backgroundSM = createTexture(manager, BACKG_FILE_SM_STAR, false);
        titleTexture = createTexture(manager, TITLE, true);

        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

    public MainMenu() {
        super();
        play = false;
        jumpTime = 0;
        lastHoverPlanet = new boolean[PLANETS.length];
    }

    public void reset() {
        System.out.println("HERE");
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
        jumpTime = 0;
        lastHoverPlanet = new boolean[PLANETS.length];
        play = false;
        jumpTime = 0;
    }

    protected void populateLevel() {

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

        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(OOB_POS.x, OOB_POS.y, OOB_RADIUS/2, 50);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS/2, .4f*OOB_RADIUS/2));
        addObject(complexAvatar);

        loadAnim();

        aiController = new AIController(ships, planets, commandPlanets, complexAvatar, scale);
    }

    public boolean screenSwitch() {
        for (int i = 1; i <= 2; i++) {
            if (currentPlanet == planets.get(i)) {
                listener.exitScreen(this, i);
                return true;
            }
        }
        if (currentPlanet == planets.get(3)) {
            listener.exitScreen(this, 4);
            return true;
        }
        else return false;
    }

    public void hover() {
        Vector2 mouse = InputController.getInstance().getCursor(canvas);
        for (int i = 0; i < PLANETS.length; i++) {
            float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
            if ((Math.sqrt(d) < planets.get(i).getRadius())) {
                if (lastHoverPlanet[i] == false) {
                    planets.get(i).setTexture(TEXTURES[i][1]);
                    planets.get(i).setRadius(planets.get(i).getRadius() * 1.1f);
                    planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                }
                lastHoverPlanet[i] = true;
            } else if (lastHoverPlanet[i] == true) {
                planets.get(i).setTexture(TEXTURES[i][0]);
                planets.get(i).setRadius(planets.get(i).getRadius() * 1 / 1.1f);
                planets.get(i).scalePicScale(new Vector2(1 / 1.2f, 1 / 1.2f));
                lastHoverPlanet[i] = false;
            }
        }
    }

    public void draw(float dt) {
        super.draw(dt);
        canvas.begin();
        canvas.draw(titleTexture, Color.WHITE, canvas.getWidth() / 2 - (titleTexture.getRegionWidth() / 2) + 50, 400, canvas.getWidth()/2, canvas.getHeight()/2);
        canvas.end();
    }
}
