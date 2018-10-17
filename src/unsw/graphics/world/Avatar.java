package unsw.graphics.world;

import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

import java.io.IOException;

public class Avatar {
    private Point3D position;

    private TriangleMesh mesh;
    private CoordFrame3D view;

    public Avatar(float x, float y, float z) {
        position = new Point3D(x, y, z);
    }

    public Point3D getPosition() {
        return position;
    }

    public void init(GL3 gl) {
        try {
            mesh = new TriangleMesh("res/models/bunny.ply", true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mesh.init(gl);
    }

    public void draw(GL3 gl) {
        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();
        CoordFrame3D avatarView = view
                .translate(x, y, z)
                .rotateY(-90)
                .scale(10, 10, 10);

        mesh.draw(gl, avatarView);
    }

    public void setView(CoordFrame3D view) {
        this.view = view;
    }
}
