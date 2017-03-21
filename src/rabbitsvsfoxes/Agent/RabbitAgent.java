/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Communication.MessageType;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Goals.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.FleeSpace;
import rabbitsvsfoxes.Goals.DistractFox;
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
    private final int threatRadius;
    private ArrayList<FoxAgent> foxesDistrated = new ArrayList<>();

    public RabbitAgent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y, env, mg);
        this.setIcon(new ImageIcon("images/rabbit1.png", "Rabbit icon"));
        foxesAround = new ArrayList<>();
        this.threatRadius = 6;
    }

    public RabbitAgent() {
        this.threatRadius = 6;
    }

    @Override
    public void findGoal() {
        this.replenishHealth();
//        System.out.println("my agenda contains: ");
//        for (Goal g : agenda.getTasks()) {
//
//            if (g instanceof EatCarrot) {
//                System.out.println("a carrot with priority: " + g.getPriority());
//            } else if (g instanceof Explore) {
//                System.out.println("exploration ");
//            }
//
//        }
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
            //checks on which sides the enemies are, so that the direction to flee
            //can be determined
            //System.out.println("there is a fox around");
            for (FoxAgent fox : foxesAround) {
                int foxX = fox.getX();
                int foxY = fox.getY();
                int differenceX = Math.abs(foxX - getX());
                int differenceY = Math.abs(foxY - getY());
                if(differenceX>differenceY){
                    if(foxX>getX()){
                        enemyR=true;
                    }else{
                        enemyL=true;
                    }
                }else{
                    if(foxY>getY()){
                        enemyD=true;
                    }else{
                        enemyU=true;
                    }
                }
            }
            //now figure out in which direction to go to avoid the threat
            goal = new Flee(null);
            goal.setGoalObject(determineFleeDirection(enemyU, enemyD, enemyR, enemyL));
            //System.out.println("running away!");
        }

        //open the postbox to check if there are any messages
        Goal postGoal = openPostbox();
        //if there is a valid message there and the goal is not already in the
        //agenda, then add it and remove other targeted rabbits
        if (postGoal != null) {
            if (agenda.getTop() != null && postGoal.getPriority() > agenda.getTop().getPriority()) {
                //If the agent has something else to - do remove it -
                //this is done to avoid keeping a reference another rabbit and
                //making sure that the foxes won't go after it unless it can see it.
                agenda.removeTop();
            }
            if (!agenda.checkExistists(postGoal)) {
                this.addGoal(postGoal);
            }
        }

        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)) {
            if (goal instanceof EatCarrot) {
                //if (!(agenda.getTop() instanceof EatCarrot)) {
                //agenda top is not eat carrot
                if (myGroup.checkCarrotClaimed(goal.getGoalObject())) {
                    //if someone has targeted it, then find goal again
                    //System.out.println("someone already has targeted this");
                    String claimer = ((Carrot) goal.getGoalObject()).getClaimedBy();
                    if (!claimer.equals(myName)) {
                        lastLogs.add(0, "Found a carrot, but " + claimer + " already saw it first!");
                        objAround.remove(goal.getGoalObject());
                        findGoal();
                    }

                } else {
                    //no one has targeted it, add this goal to the agenda
                    //System.out.println("this carrot seems free");
                    lastLogs.add(0, "I have found a carrot and I am claiming it!");
                    myGroup.broadcastMessage(new Message(MessageType.ClaimCarrot, goal.getGoalObject(), this.myName));
                    this.addGoal(goal);
                }
            } else if (goal instanceof Explore && agenda.getTasks().size() == 0) {
                lastLogs.add(0, "There is nothing around me, I will explore!");
                this.addGoal(goal);
            } else if (goal instanceof Flee) {
                this.addGoal(goal);
                lastLogs.add(0, "There is a fox around, I must flee!");
                if (env.getGui().getRabbitsTeamwork1()) {
                    if (!myGroup.checkFoxBeingDistracted(foxesAround.get(0))
                            && foxesAround.get(0).getCurrentTarget().equals(myName)) {
                        Message messageToSend = new Message(MessageType.RequestDistraction, foxesAround.get(0), this.myName);
                        messageToSend.setTeamColor(myColor);
                        myGroup.broadcastMessage(messageToSend);
                        lastLogs.add(0, "A fox is chasing me, asking for help!");
                    }
                }
                
            }
        }
        if (agenda.getTop() != null && !agenda.getTop().getGoalObject().isAlive()) {
            agenda.removeTop();
            //findGoal();
        }
        /*
         A check for a special case, when a rabbit has requested backup and it has died.
         The rabbit going for the backup will see this and do something else instead.
         This is done by changing the current target string of the fox.
         */
        if (agenda.getTop() != null) {
            if (agenda.getTop() instanceof DistractFox
                    && ((FoxAgent) agenda.getTop().getGoalObject()).getCurrentTarget().equals("")) {
                agenda.removeTop();
                //findGoal();
            }
        }
        //System.out.println("rabbit found carrot with score: " + minDistance); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Goal openPostbox() {
        Message newestMessage = myGroup.getMessage(messagesReadIndex);
        if (newestMessage != null) {
            messagesReadIndex++;
            if (newestMessage.getMsgType().equals(MessageType.RequestDistraction)
                    && !newestMessage.getSenderName().equals(myName)) {
                //Condition1 - whether the recipient is close to the sender.
                if (!myGroup.checkFoxBeingDistracted(newestMessage.getTargetObject())
                        && diagonalDistance(this, newestMessage.getTargetObject()) <= env.getSize() / 4) {
                    myGroup.broadcastMessage(new Message(MessageType.EngageInDistraction, newestMessage.getTargetObject(), myName));
                    Goal newGoal = new DistractFox(newestMessage.getTargetObject());
                    newGoal.setTeamColor(newestMessage.getTeamColor());
                    lastLogs.add(0, newestMessage.getSenderName() + " nearby needs help, going to distract " + ((Agent) newestMessage.getTargetObject()).getMyName());
                    return newGoal;
                } else if (myGroup.checkFoxBeingDistracted(newestMessage.getTargetObject())) {
                    lastLogs.add(0, newestMessage.getSenderName() + " nearby needs help, but someone is already going there!");
                } else {
                    lastLogs.add(0, newestMessage.getSenderName() + " needs help, but I am too far away!");
                }

            }

        }
        return null;
    }

    @Override
    public boolean checkMove(Direction d) {
        switch (d) {
            case UP:
                if (getY() - 1 >= 0 && (env.spaceOccupied(getX(), getY() - 1) == null)) {
                    //   || env.spaceOccupied(getX(), getY() - 1) instanceof Carrot)) {
                    //setY(getY() - 1);
                    return true;
                }
                break;
            case DOWN:
                if (getY() + 1 < env.getSize() && (env.spaceOccupied(getX(), getY() + 1) == null)) {
                    // || env.spaceOccupied(getX(), getY() + 1) instanceof Carrot)) {
                    //setY(getY() + 1);
                    return true;
                }
                break;
            case LEFT:
                if (getX() - 1 >= 0 && (env.spaceOccupied(getX() - 1, getY()) == null)) {
                    //  || env.spaceOccupied(getX() - 1, getY()) instanceof Carrot)) {
                    //setY(getX() - 1);
                    return true;
                }
                break;
            case RIGHT:
                if (getX() + 1 < env.getSize() && (env.spaceOccupied(getX() + 1, getY()) == null)) {
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
            } else if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            }
        } else if (enemyD) {
            if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            } else if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            } else if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            }
        } else if (enemyR) {
            if (checkMove(Direction.LEFT)) {
                return new FleeSpace(getX() - 1, getY());
            } else if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            }
        } else if (enemyL) {
            if (checkMove(Direction.RIGHT)) {
                return new FleeSpace(getX() + 1, getY());
            } else if (checkMove(Direction.DOWN)) {
                return new FleeSpace(getX(), getY() + 1);
            } else if (checkMove(Direction.UP)) {
                return new FleeSpace(getX(), getY() - 1);
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
