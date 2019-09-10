package tankTrouble;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;



public class Game implements Runnable{
	
	public static  int width ,height;
	
	private JFrame frame;
	private Canvas canvas;
	private Thread thread;
	private boolean running = false;
	
	private BufferStrategy bs;
	private Graphics g;
	
	private Key key;
	public static Tank tank;
	public static TankMP tankMP;
	public static ArrayList<TankMP> tankMPs;
	
	public static Level level;
	public static GameServer server;
	public static GameClient client;
	public static String username ="";
	
	public Game(int w, int h){
		width=w;
		height=h;
		frame = new JFrame("Tank Trouble");
		canvas = new Canvas();
		
//		try{
//			width = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter width of window: "));
//			height = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter width of window: "));
//		}catch(NumberFormatException e){
//			width = w;
//			height = h;
//			JOptionPane.showMessageDialog(null, "Invalid dimensions! Setting frame to default dimension: " + w + "x" + h);
//		}
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		
		canvas.setPreferredSize(new Dimension(width,height));
		canvas.setMaximumSize(new Dimension(width,height));
		canvas.setMinimumSize(new Dimension(width,height));
		canvas.setFocusable(false);
		
		key = new Key();
		frame.addKeyListener(key);
		frame.add(canvas);
		frame.pack();
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		        if(client !=null){
		        	String toSend = "-1 " + GameClient.playerNumAssigned + " ";
		        	client.sendData(toSend.getBytes(), GameClient.ipAddress, GameServer.port);
		        }
		    }
		});
		
		
	}
	
	public void initServer(){
		server = new GameServer();
		server.start();
	}
	
	public void initClient(String ip){
		client = new GameClient(ip);
		client.start();
		String toSend = "00 ";
		toSend += (int)tank.pos.x + " ";
		toSend += (int)tank.pos.y + " ";
		toSend += username + " ";
		
		client.sendData(toSend.getBytes(), GameClient.ipAddress, GameServer.port);
	}
	
	public void init(){
		level = new Level(15);
		tank = new Tank(new Vector(240,240),30,30,new Color(51, 204, 51), new Color(31, 122, 31));

		if(JOptionPane.showConfirmDialog(null,"Do You Want To Run The Server?" ,"Query",1)==0){
			initServer();
			username = JOptionPane.showInputDialog(null,"Enter username: ");
			
		}else{
			
			if(JOptionPane.showConfirmDialog(null, "Do You Want To Join Server","Query", 1)==0){
				String addres = JOptionPane.showInputDialog(null, "Enter IPAddress: ");
				username = JOptionPane.showInputDialog(null,"Enter username: ");
				initClient(addres);
			}
		}	
		
		
		tank.assignColor();
		if(tankMP!=null)
			tankMP.assignColor();
		
		tankMPs = new ArrayList<TankMP>();
	}
	
	public void start(){
		if(!running){
			thread = new Thread(this);
			thread.start();
			running = true;
		}
	}
	
	@Override
	public void run(){
		init();
		while(running){
			tick();
			render();
			try {
				Thread.sleep(1000/120);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void tick(){
		key.tick();
//		if(tankMP!=null){
//			tankMP.tick();
//		}
		for(int i=0;i<tankMPs.size();i++){
			tankMPs.get(i).tick();
		}
		tank.tick();
	}
	
	public void render(){
		bs = canvas.getBufferStrategy();
		if(bs==null){
			canvas.createBufferStrategy(3);
			return;
		}
		g=bs.getDrawGraphics();
		
		g.clearRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		Graphics2D g2  = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);	
		//render begins
		level.render(g);
		tank.render(g);
//		if(tankMP!=null){
//			tankMP.render(g);
//		}
		for(int i=0;i<tankMPs.size();i++){
			tankMPs.get(i).render(g);
		}
		
		tank.renderHealth(g);
		for(int i=0 ; i<tankMPs.size();i++){
			tankMPs.get(i).renderHealth(g);
		}
		g.setColor(Color.orange.darker());
		g.setFont(new Font("sans-serif" , Font.BOLD,12));
		
		if(server!=null && server.inGame){
			g.drawString("Press L to return to lobby", Game.width - 150, Game.height - 20);
		}else if(server !=null && server.inLobby){
			g.drawString("Press G to start game", Game.width - 150, Game.height - 20);
		}
		//render end
		bs.show();
		g.dispose();
	}
	
	public void stop(){
		if(running){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
