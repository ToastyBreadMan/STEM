import java.util.*;

class State {
	private String name;
	private double x;
	private double y;
	private boolean start;
	private boolean accept;
	private ArrayList<Transition> paths;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getX(){
		return x;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public boolean isStart() {
		return start;
	}
	
	public void setStart(boolean start) {
		this.start = start;
	}
	
	public boolean isAccept() {
		return accept;
	}
	
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	
	public ArrayList<Transition> getPaths() {
		return paths;
	}
	
	public void setPaths(ArrayList<Transition> paths) {
		this.paths = paths;
	}
}
