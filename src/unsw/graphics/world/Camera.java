package unsw.graphics.world;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;

public class Camera implements KeyListener {
    private Terrain terrain;

    private float x = 4.5f;
    private float z = 15f;
    private float rotate;

    private boolean thirdPesron;
    private boolean nightMode;
    private boolean sunMode;

    public Camera(Terrain terrain) {
        this.terrain = terrain;
    }

    public void setView(GL3 gl) {
        float y = terrain.altitude(x, z);

        float tempX = x;
        float tempZ = z;

        if (thirdPesron) {
            tempX += (float) Math.sin(Math.toRadians(rotate)) * 6;
            tempZ += (float) Math.cos(Math.toRadians(rotate)) * 6;
        }

        CoordFrame3D view = CoordFrame3D.identity().rotateX(12)
                .rotateX(78).translate(0, -10, 10)
                .rotateY(-rotate).translate(-tempX, -y, -tempZ);
        Shader.setViewMatrix(gl, view.getMatrix());

        CoordFrame3D avatarView = CoordFrame3D.identity()
                .translate(x, y, z)
                .rotateY(rotate);

        if (thirdPesron) terrain.getAvatar().setView(avatarView);

        if (nightMode) {
            float[] s = avatarView.getMatrix().getValues();
            terrain.setTorchlightDir(s[12], s[13] - 1, s[14]);
        }

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
            case KeyEvent.VK_SPACE:
                thirdPesron = !thirdPesron;
                terrain.setThirdPerson(thirdPesron);
                break;
            case KeyEvent.VK_N:
                nightMode = !nightMode;
                terrain.setNightMode(nightMode);
                break;
            case KeyEvent.VK_S:
                sunMode = !sunMode;
                terrain.setSunMode(sunMode);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
