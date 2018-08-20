import java.util.ArrayList;

class Machine {
	private State startState;
	private ArrayList<State> states = new ArrayList<>();
	private ArrayList<Transition> transitions = new ArrayList<>();
	private ArrayList<Path> paths = new ArrayList<>();
	public Tape tape;

	Machine(){
		this.tape = new Tape();
	}

	Machine(ArrayList<State> states, ArrayList<Transition> transitions, State startState){
		this.states = states;
		this.transitions = transitions;
		this.startState = startState;
		this.tape = new Tape();
	}

	public State getStartState() {
		return startState;
	}
	
	public void setStartState(State startState) {
		this.startState = startState;
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

	public ArrayList<Path> getPaths() {
		return paths;
	}

	public String toString(){
		//System.out.println("I'm in toString");
		StringBuilder ret = new StringBuilder();
		ret.append(String.format("// Save File for JFLAP-ISH\n// Version %.2f\n\n", 1.0));
		ret.append("// State Format: name x y start accept\n");
		ret.append("STATES:\n");

		for (State s : states){
			ret.append(String.format("\t%s %f %f %s %s\n",
					s.getName(), s.getX(), s.getY(),
					Boolean.toString((startState == s)), Boolean.toString(s.isAccept())));
		}
		ret.append("\n");

		ret.append("// Transition format: fromStateId toStateId readCHar writeChar moveDirection\n");
		ret.append("// The Character '~' is the catchall character\n");
		ret.append("TRANSITION:\n");

		for (Transition t : transitions){
			ret.append(String.format("\t%d %d %c %c %s\n",
					Integer.parseInt(t.getFromState().getName()),
					Integer.parseInt(t.getToState().getName()),
					t.getReadChar(), t.getWriteChar(), t.getMoveDirection().toString()));
		}
		ret.append("\n");

		ret.append("// Tape format: tapeChar(0) tapeChar(1) ... tapeChar(n)\n");
		ret.append("TAPE:\n");

		ret.append(String.format("\t%d\n", tape.getTapeHead()));
		ret.append("\t");
		for (Character c : tape.getTapeAsArray()){
			ret.append(String.format("%c", c));
		}
		ret.append("\n");

		return ret.toString();
	}
}