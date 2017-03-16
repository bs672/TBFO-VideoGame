package edu.cornell.gdiac.physics.space;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.ComplexObstacle;
import edu.cornell.gdiac.physics.obstacle.SimpleObstacle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.graphics.g2d.*;
import edu.cornell.gdiac.physics.obstacle.*;


/**
 * Created by nsterling4 on 3/11/17.
 */

/*
 * RagdollModel.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */



/**
 * A ragdoll whose body parts are boxes connected by joints
 *
 * This class has several bodies connected by joints.  For information on how
 * the joints fit together, see the ragdoll diagram at the start of the class.
 *
 *
 */
public class OobModel2 extends ComplexObstacle {

    /** Indices for the body parts in the bodies array */
    private static final int PART_NONE = -1;
    private static final int PART_BODY = 0;
    private static final int PART_HEAD = 1;
    private static final int PART_LEFT_ARM  = 2;
    private static final int PART_RIGHT_ARM = 3;
    private static final int PART_LEFT_FOREARM  = 4;
    private static final int PART_RIGHT_FOREARM = 5;
    private static final int PART_LEFT_THIGH  = 6;
    private static final int PART_RIGHT_THIGH = 7;
    private static final int PART_LEFT_SHIN  = 8;
    private static final int PART_RIGHT_SHIN = 9;

    /** The number of DISTINCT body parts */
    private static final int BODY_TEXTURE_COUNT = 6;

    /**
     * Returns the texture index for the given body part
     *
     * As some body parts are symmetrical, we reuse textures.
     *
     * @returns the texture index for the given body part
     */
    private static int partToAsset(int part) {
        switch (part) {
            case PART_BODY:
                return 0;
            case PART_HEAD:
                return 1;
            case PART_LEFT_ARM:
            case PART_RIGHT_ARM:
                return 2;
            case PART_LEFT_FOREARM:
            case PART_RIGHT_FOREARM:
                return 3;
            case PART_LEFT_THIGH:
            case PART_RIGHT_THIGH:
                return 4;
            case PART_LEFT_SHIN:
            case PART_RIGHT_SHIN:
                return 5;
            default:
                return -1;
        }
    }

    // Layout of ragdoll
    //
    // o = joint
    //                   ___
    //                  |   |
    //                  |_ _|
    //   ______ ______ ___o___ ______ ______
    //  |______o______o       o______o______|
    //                |       |
    //                |       |
    //                |_______|
    //                | o | o |
    //                |   |   |
    //                |___|___|
    //                | o | o |
    //                |   |   |
    //                |   |   |
    //                |___|___|
    //
    /** Distance between torso center and face center */
    private static final float TORSO_OFFSET   = 3.8f;
    /** Y-distance between torso center and arm center */
    private static final float ARM_YOFFSET    = 1.75f;
    /** X-distance between torso center and arm center */
    private static final float ARM_XOFFSET    = 3.15f;
    /** Distance between center of arm and center of forearm */
    private static final float FOREARM_OFFSET = 2.75f;
    /** X-distance from center of torso to center of leg */
    private static final float THIGH_XOFFSET  = 0.75f;
    /** Y-distance from center of torso to center of thigh */
    private static final float THIGH_YOFFSET  = 3.5f;
    /** Distance between center of thigh and center of shin */
    private static final float SHIN_OFFSET    = 2.75f;
    /** The offset of the bubbler from the head center */
    private Vector2 BUBB_OFF = new Vector2(0.55f,  1.9f);


    /** The density for each body part */
    private static final float DENSITY = 1.0f;


    /** Texture assets for the body parts */
    private TextureRegion[] partTextures;

    /** Cache vector for organizing body parts */
    private Vector2 partCache = new Vector2();

    /**
     * Creates a new ragdoll with its root at the origin.
     *
     * The root is NOT a body part.  It is simply a body that
     * is unconnected to the rest of the object.
     */
    public OobModel2() {
        this(0,0);
    }

    /**
     * Creates a new ragdoll with its head at the given position.
     *
     * @param x  Initial x position of the ragdoll head
     * @param y  Initial y position of the ragdoll head
     */
    public OobModel2(float x, float y) {
        super(x,y);

        float ox = BUBB_OFF.x+x;
        float oy = BUBB_OFF.y+y;
    }

