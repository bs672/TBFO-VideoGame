package edu.cornell.gdiac.model;

import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.model.obstacle.CapsuleObstacle;

/**
 * Created by jchan_000 on 3/11/2017.
 */
public class BulletModel extends CapsuleObstacle{

    private static final float WIDTH = 0.05f;
    private static final float HEIGHT = 0.15f;
    /**
     * Creates a new bullet at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public BulletModel(float x, float y, float width, float height) {
        super(x,y,width,height);
        setFixedRotation(true);

        setName("bullet");
    }

    //Creates a Bullet with predetermined width and height at given position.
    public BulletModel(float x, float y){
        super(x, y, WIDTH, HEIGHT);
        setFixedRotation(true);

        setName("bullet");
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }
}
