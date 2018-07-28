import java.util.ArrayList;

class Machine {
	private State startState;
	private State acceptState;
	private ArrayList<State> states = new ArrayList<State>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private Tape tape;

	public State getStartState() {
		return startState;
	}
	
	public void setStartState(State startState) {
		this.startState = startState;
	}
	
	public State getAcceptState() {
		return acceptState;
	}
	
	public void setAcceptState(State acceptState) {
		this.acceptState = acceptState;
	}
	
	public ArrayList<State> getStates() {
		return states;
	}
	
	public void setStates(ArrayList<State> states) {
		this.states = states;
	}
	
	public void addState(State state){
		states.add(state);
	}
	
	public void deleteState(State state){
		states.remove(state);
	}
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	
	public void setTransitions(ArrayList<Transition> transitions) {
		this.transitions = transitions;
	}

	public Tape getTape() { return tape; }
}