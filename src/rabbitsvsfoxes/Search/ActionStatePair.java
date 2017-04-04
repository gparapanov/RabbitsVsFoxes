package rabbitsvsfoxes.Search;

/**
 * When a node is expanded, the successor function generates a list of action-state pairs.
 * Each pair represents the action which can be applied to the current state, and the new state after the application.
 * This class models such a action-state pair.
 * 
 * @author Kit-ying Hui
 *
 */
public class ActionStatePair
{
/**
 * The action part of this pair.
 */
	public Action action;
/**
 * The state after the action is applied to the current state.
 */
public State state;

/**
 * Creates an action-state object.
 * 
 * @param action The action component of the pair.
 * @param state The state component of the pair.
 */
public ActionStatePair(Action action,State state)
{
this.action=action;
this.state=state;
} //end method

/**
 * This convenient method converts an action-state pair into a String (for printing?).
 */
public String toString()
{
return action.toString()+"\n"+state.toString();
} //end method
} //end class
