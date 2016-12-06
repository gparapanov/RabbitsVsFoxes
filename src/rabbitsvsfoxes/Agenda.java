package rabbitsvsfoxes;

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
}
