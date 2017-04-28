package mypackage;

import java.util.Random;   
import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.container.FullScreen;

public class GameScreen extends FullScreen
{
    // These three objects are called from many places and are made public
    public static GrafixEngine gfx;  // Static object for graphics control
    public static Random rndGenerator; // Static object for random numbers

    Refresher refresherThread;  // Thread that continually refreshes the game
    EnemyAI enemyThread; // Thread when the enemies think
    Vector gameObjects; // A vector of all objects currently in play

    boolean active; // Flag if our game is currently active or not (eg did we lose)
    boolean paused;
    boolean nextLevel;
    private int gameLevel = 1;
    private String deviceName;
    private String screenType;
    private int[] enemyXPositionsSmall = {30,80,120,170,220,270,320};
    private int[] enemyXPositionsLarge = {15,55,95,135,175,215,255,295,335};
    
    boolean getActive() { return active; }
    boolean getNextLevel() { return nextLevel; }
    
    public GameScreen(String background, String deviceName, String screenType)
    {
        //snd = new SND(); // Create sound engine
    	this.deviceName = deviceName;
    	this.screenType = screenType;
        gfx = new GrafixEngine(); // Create graphics engine
        rndGenerator = new Random(); // Create random number generator

        active = true; // Mark the game as active

        // Set our background to stars.png with a speed of 3 pixels per refresh
        gfx.initBackground(background, 3);

        // Create a new vector to hold all the active objects (hero, enemies, lasers, etc)
        gameObjects = new Vector();

        // Our hero will always be the very first object in the vector.
        gameObjects.addElement(new Player());

        // Create the refresher and enemy AI last, we want to make sure all our objects are setup first
        // so the threads don't make use of uninitialized objects
        refresherThread = new Refresher();
        enemyThread = new EnemyAI();
    }  

    // Refresh is a type of thread that runs to refresh the game.
    // Its job is to make sure all the processing is called for each object, update the background,
    // check for end of game, etc.  This is the main heart beat.
    class Refresher extends Thread
    {
        // When the object is created it starts itself as a thread
        Refresher() {
           start();
        }

        // This method defines what this thread does every time it runs.
        public void run()
        {
            int gameState = 1;

            //Initialize first game screen with enemies
            addEnemies(gameLevel);

            // This thread runs while the game is active
            while (active) {
            	
	            while (!paused) 
	            {
	            	//Update positions of all game objects, and then re-draw by calling invalidate()
	                for (int lcv = 0 ; lcv < gameObjects.size() ; lcv++) {
	                    ((GameObject) gameObjects.elementAt(lcv)).update();
	                }
	
	                // Collision detection of objects.
	                if (gameObjects.size() > 1) {
	                	collisionDetect(gameObjects);
	                } 
	                
	                // Clean up stuff that's gone (e.g. life of 0 and in
	                // destroyable state, eg explosion graphic), quit if we were destroyed
	                if (gameObjects.size() > 1) {
	                	gameState = cleanObjects(gameObjects);
	                }
	
	                //Check the state of the game to see where we are
	                if (gameState <= 0) {
	                   //Player died, mark game inactive, return to Menu
	                   active = false;
	                }
	                else if (gameState > 1){
		                // Tell the graphics engine to redraw the screen via invalidate 
	                	// (invalidates the screen and automatically causes a redraw)
	                	// because there are still enemies on the screen
		                invalidate();
	                }
	                else if (gameState == 1) {
	                	//Player is still alive and all enemies are dead - go to next level
	                	if (gameLevel < 10) {
		                	nextLevel = true;
		                	gameLevel = gameLevel + 1;
		                	addEnemies(gameLevel);
	                	} 
	                	else {
	                		active = false;
	                	}
	                }
	
	                try {
	                    // Attempt to sleep for 50 ms
	                    this.sleep(50);
	                }
	                catch (InterruptedException e) {
	                    // Do nothing if we couldn't sleep, we don't care about exactly perfect
	                    // timing.
	                }
	            }
            }
        }
    }

