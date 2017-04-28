package mypackage;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * The menu class handles showing the main menu and intercepting when the user quits or
 * begins the game.  If the user presses the button to begin, the Menu class starts
 * the game going.
 */
class GameOverMenu extends ScreenManager
{
    private VerticalFieldManager mainManager;

    // To add functionality to clicking the button, we need to override the button's
    // trackwheelClick method.  Here we define _startButton as a button with
    // text, positioning, and trackwheelClick we want.
    ButtonField mainMenuButton = new ButtonField("Main Menu", ButtonField.FIELD_HCENTER | ButtonField.FIELD_BOTTOM)
    {

       protected boolean trackwheelClick(int status, int time)
       {
    	   UiEngine engine = getUiEngine();
           engine.popScreen(getScreen());
           return true;
       }
    };     

    public GameOverMenu(Player player)
    {
        // First, turn off scroll bars for this screen in case we accidentally push past the
        // edge with our fields/whitespace
        super(NO_VERTICAL_SCROLL);

        mainManager = new VerticalFieldManager()
        {
            //background color change
            public void paint(Graphics graphics)
            {
		        //Background bg = BackgroundFactory.createLinearGradientBackground(Color.BLACK, Color.BLACK, Color.WHEAT, Color.WHEAT);
		        //Bitmap backGround = EncodedImage.getEncodedImageResource("background1.jpg").getBitmap();
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

        // Add some text
        mainManager.add(new LabelField("Game Over", LabelField.FIELD_HCENTER));

        // Create another custom manager, but this one we'll use for more than just
        // spacing, we'll actually position some text fields (our instructions)
        CustomManager instManager = new CustomManager(Display.getHeight() - 130);
        instManager.setBackground(BackgroundFactory.createSolidBackground(Color.BLACK));
        mainManager.add(instManager);       

        // A multi-d array that will store our instruction fields
        CustomTextField instArray[][] = new CustomTextField[5][2];

        double hitPercentage = 0.0;
        if (player.getEnemiesHit() > 0) { 
        	hitPercentage = (player.getEnemiesHit()/player.getShotsFired())*100.0;
        }
        
        instArray[0][0]= new CustomTextField("Final Score", 1, 0, 20);
        instArray[1][0] = new CustomTextField("Number of Hits", 1, 0, 40);
        instArray[2][0] = new CustomTextField("Number of Shots", 1, 0, 60);
        instArray[3][0] = new CustomTextField("Kill Percentage", 1, 0, 80);
        instArray[4][0] = new CustomTextField("Level", 1, 0, 100);
        instArray[0][1] = new CustomTextField(String.valueOf(player.getScore()), 2, 0, 20);
        instArray[1][1] = new CustomTextField(String.valueOf((int)player.getEnemiesHit()), 2, 0, 40);
        instArray[2][1] = new CustomTextField(String.valueOf((int)player.getShotsFired()), 2, 0, 60);
        instArray[3][1] = new CustomTextField(String.valueOf((int)hitPercentage + "%"), 2, 0, 80);
        instArray[4][1] = new CustomTextField(String.valueOf(player.getLevel()), 2, 0, 100);

        // Loop through our array and add each field to the layout manager with a different
        // font
        for (int lcv = 0 ; lcv < 4 * 2 ; lcv++) {
            instArray[lcv%4][lcv/4].setFont(Font.getDefault().derive(Font.PLAIN, 16));
            instManager.add(instArray[lcv%4][lcv/4]);
        }

        // add our button that has the click method overridden
        mainManager.add(mainMenuButton);

        // Add a buffer of 10 pixels
        mainManager.add(new CustomManager(10));   

        // More text
        LabelField copyrightText = new LabelField("Copyright 2011 Trabant Technologies, LLC", LabelField.FIELD_HCENTER);
        copyrightText.setFont(Font.getDefault().derive(Font.ITALIC, 14));
        mainManager.add(copyrightText);
        
        this.add(mainManager);
    }

}
