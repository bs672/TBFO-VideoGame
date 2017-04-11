package edu.cornell.gdiac.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import edu.cornell.gdiac.model.obstacle.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.view.g2d.*;


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

    /** The variant of the spritebatch for drawing vertices */
    private VertexBatch batch;
    /** The buffer containing the vertex data to draw */
    private VertexBuffer vertices;
    /** The indices to define triangles from the vertices */
    private short[] indices;

    /** The image to texture the vertices with */
    private Texture img;
    /** The transform to center the image on the screen */
    private Affine2 transform;

    /** The number of edges to approximate a circle */
    private int size;

    /** A vector to track the last mouse position */
    private Array<Vector2> edgePosns;

    /**
     * Creates a new ragdoll with its head at the given position.
     *
     * @param x  Initial x position of the ragdoll head
     * @param y  Initial y position of the ragdoll head
     */
    public ComplexOobModel(float x, float y, float rad, int ringCircles) {
        super(x,y);
        forceVec = new Vector2();
        size = ringCircles;
        setPosition(x,y);
        this.radius = rad;
        setBodyType(BodyDef.BodyType.DynamicBody);
        center = new WheelObstacle(x, y, 0.4f);
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

        // Create the VertexBatch for drawing
        batch = new VertexBatch();
        img = new Texture("space/Oob/oob2.png");

        // Create a transform to center the polygon
        transform = new Affine2();
        transform.setToTranslation(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);

        mapTexture();
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
     * Renders the polygon to the screen
     *
     * We do not need an update() call for this class, that is handled by the
     * InputProcessor methods.
     */
    public void draw () {
        for(int i = 0; i < size; i++)
            moveIndex(i, bodies.get(i+1).getX(), bodies.get(i+1).getY());
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img,vertices,indices,transform);
        batch.end();
    }

    /**
     * The vertex is nudged by its change in position.
     *
     * @param newX   The x-coordinate in screen space (origin top left)
     * @param newY   The y-coordinate in screen space (origin top left)
     */
    public void moveIndex(int edgeIndex, float newX, float newY) {
        float dx = newX-edgePosns.get(edgeIndex).x;
        float dy = edgePosns.get(edgeIndex).y-newY; // Inverts the y-axis
        vertices.nudge(edgeIndex,dx,dy);
        edgePosns.get(edgeIndex).set(newX,newY);
    }

    /**
     * Resets the polygon to match the current global state.
     *
     * maps the texture coordinates to the coordinates of Oob's outer ring of circles
     */
    public void mapTexture() {
        // Cache objects to create the vertex buffer
        Vector2 position = new Vector2(0,0);
        Vector2 texcoord = new Vector2(0,0);

        // Go around in a circle, starting at the right
        float step = (float)(Math.PI*2)/size;
        vertices = new VertexBuffer(size+1);
        vertices.append(position, Color.WHITE, texcoord);

        edgePosns = new Array<Vector2>();

        float dx, dy;
        for(int i = 0; i < size; i++) {
            // Compute position on unit circle
            double angle = i*step;
            dx = (float)Math.cos(angle);
            dy = (float)Math.sin(angle);

            // Set the position
            position.set(bodies.get(i+1).getPosition());

            // Set the texture coords.
            texcoord.set(dx/2,dy/2);

            // append to vertex buffer
            vertices.append(position, Color.WHITE, texcoord);
            edgePosns.add(position);
        }

        // Create the indices as a fan to the right
        indices = new short[size*3];
        for(int i = 0; i < size-1; i++) {
            indices[3*i  ]  = 0;
            indices[3*i+1]  = (short)(i+1);
            indices[3*i+2]  = (short)(i+2);
        }
        indices[3*size-3]  = 0;
        indices[3*size-2]  = (short)(3*size-1);
        indices[3*size-1]  = (short)(3*size);
    }

    /**
     * Called when the Application is destroyed.
     *
     * This is preceded by a call to pause().
     */
    public void dispose () {
        batch.dispose();
        img.dispose();
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

    public Vector2 getLinearVelocity() {return center.getLinearVelocity();}

    public float getRadius() { return radius; }

    public void setRadius(float f) {
        radius = f;
        for(DistanceJoint j : innerJoints) // the central joints
            j.setLength(radius);
        for(DistanceJoint j : outerJoints) { // the outer joints
            j.setLength(2*radius*(float)Math.sin(2*Math.PI / (bodies.size - 1) / 2));
        }
        for(int i = 1; i < bodies.size; i++)
            ((WheelObstacle)bodies.get(i)).setRadius(radius*(float)Math.sin(Math.PI / (bodies.size - 1)));
    }

    public float getMass() {
        return (float)(Math.PI*Math.pow(radius, 2));
    }

    public WheelObstacle getCenter() {return center; }

    public void setX(float f) {
        center.setX(f);
        float angle = 0;
        for(int i = 1; i < bodies.size; i++) {
            ((WheelObstacle)bodies.get(i)).setX(f + radius*(float)Math.cos(angle));
            angle += 2 * Math.PI / (bodies.size - 1);
        }
    }
    public void setY(float f) {
        center.setY(f);
        float angle = 0;
        for(int i = 1; i < bodies.size; i++) {
            ((WheelObstacle)bodies.get(i)).setY(f + radius*(float)Math.sin(angle));
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