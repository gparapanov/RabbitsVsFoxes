package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class Flee extends Goal {

    public Flee(EnvironmentObject eo) {
        this.setPriority(8);//max priority
        this.setGoalObject(eo);
    }
    
}
