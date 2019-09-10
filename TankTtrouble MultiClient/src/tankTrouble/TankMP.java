package tankTrouble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.InetAddress;

public class TankMP {
	public static final float maxVel = 1.5f, maxAcc = 0.3f,rotSpeed = 0.025f,shootVel=2f,maxHealth=10f;
	
	public Vector pos;
	public int w,h,r;
	public Color color1,color2;
	public float rot;
	public boolean shooting=false,gameOver=false;
	public int shootingCoolDown=2000;
	public int playerNum =0;
	public String username,status = "Alive";
	
	public InetAddress ip;
	public int port ;
	
	public float health=maxHealth;
	public int dim = 30;

	public TankMP(Vector pos , int w,int h,Color c1,Color c2 , int playerNum,InetAddress ip , int port,String username){
		this.pos=pos;
		this.w=dim;
		this.h=dim;
		//this.color1 = c1;
		//this.color2 = c2;
		this.username = username;
		this.rot=0;
		this.r=w/4;
		this.playerNum=playerNum;
		this.ip = ip;
		this.port = port;
		assignColor();
	}
	
	
	
	
	public void tick(){
		assignColor();
		if(health<=0){
			gameOver=true;
			status = "Dead!";
		}else
			status = "Alive";
		
		
		String prevStat = status;
		if(Game.tank.health<=0){
			if(GameServer.inGame || GameClient.inGame)
				status = "WINNER!";
			for(int i=0;i <Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).equals(this))
					continue;
				if(Game.tankMPs.get(i).health>0)
					status = prevStat;
			}
		}
		
	}
	
	
	//SINGLE PLAYER DONE
	
	public void render(Graphics g1){
		assignColor();

		int x = (int)pos.x; int y = (int)pos.y;
		
		Graphics2D g = (Graphics2D)g1;
		
		
		g.translate(x +w/2, y+h/2);
		g.rotate(rot);
		g.translate(-w/2, -h/2);
		
		g.setColor(Color.black);
		g.fillRect(0,0, w, h);
		
		g.setColor(color2);
		g.fillRect(0+3, 0+3, w-6, h-6);
		
		g.setColor(color1);
		g.fillRect(0+6, 0+6, w-12, h-12);
		
		renderBlaster(g);
		
		g.translate(w/2, h/2);
		g.rotate(-rot);
		g.translate(-x-w/2, -y-h/2);
		
		
	}
	
	public void renderHealth(Graphics g){
		renderHealthBar(g, 20,20+ 30*(playerNum-1));
	}
	
	public void assignColor(){
		switch(playerNum){
			case 1:
				color1 = new Color(51, 204, 51);
				color2 =new Color(31, 122, 31);
				break;
			case 2:
				color1 = new Color(255, 0, 0);
				color2 = new Color(204, 0, 0);
				break;
			case 3:
				color1 = new Color(51, 51, 255);
				color2 = new Color(26, 26, 255);
				break;
			case 4:
				color1 = new Color(204, 0, 255);
				color2 = new Color(184, 0, 230);
				break;
			case 5:
				color1 = new Color(153, 51, 51);
				color2 = new Color(134, 45, 45);
				break;
		}	
	}
	
	public void renderBlaster(Graphics2D g){
		g.setColor(Color.BLACK);
		for(int i=0;i<3;i++){
			g.drawOval(w/2-r+i, h/2-r+i, 2*r-2*i, 2*r-2*i);
		}
		g.fillRect(w/2+r, h/2-2, 18, 4);
		g.setColor(color1);
		g.fillRect(w/2+r+1, h/2-2+1, 18-2, 4-2);
	}
	
	public void renderHealthBar(Graphics g,int x,int y){
		g.setColor(color2);
		int wid = 100 , hei = 20;
		g.drawRect(x, y, wid, hei);
		int healthWidth = (int)(health/maxHealth * wid);
		g.setColor(color1);
		g.fillRect(x, y, healthWidth, hei);
		g.drawString(username + " (" +status + ")" , x + wid + 10 ,y+15);
		g.setColor(Color.black);
		g.drawString(playerNum+"", (int)pos.x, (int)pos.y);
	
	}
	
}
