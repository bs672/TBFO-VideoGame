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

public class PauseMenu2 extends PlayMode {

    protected static float[][] PLANETS = {
            {23.0f, 4f, 1.3f, 3f},  // MAIN MENU
            {23, 10f, 1.3f, 3f}, //SETTINGS
            {10.0f, 3f, 1.3f, 3f},    //LEVEL SELECT
            {15.0f, 8.5f, 1.5f, 3f}   //PLAY

    };

    protected static final TextureRegion[][] TEXTURES = new TextureRegion[PLANETS.length][2];

    public PauseMenu2() {
        super();
    }

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
        pauseTitleTexture = createTexture(manager,PAUSETITLE, true);

        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

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
        lastHoverPlanet = new boolean[PLANETS.length];
    }

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

        currentPlanet = planets.get(0); //The first planet is always the starting planet
        complexAvatar = new ComplexOobModel(OOB_POS.x, OOB_POS.y, OOB_RADIUS, 50);
        complexAvatar.setDrawScale(scale);
        complexAvatar.setTexture(avatarTexture);
        complexAvatar.setBodyType(BodyDef.BodyType.DynamicBody);
        complexAvatar.setSensor(true);
        complexAvatar.setName("ComplexOob");
        complexAvatar.scalePicScale(new Vector2(.4f*OOB_RADIUS, .4f*OOB_RADIUS));
        addObject(complexAvatar);

        aiController = new AIController(ships, planets, commandPlanets, complexAvatar, scale);
    }

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
            Vector2 mouse = InputController.getInstance().getCursor();
            for (int i = 0; i < PLANETS.length; i++) {
                float d = (mouse.x - planets.get(i).getX()) * (mouse.x - planets.get(i).getX()) + (mouse.y - planets.get(i).getY()) * (mouse.y - planets.get(i).getY());
                if ((Math.sqrt(d) < planets.get(i).getRadius())) {
                    if (lastHoverPlanet[i] == false) {
                        planets.get(i).setTexture(TEXTURES[i][1]);
                        planets.get(i).setRadius(planets.get(i).getRadius()*1.1f);
                        planets.get(i).scalePicScale(new Vector2(1.2f, 1.2f));
                    }
                    lastHoverPlanet[i] = true;
                }
                else if (lastHoverPlanet[i] == true) {
                    planets.get(i).setTexture(TEXTURES[i][0]);
                    planets.get(i).setRadius(planets.get(i).getRadius()*1/1.1f);
                    planets.get(i).scalePicScale(new Vector2(1/1.2f, 1/1.2f));
                    lastHoverPlanet[i] = false;
                }
            }
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
            jumpTime++;
            if (jumpTime > 300) {
                reset();
            }
            // Gravity
            Vector2 tempVec1 = new Vector2(0, 0);
            for (int i = 0; i < planets.size; i++) {
                //if (planets.get(i) != lastPlanet) {
                tempVec1.set(complexAvatar.getPosition().cpy().sub(planets.get(i).getPosition()));
                float r = Math.abs(tempVec1.len() - planets.get(i).getRadius());
                float k = complexAvatar.getMass()*planets.get(i).getMass();
                complexAvatar.addToForceVec(new Vector2(-tempVec1.x * 1f*k/(r*r), -tempVec1.y * 1f*k/(r*r)));
                //}
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

    public void draw(float dt) {
        canvas.clear();

        canvas.begin();

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
        canvas.draw(pauseTitleTexture, Color.WHITE, canvas.getWidth() / 2 - (pauseTitleTexture.getRegionWidth() / 2) + 50, 400, canvas.getWidth() / 2, canvas.getHeight() / 2);
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