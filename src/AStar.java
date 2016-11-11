import java.util.ArrayList;

public class AStar {

	class State {
		private State parent; // parent state
		private int id; // idx of point
		private int g; // cost from Start to this state
		private int h; // estimated cost to goal from this state
		private int f; // f = g + h
		private State next;

		// constructor of State class
		public State(State parent, int id, int g, int h, int f, State next) {
			this.parent = parent;
			this.id = id;
			this.g = g;
			this.h = h;
			this.f = f;
			this.next = next;
		}
	}

	State[] data;
	State open, closed, children; // Dummy head for open , closed, children
									// states
	State start, goal; // State object for start & goal state
	State current; // current State object in A* search
	boolean found = false; // true if solution of search is made

	// need to get arguments in array or something
	// SHOULD BE EDITED!!
	public AStar(State[] data) {
		this.data = data;
	}

	// do A* search, and return the solution path
	public String search() {
		insert_state(open, start);
		while (open.next != null) {
			// choose
			current = open.next;

			// goal test
			if (current.equals(goal)) {
				found = true;
				break;
			}

			// expand
			else {

			}
		}
		return null;

	}

	// insert a copy of State s into list, in sorted order
	private void insert_state(State list, State s) {
		State temp;
		while (list.next != null) {
			if (s.f < list.next.f)
				break;
			list = list.next;
		}
		temp = new State(s.parent, s.id, s.g, s.h, s.f, s.next); // copy State s
		temp.next = list.next;
		list.next = temp;
	}

	// remove State s in list
	private void remove_state(State list, State s) {
		while (list.next != null) {
			if (s.id == list.next.id)
				list.next = list.next.next;
		}
		list = list.next;
	}

	private void generate_children(State s) {
		State closedState;
		State childState;
		int i; // used in for loop
		boolean isClosed;
		for (i = 0; i < data.length; i++) { //generate children if the road is exist & it isn't closed state.
			isClosed = false;
		}
	}
}
