package rabbitsvsfoxes.Search;

/**
 * This class defines a node in the search algorithm.
 * Remember that a node is a book-keeping structure.
 * Apart from the state of the problem it has other attributes.
 * e.g. The parent pointer points back to the parent node so that we can trace the path when a goal is found.
 * The cost attribute is needed if we want to optimise on the cost instead of just depth of the solution.
 * The action attribute keeps track of the action which brings us to this state/node.
 * The depth attribute tells us the level/depth of this node in the search tree.
 * This is helpful if we want to limit the depth of the search (e.g. in a depth-limited search).
 */

public class Node
{
/**
 * This attribute stores/points to the state of the problem this node represents.
 */
	public State state;
	/**
	 * This point to the parent node. We need this to trace back the whole path (from to goal) when we find a goal.
	 */
public Node parent;	//pointing to parent node of path

/**
 * This keeps track of the cost from the root to this node. 
 */
public double cost;	//cost of node

/**
 * This is the action that brings us from the parent to this node.
 * In the root node of the tree, this attribute must be null as there is no parent node.
 */
public Action action;	//the action that takes us here

/**
 * The depth of this node in the search tree.
 */
public int depth;

/**
 * Creates a {@link Node} object.
 * @param state The state of the problem this node represents.
 * @param parent The parent node leading to this node.
 * @param action The action that leads us from the parent to this node.
 * @param cost The action from the root node to this node.
 * @param depth The depth of this node in the search tree.
 */
public Node(State state,Node parent,Action action,double cost,int depth)
{
this.state=state;
this.parent=parent;
this.action=action;
this.cost=cost;
this.depth=depth;
} //end method
} //end class
