package rabbitsvsfoxes.Search;

import java.util.*;

/**
 * This interface defines the methods required on a problem state.
 * Any class that represents a problem state must implement this interface.
 *
 * @author Kit-ying Hui
 */
public interface State {

	/**
	 * Converts the state into a {@link String}. This method controls how a state will be printed.
	 * @return A {@link String} representation of the state.
	 */
public String toString();

/**
 * Checks if two states are equal.
 * This is required as we need to check for repeated states in the history.
 * They may be 2 different {@link State} objects but if they represent the same state (i.e. same attribute values),
 * then they are equal.
 * If you get this wrong for your {@link State} class, the search may fall into an infinite loop
 * (as it cannot tell if a state is being repeated).
 * @param state The other state to compare with.
 * @return true if the current state and the other are the same (value-wise). false otherwise.
 */
public boolean equals(Object state);

/**
 * Calculate the hash code of a state.
 * This method is needed because internally we are using a {@link HashSet} to store the history (of states),
 * and the {@link java.util.Set#contains(Object) Set.contains(Object)} method of the
 * {@link java.util.HashSet} to check if a state is in the history.
 * For this to work correctly in Java, the object (i.e. State) to be stored into the
 * {@link java.util.HashSet}
 * must have a <code>int hashCode()</code> method defined.
 * Objects which are equal (e.g. all attribute values are the same) must have the same hash code.
 * The hash code is usually calculated based on the attribute values of the object.
 * @return The hash code of the State object.
 */
public int hashCode();

/**
 * This is the successor function which generates all action-states of a state when it is expanded.
 * This method must be tailored for each domain.
 * The returned result is a {@link java.util.List} of {@link ActionStatePair} objects.
 * It is a list, not a set as we want to keep the order of the children nodes.
 * If use a {@link java.util.Set}, the order is not kept/guaranteed.
 * @return All possible action-state pairs as a {@link java.util.List} of {@link ActionStatePair} objects.
 */
public List<ActionStatePair> successor();
}
