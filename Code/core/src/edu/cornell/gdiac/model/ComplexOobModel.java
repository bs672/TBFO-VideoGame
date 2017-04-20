package edu.cornell.gdiac.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    /** A vector arry to track the last position of each edge for texture mapping*/
    private Array<Vector2> edgePosns;

    /** Bools for Oob play state */
    private boolean normal;
    private boolean growing;
    private boolean command;
    private boolean flying;
    private boolean hurting;
    private boolean dying;

    /** Variables for Oob animation */

    private Animation<TextureRegion> Normal_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Normal_anim(){return Normal_Animation;}



    private Texture Normal_Sheet;

    public void set_Normal_sheet(Texture val){
        Normal_Sheet = val;}

    public Texture get_Normal_sheet(){return Normal_Sheet;}



    private Animation<TextureRegion> Growing_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Growing_anim(){return Growing_Animation;}


    private Texture Growing_Sheet;

    public void set_Growing_sheet(Texture val){Growing_Sheet = val;}

    public Texture get_Growing_sheet(){return Growing_Sheet;}



    private Animation<TextureRegion> Command_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Command_anim(){return Command_Animation;}


    private Texture Command_Sheet;

    public void set_Command_sheet(Texture val){Command_Sheet = val;}

    public Texture get_Command_sheet(){return Command_Sheet;}




    private Animation<TextureRegion> Flying_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Flying_anim(){return Flying_Animation;}


    private Texture Flying_Sheet;

    public void set_Flying_sheet(Texture val){Flying_Sheet = val;}

    public Texture get_Flying_sheet(){return Flying_Sheet;}




    private Animation<TextureRegion> Hurting_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Hurting_anim(){return Hurting_Animation;}


    private Texture Hurting_Sheet;

    public void set_Hurting_sheet(Texture val){Hurting_Sheet = val;}

    public Texture get_Hurting_sheet(){return Hurting_Sheet;}




    private Animation<TextureRegion> Dying_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Dying_anim(){return Dying_Animation;}


    private Texture Dying_Sheet;

    public void set_Dying_sheet(Texture val){Dying_Sheet = val;}

    public Texture get_Dying_sheet(){return Dying_Sheet;}

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
        for(int i = 0; i < size; i++) { // create outer circles counter-clockwise from 0 degrees
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
//        System.out.println(vertices);
//        System.out.println();
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
        float dy = newY-edgePosns.get(edgeIndex).y; // Inverts the y-axis
        vertices.nudge(edgeIndex,dx,dy);
        edgePosns.get(edgeIndex).set(newX, newY);

    }

    /**
     * Resets the polygon to match the current global state.
     *
     * maps the texture coordinates to the coordinates of Oob's outer ring of circles
     */
    public void mapTexture() {
        // Cache objects to create the vertex buffer
        Vector2 position = center.getPosition();
        Vector2 texcoord = new Vector2(0.5f,0.5f);

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
            texcoord.set((1+dx)/2,(1+dy)/2);

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
        center.setRadius(radius / 2);
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

    public Array<DistanceJoint> getInnerJoints() {return innerJoints; }

    public boolean isNormal() {return normal;}
    public void setNormal(boolean bool) {
        normal = bool; growing = false; command = false; flying=false; hurting= false; dying= false;
    }

    public boolean isGrowing() {return growing;}
    public void setGrowing(boolean bool) {
        normal = false; growing = bool; command = false; flying=false; hurting= false; dying= false;
    }

    public boolean isCommand() {return command;}
    public void setCommand(boolean bool) {
        normal = false; growing = false; command = bool; flying=false; hurting= false; dying= false;
    }

    public boolean isFlying() {return flying;}
    public void setFlying(boolean bool) {
        normal = false; growing = false; command = false; flying = bool; hurting= false; dying= false;
    }

    public boolean isHurting() {return hurting;}
    public void setHurting(boolean bool) {
        normal = false; growing = false; command = false; flying=false; hurting = bool; dying= false;
    }

    public boolean isDying() {return dying;}
    public void setDying(boolean bool) {
        normal = false; growing = false; command = false; flying=false; hurting= false; dying = bool;
    }

    public void reset_face() {
        normal=false;
        growing =false;
        command = false;
        flying=false;
        hurting=false;
        dying=false;
    }




    public void createNormaltex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Normal_Sheet,
                Normal_Sheet.getWidth() / FRAME_COLS,
                Normal_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Normal_Animation = new Animation<TextureRegion>(.5f, frames);

    }

    public void createGrowingtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Growing_Sheet,
                Growing_Sheet.getWidth() / FRAME_COLS,
                Growing_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Growing_Animation = new Animation<TextureRegion>(.3f, frames);


        // this.scalePicScale(new Vector2(1.8f,1.8f));
        // this.scalePicScale(new Vector2(.5f * this.getRadius(), .5f * this.getRadius()));

    }

    public void createCommandtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 4, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Command_Sheet,
                Command_Sheet.getWidth() / FRAME_COLS,
                Command_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Command_Animation = new Animation<TextureRegion>(.5f, frames);

    }


    public void createFlyingtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 8, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Flying_Sheet,
                Flying_Sheet.getWidth() / FRAME_COLS,
                Flying_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Flying_Animation = new Animation<TextureRegion>(.3f, frames);

    }


    public void createHurtingtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 4, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Hurting_Sheet,
                Hurting_Sheet.getWidth() / FRAME_COLS,
                Hurting_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Hurting_Animation = new Animation<TextureRegion>(.5f, frames);

    }


    public void createDyingtex() {

        // Constant rows and columns of the sprite sheet
        int FRAME_COLS = 4, FRAME_ROWS = 1;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Dying_Sheet,
                Dying_Sheet.getWidth() / FRAME_COLS,
                Dying_Sheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        Dying_Animation = new Animation<TextureRegion>(.5f, frames);
    }
}
