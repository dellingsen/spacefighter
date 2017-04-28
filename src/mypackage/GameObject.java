package mypackage;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.XYRect;
  
public abstract class GameObject      
{     		
	public static final String OBJSTATE_ALIVE = "alive";
	public static final String OBJSTATE_DAMAGED = "damaged";
	public static final String OBJSTATE_DEAD = "dead";
	public static final String TYPE_PLAYER = "player";
	public static final String TYPE_ENEMY = "enemy";
	public static final String TYPE_LASER = "laser";
	public static final String TYPE_EXPLOSION = "explosion";

	public abstract Bitmap getBitmap();
	public abstract void setBitmap(Bitmap bitmap);
	public abstract void setType(String type);
	public abstract int getX();
	public abstract int getY();
	public abstract String getType();
	public abstract String getState();
	public abstract void setState(String state);
	public abstract int getLives();
	public abstract void setLives(int lives);
	public abstract XYRect getRect();
	public abstract void setVelocity(double vx, double vy);
	public abstract void setPosition(int x, int y);
	public abstract double getXVel();
	public abstract double getYVel();
	public abstract void setXVel(double vx);
	public abstract void setYVel(double vy);
	public abstract int getLevel();
	public abstract void update();
	public abstract GameObject getParent();
	public abstract boolean getRepaint();
	public abstract void setRepaint(boolean repaint);

	protected int xPos;
    protected int yPos;
    protected double xVelocity;
    protected double yVelocity;
    protected String type;
    boolean moveLeft = false;
    boolean moveRight = true;
    private int randomCounter;
    int aiRoutine = 0;
    GameObject parent;
    public static final String[] enemyGraphic = 
    	{"enemy-1.png","enemy-2.png","enemy-3.png","enemy-4.png","enemy-5.png",
    	"enemy-1.png","enemy-2.png","enemy-3.png","enemy-4.png","enemy-5.png"}; 
    public static final int[] enemySpeed = {2, 4, 6, 8, 10, 12, 13, 14, 15, 16};     
    
    /**
     * Check on every game object update to keep inside screen bounds
     */
    protected void checkBounds() {
        if (xPos >= Display.getWidth()-28) {
            xPos = Display.getWidth()-28;
        }
        else if (xPos <= 5) {
            xPos = 5;
        }
        
        if (yPos > Display.getHeight()) {
            yPos = 0;
        }
        else if (yPos < 0) {
            yPos = Display.getHeight();
        }
    }
    
