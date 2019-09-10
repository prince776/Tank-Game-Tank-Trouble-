package tankTrouble;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Key implements KeyListener{
	
	public static boolean[] keys = new boolean[312];
	public static boolean up,down,left,right,space , L , G;
	
	
	public static void tick(){
		up = keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		space = keys[KeyEvent.VK_SPACE];
		L = keys[KeyEvent.VK_L];
		G = keys[KeyEvent.VK_G];

	}


	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()]  = true;
//		System.out.println(e.getKeyChar());
	}


	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()]  = false;
		
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
