package lamparski.tiled_dynamic1;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Program implements ApplicationListener {
    
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private World world;
    private PointLight mLight;
    private ConeLight mRotatingLight;
    private float direction = 0f;

	@Override
	public void create() {
	    tiledMap = new TmxMapLoader().load(Gdx.files.internal("assets/maps/test1.tmx").file().getAbsolutePath());
	    tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 8f);
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 40f, 30f);
	    
	    world = new World(new Vector2(0, 0), true);
	    
	    
	    
	    rayHandler = new RayHandler(world);
	    rayHandler.setCombinedMatrix(camera.combined);
	    rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.1f);
	    
	    mLight = new PointLight(rayHandler, 128, Color.WHITE, 20f, 5f, 5f);
	    mLight.setSoft(true);
	    
	    mRotatingLight = new ConeLight(rayHandler, 128, Color.RED, 20f, 20f, 10f, direction, 20f);
	    
	    camera.update();
	}

	@Override
	public void resize(int width, int height) {

	}
	
	private void update(){
	    /*
	     * We're polling for input here, because using an InputProcessor
	     * is wonky (as in, it moves the light a bit, then just stops
	     * even if the key is held down). It is not a switch/case because
	     * light can also move diagonally.
	     */
	    if(Gdx.input.isKeyPressed(Input.Keys.UP))
	        mLight.setPosition(mLight.getPosition().x, mLight.getPosition().y + 1);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            mLight.setPosition(mLight.getPosition().x, mLight.getPosition().y - 1);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            mLight.setPosition(mLight.getPosition().x - 1, mLight.getPosition().y);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            mLight.setPosition(mLight.getPosition().x + 1, mLight.getPosition().y);
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
        
        direction += 1f;
        if (direction == 360f)
            direction = 0f;
        mRotatingLight.setDirection(direction);
	}

	@Override
	public void render() {
	    update();
	    
	    Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1f);
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    camera.update();
	    
	    tiledMapRenderer.setView(camera);
	    tiledMapRenderer.render();
	    rayHandler.updateAndRender();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
	    rayHandler.dispose();
	    tiledMapRenderer.dispose();
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration myConf = new LwjglApplicationConfiguration();
		/*
		 * In an actual game, these should be loaded from a file that can be edited
		 * using either a simple config tool separate from the game, or a text
		 * editor (it could be JSON or INI, actually)
		 */
		myConf.height = 600;
		myConf.width = 800;
		myConf.useGL20 = true; // yes, even on cpu graphics
		myConf.foregroundFPS = 60;
		myConf.title = "Tiled Dynamic -- 1";
		
		new LwjglApplication(new Program(), myConf);
	}

}