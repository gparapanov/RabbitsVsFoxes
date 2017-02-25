package rabbitsvsfoxes;

import rabbitsvsfoxes.Goals.Goal;
import java.util.ArrayList;

/**
 *
 * @author Georgi
 */
public class Agenda {
    
    private ArrayList<Goal> tasks;
    
    public Agenda() {
        this.tasks = new ArrayList<>();
    }
    
    public void addTask(Goal g) {
        int posToAdd = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            if (g.getPriority() > tasks.get(i).getPriority()) {
                posToAdd = i;
                break;
            }
        }
        tasks.add(posToAdd, g);
    }
    
    public void removeTask(Goal g) {
        tasks.remove(g);
    }
    
    public void removeTop(){
        tasks.remove(0);
    }
    
    public Goal getTop() {
        if (!tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }
    
    public ArrayList<Goal> getTasks() {
        return this.tasks;
    }
    
    public boolean checkExistists(Goal goal) {
        for (Goal g : tasks) {
            if (g.getGoalObject() != null  
                    && g.getGoalObject().getX() == goal.getGoalObject().getX()
                    && g.getGoalObject().getY() == goal.getGoalObject().getY()) {
                return true;
            }
        }
        return false;
    }
}