    /**
     * Detect overlapping of 2 rectangles representing 2 game bitmap images
     * @param passObjects
     */
    private void collisionDetect(Vector passObjects)
    {
        GameObject tempObject1, tempObject2; // temporarily points to the two objects being tested
        boolean intersect, check; // flags during testing

        // Loop through all objects in our vector
        for (int lcv = 0; lcv < passObjects.size(); lcv++) {
            // Set tempObject1 to the current object
            tempObject1 = (GameObject) passObjects.elementAt(lcv);

            // Now loop from the current object to the end of the vector
            for(int lcv2 = lcv; lcv2 < passObjects.size(); lcv2++) {
                // Set tempObject2 to the current object of the nested loop
                tempObject2 = (GameObject) passObjects.elementAt(lcv2);

                check = false;
                
                //Check the TYPES in the loops now.....
                // (e.g. some objects don't matter if they collide, enemy with enemy or fire with fire for example)

                // Player and enemy 
                if (tempObject1.getType().equals(GameObject.TYPE_PLAYER) && 
                		tempObject2.getType().startsWith(GameObject.TYPE_ENEMY)) {
                    check = true;
                }

                //Enemy shoots at Player
                if (tempObject1.getType().equals(GameObject.TYPE_PLAYER) && 
                		tempObject2.getType().startsWith(GameObject.TYPE_LASER) && 
                			(tempObject2.getParent() != null && tempObject2.getParent().getType().equals(GameObject.TYPE_ENEMY))) {
                    check = true;
            	}

                //Player shoots at enemy
                if (tempObject1.getType().startsWith(GameObject.TYPE_ENEMY) && 
                		tempObject2.getType().startsWith(GameObject.TYPE_LASER) &&
                			(tempObject2.getParent() != null && tempObject2.getParent().getType().equals(GameObject.TYPE_PLAYER))) {
                    check = true;
                }
                

                // If our check flag is set to true, and the state of the objects is normal
                // (e.g. an object in a hit or exploded state can't collide with something),
                // then lets check for the actual collision
                if (check)
                {
                    intersect = false;
                    
                    // If the TYPES above are needed to be checked, see if they are still active (DAMAGED is active), and overlapping
                    // REMEMBER: TYPE Enemy has 2 states - ALIVE and DAMAGED
                    // And they will be checked in a different order depending on if they collide or are shot with a laser (just live above)
                    if ((tempObject1.getState() == GameObject.OBJSTATE_ALIVE || tempObject1.getState() == GameObject.OBJSTATE_DAMAGED) && 
                    		(tempObject2.getState() == GameObject.OBJSTATE_ALIVE || tempObject2.getState() == GameObject.OBJSTATE_DAMAGED))
                    {
                		intersect = (tempObject1.getRect().contains(new XYPoint(tempObject2.getX(), tempObject2.getY()+10)));
		            }
                    
                    // If the objects collided, damage (or delete) each one.
                    if (intersect) 
                    {
                    	//Player laser hit the enemy
                    	if (tempObject1.getType().equals(GameObject.TYPE_ENEMY) && tempObject2.getType().equals(GameObject.TYPE_LASER)) 
                    	{                    	
                    		//If we're not on level 1, make the enemies harder to kill - give them a second chance
                    		if (gameLevel == 1) {
		                        tempObject1.setBitmap(EncodedImage.getEncodedImageResource("explosion.jpeg").getBitmap());
		                        tempObject1.setType(GameObject.TYPE_EXPLOSION);
		                        tempObject1.setState(GameObject.OBJSTATE_DEAD);
                    		} 
                    		else {
                    			if (tempObject1.getState().equals(GameObject.OBJSTATE_ALIVE)) {
    		                        tempObject1.setBitmap(EncodedImage.getEncodedImageResource("enemy-damaged.png").getBitmap());
    		                        tempObject1.setState(GameObject.OBJSTATE_DAMAGED);
                    			} else {
			                        tempObject1.setBitmap(EncodedImage.getEncodedImageResource("explosion.jpeg").getBitmap());
			                        tempObject1.setType(GameObject.TYPE_EXPLOSION);
			                        tempObject1.setState(GameObject.OBJSTATE_DEAD);
                    			}
                    		}
	                        
	                        //Make sure the laser gets marked for deletion
                        	tempObject2.setLives(0);
	                        
	                        //Update Player score when enemy is killed
	                        ((Player)passObjects.elementAt(0)).updateScore(100);
	                        ((Player)passObjects.elementAt(0)).updateEnemiesHit(1);
	                        
	                        //SoundPlayer player = new SoundPlayer("explosion-sound-1.wav");
	                        //player.start();
                    	}

                    	//Player collided with enemy
                    	if (tempObject1.getType().equals(GameObject.TYPE_PLAYER) && tempObject2.getType().equals(GameObject.TYPE_ENEMY)) {
	                        tempObject1.setBitmap(EncodedImage.getEncodedImageResource("player-explosion.gif").getBitmap());
	                        tempObject1.setState(GameObject.OBJSTATE_DEAD);
	                        tempObject1.setRepaint(true);
	                        //tempObject2.setBitmap(EncodedImage.getEncodedImageResource("explosion.jpeg").getBitmap());
	                        tempObject2.setType(GameObject.TYPE_EXPLOSION);
	                        tempObject2.setState(GameObject.OBJSTATE_DEAD);
                    	}
                    	
                    	//Enemy laser hit the Player
                    	if (tempObject1.getType().equals(GameObject.TYPE_PLAYER) && tempObject2.getType().equals(GameObject.TYPE_LASER)) {
	                        tempObject1.setBitmap(EncodedImage.getEncodedImageResource("player-explosion.gif").getBitmap());
	                        tempObject1.setState(GameObject.OBJSTATE_DEAD);
	                        tempObject1.setRepaint(true);
	                        //SoundPlayer player = new SoundPlayer("explosion-sound-1.wav");
	                        //player.start();

	                        //Make sure the laser gets marked for deletion
                        	tempObject2.setLives(0);
                    	}
                    }
                }
            }
        }
    }

