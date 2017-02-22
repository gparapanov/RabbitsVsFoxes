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
    
    public void addBroadcastedMessage(Message m){
        groupMessages.add(m);
    }
    
    
}
