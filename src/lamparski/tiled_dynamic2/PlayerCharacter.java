package lamparski.tiled_dynamic2;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerCharacter extends Sprite{
    /*
     * The walk cycle should be walking1-standing-walking2-walking1...
     */
    public static enum PlayerState {
        STANDING, WALKING1, WALKING2;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    
    public static enum PlayerFacing {
        NORTH, EAST, SOUTH, WEST;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    
    private static final String SPRITE_FSTRING = "doc-glasses-%s-%s";
    
    private TextureAtlas atlas;
    private float flashlightDir = 0f;
    private float timeElapsed = 0f;
    private ConeLight flashlight;
    private PlayerState state = PlayerState.STANDING;
    public PlayerFacing direction = PlayerFacing.SOUTH;
    public boolean walk;
    
    public PlayerCharacter(RayHandler rayHandler, float x, float y) {
        super();
        //this.camera = camera;
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/doc-glasses.pack"));
        setColor(Color.WHITE);
        setRegion(atlas.findRegion("doc-glasses-standing-south"));
        setBounds(x, y, 4, 4);
        
        flashlightDir = calculateFlashlightDirection(direction);
        flashlight = new ConeLight(rayHandler, 128, Color.YELLOW, 16f, getX(), getY(), flashlightDir, 20);
        System.out.printf("Creating a player character at %f, %f\n", x, y);
    }
    
    public void update(float delta){
        flashlight.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        flashlight.setDirection(calculateFlashlightDirection(direction));
        if(walk){
            timeElapsed += delta;
            if (timeElapsed >= .1f) {
                timeElapsed = 0f;
                switch(state){
                case STANDING:
                    state = PlayerState.WALKING2;
                    break;
                case WALKING1:
                    state = PlayerState.STANDING;
                    break;
                case WALKING2:
                    state = PlayerState.WALKING1;
                    break;
                default:
                    state = PlayerState.STANDING;
                    break;
                
                }
            }
        } else {
            state = PlayerState.STANDING;
        }
        setRegion(atlas.findRegion(String.format(SPRITE_FSTRING, state, direction)));
    }
    
    public void faceUp(){
        direction = PlayerFacing.NORTH;
    }
    
    public void faceRight(){
        direction = PlayerFacing.EAST;
    }
    
    public void faceDown(){
        direction = PlayerFacing.SOUTH;
    }
    
    public void faceLeft(){
        direction = PlayerFacing.WEST;
    }
    
    private float calculateFlashlightDirection(PlayerFacing dir){
        switch(dir){
        case SOUTH:
            return 270f;
        case NORTH:
            return 90f;
        case EAST:
            return 0f;
        case WEST:
            return 180;
        default:
            return 0f;
        }
    }
}
