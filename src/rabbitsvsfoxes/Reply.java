/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import java.util.List;

/**
 *
 * @author Georgi
 */
public class Reply extends Message {
    
    public Reply(MessageType type) {
        super(type);
    }
    
    
    public List<Agent> agentsAround(){
        return null;
        
    }
    public List<EnvironmentObject> objectsAround(){
        return null;
        
    }
}
