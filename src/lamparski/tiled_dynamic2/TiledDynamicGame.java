package lamparski.tiled_dynamic2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class TiledDynamicGame extends Game {
    
    private GameScreen gameScreen;

    @Override
    public void create() {
        System.out.println("TiledDynamicGame now loading: test2.tmx");
        gameScreen = new GameScreen();
        gameScreen.loadMap("test2.tmx");
        setScreen(gameScreen);
        /*
         *  The following ensures that screens can access raw input.
         */
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameScreen.dispose();
    }

    

}
