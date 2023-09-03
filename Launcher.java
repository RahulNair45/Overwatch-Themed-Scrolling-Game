//Contains main
public class Launcher{
    
    //Initializes and launches the game
    public static void main(String[] args){              
        ScrollingGameEngine game = new NairGame();
        //ScrollingGameEngine game = new BasicGame();
        game.play();    
    }        
    
}
