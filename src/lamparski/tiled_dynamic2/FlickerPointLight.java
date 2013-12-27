package lamparski.tiled_dynamic2;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class FlickerPointLight extends PointLight {
    
    private short acc = 0;
    private short speed = 45;
    private float magnitude = 0.1f;
    private Random rand;

    public FlickerPointLight(RayHandler rayHandler, int rays, Color color,
            float distance, float x, float y) {
        super(rayHandler, rays, color, distance, x, y);
        rand = new Random(System.currentTimeMillis());
    }
    
    public void setFlicker(short speed, float magnitude){
        this.speed = speed;
        this.magnitude = magnitude;
    }
    
    public short getSpeed() { return speed; }
    public float getMagnitude() { return magnitude; }
    
    @Override
    protected void update() {
        super.update();
        float localmagn = magnitude;
        try{
            acc += ((short) rand.nextInt(speed) % 360);
            localmagn = Math.abs((float) rand.nextGaussian() * magnitude);
            // localmagn = rand.nextFloat() * magnitude;
        } catch (NullPointerException e){
            // This happens at construction.
            acc += 0;
        }
        setDistance(getDistance() + (MathUtils.sinDeg(acc) * localmagn));
    }

}
