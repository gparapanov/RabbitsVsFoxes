package rabbitsvsfoxes.Search;

import java.util.*;

/**
 * This class models a path that traces the route from a root node to a node in
 * the tree. Basically it is a list of {@link ActionStatePair} objects plus a
 * head node (of {@link State}). Conceptually a path is a specialised list of
 * action-state pairs with a head node. That is why it is extending a concrete
 * class implementing the List interface. I chose {@link java.util.LinkedList}
 * in this case. It can be any class that implements {@link java.util.List}.
 *
 * You do not have much to do with this class. A {@link Path} is return as the
 * result when you do a search. If it is not <code>null</code>, you can simply
 * call the {@link Path#print() print()} method to print out the path.
 *
 * @author Kit-ying Hui
 *
 */
public class Path extends LinkedList<ActionStatePair> {

    /**
     * The root node of the path.
     */
    public State head;

    /**
     * The cost of the path from root to the final node/goal.
     */
    public double cost;

    /**
     * Creates an empty path with no head node and 0 cost.
     */
    public Path() {
        this.head = null;
        this.cost = 0.0;
    }

    /**
     * Prints the path, with each node and action. The output is controlled by
     * the {@link cm3038.search.State#toString() toString()} method of the
     * {@link State} objects and {@link Action} objects, which can be customised
     * in the domain specific sub-classes.
     */
    public void print() {
        if (head == null) {
            return;
        }
        System.out.println(head.toString() + "\n");
        Iterator<ActionStatePair> i = this.iterator();
        while (i.hasNext()) {
            ActionStatePair next = i.next();
            System.out.println(next.action.toString());
            System.out.println(next.state.toString());
            System.out.println();
        }
    } //end method
} //end class
