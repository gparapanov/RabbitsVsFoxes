package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class EatCarrot extends Goal {

    public EatCarrot(EnvironmentObject goalObject) {
        this.setGoalObject(goalObject);
        this.setPriority(5);
    }
    
}
