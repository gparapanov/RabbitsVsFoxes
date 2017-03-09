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

    public Message(MessageType type) {
        this.msgType = type;
    }
    /**
     * A constructor for a message containing only message type and the target
     * object.
     * @param type 
     * @param obj 
     */
    public Message(MessageType type, EnvironmentObject obj) {
        this.msgType = type;
        this.targetObject = obj;
    }
    /**
     * 
     * @param type
     * @param obj
     * @param d The direction in which the target object is moving.
     */
    public Message(MessageType type, EnvironmentObject obj, Direction d) {
        this.msgType = type;
        this.targetObject=obj;
        this.directionToTarget=d;
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

    public Color getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    
    
}
