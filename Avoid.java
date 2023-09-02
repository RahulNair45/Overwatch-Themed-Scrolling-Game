//Avoids are entities the player needs to avoid colliding with.
//If a player collides with an avoid, it reduces the players Hit Points (HP).
public class Avoid extends Entity implements Consumable, Scrollable {
    
    //Location of image file to be drawn for an Avoid
    private static final String AVOID_IMAGE_FILE = "assets/avoid.gif";
    //Dimensions of the Avoid    
    private static final int AVOID_WIDTH = 75;
    private static final int AVOID_HEIGHT = 75;
    //Speed that the avoid moves each time the game scrolls
    private static final int AVOID_SCROLL_SPEED = 5;

    private static final int AVOID_REVERSE_SCROLL_SPEED = 10;

    protected static final int AVOID_POINT_VALUE = -40;

    private boolean isDeflected = false;

    
    public Avoid(){
        this(0, 0);        
    }
    
    public Avoid(int x, int y){
        super(x, y, AVOID_WIDTH, AVOID_HEIGHT, AVOID_IMAGE_FILE);  
    }
    
    
    public int getScrollSpeed(){
        return AVOID_SCROLL_SPEED;
    }
    
    //Move the avoid left by the scroll speed
    public void scroll(){
        if (isDeflected){
            setX(getX() + AVOID_REVERSE_SCROLL_SPEED);
        }
        else{
            setX(getX() - AVOID_SCROLL_SPEED);
        }
    }

    public void gotDeflect(){
        isDeflected = true;
    }

    public boolean isDeflectedChecker(){
        return isDeflected;
    }
    
    //Colliding with an Avoid does not affect the player's score
    public int getPointsValue(){
       return AVOID_POINT_VALUE;
    }
    
    //Colliding with an Avoid Reduces players HP by 1
    public int getDamageValue(){
        return -1;
    }
    
}
