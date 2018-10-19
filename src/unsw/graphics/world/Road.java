package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.*;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.Triangle3D;
import unsw.graphics.scene.MathUtil;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    private Terrain terrain;

    private int segments = 100;
    
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine) {
        this.width = width;
        this.points = spine;
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        Texture texture = new Texture(gl, "res/textures/rock.bmp", "bmp", true);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        float dt = 1.0f/segments;
        float y = terrain.altitude(points.get(0).getX(), points.get(0).getY()) + 0.01f;

        for (int n = 0; n < size(); n ++) {
            List<float[]> normals = new ArrayList<>();
            for (int i = 0; i < segments; i++) {
                float fstT = i * dt;
                float sndT = (i + 1) * dt;

                if (i == segments - 1) {
                    fstT = i * dt;
                    sndT = (i - 1) * dt;
                }

                Point2D fstPoint = point(fstT + n);
                Point2D sndPoint = point(sndT + n);

                float[] fstToSnd = {
                        sndPoint.getX() - fstPoint.getX(), 0, sndPoint.getY() - fstPoint.getY(), 1};

                float[] normal = MathUtil.getUnitVector(MathUtil.crossProduct(new float[]{0, 1, 0, 1}, fstToSnd));
                normal = MathUtil.multiply(MathUtil.scaleMatrix(width / 2), normal);

                normals.add(normal);
            }

            for (int i = 0; i < segments - 1; i++) {
                float fstT = i * dt;
                float sndT = (i + 1) * dt;
                Point2D fstPoint = point(fstT + n);
                Point2D sndPoint = point(sndT + n);

                Point3D fstLeft = new Point3D(
                        fstPoint.getX() - normals.get(i)[0], y, fstPoint.getY() - normals.get(i)[2]);
                Point3D fstRight = new Point3D(
                        fstPoint.getX() + normals.get(i)[0], y, fstPoint.getY() + normals.get(i)[2]);
                Point3D sndLeft = new Point3D(
                        sndPoint.getX() - normals.get(i + 1)[0], y, sndPoint.getY() - normals.get(i + 1)[2]);
                Point3D sndRight = new Point3D(
                        sndPoint.getX() + normals.get(i + 1)[0], y, sndPoint.getY() + normals.get(i + 1)[2]);

                Triangle3D bottomRight = new Triangle3D(
                        fstLeft.getX(), fstLeft.getY(), fstLeft.getZ(),
                        sndRight.getX(), sndRight.getY(), sndRight.getZ(),
                        fstRight.getX(), fstRight.getY(), fstRight.getZ()
                );

                Triangle3D topLeft = new Triangle3D(
                        fstLeft.getX(), fstLeft.getY(), fstLeft.getZ(),
                        sndLeft.getX(), sndLeft.getY(), sndLeft.getZ(),
                        sndRight.getX(), sndRight.getY(), sndRight.getZ()
                );

                bottomRight.draw(gl, frame);
                topLeft.draw(gl, frame);
            }
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
