package mypackage;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.*;
import java.util.Vector;

/**
 * The GrafixEngine class is our game's graphics engine with all the drawing routines
 * @author Dan
 */
class GrafixEngine
{
    Bitmap backGround; // The bitmap for the background
    Bitmap health; // The bitmap for the health meter
    Font gameFont; // The font used for drawing score and lives
    
    GrafixEngine()
    {
        try {
            gameFont = FontFamily.forName("BBSansSerif").getFont(FontFamily.SCALABLE_FONT,14);
        }
        catch (Exception e) {         	
        }
        health = EncodedImage.getEncodedImageResource("healthbar.gif").getBitmap();
    }

    // Method that sets the bitmap for the background
    public void initBackground(String passBackground, int passSpeed) {
       backGround = EncodedImage.getEncodedImageResource(passBackground).getBitmap();
    }

	/**
	 * Primary function for the graphics engine, draws all the objects, text, health, etc
	 */
    public void process(GameScreen screen, Graphics pGraphics, Vector pObjects, int pScore, int pLives, int pLevel) throws Exception
    {    
        // Draw our background at the correct position 
        pGraphics.drawBitmap(0,0, Display.getWidth(), Display.getHeight(), backGround, 0, 0);

        Player player = null;
        // Now draw each of our objects to the screen
        for (int lcv = 0 ; lcv < pObjects.size() ; lcv++)
        {  
        	GameObject object = (GameObject) pObjects.elementAt(lcv);
        	if (lcv == 0) {
        		player = (Player)object;
        		player.update();
        	}
            pGraphics.drawBitmap(object.getX(),
                                    object.getY(),
                                    object.getBitmap().getWidth(),
                                    object.getBitmap().getHeight(),
                                    object.getBitmap(), 0, 0);
        }
    
        // Draw score
        String zeroPad;

        // We want to pad the score with 0s
        zeroPad = "";
        if (pScore < 10000)
          zeroPad += "0";
        if (pScore < 1000)
          zeroPad += "0";
        if (pScore < 100)
          zeroPad += "0";
        if (pScore < 10)
          zeroPad += "0";  
  
        // Draw score and lives in white with the game font   
        pGraphics.setColor(0xFFFFFF);
        pGraphics.setFont(gameFont);
        //pGraphics.drawText("Health", 5, 5);
        pGraphics.drawText("Lives: " + pLives, 10, Display.getHeight()-15);  
        pGraphics.setColor(0xFFFF00);
        pGraphics.drawText("Score: " + zeroPad + pScore, Display.getWidth()-120, 2);
        pGraphics.drawText("Level: " + pLevel, 10, 2);
        
        if (player.getState().equals(GameObject.OBJSTATE_DEAD)) {
        	if (pLives > 1) {
        		pGraphics.drawText("Get Ready!", (Display.getWidth()/2)-50, Display.getHeight()/2);
        	} else {
        		pGraphics.drawText("Game Over!", (Display.getWidth()/2)-50, Display.getHeight()/2);
        	}
        	player.setRepaint(false);
        }

        //Draw health, with width dependent on Players life
        /*
        if (((GameObject) pObjects.elementAt(0)).getLives() > 0)
            pGraphics.drawBitmap(5, 20, health.getWidth() * 
           		((GameObject) pObjects.elementAt(0)).getLives() / 5, health.getHeight(), health, 0, 0);
        */
    }

}
