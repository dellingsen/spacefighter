package mypackage;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/**
 * Manage game screen transitions when game begins and ends.
 * @author Dan
 *
 */
abstract class ScreenManager extends MainScreen
{

    public ScreenManager(long noVerticalScroll) {
		super(noVerticalScroll);
	}

	GameScreen gameScreen;  // GamePlay handles the action part of the game itself
	GameOverMenu gameOverMenu;
    int invokeID; // A handle to our invocation of scanning for when the game ends

    // Normally LabelFields are arranged in a very plain order depending on the
    // Layout manager.  They'll repeat vertically with the option of left/center/right
    // justifying.  But sometimes it's nice to be able to specificy exactly in X,Y coordinates
    // where you want the field to go.  This class, in conjunction with the Custom Manager
    // defined below, allows for this
    // Additionally, a "customStyle" is defined for the field.  For our cases, this
    // is either left to 0 for normal X,Y positioning, set to 1 for X set to 1/8th the total
    // width of the screen, or set to 2 for X+fieldwidth set to 7/8th the total width of the screen.
    // 1/8th and 7/8th for the edge of the text allows for columns to be made, for scores,
    // instructions, or anything else.  More custom centering or something more dynamic
    // could be done with this variable, but its fine for our purposes right now.
    class CustomTextField extends LabelField
    {
       int _xPos, _yPos, _customStyle; // coordinates and style

       // We pass in the coordinates and the custom style
       CustomTextField(String passLabel, int passStyle, int passX, int passY)
       {
              super(passLabel);
              _xPos = passX;
              _yPos = passY;
              _customStyle = passStyle;
       }    
       
       public void paint(Graphics graphics) {
           graphics.setColor(Color.RED);
           super.paint(graphics);
         }


       // Getters for position and style
       int getX() { return _xPos; }
       int getY() { return _yPos; }
       int getCustomStyle() { return _customStyle; }
    }

    // Our custom manager is where the magic happens for allowing customtextfields
    // to be placed at any X,Y coordinate.  It reads in the coordinates from the
    // field and places it at those coordinates in the layout.  It will also
    // look at custom layout, and if centered is specified, will ignore the X coordinate
    // and center it at the Y coordinate on the screen.  Additionally, the total height
    // of the manager can be control how much space it takes up regardless of how many
    // fields it contains
    class CustomManager extends Manager
    {
        int _managerHeight; // Total height of the manager

        // Pass in desired height.  Scrolling is turned off in both directions.
        public CustomManager(int passHeight)
        {
            super(Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL);
            _managerHeight = passHeight;
        }   

        // Sublayout is called automatically to position all the internal fields to this
        // layout.  Its sublayouts job to read in the custom coordinates of each of the fields
        // and place them accordingly.
        protected void sublayout(int width, int height)
        {
            CustomTextField field;

            // Loop through all the fields contained with the layout manager
            for (int lcv = 0; lcv < getFieldCount(); lcv++)
            {
                //Get the field.
                field = (CustomTextField)getField(lcv);

                //Obtain the custom x and y coordinates for
                //the field and set the position for
                //the field.
                switch (field.getCustomStyle())
                {
                   // Custom style 1 is for the left side of the text to be at 1/8th the width
                   // of the screen
                   case 1:
                        setPositionChild(field, width / 8 , field.getY());
                        break;

                   // Custom style 2 is for the right side of the text to be at 7/8ths the width
                   // of the screen
                   case 2:
                        setPositionChild(field, width * 7 / 8 - field.getPreferredWidth(), field.getY());
                        break;

                   // Any other custom style gets position strictly from X,Y
                   default:
                        setPositionChild(field, field.getX(), field.getY());  

                }

                 //Layout the field.
                 layoutChild(field, width, height);
            }

            //Set the manager's dimensions
            setExtent(width, _managerHeight);
        }

        public int getPreferredWidth()
        {
            return Display.getWidth();
        }

        public int getPreferredHeight()
        {
            return Display.getHeight();
        }
    }
    
}
