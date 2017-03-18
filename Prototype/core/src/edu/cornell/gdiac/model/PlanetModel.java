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

    //TODO type of the planet
    //Type 0 is default
    private static int type;

    public void setType(int val){type = val;}

    public int getType(){return type;}

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
    public PlanetModel(float x, float y, float radius, int t) {
        super(x,y,radius);
        setDensity(PLANET_DENSITY);
        setFixedRotation(true);
        type = t;

        setName("Planet");
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }


    public boolean equals(Obstacle o) {
        return getX() == o.getX() && getY() == o.getY();
    }

}
