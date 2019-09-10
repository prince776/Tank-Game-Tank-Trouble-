package tankTrouble;

import java.awt.Color;
import java.awt.Graphics;

public class Obstacle {
	
	public int x,y,w,h;

	public Obstacle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void render(Graphics g){
		g.setColor(Color.black);
		g.fillRoundRect(x, y, w, h, 4, 4);
	}
	
}
