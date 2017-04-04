package rabbitsvsfoxes.Communication;

import java.util.ArrayList;
import java.util.Iterator;
import rabbitsvsfoxes.Agent.Agent;
import rabbitsvsfoxes.Agent.FoxAgent;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class MessageGroup {

    private ArrayList<Agent> members;
    private ArrayList<Message> groupMessages;
    private ArrayList<EnvironmentObject> claimedCarrots;
    private ArrayList<FoxAgent> foxesBeingDistracted;

    public MessageGroup() {
        this.members = new ArrayList<>();
        this.groupMessages = new ArrayList<>();
        this.claimedCarrots = new ArrayList<>();
        this.foxesBeingDistracted = new ArrayList<>();
    }

    public void addMember(Agent a) {
        members.add(a);
    }

    public void broadcastMessage(Message m) {
        if (m.getMsgType() == MessageType.ClaimCarrot) {
            Carrot toClaim = (Carrot) (m.getTargetObject());
            claimedCarrots.add(toClaim);
            toClaim.claim(m.getSender().getName());
        } else if (m.getMsgType() == MessageType.UnclaimCarrot) {
            claimedCarrots.remove(m.getTargetObject());
        } else if (m.getMsgType() == MessageType.EngageInDistraction) {
            foxesBeingDistracted.add((FoxAgent) m.getTargetObject());
        } else if (m.getMsgType() == MessageType.DisengageInDistraction) {
            foxesBeingDistracted.remove((FoxAgent) m.getTargetObject());
        } else if (m.getMsgType() == MessageType.UnclaimAllCarrots) {
            Iterator<EnvironmentObject> iter = claimedCarrots.iterator();
            while (iter.hasNext()) {
                Carrot ca = (Carrot)iter.next();
                if (ca.getClaimedBy().equals(m.getSender().getName())) {
                    ca.unClaim();
                    iter.remove();
                }
            }
        } else {
            groupMessages.add(m);
        }
    }

    /**
     * Method returns a Message object at a specified position (index) if it
     * exists, or a null object if there is no such message.
     *
     * @param index
     * @return
     */
    public Message getMessage(int index) {
        if (index < groupMessages.size()) {
            return groupMessages.get(index);
        } else {
            return null;
        }
    }

    public boolean checkCarrotClaimed(EnvironmentObject eo) {
        return this.claimedCarrots.contains(eo);
    }

    public boolean checkFoxBeingDistracted(EnvironmentObject eo) {
        return this.foxesBeingDistracted.contains((FoxAgent) eo);
    }

    public void removeEatenCarrots() {
        Iterator<EnvironmentObject> iter = claimedCarrots.iterator();
        while (iter.hasNext()) {
            EnvironmentObject eo = iter.next();
            if (!eo.isAlive()) {
                iter.remove();
            }
        }
    }

}
