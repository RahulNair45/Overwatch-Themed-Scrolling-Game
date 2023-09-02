//Contains main... run me to launch the game!
public class Launcher{
    
    //Initializes and launches the game
    public static void main(String[] args){              
        ScrollingGameEngine game = new NairGame();
        //ScrollingGameEngine game = new BasicGame();
        game.play();    
    }        
    
}
