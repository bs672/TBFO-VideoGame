package edu.cornell.gdiac.controller;

/**
 * Created by nsterling4 on 4/7/17.
 */



import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Animator  {




//    // Constant rows and columns of the sprite sheet
//    private static final int FRAME_COLS = 8, FRAME_ROWS = 1;
//
//    // Objects used
//    Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
//    Texture walkSheet;
//    SpriteBatch spriteBatch;
//
//    // A variable for tracking elapsed time for the animation
//    float stateTime;
//
//
//    public void create() {
//
//        // Load the sprite sheet as a Texture
//        walkSheet = new Texture(Gdx.files.internal("space/planets/sunAnim.png"));
//
//        // Use the split utility method to create a 2D array of TextureRegions. This is
//        // possible because this sprite sheet contains frames of equal size and they are
//        // all aligned.
//        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
//                walkSheet.getWidth() / FRAME_COLS,
//                walkSheet.getHeight() / FRAME_ROWS);
//
//        // Place the regions into a 1D array in the correct order, starting from the top
//        // left, going across first. The Animation constructor requires a 1D array.
//        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
//        int index = 0;
//        for (int i = 0; i < FRAME_ROWS; i++) {
//            for (int j = 0; j < FRAME_COLS; j++) {
//                walkFrames[index++] = tmp[i][j];
//            }
//        }
//
//        // Initialize the Animation with the frame interval and array of frames
//        walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);
//
//        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
//        // time to 0
//        spriteBatch = new SpriteBatch();
//        stateTime = 0f;
//    }
//
//    public void render() {
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
//        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
//
//        // Get current frame of animation for the current stateTime
//        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
//        spriteBatch.begin();
//        spriteBatch.draw(currentFrame, 50, 50); // Draw current frame at (50, 50)
//        spriteBatch.end();
//    }
//
//
//    public void dispose() { // SpriteBatches and Textures must always be disposed
//        spriteBatch.dispose();
//        walkSheet.dispose();
//    }
























    // Constant rows and columns of the sprite sheet
    private static int FRAME_COLS;
    private static int FRAME_ROWS;
    private static String spritePath;
    private static float frameRate;


    // Objects used
    Animation<TextureRegion> spriteAnimation; // Must declare frame type (TextureRegion)
    Texture spriteSheet;
    SpriteBatch spriteBatch;

    // A variable for tracking elapsed time for the animation
    float stateTime;




    public void setCols(int val){FRAME_COLS = val;}

    public float getCols(){return FRAME_COLS;}


    public void setRows(int val){FRAME_ROWS = val;}

    public float getRows(){return FRAME_ROWS;}


    public void setRate(float val){frameRate = val;}

    public float getRate(){return frameRate;}


    public void setSpritePath(String path){spritePath = path;}

    public String getSpritePath(){return spritePath;}



    public Animator (int columns, int rows, float framerate, String filePath) {
        setCols(columns);
        setRows(rows);
        setRate(framerate);
        setSpritePath(filePath);
    }


    public void create() {

        // Load the sprite sheet as a Texture
        spriteSheet = new Texture(Gdx.files.internal(spritePath));



        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / FRAME_COLS,
                spriteSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        spriteAnimation = new Animation<TextureRegion>(frameRate, walkFrames);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }


    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = spriteAnimation.getKeyFrame(stateTime, true);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, 50, 50); // Draw current frame at (50, 50)
        spriteBatch.end();
    }


    public void dispose() { // SpriteBatches and Textures must always be disposed
        spriteBatch.dispose();
        spriteSheet.dispose();
    }








    /**
     * Called when the Screen is resized.
     *
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        // IGNORE FOR NOW
    }

    /**
     * Called when the Screen is paused.
     * <p>
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub
    }
}