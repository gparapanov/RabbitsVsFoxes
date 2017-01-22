/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Exploration;
import rabbitsvsfoxes.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class RabbitAgent extends Agent {

    public RabbitAgent(int x, int y, Environment env) {
        super(x, y, env);
        this.setIcon(new ImageIcon("images/rabbit1.png", "Rabbit icon"));

    }

    public RabbitAgent() {

    }

    @Override
    public void findGoal() {
        Goal goal;
        int minDistance = 10000;
        goal = new EatCarrot(null);
        int distance = 0;
        if (!objAround.isEmpty()) {
            for (EnvironmentObject eo : objAround) {
                if (true) {
                    if (env.getGui().getBehaviour() == 1) {
                        distance = manhattanDistance(this, eo);
                        //System.out.println("goal drive  ");
                    } else {
                        distance = evaluationFunction(this, eo);
                        //System.out.println("hybrid ");
                    }
                    if (minDistance > distance) {
                        minDistance = distance;
                        goal.setGoalObject(eo);
                        //System.out.println("found a carrot");
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

        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)) {
            this.addGoal(goal);
        }
        System.out.println("rabbit found carrot with score: " + minDistance); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lookAround(int radius) {
        objAround.clear();
        for (Carrot envObj : env.getCarrots()) {
            if (envObj.getX() >= (this.getX() - radius) && envObj.getX() <= (this.getX() + radius)
                    && envObj.getY() >= (this.getY() - radius) && envObj.getY() <= (this.getY() + radius)) {
                objAround.add(envObj);
            }
        }
    }

    public ArrayList<Agent> foxesAtArea(int x, int y, int r) {
        ArrayList<Agent> foxes = new ArrayList<>();
        for (EnvironmentObject ag : objAround) {
            if (ag instanceof FoxAgent) {
                if (ag.getX() <= x + r && ag.getX() >= x - r && ag.getY() <= y + r
                        && ag.getY() >= y - r) {//means a fox is a threat
                    foxes.add((Agent) ag);
                }
            }
        }
        return foxes;
    }

    @Override
    public int evaluationFunction(Agent ag, EnvironmentObject eo) {
        int result = 0, radius = (diagonalDistance(ag, eo) == 1 ? 2 : diagonalDistance(ag, eo));
        ArrayList<Agent> closeFoxes = foxesAtArea(eo.getX(), eo.getY(), radius);
        if (!closeFoxes.isEmpty()) {
            for (Agent f : closeFoxes) {
                int dist = diagonalDistance(ag, f);
                switch (dist) {
                    case 1:
                        result += 55;
                        break;
                    case 2:
                        result += 40;
                        break;
                    case 3:
                        result += 30;
                        break;
                    case 4:
                        result += 15;
                        break;
                    default:
                        result += dist;
                        break;
                }
            }
        }
        if (result > 0) {
            return result * manhattanDistance(ag, eo);
        } else {
            return manhattanDistance(ag, eo);
        }
    }
}
