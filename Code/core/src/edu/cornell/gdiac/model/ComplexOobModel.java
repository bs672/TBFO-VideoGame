package edu.cornell.gdiac.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
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
    private float radius;
    private Vector2 forceVec;
    private Array<DistanceJoint> innerJoints;
    private Array<DistanceJoint> outerJoints;

    /**
     * Creates a new ragdoll with its head at the given position.
     *
     * @param x  Initial x position of the ragdoll head
     * @param y  Initial y position of the ragdoll head
     */
    public ComplexOobModel(float x, float y, float rad, int ringCircles) {
        super(x,y);
        forceVec = new Vector2();
        setPosition(x,y);
        this.radius = rad;
        setBodyType(BodyDef.BodyType.DynamicBody);
        center = new WheelObstacle(x, y, 0.05f);
        center.setBodyType(BodyDef.BodyType.DynamicBody);
        center.setName("OobCenter");
        body = center.getBody();
        bodies.add(center);
        innerJoints = new Array<DistanceJoint>();
        outerJoints = new Array<DistanceJoint>();
        float angle = 0;
        for(int i = 0; i < ringCircles; i++) {
            WheelObstacle wheel = new WheelObstacle(x + rad*(float)Math.cos(angle), y + rad*(float)Math.sin(angle), radius*(float)Math.sin(Math.PI / ringCircles));
            wheel.setBodyType(BodyDef.BodyType.DynamicBody);
            wheel.setName("Oob");
            bodies.add(wheel);
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
            innerJoints.add((DistanceJoint)joint);


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
            outerJoints.add((DistanceJoint)joint);
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

    public float getRadius() { return radius; }

    public void setRadius(float f) {
        radius = f;
        System.out.println(radius);
        for(DistanceJoint j : innerJoints) // the central joints
            j.setLength(radius);
        for(DistanceJoint j : outerJoints) {
            j.setLength(2*radius*(float)Math.sin(2*Math.PI / (bodies.size - 1) / 2));
        }
    }

    public float getMass() {
        return (float)(Math.PI*Math.pow(radius, 2));
    }

    public WheelObstacle getCenter() {return center; }

    public void setX(float f) {
        center.setX(f);
        float angle = 0;
        for(int i = 1; i < bodies.size; i++) {
            bodies.get(i).setX(f + radius*(float)Math.cos(angle));
            angle += 2 * Math.PI / (bodies.size - 1);
        }
    }
    public void setY(float f) {
        center.setY(f);
        float angle = 0;
        for(int i = 1; i < bodies.size; i++) {
            bodies.get(i).setY(f + radius*(float)Math.sin(angle));
            angle += 2 * Math.PI / (bodies.size - 1);
        }
    }

    public float getX() {return center.getX();}
    public float getY() {return center.getY();}

    public Vector2 getPosition() {return center.getPosition();}

    public void addToForceVec(Vector2 v){forceVec.add(v);}

    public void resetForceVec() {forceVec.set(0,0);}

    public void applyForce() {applyForce(forceVec);}

    public void setLinearVelocity(Vector2 v) {
        for(Obstacle o : bodies)
            o.setLinearVelocity(v);
    }

    public Vector2 getForceVec() {return forceVec;}

    public void setPosition(Vector2 v) {
        for(Obstacle o : bodies)
            o.setPosition(v);
    }
}