/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

/**
 *
 * @author Georgi
 */
public abstract class Message {
    protected MessageType msgType;

    public Message(MessageType type) {
        this.msgType = type;
    }
    
}
