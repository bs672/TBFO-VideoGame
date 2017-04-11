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
    private static final float MOTHER_MASS = 0.6f;

    private static final float DAMPING = 5.0f;
    private static final float MOVE_SPEED = 0.1f;
    private Vector2 oldPosition = new Vector2();
    private boolean inOrbit;
    private boolean aggroed;
    private int firingCooldown;
    private int burstCount=10;
    private int delay;
    private float range;
    private float mass;

    private static final float AGGRO_RANGE = 8.0f;


    //TODO type of the ship
    //Type 0 is default, Type 1 is Guard Ship, Type 2 is Mothership
    private static float type;

    public Vector2 getOldPosition() {return oldPosition; }

    public void setOldPosition(Vector2 v) {oldPosition.set(v); }

    public void setType(float val){type = val;}

    public float getType(){return type;}

    public void setInOrbit(boolean b) {inOrbit = b;}

    public boolean getInOrbit() {return inOrbit; }

    public void setAggroed(boolean b) {aggroed = b; }

    public boolean getAggroed() {return aggroed; }

    public void setAggroRange(float f){range = f;}

    public float getAggroRange() {return range; }

    public void setCooldown(int c) {firingCooldown = c; }

    public int getCooldown() {return firingCooldown; }

    public void decCooldown() {firingCooldown--; }

    public void setBurstCount(int b) {burstCount = b; }

    public int getBurstCount() {return burstCount; }

    public void decBurstCount() {burstCount --; }

    public void setDelay(int d) {delay = d; }

    public int getDelay() {return delay; }

    public void decDelay() {delay --; }

    public float getMoveSpeed() {return MOVE_SPEED; }

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
    public ShipModel(float x, float y, float width, float height, float t) {
        super(x,y,width,height);
        setFixedRotation(true);

        type = t;
        if (t == 2) {
            mass = MOTHER_MASS;
        }
        setName("ship");
        firingCooldown = 0;
        delay=0;
    }

    //Creates a Ship with predetermined width and height at given position.
    public ShipModel(float x, float y, float t){
        super(x, y, WIDTH, HEIGHT);
        setFixedRotation(true);
        type = t;
        if (t == 2) {
            mass = MOTHER_MASS;
        }
        setName("ship");
        firingCooldown = 0;
        delay=0;
        range = AGGRO_RANGE;
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }
}
