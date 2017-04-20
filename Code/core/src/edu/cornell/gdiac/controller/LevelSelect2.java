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

public class LevelSelect2 extends MainMenu2 {

    private static final String TITLE = "space/menus/levels.png";

    private static final float[][] PLANETS = {
            {23f, 5.5f, 1.1f, 3f},   // MAIN MENU
            {10f, 5.5f, 1.1f, 3f},  //SETTINGS
            {15.0f, 10f, 1.1f, 3f},    // LEVEL 1
    };

    public LevelSelect2() {
        super();
    }

    public void loadContent(AssetManager manager) {
        if (platformAssetState != AssetState.LOADING) {
            return;
        }

        avatarTexture = createTexture(manager,OOB_FILE,false);
        expulsion_Texture = createTexture(manager,EXPULSION_TEXTURE, false);

        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);

        main_Menu_Texture = createTexture(manager,MAIN_MENU_TEXTURE,false);
        main_Menu_Hover_Texture = createTexture(manager,MAIN_MENU_HOVER_TEXTURE,false);
        settings_Texture = createTexture(manager,SETTINGS_TEXTURE, false);
        settings_Hover_Texture = createTexture(manager,SETTINGS_HOVER_TEXTURE, false);
        level_1_Texture = createTexture(manager,NEUTRAL_P,false);       // TODO: FIX THIS
        level_1_hover_Texture = createTexture(manager,NEUTRAL_P,false);

        TEXTURES[0][0] = main_Menu_Texture;
        TEXTURES[0][1] = main_Menu_Hover_Texture;
        TEXTURES[1][0] = settings_Texture;
        TEXTURES[1][1] = settings_Hover_Texture;
        TEXTURES[2][0] = level_1_Texture;
        TEXTURES[2][1] = level_1_hover_Texture;

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
}
//package edu.cornell.gdiac.controller;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.math.Plane;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.JsonValue;
//import com.badlogic.gdx.utils.JsonReader;
//import com.badlogic.gdx.utils.ObjectSet;
//import edu.cornell.gdiac.model.*;
//import edu.cornell.gdiac.model.obstacle.Obstacle;
//import edu.cornell.gdiac.model.obstacle.WheelObstacle;
//import edu.cornell.gdiac.util.FilmStrip;
//import edu.cornell.gdiac.util.ScreenListener;
//import edu.cornell.gdiac.util.SoundController;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.Files;
//import com.badlogic.gdx.utils.ObjectMap;
//
//import javax.swing.plaf.TextUI;
//
///**
// * Created by Jaiveer on 4/12/17.
// */
//
//public class LevelSelect2 extends MainMenu2 {
//
//    private static final String TITLE = "space/menus/levels.png";
//
//    private static final float[][] PLANETS = {
//            {23f, 5.5f, 1.1f, 3f},   // MAIN MENU
//            {10f, 5.5f, 1.1f, 3f},  //SETTINGS
//            {15.0f, 10f, 1.1f, 3f},    // LEVEL 1
//    };
//
//    public LevelSelect2() {
//        super();
//    }
//
//    public void loadContent(AssetManager manager) {
//        if (platformAssetState != AssetState.LOADING) {
//            return;
//        }
//
//        avatarTexture = createTexture(manager,OOB_FILE,false);
//        expulsion_Texture = createTexture(manager,EXPULSION_TEXTURE, false);
//
//        neutral_P_Texture = createTexture(manager,NEUTRAL_P,false);
//
//        main_Menu_Texture = createTexture(manager,MAIN_MENU_TEXTURE,false);
//        main_Menu_Hover_Texture = createTexture(manager,MAIN_MENU_HOVER_TEXTURE,false);
//        settings_Texture = createTexture(manager,SETTINGS_TEXTURE, false);
//        settings_Hover_Texture = createTexture(manager,SETTINGS_HOVER_TEXTURE, false);
//        level_1_Texture = createTexture(manager,NEUTRAL_P,false);       // TODO: FIX THIS
//        level_1_hover_Texture = createTexture(manager,NEUTRAL_P,false);
//
//        TEXTURES[0][0] = main_Menu_Texture;
//        TEXTURES[0][1] = main_Menu_Hover_Texture;
//        TEXTURES[1][0] = settings_Texture;
//        TEXTURES[1][1] = settings_Hover_Texture;
//        TEXTURES[2][0] = level_1_Texture;
//        TEXTURES[2][1] = level_1_hover_Texture;
//
//        backgroundMAIN = createTexture(manager,BACKG_FILE_MAIN,false);
//        backgroundWHITESTAR = createTexture(manager,BACKG_FILE_WHITE_STAR,false);
//        backgroundLG = createTexture(manager,BACKG_FILE_LG_STAR,false);
//        backgroundMED = createTexture(manager,BACKG_FILE_MED_STAR,false);
//        backgroundSM = createTexture(manager,BACKG_FILE_SM_STAR,false);
//        titleTexture = createTexture(manager,TITLE, true);
//
//        ship_texture = createTexture(manager, SHIP_TEXTURE, false);
//        bullet_texture = createTexture(manager, BULLET_TEXTURE, false);
//
//        SoundController sounds = SoundController.getInstance();
//        sounds.allocate(manager, JUMP_FILE);
//        sounds.allocate(manager, PEW_FILE);
//        sounds.allocate(manager, POP_FILE);
//        super.loadContent(manager);
//        platformAssetState = AssetState.COMPLETE;
//    }
//}
