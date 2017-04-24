/*
 * VertexBuffer.java
 *
 * This class represents a collection of vertices for drawing. It stores the vertices
 * in an optimized format, ensure memory locality in the heap.  If you want to draw
 * a complex textured polygon, you should create a VertexBuffer for the entire polygon.
 *
 * A VertexBuffer is not enough by itself to define a polygon.  In addition to the 
 * vertices, you need the triangles.  Typically triangles are represented as 
 * an array of indices, where each index is a position in the VertexBuffer.  Therefore,
 * it makes sense for vertices in a VertexBuffer object to be unique.  However, this is
 * not enforced.
 *
 * Author: Walker M. White
 * LibGDX version, 2/2/2017
 */
 package edu.cornell.gdiac.view.g2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import java.util.IllegalFormatCodePointException;

/**
 * An optimized buffer for storing a list of vertices.
 *
 * This class is the preferred way to represent a vertex (or several vertices) over the
 * Vertex class.  It stores the data in an optimized format for fast use by VertexBatch.  
 * Each vertex in the buffer consists of three pieces of information:
 *   + The position of the vertex
 *   + The color of the vertex
 *   + The texture coordinates of the vertex
 * A vertex buffer does not have an assigned texture.  That is determined by the VertexBatch.
 *
 * Because the data is in an optimized format, there is significant overhead for querying 
 * a vertex buffer (e.g. asking the position of the vertex at a given index).  For that 
 * reason, this class has some additional methods like nudge() and nearest() to allow a 
 * user to efficiently update a vertex buffer in place.  These are by no means exhaustive. 
 *  You may find that you need to add other methods for your particular application.
 */
public class VertexBuffer {
    /** The number floats to encode a single vertex */
    public static int STRIDE = 5;

    /** The (encoded) backing buffer */
    private float[] data;

    /** The number of vertices in this buffer */
    private int size;

    /**
     * Creates a vertex buffer with capacity 4 (e.g. a rectangle)
     *
     * The capacity is the maximum number of vertices that may be added to the buffer.
     * However, there are no vertices in the buffer when it is initialized. Add vertices
     * with the append() method.
     */
    public VertexBuffer() {
        this(4);
    }

