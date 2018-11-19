package unsw.graphics.world;

import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;


/**
 * Extentions:
 *
 * Make the sun move and change colour according to the time of day (world/Sun.java, world/Terrain.java)
 * Add distance attenuation to the torch light (fragment_tex_phong_assignment2.glsl)
 *
 */
public class World extends Application3D {
    private Terrain terrain;
    private Camera camera;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        camera = new Camera(terrain);
        //github commit test
		//github commit test 2
		//github commit test 3
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
    	super.display(gl);
		camera.setView(gl);
    	terrain.draw(gl);
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		terrain.init(gl);

        getWindow().addKeyListener(camera);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
}
