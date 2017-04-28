package mypackage;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * The menu class handles showing the main menu and intercepting when the user quits or
 * begins the game.  If the user presses the button to begin, the Menu class starts
 * the game going.
 */
class GameMenu extends ScreenManager
{
    private VerticalFieldManager mainManager;
    
    // To add functionality to clicking the button, we need to override the button's
    // trackwheelClick method.  Here we define _startButton as a button with
    // text, positioning, and trackwheelClick we want.
    ButtonField startButton = new ButtonField("Start", ButtonField.FIELD_HCENTER | ButtonField.FIELD_BOTTOM)
    {
    	/*
    	public void paint(Graphics g){
    		g.setBackgroundColor(Color.BLACK);
    		g.setColor(Color.RED);
    		g.clear();
    		super.paint(g);
    	}
    	*/
    	
    	protected boolean trackwheelClick(int status, int time)
       {
          // If the button is pressed, we create a new GamePlay object
    	  String background = null;
    	  String screenType = null;
          if (Display.getWidth() <= 320 && 
        		  Display.getHeight() <= 240) {
        	  background = "planet-black-hole-8530.jpg";
        	  screenType = "small";
          } else {
        	  background = "planet-black-hole.jpg";
        	  screenType = "large";
          }
          
          final String deviceName = DeviceInfo.getDeviceName();
          gameScreen = new GameScreen(background, deviceName, screenType);

          // Then we push it onto the screen stack.  This then becomes the
          // active screen.
          getUiEngine().pushScreen(gameScreen);

          // The invokeLater method allows us to continually run a segment of
          // code from outside the GameScreen object.  In this case, we do
          // this to monitor if the GameScreen object is active our not.
          // When the player loses, the object marks itself inactive, at
          // which point we first cancel invoking further, then stop
          // the music from playing, then pop the GameScreen screen off
          // the stack so we return to the main menu.
          invokeID = getApplication().invokeLater(new Runnable()
            {
                public void run()
                {
                    // Check to see if the game is done.
                    if (gameScreen.getActive() == false)
                    {
                        // Cancel invoking this piece of code again (normally is invoked
                        // every 500 ms, as specified below)
                        getApplication().cancelInvokeLater(invokeID);

                        // Kill the music
                        //GamePlay.snd.stopMusic();

                        // Pop the gameplay screen off the stack, which returns
                        // the user to the main menu
                        getUiEngine().popScreen(gameScreen);

                 	    gameOverMenu = new GameOverMenu(gameScreen.getGamePlayer());
                        getUiEngine().pushScreen(gameOverMenu);
                        // Display the final score
                        //Dialog.inform("Final Score: " + _game.getScore());

                        // We're done with our game object now
                        gameScreen = null;
                    }
                }
            }
            , 500,true); // rerun this code every 500ms

          return true;
       }
    };     

    /*
    public GameMenu()
    {
        // First, turn off scroll bars for this screen in case we accidentally push past the
        // edge with our fields/whitespace
        super(NO_VERTICAL_SCROLL);

        // We set the title on the screen
        LabelField title = new LabelField("Alien Death March", LabelField.FIELD_HCENTER);
        setTitle(title);

        // CustomManagers can also be used just as space buffers.  First we make
        // 20 pixels of space
        getScreen().add(new CustomManager(20));

        Bitmap backGround = EncodedImage.getEncodedImageResource("background1.jpg").getBitmap();
        Background bg = BackgroundFactory.createBitmapBackground(backGround);
        getScreen().setBackground(bg);

        // Add some text
        add(new LabelField("Instructions", LabelField.FIELD_HCENTER));

        // Create another custom manager, but this one we'll use for more than just
        // spacing, we'll actually position some text fields (our instructions)
        CustomManager instManager = new CustomManager(Graphics.getScreenHeight() - 145);
        getScreen().add(instManager);       

        // A multi-d array that will store our instruction fields
        CustomTextField instArray[][] = new CustomTextField[4][2];

        instArray[0][0]= new CustomTextField("T", 1, 0, 20);
        instArray[1][0] = new CustomTextField("Space", 1, 0, 40);
        instArray[2][0] = new CustomTextField("A", 1, 0, 60);
        instArray[3][0] = new CustomTextField("D", 1, 0, 80);
        instArray[0][1] = new CustomTextField("Forward", 2, 0, 20);
        instArray[1][1] = new CustomTextField("Fire Cannon", 2, 0, 40);
        instArray[2][1] = new CustomTextField("Move Left", 2, 0, 60);
        instArray[3][1] = new CustomTextField("Move Right", 2, 0, 80);

        // Loop through our array and add each field to the layout manager with a different
        // font
        for (int lcv = 0 ; lcv < 4 * 2 ; lcv++)
        {
            instArray[lcv%4][lcv/4].setFont(Font.getDefault().derive(Font.PLAIN, 16));
            instManager.add(instArray[lcv%4][lcv/4]);
        }

        // add our button that has the click method overridden
        add(_startButton);

        // Add a buffer of 10 pixels
        getScreen().add(new CustomManager(10));   

        // More text
        LabelField copyrightText = new LabelField("Copyright 2011 Trabant Tech", LabelField.FIELD_HCENTER);
        copyrightText.setFont(Font.getDefault().derive(Font.ITALIC, 14));
        add(copyrightText);

        SoundPlayer player = new SoundPlayer();
        player.playMusic("galaga.mid");
        player.stopMusic();
        player.playMusic("galaga.mid");

    }
    */
    
