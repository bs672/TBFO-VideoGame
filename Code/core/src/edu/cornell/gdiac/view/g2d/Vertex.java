/*
 * Vertex.java
 *
 * This class represents a single vertex for drawing. For optimization purposes, we 
 * generally do not refer to vertices with Vertex objects.  Instead, we store them in
 * a VertexBuffer.  This class is simply meant to provide us with a way to access 
 * individual vertices in a buffer.
 *
 * Author: Walker M. White
 * LibGDX version, 2/2/2017
 */
package edu.cornell.gdiac.view.g2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

/**
 * A 2D vertex for drawing textured polygons.
 *
 * A vertex consists of three pieces of information:
 *   + The position of the vertex
 *   + The color of the vertex
 *   + The texture coordinates of the vertex
 * A vertex does not have an assigned texture.  That is determined by the VertexBatch.
 *
 * Vertex objects are heavy-weight.  Each vertex is a separate object on the heap.
 * In addition, they contain information (position, color, texture coordinates) that 
 * are also allocated on the heap.  This is not what you want to do for large polygons.
 *
 * Instead, you should always use a VertexBuffer object.  This object is only provided
 * to give you a nice way to access information present in a VertexBuffer.
 */
public class Vertex {
    /** The vertex position. */
    private Vector2 position;
    /** The vertex texture coordinates. */
    private Vector2 texcoord;
    /** The vertex color. */
    private Color color;

    /**
     * Creates a new vertex at the origin.
     *
     * The vertex color is white, and has texture coordinates (0,0).
     */
    public Vertex() {
        this(null,null,null);
    }

    /**
     * Creates a new vertex at the given position.
     *
     * The vertex color is white, and has texture coordinates (0,0).
     *
     * @param position  The vertex position
     */
    public Vertex(Vector2 position) {
        this(position,null,null);
    }

    /**
     * Creates a new vertex with the given position and color.
     *
     * The vertex has texture coordinates (0,0).
     *
     * @param position  The vertex position
     * @param color     The vertex color
     */
    public Vertex(Vector2 position, Color color) {
        this(position,color,null);
    }

    /**
     * Creates a new vertex with the given attributes.
     *
     * @param position  The vertex position
     * @param color     The vertex color
     * @param texcoord  The texture coordinates
     */
    public Vertex(Vector2 position, Color color, Vector2 texcoord) {
        if (position == null) {
            this.position = new Vector2(0,0);
        } else {
            this.position = new Vector2(position);
        }

        if (color == null) {
            this.color = new Color(Color.WHITE);
        } else {
            this.color = new Color(color);
        }

        if (texcoord == null) {
            this.texcoord = new Vector2(0,0);
        } else {
            this.texcoord = new Vector2(texcoord);
        }
    }

    /**
     * Returns the position of this vertex.
     *
     * The position is returned by reference.  Changes to this Vector2
     * will change the vertex as well.
     *
     * @return the position of this vertex.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the position of this vertex.
     *
     * The position value is copied.  This vertex does not keep a reference to the
     * original Vector2 object.
     *
     * @param position  The position of this vertex.
     */
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    /**
     * Sets the position of this vertex.
     *
     * @param x     The x-coordinate of the position
     * @param y     The y-coordinate of the position
     */
    public void setPosition(float x, float y) {
        this.position.set(x,y);
    }

    /**
     * Returns the color of this vertex.
     *
     * The position is returned by reference.  Changes to this Color object
     * will change the vertex as well.
     *
     * @return the color of this vertex.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of this vertex.
     *
     * The color value is copied.  This vertex does not keep a reference to the
     * original Color object.
     *
     * @param color The color of this vertex.
     */
    public void setColor(Color color) {
        this.color.set(color);
    }

    /**
     * Returns the texture coordinates of this vertex.
     *
     * The position is returned by reference.  Changes to this Vector2
     * will change the vertex as well.
     *
     * @return the texture coordinates of this vertex.
     */
    public Vector2 getTexCoords() {
        return position;
    }

    /**
     * Sets the texture coordinates of this vertex.
     *
     * The coordinate value is copied.  This vertex does not keep a reference to the
     * original Vector2 object.
     *
     * @param coords    The texture coordinates of this vertex.
     */
    public void setTexCoords(Vector2 coords) {
        this.texcoord = coords;
    }

    /**
     * Sets the texture coordinates of this vertex.
     *
     * @param u     The u-coordinate of the texture
     * @param v     The v-coordinate of the texture
     */
    public void setTexCoords(float u, float v) {
        this.texcoord = new Vector2(u,v);
    }
}
