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
        super(x, y, env,mg);
        this.setIcon(new ImageIcon("images/fox (1).png", "Fox icon"));
    }

    @Override
    public void findGoal() {
        System.out.println("my agenda contains: ");
        for(Goal g:agenda.getTasks()){
            
            if(g instanceof CatchRabbit){
                System.out.println("a rabbit with priority: "+g.getPriority());
            }else if(g instanceof Explore){
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
        if (goal.getGoalObject() != null && !agenda.checkExistists(goal)
                &&!(agenda.getTop() instanceof CatchRabbit)) {
            
            //&& agenda.getTop()!=null && !(agenda.getTop() instanceof CatchRabbit) add this up
            this.addGoal(goal);
            //If the goal is catch a rabbit, then the agent could message other foxes
            //about the rabbit's location
            if(goal instanceof CatchRabbit && env.getGui().getFoxesTeamwork()){
               // CatchRabbit teamGoal=new CatchRabbit(goal.getGoalObject());
                goal.setPriority(6);
                Message messageToSend=new Message(MessageType.RequestBackup,goal.getGoalObject());
                myGroup.broadcastMessage(messageToSend);
                System.out.println("asking for help");
            }
        }
        //open the postbox to check if there are any messages
        Goal postGoal=openPostbox();
        //if there is a valid message there and the goal is not already in the
        //agenda, then add it and remove other targeted rabbits
        if(postGoal!=null && !agenda.checkExistists(postGoal)){
            
            //this.addGoal(postGoal);
            if(agenda.getTop() !=null){
                agenda.removeTop();
            }
            this.agenda.getTasks().add(postGoal);
            System.out.println("going for help");
        }
        if(!agenda.getTop().getGoalObject().isAlive()){
            agenda.removeTop();
        }
            
    }

    public Goal openPostbox(){
        Message newestMessage=myGroup.getMessage(messagesReadIndex);
        if(newestMessage!=null){//there is a valid message there
            messagesReadIndex++;
            if(newestMessage.getMsgType().equals(MessageType.RequestBackup)){
                System.out.println("someone needs backup");
                Goal teamGoal=new CatchRabbit(newestMessage.getTargetObject());
                teamGoal.setPriority(6);
                return teamGoal;
            }
        }
        return null;
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
