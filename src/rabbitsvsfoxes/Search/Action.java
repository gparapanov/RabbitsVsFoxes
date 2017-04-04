package rabbitsvsfoxes.Search;

/**
 * This abstract class defines all methods that an action (which changes state) must have.
 * Originally I modelled an Action as an interface.
 * I have changed it into an abstract class because all Action should have a cost.
 * Thus I defined a {@link Action#cost} attribute in this abstract class, which
 * has a default value of 1.0 unless you override it in any sub-class.
 * 
 * @author Kit-ying Hui
 */
public abstract class Action
{
/**
 * The cost of the action.
 * This is default to 1.0 for problem that does not care about the action/path cost.
 * If different actions have different costs in your domain, you must assign a proper value to this attribute
 * instead of using the default 1.0.
 * However, DO NOT re-declare this attribute in your subclass.
 * If you do, there will be troubles.
 */
public double cost;

/**
 * The default constructor that gives a cost of 1.0 to an {@link Action}.
 */
public Action()
{
this.cost=1.0;	
} //end method

/**
 * Converting the action into a printable string.
 * This method should be tailored in a sub-class to customise the way an {@link Action} is printed.
 * 
 * @return A String representation of the action, which can be used for printing.
 */
public abstract String toString();
}
