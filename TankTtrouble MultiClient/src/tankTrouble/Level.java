package tankTrouble;

import java.awt.Graphics;
import java.util.Random;

public class Level {
	
	public static Obstacle[] obstacles;
	public static Random rand;
	
	public Level(int numObst){
		rand = new Random();
		obstacles = new Obstacle[7+4];
		
		
		int w = Game.width;
		int h = Game.height;
		obstacles[0] = new Obstacle(0,0, Game.width, 10);
		obstacles[1] = new Obstacle(0, Game.height-10, Game.width, 10);
		obstacles[2] = new Obstacle(0,0, 10, Game.height);
		obstacles[3] = new Obstacle(Game.width-10,0, 10, Game.height);
		
		obstacles[4] = new Obstacle(w/4,h/4, w/2, 10);
		obstacles[5] = new Obstacle(w/4,3*h/4, w/2, 10);
		obstacles[6] = new Obstacle(2*w/5,h/2, w/5, 10);
		obstacles[7] = new Obstacle(0,3*h/5, w/5, 10);
		obstacles[8] = new Obstacle(4*w/5,2*h/5, w/5, 10);
		obstacles[9] = new Obstacle(5*w/7,h/4, 10, h/4);
		obstacles[10] = new Obstacle(2*w/7,3*h/4 - h/4, 10, h/4);

		
		
//		for(int i=4;i<obstacles.length;i++){
//			if(i<(obstacles.length)/2){//horiz
//				int x = (int)(rand.nextFloat() * Game.width);
//				int y = (int)(rand.nextFloat() * Game.height);
//				int w = (int)(rand.nextFloat()*Game.width/6 + Game.width/6);
//				int h = 10;
//				obstacles[i] = new Obstacle(x, y, w, h);
//			}else {//horiz
//				int x = (int)(rand.nextFloat() * Game.width);
//				int y = (int)(rand.nextFloat() * Game.height);
//				int h = (int)(rand.nextFloat()*Game.width/6 + Game.width/6);
//				int w = 10;
//				obstacles[i] = new Obstacle(x, y, w, h);
//			}
//		}
	}
	
	public void render(Graphics g){
		for(Obstacle o : obstacles){
			o.render(g);
		}
	}
	
}
