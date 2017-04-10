///*
// * GDXRoot.java
// *
// * This is the primary class file for running the game.  It is the "static main" of
// * LibGDX.  Typically we use this class with player modes and make the input a separate
// * class.  This time, we have put all of the game logic in this class so that you can
// * see what is happening in a single glance.
// *
// * Author: Walker M. White
// * LibGDX version, 2/2/2017
// */
//package edu.cornell.gdiac.view.vertices;
//
//import com.badlogic.gdx.*;
//import com.badlogic.gdx.math.*;
//import com.badlogic.gdx.graphics.*;
//import com.badlogic.gdx.utils.Array;
//import edu.cornell.gdiac.view.g2d.*;
//
///**
// * Root class for the texture demo.
// *
// * This class shows off a circle-like texture in the middle of the screen.  It allows
// * the user to distort the image by dragging the various vertices.
// *
// * This class is technically not the ROOT CLASS. Each platform has another class above
// * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However,
// * those classes are unique to each platform, while this class is the same across all
// * plaforms. In addition, this functions as the root class all intents and purposes,
// * and you would draw it as a root class in an architecture specification.
// */
//public class Animator extends ApplicationAdapter {
//
//    /** The variant of the spritebatch for drawing vertices */
//    private VertexBatch batch;
//    /** The buffer containing the vertex data to draw */
//    private VertexBuffer vertices;
//    /** The indices to define triangles from the vertices */
//    private short[] indices;
//
//    /** The image to texture the vertices with */
//    private Texture img;
//    /** The transform to center the image on the screen */
//    private Affine2 transform;
//
//    /** The number of edges to approximate a circle */
//    private int size;
//
//    /** A vector to track the last mouse position */
//    private Array<Vector2> edgePosns = null;
//
//    /**
//     * Called when the Application is first created.
//     *
//     * This is method loads the image and defines the (initial) vertex buffer
//     */
//    @Override
//    public void create () {
//        // Create the VertexBatch for drawing
//        batch = new VertexBatch();
//        img = new Texture("space/Oob/oob2.png");
//
//        // Create a transform to center the polygon
//        transform = new Affine2();
//        transform.setToTranslation(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
//
//        // Reset the polygon
//        size = 8;
//        reset();
//    }
//
//
//    /**
//     * Renders the polygon to the screen
//     *
//     * We do not need an update() call for this class, that is handled by the
//     * InputProcessor methods.
//     */
//    @Override
//    public void render () {
//        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.begin();
//        batch.draw(img,vertices,indices,transform);
//        batch.end();
//    }
//
//    public void update () {
//
//    }
//
//    /**
//     * Resets the polygon to match the current global state.
//     *
//     * The polygon created will be centered in the screen with size number of
//     * edges.  Any distortions applied by the mouse will be removed.  If the texture
//     * is turned off, the vertices will be colored using an HSV color-wheel.
//     */
//    public void reset() {
//        // Cache objects to create the vertex buffer
//        Vector2 position = new Vector2();
//        Vector2 texcoord = new Vector2();
//        Color color = new Color();
//
//        // Go around in a circle, starting at the top
//        float step = (float)(Math.PI*2)/size;
//        vertices = new VertexBuffer(size);
//
//        float dx, dy;
//        for(int ii = 0; ii < size; ii++) {
//            // Compute position on unit circle
//            double angle = ii*step+Math.PI/2.0f;
//            dx = (float)Math.cos(angle);
//            dy = (float)Math.sin(angle);
//
//            // Set the position
//            position.set(dx*radius,dy*radius);
//
//            // Set the texture coords.
//            texcoord.set((1+dx)/2,(1-dy)/2);
//
//            // Set the color and append to vertex buffer
//            //vertices.append(position, color, texcoord);
//            vertices.append(position, Color.WHITE, texcoord);
//        }
//
//        // Create the indices as a fan to the top
//        indices = new short[size*3];
//        for(int ii = 0; ii < size-2; ii++) {
//            indices[3*ii  ]  = 0;
//            indices[3*ii+1]  = (short)(ii+1);
//            indices[3*ii+2]  = (short)(ii+2);
//        }
//
//        // Reset the mouse selection
//        mousepos  = null;
//    }
//
//    /**
//     * Called when the Application is destroyed.
//     *
//     * This is preceded by a call to pause().
//     */
//    @Override
//    public void dispose () {
//        batch.dispose();
//        img.dispose();
//    }
//
//    /**
//     * Called when a finger or the mouse was dragged.
//     *
//     * The vertex is nudged by its change in position.
//     *
//     * @param newX   The x-coordinate in screen space (origin top left)
//     * @param newY   The y-coordinate in screen space (origin top left)
//     */
//    public void moveIndex(int edgeIndex, int newX, int newY) {
//        float dx = newX-(int)edgePosns.get(edgeIndex).x;
//        float dy = (int)edgePosns.get(edgeIndex).y-newY; // Inverts the y-axis
//        vertices.nudge(edgeIndex,dx,dy);
//        edgePosns.get(edgeIndex).set(newX,newY);
//    }
//
//}
