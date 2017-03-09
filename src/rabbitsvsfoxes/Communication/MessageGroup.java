package rabbitsvsfoxes.Communication;

import java.util.ArrayList;
import java.util.Iterator;
import rabbitsvsfoxes.Agent.Agent;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class MessageGroup {
    private ArrayList<Agent> members;
    private ArrayList<Message>groupMessages;
    private ArrayList<EnvironmentObject>claimedCarrots;

    public MessageGroup() {
        this.members = new ArrayList<>();
        this.groupMessages = new ArrayList<>();
        this.claimedCarrots = new ArrayList<>();
    }
    
    public void addMember(Agent a){
        members.add(a);
    }
    
    public void broadcastMessage(Message m){
        if(m.getMsgType()==MessageType.ClaimCarrot){
            claimedCarrots.add(m.getTargetObject());
        }else if(m.getMsgType()==MessageType.UnclaimCarrot){
            claimedCarrots.remove(m.getTargetObject());
        }else{
            groupMessages.add(m);
        }
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
    
    public boolean checkCarrotClaimed(EnvironmentObject eo){
        return this.claimedCarrots.contains(eo);
    }
    
    public void removeEatenCarrots(){
        Iterator<EnvironmentObject> iter = claimedCarrots.iterator();
        while (iter.hasNext()) {
            EnvironmentObject eo = iter.next();
            if (!eo.isAlive()) {
                iter.remove();
            }
        }
    }
    
}