    /**
     * Clean up objects that have been marked for deletion - example, if lives = 0
     * or state = dead.
     * @param gameObjects
     * @return
     */
    private int cleanObjects(Vector gameObjects)
    {
        GameObject tempObject; // Temporary points to object we're checking
        boolean delFlag; // Flag if we should get rid of it or not
        int returnStatus = 0; //number of active game objects
        
        // Loop through all objects in our vector
        for (int lcv = 0; lcv < gameObjects.size(); lcv++) {
        	tempObject = (GameObject) gameObjects.elementAt(lcv);

            // Assume we're not deleting it
            delFlag = false;

            //First, check if the Player has been killed and if the game is over or not
            if (tempObject.getType().equals(GameObject.TYPE_PLAYER) && 
            		tempObject.getState().equals(GameObject.OBJSTATE_DEAD)) 
            {
            	if (!tempObject.getRepaint()) {
	                // If there are lives left...
	                if (((Player)tempObject).getLives() > 0) {
	                   // Decrement the number of lives left, set state, bitmap,
	                   // and position back to normal
	                   ((Player)tempObject).setLives(((Player)tempObject).getLives()-1);
	                   tempObject.setState(GameObject.OBJSTATE_ALIVE);
	                   tempObject.setPosition(Display.getWidth() / 2, Display.getHeight()-65);
	                   tempObject.setBitmap(EncodedImage.getEncodedImageResource("player.gif").getBitmap());
	                   //Need to reset game screen before we start again
	                   resetGameScreen(gameObjects);
	                }
	                else {
	                   // The player is out of lives - destroy the Player and end the game
	                   delFlag = true;
	                }
            	} else {
                   paused = true;
            	}
            }

            //Check if any enemies have been killed or if lasers have gone off the screen
            if (tempObject.getLives() == 0) {
                delFlag = true;
            }

            //Only an enemy can have an explosion type AND a dead state
            //Don't delete yet so we have time to show the explosion
            if (GameObject.TYPE_EXPLOSION.equals(tempObject.getType()) 
            		&& GameObject.OBJSTATE_DEAD.equals(tempObject.getState())) {
            	tempObject.setLives(0);
            }

            // If the delete flag is true
            if (delFlag) {

            	//Don't delete the Player so we can get Player statistics for the user
                if (GameObject.TYPE_PLAYER.equals(tempObject.getType())) {
                    return -1;
                }                

                // Remove the object from the vector
                gameObjects.removeElementAt(lcv);
                tempObject = null;
            }    
            
            returnStatus = gameObjects.size();
        }
        
        return returnStatus;
    }

    private void resetGameScreen(Vector gameObjects) {
        for (int lcv = 0; lcv < gameObjects.size(); lcv++) {
        	GameObject gameObject = (GameObject) gameObjects.elementAt(lcv);
        	if (gameObject.getType().equals(GameObject.TYPE_LASER) || 
        			gameObject.getState().equals(GameObject.OBJSTATE_DAMAGED)) {
                gameObjects.removeElementAt(lcv);
                gameObject = null;
        	}
        }
	}

	// We have a separate thread that takes care of enemy AI.  This allows us to control how
    // quickly the enemies think outside of our main refresh thread.  That way we can make
    // dumb enemies that think much slower than the action happening around them, or smart
    // enemies that think as fast.
    class EnemyAI extends Thread
    {
        EnemyAI() {
           start();
        }

