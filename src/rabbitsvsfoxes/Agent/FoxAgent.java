/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import rabbitsvsfoxes.Agent.Agent;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.CatchRabbit;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Exploration;
import rabbitsvsfoxes.Goal;
import rabbitsvsfoxes.UnexploredSpace;

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
        Goal goal = null;
        int distance = 0;
        if (!objAround.isEmpty()) {
            for (EnvironmentObject eo : objAround) {
                if (eo.isAlive()) {
                    distance = manhattanDistance(this, eo);
                    if (minDistance > distance) {
                        minDistance = distance;
                        goal = new CatchRabbit((RabbitAgent) eo);
                        System.out.println("fox found rabbit" + goal.getGoalObject().getX());
                    }
                }
            }
        } else {
            System.out.println("no objects around");
            goal=new Exploration(null);
            if(toExplore.isEmpty()){
                discoverExplorationSpaces();
            }
            for(UnexploredSpace us:toExplore){
                System.out.println("looking for spaces");
                distance=manhattanDistance(this,us);
                if(distance<minDistance){
                    minDistance=distance;
                    goal.setGoalObject(us);
                    goal.setPriority(4);
                    //System.out.println("better space found:"+distance);
                }
            }
        }

        if (goal != null && !agenda.checkExistists(goal)) {
            this.addGoal(goal);
        }
    }

    @Override
    public void lookAround(int radius) {
        objAround.clear();
        for (Agent envObj : env.getAgents()) {
            if (envObj instanceof RabbitAgent && envObj.getX() > (this.getX() - radius)
                    && envObj.getX() < (this.getX() + radius) && envObj.getY() > (this.getY() - radius)
                    && envObj.getY() < (this.getY() + radius)) {
                objAround.add(envObj);
            }
        }
    }

}
