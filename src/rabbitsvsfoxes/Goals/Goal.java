package rabbitsvsfoxes.Goals;

import rabbitsvsfoxes.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class Goal {
    private EnvironmentObject goalObject;
    private boolean completed;
    private int priority;//1<<<<<10  5 is default

    public Goal() {
        this.priority=5;
        this.completed=false;
    }
    
    public Goal(EnvironmentObject eo) {
        this.priority=5;
        this.completed=false;
        this.setGoalObject(eo);
    }

    public EnvironmentObject getGoalObject() {
        return goalObject;
    }

    public void setGoalObject(EnvironmentObject goalObject) {
        this.goalObject = goalObject;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    
    
}
