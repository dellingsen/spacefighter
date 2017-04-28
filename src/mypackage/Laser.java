package mypackage;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.XYRect;
     
public class Laser extends GameObject    
{    
    public static final double MU_FRICTION = 0.95;
    private Bitmap bitmap;    
    private String state;
    private int lives;
    private boolean repaint;
    
	public Laser(int x, int y, int xVel, int yVel, GameObject parent)
    {
		String laserImage = "";
		if (parent instanceof Player) {
			laserImage = "laser-player.jpg";
		} else {
			laserImage = "laser-enemy.jpg";
		}
        bitmap = EncodedImage.getEncodedImageResource(laserImage).getBitmap();
        xPos = x;
        yPos = y;
        xVelocity = xVel;
        yVelocity = yVel;
        type = TYPE_LASER;
        lives = 1;
        this.parent = parent;
        state = OBJSTATE_ALIVE;
    }

    public Bitmap getBitmap() {  
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getX() {
        return xPos;
    }

    public double getXVel() {
        return xVelocity;
    }

    public int getY() {
        return yPos;
    }

    public double getYVel() {
        return yVelocity;
    }

    /**
     * This will be called repeatedly after it is created so it moves across
     * the screen without stopping until it reaches the end of the screen.
     */
    public void update() {
        //xPos = (int)(xPos - xVelocity);
        if (getYVel() > 0) {
        	yPos = yPos - 10;
        }
        else {
        	yPos = yPos + 10;
        }
        
        //Set life = 0 so we can remove from screen
        if (yPos <= 10 || yPos >= Display.getHeight()-20) {
        	lives = 0;
        }
        
        //checkBounds();
        
        //Don't need to slow down the object to stop
        //applyFriction();
    }
    
    
    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public void setVelocity(double vx, double vy) {
        xVelocity = vx; 
        yVelocity = vy;
    }

    public void setX(int x) {
        xPos = x;
    }

    public void setXVel(double vx) {
        xVelocity = vx;
    }

    public void setY(int y) {
        yPos = y;
    }

    public void setYVel(double vy) {
        yVelocity = vy;
    }

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public int getLives() {
		return lives;
	}

	public GameObject getParent() {
		return parent;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public String getState() {
		return state;
	}

	/**
	 * Used to build a rectangle of our image so we can detect collisions
	 * between objects when two shapes overlap.
	 */
	public XYRect getRect() {
	     return new XYRect(getX(),getY(),bitmap.getWidth(),bitmap.getHeight());
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getLevel() {
		return 0;
	}
	
	public void setRepaint(boolean paint) {
		repaint = paint;
	}

	public boolean getRepaint() {
		return repaint;
	}

}
