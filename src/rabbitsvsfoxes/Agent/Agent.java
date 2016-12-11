package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Agenda;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Goal;

/**
 *
 * @author Georgi
 */
public class Agent extends EnvironmentObject {
    
    private Agenda agenda;
    
    public Agent(int x, int y) {
        super(x, y);
        agenda=new Agenda();
    }
    public Agent(){
        
    }
    
    public void move(Direction d){
        switch (d){
            case UP:
                setY(getY()-1);
                break;
            case DOWN:
                setY(getY()+1);
                break;
            case LEFT:
                setX(getX()-1);
                break;
            case RIGHT:
                setX(getX()+1);
                break;
            case UPLEFT:
                setY(getY()-1);
                setX(getX()-1);
                break;
            case DOWNLEFT:
                setY(getY()+1);
                setX(getX()-1);
                break;
            case UPRIGHT:
                setX(getX()+1);
                setY(getY()-1);
                break;
            case DOWNRIGHT:
                setX(getX()+1);
                setY(getY()+1);
                break;
        }
    }
    
    private void findGoal(){
        
    }
    
    public void addGoal(Goal g){
        this.agenda.addTask(g);
    }

    public Agenda getAgenda() {
        return agenda;
    }
    
    public void sendMessage(Agent target,Message msg){
        
    }
    
    public void broadcastToAgents(List<Agent> agents, Message msg){
        
    }
    
    public void receiveMessage(Message msg){
        
    }

    @Override
    public String toString() {
        return "Agent{"+this.isAlive() + " "+this.getX()+" , "+this.getY();
    }
    
    
    
   

}
