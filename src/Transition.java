class Transition {
	private State toState;
	private State fromState;
	private char readChar;
	private char writeChar;
	private Direction moveChar;
	private enum Direction{
		LEFT, RIGHT, STAY
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
	
	public Direction getMoveChar(){
		return moveChar;
	}
	
	public void setMoveChar(Direction moveChar){
		this.moveChar = moveChar;
	}
}