    protected void init() {
        // We do not do anything yet.
        BoxObstacle part;

        // TORSO
        part = makePart(PART_BODY, PART_NONE, getX(), getY());
        part.setFixedRotation(true);

        // HEAD
        makePart(PART_HEAD, PART_BODY, 0, TORSO_OFFSET);

        // ARMS
        makePart(PART_LEFT_ARM, PART_BODY, -ARM_XOFFSET, ARM_YOFFSET);
        part = makePart(PART_RIGHT_ARM, PART_BODY, ARM_XOFFSET, ARM_YOFFSET);
        part.setAngle((float)Math.PI);

        // FOREARMS
        makePart(PART_LEFT_FOREARM, PART_LEFT_ARM, -FOREARM_OFFSET, 0);
        part = makePart(PART_RIGHT_FOREARM, PART_RIGHT_ARM, FOREARM_OFFSET, 0);
        part.setAngle((float)Math.PI);

        // THIGHS
        makePart(PART_LEFT_THIGH, PART_BODY, -THIGH_XOFFSET, -THIGH_YOFFSET);
        makePart(PART_RIGHT_THIGH, PART_BODY, THIGH_XOFFSET, -THIGH_YOFFSET);

        // SHINS
        makePart(PART_LEFT_SHIN,  PART_LEFT_THIGH, 0, -SHIN_OFFSET);
        makePart(PART_RIGHT_SHIN, PART_RIGHT_THIGH, 0, -SHIN_OFFSET);

    }

    /**
     * Sets the drawing scale for this physics object
     *
     * The drawing scale is the number of pixels to draw before Box2D unit. Because
     * mass is a function of area in Box2D, we typically want the physics objects
     * to be small.  So we decouple that scale from the physics object.  However,
     * we must track the scale difference to communicate with the scene graph.
     *
     * We allow for the scaling factor to be non-uniform.
     *
     * @param x  the x-axis scale for this physics object
     * @param y  the y-axis scale for this physics object
     */
    public void setDrawScale(float x, float y) {
        super.setDrawScale(x,y);

        if (partTextures != null && bodies.size == 0) {
            init();
        }
    }

    /**
     * Sets the array of textures for the individual body parts.
     *
     * The array should be BODY_TEXTURE_COUNT in size.
     *
     * @param textures the array of textures for the individual body parts.
     */
    public void setPartTextures(TextureRegion[] textures) {
        assert textures != null && textures.length > BODY_TEXTURE_COUNT : "Texture array is not large enough";

        partTextures = new TextureRegion[BODY_TEXTURE_COUNT];
        System.arraycopy(textures, 0, partTextures, 0, BODY_TEXTURE_COUNT);
        if (bodies.size == 0) {
            init();
        } else {
            for(int ii = 0; ii <= PART_RIGHT_SHIN; ii++) {
                ((SimpleObstacle)bodies.get(ii)).setTexture(partTextures[partToAsset(ii)]);
            }
        }
    }

    /**
     * Returns the array of textures for the individual body parts.
     *
     * Modifying this array will have no affect on the physics objects.
     *
     * @return the array of textures for the individual body parts.
     */
    public TextureRegion[] getPartTextures() {
        return partTextures;
    }



    /**
     * Helper method to make a single body part
     *
     * While it looks like this method "connects" the pieces, it does not really.  It
     * puts them in position to be connected by joints, but they will fall apart unless
     * you make the joints.
     *
     * @param part		The part to make
     * @param connect	The part to connect to
     * @param x 		The x-offset RELATIVE to the connecting part
     * @param y			The y-offset RELATIVE to the connecting part
     *
     * @return the newly created part
     */
    private BoxObstacle makePart(int part, int connect, float x, float y) {
        TextureRegion texture = partTextures[partToAsset(part)];

        partCache.set(x,y);
        if (connect != PART_NONE) {
            partCache.add(bodies.get(connect).getPosition());
        }

        float dwidth  = texture.getRegionWidth()/drawScale.x;
        float dheight = texture.getRegionHeight()/drawScale.y;

        BoxObstacle body = new BoxObstacle(partCache.x, partCache.y, dwidth, dheight);
        body.setDrawScale(drawScale);
        body.setTexture(texture);
        body.setDensity(DENSITY);
        bodies.add(body);
        return body;
    }

