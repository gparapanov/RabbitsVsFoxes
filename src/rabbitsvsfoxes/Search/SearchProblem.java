package rabbitsvsfoxes.Search;

import java.util.*;

/**
 * This class defines the default behaviour of a search problem. Any class
 * modelling a domain-specific search problem must extend this class.
 *
 * @author Kit-ying Hui
 *
 */
public abstract class SearchProblem {

    /**
     * This attribute counts the number of nodes visited. This includes the root
     * node and other generated nodes. This value is set to 0 when the
     * {@link SearchProblem} is created. Invoking the
     * {@link SearchProblem#search() search()} method does not reset the value.
     * You can manually reset it if you want.
     */
    public int nodeVisited;

    /**
     * The initial state of the problem.
     */
    protected State startState;	//the initial state
//protected Set<State> visitedState;	//keeping the history of visited state, not node!

    /**
     * Create a {@link SearchProblem} instance with an initial state.
     *
     * @param start The initial state of the search problem.
     */
    public SearchProblem(State start) {
        this.startState = start;
        this.nodeVisited = 0;
    } //end method

    /**
     * Perform a search. The current implementation uses breadth-first search.
     *
     * @return null if no solution is found. Or a {@link Path} object otherwise.
     */
    public Path search() {
        Set<State> visitedState = new HashSet<State>();		//the set of visited states
        List<Node> fringe = new LinkedList<Node>();	//the list of fringe nodes

        fringe.add(new Node(this.startState, null, null, 0.0, 0));	//add initial state into fringe
        this.nodeVisited++;
        while (true) {
            if (fringe.isEmpty()) //no more node to expand
            {
                return null;			//no solution
            }
            Node node = fringe.remove(0);		//remove and take 1st node
            if (this.isGoal(node.state)) //if goal is found
            {
                return constructPath(node);	//construct path and return path
            }
            if (!visitedState.contains(node.state)) //if state of node has not been expanded before
            {
                List<ActionStatePair> childrenNodes = node.state.successor();	//otherwise expand current node
                visitedState.add(node.state);								//add expanded node into history
                addChildrenNodes(fringe, node, childrenNodes);				//add children nodes into fringe
            }
        }
    } //end method

    /**
     * Given a list of action-state pairs, add them as children of the current
     * node.
     *
     * @param fringe The fringe of the tree to explore.
     * @param parentNode The parent node of these children.
     * @param children A list of children as action-state pairs.
     */
    protected void addChildrenNodes(List<Node> fringe, Node parentNode, List<ActionStatePair> children) {
        Object[] childrenArray = children.toArray();

        for (int i = 0; i < childrenArray.length; i++) {
            ActionStatePair actionState = (ActionStatePair) childrenArray[i];
            Action action = actionState.action;							//get the action component
            State childState = actionState.state;							//get the state component
            double newPathCost = parentNode.cost + action.cost;				//assume cost is 1 unit per action
            int newDepth = parentNode.depth + 1;							//increment depth by 1
            Node childNode = new Node(childState, parentNode, action, newPathCost, newDepth);	//create new child node
            this.addChild(fringe, childNode);							//add child node to end of fringe
            this.nodeVisited++;
        }
    } //end method

    /**
     * Adding a child {@link Node} into the fringe list. Note that it is a
     * {@link Node}, not a {@link State} that we are adding into the fringe. The
     * first node in the fringe list is the next one to be expanded. The default
     * strategy is adding the child to the end of the fringe, giving a
     * breadth-first behaviour. By overriding this method in a subclass, you can
     * change how the search tree is expanded.
     *
     * In a more sophisticated version, you can sort the fringe list according
     * to a criteria. For example, if you insert the child right before a
     * {@link Node} whose cost is greater than the child, the fringe will be
     * sorted in ascending order of the cost.
     *
     * @param fringe The list of {@link Node} awaiting to be expanded.
     * @param childNode The child {@link Node} to be added to the fringe list.
     */
    protected void addChild(List<Node> fringe, Node childNode) {
        fringe.add(childNode);
    } //end method

    /**
     * Given a goal Node, construct the Path from the root to this goal by
     * tracing the parentNode pointer/reference in each Node.
     *
     * @param node A Node from which to trace to the root.
     * @return A Path leading from the root to this Node.
     */
    protected Path constructPath(Node node) {
        if (node == null) {
            return null;
        }

        Path result = new Path();
        result.cost = node.cost;
        while (node.parent != null) {
            result.add(0, new ActionStatePair(node.action, node.state));	//add state to the beginning of list
            node = node.parent;
        }
        result.head = node.state;	//now node is the head of the path

        return result;
    } //end method

    /**
     * Test if a state is goal.
     *
     * @param state The goal to test.
     * @return <code>true</code> if <code>state</code> is a goal.
     * <code>false</code> otherwise.
     */
    public abstract boolean isGoal(State state);
} //end interface