	/**
	 * Firing means creating and shooting a laser object until it reaches the end of
	 * the screen boundaries and then it will be deleted. 
	 * @param passObjects
	 * @param passParent
	 * @param passVelocity
	 */
    public void fire(Vector passObjects, GameObject passParent, int passVelocity)
    {
        Laser laser = null;

        // From Player
        if (passVelocity > 0) {
        	laser = 
        		new Laser(passParent.getX()+passParent.getBitmap().getWidth()/2,
        				passParent.getY()-1, 0, passVelocity, passParent);
        } else {
        	laser = 
        		new Laser(passParent.getX()+passParent.getBitmap().getWidth()/2,
        				passParent.getY()+passParent.getBitmap().getHeight()+1, 0, passVelocity, passParent);
        }

        // Add the laser object to our object vector to it's position can be updated
        passObjects.addElement(laser);
        
        if (passParent instanceof Player) {
        	((Player) passParent).updateShotsFired(1);        	
        }        
    }


    
    // The think method is where the individual enemy AI takes place
    public void think(Vector passObjects)
    {
       //Don't use right now since we are just changing the xPos/yPos to move
       //xVelocity = 0;
       //yVelocity = 0;

       // Grab a handle on the hero object so we know how to direct our enemies
       // Player could be removed from the Vector before we can stop this thread
       Player tempPlayer = null;
       if (passObjects.elementAt(0) instanceof Player) {
    	   tempPlayer = (Player) passObjects.elementAt(0);
       }
          
       //Not sure why level is 0 when we start, but this fixes it
       int currentLevel = getLevel();
       if (currentLevel == 0) {
    	   currentLevel = 1;
       }
       
       // If we're in normal AI mode
       if (aiRoutine == 0)
       {    	
    	   //If an enemy was damaged make it dive bomb
    	   if (getState().equals(OBJSTATE_DAMAGED)) {
        	   yPos = yPos + 15;
        	   if (yPos > Display.getHeight() - 15) {
        		   setLives(0);
        	   }
    	   }
    	   else {
	    	   //Enemies move back-n-forth, faster for each level, and move down 30 pixels
	    	   //after they reach the end edge of the screen on either side
	    	   if (moveRight) {
		    	   if (xPos > Display.getWidth()-20) {
		    		   xPos = xPos - enemySpeed[currentLevel-1];
		    		   moveRight = false;
		    		   moveLeft = true;
		        	   yPos = yPos + 20;
		    	   }
		    	   else {
		    		   xPos = xPos + enemySpeed[currentLevel-1];
		    	   }
	    	   }
	    	   else {
		    	   if (xPos < 6) {
		    		   xPos = xPos + enemySpeed[currentLevel-1];
		    		   moveRight = true;
		    		   moveLeft = false;
		        	   yPos = yPos + 20;
		    	   }
		    	   else {
		    		   xPos = xPos - enemySpeed[currentLevel-1];
		    	   }
	    	   }
    	   }
    	   
    	   //Get a random number between 0 and 15, then single out 1/3 of those
    	   //numbers...then make sure that range is generated a specific number
    	   //of times based on the level you are on.  As the levels get higher,
    	   //the requirements to land in that range of numbers is easier to achieve,
    	   //and as a result success will occur more often, and more enemies will fire
    	   //their lasers.
    	   int randomIndex = GameScreen.rndGenerator.nextInt(15);    	   
    	   if (randomIndex > 10) {
    		   randomCounter++;
    		   
    		   if (currentLevel <= 5) {
	    		   //Fire more lasers as the levels progress
	    		   if (randomCounter == (8 - currentLevel)) {
	    			   fire(passObjects, this, -20);
	    			   randomCounter = 0;
	    		   }
    		   } else {
    			   //Fire the same for last 5 levels
	    		   if (randomCounter == 3) {
	    			   fire(passObjects, this, -20);
	    			   randomCounter = 0;
	    		   }
    		   }
    	   }    	   

         //Enemies follow Player around and are more aggressive
         // If hero is to our right, set velocity to right
         //if (xPos < tempHero.getxPosition())
         //    xVelocity = 5;

         // If Player is to our left, set velocity to our left
         //if (xPos > tempHero.getxPosition())
         //    xVelocity = -5;

         // Enemies try to stay 40 pixels above hero
         //if (yPos  < tempHero.getyPosition() - 40)
             //yVelocity = 5;

         // If enemy is below hero, they move up
         //if (yPos > tempHero.getyPosition() + tempHero.getBitmap().getHeight())
             //yVelocity = -5;

         // Add a little bit of random movement in
         //xVelocity += GamePlay.rndGenerator.nextInt() % 4 - 2;
         //yVelocity += GamePlay.rndGenerator.nextInt() % 4 - 2;
      }
      else
      {
    	  if (tempPlayer != null) {
	         if (xPos + getBitmap().getWidth() / 2 < tempPlayer.getX() + tempPlayer.getBitmap().getWidth()/2)
	             xVelocity = 5;
	
	         if (xPos + getBitmap().getWidth() / 2 > tempPlayer.getX() + tempPlayer.getBitmap().getWidth()/2)
	             xVelocity = -5;
	
	         // For vertical though, the enemy drone is always going downward at a faster rate
	         yVelocity = 8;
    	  }
      }
    }

}