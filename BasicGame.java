import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Random;

//A Simple version of the scrolling game, featuring Avoids, Gets, and RareGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class BasicGame extends ScrollingGameEngine {
    
    //Dimensions of game window
    protected static final int DEFAULT_WIDTH = 900;
    protected static final int DEFAULT_HEIGHT = 600;  
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 300;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE = 20;    
    
    protected static final String INTRO_SPLASH_FILE = "assets/splash.gif";    // change to rule splash image    
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;
    
    
    //A Random object for all your random number generation needs!
    protected static final Random rand = new Random();

    // subtract current chance by previous chance to get percent of thing spawning
    
    // entities per row spawn chance

    private static final int zeroEntitiesSpawnedChance = 5; // 5 percent chance

    private static final int oneEntitiesSpawnedChance = 35; // 30 percent chance

    private static final int twoEntitiesSpawnedChance = 75; // 40 percent chance

    private static final int threeEntitiesSpawnedChance = 90; // 15 percent chance

    private static final int fourEntitiesSpawnedChance = 100; // 10 percent chance

    // general entity spawn chance

    private static final int rareGetSpawnChance = 7; // 8 percent chance

    private static final int getSpawnChance = 30; // 22  percent chance

    private static final int avoidSpawnChance = 100; // 69 percent chance

    // entity spawn chance when 3 avoids in row

    private static final int rareGetSpawnChanceWithThreeAvoid = 30; // 30 percent chance

    private static final int GetSpawnChanceWithThreeAvoid = 100; // 70 percent chance

    
    
    
    
    
    //Player's current score
    protected int score;
    
    //Stores a reference to game's Player object for quick reference
    //(This Player will also be in the displayList)
    protected Player player;
    
    
    
    
    
    public BasicGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public BasicGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBackgroundColor(Color.BLACK);
        this.setSplashImage(INTRO_SPLASH_FILE);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        score = 0;
    }
    
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities(); 
        checkPlayerCollisions();  
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0){            
            spawnEntities();
            garbageCollectOffscreenEntities();
        }
        
        //Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + ", Score: " + score);        
    }

    private void checkPlayerCollisions(){
        ArrayList<Entity> collisionsWithPlayer = checkCollision(player); // creates an array list of things player collides with 
        for (int i = 0; i < collisionsWithPlayer.size(); i++){ // and if it collides with entities do their respective tasks
            if (collisionsWithPlayer.get(i) instanceof Entity){
                handlePlayerCollision((Consumable)collisionsWithPlayer.get(i));
                //System.out.println("remove");
            }
        }
    }
    
    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int numberEntityInDisplayList = 0; numberEntityInDisplayList < displayList.size(); numberEntityInDisplayList++){
            if (displayList.get(numberEntityInDisplayList) instanceof Scrollable){
                ((Scrollable) displayList.get(numberEntityInDisplayList)).scroll();
            }
           
        }

    }
    
    
    //Handles "garbage collection" of the displayList
    //Removes entities from the displayList that have scrolled offscreen
    //(i.e. will no longer need to be drawn in the game window).
    protected void garbageCollectOffscreenEntities(){
        for (int entityNum = 0; entityNum < displayList.size(); entityNum++){
            if (displayList.get(entityNum) instanceof Get || displayList.get(entityNum) instanceof Avoid){
                if (displayList.get(entityNum).getX() < 0 - displayList.get(entityNum).getWidth()){
                    displayList.remove(entityNum);
                }
            }
        }
        
    }
    
    
    //Called whenever it has been determined that the Player collided with a consumable 
    protected void handlePlayerCollision(Consumable collidedWith){
        if (collidedWith instanceof Avoid){
            player.setHP(player.getHP() + collidedWith.getDamageValue());
        }
        else if (collidedWith instanceof RareGet){
            score += collidedWith.getPointsValue();
            player.setHP(player.getHP()+1);
        }
        else if (collidedWith instanceof Get){
            score += collidedWith.getPointsValue();
        }
        displayList.remove((Entity) collidedWith);
        if(isGameOver()){
            postgame();
        }
    }
    
    
    //Spawn new Entities on the right edge of the game board
    protected void spawnEntities(){
        int numOfEntityGenerator = rand.nextInt(100) + 1; // chooses a random number to decide the number of entities spawned per row
        if (numOfEntityGenerator <= zeroEntitiesSpawnedChance){
            spawnEntitiesHelper(0);
        }
        else if (numOfEntityGenerator <= oneEntitiesSpawnedChance){
            spawnEntitiesHelper(1);
        }
        else if (numOfEntityGenerator <= twoEntitiesSpawnedChance){
            spawnEntitiesHelper(2);
        }
        else if (numOfEntityGenerator <= threeEntitiesSpawnedChance){
            spawnEntitiesHelper(3);
        }
        else if (numOfEntityGenerator <= fourEntitiesSpawnedChance){
            spawnEntitiesHelper(4);
        }
    }

    private void spawnEntitiesHelper(int numOfEntitiesInRow){ // has a random number generator to help choose what type of entites are spawned in the column
        int badCounter = 0;
        for (int numOfEntitiesToSpawn = 0; numOfEntitiesToSpawn < numOfEntitiesInRow; numOfEntitiesToSpawn++){
            int randomHeight = rand.nextInt(getWindowHeight()-player.getHeight());
            int entityChance = rand.nextInt(100) + 1;
            if (badCounter < 3){
                if (entityChance <= rareGetSpawnChance){ 
                    RareGet superGood = new RareGet(getWindowWidth(), randomHeight);
                    displayList.add(superGood);
                }
                else if (entityChance <= getSpawnChance){ 
                    Get good = new Get(getWindowWidth(), randomHeight);
                    displayList.add(good);
                }
                else if (entityChance <= avoidSpawnChance){
                    Avoid bad = new Avoid(getWindowWidth(), randomHeight);
                    displayList.add(bad);
                }

            }
            else{ // if there are gonna be 4 entities in the column, make sure one of them is a get or rare get (avoids the player being forced to lose health)
                if (entityChance <= rareGetSpawnChanceWithThreeAvoid){ 
                    RareGet superGood = new RareGet(getWindowWidth(), randomHeight);
                    displayList.add(superGood);
                }
                else if (entityChance <= GetSpawnChanceWithThreeAvoid) { 
                    Get good = new Get(getWindowWidth(), randomHeight);
                    displayList.add(good);
                }
            }
            if (checkIfOverLappping()){
                numOfEntitiesToSpawn--;
            }
            else if (displayList.get(displayList.size() - 1) instanceof Avoid){
                badCounter++;
            }

  
        }
    }

    protected boolean checkIfOverLappping(){  // checks if an entity when spawned is overlapping another one in the column and if so, remove it
        for (int entityNum = 0; entityNum < displayList.size() - 1; entityNum++){
            if (displayList.get(displayList.size() - 1).isCollidingWith(displayList.get(entityNum))){
                displayList.remove(displayList.size() - 1);
                return true;
            }
        }
        return false;
    }
    
    
    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){
        if (player.getHP() <= 0){
            super.setTitleText("GAME OVER - You Lose!");
        }
        else{
            super.setTitleText("GAME OVER - You Won!");
        }
    }
    
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){
        if (player.getHP() <= 0){
            return true;
        }
        else if (score >= SCORE_TO_WIN){
            return true;
        }
        return false;

       
    }
    
    
    
    //Reacts to a single key press on the keyboard
    protected void reactToKey(int key){
        
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key) + ",  DisplayList size: " + displayList.size());
        
        //if a splash screen is active, only react to the "advance splash" key... nothing else!
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            
            return;
        }
        else if((key == UP_KEY || key == DOWN_KEY || key == LEFT_KEY || key == RIGHT_KEY) && !isPaused){ // deals with movement
            if (key == UP_KEY && player.getY() > 0){
                player.setY(player.getY()+(-1*player.getMovementSpeed()));
            }
            else if(key == DOWN_KEY && player.getY() < (getWindowHeight() - Player.PLAYER_HEIGHT)){
                player.setY(player.getY()+(1*player.getMovementSpeed()));
            }
            else if(key == RIGHT_KEY && player.getX() < getWindowWidth() - Player.PLAYER_WIDTH){
                player.setX(player.getX()+(1*player.getMovementSpeed()));
            }
            else if(key == LEFT_KEY && player.getX() > 0){
                player.setX(player.getX()+(-1*player.getMovementSpeed()));
            }
        }
        else if ((key == SPEED_UP_KEY || key == SPEED_DOWN_KEY) && !isPaused){ // deals with speed change
            if (key == SPEED_UP_KEY){
                if (getGameSpeed() < MAX_GAME_SPEED){
                    setGameSpeed(getGameSpeed() + SPEED_CHANGE);
                }
            }
            else if (key == SPEED_DOWN_KEY){
                if (getGameSpeed() > SPEED_CHANGE){
                    setGameSpeed(getGameSpeed() - SPEED_CHANGE);
                }
            }
        }
        else if (key == KEY_PAUSE_GAME){ // deals with pausing
            isPaused = !isPaused;
        }
    }    
    
    
    //Handles reacting to a single mouse click in the game window
    //Won't be used in Simple Game... you could use it in Creative Game though!
    protected MouseEvent reactToMouseClick(MouseEvent click){
        if (click != null){ //ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;//returns the mouse event for any child classes overriding this method
    }
    
    
    
    
}
