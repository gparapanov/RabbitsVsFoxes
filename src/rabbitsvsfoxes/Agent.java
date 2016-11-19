package rabbitsvsfoxes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class Agent extends EnvironmentObject {
    
    public Agent(int x, int y) {
        super(x, y);
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

    
    
    
    
   

}
