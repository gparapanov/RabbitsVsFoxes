/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private final int distractionTimeout = 12;
    private final int threatRadius = 8;
    private int helpTimeout = 0;

    public RabbitAgent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y, env, mg);
        this.setIcon(new ImageIcon("images/rabbit1.png", "Rabbit icon"));
        foxesAround = new ArrayList<>();
    }

    public RabbitAgent() {
    }

    @Override
    public void findGoal() {
        Goal goal;
        goal = new EatCarrot(null);
        double maxUtility = 0, utility = 0;

        if (foxesAround.isEmpty()) {//no dangerous foxes; flee ON
            //   if (true) {//flee OFF
            if (!objAround.isEmpty()) {//there are carrots nearby
                //System.out.println("no foxes around, so i'm gonna eat that carrot");
                for (EnvironmentObject eo : objAround) {
                    if (env.getGui().getBehaviour() == 1) {
                        utility = manhattanDistance(this, eo);
                        //System.out.println("goal drive  ");
                    } else {
                        utility = evaluationFunctionCarrot(this, eo);
                        //System.out.println("hybrid ");
                    }

                    if (maxUtility < utility) {
                        maxUtility = utility;
                        goal.setGoalObject(eo);
                        goal.setUtility(maxUtility);
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
                    utility = evaluationFunctionFleeAndExplore(this, us);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        goal.setGoalObject(us);
                        goal.setUtility(maxUtility);
                        goal.setPriority(4);//lower utility for exploration
                    }
                }
            }
        } else {
            //foxes around .i.e flee
            boolean enemyL = false, enemyR = false, enemyU = false, enemyD = false;
            //checks on which sides the enemies are, so that the direction to flee
            //can be determined
            for (FoxAgent fox : foxesAround) {
                int foxX = fox.getX();
                int foxY = fox.getY();
                int differenceX = Math.abs(foxX - getX());
                int differenceY = Math.abs(foxY - getY());
                if (differenceX > differenceY) {
                    if (foxX > getX()) {
                        enemyR = true;
                        System.out.println("there is a fox on the right");
                    } else {
                        enemyL = true;
                        System.out.println("there is a fox on the left");
                    }
                } else if (differenceX < differenceY) {
                    if (foxY > getY()) {
                        enemyD = true;
                        System.out.println("there is a fox down");
                    } else {
                        enemyU = true;
                        System.out.println("there is a fox up");
                    }
                } else {
                    if (foxY > getY() && foxX > getX()) {
                        //enemy is diagonally down right
                        System.out.println("there is a fox down right");
                        enemyD = true;
                        enemyR = true;
                    } else if (foxY > getY() && foxX < getX()) {
                        //enemy is diagonally down left
                        System.out.println("there is a fox down left");
                        enemyD = true;
                        enemyL = true;
                    } else if (foxY < getY() && foxX < getX()) {
                        //enemy is diagonally up left
                        System.out.println("there is a fox up left");
                        enemyU = true;
                        enemyL = true;
                    } else if (foxY < getY() && foxX > getX()) {
                        //enemy is diagonally up right
                        System.out.println("there is a fox up right");
                        enemyU = true;
                        enemyR = true;
                    }
                }
            }
            //now figure out in which direction to go to avoid the threat
            goal = new Flee(null);
            goal.setGoalObject(determineFleeDirection(enemyU, enemyD, enemyR, enemyL));
            //System.out.println("running away!");
        }

        //if the communication is enabled
        if (env.getGui().getRabbitsTeamwork1()) {
            //open the postbox to check if there are any messages
            Goal postGoal = openPostbox();
            //if there is a valid message there and the goal is not already in the
            //agenda, then add it and remove other targeted rabbits
            if (postGoal != null) {
                if (postGoal.getUtility() > goal.getUtility()) {
                    lastLogs.add(0, lastMessageRead.getSender().getName() + " is asking for help, I will go and distract " + ((Agent) lastMessageRead.getTargetObject()).getName() + "!");
                    //env.getGui().writeLogToGui(myName + " is going to help " + lastMessageRead.getSender().getName() + " by distracting " + ((Agent) lastMessageRead.getTargetObject()).getName());
                    goal = postGoal;
                    env.getGui().writeLogToGui("Rabbit: " + myName + " going to help its teammate " + lastMessageRead.getSender().getName() + " by distracting " + ((Agent) lastMessageRead.getTargetObject()).getName());
                    myGroup.broadcastMessage(new Message(MessageType.EngageInDistraction, goal.getGoalObject(), this));
                } else {
                    lastLogs.add(0, lastMessageRead.getSender().getName() + " is asking for help, but I prefer to work on my own!");
                    env.getGui().writeLogToGui(myName + " can help " + lastMessageRead.getSender().getName() + " but prefers to do something for himself!");
                }
            }
        }

        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)) {
            if (agenda.getTop() instanceof Flee
                    && (foxesAround.isEmpty())) {
                agenda.removeTop();
            }
            if (goal instanceof EatCarrot) {
                //if (!(agenda.getTop() instanceof EatCarrot)) {
                //agenda top is not eat carrot
                if (myGroup.checkCarrotClaimed(goal.getGoalObject())
                        ) {
                    //if someone has targeted it, then find goal again
                    String claimer = ((Carrot) goal.getGoalObject()).getClaimedBy();
                    if (!claimer.equals(myName)) {
                        lastLogs.add(0, "Found a carrot, but " + claimer + " already saw it first!");
                        env.getGui().writeLogToGui("Rabbit: " + myName + " saw a carrot at x:" + goal.getGoalObject().getX() + " y:" + goal.getGoalObject().getY() + " but it's already claimed by " + claimer);
                        objAround.remove(goal.getGoalObject());
                        findGoal();
                    }else{
                        //claimed by me, but it is not in the agenda
                        this.addGoal(goal);
                    }
                } else {
                    //no one has targeted it, add this goal to the agenda
                    //System.out.println("this carrot seems free");
                    lastLogs.add(0, "I have found a carrot and I am claiming it!");
                    env.getGui().writeLogToGui("Rabbit: " + myName + " saw a carrot at x:" + goal.getGoalObject().getX() + " y:" + goal.getGoalObject().getY() + " and claims it.");
                    myGroup.broadcastMessage(new Message(MessageType.ClaimCarrot, goal.getGoalObject(), this));
                    this.addGoal(goal);
                }
            } else if (goal instanceof Explore && agenda.getTasks().size() == 0) {
                lastLogs.add(0, "There is nothing around me, I will explore!");
                this.addGoal(goal);
            } else if (goal instanceof Flee) {
                if (!(agenda.getTop() instanceof DistractFox) && !(agenda.getTop() instanceof Flee)) {
                    this.addGoal(goal);
                    lastLogs.add(0, "There is a fox around, I must flee!");
                    env.getGui().writeLogToGui("Rabbit: " + myName + " saw a fox and is fleeing to x:" + goal.getGoalObject().getX() + " y:" + goal.getGoalObject().getY());
                }

                if (env.getGui().getRabbitsTeamwork1()) {
                    //finding the nearest fox, so that a request broadcast message 
                    //can be send for someone to distract it
                    FoxAgent nearestFox = foxesAround.get(0);
                    int distance = 0, minDistance = 1000;
                    for (FoxAgent foxAgent : foxesAround) {
                        distance = manhattanDistance(this, foxAgent);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestFox = foxAgent;
                        }
                    }
                    if (!myGroup.checkFoxBeingDistracted(nearestFox)//no one is distracting the fox
                            && nearestFox.getCurrentTarget().equals(myName)// the fox has targeted me
                            && helpTimeout == 0
                            && diagonalDistance(this, nearestFox) <= threatRadius) {// the fox is dangerously close
                        Message messageToSend = new Message(MessageType.RequestDistraction, nearestFox, this);
                        messageToSend.setTeamColor(myColor);
                        myGroup.broadcastMessage(messageToSend);
                        env.getGui().writeLogToGui("Rabbit: " + myName + " - the fox " + nearestFox.getName() + " is very close asking for someone to distract it!");
                        lastLogs.add(0, "A fox is chasing me, asking for help!");
                    } else if (myGroup.checkFoxBeingDistracted(nearestFox)) {

                    }
                }

            } else if (goal instanceof DistractFox) {
                this.addGoal(goal);
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
            if (agenda.getTop() instanceof DistractFox) {
                int differenceX = Math.abs(agenda.getTop().getGoalObject().getX() - getX());
                int differenceY = Math.abs(agenda.getTop().getGoalObject().getY() - getY());
                if (((FoxAgent) agenda.getTop().getGoalObject()).getCurrentTarget().equals("---")) {
                    myGroup.broadcastMessage(new Message(MessageType.DisengageInDistraction, agenda.getTop().getGoalObject(), this));
                    agenda.removeTop();
                    findGoal();
                } else if (((FoxAgent) agenda.getTop().getGoalObject()).getCurrentTarget().equals(myName)
                        || (differenceX <= 1 && differenceY <= 1)) {
                    /*
                     If my top goal is to distract a fox and the fox has changed its target to my name,
                     then I have distracted it and the goal is completed.
                     */;

                    helpTimeout = distractionTimeout;//wait n runs until calling for help again
                    System.out.println("I have distracted the fox");
                    myGroup.broadcastMessage(new Message(MessageType.DisengageInDistraction, agenda.getTop().getGoalObject(), this));
                    lastLogs.add(0, "I have distracted the fox, running away!");
                    env.getGui().writeLogToGui(myName + " has distracted " + ((FoxAgent) agenda.getTop().getGoalObject()).getName() + " and is running away!");
                    agenda.removeTop();
                }
            }
        }
        if (helpTimeout > 0) {
            helpTimeout--;
        }
        //System.out.println("rabbit found carrot with score: " + minDistance); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Goal openPostbox() {
        Message newestMessage = myGroup.getMessage(messagesReadIndex);
        if (newestMessage != null) {
            lastMessageRead = newestMessage;
            messagesReadIndex++;
            if (newestMessage.getMsgType().equals(MessageType.RequestDistraction)
                    && !newestMessage.getSender().getName().equals(myName)
                    && helpTimeout == 0) {
                //Condition 1 - whether the recipient is close to the sender.
                //Condition 2 whether the fox is not already distracted by someone else
                if (!myGroup.checkFoxBeingDistracted(newestMessage.getTargetObject())
                        && diagonalDistance(this, newestMessage.getTargetObject()) <= env.getSize() / 4) {
                    Goal newGoal = new DistractFox(newestMessage.getTargetObject());
                    newGoal.setTeamColor(newestMessage.getTeamColor());
                    newGoal.setUtility(evaluationFunctionTeamwork(this, newestMessage.getSender(), (Agent) newestMessage.getTargetObject()));

                    return newGoal;
                } else if (myGroup.checkFoxBeingDistracted(newestMessage.getTargetObject())) {
                    lastLogs.add(0, newestMessage.getSender().getName() + " nearby needs help, but someone is already going there!");
                } else {
                    lastLogs.add(0, newestMessage.getSender().getName() + " needs help, but I am too far away!");
                    env.getGui().writeLogToGui("Rabbit: " + myName + " received a help message from " + newestMessage.getSender().getName() + " but is too far away. ");
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

    /**
     * This method returns a space to which the agent has to flee. It takes 4
     * parameters, each of them is true if there are enemies on that side. E.g.
     * if U and R are true, this means there is an enemy up and of the right of
     * the agent.
     *
     * @param enemyU
     * @param enemyD
     * @param enemyR
     * @param enemyL
     * @return
     */
    public FleeSpace determineFleeDirection(boolean enemyU, boolean enemyD, boolean enemyR, boolean enemyL) {
        int fleeX = getX(), fleeY = getY(), maxX = getX(), maxY = getY(), maxUtility = 0;
        ArrayList<FleeSpace> bestSpaces = new ArrayList<>();
        if (enemyU && enemyD && enemyL) {
            //enemies up, down, left, therefore go right
            while (fleeX <= getX() + radius && fleeX < env.getSize()) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeX++;
            }
        } else if (enemyU && enemyD && enemyR) {
            //enemies up, down, right, therefore go left etc....
            while (fleeX >= getX() - radius && fleeX >= 0) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeX--;
            }
        } else if (enemyU && enemyR && enemyL) {
            //enemies up, right, left therefore go down
            while (fleeY <= getY() + radius && fleeY < env.getSize()) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeY++;
            }
        } else if (enemyD && enemyR && enemyL) {
            //enemies down right left - go up
            while (fleeY >= getY() - radius && fleeY >= 0) {
                if (env.spaceOccupied(fleeX, fleeY) != null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeY--;
            }
        } else if (enemyU && enemyD) {
            //enemies up and down
            while (fleeX >= getX() - radius && fleeX >= 0) {
                if (env.spaceOccupied(fleeX, fleeY) != null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                        bestSpaces.clear();
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == maxUtility) {
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeX--;
            }
            fleeX = getX();
            while (fleeX <= getX() + radius && fleeX < env.getSize()) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                        bestSpaces.clear();
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == maxUtility) {
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeX++;
            }
        } else if (enemyU && enemyR) {
            for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }
        } else if (enemyU && enemyL) {
            for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }
        } else if (enemyR && enemyL) {
            while (fleeY <= getY() + radius && fleeY < env.getSize()) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                        bestSpaces.clear();
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == maxUtility) {
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeY++;
            }//check for spaces down
            fleeY = getY();
            while (fleeY >= getY() - radius && fleeY >= 0) {
                if (env.spaceOccupied(fleeX, fleeY) == null) {
                    int utility = safetyUtility(fleeX, fleeY, radius);
                    if (utility > maxUtility) {
                        maxUtility = utility;
                        maxX = fleeX;
                        maxY = fleeY;
                        bestSpaces.clear();
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == maxUtility) {
                        bestSpaces.add(new FleeSpace(maxX, maxY));
                    } else if (utility == 0) {
                        return new FleeSpace(maxX, maxY);
                    }
                }
                fleeY--;
            }//check for spaces up
        } else if (enemyD && enemyL) {
            for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }
        } else if (enemyD && enemyR) {
            for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }
        } else if (enemyU) {
            for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy is up left
            for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy is up right
        } else if (enemyD) {
            for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy down left
            for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {

                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy down right
        } else if (enemyR) {
            for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy down right
            for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                for (fleeX = getX(); fleeX >= 0 && fleeX >= getX() - radius; fleeX--) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {
                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//up right
        } else if (enemyL) {
            for (fleeY = getY(); fleeY >= 0 && fleeY >= getY() - radius; fleeY--) {
                for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {

                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy is down left
            for (fleeY = getY(); fleeY < env.getSize() && fleeY <= getY() + radius; fleeY++) {
                for (fleeX = getX(); fleeX < env.getSize() && fleeX <= getX() + radius; fleeX++) {
                    if (env.spaceOccupied(fleeX, fleeY) == null) {

                        int utility = safetyUtility(fleeX, fleeY, radius);
                        if (utility > maxUtility) {
                            maxUtility = utility;
                            maxX = fleeX;
                            maxY = fleeY;
                            bestSpaces.clear();
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == maxUtility) {
                            bestSpaces.add(new FleeSpace(maxX, maxY));
                        } else if (utility == 0) {
                            return new FleeSpace(maxX, maxY);
                        }
                    }
                }
            }//enemy is up left
        }
        System.out.println("running to " + maxX + " " + maxY);
        int randIndex=(int)(Math.random()*bestSpaces.size());
        //return bestSpaces.get(randIndex);
        return new FleeSpace(maxX, maxY);
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
            if (envObj instanceof FoxAgent && envObj.getX() >= (this.getX() - radius) && envObj.getX() <= (this.getX() + radius)
                    && envObj.getY() >= (this.getY() - radius) && envObj.getY() <= (this.getY() + radius)) {
                foxesAround.add((FoxAgent) envObj);
            }
        }
        //foxesAround = foxesAtArea(this.getX(), this.getY(), threatRadius);
    }

    public int safetyUtility(int x, int y, int radius) {
        int result = 0, count = 0;
        for (FoxAgent fox : foxesAround) {
            result += Math.abs(x - fox.getX()) + Math.abs(y - fox.getY());
            if (Math.max(Math.abs(x - fox.getX()), Math.abs(y - fox.getY())) > radius + 1) {
                //System.out.println("utility is 0 at " + x + " " + y);
                count++;
            }
        }
        if (count == foxesAround.size()) {
            return 0;
        } else {
            return result / foxesAround.size();
        }

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

    public void unclaimAllCarrots() {
        myGroup.broadcastMessage(new Message(MessageType.UnclaimAllCarrots, null, this));
        if (agenda.getTop() instanceof DistractFox) {
            myGroup.broadcastMessage(new Message(MessageType.DisengageInDistraction, agenda.getTop().getGoalObject(), this));
        }
    }

    /**
     * A function that gives a utility to a goal of type Flee or Explore. The
     * utility is calculated based on the distance to the object. (E.g. distance
     * between me and fox, me and exploration space etc.)
     *
     * @param ag
     * @param eo
     * @return
     */
    public double evaluationFunctionFleeAndExplore(Agent ag, EnvironmentObject eo) {
        double distanceMultiplier,
                characterMultiplier = (agentCharacter < characterSeparator) ? 0.7 : 1.3;
//        if (manhattanDistance(ag, eo) <= 3) {
//            distanceMultiplier = 10;
//        } else if (manhattanDistance(ag, eo) <= 4) {
//            distanceMultiplier = 9;
//        } else if (manhattanDistance(ag, eo) <= 5) {
//            distanceMultiplier = 8;
//        } else if (manhattanDistance(ag, eo) <= 6) {
//            distanceMultiplier = 7;
//        } else if (manhattanDistance(ag, eo) <= 7) {
//            distanceMultiplier = 6;
//        } else {
//            distanceMultiplier = 5;
//        }
        return manhattanDistance(ag, eo) * characterMultiplier;
    }

    /**
     *
     * @param ag
     * @param requester
     * @param target
     * @return
     */
    public double evaluationFunctionTeamwork(Agent ag, Agent requester, Agent target) {
        double rabbitsMultiplier,
                distanceMultiplier,
                characterMultiplier = (agentCharacter < characterSeparator) ? 1.3 : 0.7;//i.e. is more inclined to eating carrots

        int radius = diagonalDistance(ag, requester);
        //assign rabbits number multiplier
        //influenced by the number of rabbits between requester and me
        ArrayList<RabbitAgent> rabbitsAround = rabbitsAtArea(requester.getX(), requester.getY(), radius);
        if (!rabbitsAround.isEmpty()) {
            rabbitsMultiplier = rabbitsAround.size();
        } else {
            rabbitsMultiplier = 1;
        }

        //assign distance multiplier
        if (manhattanDistance(ag, target) <= 5) {
            distanceMultiplier = 10;
        } else if (manhattanDistance(ag, target) <= 8) {
            distanceMultiplier = 9;
        } else if (manhattanDistance(ag, target) <= 11) {
            distanceMultiplier = 8;
        } else if (manhattanDistance(ag, target) <= 14) {
            distanceMultiplier = 7;
        } else if (manhattanDistance(ag, target) <= 17) {
            distanceMultiplier = 6;
        } else {
            distanceMultiplier = 5;
        }

        return rabbitsMultiplier * distanceMultiplier * characterMultiplier;
    }

    /**
     *
     * @param ag
     * @param eo
     * @return
     */
    public double evaluationFunctionCarrot(Agent ag, EnvironmentObject eo) {
        int radius = (diagonalDistance(ag, eo) == 1 ? 2 : diagonalDistance(ag, eo));
        double foxesMultiplier,
                distanceMultiplier,
                characterMultiplier = (agentCharacter < characterSeparator) ? 0.7 : 1.3;//i.e. is more inclined to eating carrots

        //assign foxes number multiplier
        ArrayList<FoxAgent> closeFoxes = foxesAtArea(eo.getX(), eo.getY(), radius);
        if (closeFoxes.isEmpty()) {
            foxesMultiplier = 2;
        } else {
            switch (closeFoxes.size()) {
                case 1:
                    foxesMultiplier = 1;
                    break;
                default:
                    foxesMultiplier = 0.5;
                    break;
            }
            foxesMultiplier = 2;
        }

        //assign distance multiplier
        if (manhattanDistance(ag, eo) <= 3) {
            distanceMultiplier = 10;
        } else if (manhattanDistance(ag, eo) <= 4) {
            distanceMultiplier = 9;
        } else if (manhattanDistance(ag, eo) <= 5) {
            distanceMultiplier = 8;
        } else if (manhattanDistance(ag, eo) <= 6) {
            distanceMultiplier = 7;
        } else if (manhattanDistance(ag, eo) <= 7) {
            distanceMultiplier = 6;
        } else {
            distanceMultiplier = 5;
        }

        return foxesMultiplier * distanceMultiplier * characterMultiplier;
    }
}
