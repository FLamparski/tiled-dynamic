package lamparski.tiled_dynamic2;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TiledDynamicLauncher {

    public static void main(String[] args) {
        LwjglApplicationConfiguration myConf = new LwjglApplicationConfiguration();
        /*
         * In an actual game, these should be loaded from a file that can be edited
         * using either a simple config tool separate from the game, or a text
         * editor (it could be JSON or INI, actually)
         */
        myConf.height = 720;
        myConf.width = 1280;
        myConf.useGL20 = true; // yes, even on cpu graphics
        myConf.foregroundFPS = 60;
        myConf.title = "Tiled Dynamic - 2";
        
        System.out.printf("Launching %s in a %dx%d window. %s.\n",
                            myConf.title,
                            myConf.width,
                            myConf.height,
                            myConf.useGL20 ? "Using GL2.0" : "Using GL1.1"
                            );
        
        TiledDynamicGame app = new TiledDynamicGame();
        new LwjglApplication(app, myConf);
    }

}
