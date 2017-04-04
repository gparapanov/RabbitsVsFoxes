package rabbitsvsfoxes.Search;

import java.util.*;
import rabbitsvsfoxes.Search.*;

/**
 * This is a specialised SearchProblem for informed searches. This class
 * contains most ingredients of an informed search problem. In most cases you
 * can simply extend this class to create your own specialised problem of
 * informed search. However, you still need to supply the evaluation function
 * (and heuristic if needed).
 *
 * @author kit
 *
 */
public abstract class BestFirstSearchProblem extends rabbitsvsfoxes.Search.SearchProblem {

    /**
     * Most informed searches need to compute the distance between the current
     * state and the goal. So I am storing the goal state into this attribute.
     * You can always refer to this attribute and check if you have reached the
     * goal state. Note that this attribute is only defined in the
     * {@link BestFirstSearchProblem} class but not in its {@link SearchProblem}
     * superclass.
     */
    public State goalState = null;

    /**
     * Construct a {@link BestFirstSearchProblem} object. Note that
     * BestFirstSearchProblem is an abstract class. So you will not directly
     * create a {@link BestFirstSearchProblem} object but from one of its
     * concrete subclasses. This constructor is expected to be invoked from a
     * concrete subclass.
     *
     * @param start	The initial state.
     * @param goal	The goal state.
     */
    public BestFirstSearchProblem(State start, State goal) {
        super(start);
        this.goalState = goal;
    } //end method

    /**
     * This is a specialised version of search tailored for informed search. It
     * takes care of repeating states. The initial and goal states should be
     * defined when the {@link BestFirstSearchProblem} object is created. This
     * method simply starts the search. For debugging purpose, it prints a
     * message for every 1000 nodes explored.
     *
     * @return The path to the goal. Or <code>null</code> if there is no
     * solution.
     */
    @Override
    public Path search() {
        Map<State, Node> visitedNodes = new HashMap<State, Node>();
        List<Node> fringe = new LinkedList<Node>();					//the list of fringe nodes

        Node rootNode = new Node(this.startState, null, null, 0.0, 0);	//create root node
        fringe.add(rootNode);										//add root node into fringe
        visitedNodes.put(rootNode.state, rootNode);					//seen root node and state
        this.nodeVisited++;											//increment node count
        if (nodeVisited % 1000 == 0) //print message every 1000 nodes
        {
            System.out.println("No. of nodes explored: " + nodeVisited);
        }

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
            Object[] childrenNodes = node.state.successor().toArray();
            for (int i = 0; i < childrenNodes.length; i++) {
                this.nodeVisited++;										//increment node count
                if (nodeVisited % 1000 == 0) //print message every 1000 nodes
                {
                    System.out.println("No. of nodes explored: " + nodeVisited);
                }
                ActionStatePair child = (ActionStatePair) childrenNodes[i];
                Action action = child.action;
                State nextState = child.state;
                Node lastSeenNode = (Node) visitedNodes.get(nextState);

                if (lastSeenNode == null) //have not seen this state before
                {
                    Node childNode = new Node(nextState, node, action, node.cost + action.cost, node.depth + 1);
                    addChild(fringe, childNode);
                    visitedNodes.put(nextState, childNode);
                } else {
                    if (lastSeenNode.cost > action.cost + node.cost) //going this new path is cheaper
                    {
                        lastSeenNode.parent = node;	//to reach this next state, you should go through the current node
                        lastSeenNode.cost = action.cost + node.cost;	//update cost of last seen node
                        lastSeenNode.depth = node.depth + 1;			//update depth of last seen node
                        lastSeenNode.action = action;					//update the action too
                    }
                }
            } //end for
        } //end while
    } //end method

    /**
     * Adding a child node into the fringe list. We sort the fringe by the
     * evaluation function values of the nodes. This method can be customised to
     * change the order of expanding children nodes. In best-first search, this
     * the child with the lowest f(n) score is placed at the head of the queue.
     *
     * @param fringe	A list of {@link Node}.
     * @param childNode	The child {@link Node} to add into the fringe.
     */
    @Override
    protected void addChild(List<Node> fringe, Node childNode) {
        for (int i = 0; i < fringe.size(); i++) //scan fringe from beginning to end
        {
            if (evaluation(childNode) < evaluation((Node) fringe.get(i))) //find position where node is just bigger than child in evaluation function value
            {
                fringe.add(i, childNode);	//add child just before that node
                return;						//exit, no need to continue
            }
        }
        fringe.add(childNode);		//if you hit end of list, add child to the end
    } //end method

    /**
     * The evaluation function is to be supplied. You must supply this in a
     * concrete subclass. Note that the {@link BestFirstSearchProblem} class
     * only requires the evaluation function (i.e. f(n)). If you are
     * implementing A* search, your evaluation function may need a heuristic
     * function too (i.e. h(n)), which you also have to supply. A pure
     * best-first search does not care about the existence of an h(n).
     *
     * @param node	The node to consider.
     * @return The score of the node. The lower the score is, the better the
     * node.
     */
    public abstract double evaluation(Node node);
} //end class
