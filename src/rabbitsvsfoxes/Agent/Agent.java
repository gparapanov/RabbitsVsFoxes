package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Agent.Communication.Message;

/**
 *
 * @author Georgi
 */
public class Agent extends EnvironmentObject {
    
    public Agent(int x, int y) {
        super(x, y);
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
        }
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
