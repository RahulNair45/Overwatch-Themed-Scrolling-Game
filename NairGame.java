import java.awt.Color;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Random;

public class NairGame extends BasicGame  {

    protected static final int DEFAULT_WIDTH = 900;

    protected static final int SCORE_TO_WIN = 400;

    public static final int DEFLECT_KEY = KeyEvent.VK_E;

    protected static final String INTRO_SPLASH_FILE = "assets/Genji Run (1).gif";

    private static final String NAIR_AVOID_IMAGE_FILE = "assets/rocket4-removebg-preview.png";

    private static final String NAIR_AVOID_REFLECTED_IMAGE_FILE = "assets/rocket4-removebg-preview-reverse.png";

    protected static final String NAIR_GET_IMAGE_FILE = "assets/healMini2.png";

    protected static final String NAIR_RAREGET_IMAGE_FILE = "assets/healthpack02.png";

    protected static final String BACKGROUND_IMAGE = "assets/hanamuraInside.png"; 

    protected static final String NAIR_PLAYER_IMAGE_FILE = "assets/genji3-removebg-preview.png";

    protected static final String NAIR_PLAYER_NANO_IMAGE_FILE = "assets/NanoGenji-removebg-preview.png";

    protected static final String NAIR_PLAYER_NANO_DEFLECT_IMAGE_FILE = "assets/pngegg (1).png";

    protected static final String NAIR_PLAYER_DEFLECT_IMAGE_FILE = "assets/GenjiDeflect.png";

    protected static final String WIN_IMAGE_FILE = "assets/GamwWin.gif";

    protected static final String LOSS_IMAGE_FILE = "assets/defeat-overwatch.gif";

    private static final int zeroEntitiesSpawnedChance = 5; // 5 percent chance

    private static final int oneEntitiesSpawnedChance = 35; // 30 percent chance

    private static final int twoEntitiesSpawnedChance = 75; // 40 percent chance

    private static final int threeEntitiesSpawnedChance = 90; // 15 percent chance

    private static final int fourEntitiesSpawnedChance = 100; // 10 percent chance

    
    private static final int rareGetSpawnChance = 1; // 8 percent chance

    private static final int getSpawnChance = 14; // 22  percent chance

    private static final int deflectSpawnChance = 20;

    private static final int nanoSpawnChance = 22;

    private static final int avoidSpawnChance = 100; // 69 percent chance


    // entity spawn chance when 3 avoids in row

    private static final int rareGetSpawnChanceWithThreeAvoid = 10; // 30 percent chance

    private static final int GetSpawnChanceWithThreeAvoid = 50; // 70 percent chance

    private static final int deflectSpawnChanceWithThreeAvoid = 80;

    private static final int nanoSpawnChanceWithThreeAvoid = 100;

    private static final int nanoSpeedChnage = 3;

    private static final int speedChangeAfterCollectingHealing = 3;

    private  boolean deflectUsable = false;
    private  boolean deflectActive = false;
    private boolean nanoBoostActive = false;

    private int smallHealCounter = 0;

    private static final int smallHealsNeededForLife = 10;

    private int deflectEndTime;

    private int nanoEndTime;

    private static final int explosionLastingTime = 40;

    private int deflectCounter;

    private int avoidsHitCounter;

    private int smallHealHitCounter;

    private int bigHealHitCounter;

    private int avoidsDestroyed;


