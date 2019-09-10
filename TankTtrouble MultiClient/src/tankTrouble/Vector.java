package tankTrouble;


public class Vector {
	
	public float x,y;
	
	public Vector(){
		this.x=0;
		this.y=0;
	}
	
	public Vector(float x,float y){
		this.x=x;
		this.y=y;
	}
	
	public void randomize(){
		this.x = (float)Math.random()*2-1;
		this.y = (float)Math.random()*2-1;
	}
	
	public void add(Vector vector){
		this.x+=vector.x;
		this.y+=vector.y;
	}
	
	public void subtract(Vector vector){
		this.x-=vector.x;
		this.y-=vector.y;
	}
	
	public void multiply(float scale){
		this.x*=scale;
		this.y*=scale;
	}
	
	public float dot(Vector v){
		return (this.x*v.x+this.y*v.y);
	}
	
	public float getMagnitude(){
		return 	 (float) Math.sqrt(x*x + y*y);
		
	}
	
	public void normalize(){
		float mag =getMagnitude();
		x/=mag;
		y/=mag;
	}
	
	public void setMagnitude(float x){
		normalize();
		multiply(x);
	}
	
	public void limit(float x){
		if(getMagnitude()>x){
			setMagnitude(x);
		}
	}
	
	//static functions
	
	public static Vector add(Vector v1,Vector v2){
		Vector v = new Vector();
		v.x=v1.x+v2.x;
		v.y=v1.y+v2.y;
		return v;
	}
	
	public static Vector subtract(Vector v1,Vector v2){
		Vector v = new Vector();
		v.x=v1.x-v2.x;
		v.y=v1.y-v2.y;
		return v;
	}
	
	public static Vector multiply(Vector v , float scale){
		Vector r=new Vector();
		r.x=v.x*scale;
		r.y=v.y*scale;
		return r;
	}
	
	public static float dot(Vector v1,Vector v2){
		return (v1.x*v2.x+v1.y*v2.y);
	}
	
	public static float getMagnitude(Vector v){
		return 	 (float) Math.sqrt(v.x*v.x + v.y*v.y);
		
	}
	
	public static Vector normalize(Vector v){
		Vector r  = new Vector();
		float mag =getMagnitude(v);
		r.x=v.x/mag;
		r.y=v.y/mag;
		return r;
	}
	
}
