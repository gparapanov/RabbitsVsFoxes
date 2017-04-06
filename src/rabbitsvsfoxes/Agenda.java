package rabbitsvsfoxes;

import rabbitsvsfoxes.Goals.Goal;
import java.util.ArrayList;
import rabbitsvsfoxes.Agent.Agent;
import rabbitsvsfoxes.Goals.EatCarrot;

/**
 *
 * @author Georgi
 */
public class Agenda {

    private ArrayList<Goal> tasks;
    private Agent ownerAgent;

    public Agenda(Agent ownerAgent) {
        this.tasks = new ArrayList<>();
        this.ownerAgent=ownerAgent;
    }

    public void addTask(Goal g) {
        int posToAdd = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            if (g.getPriority() > tasks.get(i).getPriority()) {
                posToAdd = i;
                break;
            }
        }
        if (g.getGoalObject() != null && g instanceof EatCarrot) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i) instanceof EatCarrot &&
                        Agent.manhattanDistance(ownerAgent, tasks.get(i).getGoalObject())
                        >=
                        Agent.manhattanDistance(ownerAgent, g.getGoalObject())) {
                    posToAdd = i;
                    break;
                }
            }
        }
        tasks.add(posToAdd, g);
    }

    public void addTask(int pos, Goal g) {
        tasks.add(pos, g);
    }

    public void removeTask(Goal g) {
        tasks.remove(g);
    }

    public void removeTop() {
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

    @Override
    public String toString() {
        String outputString = "";
        for (Goal g : tasks) {
            outputString += g.getClass().getSimpleName() +" x:"+g.getGoalObject().getX()+
                    " y:"+g.getGoalObject().getY()+ " Priority: " + g.getPriority() + "<br>"
                    ;
        }
        return outputString;
    }

}
