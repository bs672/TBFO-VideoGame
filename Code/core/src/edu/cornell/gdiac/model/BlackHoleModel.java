package edu.cornell.gdiac.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.model.obstacle.WheelObstacle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jchan_000 on 3/11/2017.
 */
public class BlackHoleModel extends WheelObstacle{

    // Physics constants
    private static final float PLANET_DENSITY = 1.0f;

    private static final String TEXTURE_PATH = "space/planets/blackHole.png";

    // the black hole paired with this one
    private BlackHoleModel pair;

    // used when we interact with BlackHoleModels
    private float oldRadius;

    private Vector2 outVelocity;

    public void setOutVelocity(Vector2 v) {outVelocity = v;}

    public Vector2 getOutVelocity() {return outVelocity; }

    public void setPair(BlackHoleModel b) {pair = b;}

    public BlackHoleModel getPair() {return pair; }

    public float getOldRadius() {return oldRadius; }

    public void setOldRadius(float f) {oldRadius = f; }

    /**
     * Creates a new planet at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		          Initial x position of the avatar center
     * @param y  		          Initial y position of the avatar center
     * @param radius		      The object radius in physics units
     * @param ov                  The type of the planet
     */
    public BlackHoleModel(float x, float y, float radius, Vector2 ov) {
        super(x,y,radius);
        setDensity(PLANET_DENSITY);
        setFixedRotation(true);
        setRadius(radius);
        setOutVelocity(ov);

        setName("black hole");
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }


    public boolean equals(Obstacle o) {
        return getX() == o.getX() && getY() == o.getY();
    }






}
