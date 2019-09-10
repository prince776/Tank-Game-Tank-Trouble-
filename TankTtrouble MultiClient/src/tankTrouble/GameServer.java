package tankTrouble;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class GameServer extends Thread{
	//TODO : exchange level data
	public DatagramSocket socket;
	public static int port = 1931;
	private byte[] data;
	public static InetAddress clientIP;
	public static int clientPort;
	
	public static int numConnected =0;
	public static int maxClients = 4;
	public static boolean inGame = false , inLobby = true;
	
	public static boolean beginGame = false , stopGame = false;
	
	public GameServer(){
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("SERVER INITIATION FAILED!");
			e.printStackTrace();
		}
		data = new byte[256];
	}
	
	public void run(){
		while(true){
			
//			if(numConnected == maxClients){
//				beginGame = true;
//				
//			}
			
			if(Key.L){
				stopGame = true;
				beginGame = false;
			}if(Key.G){
				beginGame = true;
				stopGame = false;
			}
			
			DatagramPacket packet = new DatagramPacket(data,data.length);//Constructs a DatagramPacket for receiving packets of length data.length.
			try {
				socket.receive(packet); 
			} catch (IOException e) {
				e.printStackTrace();
			}		
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	public void parsePacket(byte[] data , InetAddress ipAddress , int port){
		clientIP = ipAddress;
		clientPort=port;
		String message = new String(data).trim();
		String[] tokens = message.split("\\s+");
		String id = tokens[0];
		
		//SOME CLIP WENT MISSING, I STARTED ADDING MULTIPLAYER..
		if(id.equalsIgnoreCase("00")){
			int x = toInt(tokens[1]);
			int y = toInt(tokens[2]);
			String name = tokens[3];
			numConnected++;
			
//			Game.tankMP = new TankMP(new Vector(x,y), 30, 30, new Color(255, 0, 0), new Color(204, 0, 0),numConnected+1 , ipAddress , port);
			
			Game.tankMPs.add(new TankMP(new Vector(x,y), 30, 30, new Color(255, 0, 0), new Color(204, 0, 0),numConnected+1 , ipAddress , port,name));
			
			
			
			
			//SEND NEW CLIENT'S INFO TO OTHER CLIENTS
			for(int i=0;i<Game.tankMPs.size();i++){
				TankMP t = Game.tankMPs.get(i);
				if(t.ip.equals(ipAddress) && t.port == port){
					continue;
				}
				String toSend = "00 ";
				toSend += x + " ";
				toSend += y + " ";
				toSend += numConnected + 1;
				toSend += " ";
				toSend += t.playerNum + " ";
				toSend += name + " ";
				sendData(toSend.getBytes(), t.ip, t.port);
			}			
			//SEND IT'S OWN INFORMATION
			String toSend = "00 ";
			toSend += (int)Game.tank.pos.x + " ";
			toSend += (int)Game.tank.pos.y + " " ;
			toSend += 1 + " ";
			toSend += numConnected + 1;
			toSend += " ";
			toSend += Game.username + " ";
			sendData(toSend.getBytes(), ipAddress, port);
			
			//SEND INFO OF ALL CLIENTS TO NEW CLIENT
			for(int i=0;i<Game.tankMPs.size();i++){
				TankMP t = Game.tankMPs.get(i);
				if(t.ip.equals(ipAddress) && t.port == port){
					continue;
				}
				toSend = "00 ";
				toSend += (int)t.pos.x + " ";
				toSend += (int)t.pos.y + " ";
				toSend += (t.playerNum) + " ";
				toSend += numConnected + 1;
				toSend += " ";
				toSend += t.username + " ";
				sendData(toSend.getBytes(), ipAddress, port);
			}
			
		}else if(id.equalsIgnoreCase("01")){
			float x = toFloat(tokens[1]);
			float y = toFloat(tokens[2]);
			float rot = toFloat(tokens[3]);
			float health = toFloat(tokens[4]);

//			Game.tankMP.pos = new Vector(x,y);
//			Game.tankMP.rot =rot;
//			Game.tankMP.health = health;
//			
			/*for(int i=0;i<Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).ip.equals(ipAddress) && Game.tankMPs.get(i).port == port){
					Game.tankMPs.get(i).pos = new Vector(x,y);
					Game.tankMPs.get(i).rot =rot;
					Game.tankMPs.get(i).health = health;
				}
			}*/
			
			
			TankMP t = whoSent(ipAddress, port);
			if(t!=null){
				t.pos = new Vector(x,y);
				t.rot=rot;
				t.health = health;
			}
		}
		
		else if(id.equalsIgnoreCase("02")){
			float x = toFloat(tokens[1]);
			float y = toFloat(tokens[2]);
			float vx = toFloat(tokens[3]);
			float vy = toFloat(tokens[4]);			
			
			Game.tank.bullets.add(new Bullet(new Vector(x,y), new Vector(vx,vy)));
			
			String toSend = "02 ";
			toSend += x + " ";
			toSend += y + " ";
			toSend += vx + " ";
			toSend += vy + " ";
			
			for(int i=0;i<Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).ip.equals(ipAddress) && Game.tankMPs.get(i).port==port)
					continue;
				sendData(toSend.getBytes(), Game.tankMPs.get(i).ip, Game.tankMPs.get(i).port);
			}
			
		}else if(id.equalsIgnoreCase("-1")){
			numConnected--;
			int pNo = toInt(tokens[1]);
			for(int i=0;i<Game.tankMPs.size();i++){
				if(Game.tankMPs.get(i).playerNum == pNo){
					
					sendDataToAllClientsExceptOne(("-1 " + pNo + " ").getBytes(), Game.tankMPs.get(i));
					Game.tankMPs.remove(i);
				//	Game.tankMPs.get(i).status = "DISCONNECTED";

					break;
				}
			}
			if(numConnected == 0){
				stopGame = true;
			}
		}
		
	}
	
	
	public void sendData(byte[] data,InetAddress ipAddress,int port){
		DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,port);
		try {
			if(ipAddress !=null)
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDataToAllClients(byte[] data){
		for(int i=0;i<Game.tankMPs.size();i++){
			DatagramPacket packet = new DatagramPacket(data,data.length,Game.tankMPs.get(i).ip,Game.tankMPs.get(i).port);
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void sendDataToAllClientsExceptOne(byte[] data,TankMP t){
		for(int i=0;i<Game.tankMPs.size();i++){
			if(Game.tankMPs.get(i).equals(t))
				continue;
			DatagramPacket packet = new DatagramPacket(data,data.length,Game.tankMPs.get(i).ip,Game.tankMPs.get(i).port);
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
