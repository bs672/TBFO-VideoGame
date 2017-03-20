package edu.cornell.gdiac.model;

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

    //Type 0 is normal planet
    //Type 1 is command planet
    //Type 2 is poison planet
    //Type 3 is neutral planet

    private static float type;

    private static boolean dying;

    public boolean isDying() {return dying;}

    public void setType(float val){type = val;}

    public float getType(){return type;}

    public void setDying(boolean bool) {dying = bool;}

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

        setName("Planet");
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }


    public boolean equals(Obstacle o) {
        return getX() == o.getX() && getY() == o.getY();
    }

}
