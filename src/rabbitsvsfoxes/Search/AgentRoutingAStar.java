package rabbitsvsfoxes.Search;


public class AgentRoutingAStar extends BestFirstSearchProblem {

    public AgentState goal;	//the goal state
    public char [][] world;	//the agent world

    /**
     * Create an AntRoutingAStarDiagonal object.
     *
     * @param start	The initial state.
     * @param goal	The goal state.
     * @param world	The ant world.
     */
    public AgentRoutingAStar(State start, State goal, char [][] world) {
        super(start, goal);
        this.world = world;
    } //end method

    /**
     * This method overrides the evaluation method defined in the
     * cm3038.search.informed.BestFirstSearchProblem class. It return the f(n)
     * value of a node n.
     *
     * @param node	The node to be evaluated.
     */
    @Override
    public double evaluation(Node node) {
        return node.cost + this.heuristic(node.state);	//f(n)=g(n)+h(n)
    } //end method

    /**
     * The heuristic function that estimates the distance of a state/node to a
     * goal state. For A* to work, this heuristic cannot over-estimate.
     *
     * @param state The state to be estimated.
     * @return The estimated distance of state to a goal.
     */
    public double heuristic(State state) {
        AgentState antState = (AgentState) state;	//cast State into AntState
        AgentState goalAntState = (AgentState) this.goalState;

        int xDiff = Math.abs(antState.x - goalAntState.x);
        int yDiff = Math.abs(antState.y - goalAntState.y);
       
        return xDiff+yDiff;	
    } //end method

    /**
     * The method checks if a state is a goal.
     */
    @Override
    public boolean isGoal(State state) {
        return state.equals(this.goalState);
    } //end method
} //end class
