package lamparski.tiled_dynamic2;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements Screen {
    
    /* Some basics */
    /**
     * The scale factor for the map
     */
    public static final float SCALE_FACTOR = 1 / 8f;
    /**
     * The default number of rays
     */
    private static final int NUM_RAYS = 128;
    
    private static final float _BRIGHTNESS = 0.2f;
    
    /* Physics */
    private World world;
    private Body playerBody;
    
    /* Light */
    private RayHandler rayHandler;
    
    /* The map */
    private TmxMapLoader tmxLoader;
    private TiledMap map;
    
    /* Renderers
     *   The renderer stack works like this:
     *   1. Render the map itself
     *   2. Render sprites and objects
     *   (3. Debug-render the physics objects)
     *   4. Render the lighting and effects 
     */
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer bodyRenderer;

    /* Camera */
    private OrthographicCamera camera;
    
    /* Sprite rendering */
    private SpriteBatch batch;
    private PlayerCharacter player;
    
    public GameScreen() {
        System.out.println("Constructing GameScreen");
        tmxLoader = new TmxMapLoader();
        bodyRenderer = new Box2DDebugRenderer(false, false, false, true, true, true);
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
    }
    
    public void update(float delta){
        if(Gdx.input.isKeyPressed(Input.Keys.END)){
            Rectangle pr = player.getBoundingRectangle();
            System.out.printf("PLAYER: @ %f, %f wh %f, %f\n", pr.x, pr.y, pr.width, pr.height);
            
            //System.out.printf("+ TEXTURE: wh %d, %d\n", pt.getWidth(), pt.getHeight());
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
        
        movePlayer();
        world.step(1 / 60f, 4, 2);
        
        /*
         * game logic (?)
         */
        player.setPosition(playerBody.getPosition().x - player.getWidth() / 2f, playerBody.getPosition().y - player.getHeight() / 2f);
        player.update(delta);
    }

    private void movePlayer() {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            if(player.direction != PlayerCharacter.PlayerFacing.NORTH){
                player.faceUp();
            } else {
                playerBody.setLinearVelocity(0f, 5f);
                player.walk = true;
            }
            return;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            if(player.direction != PlayerCharacter.PlayerFacing.SOUTH){
                player.faceDown();
            } else {
                playerBody.setLinearVelocity(0f, -5f);
                player.walk = true;
            }
            return;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            if(player.direction != PlayerCharacter.PlayerFacing.WEST){
                player.faceLeft();
            } else {
                playerBody.setLinearVelocity(-5f, 0f);
                player.walk = true;
            }
            return;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            if(player.direction != PlayerCharacter.PlayerFacing.EAST){
                player.faceRight();
            } else {
                playerBody.setLinearVelocity(5f, 0f);
                player.walk = true;
            }
            return;
        }
        
        playerBody.setLinearVelocity(0f, 0f);
        player.walk = false;
    }

    @Override
    public void render(float delta) {
        update(delta);
        
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        
        batch.setProjectionMatrix(camera.combined);
        //batch.setTransformMatrix(camera.view);
        batch.begin();
        player.draw(batch);
        batch.end();
        
        rayHandler.setCombinedMatrix(camera.combined);
        rayHandler.updateAndRender();
        
        bodyRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        camera.setToOrtho(false, 160f/2f, 90f/2f);
        rayHandler.setCombinedMatrix(camera.combined);
        mapRenderer = new OrthogonalTiledMapRenderer(map, SCALE_FACTOR);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        world.dispose();
        rayHandler.dispose();
        map.dispose();
        mapRenderer.dispose();
        bodyRenderer.dispose();
        batch.dispose();
    }
    
    public void loadMap(String mapName){
        System.out.println("Loading map " + mapName);
        
        // Load the map file itself
        map = tmxLoader.load(Gdx.files.internal("assets/maps/" + mapName).file().getAbsolutePath());
        
        // Create the world and lighting
        world = new World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, _BRIGHTNESS);
        
        // Find all the physics bodies and process them
        MapLayer physBodiesLayer = map.getLayers().get("PhysBodies");
        /* Declare the variables here, but only initialise it after encountering
         * the first static rectangle object, then keep it around.
         */
        BodyDef boxDef = null; 
        PolygonShape boxShape = null;
        for(MapObject obj : physBodiesLayer.getObjects()){
            /*
             * This is a very debug-style routine. It dumps all the information
             * it finds to the system out, which may not be desired for actual
             * production.
             */
            
            /*Iterator<String> keys = obj.getProperties().getKeys();
            while(keys.hasNext()){
                String key = keys.next();
                Object val = obj.getProperties().get(key);
                System.out.printf("    %s = %s (%s)\n", key, val.toString(), val.getClass().toString());
            }*/
            
            String objPhysType = obj.getProperties().get("physType", String.class);
            
            if(objPhysType.equals("staticBody")){
                if(obj instanceof RectangleMapObject){
                    if(boxDef == null)
                        boxDef = new BodyDef();
                    boxDef.type = BodyDef.BodyType.StaticBody;
                    if(boxShape == null)
                        boxShape = new PolygonShape();
                    
                    RectangleMapObject rect = (RectangleMapObject) obj;
                    float x, y, w, h;
                    
                    x = rect.getRectangle().x;
                    y = rect.getRectangle().y;
                    w = rect.getRectangle().width;
                    h = rect.getRectangle().height;
                    
                    boxDef.position.set((x + w / 2f) * SCALE_FACTOR, (y + h / 2f) * SCALE_FACTOR);
                    boxShape.setAsBox((w / 2f) * SCALE_FACTOR, (h / 2f) * SCALE_FACTOR);
                    Body boxBody = world.createBody(boxDef);
                    boxBody.createFixture(boxShape, 5f);
                }

                /*if(obj instanceof EllipseMapObject){
                    EllipseMapObject ell = (EllipseMapObject) obj;
                    float x, y, w, h;
                    x = ell.getEllipse().x;
                    y = ell.getEllipse().y;
                    w = ell.getEllipse().width;
                    h = ell.getEllipse().height;
                    System.out.printf("    ELLIPSE: (%f, %f, %f, %f)", x, y, w, h);
                }*/
            }
            
            if(objPhysType.equals("pointLight")){
                float x, y, r, g, b, dist;
                x = obj.getProperties().get("x", Float.class);
                y = obj.getProperties().get("y", Float.class);
                r = Float.parseFloat(obj.getProperties().get("red", String.class));
                g = Float.parseFloat(obj.getProperties().get("green", String.class));
                b = Float.parseFloat(obj.getProperties().get("blue", String.class));
                dist = Float.parseFloat(obj.getProperties().get("distance", String.class));
                
                new PointLight(rayHandler, NUM_RAYS, new Color(r, g, b, 1f), dist,
                        x * SCALE_FACTOR, y * SCALE_FACTOR);
            }
        }
        
        MapLayer helperLayer = map.getLayers().get("Helpers");
        for(MapObject helper : helperLayer.getObjects()){
            if(helper.getName().equals("PLAYER_START")){
                float x, y;
                x = helper.getProperties().get("x", Float.class);
                y = helper.getProperties().get("y", Float.class);
                player = new PlayerCharacter(rayHandler, x * SCALE_FACTOR, y * SCALE_FACTOR);
                
                BodyDef playerBodyDef = new BodyDef();
                playerBodyDef.type = BodyType.KinematicBody;
                playerBodyDef.position.set(x * SCALE_FACTOR + player.getWidth() / 2f,
                        y * SCALE_FACTOR + player.getHeight() / 2f);
                PolygonShape playerBoundingBox = new PolygonShape();
                playerBoundingBox.setAsBox(player.getWidth() / 2f, player.getHeight() / 2f);
                playerBody = world.createBody(playerBodyDef);
                playerBody.createFixture(playerBoundingBox, 1f);
                playerBody.setUserData(player);
            }
        }
    }
}
