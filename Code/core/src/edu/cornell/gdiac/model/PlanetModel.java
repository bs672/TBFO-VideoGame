package edu.cornell.gdiac.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public void setType(float val){type = val;}

    public float getType(){return type;}

    public void convert(){becomingCommand++;}

    public int getConvert(){return becomingCommand;}

    public void setDying(boolean bool) {dying = bool;}


    public Animation createtex(Texture sheet, Animation animation) {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 9, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(sheet,
                sheet.getWidth() / FRAME_COLS,
                sheet.getHeight() / FRAME_ROWS);

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
        animation = new Animation<TextureRegion>(.2f, frames);
        return animation;

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
