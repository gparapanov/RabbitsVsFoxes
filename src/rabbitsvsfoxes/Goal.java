package rabbitsvsfoxes;

/**
 *
 * @author Georgi
 */
public class Goal {
    private EnvironmentObject goalObject;
    private int priority;//1<<<<<10

    public Goal() {
        
    }

    public EnvironmentObject getGoalObject() {
        return goalObject;
    }

    public void setGoalObject(EnvironmentObject goalObject) {
        this.goalObject = goalObject;
    }
    
    
    
}
