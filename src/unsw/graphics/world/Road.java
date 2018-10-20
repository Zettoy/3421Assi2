package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.*;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {
    private static final int SEGMENTS = 100;

    private TriangleMesh mesh;

    private List<Point2D> points;
    private float width;
    private Terrain terrain;

    private Texture texture;
    
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

    public void init(GL3 gl) {
        List<Point3D> points = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Point2D> textCoords = new ArrayList<>();

        float dt = 1.0f/ SEGMENTS;

        // altitute = altitute at the beginning of the road
        float y = terrain.altitude(this.points.get(0).getX(), this.points.get(0).getY()) + 0.01f;

        // for spline curves
        for (int n = 0; n < size(); n ++) {
            List<Vector3> normals = new ArrayList<>();

            // compute the normals at each point
            for (int i = 0; i <= SEGMENTS; i++) {
                float fstT = i * dt + n;
                float sndT = (i + 1) * dt + n;

                if (i == SEGMENTS - 1) {
                    fstT = i * dt + n;
                    sndT = (i - 1) * dt + n;
                }

                Point2D fstPoint, sndPoint;
                if (i == SEGMENTS) {
                    fstPoint = this.points.get(3 * (n + 1));
                    sndPoint = point((i - 1) * dt + n);

                } else {
                    fstPoint = point(fstT);
                    sndPoint = point(sndT);
                }

                Vector3 fstToSnd = new Vector3(sndPoint.getX() - fstPoint.getX(), 0, sndPoint.getY() - fstPoint.getY());
                Vector3 normal = new Vector3(0, 1, 0).cross(fstToSnd).normalize();
                Matrix4 scale = Matrix4.scale(width / 2, width / 2, width / 2);
                normal = scale.multiply(new Vector4(normal.getX(), normal.getY(), normal.getZ(), 1f)).trim();

                if (i >= SEGMENTS - 1) normal = normal.negate();
                normals.add(normal);
            }

            // compute the points on each normal
            for (int i = 0; i <= SEGMENTS; i++) {
                float fstT = i * dt + n;
                Point2D fstPoint;

                if (i == SEGMENTS) {
                    fstPoint = this.points.get(3 * (n + 1));
                } else {
                    fstPoint = point(fstT);
                }

                Point3D fstLeft = new Point3D(
                        fstPoint.getX() - normals.get(i).getX(), y, fstPoint.getY() - normals.get(i).getZ());
                Point3D fstRight = new Point3D(
                        fstPoint.getX() + normals.get(i).getX(), y, fstPoint.getY() + normals.get(i).getZ());

                points.add(fstLeft);
                points.add(fstRight);
            }
        }

        for (Point3D p : points)
            textCoords.add(new Point2D(p.getX() / width, p.getZ() / width));

        for (int i = 0; i < points.size() - 2; i += 2) {
            // top left triangle
            indices.add(i);
            indices.add(i + 2);
            indices.add(i + 1);

            // bottom right triangle
            indices.add(i + 1);
            indices.add(i + 2);
            indices.add(i + 3);
        }

        texture = new Texture(gl, "res/textures/rock.bmp", "bmp", true);

        mesh = new TriangleMesh(points, indices, true, textCoords);
        mesh.init(gl);
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        Shader.setInt(gl, "tex", 1);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        mesh.draw(gl, frame);
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
     * Get the number of SEGMENTS in the curve
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
