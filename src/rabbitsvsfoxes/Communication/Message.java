package rabbitsvsfoxes.Communication;

import java.awt.Color;
import rabbitsvsfoxes.Agent.RabbitAgent;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.EnvironmentObject;
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
    protected String senderName;

    public Message(MessageType type) {
        this.msgType = type;
    }
    /**
     * A constructor for a message containing only message type, the target
     * object and name of the sender.
     * @param type 
     * @param obj 
     * @param name Name of the sender.
     */
    public Message(MessageType type, EnvironmentObject obj, String name) {
        this.msgType = type;
        this.targetObject = obj;
        this.senderName=name;
    }
    /**
     * 
     * @param type
     * @param obj
     * @param d
     * @param name Name of sender.
     */
    public Message(MessageType type, EnvironmentObject obj, Direction d, String name) {
        this.msgType = type;
        this.targetObject=obj;
        this.directionToTarget=d;
        this.senderName=name;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    
    
}
