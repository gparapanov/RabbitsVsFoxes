/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgi
 */
public class Environment {
    private Set<Agent> agents;
    private Set<EnvironmentObject> envObjects;
    private int sizeX,sizeY;

    public Environment(int x, int y) {
        this.sizeX=x;
        this.sizeY=y;
        this.agents = new LinkedHashSet();
        this.envObjects = new LinkedHashSet();
    }
    
    public void addAgent(Agent a){
        addEnvironmentObject(a);
    }
    public void removeAgent(Agent a){
        removeEnvironmentObject(a);
    }
    public void addEnvironmentObject(EnvironmentObject eo){
        
    }
    public void removeEnvironmentObject(EnvironmentObject eo){
        
    }
    public void step(){
        
    }
}
