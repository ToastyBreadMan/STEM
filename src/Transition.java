import javafx.scene.shape.Line;
import javafx.scene.text.Text;

class Transition {
	private Line line;

	private Text label;
	private State toState;
	private State fromState;
	private char readChar;
	private char writeChar;
	private Direction moveDirection;
	public enum Direction{
		LEFT, RIGHT, STAY
	}

	public Transition(){ }

	public Transition(State toState, State fromState, char readChar, char writeChar, Direction moveDirection){
		this.toState = toState;
		this.fromState = fromState;
		this.readChar = readChar;
		this.writeChar = writeChar;
		this.moveDirection = moveDirection;
	}

	public Transition(State toState, State fromState, char readChar, char writeChar, Direction moveDirection, Line line){
		this.toState = toState;
		this.fromState = fromState;
		this.readChar = readChar;
		this.writeChar = writeChar;
		this.moveDirection = moveDirection;
		this.line = line;
	}

	public Line getLine(){
		return line;
	}
	
	public void setLine(Line line){
		this.line = line;
	}

	public Text getLabel() {
		return label;
	}

	public void setLabel(Text label) {
		this.label = label;
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
