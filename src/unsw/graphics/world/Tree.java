package unsw.graphics.world;

import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

import java.awt.*;
import java.io.IOException;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {
    private Point3D position;

    private TriangleMesh mesh;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
    }
    
    public Point3D getPosition() {
        return position;
    }

    public void init(GL3 gl) {
        try {
            mesh = new TriangleMesh("res/models/tree.ply", true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mesh.init(gl);
    }

    public void draw(GL3 gl, CoordFrame3D view) {
        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();
        float scale = 0.25f;

        CoordFrame3D treeView = view.translate(x, y + 5 * scale, z).scale(scale, scale, scale);

        mesh.draw(gl, treeView);
    }
}
