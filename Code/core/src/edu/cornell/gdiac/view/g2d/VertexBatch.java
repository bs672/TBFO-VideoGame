/*
 * VerteBatch.java
 *
 * This class is an alternative to SpriteBatch for drawing VertexBuffer objects.  The
 * SpriteBatch interface does not allow the user to define texture coordinates, those
 * are always computed automatically.  This interface allows the user more fine-grained
 * control over texturing.
 *
 * Unlike SpriteBatch, this class does not implement the Batch interface.  That interface
 * has a lot of methods that require package (not public) access to TextureRegion.
 * Hence you cannot have a subclass of Batch outside of com.badlogic.gdx.graphics.g2d.
 * This is a poor design decision.  However, it is not really a big deal, since we
 * rarely want to mix a VertexBatch with a traditional SpriteBatch.
 *
 * Author: Walker M. White
 * LibGDX version, 2/2/2017
 */
 package edu.cornell.gdiac.view.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

/** 
 * Draws VertexBuffer objects with or without indices.
 *
 * This class is an alternative to SpriteBatch for drawing VertexBuffer objects.  The
 * SpriteBatch interface does not allow the user to define texture coordinates, those
 * are always computed automatically.  This interface allows the user more fine-grained
 * control over texturing.
 *
 * This class batches the drawing commands and optimizes them for processing by the GPU.  
 * Batches are flushed whenever a texture is changed, so it helps to minimize the number 
 * of textures in your application.
 *
 * To draw something with a VertexBatch, you must first call the {@link begin()} 
 * method which will setup appropriate render states. When you are done with drawing you 
 * call {@link end()} which will flush the buffer and actually draw the things specified.
 *
 * All drawing commands of the VertexBatch operate in OpenGL coordinates. By default,
 * the OpenGL coordinate system has an x-axis pointing to the right, an y-axis pointing 
 * upwards and the origin is in the lower left corner of the screen. You can also
 * provide your own transformation and projection matrices if you so wish.
 * 
 * A VertexBatch is managed. In case the OpenGL context is lost all OpenGL resources 
 * a VertexBatch uses internally get invalidated. A context is lost when a user 
 * switches to another application or receives an incoming call on Android. A
 * VertexBatch will be automatically reloaded after the OpenGL context is restored.
 * It should also be disposed if it is no longer used.
 *
 * Ideally, this class would implement the Batch interface so that it could completely
 * replace SpriteBatch.  However, we cannot do that because of package access issues
 * with TextureRegion.  Therefore, this class is designed to complement SpriteBatch,
 * not replace it.
 *
 * The architecture of this class is heavily based on the existing SpriteBatch class of
 * LibGDX by Nathan Sweet.
 */
public class VertexBatch implements Disposable {
    /** The size (in pixels) of the default blank texture */
    private static int BLANK_SIZE = 1;

    // THE MESH
    /** The underlying mesh for rendering the sprite batch */
    private Mesh mesh;
    /** The vertex buffer for the mesh */
    final float[] vertices;
    /** The index buffer for the mesh */
    final short[] indices;
    /** The current number of vertices in the vertex buffer */
    int vpos = 0;
    /** The current number of indices in the index buffer */
    int ipos = 0;

    /** The default blank texture (used with the set texture is null) */
    private Texture blank;
    /** The current texture in use (to track context switches) */
    Texture lastTexture = null;
    /** The inverse width of the current texture */
    private float invTexWidth = 0;
    /** The inverse height of the current texture */
    private float invTexHeight = 0;

    /** Whether or not this batch is actively drawing */
    boolean drawing = false;

    /** Global transformation matrix */
    private final Matrix4 transformMatrix = new Matrix4();
    /** Projection matrix (from the camera) */
    private final Matrix4 projectionMatrix = new Matrix4();
    /** The combination of the two transform matrices */
    private final Matrix4 combinedMatrix = new Matrix4();

    /** Whether or not blending is active */
    private boolean blendingDisabled = false;
    /** The source blending function */
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    /** The destination blending function */
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;