        public void run()
        {
            // Just make sure the thread doesn't accidentally run when the game is over
            while (active)
            {
                while (!paused)
                {
	                // Loop through all the objects and call the think method, which
	                // controls the actions of that object.
	                for (int lcv = 1; lcv < gameObjects.size(); lcv++) {
	                    if (active) {
	                    	((GameObject)gameObjects.elementAt(lcv)).think(gameObjects);
	                    }
	                }
	                try {
	                    // Enemies think 5 times a second
	                    this.sleep(1000/5);
	                }
	                catch (InterruptedException e) {
	                    // Do nothing, again we don't care if timing isn't exact
	                }
                }
                if (paused) {
                	try {
						sleep(3000);
					} catch (InterruptedException e) { }
                	paused = false;
                }
            }
        }
    }

    /**
     * Create 2 rows of enemies for each level.  Add more enemies as the levels increase.
     * Once the enemies are created, they also need to "think" differently for each level.  
     * GameObject.think() defines their behavior for each level.
     * @param level
     */
    public void addEnemies(int level)
    {
        int enemyRowCount = 0;
        int currentRow = 1;
        int[] enemyPositions;
        
        if ("large".equals(screenType)) {
        	enemyPositions = enemyXPositionsLarge;
        } else {
        	enemyPositions = enemyXPositionsSmall;
        }
        
		for (int i=0; i<(enemyPositions.length-1)*2; i++) 
		{
    		if (currentRow == 1 && enemyPositions[enemyRowCount] == enemyPositions[enemyPositions.length-1]) {
    			enemyRowCount = 0;
    			currentRow = 2;
    		}

    		//Larger screens can have more enemies that are farther apart, while
    		//smaller screens (320x240) start enemies higher with less space between
    		int yPosition = 0;
            if ("large".equals(screenType)) {
	    		yPosition = 50*currentRow;
            } else {
	    		if (currentRow == 1) {
	    			yPosition = 10*currentRow;
	    		} else {
	    			yPosition = 10*currentRow+25;
	    		}
            }
    		
            GameObject tempObject = new EnemyShip(level);
            tempObject.setPosition(enemyPositions[enemyRowCount], yPosition);

            // Add the enemy to the object vector
            gameObjects.addElement(tempObject);
           	enemyRowCount++;
		}
    }

    /**
     * Used by the GameMenu to display game stats to the user when the game has finished.
     * @return
     */
    Player getGamePlayer() {
    	Player player = null;
    	if (gameObjects != null && gameObjects.size() >= 1) {
    		player = (Player)gameObjects.elementAt(0);
    	}
    	return player; 
    }

    // This method is called when the invalidate method is called from the refresh thread.
    // We have it passing the graphics object over to our graphics engine so our
    // custom graphics routines can take care of any drawing necessary.
    protected void paint(Graphics graphics)
    {
    	try {  
			gfx.process(this, graphics, gameObjects, ((Player)gameObjects.elementAt(0)).getScore(), 
				((Player)gameObjects.elementAt(0)).getLives(), gameLevel);
		} catch (Exception e) {
			//Not sure what could've happened here, but if this happens we are f'ed
		}
    }

	protected boolean keyChar(char ch,int status,int time)
    {
        boolean retVal = false;
        if(ch == Characters.ESCAPE | ch == Characters.LATIN_SMALL_LETTER_Q) {
            active = false;
            retVal = true;
        }
        else if(ch == Characters.LATIN_SMALL_LETTER_T) { 
        	//((Player)gameObjects.elementAt(0)).forwardBlaster();
        }        
        else if(ch == Characters.LATIN_SMALL_LETTER_L) {
        	((Player)gameObjects.elementAt(0)).moveRight();
            retVal = true;
        }
        else if(ch == Characters.LATIN_SMALL_LETTER_A) {
        	((Player)gameObjects.elementAt(0)).moveLeft();
            retVal = true;
        }
        else if (ch == Characters.SPACE) {
            ((Player)gameObjects.elementAt(0)).fire(gameObjects, (Player)gameObjects.elementAt(0), 20);
            retVal = true;
        }
        return retVal;
    }

	// The navigationMovement method is called by the event handler when the trackball is used.
    protected boolean navigationMovement(int dx, int dy, int status, int time)
    {
        if (dx > 0) {
            ((Player)gameObjects.elementAt(0)).moveRight();
        } else if (dx < 0) {
            ((Player)gameObjects.elementAt(0)).moveLeft();
        }

        if (dy != 0) {
            //((GameObject)gameObjects.elementAt(0)).setYVel(dy/Math.abs(dy)*5+((GameObject)gameObjects.elementAt(0)).getYVel());
        }

        return true;
    }

}
