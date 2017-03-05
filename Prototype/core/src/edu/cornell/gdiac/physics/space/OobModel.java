package edu.cornell.gdiac.physics.space;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

/**
 * Created by Matt Loughney on 2/28/2017.
 */
public class OobModel extends WheelObstacle {

    // Physics constants
    /** The density of the character */
    private static final float OOB_DENSITY = 1.0f;
    /** The factor to multiply by the input */
    private static final float OOB_FORCE = 20.0f;
    /** The amount to slow the character down */
    private static final float OOB_DAMPING = 10.0f;
    /** The Oob is a slippery one */
    private static final float OOB_FRICTION = 0.0f;
    /** The maximum character speed */
    private static final float OOB_MAXSPEED = 5.0f;
    /** minimum speed while floating through space */
    private static final float OOB_MINSPEED = 1.0f;
    /** The impulse for the character jump */
    private static final float OOB_JUMP = 5.5f;
    /** Cooldown (in animation frames) for jumping */
    private static final int JUMP_COOLDOWN = 30;
    /** Identifier to allow us to track the sensor in ContactListener */
    private static final String SENSOR_NAME = "OobGroundSensor";

    /** The current horizontal movement of the character */
    private Vector2 movement;
    /** How long until we can jump again */
    private int jumpCooldown;
    /** Whether we are actively jumping */
    private boolean isJumping;
    /** Whether our feet are on the ground */
    private boolean isGrounded;
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private CircleShape sensorShape;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();

    /** health */
    private float mass;
    /** radial vector from center of planet */
    private Vector2 radDirection;

    public float getMass() {
        return mass;
    }

    public void setMass(float value) {
        mass = value;
    }

    /**
     * Returns left/right movement of this character.
     *
     * This is the result of input times Oob force.
     *
     * @return left/right movement of this character.
     */
    public Vector2 getMovement() {
        return movement;
    }

    /**
     * Sets vector movement of this character.
     *
     * This is the result of input times Oob force.
     *
     * @param value left/right movement of this character.
     */
    public void setMovement(Vector2 value) {
        movement = value;
    }

    /**
     * Returns true if the Oob is actively jumping.
     *
     * @return true if the Oob is actively jumping.
     */
    public boolean isJumping() {
        return isJumping && jumpCooldown <= 0;
    }

    /**
     * Sets whether the Oob is actively jumping.
     *
     * @param value whether the Oob is actively jumping.
     */
    public void setJumping(boolean value) {
        isJumping = value;
    }

    /**
     * Returns true if the Oob is on the ground.
     *
     * @return true if the Oob is on the ground.
     */
    public boolean isGrounded() {
        return isGrounded;
    }

    /**
     * Sets whether the Oob is on the ground.
     *
     * @param value whether the Oob is on the ground.
     */
    public void setGrounded(boolean value) {
        isGrounded = value;
    }

    /**
     * Returns how much force to apply to get Oob moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the Oob moving
     */
    public float getForce() {
        return OOB_FORCE;
    }

    /**
     * Returns ow hard the brakes are applied to get a Oob to stop moving
     *
     * @return ow hard the brakes are applied to get a Oob to stop moving
     */
    public float getDamping() {
        return OOB_DAMPING;
    }

    /**
     * Returns the upper limit on Oob left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on Oob left-right movement.
     */
    public float getMaxSpeed() {
        return OOB_MAXSPEED;
    }

    /**
     * Returns the name of the ground sensor
     *
     * This is used by ContactListener
     *
     * @return the name of the ground sensor
     */
    public String getSensorName() {
        return SENSOR_NAME;
    }

    /**
     * Creates a new Oob at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param radius		The object radius in physics units
     */
    public OobModel(float radius) {
        this(0,0,radius);
    }

    /**
     * Creates a new Oob avatar at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param radius		The object radius in physics units
     */
    public OobModel(float x, float y, float radius) {
        super(x,y,radius);
        setDensity(OOB_DENSITY);
//        setFriction(OOB_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);

        // Gameplay attributes
        isGrounded = false;

        jumpCooldown = 0;
        setName("Oob");
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        // Ground Sensor
        // -------------
        // We only allow the Oob to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the Oob is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        body.setGravityScale(0);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = OOB_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new CircleShape();
        sensorShape.setRadius(getRadius());
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;
    }


    /**
     * Applies the force to the body of this Oob
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }
        // Don't want to be moving. Damp out player motion
        forceCache.set(-getDamping()*getVX(),-getDamping()*getVY());
        body.applyForce(forceCache,getPosition(),true);

        // Velocity too high, clamp it
        if (movement.len() >= getMaxSpeed())
            setMovement(new Vector2(movement.cpy().nor().scl(OOB_MAXSPEED)));
        forceCache.set(getMovement().x,getMovement().y);
        body.applyLinearImpulse(forceCache,getPosition(),true);

        // Jump!
        if (isJumping()) {
//            forceCache.set(OOB_JUMP*radDirection.x, OOB_JUMP*radDirection.y);
            forceCache.set(0, OOB_JUMP*OOB_FORCE/5);
            body.applyLinearImpulse(forceCache,getPosition(),true);
        }
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Apply cooldowns
        if(InputController.getInstance().getJump())
            isJumping = true;
        else
            isJumping = false;

        super.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1.0f,1.0f);
    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
//        canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }

}
