package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Goals.Goal;

/**
 *
 * @author Georgi
 */
public class Exploration extends Goal {
    public Exploration(EnvironmentObject goalObject) {
        this.setGoalObject(goalObject);
        this.setPriority(4);
    }
}