    protected void pregame(){
        this.setBackgroundImage(BACKGROUND_IMAGE);
        this.setSplashImage(INTRO_SPLASH_FILE);
        setTitleText("Genji Run: click enter to continue");
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        ((Entity) player).setImageName(NAIR_PLAYER_IMAGE_FILE);
        displayList.add(player); 
        score = 0;
    }

    

    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities(); 
        if (deflectActive){ 
            if (ticksElapsed == deflectEndTime){ // deactivates deflect after a certain time
                deflectActive = false;
            }
        }
        if (nanoBoostActive){
            if (ticksElapsed == nanoEndTime){ // deactivates nano after a certain time
                nanoBoostActive = false;
                player.setMovementSpeed(player.getMovementSpeed() - nanoSpeedChnage);
            }
        }
        if (nanoBoostActive & deflectActive){
            ((Entity) player).setImageName(NAIR_PLAYER_NANO_DEFLECT_IMAGE_FILE);
        }
        else if (nanoBoostActive){
            ((Entity) player).setImageName(NAIR_PLAYER_NANO_IMAGE_FILE);
        }
        else if (deflectActive){
            ((Entity) player).setImageName(NAIR_PLAYER_DEFLECT_IMAGE_FILE);
        }
        else{
            ((Entity) player).setImageName(NAIR_PLAYER_IMAGE_FILE);
        }
        checkDeflectPlayerCollisions(); 
        checkPlayerCollisions();  
        checkAvoidCollisions();
        deleteExplosions();
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0){            
            spawnEntities();
            garbageCollectOffscreenEntities();
        }
        //Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + ", Score: " + score + ", Small Heals Needed Before Health: " + (smallHealsNeededForLife - smallHealCounter) + ", Deflect Available: " + deflectUsable);        
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

    private void checkAvoidCollisions(){
        for (int j = 0; j < displayList.size() - 1; j++){
            if (displayList.get(j) instanceof Avoid){
                if (((Avoid)displayList.get(j)).isDeflectedChecker()){
                    ArrayList<Entity> collisionsWithDeflectedAvoid = checkCollision(displayList.get(j)); // creates an array list of things player collides with 
                    for (int i = 0; i < collisionsWithDeflectedAvoid.size(); i++){ // and if it collides with entities do their respective tasks
                        if (collisionsWithDeflectedAvoid.get(i) instanceof Avoid){
                            if (!((Avoid) collisionsWithDeflectedAvoid.get(i)).isDeflectedChecker()){
                                 handleAvoidCollision((Consumable)collisionsWithDeflectedAvoid.get(i));
                            }
            
                            //System.out.println("remove");
                        }
                    }
                }
            }
        }
    }

    protected boolean isGameOver(){
        if (player.getHP() <= 0){
            return true;
        }
        else if (score >= SCORE_TO_WIN){
            return true;
        }
        return false;

       
    }

    private void checkDeflectPlayerCollisions(){ // if deflect is active, checks if an avoid hits the front of the player
        ArrayList<Entity> frontCollisionsWithPlayer = checkDeflectCollision(player); // creates an array list of things player collides with 
        for (int i = 0; i < frontCollisionsWithPlayer.size(); i++){ // and if it collides with entities do their respective tasks
            if (frontCollisionsWithPlayer.get(i) instanceof Avoid && deflectActive){
                handleDeflectPlayerCollision((Consumable)frontCollisionsWithPlayer.get(i));
            }
        }
    }

    protected void handlePlayerCollision(Consumable collidedWith){
        if (collidedWith instanceof Avoid){
            if (!((Avoid)collidedWith).isDeflectedChecker()){
                if (!nanoBoostActive){ // prevents damage if nano is active
                    player.setHP(player.getHP() + collidedWith.getDamageValue());
                    avoidsHitCounter++;
                    if (score + collidedWith.getPointsValue() >= 0){
                        score += collidedWith.getPointsValue();
                        setGameSpeed(getGameSpeed() - speedChangeAfterCollectingHealing * 2);
                    }
                    else if (score + (collidedWith.getPointsValue()/2) >= 0){
                        score += collidedWith.getPointsValue()/2;
                        setGameSpeed(getGameSpeed() - speedChangeAfterCollectingHealing);
                    }
                    smallHealCounter = 0;
                }
            }
        }
        else if (collidedWith instanceof RareGet){
            bigHealHitCounter++;
            score += collidedWith.getPointsValue() * 5;
            player.setHP(player.getHP()+1);
            setGameSpeed(getGameSpeed() + speedChangeAfterCollectingHealing * 5);
        }
        else if (collidedWith instanceof Get){
            smallHealHitCounter++;
            score += collidedWith.getPointsValue();
            setGameSpeed(getGameSpeed() + speedChangeAfterCollectingHealing);
            smallHealCounter++;
            if (smallHealCounter >= smallHealsNeededForLife){
                player.setHP(player.getHP()+1);
                smallHealCounter = 0;
            }

        }
        else if (collidedWith instanceof DeflectPowerUp && !deflectUsable){
            deflectUsable = true;
        }
        else if (collidedWith instanceof NanoBoost){ // speeds up charecter if nano is active
            if (nanoBoostActive){
                nanoEndTime = ticksElapsed + 100;
            }
            else{
                nanoBoostActive = true;
                player.setMovementSpeed(player.getMovementSpeed() + nanoSpeedChnage);
                nanoEndTime = ticksElapsed + 100;
            }
            
        }
        if (collidedWith instanceof Avoid){
            if (!((Avoid)collidedWith).isDeflectedChecker()){
                displayList.remove((Entity) collidedWith);
            }
        }
        else if (collidedWith instanceof DestroyedRocket){
            
        }
        else{
            displayList.remove((Entity) collidedWith);
        }
        if(isGameOver()){
            postgame();
        }
    }
    
    protected void postgame(){
        if (player.getHP() <= 0){
            super.setTitleText("Small Heals Collected: " + smallHealHitCounter + ", Big Heals Collected: " + bigHealHitCounter + ", Avoids Hit: " + avoidsHitCounter + ", Rockets Deflected: " + deflectCounter + ", Rockets Destroyed: " + avoidsDestroyed);
            this.setSplashImage(LOSS_IMAGE_FILE);
        }
        else{
            super.setTitleText("Small Heals Collected: " + smallHealHitCounter + ", Big Heals Collected: " + bigHealHitCounter + ", Avoids Hit: " + avoidsHitCounter + ", Rockets Deflected: " + deflectCounter + ", Rockets Destroyed: " + avoidsDestroyed);
            this.setSplashImage(WIN_IMAGE_FILE);
        }
    }

    protected void handleDeflectPlayerCollision(Consumable collidedWith){ // reflects avoid if avoid is deflected
        if (collidedWith instanceof Avoid){
            deflectCounter++;
            ((Avoid)collidedWith).setImageName(NAIR_AVOID_REFLECTED_IMAGE_FILE);
            ((Avoid) collidedWith).gotDeflect();
        }
    }

    protected void handleAvoidCollision(Consumable collidedWith){
        int explosionY = ((Entity) collidedWith).getY();
        int explosionX = ((Entity) collidedWith).getX();
        displayList.remove((Entity) collidedWith);
        DestroyedRocket explosion = new DestroyedRocket(explosionX, explosionY);
        displayList.add(explosion);
        avoidsDestroyed++;
    }

    protected void deleteExplosions(){
        for (int i = 0; i < displayList.size() - 1; i++){
            if (displayList.get(i) instanceof DestroyedRocket){
                ((DestroyedRocket) displayList.get(i)).setExplosionEndTime(ticksElapsed + explosionLastingTime);
            }
        }
        for (int j = 0; j < displayList.size() - 1; j++){
            if (displayList.get(j) instanceof DestroyedRocket){
                if (((DestroyedRocket) displayList.get(j)).getExplosionEndTime() >= ticksElapsed){
                    displayList.remove(displayList.get(j));
                }
            }
        }
    }

    protected void garbageCollectOffscreenEntities(){
        for (int entityNum = 0; entityNum < displayList.size(); entityNum++){
            if (displayList.get(entityNum) instanceof Get || displayList.get(entityNum) instanceof Avoid){
                if (displayList.get(entityNum).getX() < 0 - displayList.get(entityNum).getWidth()){
                    displayList.remove(entityNum);
                }
            }
            else if (displayList.get(entityNum) instanceof Avoid){ // deletes avoids that are deflected once they leave the screen
                if (displayList.get(entityNum).getX() > DEFAULT_WIDTH + 100){
                    displayList.remove(entityNum);
                }
            }
        }
        
    }

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
                    ((Entity)superGood).setImageName(NAIR_RAREGET_IMAGE_FILE);
                    displayList.add(superGood);
                }
                else if (entityChance <= getSpawnChance){ 
                    Get good = new Get(getWindowWidth(), randomHeight);
                    ((Entity)good).setImageName(NAIR_GET_IMAGE_FILE);
                    displayList.add(good);
                }
                else if (entityChance <= deflectSpawnChance){
                    DeflectPowerUp deflect = new DeflectPowerUp(getWindowWidth(), randomHeight);
                    displayList.add(deflect);
                }
                else if (entityChance <= nanoSpawnChance){
                    NanoBoost nano = new NanoBoost(getWindowWidth(), randomHeight);
                    displayList.add(nano);
                }
                else if (entityChance <= avoidSpawnChance){
                    Avoid bad = new Avoid(getWindowWidth(), randomHeight);
                    ((Entity)bad).setImageName(NAIR_AVOID_IMAGE_FILE);
                    displayList.add(bad);
                }

            }
            else{ // if there are gonna be 4 entities in the column, make sure one of them is a get or rare get (avoids the player being forced to lose health)
                if (entityChance <= rareGetSpawnChanceWithThreeAvoid){ 
                    RareGet superGood = new RareGet(getWindowWidth(), randomHeight);
                    ((Entity)superGood).setImageName(NAIR_RAREGET_IMAGE_FILE);
                    displayList.add(superGood);
                }
                else if (entityChance <= GetSpawnChanceWithThreeAvoid) { 
                    Get good = new Get(getWindowWidth(), randomHeight);
                    ((Entity)good).setImageName(NAIR_GET_IMAGE_FILE);
                    displayList.add(good);
                }
                else if (entityChance <= deflectSpawnChanceWithThreeAvoid){
                    DeflectPowerUp deflect = new DeflectPowerUp(getWindowWidth(), randomHeight);
                    displayList.add(deflect);
                }
                else if (entityChance <= nanoSpawnChanceWithThreeAvoid){
                    NanoBoost nano = new NanoBoost(getWindowWidth(), randomHeight);
                    displayList.add(nano);
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
        else if (key == KEY_PAUSE_GAME){ // deals with pausing
            isPaused = !isPaused;
        }
        else if (key == DEFLECT_KEY && deflectUsable){ // activates deflect if deflect key is pressed
            deflectActive = true;
            deflectUsable = false;
            deflectEndTime = ticksElapsed + 50;
        }
    }    

}
