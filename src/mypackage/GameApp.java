package mypackage;

import net.rim.device.api.ui.UiApplication;

public class GameApp extends UiApplication 
{  
    public static void main(String[] args) {
        GameApp game = new GameApp();
        game.enterEventDispatcher();
    }
 
    public GameApp() {
        pushScreen(new GameMenu());
    }
}