    GameMenu()
    {
        super(NO_VERTICAL_SCROLL);
           
        mainManager = new VerticalFieldManager()
        {
            //background color change
            public void paint(Graphics graphics)
            {
		        //Background bg = BackgroundFactory.createLinearGradientBackground(Color.BLACK, Color.BLACK, Color.WHEAT, Color.WHEAT);
		        //Bitmap backGround = EncodedImage.getEncodedImageResource("stars.jpg").getBitmap();
                //graphics.drawBitmap(0,0, Display.getWidth(), Display.getHeight(), backGround, 0, 0);
                graphics.setBackgroundColor(Color.BLACK);
                graphics.clear();
                super.paint(graphics);
            }
            //height and width change
            protected void sublayout( int maxWidth, int maxHeight )
            {
                int width = Display.getWidth();
                int height = Display.getHeight();
                
                super.sublayout( width, height);
                setExtent( width, height);
            }
        };
        
        
        // We set the title on the screen
        LabelField title = new LabelField("Planetary Defender", LabelField.FIELD_HCENTER);
        setTitle(title);

        // CustomManagers can also be used just as space buffers.  First we make
        // 20 pixels of space
        mainManager.add(new CustomManager(15));

        //mainManager.add(new MyField("Instructions", Color.WHITE, LabelField.HCENTER, 120, 10).getField());

        CustomManager instManager = new CustomManager(Display.getHeight() - 130);
        instManager.setBackground(BackgroundFactory.createSolidBackground(Color.BLACK));
        mainManager.add(instManager);       

        // A multi-d array that will store our instruction fields
        CustomTextField instArray[][] = new CustomTextField[4][2];

        instArray[0][0]= new MyField("Track-wheel", Color.RED, 1, 0, 20).getField();
        instArray[1][0] = new MyField("Space", Color.RED, 1, 0, 40).getField();
        instArray[2][0] = new MyField("A", Color.RED, 1, 0, 60).getField();
        instArray[3][0] = new MyField("L", Color.RED, 1, 0, 80).getField();
        instArray[0][1] = new MyField("Right and Left", Color.YELLOW, 2, 0, 20).getField();
        instArray[1][1] = new MyField("Fire Laser", Color.YELLOW, 2, 0, 40).getField();
        instArray[2][1] = new MyField("Move Left", Color.YELLOW, 2, 0, 60).getField();
        instArray[3][1] = new MyField("Move Right", Color.YELLOW, 2, 0, 80).getField();
        
        
        // Loop through our array and add each field to the layout manager with a different font
        for (int lcv = 0 ; lcv < 4 * 2 ; lcv++) {
            instArray[lcv%4][lcv/4].setFont(Font.getDefault().derive(Font.PLAIN, 16));
            instManager.add(instArray[lcv%4][lcv/4]);
        }		
        
        mainManager.add(startButton);
       
        // Add a buffer of 10 pixels
        mainManager.add(new CustomManager(10));   

        // More text
        LabelField copyrightText = new LabelField("Copyright 2011 Trabant Technologies, LLC", LabelField.FIELD_HCENTER);
        copyrightText.setFont(Font.getDefault().derive(Font.ITALIC, 14));
        mainManager.add(copyrightText);

        this.add(mainManager);
    }
    
    private class MyField 
    {
    	private CustomTextField ctf;
    	
	    public MyField(final String text, final int color, int style, int x,  int y) {	    	
	    	ctf = new CustomTextField(text, style, x, y) {
	            // Override the paint method to set Font and Color.
	            public void paint(Graphics graphics) {
	              graphics.setColor(color);
	              graphics.clear();
	              super.paint(graphics);
	            }
	        };
	    }
	    
	    public CustomTextField getField() {
	    	return ctf;
	    }
    }
    
}
