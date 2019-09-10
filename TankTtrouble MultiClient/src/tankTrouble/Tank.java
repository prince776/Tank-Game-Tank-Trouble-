package tankTrouble;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Tank {
	
	public static final float maxVel = 1.5f, maxAcc = 0.3f,rotSpeed = 0.025f,shootVel=2f,maxHealth=10f;
	public Vector pos,vel,acc;
	public int w,h,r;
	public Color color1,color2;
	public float rot;
	public boolean shooting=false,gameOver=false;
	public int shootingCoolDown=2000;
	
	public String status = "Alive";
	
	public String lobbyMsg = "In Lobby, Players: ";
	
	public long lastTime,timer=0;
	
	public static ArrayList<Bullet> bullets;
	
	public float health=maxHealth;
	
	public int dim = 30;
	
	public Tank(Vector pos , int w,int h,Color c1,Color c2){
		this.pos=pos;
		this.w=dim;
		this.h=dim;
		//this.color1 = c1;
		//this.color2 = c2;
		this.vel = new Vector();
		this.acc = new Vector();
		this.rot=0;
		this.r=w/4;
		bullets = new ArrayList<Bullet>();
		lastTime = System.currentTimeMillis();
		assignRandomPos();
		assignColor();
	}
	
	public void assignColor(){
		if(Game.server == null  && Game.client == null){
			color1 = new Color(51, 204, 51);
			color2 =new Color(31, 122, 31);
		}
		else if(Game.server != null){
			color1 = new Color(51, 204, 51);
			color2 =new Color(31, 122, 31);
		}else if(Game.client !=null){
			switch(GameClient.playerNumAssigned){
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
	}
	
	public void assignRandomPos(){
			pos.x = Level.rand.nextFloat() * Game.width;
			pos.y = Level.rand.nextFloat() * Game.height;
		 for(int i=0;i<Level.obstacles.length;i++){
				Obstacle o = Level.obstacles[i];
				Ellipse2D e = new Ellipse2D.Float();
				e.setFrame(pos.x+vel.x,pos.y+vel.y,w,h);
				Rectangle2D r = new Rectangle2D.Float();
				r.setFrame(o.x,o.y,o.w,o.h);
				if(e.intersects(r)){
					pos.x = Level.rand.nextFloat() * Game.width;
					pos.y = Level.rand.nextFloat() * Game.height;
					i=0;
				}
			}
	 }
	
	public void applyForce(Vector force){
		this.acc.add(force);
		this.acc.limit(maxAcc);
	}
	
	public void tick(){
		if(health<=0){
			gameOver=true;
			status = "Dead!";
		}else{
			status = "Alive";
		}
		if(health>0)
			getInput();
		vel.add(acc);
		
		String prevStat = status;
		if(GameServer.inGame || GameClient.inGame)
			status = "WINNER!";

		for(int i=0;i <Game.tankMPs.size();i++){
			if(Game.tankMPs.get(i).health>0)
				status = prevStat;
		}
		
		//COLLISION
		for(int i=0;i<Level.obstacles.length;i++){
			Obstacle o = Level.obstacles[i];
			Ellipse2D e = new Ellipse2D.Float();
			e.setFrame(pos.x+vel.x,pos.y+vel.y,w,h);
			Rectangle2D r = new Rectangle2D.Float();
			r.setFrame(o.x,o.y,o.w,o.h);
			if(e.intersects(r))
				vel.multiply(0);
		}
		//
		
		pos.add(vel);
		vel.limit(maxVel);
		vel.multiply(0.80f);
		acc.multiply(0);
		//timer
		long now = System.currentTimeMillis();
		timer+=now-lastTime;
		lastTime = now;
		
		if(timer>=shootingCoolDown){
			timer=0;
			shooting=false;
		}
		
		boolean stopBullets=false;
		for(int i=0;i<Game.tankMPs.size();i++){
			if(Game.tankMPs.get(i).status.contains("WINNER"))
				stopBullets = true;
		}
		if(status.contains("WINNER") )stopBullets = true;
		for(int i=bullets.size()-1;i>=0;i--){
				bullets.get(i).tick();
			
			if(bullets.get(i).vel.getMagnitude()<=1.5f){
				bullets.remove(i);
			}else{
				float x = bullets.get(i).pos.x;
				float y = bullets.get(i).pos.y;
				
				if(new Rectangle((int)pos.x,(int)pos.y,w,h).contains(x,y)){
					if(Game.server !=null && GameServer.inLobby)
						continue;
					if(Game.client !=null && GameClient.inLobby)
						continue;
					if(!stopBullets)
						health-=0.1f;
				}
				
				if(x<0||y<0||x>Game.width + Bullet.r || y > Game.height + Bullet.r){
					bullets.remove(i);
				}	
			}
		}
		
		if(Game.server != null){
			if(GameServer.beginGame && GameServer.inLobby){
				
				bullets.clear();
				assignRandomPos();
				health=maxHealth;
				GameServer.inGame = true;
				GameServer.inLobby = false;
				
				Game.server.sendDataToAllClients("04 1".getBytes());
				
			}
			if(GameServer.inGame && GameServer.stopGame){
				bullets.clear();
				assignRandomPos();
				health=maxHealth;
				GameServer.inGame = false;
				GameServer.inLobby = true;
				GameServer.stopGame = false;
				GameServer.beginGame= false;
				Game.server.sendDataToAllClients("06 1".getBytes());
			}
			if(GameServer.inLobby){
				String toSend = "03 ";
				toSend += GameServer.numConnected+1;
				toSend += " ";
				toSend += GameServer.maxClients+1;
				toSend += " ";
				Game.server.sendDataToAllClients(toSend.getBytes());
			}
		}
		
		if(Game.client != null){
			if(GameClient.beginGame && GameClient.inLobby){
				
				bullets.clear();
				assignRandomPos();
				health=maxHealth;
				GameClient.inGame = true;
				GameClient.inLobby = false;
				
				
			}	if(GameClient.inGame && GameClient.stopGame){
				bullets.clear();
				assignRandomPos();
				health=maxHealth;
				GameClient.inGame = false;
				GameClient.inLobby = true;
				GameClient.stopGame = false;
				GameClient.beginGame= false;
			}
	
		}
		
		//POS, ROT, HEALTH data exchange
		
		//server sends its data to all clients
		if(Game.server !=null){
			String toSend = "01 ";
			toSend += Math.round(pos.x *100.00) /100.00+ " ";
			toSend += Math.round(pos.y *100.00) /100.00 + " ";
			toSend += Math.round(rot *100.0) /100.0+" ";
			toSend += Math.round(health * 100.00)/100.00 + " ";
			Game.server.sendDataToAllClients(toSend.getBytes());
		}
		
		//all clients sends their data to the server
		if(Game.client !=null){
			String toSend = "01 ";
			toSend += Math.round(pos.x *100.00) /100.00+ " ";
			toSend += Math.round(pos.y *100.00) /100.00 + " ";
			toSend += Math.round(rot *100.0) /100.0+" ";
			toSend += Math.round(health * 100.00)/100.00 + " ";
			Game.client.sendData(toSend.getBytes(), GameClient.ipAddress, Game.server.port);
		}
		
		//server sends data of all other clients to all clients
		if(Game.server!=null){
			for(int i=0;i<Game.tankMPs.size();i++){
				TankMP t = Game.tankMPs.get(i);
				String toSend = "05 ";
				toSend += Math.round(t.pos.x *100.00) /100.00+ " ";
				toSend += Math.round(t.pos.y *100.00) /100.00 + " ";
				toSend += Math.round(t.rot *100.0) /100.0+" ";
				toSend += Math.round(t.health * 100.00)/100.00 + " ";
				toSend += t.playerNum + " ";
				Game.server.sendDataToAllClientsExceptOne(toSend.getBytes(),t);
			}
		}
		
//		if(GameServer.inLobby || GameClient.inLobby){
//			for(int i=0;i<Game.tankMPs.size();i++){
//				if(Game.tankMPs.get(i).status.equalsIgnoreCase("DISCONNECTED"))
//					Game.tankMPs.remove(i);
//			}
//		}
		
	}
	
	public void getInput(){
		if(Key.left){
			rot -= rotSpeed;
		}else if(Key.right){
			rot += rotSpeed;
		}
		if(Key.up){
			float fx = (float)Math.cos(rot) * maxAcc;
			float fy = (float)Math.sin(rot) * maxAcc;
			applyForce(new Vector(fx,fy));
		}
		else if(Key.down){
			float fx = -(float)Math.cos(rot) * maxAcc;
			float fy = -(float)Math.sin(rot) * maxAcc;
			applyForce(new Vector(fx,fy));
		}
		
		if(Key.space && !shooting){
			shootBullet();
		}
		
			
		
	}
	
	public void shootBullet(){
		
		float x=0,y=0;
		float vx=0,vy=0;
		
		//set origin at center
		x = pos.x + w/2 ;
		y = pos.y + h/2;
		x+= (w/2+8)*Math.cos(rot);
		y+= (w/2+8)*Math.sin(rot);

		vx = (float)Math.cos(rot);
		vy = (float)Math.sin(rot);
		Vector v = new Vector(vx,vy);
		v.setMagnitude(shootVel + this.vel.getMagnitude());
		
		bullets.add(new Bullet(new Vector(x,y),v));
		shooting=true;
		
		if(Game.client !=null && Game.tankMPs.size() > 0 ){
			String toSend = "02 ";
			toSend += Math.round(x *100.00) /100.00+ " ";
			toSend += Math.round(y *100.00) /100.00 + " ";
			toSend += Math.round(v.x *100.00) /100.00 + " ";
			toSend += Math.round(v.y *100.00) /100.00+" ";
			Game.client.sendData(toSend.getBytes(), Game.client.ipAddress, Game.server.port);
		}
		if(Game.server !=null && Game.tankMPs.size() > 0){
			String toSend = "02 ";
			toSend += Math.round(x *100.00) /100.00+ " ";
			toSend += Math.round(y *100.00) /100.00 + " ";
			toSend += Math.round(v.x *100.00) /100.00 + " ";
			toSend += Math.round(v.y *100.00) /100.00+" ";
			Game.server.sendDataToAllClients(toSend.getBytes());
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
		
		for(int i=bullets.size()-1;i>=0;i--){
			bullets.get(i).render(g1);
		}
//		if(gameOver){
//			g.setColor(Color.RED);
//			g.setFont(new Font("sans-serif" , Font.BOLD,48));
//			g1.drawString("GAME OVER!", 300, 150);
//		}
		
		if(Game.server != null){
			if(!GameServer.beginGame){
				g.setColor(Color.DARK_GRAY);
				g.drawString(lobbyMsg + (GameServer.numConnected+1) + "/" +( GameServer.maxClients+1),200,20);
			}
		}if(Game.client !=null){
			if(!GameClient.beginGame){
				g.setColor(Color.DARK_GRAY);
				g.drawString( lobbyMsg + (GameClient.numPlayers) + "/" +( GameClient.maxPlayers),200,20);
			}
		}
		
//		if(Game.server!=null)
//			renderHealthBar(g,20,20);
//		else if(Game.client!=null){
//			renderHealthBar(g, 20, 20 + 30*(GameClient.playerNumAssigned-1));
//		}
//		else if(Game.server ==null && Game.client == null){
//			renderHealthBar(g, 20, 20);
//		}
		
		
	}
	
	public void renderBlaster(Graphics2D g){
		g.setColor(Color.BLACK);
		for(int i=0;i<3;i++){
			g.drawOval(w/2-r+i, h/2-r+i, 2*r-2*i, 2*r-2*i);
		}
		g.fillRect(w/2+r, h/2-2, 18, 4);
		g.setColor(color1);
		g.fillRect(w/2+r+1, h/2-2+1, 18-2, 4-2);
//		System.out.println(w/2+r+20-w);
	}
	
	public void renderHealthBar(Graphics g,int x,int y){
		g.setColor(color2);
		int wid = 100 , hei = 20;
		g.drawRect(x, y, wid, hei);
		int healthWidth = (int)(health/maxHealth * wid);
		g.setColor(color1);
		g.fillRect(x, y, healthWidth, hei);
		g.drawString(Game.username + " - YOU (" +status + ")" , x + wid + 10 ,y+15);
		
		
		g.setColor(Color.black);
		g.drawString(((Game.server!=null)?"1":(Game.client!=null)?GameClient.playerNumAssigned+"":"0"), (int)pos.x, (int)pos.y);
	}
	
	public void renderHealth(Graphics g){
		if(Game.server!=null)
			renderHealthBar(g,20,20);
		else if(Game.client!=null){
			renderHealthBar(g, 20, 20 + 30*(GameClient.playerNumAssigned-1));
		}
		else if(Game.server ==null && Game.client == null){
			renderHealthBar(g, 20, 20);
		}
	}
	
}
