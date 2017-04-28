package mypackage;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.XYRect;
     
public class EnemyShip extends GameObject    
{    
	public static final double MU_FRICTION = 0.95;
    private static final int SPEED_OR_LENGTH_OF_ANGLE = 4;
    private int angle;
    private Bitmap bitmap;    
    private int image;    
    private String state;
    private int level;
    private int lives;
    private boolean repaint;
    
	public EnemyShip(int level)
    {
        bitmap = EncodedImage.getEncodedImageResource(enemyGraphic[level-1]).getBitmap();
        angle = 0;
        xVelocity = 0;
        yVelocity = 0;        
        type = TYPE_ENEMY;
        lives = 1;
        state = OBJSTATE_ALIVE;
        this.level = level;
    }

    public int getLevel() {
        return level;
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

    public void update() {
        xPos = (int)(xPos - (int)xVelocity);
        yPos = (int)(yPos - (int)yVelocity);
        applyFriction();
        //Enemies don't need to check screen bounds - they think on their own
        //checkBounds();
    }
    
    /**
     * Need to get Velocity down to 0 to stop the ship motion.
     */
    public void applyFriction()
    {
        xVelocity = xVelocity * MU_FRICTION;
        yVelocity = yVelocity * MU_FRICTION;
    }
    
    public void rotateRight() 
    {
       angle = (angle - 10) % 360;
    }
    
    public void rotateLeft() 
    {
        angle = (angle + 10) % 360;
    }
        
    /**
     * Use the angle to compute new x and y coordinates (x,y velocity) based on a length of movement,
     * which is then added to the current x,y coordinates where the image is re-drawn and recomputed
     * based on a friction component to slowly stop the motion once it has started.
     * 
     */
    public void forwardBlaster()
    {
        double a = Math.toRadians(angle);

        //Basic Definitions (from trigonometry book) : cos(a) = x/h, sin(a) = y/h
        //Use these values if using a "clock" angle - 0 degrees is at 12:00
        //xVelocity = xVelocity + (int) Math.floor((Math.cos(a)) * SPEED_OR_LENGTH_OF_ANGLE);
        //yVelocity = yVelocity + (int) Math.floor((Math.sin(a)) * SPEED_OR_LENGTH_OF_ANGLE);
        
        //Standard Geometric Orientation
        //Now we have to mirror our entire universe so that the X and Y axes are flipped
        //since the y-axis is facing the other direction, 
        //you multiply all y-values by a negative 1 to get the correct orientation
    	System.out.println("Ship - forwardBlaster() - sin - " + Math.floor((Math.sin(angle)) * SPEED_OR_LENGTH_OF_ANGLE));
    	System.out.println("Ship - forwardBlaster() - cos - " + (-1)*(Math.floor((Math.cos(angle)) * SPEED_OR_LENGTH_OF_ANGLE)));
        xVelocity = xVelocity + (int) Math.floor((Math.sin(angle)) * SPEED_OR_LENGTH_OF_ANGLE);
        yVelocity = yVelocity + (-1) * ((int) Math.floor((Math.cos(angle)) * SPEED_OR_LENGTH_OF_ANGLE));
    }
    
    public void setPosition(int x, int y) 
    {
        xPos = x;
        yPos = y;
    }

    public void setVelocity(double vx, double vy) 
    {
        xVelocity = vx; 
        yVelocity = vy;
    }

    public void setX(int x) 
    {
        xPos = x;
    }

    public void setXVel(double vx) 
    {
        xVelocity = vx;
    }

    public void setY(int y) 
    {
        yPos = y;
    }

    public void setYVel(double vy) 
    {
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

	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public GameObject getParent() {
		return null;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public XYRect getRect() {
	     return new XYRect(getX(),getY(),bitmap.getWidth(),bitmap.getHeight());
	}

	public void setRepaint(boolean paint) {
		repaint = paint;
	}

	public boolean getRepaint() {
		return repaint;
	}

	/*
    public void render(VG vg)
    {
        VG11 vg11 = (VG11)vg;
        vg11.vgSeti(VG11.VG_MATRIX_MODE, VG11.VG_MATRIX_IMAGE_USER_TO_SURFACE);
        vg11.vgLoadIdentity();
        
        int bitmapWidth  = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        vg11.vgTranslate(xPos - bitmapWidth/2, yPos - bitmapHeight/2);
        
        vg11.vgTranslate(bitmapWidth/2, bitmapHeight/2);
        vg11.vgRotate(angle);
        vg11.vgTranslate(-bitmapWidth/2,-bitmapHeight/2);
        
        vg11.vgDrawImage(_image);
    }

    public void initialize(VG vg)
    {
    	System.out.println("EnemyShip - initialize() - shipNumber = " + shipNumber);
        VG11 vg11 = (VG11)vg;
        image = VGUtils.vgCreateImage(vg11, bitmap, true, VG11.VG_IMAGE_QUALITY_BETTER);
    }
    */

}
