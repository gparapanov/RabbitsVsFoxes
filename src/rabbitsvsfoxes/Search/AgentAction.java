package rabbitsvsfoxes.Search;

import rabbitsvsfoxes.Direction;



/**
 * This class models an action of the ant.
 *
 * @author Kit-ying Hui
 *
 */
public class AgentAction extends Action {

    /**
     * The direction of movement.
     */
    public Direction movement;
    /**
     * The original X coordinate of the ant.
     */
    int fromX;
    /**
     * The original Y coordinate of the ant.
     */
    int fromY;

    /**
     * Create an AgentAction object from a given (x,y) and movement direction.
     *
     * @param fromX
     * @param fromY
     * @param movement
     */
    public AgentAction(int fromX, int fromY, Direction movement) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.movement = movement;
    } //end method

    /**
     * Return details of an AgentAction as a String.
     */
    @Override
    public String toString() {
        String result = "";
        int toX = 0, toY = 0;

        switch (movement) {
            case UP:
                result += "Move north";
                toX = fromX;
                toY = fromY - 1;
                break;
            /**
             * *** Complete the method here!!! *** Base on the direction of
             * movement, *** compute the new (x,y) position. *** Append the
             * correct string to result before we add the "from (...) to (...)"
             * string below.
             */
            case DOWN:
                result += "Move south";
                toX = fromX;
                toY = fromY + 1;
                break;
            case RIGHT:
                result += "Move east";
                toX = fromX + 1;
                toY = fromY;
                break;
            case LEFT:
                result += "Move west";
                toX = fromX - 1;
                toY = fromY;
                break;
            default:
                return "Unknown!";
                
        }
        return result += " from (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")";	//return result as a String
    } //end method
} //end class