    /**
     * Creates a vertex buffer with the given capacity.
     *
     * The capacity is the maximum number of vertices that may be added to the buffer. 
     * However, there are no vertices in the buffer when it is initialized. Add vertices
     * with the append() method.
     *
     * @param capacity  The buffer capacity
     */
    public VertexBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Value " + capacity + " is an illegal capacity");
        data = new float[capacity * STRIDE];
        size = 0;
    }

    /**
     * Returns the capacity of this vertex buffer
     *
     * The capacity is the maximum number of vertices that may be added to the buffer.
     *
     * @return the capacity of this vertex buffer
     */
    public int capacity() {
        return data.length/STRIDE;
    }

    /**
     * Returns the number of vertices in this buffer
     *
     * A newly created buffer has no vertices.  Add vertices with the append() method.
     *
     * @return the capacity of this vertex buffer
     */
    public int size() {
        return size;
    }

    /**
     * Appends a vertex with the given position
     *
     * The vertex is appended to the next available position.  The operation fails if
     * there is no more capacity in the buffer.  The created vertex has color WHITE
     * and texture coordinates (0,0)
     *
     * @param x	The x-coordinate of the vertex
     * @param y	The y-coordinate of the vertex
     */
    public void append(float x, float y) {
        if (size >= data.length) throw new IndexOutOfBoundsException("VertexBuffer is full");
        data[size    ] = x;
        data[size + 1] = y;
        data[size + 2] = Color.WHITE.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
        size += 5;
    }

    /**
     * Appends a vertex with the given position
     *
     * The vertex is appended to the next available position.  The operation fails if
     * there is no more capacity in the buffer.  The created vertex has color WHITE
     * and texture coordinates (0,0)
     *
     * @param position	The vertex position
     */
    public void append(Vector2 position) {
        if (size >= data.length) throw new IndexOutOfBoundsException("VertexBuffer is full");
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = Color.WHITE.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
        size += 5;
    }

    /**
     * Appends a vertex with the given position and color
     *
     * The vertex is appended to the next available position.  The operation fails if
     * there is no more capacity in the buffer.  The created vertex has texture 
     * coordinates (0,0)
     *
     * @param position	The vertex position
     * @param color		The vertex color
     */
    public void append(Vector2 position, Color color) {
        if (size >= data.length) throw new IndexOutOfBoundsException("VertexBuffer is full");
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = color.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
        size += 5;
    }

    /**
     * Appends a vertex with the given attributes
     *
     * The vertex is appended to the next available position.  The operation fails if
     * there is no more capacity in the buffer. 
     *
     * @param position	The vertex position
     * @param color		The vertex color
     * @param texcoord	The vertex texture coordinates
     */
    public void append(Vector2 position, Color color, Vector2 texcoord) {
        if (size >= data.length) throw new IndexOutOfBoundsException("VertexBuffer is full");
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = color.toFloatBits();
        data[size + 3] = texcoord.x;
        data[size + 4] = texcoord.y;
        size += 5;
    }

    /**
     * Appends a vertex to this buffer
     *
     * The vertex is appended to the next available position.  The operation fails if
     * there is no more capacity in the buffer. 
     *
     * @param vertex	The vertex to append
     */
    public void append(Vertex vertex) {
        if (size >= data.length) throw new IndexOutOfBoundsException("VertexBuffer is full");
        data[size    ] = vertex.getPosition().x;
        data[size + 1] = vertex.getPosition().y;
        data[size + 2] = vertex.getColor().toFloatBits();
        data[size + 3] = vertex.getTexCoords().x;
        data[size + 4] = vertex.getTexCoords().y;
        size += 5;
    }

    /**
     * Returns the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds
     *
     * @param index	The vertex index
     *
     * @return the vertex at the given index
     */
    public Vertex get(int index) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        Vector2 verts = new Vector2(data[off], data[off + 1]);
        Color color = new Color();
        Color.abgr8888ToColor(color, data[off + 2]);
        Vector2 coord = new Vector2(data[off + 3], data[off + 4]);

        return new Vertex(verts, color, coord);
    }

    /**
     * Returns the position of the vertex at the given index
     *
     * The object returned is a newly allocated Vector2 object.  Modifying this object
     * has no affect on the underlying buffer.
     * 
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     *
     * @return the position of the vertex at the given index
     */
    public Vector2 getPosition(int index) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        return new Vector2(data[off], data[off + 1]);
    }

    /**
     * Returns the color of the vertex at the given index
     *
     * The object returned is a newly allocated Color object.  Modifying this object
     * has no affect on the underlying buffer.
     * 
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     *
     * @return the color of the vertex at the given index
     */
    public Color getColor(int index) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        Color color = new Color();
        Color.abgr8888ToColor(color, data[off + 2]);
        return color;
    }

    /**
     * Returns the texture coordinates of the vertex at the given index
     *
     * The object returned is a newly allocated Vector2 object.  Modifying this object
     * has no affect on the underlying buffer.
     * 
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     *
     * @return the texture coordinates of the vertex at the given index
     */
    public Vector2 getTexCoords(int index) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        return new Vector2(data[off + 3], data[off + 4]);
    }

    /**
     * Sets the position of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     * @param x		The new x-coordinate
     * @param y		The new y-coordinate
     */
    public void set(int index, float x, float y) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size    ] = x;
        data[size + 1] = y;
        data[size + 2] = Color.WHITE.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
    }

    /**
     * Sets the position of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index		The vertex index
     * @param position	The vertex position
     */
    public void set(int index, Vector2 position) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = Color.WHITE.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
    }

    /**
     * Sets the position and color of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index		The vertex index
     * @param position	The vertex position
     * @param color		The vertex color
     */
    public void set(int index, Vector2 position, Color color) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = color.toFloatBits();
        data[size + 3] = 0.0f;
        data[size + 4] = 0.0f;
    }

    /**
     * Sets the attributes of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index		The vertex index
     * @param position	The vertex position
     * @param color		The vertex color
     * @param texcoord	The texture coordinates
     */
    public void set(int index, Vector2 position, Color color, Vector2 texcoord) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size    ] = position.x;
        data[size + 1] = position.y;
        data[size + 2] = color.toFloatBits();
        data[size + 3] = texcoord.x;
        data[size + 4] = texcoord.y;
    }

    /**
     * Sets the attributes of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index		The vertex index
     * @param vertex	The vertex atributes
     */
    public void set(int index, Vertex vertex) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[off    ] = vertex.getPosition().x;
        data[off + 1] = vertex.getPosition().y;
        data[off + 2] = vertex.getColor().toFloatBits();
        data[off + 3] = vertex.getTexCoords().x;
        data[off + 4] = vertex.getTexCoords().y;
    }

    /**
     * Sets the position of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     * @param x		The new x-coordinate
     * @param y		The new y-coordinate
     */
    public void setPosition(int index, float x, float y) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size    ] = x;
        data[size + 1] = y;
        size += 5;
    }

    /**
     * Sets the color of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     * @param color	The vertex color
     */
    public void setColor(int index, Color color) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[size + 2] = color.toFloatBits();
    }

    /**
     * Sets the texture coordinates of the vertex at the given index
     *
     * This method throws an exception if the index is out of bounds.
     *
     * @param index	The vertex index
     * @param u		The new texture u-coordinate
     * @param v		The new texture v-coordinate
     */
    public void setTexCoords(int index, float u, float v) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[off + 3] = u;
        data[off + 4] = v;
    }

	/**
	 * Clears this vertex buffer, deleting all vertices.
	 *
	 * The size will be 0 after this method is called.
	 */
    public void clear() {
        size = 0;
    }

	/**
	 * Returns the encoded data for this texture buffer.
	 *
	 * This data is returned by reference.  Modifying it will modify the underlying
	 * data.  This is not advised.  We only expose this data because it is needed
	 * by the VertexBatch object.
=	 */
    public float[] getData() {
        return data;
    }


    /// METHODS FOR EFFICIENT EDITING

	/**
	 * Returns the index (if any) of the nearest vertex within the distance threshold
	 *
	 * This method is typically used to find the nearest vertex to a mouse click.  It
	 * takes the given position, and measures the distance from it to each vertex in
	 * the buffer.  If any vertex has distance less than threshold, it choses the 
	 * nearest such one and returns the index.  If no vertex is within the distance
	 * threshold, it returns -1.
	 *
	 * The distance threshold typically corresponds to the radius of the selection pointer.
	 * However, this is not required.
	 *
	 * @param position	The position to search against
	 * @param threshold	The distance threshold for vertex selection.
	 *
	 * @return the index (if any) of the nearest vertex within the distance threshold
	 */
    public int nearest(Vector2 position, float threshold) {
        return nearest(position.x,position.y,threshold);
    }

	/**
	 * Returns the index (if any) of the nearest vertex within the distance threshold
	 *
	 * This method is typically used to find the nearest vertex to a mouse click.  It
	 * takes the given position, and measures the distance from it to each vertex in
	 * the buffer.  If any vertex has distance less than threshold, it choses the 
	 * nearest such one and returns the index.  If no vertex is within the distance
	 * threshold, it returns -1.
	 *
	 * The distance threshold typically corresponds to the radius of the selection pointer.
	 * However, this is not required.
	 *
	 * @param x			The x-coordinate to search against
	 * @param y			The y-coordinate to search against
	 * @param threshold	The distance threshold for vertex selection.
	 *
	 * @return the index (if any) of the nearest vertex within the distance threshold
	 */
    public int nearest(float x, float y, float threshold) {
        int minpos = -1;
        float mindist = threshold*threshold;
        for(int ii = 0; ii < size; ii +=5 ) {
            float dx = x-data[ii  ];
            float dy = y-data[ii+1];
            float dist = dx*dx+dy*dy;
            if (dist < mindist) {
                mindist = dist;
                minpos = ii/5;
            }
        }
        return minpos;
    }

    /**
     * Adjusts the position of the vertex at the given index by the given amount
     *
     * This method throws an exception if the index is out of bounds.  Because the
     * getter methods allocate to the heap, this method is the preferred way to 
     * update a vertex in place.
     *
     * @param index	The vertex index
     * @param dx	The amount to adjust the x-coordinate
     * @param dy	The amount to adjust the y-coordinate
     */
    public void nudge(int index, float dx, float dy) {
        if (index >= size) throw new IllegalArgumentException("VertexBuffer has size "+size+" < "+index);
        if (size < 0) throw new IllegalArgumentException("Index "+index+" is negative");
        int off = STRIDE * index;
        data[off    ] += dx;
        data[off + 1] += dy;
    }

}
