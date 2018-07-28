import javafx.scene.shape.Line;

class Transition {
	private Line line;
	private State toState;
	private State fromState;
	private char readChar;
	private char writeChar;
	private Direction moveDirection;
	public enum Direction{
		LEFT, RIGHT, STAY
	}
	
	public Line getLine(){
		return line;
	}
	
	public void setLine(Line line){
		this.line = line;
	}
	
	public State getToState() {
		return toState;
	}
	
	public void setToState(State toState) {
		this.toState = toState;
	}
	
	public State getFromState() {
		return fromState;
	}
	
	public void setFromState(State fromState) {
		this.fromState = fromState;
	}
	
	public char getReadChar() {
		return readChar;
	}
	
	public void setReadChar(char readChar) {
		this.readChar = readChar;
	}
	
	public char getWriteChar() {
		return writeChar;
	}
	
	public void setWriteChar(char writeChar) {
		this.writeChar = writeChar;
	}
	
	public Direction getMoveDirection(){
		return moveDirection;
	}
	
	public void setMoveDirection(Direction moveDirection){
		this.moveDirection = moveDirection;
	}
}