    // SHADER SETTINGS
    /** The shader program owned by this SpriteBatch */
    private final ShaderProgram shader;
    /** The custom shader program that is not owned by this SpriteBatch */
    private ShaderProgram customShader = null;
    /** Whether or not the active shader is owned by this SpriteBatch */
    private boolean ownsShader;

    /** The current tint color representing at a packed color */
    float color = Color.WHITE.toFloatBits();

    /** Number of render calls since the last {@link #begin()}. **/
    public int renderCalls = 0;

    /** Number of rendering calls, ever. Will not be reset unless set manually. **/
    public int totalRenderCalls = 0;

    /** The maximum number of sprites rendered in one batch so far. **/
    public int maxSpritesInBatch = 0;
    
    
    // CACHE objects
    /** A temporary vector for cached computations */
    private Vector2 tempVect = new Vector2();
    /** Temporary colors for cached computations */
    private Color tempColor = new Color(1, 1, 1, 1);
    private Color multColor = new Color(1, 1, 1, 1);


    /** 
     * Constructs a new VertexBatch capacity for 1000 vertices, one buffer, and the default shader.
     *
     * The VertexBatch uses a projection matrix for an orthographic projection with the 
     * y-axis point upwards, x-axis point to the right and the origin being in the bottom 
     * left corner of the screen. The projection will be pixel perfect with respect to the
     * current screen resolution.
     *
     * When the VertexBatch fills to capacity, or when the texture changes, it will flush
     * to the screen. Otherwise, nothing will be drawn until the end() command is called.
     *
     * @see VertexBatch#VertexBatch(int, ShaderProgram) 
     */
    public VertexBatch () {
        this(1000, null);
    }

    /** 
     * Constructs a VertexBatch with one buffer and the default shader.
     *
     * The VertexBatch uses a projection matrix for an orthographic projection with the 
     * y-axis point upwards, x-axis point to the right and the origin being in the bottom 
     * left corner of the screen. The projection will be pixel perfect with respect to the
     * current screen resolution.
     *
     * The value size is the vertex capacity of the buffer. When the VertexBatch fills
     * to capacity, or when the texture changes, it will flush to the screen. Otherwise, 
     * nothing will be drawn until the end() command is called.
     *
     * @param size The max number of vertices in a single batch. Max of 32767.
     *
     * @see VertexBatch#VertexBatch(int, ShaderProgram) 
     */
    public VertexBatch (int size) {
        this(size, null);
    }

