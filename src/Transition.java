/*
 *     Simple Turing machine EMulator (STEM)
 *     Copyright (C) 2018  Sam MacLean,  Joel Kovalcson, Dakota Sanders, Matt Matto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

class Transition {
    private Path path;
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

	public void setPath(Path p){
		this.path = p;
	}

	public Path getPath(){
		return this.path;
	}

	public boolean compareTo(Transition t){
		return (t.fromState == fromState)
				&&(t.toState == toState)
				&&(t.readChar == readChar)
				&&(t.writeChar == writeChar)
				&&(t.moveDirection == moveDirection);
	}
}
