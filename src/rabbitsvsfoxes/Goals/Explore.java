package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Goals.Goal;

/**
 *
 * @author Georgi
 */
public class Explore extends Goal {
    public Explore(EnvironmentObject goalObject) {
        this.setGoalObject(goalObject);
        this.setPriority(4);
    }
}