    /** 
     * Constructs a new VertexBatch. 
     *
     * The VertexBatch uses a projection matrix for an orthographic projection with the 
     * y-axis point upwards, x-axis point to the right and the origin being in the bottom 
     * left corner of the screen. The projection will be pixel perfect with respect to the
     * current screen resolution.
     * 
     * The value size is the vertex capacity of the buffer. When the VertexBatch fills
     * to capacity, or when the texture changes, it will flush to the screen. Otherwise, 
     * nothing will be drawn until the end() command is called.
     *
     * The defaultShader specifies the shader to use. See {@link #createDefaultShader()} 
     * for more details.  If it is not null, this shader is not owned by the SpriteBatch 
     * and must be disposed separately.
     *
     * @param size The max number of vertices in a single batch. Max of 32767.
     * @param defaultShader The default shader to use. 
     */
    public VertexBatch (int size, ShaderProgram defaultShader) {
        // 32767 is max vertex index
        if (size > 32767) throw new IllegalArgumentException("Can't have more than 32767 vertices per batch: " + size);

        // Build the mesh
        VertexDataType vertexDataType = (Gdx.gl30 != null) ? VertexDataType.VertexBufferObjectWithVAO : VertexDataType.VertexArray;
        mesh = new Mesh(vertexDataType, false, size, size,
            new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
            new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
        
        // Create the backing buffer
        vertices = new float[size*VertexBuffer.STRIDE];
        indices  = new short[size];

        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (defaultShader == null) {
            shader = createDefaultShader();
            ownsShader = true;
        } else {
            shader = defaultShader;
        }

        // Create a blank texture
        Pixmap map = new Pixmap(BLANK_SIZE,BLANK_SIZE,Pixmap.Format.RGBA4444);
        map.setColor(Color.WHITE);
        map.fillRectangle(0, 0, BLANK_SIZE, BLANK_SIZE);
        blank = new Texture(map);
    }

    /** 
     * Returns a new instance of the default shader used by VertexBatch
     *
     * The vertex position attribute is called "a_position", the texture coordinates 
     * attribute is called "a_texCoord0", the color attribute is called "a_color". See
     * {@link ShaderProgram#POSITION_ATTRIBUTE}, {@link ShaderProgram#COLOR_ATTRIBUTE} 
     * and {@link ShaderProgram#TEXCOORD_ATTRIBUTE}; the last gets "0" appended to 
     * indicate the use of the first texture unit. 
     * 
     * The combined transform and projection matrx is uploaded via a mat4 uniform called 
     * "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
     *
     * This shader has been tested on Desktop platforms. There is no guarantee that
     * it works on mobile platforms.
     *
     * This method transfers ownership of the shader.  It is the responsibility of the
     * caller to dispose of the shader when it is no longer necessary.
     *
     * @return a new instance of the default shader used by SpriteBatch
     */
    static public ShaderProgram createDefaultShader () {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "   v_color.a = v_color.a * (255.0/254.0);\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
            + "#define LOWP lowp\n" //
            + "precision mediump float;\n" //
            + "#else\n" //
            + "#define LOWP \n" //
            + "#endif\n" //
            + "varying LOWP vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "uniform sampler2D u_texture;\n" //
            + "void main()\n"//
            + "{\n" //
            + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
            + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }
    
    @Override
    /**
     * Releases the mesh and SpriteBatch30 shader, provided it is owned
     *
     * This method should be called on any SpriteBatch when it is no longer
     * needed, even if it has a nonstandard shader.
     */
    public void dispose () {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    // CAMERA AND TRANSFORMS
    /**
     * Returns the current projection matrix. 
     *
     * Changing this value within {@link #begin()}/{@link #end()} results in undefined 
     * behaviour. 
     *
     * @return the current projection matrix.      
     */
    public Matrix4 getProjectionMatrix () {
        return projectionMatrix;
    }

    /**
     * Sets the projection matrix to be used for subsequent images
     *
     * If this is called inside a {@link #begin()}/{@link #end()} block, the current 
     * batch is flushed to the gpu. 
     *
     * @param projection     the projection matrix to be used for subsequent images
     */
    public void setProjectionMatrix(Matrix4 projection) {
        if (drawing) flush();
        projectionMatrix.set(projection);
        if (drawing) setupMatrices();
    }

    /**
     * Returns the current transform matrix. 
     *
     * Changing this value within {@link #begin()}/{@link #end()} results in undefined 
     * behaviour. 
     *
     * @return the current transform matrix.      
     */
    public Matrix4 getTransformMatrix() {
        return transformMatrix;
    }

    /**
     * Sets the transform matrix to be used for subsequent images
     *
     * If this is called inside a {@link #begin()}/{@link #end()} block, the current 
     * batch is flushed to the gpu. 
     *
     * @param transform     the transform matrix to be used for subsequent images
     */
    public void setTransformMatrix(Matrix4 transform) {
        if (drawing) flush();
        transformMatrix.set(transform);
        if (drawing) setupMatrices();
    }

    /**
     * Applies the projection and transform matrices to the shader
     */
    private void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        if (customShader != null) {
            customShader.setUniformMatrix("u_projTrans", combinedMatrix);
            customShader.setUniformi("u_texture", 0);
        } else {
            shader.setUniformMatrix("u_projTrans", combinedMatrix);
            shader.setUniformi("u_texture", 0);
        }
    }

    // BEGIN/END Methods
    /** 
     * Sets up the Batch for drawing. 
     *
     * This will disable depth buffer writing. It enables blending and texturing. If you 
     * have more texture units enabled than the first one you have to disable them before 
     * calling this. This pass uses a screen coordinate system by default where everything 
     * is given in pixels. You can specify your own projection and modelview matrices via
     * {@link #setProjectionMatrix(Matrix4)} and {@link #setTransformMatrix(Matrix4)}.
     */
    public void begin() {
        if (drawing) throw new IllegalStateException("VertexBatch.end must be called before begin.");
        renderCalls = 0;

        Gdx.gl.glDepthMask(false);
        if (customShader != null) {
            customShader.begin();
        } else {
            shader.begin();
        }
        setupMatrices();

        drawing = true;
    }

    /** 
     * Finishes off rendering. 
     *
     * This method enables depth writes, disables blending and texturing. It must always 
     * be called after a call to {@link #begin()} 
     */
    public void end () {
        if (!drawing) throw new IllegalStateException("VertexBatch.begin must be called before end.");
        if (ipos > 0) flush();
        lastTexture = null;
        drawing = false;

        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        if (isBlendingEnabled()) gl.glDisable(GL20.GL_BLEND);

        if (customShader != null) {
            customShader.end();
        } else {
            shader.end();
        }
    }
    
    /**
     * Returns true if the Batch is actively drawing
     *
     * This method returns true if it is  inside a {@link #begin()}/{@link #end()} block.
     * Otherwise, it returns false.
     *
     * @return true if the Batch is actively drawing
     */
    public boolean isDrawing() {
        return drawing;
    }

    /** 
     * Sets the color used to tint images when they are added to the Batch. 
     *
     * The color can be changed at any time.  Changes are applied to subsequently added
     * textures.  The default is {@link Color#WHITE}. 
     *
     * @param tint    the color to tint images when they are added 
     */
    public void setColor (Color tint) {
        color = tint.toFloatBits();
    }

    /**
     * Sets the color used to tint images when they are added to the Batch. 
     *
     * The color can be changed at any time.  Changes are applied to subsequently added
     * textures. The color values are expressed in the range [0,1]. The default is 
     * {@link Color#WHITE}, which is (1,1,1,1).
     *
     * @param  r    the amount of red to tint images when they are added 
     * @param  g    the amount of green to tint images when they are added 
     * @param  b    the amount of blue to tint images when they are added 
     * @param  a    the amount of transparency to tint images when they are added 
     *
     * @see #setColor(Color)
     */
    public void setColor (float r, float g, float b, float a) {
        int intBits = (int)(255 * a) << 24 | (int)(255 * b) << 16 | (int)(255 * g) << 8 | (int)(255 * r);
        color = NumberUtils.intToFloatColor(intBits);
    }

    /**
     * Sets the color used to tint images when they are added to the Batch. 
     *
     * The color can be changed at any time.  Changes are applied to subsequently added
     * textures. The color value is expressed as in vertex format, according to the method
     * {@link Color#toFloatBits()}. The default is {@link Color#WHITE}.
     *
     * @param color    the color to tint images when they are added 
     *
     * @see #setColor(Color)
     */
     public void setColor (float color) {
        this.color = color;
    }

    /**
     * Returns the current rendering color of this Batch. 
     *
     * Manipulating the returned instance has no effect. 
     *
     * @return the current rendering color of this Batch. 
     * @see #setColor(Color)
     */
    public Color getColor () {
        Color.abgr8888ToColor(tempColor,color);
        return tempColor;
    }

    /** 
     * Returns the rendering color of this Batch in vertex format
     *
     * The format is defined according to the method {@link Color#toFloatBits()}. 
     *
     * @return the rendering color of this Batch in vertex format
     * @see #setColor(float)
     */
    public float getPackedColor () {
        return color;
    }

    /**
     * Disables blending for drawing sprites. 
     *
     * Calling this within {@link #begin()}/{@link #end()} will flush the batch. 
     */
    public void disableBlending () {
        if (blendingDisabled) return;
        flush();
        blendingDisabled = true;
    }

    /**
     * Enables blending for drawing sprites. 
     *
     * Calling this within {@link #begin()}/{@link #end()} will flush the batch. 
     */
    public void enableBlending () {
        if (!blendingDisabled) return;
        flush();
        blendingDisabled = false;
    }

    /**
     * Returns true if blending for sprites is enabled 
     *
     * @return true if blending for sprites is enabled 
     */
    public boolean isBlendingEnabled () {
        return !blendingDisabled;
    }

    /**
     * Sets the blending function to be used when rendering sprites.
     *
     * The functions are the same values as for glBlendFunction(). By default, the
     * srcFunc is GL20.GL_SRC_ALPHA, while dstFunc is GL20.GL_ONE_MINUS_SRC_ALPHA.
     * Assigning -1 to srcFunc means that the batch will not chance the function.
     *
     * @param srcFunc    the source function; unchanged if set to -1
     * @param dstFunc     the destination function
     */
    public void setBlendFunction (int srcFunc, int dstFunc) {
        if (blendSrcFunc == srcFunc && blendDstFunc == dstFunc) return;
        flush();
        blendSrcFunc = srcFunc;
        blendDstFunc = dstFunc;
    }

    /**
     * Returns the source function for blending
     * 
     * By default, this is GL20.GL_SRC_ALPHA
     *
     * @return the source function for blending
     */
    public int getBlendSrcFunc () {
        return blendSrcFunc;
    }

    /**
     * Returns the destination function for blending
     * 
     * By default, this is GL20.GL_ONE_MINUS_SRC_ALPHA
     *
     * @return the source function for blending
     */
    public int getBlendDstFunc () {
        return blendDstFunc;
    }

    // BASIC DRAWING METHODS
    /**
     * Draws the given VertexBuffer with the blank texture
     *
     * Since no indices are specified, this method assumes that the VertexBuffer is
     * a sequence of triangles (e.g. the size is a multiple of 3), and draws them
     * in order.
     *
     * @param vertices  The vertex buffer to draw
     */
    public void draw (VertexBuffer vertices) {
        draw(null,vertices);
    }

    /**
     * Draws and transforms a VertexBuffer with the blank texture
     *
     * The transform is applied to the vertex positions, not the texture coordinates.
     * The transform is applied by the CPU, so there is no context switch by the
     * graphics card.
     * 
     * Since no indices are specified, this method assumes that the VertexBuffer is
     * a sequence of triangles (e.g. the size is a multiple of 3), and draws them
     * in order.
     *
     * @param vertices  The vertex buffer to draw
     * @param transform The drawing transform
     */
    public void draw (VertexBuffer vertices, Affine2 transform) {
        draw(null,vertices,transform);
    }

    /**
     * Draws the given VertexBuffer with the given texture
     *
     * Since no indices are specified, this method assumes that the VertexBuffer is
     * a sequence of triangles (e.g. the size is a multiple of 3), and draws them
     * in order.
     *
     * @param texture   The texture to apply
     * @param vertices  The vertex buffer to draw
     */
    public void draw (Texture texture, VertexBuffer vertices) {
        draw(texture,vertices,(Affine2)null);
    }

    /**
     * Draws and transforms a VertexBuffer with the given texture
     *
     * The transform is applied to the vertex positions, not the texture coordinates.
     * The transform is applied by the CPU, so there is no context switch by the
     * graphics card.
     * 
     * Since no indices are specified, this method assumes that the VertexBuffer is
     * a sequence of triangles (e.g. the size is a multiple of 3), and draws them
     * in order.
     *
     * @param texture   The texture to apply
     * @param vertices  The vertex buffer to draw
     * @param transform The drawing transform
     */
    public void draw (Texture texture, VertexBuffer vertices, Affine2 transform) {
        if (!drawing) throw new IllegalStateException("VertexBatch.begin must be called before draw.");
        if (vertices == null) throw new NullPointerException("No vertices to draw");
        if (vertices.size() % 3 != 0)  throw new IllegalStateException("The VertexBuffer size must be a multiple of 3");

        float[] buffer = this.vertices;

        if (texture != lastTexture) {
            switchTexture(texture);
        } else if (ipos+vertices.size() >= indices.length) {
            flush();
        }

        float color = this.color;
        Color.abgr8888ToColor(tempColor,color);
        int vpos = this.vpos;
        int ipos = this.ipos;

        float[] data = vertices.getData();
        for(int ii = 0; ii < vertices.size(); ii++) {
            switch (ii % VertexBuffer.STRIDE) {
                case 0:
                    indices[ipos++] = (short)((ii+vpos)/5);
                    tempVect.set(data[ii],data[ii+1]);
                    if (transform != null) {
                        transform.applyTo(tempVect);
                    }
                    buffer[ii+vpos] = tempVect.x;
                    break;
                case 1:
                    buffer[ii+vpos] = tempVect.y;
                    break;
                case 2:
                    Color.abgr8888ToColor(multColor,data[ii]);
                    multColor.mul(tempColor);
                    buffer[ii+vpos] = multColor.toFloatBits();
                    break;
                case 3:
                case 4:
                    buffer[ii+vpos] = data[ii];
                    break;
            }
        }
        this.vpos = vpos+vertices.size();
        this.ipos = ipos;
    }

    /**
     * Draws the indexed VertexBuffer with the blank texture
     *
     * The indices corespond indices in the vertex buffer.  The batch assumes that the
     * indices specify triangles, so the array size should be a multiple of 3.
     *
     * @param vertices  The buffer storing the indexed vertices
     * @param indices   The indices defining the triangles
     */
    public void draw (VertexBuffer vertices, short[] indices) {
        draw(null,vertices,indices,null);
    }

    /**
     * Draws and transforms the indexed VertexBuffer with the blank texture
     *
     * The transform is applied to the vertex positions, not the texture coordinates.
     * The transform is applied by the CPU, so there is no context switch by the
     * graphics card.
     *
     * The indices corespond indices in the vertex buffer.  The batch assumes that the
     * indices specify triangles, so the array size should be a multiple of 3.
     *
     * @param vertices  The buffer storing the indexed vertices
     * @param indices   The indices defining the triangles
     */
    public void draw (VertexBuffer vertices, short[] indices, Affine2 transform) {
        draw(null,vertices,indices,transform);
    }

    /**
     * Draws the indexed VertexBuffer with the given texture
     *
     * The indices corespond indices in the vertex buffer.  The batch assumes that the
     * indices specify triangles, so the array size should be a multiple of 3.
     *
     * @param texture   The texture to apply
     * @param vertices  The buffer storing the indexed vertices
     * @param indices   The indices defining the triangles
     */
    public void draw (Texture texture, VertexBuffer vertices, short[] indices) {
        draw(texture,vertices,indices,null);
    }

    /**
     * Draws and transforms the indexed VertexBuffer with the given texture
     *
     * The transform is applied to the vertex positions, not the texture coordinates.
     * The transform is applied by the CPU, so there is no context switch by the
     * graphics card.
     *
     * The indices correspond to indices in the vertex buffer.  The batch assumes that the
     * indices specify triangles, so the array size should be a multiple of 3.
     *
     * @param texture   The texture to apply
     * @param vertices  The buffer storing the indexed vertices
     * @param indices   The indices defining the triangles
     */
    public void draw (Texture texture, VertexBuffer vertices, short[] indices, Affine2 transform) {
        if (!drawing) throw new IllegalStateException("VertexBatch.begin must be called before draw.");
        if (vertices == null) throw new NullPointerException("No vertices to draw");
        if (indices.length % 3 != 0)  throw new IllegalStateException("The index array length must be a multiple of 3");

        float[] buffer = this.vertices;
        short[] edges  = this.indices;

        if (texture != lastTexture) {
            switchTexture(texture);
        } else if (ipos+vertices.size() >= indices.length) {
            flush();
        }

        float color = this.color;
        Color.abgr8888ToColor(tempColor,color);
        int vpos = this.vpos;
        int ipos = this.ipos;

        float[] data = vertices.getData();
        for(int ii = 0; ii < vertices.size(); ii++) {
            switch (ii % VertexBuffer.STRIDE) {
                case 0:
                    tempVect.set(data[ii],data[ii+1]);
                    if (transform != null) {
                        transform.applyTo(tempVect);
                    }
                    buffer[ii+vpos] = tempVect.x;
                    break;
                case 1:
                    buffer[ii+vpos] = tempVect.y;
                    break;
                case 2:
                    Color.abgr8888ToColor(multColor,data[ii]);
                    multColor.mul(tempColor);
                    buffer[ii+vpos] = multColor.toFloatBits();
                    break;
                case 3:
                case 4:
                    buffer[ii+vpos] = data[ii];
                    break;
            }
        }
        this.vpos = vpos+vertices.size();
        for(int ii = 0; ii < indices.length; ii++) {
            edges[ipos+ii] = indices[ii];
        }
        this.ipos = ipos+indices.length;
    }

    /** 
     * Causes any pending sprites to be rendered, without ending the Batch. 
     */
    public void flush () {
        if (ipos == 0) return;

        renderCalls++;
        totalRenderCalls++;
        int vertsInBatch = vpos / 5;
        if (vertsInBatch > maxSpritesInBatch) maxSpritesInBatch = vertsInBatch;

        if (lastTexture == null) {
            blank.bind();
        } else {
            lastTexture.bind();
        }
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, vpos);
        mesh.setIndices(indices, 0, ipos);

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1) Gdx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
        }

        mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, ipos);

        vpos = 0;
        ipos = 0;
    }

    /** 
     * Switches the active texture to the one provided.
     *
     * Switching the texture always causes a flush of the Batch.
     *
     * @param texture the new active texture 
     */
    protected void switchTexture (Texture texture) {
        flush();
        lastTexture = texture;
        if (texture == null) {
            invTexWidth  = 1.0f/blank.getWidth();
            invTexHeight = 1.0f/blank.getHeight();
        } else {
            invTexWidth = 1.0f / texture.getWidth();
            invTexHeight = 1.0f / texture.getHeight();
        }
    }

    // SHADER CUSTOMIZATION
    /** 
     * Sets the shader to be used in a GLES 2.0 environment.
     *
     * The vertex position attribute is called "a_position", the texture coordinates 
     * attribute is called "a_texCoord0", the color attribute is called "a_color". See
     * {@link ShaderProgram#POSITION_ATTRIBUTE}, {@link ShaderProgram#COLOR_ATTRIBUTE} 
     * and {@link ShaderProgram#TEXCOORD_ATTRIBUTE}; the last gets "0" appended to 
     * indicate the use of the first texture unit. 
     * 
     * The combined transform and projection matrx is uploaded via a mat4 uniform called 
     * "u_projTrans". The texture sampler is passed via a uniform called "u_texture".
     *
     * Calling this method with a null argument will use the default shader.
     *
     * This method will flush the batch before setting the new shader.  Therefore, you 
     * can call it in between {@link #begin()} and {@link #end()}.
     *
     * This method DOES NOT transfer ownership of the shader.  It is the responsibility 
     * of the caller to dispose of the shader when it is no longer necessary.
     *
     * @param shader the {@link ShaderProgram} or null to use the default shader. 
     */
    public void setShader (ShaderProgram shader) {
        if (drawing) {
            flush();
            if (customShader != null)
                customShader.end();
            else
                this.shader.end();
        }
        customShader = shader;
        if (drawing) {
            if (customShader != null)
                customShader.begin();
            else
                this.shader.begin();
            setupMatrices();
        }
    }

    /** 
     * Returns the current {@link ShaderProgram}.
     *
     * This value is set by {@link #setShader(ShaderProgram)} or the defaultShader.
     * Modifying this value at all (even outside of a {@link #begin()}/{@link #end()}
     * block) results in undefined behavior.
     *
     * @return the current {@link ShaderProgram}.
    */
    public ShaderProgram getShader () {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }
}