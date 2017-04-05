package edu.cornell.gdiac.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import edu.cornell.gdiac.model.obstacle.*;
import com.badlogic.gdx.utils.Array;


/**
 * Created by Matt Loughney on 4/4/17.
 */



/**
 * Blobby Oob model
 */
public class ComplexOobModel extends ComplexObstacle {

    private WheelObstacle center;
    private Array<WheelObstacle> outerRing;
    private float radius;

    /**
     * Creates a new ragdoll with its head at the given position.
     *
     * @param x  Initial x position of the ragdoll head
     * @param y  Initial y position of the ragdoll head
     */
    public ComplexOobModel(float x, float y, float rad, int ringCircles) {
        super(x,y);
        this.radius = rad;
        center = new WheelObstacle(x, y, rad / 5);
        center.setBodyType(BodyDef.BodyType.DynamicBody);
        center.setName("OobCenter");
        bodies.add(center);
        float angle = 0;
        outerRing = new Array<WheelObstacle>();
        for(int i = 0; i < ringCircles; i++) {
            WheelObstacle wheel = new WheelObstacle(x + rad*(float)Math.cos(angle), y + rad*(float)Math.sin(angle), rad / 15);
            wheel.setBodyType(BodyDef.BodyType.DynamicBody);
            wheel.setName("ring" + Integer.toString(i));
            bodies.add(wheel);
            outerRing.add(wheel);
            angle += 2 * Math.PI / ringCircles;
        }
        for(Obstacle b : bodies)
            b.setGravityScale(0);

    }


    protected boolean createJoints(World world) {
        for(int i = 1; i < bodies.size; i++) {
            // making joints from center to outer ring
            DistanceJointDef jointDef = new DistanceJointDef();
            jointDef.bodyA = center.getBody();
            jointDef.bodyB = bodies.get(i).getBody();
            jointDef.localAnchorA.set(new Vector2(0, 0));
            jointDef.localAnchorB.set(new Vector2(0, 0));
            jointDef.length = radius;
            jointDef.dampingRatio = 0.5f;
            jointDef.frequencyHz = 5;
            Joint joint = world.createJoint(jointDef);
            joints.add(joint);


            // making joints between outer ring
            jointDef = new DistanceJointDef();
            if (i == bodies.size - 1) {
                jointDef.bodyA = bodies.get(i).getBody();
                jointDef.bodyB = bodies.get(1).getBody();
            } else {
                jointDef.bodyA = bodies.get(i).getBody();
                jointDef.bodyB = bodies.get(i + 1).getBody();
            }
            jointDef.localAnchorA.set(new Vector2(0, 0));
            jointDef.localAnchorB.set(new Vector2(0, 0));
            jointDef.length = 2*radius*(float)Math.sin(2*Math.PI / (bodies.size - 1) / 2);
            joint = world.createJoint(jointDef);
            joints.add(joint);
        }

        return true;
    }


    /**
     * Sets the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @param value  the object texture for drawing purposes.
     */
    public void setTexture(TextureRegion value) {
        center.setTexture(value);
    }

    public void scalePicScale(Vector2 v) {
        center.scalePicScale(v);
    }

    public void applyForceZero() {
        center.getBody().setLinearVelocity(0, 0);
    }

    public void applyForce(Vector2 v) {
        center.getBody().setLinearVelocity(v);
    }
}