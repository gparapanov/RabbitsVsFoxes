package rabbitsvsfoxes.Communication;

import rabbitsvsfoxes.Agent.RabbitAgent;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Goals.Goal;

/**
 *
 * @author Georgi
 */
public class Message {
    protected MessageType msgType;
    protected EnvironmentObject targetObject;

    public Message(MessageType type) {
        this.msgType = type;
    }
    
    public Message(MessageType type, EnvironmentObject obj) {
        this.msgType = type;
        this.targetObject=obj;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public EnvironmentObject getTargetObject() {
        return targetObject;
    }

    
    
}
