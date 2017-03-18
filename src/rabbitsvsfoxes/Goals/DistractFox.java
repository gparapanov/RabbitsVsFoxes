package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class DistractFox extends Goal{
    
    public DistractFox(EnvironmentObject goalObject) {
        this.setGoalObject(goalObject);
        this.setPriority(9);
    }
    
}
