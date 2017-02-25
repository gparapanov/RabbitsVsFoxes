package rabbitsvsfoxes.Communication;

import java.util.ArrayList;
import rabbitsvsfoxes.Agent.Agent;

/**
 *
 * @author Georgi
 */
public class MessageGroup {
    private ArrayList<Agent> members;
    private ArrayList<Message>groupMessages;

    public MessageGroup() {
        this.members = new ArrayList<>();
        this.groupMessages = new ArrayList<>();
    }
    
    public void addMember(Agent a){
        members.add(a);
    }
    
    public void broadcastMessage(Message m){
        groupMessages.add(m);
    }
    /**
     * Method returns a Message object at a specified position (index) if it exists,
     * or a null object if there is no such message.
     * 
     * @param index
     * @return 
     */
    public Message getMessage(int index){
        if(index<groupMessages.size()){
            return groupMessages.get(index);
        }else{
            return null;
        }
    }
    
    
}
