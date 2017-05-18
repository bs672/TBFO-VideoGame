package edu.cornell.gdiac.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.view.GameCanvas;
import edu.cornell.gdiac.model.obstacle.CapsuleObstacle;

/**
 * Created by jchan_000 on 3/11/2017.
 */
public class ShipModel extends CapsuleObstacle{

    private static final float WIDTH = 0.6f;
    private static final float HEIGHT = 1.1f;

    private static final float G_WIDTH = .9f;
    private static final float G_HEIGHT = 2.1f;

    private static final float M_WIDTH = 3.1f;
    private static final float M_HEIGHT = 3.7f;

    private static final float MOTHER_MASS = 5f;

    private static final float DAMPING = 5.0f;
    private static final float MOVE_SPEED = 0.1f;
    private Vector2 oldPosition = new Vector2();
    private boolean inOrbit;
    private boolean  aggroed;
    private boolean exploding;
    private int firingCooldown;
    private int burstCount=10;
    private int delay;
    private float range;
    private float mass;
    private boolean isConverting;
    private Music music;

    private float orbitDistance;

    private static final float AGGRO_RANGE = 8.0f;


    //TODO type of the ship
    //Type 0 is default, Type 1 is Guard Ship, Type 2 is Mothership
    private float type;

    private float commandSpawn = -1;

    public float getMass() {return  mass;}
    public void setMass(float m) {mass = m;}
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
    public float getOrbitDistance() {return orbitDistance; }
    public void setOrbitDistance(float f) {orbitDistance = f; }
    public float getCommandSpawn(){return commandSpawn;}
    public void setCommandSpawn(float c){commandSpawn = c;}
    public boolean isExploding() {return exploding;}
    public void setExploding(boolean bool) {exploding = bool;}
    public boolean isConverting(){return isConverting;}
    public void setConverting(boolean convert){isConverting = convert;}
    public Music getMusic(){return music;}
    public void setMusic(Music m){music = m;}

    private float EXP_stateTime= -1;

    public void set_EXP_ST(float val){EXP_stateTime = val;}

    public float get_EXP_ST(){return EXP_stateTime;}

    public void update_EXP_ST(){
        EXP_stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
    }

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
        range = AGGRO_RANGE;
        orbitDistance = 3;
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
        orbitDistance = 3;
    }

    //Creates a Ship with predetermined width and height at given position.
    public ShipModel(float x, float y, float t, String s){
        super(x, y, G_WIDTH, G_HEIGHT);
        setFixedRotation(true);
        type = t;
        if (t == 2) {
            mass = MOTHER_MASS;
        }
        setName("ship");
        firingCooldown = 0;
        delay=0;
        range = AGGRO_RANGE;
        orbitDistance = 3;
    }

    //Creates a Ship with predetermined width and height at given position.
    public ShipModel(float x, float y, float t, String s, String d, float cPlanetSpawn){
        super(x, y, M_WIDTH, M_HEIGHT);
        setFixedRotation(true);
        type = t;
        if (t == 2) {
            mass = MOTHER_MASS;
        }
        setName("ship");
        firingCooldown = 0;
        delay=0;
        range = AGGRO_RANGE;
        orbitDistance = 3;
        music = null;
        setCommandSpawn(cPlanetSpawn);
    }


    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
    }





}
