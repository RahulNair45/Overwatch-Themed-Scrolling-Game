public class DeflectPowerUp extends Entity implements Consumable, Scrollable {
    
    protected static final String DEFLECT_IMAGE_FILE = "assets/Odbicie.png"; // not sure why this was wrong in the file ngl
    //Dimensions of the Get  
    protected static final int DEFLECT_WIDTH = 55;
    protected static final int DEFELCT_HEIGHT = 55;
    //Speed that the Get moves (in pixels) each time the game scrolls
    protected static final int DEFLECT_SCROLL_SPEED = 5;
    
    
    public DeflectPowerUp(){
        this(0, 0);        
    }
    
    public DeflectPowerUp(int x, int y){
        super(x, y, DEFLECT_WIDTH, DEFELCT_HEIGHT, DEFLECT_IMAGE_FILE);  
    }
    
    public DeflectPowerUp(int x, int y, String imageFileName){
        super(x, y, DEFLECT_WIDTH, DEFELCT_HEIGHT, imageFileName);
    }
    
    public int getScrollSpeed(){
        return DEFLECT_SCROLL_SPEED;
    }
    
    //Move the Get left by its scroll speed
    public void scroll(){
        setX(getX() - DEFLECT_SCROLL_SPEED);
    }

    public int getPointsValue(){
        //implement me!
        throw new IllegalStateException("Hey 102 Student! You need to implement getPointsValue in Avoid.java!");
     }
     
     //Colliding with an Avoid Reduces players HP by 1
     public int getDamageValue(){
         return 0;
     }
    
}
