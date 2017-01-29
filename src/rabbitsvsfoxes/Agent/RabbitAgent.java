/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Goals.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.FleeSpace;
import rabbitsvsfoxes.Goals.Exploration;
import rabbitsvsfoxes.Goals.Flee;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class RabbitAgent extends Agent {

    private ArrayList<FoxAgent> foxesAround;
    private final int threatRadius = 4;

    public RabbitAgent(int x, int y, Environment env) {
        super(x, y, env);
        this.setIcon(new ImageIcon("images/rabbit1.png", "Rabbit icon"));
        foxesAround = new ArrayList<>();
    }

    public RabbitAgent() {

    }

    @Override
    public void findGoal() {
        Goal goal;
        goal = new EatCarrot(null);
        int minDistance = 10000;
        int distance = 0;
        if (!objAround.isEmpty()) {//there are carrots nearby
            if (foxesAround.isEmpty()) {//no dangerous foxes

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
            } else {//there are carrots and foxes around .i.e flee
                boolean enemyL = false, enemyR = false, enemyU = false, enemyD = false;
                //checks on which sides the enemies are, so that the direction too flee
                //can be determined
                System.out.println("there is a fox around");
                for (FoxAgent fox : foxesAround) {

                    if (fox.getX() <= this.getX() + 1 && fox.getX() >= this.getX() - 1
                            && fox.getY() <= this.getY() + threatRadius
                            && fox.getY() >= this.getY()) {
                        enemyD = true;
                        System.out.println("there is a fox down");
                        //there is an enemy down
                    } else if (fox.getX() <= this.getX() + 1 && fox.getX() >= this.getX() - 1
                            && fox.getY() >= this.getY() - threatRadius
                            && fox.getY() <= this.getY()) {
                        enemyU = true;
                        System.out.println("there is a fox up");
                        //there is an enemy up
                    } else if (fox.getX() <= this.getX() + threatRadius && fox.getX() >= this.getX()
                            && fox.getY() <= this.getY() + 1
                            && fox.getY() >= this.getY() - 1) {
                        enemyR = true;
                        System.out.println("there is a fox right");
                        //enemy on the right
                    } else if (fox.getX() <= this.getX() && fox.getX() >= this.getX() - threatRadius
                            && fox.getY() <= this.getY() + 1
                            && fox.getY() >= this.getY() - 1) {
                        enemyL = true;
                        System.out.println("there is a fox left");
                        //enemy on the left
                    }
                }
                //now figure out in which direction to go to avoid the threat
                goal = new Flee(null);
                if (enemyU && enemyD && enemyL && checkAndMove(Direction.RIGHT)) {
                    goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                } else if (enemyU && enemyD && enemyR && checkAndMove(Direction.LEFT)) {
                    goal.setGoalObject(new FleeSpace(getX() - 1, getY()));
                } else if (enemyU && enemyR && enemyL && checkAndMove(Direction.DOWN)) {
                    goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                } else if (enemyD && enemyR && enemyL && checkAndMove(Direction.UP)) {
                    goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                } else if (enemyU && enemyD) {
                    if (checkAndMove(Direction.LEFT)) {
                        goal.setGoalObject(new FleeSpace(getX() - 1, getY()));
                    } else if (checkAndMove(Direction.RIGHT)) {
                        goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                    }
                } else if (enemyU && enemyR) {
                    if (checkAndMove(Direction.LEFT)) {
                        goal.setGoalObject(new FleeSpace(getX() - 1, getY()));
                    } else if (checkAndMove(Direction.DOWN)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                    }
                } else if (enemyU && enemyL) {
                    if (checkAndMove(Direction.RIGHT)) {
                        goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                    } else if (checkAndMove(Direction.DOWN)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                    }
                } else if (enemyR && enemyL) {
                    if (checkAndMove(Direction.UP)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                    } else if (checkAndMove(Direction.DOWN)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                    }
                } else if (enemyD && enemyL) {
                    if (checkAndMove(Direction.UP)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                    } else if (checkAndMove(Direction.RIGHT)) {
                        goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                    }
                } else if (enemyD && enemyR) {
                    if (checkAndMove(Direction.LEFT)) {
                        goal.setGoalObject(new FleeSpace(getX() - 1, getY()));
                    } else if (checkAndMove(Direction.UP)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                    }
                } else if (enemyU) {
                    if (checkAndMove(Direction.DOWN)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                    } else if (checkAndMove(Direction.LEFT)) {
                        goal.setGoalObject(new FleeSpace(getX() - 1, getY()));;
                    }
                } else if (enemyD) {
                    if (checkAndMove(Direction.UP)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                    } else if (checkAndMove(Direction.RIGHT)) {
                        goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                    }
                } else if (enemyR) {
                    if (checkAndMove(Direction.LEFT)) {
                        goal.setGoalObject(new FleeSpace(getX() - 1, getY()));
                    } else if (checkAndMove(Direction.UP)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() - 1));
                    }
                } else if (enemyL) {
                    if (checkAndMove(Direction.RIGHT)) {
                        goal.setGoalObject(new FleeSpace(getX() + 1, getY()));
                    } else if (checkAndMove(Direction.DOWN)) {
                        goal.setGoalObject(new FleeSpace(getX(), getY() + 1));
                    }
                }
                System.out.println("running away!");
            }

        } else {
            //start exploring
            //System.out.println("no objects around");
            goal = new Exploration(null);
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
        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)) {
            this.addGoal(goal);
        }
        //System.out.println("rabbit found carrot with score: " + minDistance); //To change body of generated methods, choose Tools | Templates.
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
            if (envObj instanceof FoxAgent) {
                if (envObj.getX() >= (this.getX() - threatRadius) && envObj.getX() <= (this.getX() + threatRadius)
                        && envObj.getY() >= (this.getY() - threatRadius) && envObj.getY() <= (this.getY() + threatRadius)) {
                    foxesAround.add((FoxAgent) envObj);
                }
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
