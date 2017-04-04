package rabbitsvsfoxes.Search;

import java.util.*;
import rabbitsvsfoxes.Agent.Agent;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Environment;


/**
 * This class models the state of an ant in a 2D world.
 *
 * @author kit
 *
 */
public class AgentState implements State {
    
    public Agent agentObject;

    /**
     * The X coordinate of the ant. This is also called the column. Note that X
     * must be between 0 and width of world -1.
     */
    public int x;

    /**
     * The Y coordinate of the ant. This is also called the row. Note that Y
     * must be between 0 and height of world -1.
     */
    public int y;

    /**
     * A reference to the AntWorld object. Strictly speaking this is not needed
     * to model the ant. But when we find the actions, we need to know if there
     * is any barrier, and the action-finding method only has the AntState as
     * input. So it is handy to have a reference to the AntWorld in an AntState.
     * Note that all AntWorld objects created will refer to the one-and-only-one
     * AntWorld object. There is no need to create multiple AntWorld objects.
     */
    public char[][] world;

    /**
     * Create a new AntWorld object with at position (x,y), with reference to an
     * AntWorld.
     *
     * @param agentObject
     * @param x The X coordinate of the ant.
     * @param y The Y coordinate of the ant.
     * @param world A reference to the AntWorld object.
     */
    public AgentState(Agent agentObject,int x, int y, char [][] world) {
        this.agentObject=agentObject;
        this.x = x;
        this.y = y;
        this.world = world;
    } //end method

    /**
     * Create a list of action-pair from the current AntState.
     *
     * @return A List<ActionStatePair> that contains all valid action and
     * next-state pairs.
     */
    @Override
    public List<ActionStatePair> successor() {
        List<ActionStatePair> result = new ArrayList<ActionStatePair>();

        /**
         * See if ant can move to the north. If yes, create and add the
         * action-state pair into the list of result.
         */
        //NORTH
        if (this.y > 0) //ant is not on the north border
        {
            if (this.world[y - 1][x]=='0' || 
                    (agentObject.getAgenda().getTop().getGoalObject().getX()==x &&
                    agentObject.getAgenda().getTop().getGoalObject().getY()==y-1)) //check to see if space in north is free
            {
                AgentAction action = new AgentAction(x, y, Direction.UP);					//create Action object
                AgentState nextState = this.applyAction(action);							//apply action to find next state
                ActionStatePair actionStatePair = new ActionStatePair(action, nextState);	//create action-state pair
                result.add(actionStatePair);											//add action-state pair into list
            }
        }
        
        //SOUTH
        if (this.y < this.world.length - 1) //ant is not on the south border
        {
            if (this.world[y + 1][x]=='0' || 
                    (agentObject.getAgenda().getTop().getGoalObject().getX()==x &&
                    agentObject.getAgenda().getTop().getGoalObject().getY()==y+1)) //check to see if space in south is free
            {
                AgentAction action = new AgentAction(x, y, Direction.DOWN);					//create Action object
                AgentState nextState = this.applyAction(action);						//apply action to find next state
                ActionStatePair actionStatePair = new ActionStatePair(action, nextState);	//create action-state pair
                result.add(actionStatePair);											//add action-state pair into list
            }
        }
        //WEST
        if (this.x > 0) //ant is not on the west border
        {
            if (this.world[y ][x-1]=='0' || 
                    (agentObject.getAgenda().getTop().getGoalObject().getX()==x-1 &&
                    agentObject.getAgenda().getTop().getGoalObject().getY()==y)) //check to see if space on west is free
            {
                AgentAction action = new AgentAction(x, y, Direction.LEFT);					//create Action object
                AgentState nextState = this.applyAction(action);						//apply action to find next state
                ActionStatePair actionStatePair = new ActionStatePair(action, nextState);	//create action-state pair
                result.add(actionStatePair);											//add action-state pair into list
            }
        }
        //EAST
        if (this.x < world[y].length - 1) //ant is not on the east border
        {
            if (this.world[y ][x+1]=='0' || 
                    (agentObject.getAgenda().getTop().getGoalObject().getX()==x+1 &&
                    agentObject.getAgenda().getTop().getGoalObject().getY()==y)) //check to see if space on east is free
            {
                AgentAction action = new AgentAction(x, y, Direction.RIGHT);					//create Action object
                AgentState nextState = this.applyAction(action);						//apply action to find next state
                ActionStatePair actionStatePair = new ActionStatePair(action, nextState);	//create action-state pair
                result.add(actionStatePair);											//add action-state pair into list
            }
        }
        return result;	//return the list of action-state pair
    } //end method

    /**
     * Compare if the current AntState equals to another state. We only compare
     * the x and y values. There is no need to compare the AntWorld.
     *
     * @return true if the 2 AntState objects are equal. false otherwise.
     */
    public boolean equals(Object state) {
        
        AgentState agentState = (AgentState) state;					//cast state into an AntState object
        return this.x == agentState.x && this.y == agentState.y;	//true if x and y are the same
    } //end method

    /**
     * Compute a hash code of the AntState. This is needed as we store our
     * AntState objects into a hash map. We take the simple formula of y*100+x.
     */
    public int hashCode() {
        return this.x + this.y * 100;
    } //end method

    /**
     * Apply an action to the current state, giving the next state.
     *
     * @param action The action to apply.
     * @return A next state for the ant.
     */
    public AgentState applyAction(AgentAction action) {
        int newX = 0, newY = 0;				//to hold new x and y after action is applied

        switch (action.movement) {
            case UP:
                newX = this.x;		//moving north, x remains unchange
                newY = this.y - 1;		//decrement y
                break;
            case DOWN:
                newX = this.x;
                newY = this.y + 1;
                break;
            case RIGHT:
                newX = this.x + 1;
                newY = this.y;
                break;
            case LEFT:
                newX = this.x - 1;
                newY = this.y;
                break;

            default:
                newX = this.x;
                newY = this.y;
        }
        AgentState result = new AgentState(this.agentObject,newX, newY, this.world);	//create next state from new x,y and ant world
        return result;	//return next state as result
    } //end method

//    public String toString() {
//        String result = "";
//
//        for (int y = 0; y < this.world.grid.length; y++) {
//            for (int x = 0; x < this.world.grid[y].length; x++) /**
//             * *** Complete the method here!!! ** If the space is occupied,
//             * append an "X" to result. ** If it is free, append a ".". *** If
//             * it ant is here (check the coordinates!), append an "O".
//             */
//            {
//                if (this.x == x && this.y == y) {
//                    result += "O";
//                } else if (this.world.grid[y][x]) {
//                    result += "X";
//                } else {
//                    result += ".";
//                }
//
//            }
//            result += "\n";//append a newline at the end of each row
//        }
//        return result;		//return result String
//    } //end method
} //end class
