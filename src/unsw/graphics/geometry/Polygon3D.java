/**
 * 
 */
package unsw.graphics.geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A convex polygon in 2D space.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Polygon3D {
    private List<Point3D> points;

    public Polygon3D(List<Point3D> points) {
        this.points = new ArrayList<Point3D>(points);
    }

    /**
     * Draw the polygon in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer buffer = new Point3DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 3 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    public void drawOutline(GL3 gl, CoordFrame2D frame) {
        // TODO: You need to write this method.
        // It should draw an outline of a polygon using GL_LINE_LOOP
    }
    
    /**
     * Draw the polygon on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }

}