    /**
     * Creates the joints for this object.
     *
     * We implement our custom logic here.
     *
     * @param world Box2D world to store joints
     *
     * @return true if object allocation succeeded
     */
    // Layout of ragdoll
    //
    // o = joint
    //                   ___
    //                  |   |
    //                  |_ _|
    //   ______ ______ ___o___ ______ ______
    //  |______o______o       o______o______|
    //                |       |
    //                |       |
    //                |_______|
    //                | o | o |
    //                |   |   |
    //                |___|___|
    //                | o | o |
    //                |   |   |
    //                |   |   |
    //                |___|___|
    //
    protected boolean createJoints(World world) {
        assert bodies.size > 0;

        //#region INSERT CODE HERE
        // Implement all of the Ragdoll Joints here
        // You may add additional methods if you find them useful
        // body, head, left arm, right arm, left forearm, right forearm, left thigh, right thigh, left shin, right shin
        BoxObstacle body = (BoxObstacle)bodies.get(0);
        BoxObstacle head = (BoxObstacle)bodies.get(1);
        BoxObstacle la = (BoxObstacle)bodies.get(2);
        BoxObstacle ra = (BoxObstacle)bodies.get(3);
        BoxObstacle lf = (BoxObstacle)bodies.get(4);
        BoxObstacle rf = (BoxObstacle)bodies.get(5);
        BoxObstacle lt = (BoxObstacle)bodies.get(6);
        BoxObstacle rt = (BoxObstacle)bodies.get(7);
        BoxObstacle ls = (BoxObstacle)bodies.get(8);
        BoxObstacle rs = (BoxObstacle)bodies.get(9);
        createJoint(body, head, new Vector2(0, 2f), new Vector2(0, -1.8f), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(body, la, new Vector2(-1.5f, 1.75f), new Vector2(1.65f, 0f), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(body, ra, new Vector2(1.5f, 1.75f), new Vector2(1.65f, 0f), world, (float)Math.PI/2, (float)Math.PI/2*3);
        createJoint(body, lt, new Vector2(-0.75f, -2), new Vector2(0, 1.5f), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(body, rt, new Vector2(0.75f, -2), new Vector2(0, 1.5f), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(la, lf, new Vector2(-1.4f, 0), new Vector2(1.35f, 0), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(ra, rf, new Vector2(-1.4f, 0), new Vector2(1.35f, 0), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(lt, ls, new Vector2(0, -1.5f), new Vector2(0, 1.25f), world, (float)-Math.PI/2, (float)Math.PI/2);
        createJoint(rt, rs, new Vector2(0, -1.5f), new Vector2(0, 1.25f), world, (float)-Math.PI/2, (float)Math.PI/2);
        //#endregion


        // Weld the bubbler to this mask
        WeldJointDef weldDef = new WeldJointDef();
        weldDef.bodyA = bodies.get(PART_HEAD).getBody();
        weldDef.localAnchorA.set(BUBB_OFF);
        weldDef.localAnchorB.set(0,0);
        Joint wjoint = world.createJoint(weldDef);
        joints.add(wjoint);

        return true;
    }

    /**
     *
     * @param b1 first Obstacle
     * @param b2 second Obstacle
     * @param b1Vec joint position relative to first body
     * @param b2Vec joint position relative to second body
     * @param world Box2D world to store joints
     * @param lowAng lower bound on rotation angle
     * @param upAng upper bound on rotation angle
     */
    private void createJoint(BoxObstacle b1, BoxObstacle b2, Vector2 b1Vec, Vector2 b2Vec, World world, float lowAng, float upAng) {
        RevoluteJointDef jointDef = new RevoluteJointDef();

        jointDef.bodyA = b1.getBody();
        jointDef.bodyB = b2.getBody();
        jointDef.localAnchorA.set(b1Vec);
        jointDef.localAnchorB.set(b2Vec);
        jointDef.collideConnected = false;
        jointDef.lowerAngle = lowAng;
        jointDef.upperAngle = upAng;
        jointDef.enableLimit = true;
        Joint joint = world.createJoint(jointDef);
        joints.add(joint);
    }
}

//
//    public OobModel(float x, float y, float radius, World world) {
//        super(x, y);
//        this.world = world;
//        this.radius = radius;
//        // Gameplay attributes
//        isGrounded = false;
//        jumpCooldown = 0;
//    }
//    public void init() {
//
//        // Create central blob
//        WheelObstacle centerBlob = new WheelObstacle(getX(), getY(), 0.25f);
//        centerBlob.setDensity(OOB_DENSITY);
//        centerBlob.setRestitution(0.4f);
//        centerBlob.setFriction(0.5f);
//        bodies.add(centerBlob);
//
//        // Create outer blobs
//        for (int i = 0; i < BLOBS; i++) {
//            double angle = (2 * Math.PI) / (i * BLOBS);
//            float blobX = (float) (getX() + radius * Math.cos(angle));
//            float blobY = (float) (getY() + radius * Math.sin(angle));
//            WheelObstacle blob = new WheelObstacle(blobX, blobY, 0.25f);
//            blob.setDensity(OOB_DENSITY);
//            blob.setRestitution(0.4f);
//            blob.setFriction(0.5f);
//            bodies.add(blob);
//        }
//    }
//
//    public boolean createJoints(World world) {
//        assert bodies.size > 0;
//
//        for (int i = 1; i <= BLOBS; i++) {
//            // Connect all outer blobs to center
//            DistanceJointDef jointDef = new DistanceJointDef();
//            jointDef.bodyA = bodies.get(0).getBody();
//            jointDef.bodyB = bodies.get(i).getBody();
//            jointDef.length = radius;
//            jointDef.collideConnected = true;
//            jointDef.frequencyHz = 5.0f;
//            jointDef.dampingRatio = 0.5f;
//            Joint joint = world.createJoint(jointDef);
//            joints.add(joint);
//
//            // Connect the outer blobs to each other
//            jointDef.bodyA = bodies.get(i).getBody();
//            jointDef.bodyB = bodies.get((i+1)%BLOBS).getBody();
//            jointDef.collideConnected = true;
//            jointDef.frequencyHz = 5.0f;
//            jointDef.dampingRatio = 0.5f;
//            Joint joint2 = world.createJoint(jointDef);
//            joints.add(joint2);
//        }
//        return true;
//    }