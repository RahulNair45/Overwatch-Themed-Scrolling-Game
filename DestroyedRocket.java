public class DestroyedRocket extends Entity implements Consumable{
        private static final String EXPLOSION_IMAGE_FILE = "assets/explosion.gif";
    
        private static final int EXPLOSION_WIDTH = 75;
        private static final int EXPLOSION_HEIGHT = 75;

        private int explosionEndTime;
        
        public DestroyedRocket(){
            this(0, 0);        
        }
        
        public DestroyedRocket(int x, int y){
            super(x, y, EXPLOSION_WIDTH, EXPLOSION_HEIGHT, EXPLOSION_IMAGE_FILE);  
        }
        
        public DestroyedRocket(int x, int y, String imageFileName){
            super(x, y, EXPLOSION_WIDTH, EXPLOSION_HEIGHT, imageFileName);
        }

        public void setExplosionEndTime(int time){
            explosionEndTime = explosionEndTime + time;
        }

        public int getExplosionEndTime(){
            return explosionEndTime;
        }

        public int getPointsValue(){
            return 0;
         }
         
         //Colliding with an Avoid Reduces players HP by 1
         public int getDamageValue(){
             return 0;
         }


    
    
}
