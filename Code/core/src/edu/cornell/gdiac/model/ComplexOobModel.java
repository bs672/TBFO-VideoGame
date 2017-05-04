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

    // orientation
    private float angle;

    /** The number of edges to approximate a circle */
    private int size;

    /** A vector arry to track the last position of each edge for texture mapping*/
    private Array<Vector2> edgePosns;

    private int FRAME_COLS;
    private int FRAME_ROWS;

    protected static int SHOT_COOLDOWN;

    /** Bools for Oob play state */
    private boolean normal;
    private boolean growing;
    private boolean command;
    private boolean flying;
    private boolean teleporting;
    private boolean hurting;
    private boolean dying;
    private boolean max;

    /** the direction he should be going */
    private Vector2 direction;

    /** Variables for Oob animation */

    private Animation<TextureRegion> Normal_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Normal_anim(){return Normal_Animation;}

    public VertexBatch getVertexBatch() {return batch; }

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



    private Animation<TextureRegion> Teleporting_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Teleporting_anim(){return Teleporting_Animation;}


    private Texture Teleporting_Sheet;

    public void set_Teleporting_sheet(Texture val){Teleporting_Sheet = val;}

    public Texture get_Teleporting_sheet(){return Teleporting_Sheet;}




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




    private Animation<TextureRegion> Max_Animation; // Must declare frame type (TextureRegion)

    public Animation<TextureRegion> get_Max_anim(){return Max_Animation;}


    private Texture Max_Sheet;

    public void set_Max_sheet(Texture val){Max_Sheet = val;}

    public Texture get_Max_sheet(){return Max_Sheet;}

    public Vector2 getDirection() {return direction; }

    public void setDirection(Vector2 v) {direction = v.nor(); }

    public void set_Shot_Cooldown(int c) {SHOT_COOLDOWN = c; }

    public int get_Shot_Cooldown() {return SHOT_COOLDOWN; }

    public void decCooldown() {SHOT_COOLDOWN--; }

    /**
     * Creates a new ragdoll with its head at the given position.
     *
     * @param x  Initial x position of the ragdoll head
     * @param y  Initial y position of the ragdoll head
     */
    public ComplexOobModel(float x, float y, float rad) {
        super(x,y);
        forceVec = new Vector2();
        size = 40;
        super.setPosition(x,y);
        radius = rad;
        setBodyType(BodyDef.BodyType.DynamicBody);
        center = new WheelObstacle(x, y, radius / 2);
        center.setBodyType(BodyDef.BodyType.DynamicBody);
        center.setName("OobCenter");
        body = center.getBody();
        bodies.add(center);
        innerJoints = new Array<DistanceJoint>();
        outerJoints = new Array<DistanceJoint>();
        float angle = 0;
        for(int i = 0; i < size; i++) { // create outer circles counter-clockwise from 0 degrees
            WheelObstacle wheel = new WheelObstacle(x + rad*(float)Math.cos(angle), y + rad*(float)Math.sin(angle), radius*(float)Math.sin(Math.PI / size)*0.7f);
            wheel.setBodyType(BodyDef.BodyType.DynamicBody);
            wheel.setName("Oob");
            bodies.add(wheel);
            angle += 2 * Math.PI / size;
        }
        for(Obstacle b : bodies)
            b.setGravityScale(0);

        // Create the VertexBatch for drawing
        batch = new VertexBatch();
        img = new Texture("space/Oob/oob2.png");

        // Create a transform to center the polygon
        transform = new Affine2();
        transform.setToScaling(40,40);

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
            jointDef.dampingRatio = 0.9f;
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
//            jointDef.dampingRatio = 0.7f;
//            jointDef.frequencyHz = 10;
            jointDef.length = 2*radius*(float)Math.sin(2*Math.PI / (bodies.size - 1) / 2)*0.7f;
            joint = world.createJoint(jointDef);
            joints.add(joint);
            outerJoints.add((DistanceJoint)joint);
        }

        return true;
    }

    public void checkForInsideOut(float f, Vector2 v) {
        boolean broken = false;
        for(int i = 0; i < size; i++) {
            Vector2 dist = innerJoints.get(i).getBodyA().getPosition().cpy().sub(innerJoints.get(i).getBodyB().getPosition());
            if(dist.len() > f*0.9) {
                forceVec.scl(0.6f);
                break;
            }
        }
        for(int i = 0; i < size; i++) {
            int index2 = i + size / 2;
            if (index2 >= size)
                index2 -= size;
            Vector2 dist = innerJoints.get(i).getBodyB().getPosition().cpy().sub(innerJoints.get(index2).getBodyB().getPosition());
            if (dist.len() < 0.5f * radius) {
                center.setPosition(center.getPosition().add(v.cpy().nor().scl(radius * 0.5f)));
                float angle = 0;
                float x = center.getX();
                float y = center.getY();
                for (int j = 0; j < size; j++) { // create outer circles counter-clockwise from 0 degrees
                    bodies.get(j + 1).setPosition(x + radius * (float) Math.cos(angle), y + radius * (float) Math.sin(angle));
                    angle += 2 * Math.PI / size;
                }
            }
        }
    }

    /**
     * Renders the polygon to the screen
     *
     * We do not need an update() call for this class, that is handled by the
     * InputProcessor methods.
     */
    public void draw () {
        for(int i = 1; i < bodies.size; i++)
            moveIndex(i-1, bodies.get(i).getX(), bodies.get(i).getY());
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
        float dy = newY-edgePosns.get(edgeIndex).y;
        vertices.nudge(edgeIndex,dx,dy);
        edgePosns.set(edgeIndex, new Vector2(newX, newY));
    }

    public void setAngle(float f) { angle = f; }

    /**
     * Resets the polygon to match the current global state.
     *
     * maps the texture coordinates to the coordinates of Oob's outer ring of circles
     */
    public void mapTexture() {
        // Cache objects to create the vertex buffer
        Vector2 texcoord = new Vector2(0.5f,0.5f);

        // Go around in a circle, starting at the right
        float step = (float)(Math.PI*2)/size;
        vertices = new VertexBuffer(bodies.size-1);

        edgePosns = new Array<Vector2>();

        float dx, dy;
        for(int i = 1; i < bodies.size; i++) {
            // Compute position on unit circle
            double angle = (i-1)*step;
            dx = (float)Math.cos(angle);
            dy = -(float)Math.sin(angle);

            WheelObstacle w = (WheelObstacle) bodies.get(i);

            // Set the position
            Vector2 position = w.getPosition().cpy();

            // Set the texture coords.
            texcoord.set((1+dx)/2,(1+dy)/2);

            // append to vertex buffer
            vertices.append(position, Color.WHITE, texcoord.cpy());
            edgePosns.add(position);
        }

        // Create the indices as a fan to the right
        // the size field is the number of circles on edge of Oob
        indices = new short[(size-1)*3];
        for(int i = 0; i < size-2; i++) {
            indices[3*i  ]  = 0;
            indices[3*i+1]  = (short)(i+1);
            indices[3*i+2]  = (short)(i+2);
        }
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
        img = value.getTexture();
        float baseX = ((float)value.getRegionX()) / img.getWidth();
        float baseY = ((float)value.getRegionY()) / img.getHeight();
        float step = (float)(Math.PI*2)/size;
        for(int i = 0; i < size; i++) {
            Vector2 centToEdge = bodies.get(i+1).getPosition().cpy().sub(center.getPosition());
//            float baseAngle = (float)Math.atan2(bodies.get(i+1).getX() - bodies.get(0).getX(), bodies.get(i+1).getY() - bodies.get(0).getY());
            float relAngle = (float)Math.atan2(centToEdge.y, centToEdge.x) - (angle - (float)Math.PI/2);
            float offsetX = (((float)Math.cos(relAngle) + 1) / 2) * (1f / FRAME_COLS);
            float offsetY = ((-(float)Math.sin(relAngle) + 1) / 2) * (1f / FRAME_ROWS);
            vertices.setTexCoords(i, baseX + offsetX, baseY + offsetY);
        }
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
            j.setLength(2*radius*(float)Math.sin(2*Math.PI / (bodies.size - 1) / 2)*0.7f);
        }
        for(int i = 1; i < bodies.size; i++)
            ((WheelObstacle)bodies.get(i)).setRadius(radius*(float)Math.sin(Math.PI / (bodies.size - 1))*0.7f);
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

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void addToPosition(float x, float y) {
        for(Obstacle o : bodies) {
            o.setX(o.getX() + x);
            o.setY(o.getY() + y);
        }
    }

    public Array<DistanceJoint> getInnerJoints() {return innerJoints; }

    public Array<DistanceJoint> getOuterJoints() {return outerJoints; }

    public boolean isNormal() {return normal;}
    public void setNormal(boolean bool) {
        normal = bool; growing = false; command = false; flying=false; teleporting=false; hurting= false; dying= false; max= false;
    }

    public boolean isGrowing() {return growing;}
    public void setGrowing(boolean bool) {
        normal = false; growing = bool; command = false; flying=false; teleporting=false; hurting= false; dying= false; max= false;
    }

    public boolean isCommand() {return command;}
    public void setCommand(boolean bool) {
        normal = false; growing = false; command = bool; flying=false; teleporting=false; hurting= false; dying= false; max= false;
    }

    public boolean isFlying() {return flying;}
    public void setFlying(boolean bool) {
        normal = false; growing = false; command = false; flying = bool; teleporting=false; hurting= false; dying= false; max= false;
    }

    public boolean isTeleporting() {return teleporting;}
    public void setTeleporting(boolean bool) {
        normal = false; growing = false; command = false; flying = false; teleporting=bool; hurting= false; dying= false; max= false;
    }

    public boolean isHurting() {return hurting;}
    public void setHurting(boolean bool) {
        normal = false; growing = false; command = false; flying=false; teleporting=false; hurting = bool; dying= false; max= false;
    }

    public boolean isDying() {return dying;}
    public void setDying(boolean bool) {
        normal = false; growing = false; command = false; flying=false; teleporting=false; hurting= false; dying = bool; max= false;
    }

    public boolean isMax() {return max;}
    public void setMax(boolean bool) {
        normal = false; growing = false; command = false; flying=false; teleporting=false; hurting= false; dying = false; max= bool;
    }


    public void reset_face() {
        normal=false;
        growing =false;
        command = false;
        flying=false;
        teleporting=false;
        hurting=false;
        dying=false;
        max=false;
    }


    public enum Face {
        NORMAL, GROWING, COMMAND, FLYING, TELEPORTING, HURTING, DYING, MAX
    }

    public void setAnimDimensions(int cols, int rows) {
        FRAME_COLS = cols;
        FRAME_ROWS = rows;
    }

    public void createNormaltex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 8;
        FRAME_ROWS = 7;

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
        Normal_Animation = new Animation<TextureRegion>(.05f, frames);

    }

    public void createGrowingtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 4;
        FRAME_ROWS = 3;


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
        Growing_Animation = new Animation<TextureRegion>(.1f, frames);


        // this.scalePicScale(new Vector2(1.8f,1.8f));
        // this.scalePicScale(new Vector2(.5f * this.getRadius(), .5f * this.getRadius()));

    }

    public void createCommandtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 4;
        FRAME_ROWS = 3;

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
        Command_Animation = new Animation<TextureRegion>(.3f, frames);

    }


    public void createFlyingtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 40;
        FRAME_ROWS = 1;

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
        Flying_Animation = new Animation<TextureRegion>(.05f, frames);

    }

    public void createTeleportingtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 3;
        FRAME_ROWS = 2;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Teleporting_Sheet,
                Teleporting_Sheet.getWidth() / FRAME_COLS,
                Teleporting_Sheet.getHeight() / FRAME_ROWS);

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
        Teleporting_Animation = new Animation<TextureRegion>(.1f, frames);

    }


    public void createHurtingtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 25;
        FRAME_ROWS = 1;

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
        Hurting_Animation = new Animation<TextureRegion>(.05f, frames);

    }


    public void createDyingtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 4;
        FRAME_ROWS = 3;

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
        Dying_Animation = new Animation<TextureRegion>(.15f, frames);
    }

    public void createMaxtex() {

        // Constant rows and columns of the sprite sheet
        FRAME_COLS = 4;
        FRAME_ROWS = 3;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(Max_Sheet,
                Max_Sheet.getWidth() / FRAME_COLS,
                Max_Sheet.getHeight() / FRAME_ROWS);

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
        Max_Animation = new Animation<TextureRegion>(.15f, frames);
    }
}
