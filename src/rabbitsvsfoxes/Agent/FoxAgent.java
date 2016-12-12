/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import rabbitsvsfoxes.Agent.Agent;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.CatchRabbit;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Goal;

/**
 *
 * @author Georgi
 */
public class FoxAgent extends Agent {

    public FoxAgent(int x, int y, Environment env) {
        super(x, y, env);
        this.setIcon(new ImageIcon("images/fox (1).png", "Fox icon"));
    }

    @Override
    public void findGoal() {
        
        System.out.println("fox looking for rabbbits");
        int minDistance = 1000;
        CatchRabbit goal=null;
        int distance = 0;
        for (EnvironmentObject eo : objAround) {
            if (eo instanceof RabbitAgent && eo.isAlive()) {
                distance = manhattanDistance(this, eo);
                if (minDistance > distance) {
                    minDistance = distance;
                    goal=new CatchRabbit((RabbitAgent)eo);
                    System.out.println("fox found rabbit" + goal.getGoalObject().getX());
                }
            }
        }
        if (goal!= null && !agenda.checkExistists(goal)) {
            this.addGoal(goal);
        }
    }

}
