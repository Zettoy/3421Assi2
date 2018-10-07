package unsw.graphics.world;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import unsw.graphics.CoordFrame3D;

public class Camera implements KeyListener {
    private static final CoordFrame3D defaultView = CoordFrame3D.identity().translate(0, -3, 0).rotateX(12);

    private Terrain terrain;
    private CoordFrame3D view;

    private float x;
    private float z;
    private float rotate;

    public Camera(Terrain terrain) {
        this.terrain = terrain;

        // This preset creates a full view of test1.json
        x = 4.5f;
        z = 15;

        setView();
    }

    private void setView() {
        float y = terrain.altitude(x, z);
        view = defaultView.rotateY(-rotate).translate(-x, -y, -z);
    }

    public CoordFrame3D getView() {
        return view;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:
                x -= Math.sin(Math.toRadians(rotate)) / 2;
                z -= Math.cos(Math.toRadians(rotate)) / 2;
                break;
            case KeyEvent.VK_DOWN:
                x += Math.sin(Math.toRadians(rotate)) / 2;
                z += Math.cos(Math.toRadians(rotate)) / 2;
                break;
            case KeyEvent.VK_LEFT:
                rotate += 5;
                break;
            case KeyEvent.VK_RIGHT:
                rotate -= 5;
                break;
        }
        setView();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
