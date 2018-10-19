package unsw.graphics.world;



import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {
    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;

    private TriangleMesh mesh;
    private Texture texture;

    private Avatar avatar;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

     /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        if (x > width - 1 || x < 0 || z > depth - 1 || z < 0) return 0;

        int x0 = (int) x;
        int z0 = (int) z;

        if (x == x0 & z == z0) return (float) getGridAltitude(x0, z0);

        int x1 = x0 + 1;
        int z1 = z0 + 1;
        double altitude = 0;

        if (x != x0 && z != z0) {
            double r1 = (x - x0) * getGridAltitude(x1, z0) + (x1 - x) * getGridAltitude(x0, z0);
            double r2 = (x - x0) * getGridAltitude(x1, z1) + (x1 - x) * getGridAltitude(x0, z1);
            altitude = (z - z0) * r2 + (z1 - z) * r1;

        } else if (x != x0 && z == z0) {
            altitude = (x - x0) * getGridAltitude(x1, z0) + (x1 - x) * getGridAltitude(x0, z0);

        } else if (x == x0 && z != z0) {
            altitude = (z - z0) * getGridAltitude(x0, z1) + (z1 - z) * getGridAltitude(x0, z0);

        }
        
        return (float) altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param width
     * @param spine
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        road.setTerrain(this);
        roads.add(road);        
    }

    public float getDepth() {
        return depth;
    }

    public float getWidth() {
        return width;
    }

    public void init(GL3 gl) {
        List<Point3D> points = new ArrayList<>();
        List<Point2D> textCoords = new ArrayList<>();

        for(int x = 0; x < width; x ++) {
            for(int z = 0; z < depth; z ++) {
                points.add(new Point3D(x, (float) getGridAltitude(x, z), z));
                textCoords.add(new Point2D((float) x / width,(float) z / depth));
            }
        }


        List<Integer> indices = new ArrayList<>();

        for(int x = 0; x < width - 1; x ++) {
            for (int z = 0; z < depth - 1; z++) {
                indices.add(depth * x + z);
                indices.add(depth * x + (z + 1));
                indices.add(depth * (x + 1) + z);

                indices.add(depth * (x + 1) + z);
                indices.add(depth * x + (z + 1));
                indices.add(depth * (x + 1) + (z + 1));
            }
        }

        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);

        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                "shaders/fragment_tex_phong_directional_light.glsl");
        shader.use(gl);

        mesh = new TriangleMesh(points, indices, true, textCoords);
        mesh.init(gl);

        for (Tree t : trees) t.init(gl);

        avatar = new Avatar(0, -3, 0);
        avatar.init(gl);
    }

    public void draw(GL3 gl) {
        Shader.setInt(gl, "tex", 0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        Shader.setPenColor(gl, Color.WHITE);

        Shader.setPoint3D(gl, "lightDir",
                new Point3D(sunlight.getX(), sunlight.getY(), sunlight.getZ()));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));

        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 16f);

        CoordFrame3D view = CoordFrame3D.identity().translate(0, -3, 0);

        mesh.draw(gl, view);

        for (Tree t : trees) t.draw(gl, view);

        avatar.draw(gl);

        for (Road r : roads) r.draw(gl, view);
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
