/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import rabbitsvsfoxes.Agent.Agent;
import javax.swing.ImageIcon;
import static rabbitsvsfoxes.Agent.Agent.manhattanDistance;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Communication.MessageType;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Goals.CatchRabbit;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Goals.Explore;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class FoxAgent extends Agent {

    private String currentTarget = "";

    public FoxAgent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y, env, mg);
        this.setIcon(new ImageIcon("images/fox2.png", "Fox icon"));
    }
    
    /**
     * A constructor for agents with limited functionality. This constructor is
     * only for dummy foxes that represent the last seen location of a real fox.
     * @param x
     * @param y
     * @param name 
     */
    public FoxAgent(int x, int y, String name) {
        this.setX(x);
        this.setY(y);
        this.myName=name;
    }

    @Override
    public void findGoal() {
        double maxUtility = 0, utility = 0;
        Goal goal = new CatchRabbit(null);
        if (!objAround.isEmpty()) {//there are rabbits around
            //System.out.println("there's sth around");
            for (EnvironmentObject eo : objAround) {
                if (eo.isAlive()) {
                    utility = evaluationFunctionCatchRabbits(this, eo);
                    if (maxUtility < utility) {
                        maxUtility = utility;
                        goal = new CatchRabbit((RabbitAgent) eo);
                        goal.setTeamColor(myColor);
                        //System.out.println("fox found rabbit" + goal.getGoalObject().getX());
                    }
                }
            }
        } else {
            //explore
            //System.out.println("no objects around - exploring");
            goal = new Explore(null);
            if (toExplore.isEmpty()) {
                discoverExplorationSpaces();
            }

            for (UnexploredSpace us : toExplore) {
                utility = evaluationFunctionExplore(this, us);
                if (utility > maxUtility) {
                    maxUtility = utility;
                    goal.setGoalObject(us);
                    goal.setPriority(4);
                    goal.setTeamColor(myColor);
                    //System.out.println("better space found:"+distance);
                }
            }
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
            if (postGoal.getUtility() > goal.getUtility()
                    || compareObjects(postGoal.getGoalObject(), goal.getGoalObject())) {
                goal = postGoal;
                env.getGui().writeLogToGui("Fox: "+myName+ " going to help its teammate "+lastMessageRead.getSender().getName() + " catch the rabbit " + ((Agent) lastMessageRead.getTargetObject()).getName() + "!");
                lastLogs.add(0, lastMessageRead.getSender().getName() + " wants backup, going to help him catch " + ((Agent) lastMessageRead.getTargetObject()).getName() + "!");
            } else {
                lastLogs.add(0, lastMessageRead.getSender().getName() + " is asking for help, but I prefer to work on my own!");
            }

            //this.agenda.getTasks().add(postGoal);
            //System.out.println("going for help");
        }

        //now we need to add the object to the agenda, but make some checks first
        boolean cond1 = goal.getGoalObject() != null;
        boolean cond2 = !agenda.checkExistists(goal);
        boolean cond3 = !(agenda.getTop() instanceof CatchRabbit);
        boolean cond4 = (agenda.getTop() instanceof CatchRabbit)
                && (manhattanDistance(this, agenda.getTop().getGoalObject())
                >= manhattanDistance(this, goal.getGoalObject()))
                && agenda.getTop().getPriority() <= goal.getPriority();
        if (cond1 && cond2 && (cond3 || cond4)) {
            if (cond4) {
                agenda.removeTop();
            }

            //If the goal is catch a rabbit, then the agent could message other foxes
            //about the rabbit's location
            if (goal instanceof CatchRabbit) {
                lastLogs.add(0, "Saw " + ((Agent) goal.getGoalObject()).getName() + ", going after it!");
                env.getGui().writeLogToGui("Fox: "+myName + " just spotted the rabbit "+((RabbitAgent)goal.getGoalObject()).getName()+ " at x:"+goal.getGoalObject().getX()+" y:"+goal.getGoalObject().getY());
                this.addGoal(goal);
                // CatchRabbit teamGoal=new CatchRabbit(goal.getGoalObject());
                if (env.getGui().getFoxesTeamwork1()) {
                    //this is teamwork type 1 in which all foxes go after the same  rabbit
                    goal.setPriority(6);
                    Message messageToSend = new Message(MessageType.RequestBackup, goal.getGoalObject(), this);
                    messageToSend.setTeamColor(goal.getTeamColor());
                    myGroup.broadcastMessage(messageToSend);
                    //System.out.println("asking for help");
                    env.getGui().writeLogToGui("Fox: "+myName + " is asking for help from all foxes to catch "+((RabbitAgent)goal.getGoalObject()).getName()+"at x:"+goal.getGoalObject().getX()+" y:"+goal.getGoalObject().getY());
                    lastLogs.add(0, "Asking for help from all my friends!");
                } else if (env.getGui().getFoxesTeamwork2()) {
                    //this is teamwork type 2, in which a fox asks for an ambush 
                    //for a rabbit it is chasing
                    goal.setPriority(6);
                    RabbitAgent targetAgent = (RabbitAgent) goal.getGoalObject();
                    Direction d = directionToObject(targetAgent);

                    Message messageToSend = new Message(MessageType.RequestAmbush, goal.getGoalObject(), d, this);
                    messageToSend.setTeamColor(goal.getTeamColor());
                    myGroup.broadcastMessage(messageToSend);
                    //System.out.println("asking for ambush");
                    lastLogs.add(0, "Asking for ambush!");
                    env.getGui().writeLogToGui("Fox: "+myName + " is asking for ambush to catch "+((RabbitAgent)goal.getGoalObject()).getName()+"at x:"+goal.getGoalObject().getX()+" y:"+goal.getGoalObject().getY());
                } else if (env.getGui().getFoxesTeamwork3()) {
                    //this is teamwork type 3 in which all foxes help their nearby foxes
                    // if (agenda.getTop().getPriority() != 6) {
                    goal.setPriority(6);
                    Message messageToSend = new Message(MessageType.RequestGroupWork, goal.getGoalObject(), this);
                    messageToSend.setTeamColor(goal.getTeamColor());
                    myGroup.broadcastMessage(messageToSend);
                    //System.out.println("asking for group work");
                    lastLogs.add(0, "Requesting help for " + ((Agent) goal.getGoalObject()).getName() + " from foxes nearby!");
                    env.getGui().writeLogToGui("Fox: "+myName + " is asking for help from foxes nearby to catch "+((RabbitAgent)goal.getGoalObject()).getName()+"at x:"+goal.getGoalObject().getX()+" y:"+goal.getGoalObject().getY());
                    //}

                }

            } else if (goal instanceof Explore) {
                lastLogs.add(0, "There is nothing around me, I will explore!");
                env.getGui().writeLogToGui("Fox: " + getName() + " does not detect anything around..... going to explore!");
                this.addGoal(goal);
            }
        }

        if (!agenda.getTop().getGoalObject().isAlive()) {
            agenda.removeTop();
        }
        if (agenda.getTop() instanceof CatchRabbit) {
            currentTarget = ((RabbitAgent) agenda.getTop().getGoalObject()).getName();
        } else {
            currentTarget = "---";
        }

    }

    @Override
    public Goal openPostbox() {
        Message newestMessage = myGroup.getMessage(messagesReadIndex);
        if (newestMessage != null) {//there is a valid message there
            messagesReadIndex++;
            if (!newestMessage.getSender().getName().equals(myName)) {
                lastMessageRead = newestMessage;
                if (newestMessage.getMsgType().equals(MessageType.RequestBackup)) {
                //System.out.println("someone needs backup I can go and help");
                    //lastLogs.add(0,"Someone needs backup, going for help!");
                    CatchRabbit teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                    teamGoal.setTeamColor(newestMessage.getTeamColor());
                    teamGoal.setPriority(6);
                    teamGoal.setUtility(evaluationFunctionTeamwork(this, newestMessage.getSender(), (Agent) newestMessage.getTargetObject()));

                    return teamGoal;
                } else if (newestMessage.getMsgType().equals(MessageType.RequestAmbush)) {
                    /*
                     Conditions that need to be satisfied in order for the ambush to work.
                     E.g. if the sender of the message fox is chasing a rabbit and 
                     they are both going to the right, the recipient has to be
                     on their right so that it can go left to catch the rabbit. The
                     effect will be both foxes going in opposite direction, thus leaving
                     less space for the rabbit to run away.
                     */
                    boolean condition1 = newestMessage.getDirectionToTarget() == Direction.RIGHT
                            && directionToObject(newestMessage.getTargetObject()) == Direction.LEFT;
                    boolean condition2 = newestMessage.getDirectionToTarget() == Direction.LEFT
                            && directionToObject(newestMessage.getTargetObject()) == Direction.RIGHT;
                    boolean condition3 = newestMessage.getDirectionToTarget() == Direction.UP
                            && directionToObject(newestMessage.getTargetObject()) == Direction.DOWN;
                    boolean condition4 = newestMessage.getDirectionToTarget() == Direction.DOWN
                            && directionToObject(newestMessage.getTargetObject()) == Direction.UP;
                    boolean condition5 = diagonalDistance(this, newestMessage.getTargetObject()) <= env.getSize() / 3;
                    if ((condition1 || condition2 || condition3 || condition4) && condition5) {
                        Goal teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                        teamGoal.setPriority(6);
                        teamGoal.setTeamColor(newestMessage.getTeamColor());
                        teamGoal.setUtility(evaluationFunctionTeamwork(this, newestMessage.getSender(), (Agent) newestMessage.getTargetObject()));

                        lastLogs.add(0, newestMessage.getSender().getName() + " requested ambush, going for help!");

                        return teamGoal;
                    } else {
                        lastLogs.add(0, newestMessage.getSender().getName() + " requested ambush, but I'm not suitable for that!");
                    }
                } else if (newestMessage.getMsgType().equals(MessageType.RequestGroupWork)) {
                    if (diagonalDistance(this, newestMessage.getTargetObject()) <= env.getSize() / 4) {
                        // System.out.println("someone nearby needs backup I can go and help");
                        if (!newestMessage.getSender().getName().equals(myName)) {
                            lastLogs.add(0, newestMessage.getSender().getName() + " nearby needs help, I'm going there!");
                        }

                        Goal teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                        teamGoal.setPriority(6);
                        teamGoal.setTeamColor(newestMessage.getTeamColor());
                        teamGoal.setUtility(evaluationFunctionTeamwork(this, newestMessage.getSender(), (Agent) newestMessage.getTargetObject()));

                        return teamGoal;
                    } else {
                        lastLogs.add(0, newestMessage.getSender().getName() + " needs help, but I am too far away!");
                    }
                }
            }

        }

        return null;
    }

    public Direction directionToObject(EnvironmentObject targetAgent) {
        int differenceX = Math.abs(targetAgent.getX() - this.getX());
        int differenceY = Math.abs(targetAgent.getY() - this.getY());
        //figure out in which direction is the chase going
        if (differenceX >= differenceY) {
            if (targetAgent.getX() > this.getX()) {
                return Direction.RIGHT;
            } else if (targetAgent.getX() < this.getX()) {
                return Direction.LEFT;
            } else if (targetAgent.getY() < this.getY()) {
                return Direction.UP;
            } else {
                return Direction.DOWN;
            }
        } else {
            if (targetAgent.getY() < this.getY()) {
                return Direction.UP;
            } else if (targetAgent.getY() > this.getY()) {
                return Direction.DOWN;
            } else if (targetAgent.getX() > this.getX()) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        }

    }

    //evaluation function for catching rabbits
    public double evaluationFunctionCatchRabbits(Agent ag, EnvironmentObject eo) {
        int radius = (diagonalDistance(ag, eo) == 1 ? 2 : diagonalDistance(ag, eo));
        double distanceMultiplier,
                characterMultiplier = (agentCharacter < characterSeparator) ? 0.7 : 1.3;//

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

        return distanceMultiplier * characterMultiplier;
    }

    public double evaluationFunctionExplore(Agent ag, EnvironmentObject eo) {
        double distanceMultiplier,
                characterMultiplier = (agentCharacter < characterSeparator) ? 0.7 : 1.3;
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
        return distanceMultiplier * characterMultiplier;
    }

    /**
     *
     * @param ag Me
     * @param requester The one who seeks help
     * @param target Target object of the message (e.g. rabbit)
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

    public String getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(String currentTarget) {
        this.currentTarget = currentTarget;
    }

    @Override
    public void lookAround(int radius) {
        objAround.clear();
        for (Agent envObj : env.getAgents()) {
            if (envObj instanceof RabbitAgent && envObj.isAlive() && envObj.getX() >= (this.getX() - radius)
                    && envObj.getX() <= (this.getX() + radius) && envObj.getY() >= (this.getY() - radius)
                    && envObj.getY() <= (this.getY() + radius)) {
                objAround.add(envObj);
            }
        }
    }

    @Override
    public String toString() {
        String output = "Target: " + currentTarget
                + "<br>" + super.toString();
        return output;
    }

}
