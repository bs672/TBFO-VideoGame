package edu.cornell.gdiac.model;

import edu.cornell.gdiac.model.obstacle.WheelObstacle;

/**
 * Created by jchan on 4/23/2017.
 */
public class AsteroidModel extends WheelObstacle{

    private static final float ASTEROID_DENSITY = 1.0f;

    public AsteroidModel(float x, float y, float radius) {
            super(x,y,radius);
            setDensity(ASTEROID_DENSITY);
            setFixedRotation(true);
            setName("Asteroid");
        }
}
