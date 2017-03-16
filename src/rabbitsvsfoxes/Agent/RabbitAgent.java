/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Communication.MessageType;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Goals.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.FleeSpace;
import rabbitsvsfoxes.Goals.Explore;
import rabbitsvsfoxes.Goals.Flee;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class RabbitAgent extends Agent {

    private ArrayList<FoxAgent> foxesAround;
    private final int threatRadius = 5;

    public RabbitAgent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y, env, mg);
        this.setIcon(new ImageIcon("images/rabbit1.png", "Rabbit icon"));
        foxesAround = new ArrayList<>();

    }

    public RabbitAgent() {

    }

    @Override
    public void findGoal() {
        this.replenishHealth();
        System.out.println("my agenda contains: ");
        for (Goal g : agenda.getTasks()) {

            if (g instanceof EatCarrot) {
                System.out.println("a carrot with priority: " + g.getPriority());
            } else if (g instanceof Explore) {
                System.out.println("exploration ");
            }

        }
        Goal goal;
        goal = new EatCarrot(null);
        int minDistance = 10000;
        int distance = 0;
        if (foxesAround.isEmpty()) {//no dangerous foxes; flee ON
            //if (true) {//flee OFF
            if (!objAround.isEmpty()) {//there are carrots nearby
                //System.out.println("no foxes around, so i'm gonna eat that carrot");
                for (EnvironmentObject eo : objAround) {
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
            } else {
                //start exploring
                //System.out.println("no objects around - exploring");
                goal = new Explore(null);
                if (toExplore.isEmpty()) {
                    discoverExplorationSpaces();
                }
                for (UnexploredSpace us : toExplore) {
                    //System.out.println("looking for spaces");
                    distance = manhattanDistance(this, us);
                    if (distance < minDistance) {
                        minDistance = distance;
                        goal.setGoalObject(us);
                        goal.setPriority(4);
                        //System.out.println("better space found:"+distance);
                    }
                }
            }
        } else {
            //foxes around .i.e flee
            boolean enemyL = false, enemyR = false, enemyU = false, enemyD = false;
            //checks on which sides the enemies are, so that the direction too flee
            //can be determined
            //System.out.println("there is a fox around");
            lastLogs.add(0,"There is a fox around, I must flee!");
            for (FoxAgent fox : foxesAround) {
                if (fox.getX() <= this.getX() + 1 && fox.getX() >= this.getX() - 1
                        && fox.getY() <= this.getY() + threatRadius
                        && fox.getY() >= this.getY()) {
                    enemyD = true;
                    //System.out.println("there is a fox down");
                    //there is an enemy down
                } else if (fox.getX() <= this.getX() + 1 && fox.getX() >= this.getX() - 1
                        && fox.getY() >= this.getY() - threatRadius
                        && fox.getY() <= this.getY()) {
                    enemyU = true;
                    //System.out.println("there is a fox up");
                    //there is an enemy up
                } else if (fox.getX() <= this.getX() + threatRadius && fox.getX() >= this.getX()
                        && fox.getY() <= this.getY() + 1
                        && fox.getY() >= this.getY() - 1) {
                    enemyR = true;
                    //System.out.println("there is a fox right");
                    //enemy on the right
                } else if (fox.getX() <= this.getX() && fox.getX() >= this.getX() - threatRadius
                        && fox.getY() <= this.getY() + 1
                        && fox.getY() >= this.getY() - 1) {
                    enemyL = true;
                    //System.out.println("there is a fox left");
                    //enemy on the left
                }
            }
            //now figure out in which direction to go to avoid the threat
            goal = new Flee(null);
            goal.setGoalObject(determineFleeDirection(enemyU, enemyD, enemyR, enemyL));
            //System.out.println("running away!");
        }
        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)) {
            if (goal instanceof EatCarrot) {
                //if (!(agenda.getTop() instanceof EatCarrot)) {
                    //agenda top is not eat carrot
                    if (myGroup.checkCarrotClaimed(goal.getGoalObject())) {
                        //if someone has targeted it, then find goal again
                        System.out.println("someone already has targeted this");
                        lastLogs.add(0,"Found a carrot, but someone already saw it first!");
                        objAround.remove(goal.getGoalObject());
                        findGoal();
                    } else {
                        //no one has targeted it, add this goal to the agenda
                        System.out.println("this carrot seems free");
                        lastLogs.add(0,"I have found a carrot and I am claiming it!");
                        myGroup.broadcastMessage(new Message(MessageType.ClaimCarrot,goal.getGoalObject(),this.name));
                        this.addGoal(goal);
                    }
            } else if(goal instanceof Explore) {
                lastLogs.add(0,"There is nothing around me, I will explore!");
                this.addGoal(goal);
            }else{
                this.addGoal(goal);
            }
        }
        if (agenda.getTop()!=null && !agenda.getTop().getGoalObject().isAlive()) {
            agenda.removeTop();
        }
        //System.out.println("rabbit found carrot with score: " + minDistance); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkMove(Direction d) {
        switch (d) {
            case UP:
                if (getY() - 1 >= 0 && (env.spaceOccupied(getX(), getY() - 1) == null)){
                     //   || env.spaceOccupied(getX(), getY() - 1) instanceof Carrot)) {
                    //setY(getY() - 1);
                    return true;
                }
                break;
            case DOWN:
                if (getY() + 1 < env.getSize() && (env.spaceOccupied(getX(), getY() + 1) == null)){
                       // || env.spaceOccupied(getX(), getY() + 1) instanceof Carrot)) {
                    //setY(getY() + 1);
                    return true;
                }
                break;
            case LEFT:
                if (getX() - 1 >= 0 && (env.spaceOccupied(getX() - 1, getY()) == null)){
                      //  || env.spaceOccupied(getX() - 1, getY()) instanceof Carrot)) {
                    //setY(getX() - 1);
                    return true;
                }
                break;
            case RIGHT:
                if (getX() + 1 < env.getSize() && (env.spaceOccupied(getX() + 1, getY()) == null)){
                      //  || env.spaceOccupied(getX() + 1, getY()) instanceof Carrot)) {
                    //setY(getX() + 1);
                    return true;
                }
                break;
        }
        return false;
    }

    public FleeSpace determineFleeDirection(boolean enemyU, boolean enemyD, boolean enemyR, boolean enemyL) {
        if (enemyU && enemyD && enemyL && checkMove(Direction.RIGHT)) {
            //enemies up, down, left, therefore go right

            return new FleeSpace(getX() + 1, getY());
        } else if (enemyU && enemyD && enemyR && checkMove(Direction.LEFT)) {
            //enemies up, down, right, therefore go left etc....
            return new FleeSpace(getX() - 1, getY());
        } else if (enemyU && enemyR && enemyL && checkMove(Direction.DOWN)) {
            return new FleeSpace(getX(), getY() + 1);
        } else if (enemyD && enemyR && enemyL && checkMove(Direction.UP)) {
            return new FleeSpace(getX(), getY() - 1);
        } else if (enemyU && enemyD) {
            if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            } else if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            }
        } else if (enemyU && enemyR) {
            if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            }
        } else if (enemyU && enemyL) {
            if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            }
        } else if (enemyR && enemyL) {
            if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            }
        } else if (enemyD && enemyL) {
            if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            } else if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            }
        } else if (enemyD && enemyR) {
            if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            } else if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            }
        } else if (enemyU) {
            if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            } else if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            }
        } else if (enemyD) {
            if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            } else if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            }
        } else if (enemyR) {
            if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            } else if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            }
        } else if (enemyL) {
            if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            }
        }
        return null;
    }

    public Carrot findCarrotAt(int x, int y) {
        for (Carrot c : env.getCarrots()) {
            if (x == c.getX() && y == c.getY()) {
                return c;
            }
        }
        return null;
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
        foxesAround.clear();
        for (Agent envObj : env.getAgents()) {
            if (envObj instanceof FoxAgent && envObj.getX() >= (this.getX() - threatRadius) && envObj.getX() <= (this.getX() + threatRadius)
                    && envObj.getY() >= (this.getY() - threatRadius) && envObj.getY() <= (this.getY() + threatRadius)) {
                foxesAround.add((FoxAgent) envObj);
            }
        }
        //foxesAround = foxesAtArea(this.getX(), this.getY(), threatRadius);
    }

    public ArrayList<FoxAgent> foxesAtArea(int x, int y, int r) {
        ArrayList<FoxAgent> foxes = new ArrayList<>();
        for (EnvironmentObject ag : env.getAgents()) {
            if (ag instanceof FoxAgent) {
                if (ag.getX() <= x + r && ag.getX() >= x - r && ag.getY() <= y + r
                        && ag.getY() >= y - r) {//means a fox is a threat
                    foxes.add((FoxAgent) ag);
                }
            }
        }
        return foxes;
    }

    @Override
    public int evaluationFunction(Agent ag, EnvironmentObject eo) {
        int result = 0, radius = (diagonalDistance(ag, eo) == 1 ? 2 : diagonalDistance(ag, eo));
        ArrayList<FoxAgent> closeFoxes = foxesAtArea(eo.getX(), eo.getY(), radius);
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
