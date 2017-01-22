/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import java.util.ArrayList;
import java.util.Iterator;
import rabbitsvsfoxes.Agent.Agent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import rabbitsvsfoxes.Agent.FoxAgent;
import rabbitsvsfoxes.Agent.RabbitAgent;

/**
 *
 * @author Georgi
 */
public class Environment {

    private Set<Agent> agents;
    private Set<EnvironmentObject> envObjects;
    private ArrayList<Carrot> carrots;
    private int size;
    private RVFGui gui;

    public Environment(int size, int r, int f, int c, int b, RVFGui gui) {
        this.size = size;
        this.agents = new LinkedHashSet();
        this.envObjects = new LinkedHashSet();
        this.carrots = new ArrayList<>();
        this.gui = gui;

        int newX, newY;
        for (int i = 0; i < c; i++) {//create carrots
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            this.addEnvironmentObject(new Carrot(newX, newY));
        }
        for (int i = 0; i < b; i++) {//create bombs
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            this.addEnvironmentObject(new Bomb(newX, newY));
        }
        for (int i = 0; i < r; i++) {//create rabbits
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            RabbitAgent rabbit = new RabbitAgent(newX, newY, this);
            this.addEnvironmentObject(rabbit);
        }
        for (int i = 0; i < f; i++) {//create foxes
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            FoxAgent fox = new FoxAgent(newX, newY, this);
            this.addEnvironmentObject(fox);
        }

    }

    public void addAgent(Agent a) {
        addEnvironmentObject(a);
    }

    public void removeAgent(Agent a) {
        removeEnvironmentObject(a);
    }

    public void addEnvironmentObject(EnvironmentObject eo) {
        envObjects.add(eo);
        if (eo instanceof Agent) {
            Agent a = (Agent) eo;
            if (!agents.contains(a)) {
                agents.add(a);
            }
        } else if (eo instanceof Carrot) {
            Carrot c = (Carrot) eo;
            if (!carrots.contains(c)) {
                carrots.add(c);
            }
        }
    }

    public void removeEnvironmentObject(EnvironmentObject eo) {
        envObjects.remove(eo);
        agents.remove(eo);
        carrots.remove(eo);
    }

    public void step() {
        for (Agent a : getAgents()) {
            if (a.isAlive()) {
                a.makeAStep();
            }
        }
    }
    public void cleanup(){
        ArrayList<EnvironmentObject>toRemove=new ArrayList<>();
        Iterator<EnvironmentObject> iter = getEnvObjects().iterator();
        while (iter.hasNext()) {
            EnvironmentObject eo = iter.next();
            if(!eo.isAlive()){
                iter.remove();
            }
        }
        
    }
    private int randomRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int) (Math.random() * range) + (min <= max ? min : max);
    }

    public EnvironmentObject spaceOccupied(int x, int y) {
        for (EnvironmentObject eo : envObjects) {
            if (eo.getX() == x && eo.getY() == y) {
                return eo;
            }
        }
        return null;
    }

    public RVFGui getGui() {
        return gui;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Agent> getAgents() {
        return agents;
    }

    public Set<EnvironmentObject> getEnvObjects() {
        return envObjects;
    }

    public ArrayList<Carrot> getCarrots() {
        return carrots;
    }

}
