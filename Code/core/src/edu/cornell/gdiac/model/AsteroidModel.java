package edu.cornell.gdiac.model;

import edu.cornell.gdiac.model.obstacle.BoxObstacle;

/**
 * Created by jchan on 4/23/2017.
 */
public class AsteroidModel extends BoxObstacle{

    private static final float ASTEROID_DENSITY = 1.0f;

    public AsteroidModel(float x, float y, float width, float height ) {
            super(x,y,width, height);
            setDensity(ASTEROID_DENSITY);
            setFixedRotation(true);
            setName("Asteroid");
        }
}
