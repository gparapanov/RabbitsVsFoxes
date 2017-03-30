package rabbitsvsfoxes.Communication;

import java.awt.Color;
import rabbitsvsfoxes.Agent.Agent;
import rabbitsvsfoxes.Agent.RabbitAgent;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Goals.Goal;

/**
 *
 * @author Georgi
 */
public class Message {
    protected MessageType msgType;
    protected EnvironmentObject targetObject;
    protected Direction directionToTarget;
    protected Color teamColor;
    protected Agent sender;

    public Message(MessageType type) {
        this.msgType = type;
    }
    /**
     * A constructor for a message containing only message type, the target
     * object and name of the sender.
     * @param type 
     * @param obj 
     * @param sender Sender agent.
     */
    public Message(MessageType type, EnvironmentObject obj, Agent sender) {
        this.msgType = type;
        this.targetObject = obj;
        this.sender=sender;
    }
    /**
     * 
     * @param type
     * @param obj
     * @param d
     * @param sender Sender.
     */
    public Message(MessageType type, EnvironmentObject obj, Direction d, Agent sender) {
        this.msgType = type;
        this.targetObject=obj;
        this.directionToTarget=d;
        this.sender=sender;
    }
    public MessageType getMsgType() {
        return msgType;
    }
    public Direction getDirectionToTarget() {
        return directionToTarget;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public EnvironmentObject getTargetObject() {
        return targetObject;
    }

    public Agent getSender() {
        return sender;
    }

    public void setSender(Agent sender) {
        this.sender = sender;
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    
    
}
