package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class CatchRabbit extends Goal {

    public CatchRabbit(EnvironmentObject goalObject) {
        this.setGoalObject(goalObject);
        this.setPriority(5);
    }
    
}
