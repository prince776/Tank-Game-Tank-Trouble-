package tankTrouble;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameClient extends Thread{
	
	public DatagramSocket socket;
	public static InetAddress ipAddress;
	private byte[] data;
	public static boolean inGame = false , inLobby = true,beginGame = false,stopGame = false;
	public static int numPlayers =0 , maxPlayers=0;
	public static int playerNumAssigned =0;
	
	public GameClient(String ipAdd){
		try {
			socket = new DatagramSocket();
			ipAddress = InetAddress.getByName(ipAdd);
		} catch (SocketException | UnknownHostException e) {
			System.err.println("SERVER CONNECTION FAILED!");
			e.printStackTrace();
		}
		data = new byte[256];
	}
	
	public void run(){
		while(true){
			
			DatagramPacket packet = new DatagramPacket(data,data.length);//Constructs a DatagramPacket for receiving packets of length data.length.
			try {
				socket.receive(packet); 
			} catch (IOException e) {
				e.printStackTrace();
			}		
			parsePacket(packet.getData(), packet.getAddress() ,  packet.getPort());
		}
	}
	
	public void parsePacket(byte[] data , InetAddress ipAddress , int port){
		String message = new String(data).trim();
		String[] tokens = message.split("\\s+");
		String id = tokens[0];
		
		if(id.equalsIgnoreCase("00")){
			int x = toInt(tokens[1]);
			int y = toInt(tokens[2]);
			int playerNum= toInt(tokens[3]);
			playerNumAssigned = toInt(tokens[4]);
			String name = tokens[5];
			Game.tankMPs.add(new TankMP(new Vector(x,y), 30, 30, new Color(255, 0, 0), new Color(204, 0, 0),playerNum,ipAddress,port,name));
			
		}else if(id.equalsIgnoreCase("01")){ // for only server's tank movement
			float x = toFloat(tokens[1]);
			float y = toFloat(tokens[2]);
			float rot = toFloat(tokens[3]);
			float health =toFloat(tokens[4]);

			
			for(int i=0;i<Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).playerNum==1){
					Game.tankMPs.get(i).pos = new Vector(x,y);
					Game.tankMPs.get(i).rot = rot;
					Game.tankMPs.get(i).health=health;
				}
			}
		}                 
		else if(id.equalsIgnoreCase("02")){
			float x = toFloat(tokens[1]);
			float y = toFloat(tokens[2]);
			float vx = toFloat(tokens[3]);
			float vy = toFloat(tokens[4]);			
			
			Game.tank.bullets.add(new Bullet(new Vector(x,y), new Vector(vx,vy)));
			
		}else if(id.equalsIgnoreCase("03")){
			numPlayers = toInt(tokens[1]);
			maxPlayers = toInt(tokens[2]);
			inLobby = true;
			inGame = false;
			
		}else if(id.equalsIgnoreCase("04")){
			beginGame = true;
			
		}else if(id.equalsIgnoreCase("05")){
			float x = toFloat(tokens[1]);
			float y = toFloat(tokens[2]);
			float rot = toFloat(tokens[3]);
			float health =toFloat(tokens[4]);
			int playerNum = toInt(tokens[5]);
			for(int i=0;i<Game.tankMPs.size();i++){
				if(playerNum == Game.tankMPs.get(i).playerNum){
					Game.tankMPs.get(i).pos = new Vector(x,y);
					Game.tankMPs.get(i).rot = rot;
					Game.tankMPs.get(i).health=health;
				}
					
			}

		}else if(id.equalsIgnoreCase("06")){
			stopGame = true;
		}else if(id.equalsIgnoreCase("-1")){
			int pNo = toInt(tokens[1]);
			for(int i=0;i<Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).playerNum == pNo){
					Game.tankMPs.remove(i);
					//Game.tankMPs.get(i).status = "DISCONNECTED";
					break;
				}
			}
		}
		
		
	}
	
	public void sendData(byte[] data,InetAddress ipAddress,int port){
		DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int toInt(String x){
		try{
			return Integer.parseInt(x);
		}catch(NumberFormatException e){
			return 0;
		}
	}
	public float toFloat(String x){
		try{
			return Float.parseFloat(x);
		}catch(NumberFormatException e){
			return 0;
		}
	}
	
	public TankMP whoSent(InetAddress ipAddress ,int port){
		TankMP t = null;
		for(int i=0;i<Game.tankMPs.size();i++){
			if(Game.tankMPs.get(i).ip.equals(ipAddress) && Game.tankMPs.get(i).port ==port)
				t = Game.tankMPs.get(i);
		}
		return t;
	}
	
	
	
}
