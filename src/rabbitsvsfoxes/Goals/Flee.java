package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class Flee extends Goal {

    public Flee(EnvironmentObject eo) {
        this.setPriority(10);//max priority
        this.setGoalObject(eo);
    }
    
}
