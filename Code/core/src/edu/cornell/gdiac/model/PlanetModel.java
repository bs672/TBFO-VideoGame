package edu.cornell.gdiac.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.model.obstacle.WheelObstacle;

/**
 * Created by jchan_000 on 3/11/2017.
 */
public class PlanetModel extends WheelObstacle{

    // Physics constants
    /** The density of the character */
    private static final float PLANET_DENSITY = 1.0f;

    private static final int SPAWN_COOLDOWN = 300;

    private int totalSpawned;

    //Type 0 is normal planet
    //Type 1 is command planet
    //Type 2 is poison planet
    //Type 3 is neutral planet

    private float type;

    private int becomingCommand = 0;

    private boolean dying;

    private boolean exploding;

    private int spawnCooldown = SPAWN_COOLDOWN;

    public boolean canSpawn(){
        if (type==1f) {
            if(spawnCooldown>0) {
                spawnCooldown--;
                return false;
            }
            else{
                spawnCooldown= SPAWN_COOLDOWN;
                return true;
            }
        }
        else{
            return false;
        }
    }


    public boolean isDying() {return dying;}

    public boolean isExploding() {return exploding;}

    public void setType(float val){type = val;}

    public float getType(){return type;}

    public void convert(){becomingCommand++;}

    public int getConvert(){return becomingCommand;}

    public void setDying(boolean bool) {dying = bool;}

    public void setExploding(boolean bool) {exploding = bool;}



    private float WARN_stateTime;

    public void set_WARN_ST(float val){WARN_stateTime = val;}

    public float get_WARN_ST(){return WARN_stateTime;}


    private float EXP_stateTime;

    public void set_ST(float val){EXP_stateTime = val;}

    public float get_ST(){return EXP_stateTime;}



    public void update_WARN_ST(){
        WARN_stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
    }

    public void update_ST(){
        EXP_stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
    }




    private Animation<TextureRegion> WARN_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_WARN_anim(){return WARN_Animation;}


    private Animation<TextureRegion> EXP_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_anim(){return EXP_Animation;}



    private Texture WARN_Sheet;

    public void set_WARN_sheet(Texture val){WARN_Sheet = val;}

    public Texture get_WARN_sheet(){return WARN_Sheet;}


    private Texture EXP_Sheet;

    public void set_sheet(Texture val){EXP_Sheet = val;}

    public Texture get_sheet(){return EXP_Sheet;}




    public void createWARNtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 6, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(WARN_Sheet,
                WARN_Sheet.getWidth() / FRAME_COLS,
                WARN_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        WARN_Animation = new Animation<TextureRegion>(.3f, frames);

        WARN_stateTime=0;
    }

    public void createtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 5, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(EXP_Sheet,
                EXP_Sheet.getWidth() / FRAME_COLS,
                EXP_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        EXP_Animation = new Animation<TextureRegion>(.1f, frames);

        EXP_stateTime=0;
    }





    /**
     * Creates a new planet at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param radius		The object radius in physics units
     * @param t         The type of the planet
     */
    public PlanetModel(float x, float y, float radius, float t) {
        super(x,y,radius);
        setDensity(PLANET_DENSITY);
        setFixedRotation(true);
        this.type = t;
        dying=false;
        becomingCommand = 0;

        setName("Planet");
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }


    public boolean equals(Obstacle o) {
        return getX() == o.getX() && getY() == o.getY();
    }

}
