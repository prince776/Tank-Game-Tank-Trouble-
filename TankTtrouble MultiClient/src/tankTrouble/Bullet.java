package tankTrouble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
	
	public Vector pos,vel;
	public static int r =4;
	
	public Bullet(Vector pos,Vector vel){
		this.pos = pos;
		this.vel = vel;
	}
	
	public void tick(){
		pos.add(vel);
		vel.multiply(0.9999f);
		
		if(pos.x-r<=0) reflect(new Vector(1,0));
		if(pos.y-r<=0) reflect(new Vector(0,1));
		if(pos.x+r>=Game.width ) reflect(new Vector(-1,0));
		if(pos.y+r>=Game.height ) reflect(new Vector(0,-1));
		
		for(int i=0;i<Level.obstacles.length;i++){
			Obstacle o = Level.obstacles[i];
//			int x = (int)pos.x-r;
//			int y = (int)pos.y-r;
			if(new Rectangle(o.x,o.y,o.w,o.h).intersects(new Rectangle((int)pos.x+(int)vel.x-r,(int)pos.y+(int)vel.y-r,2*r,2*r))){
				Vector normal = new Vector(0,0);
//				if(o.h == 10){//horiz obst
//					if(vel.y>=0)
//						normal.y = -1;
//					else
//						normal.y = 1;
//				}else{
//					if(vel.x>=0)
//						normal.x = -1;
//					else
//						normal.x = 1;
//				}
//				
				
				if(pos.y <= o.y+ o.h && pos.y >= o.y){//horizontal collision
					if(pos.x<=o.x +vel.x )
						normal.x=-1;
					if(pos.x + vel.x >=o.x+o.w)
						normal.x=1;
				}
				if(pos.x <= o.x+ o.w && pos.x >= o.x){//vertical collision
					if(pos.y<=o.y +vel.y )
						normal.y=-1;
					if(pos.y + vel.y >=o.y+o.h)
						normal.y=1;
				}

				reflect(normal);
			}
		}
		
	}
	
	public void render(Graphics g){
		g.setColor(Color.BLACK);
		g.fillOval((int)pos.x-r, (int)pos.y-r, 2*r,2*r);
	}
	
	public void reflect(Vector normal){
		float mag = vel.getMagnitude();
		Vector reflected = new Vector();
		Vector incident = new Vector(vel.x,vel.y);
		incident.normalize();
		float iDotN = Vector.dot(incident, normal);
		
		normal.multiply(2*iDotN);
		incident.subtract(normal);
		reflected.add(incident);
		
		
		reflected.setMagnitude(mag);
		this.vel = new Vector(reflected.x,reflected.y);
		//formula used: R = I - 2(I.N)N
	}
	
}
