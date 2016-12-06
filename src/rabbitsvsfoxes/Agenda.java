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
    
    public void addTask(Goal g){
        tasks.add(g);
    }
    public void removeTask(Goal g){
        tasks.remove(g);
    }
}
