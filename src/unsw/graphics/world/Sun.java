package unsw.graphics.world;

import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sun {
    private static final int VERTICES = 32;
    private static final float RADIUS = 3;

    private Point3D position;
    private Polygon3D poly;

    public Sun(float x, float y, float z) {
        position = new Point3D(x, y, z);
    }

    public void init(GL3 gl) {
        List<Point3D> points = new ArrayList<>();
        for (int i = 0; i < VERTICES; i++) {
            float a = (float) (i * Math.PI * 2 / VERTICES); // java.util.Math uses radians!!!
            float x = RADIUS * (float) Math.cos(a);
            float y = RADIUS * ((float) Math.sin(a) + 1); // Off center
            Point3D p = new Point3D(x, y, 0);
            points.add(p);
        }

        poly = new Polygon3D(points);
    }

    public void draw(GL3 gl, CoordFrame3D view) {
        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();
        float scale = 0.5f;

        CoordFrame3D sunView = view.translate(x, y, z).scale(scale, scale, scale);

        if (y >= 0 && y < 2) {
            Shader.setPenColor(gl, Color.WHITE);
        } else if (y >= 2 && y < 4) {
            Shader.setPenColor(gl, Color.RED);
        } else if (y >= 4 && y <= 6) {
            Shader.setPenColor(gl, Color.YELLOW);
        }

        poly.draw(gl, sunView);
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Point3D(x, y, z);
    }
}
