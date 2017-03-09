/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes.Agent;

import rabbitsvsfoxes.Agent.Agent;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Communication.MessageType;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Goals.CatchRabbit;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Goals.Explore;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class FoxAgent extends Agent {

    public FoxAgent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y, env, mg);
        this.setIcon(new ImageIcon("images/fox2.png", "Fox icon"));
    }

    @Override
    public void findGoal() {
        System.out.println("my agenda contains: ");
        for (Goal g : agenda.getTasks()) {

            if (g instanceof CatchRabbit) {
                System.out.println("a rabbit with priority: " + g.getPriority());
            } else if (g instanceof Explore) {
                System.out.println("exploration ");
            }

        }

        int minDistance = 1000;
        Goal goal = new CatchRabbit(null);
        int distance = 0;
        if (!objAround.isEmpty()) {//there are objects around
            //System.out.println("there's sth around");
            for (EnvironmentObject eo : objAround) {
                if (eo.isAlive()) {
                    distance = manhattanDistance(this, eo);
                    if (minDistance > distance) {
                        minDistance = distance;
                        goal = new CatchRabbit((RabbitAgent) eo);
                        goal.setTeamColor(myColor);
                        //System.out.println("fox found rabbit" + goal.getGoalObject().getX());
                    }
                }
            }
        } else {
            //System.out.println("no objects around - exploring");
            goal = new Explore(null);
            if (toExplore.isEmpty()) {
                discoverExplorationSpaces();
            }

            for (UnexploredSpace us : toExplore) {
                distance = manhattanDistance(this, us);
                if (distance < minDistance) {
                    minDistance = distance;
                    goal.setGoalObject(us);
                    goal.setPriority(4);
                    //System.out.println("better space found:"+distance);
                }
            }
        }
        //now we need to add the object to the agenda, but make some checks first
        boolean cond1 = goal.getGoalObject() != null;
        boolean cond2 = !agenda.checkExistists(goal);
        boolean cond3 = !(agenda.getTop() instanceof CatchRabbit);
        boolean cond4 = (agenda.getTop() instanceof CatchRabbit)
                && (manhattanDistance(this, agenda.getTop().getGoalObject())
                > manhattanDistance(this, goal.getGoalObject()))
                && agenda.getTop().getPriority() < goal.getPriority();
        if (cond1 && cond2 && (cond3 || cond4)) {
            if (cond4) {
                agenda.removeTop();
            }
            this.addGoal(goal);
            //If the goal is catch a rabbit, then the agent could message other foxes
            //about the rabbit's location
            if (goal instanceof CatchRabbit) {
                // CatchRabbit teamGoal=new CatchRabbit(goal.getGoalObject());
                if (env.getGui().getFoxesTeamwork1()) {
                    //this is teamwork type 1 in which all foxes go after the same  rabbit
                    goal.setPriority(6);
                    Message messageToSend = new Message(MessageType.RequestBackup, goal.getGoalObject());
                    messageToSend.setTeamColor(goal.getTeamColor());
                    myGroup.broadcastMessage(messageToSend);
                    System.out.println("asking for help");
                } else if (env.getGui().getFoxesTeamwork2()) {
                    //this is teamwork type 2, in which a fox asks for an ambush 
                    //for a rabbit it is chasing
                    goal.setPriority(6);
                    RabbitAgent targetAgent = (RabbitAgent) goal.getGoalObject();
                    Direction d = directionToObject(targetAgent);

                    Message messageToSend = new Message(MessageType.RequestAmbush, goal.getGoalObject(), d);
                    myGroup.broadcastMessage(messageToSend);
                    System.out.println("asking for ambush");
                } else if (env.getGui().getFoxesTeamwork3()) {
                    //this is teamwork type 3 in which all foxes help their nearby foxes
                    goal.setPriority(6);
                    Message messageToSend = new Message(MessageType.RequestGroupWork, goal.getGoalObject());
                    myGroup.broadcastMessage(messageToSend);
                    System.out.println("asking for group work");
                }

            }
        }
        //open the postbox to check if there are any messages
        Goal postGoal = openPostbox();
        //if there is a valid message there and the goal is not already in the
        //agenda, then add it and remove other targeted rabbits
        if (postGoal != null ) {
            if (agenda.getTop() != null && postGoal.getPriority() > agenda.getTop().getPriority()) {
                //If the agent has something else to - do remove it -
                //this is done to avoid keeping a reference another rabbit and
                //making sure that the foxes won't go after it unless it can see it.
                agenda.removeTop();
            }
            if(agenda.checkExistists(postGoal)){
                setTeamColor(postGoal.getTeamColor());
            }else{
                this.addGoal(postGoal);
            }
            
            //this.agenda.getTasks().add(postGoal);
            System.out.println("going for help");
        }
        if (!agenda.getTop().getGoalObject().isAlive()) {
            agenda.removeTop();
        }
        if(agenda.getTop()!=null ){
            if(agenda.getTop() instanceof CatchRabbit){
                this.setTeamColor(agenda.getTop().getTeamColor());
            }else{
                this.setTeamColor(myColor);
            }
        }

    }

    public Goal openPostbox() {
        Message newestMessage = myGroup.getMessage(messagesReadIndex);
        if (newestMessage != null) {//there is a valid message there
            messagesReadIndex++;
            if (newestMessage.getMsgType().equals(MessageType.RequestBackup)) {
                System.out.println("someone needs backup I can go and help");
                CatchRabbit teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                teamGoal.setTeamColor(newestMessage.getTeamColor());
                teamGoal.setPriority(6);
                return teamGoal;
            } else if (newestMessage.getMsgType().equals(MessageType.RequestAmbush)) {
                System.out.println("someone has requested an ambush");
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
                if (condition1 || condition2 || condition3 || condition4) {
                    Goal teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                    teamGoal.setPriority(6);
                    return teamGoal;
                }
            } else if (newestMessage.getMsgType().equals(MessageType.RequestGroupWork)) {
                if (diagonalDistance(this, newestMessage.getTargetObject()) <= env.getSize() / 5) {
                    System.out.println("someone nearby needs backup I can go and help");
                    Goal teamGoal = new CatchRabbit(newestMessage.getTargetObject());
                    teamGoal.setPriority(6);
                    return teamGoal;
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

}
