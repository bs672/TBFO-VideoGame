package edu.cornell.gdiac.model;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.model.obstacle.CapsuleObstacle;

/**
 * Created by jchan_000 on 3/11/2017.
 */
public class ShipModel extends CapsuleObstacle{

    private static final float WIDTH = 0.3f;
    private static final float HEIGHT = 0.6f;

    private static final float DAMPING = 5.0f;
    private static final float SPEED = 3.0f;
    private Vector2 movement;
    private boolean inOrbit;
    private boolean aggroed;
    private int firingCooldown;

    private static final float AGGRO_RANGE = 1.0f;


    //TODO type of the ship
    //Type 0 is default
    private static int type;

    public void setType(int val){type = val;}

    public int getType(){return type;}

    public void setInOrbit(boolean b) {inOrbit = b;}

    public boolean getInOrbit() {return inOrbit; }

    public void setAggroed(boolean b) {aggroed = b; }

    public boolean getAggroed() {return aggroed; }

    public float getAggroRange() {return AGGRO_RANGE; }

    public void setCooldown(int c) {firingCooldown = c; }

    public int getCooldown() {return firingCooldown; }

    public void decCooldown() {firingCooldown++; }

    /**
     * Creates a new ship at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     * @param t         The type of the ship
     */
    public ShipModel(float x, float y, float width, float height, int t) {
        super(x,y,width,height);
        setFixedRotation(true);

        type = t;
        setName("ship");
        firingCooldown = 0;
    }

    //Creates a Ship with predetermined width and height at given position.
    public ShipModel(float x, float y){
        super(x, y, WIDTH, HEIGHT);
        setFixedRotation(true);

        setName("ship");
        firingCooldown = 0;
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }
}
