import java.util.*;

class State {
	private int number;
	private boolean start;
	private boolean accept;
	private ArrayList<Transition> paths;
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
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
