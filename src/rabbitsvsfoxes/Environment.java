/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import rabbitsvsfoxes.Agent.Agent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import rabbitsvsfoxes.Agent.FoxAgent;
import rabbitsvsfoxes.Agent.RabbitAgent;
import rabbitsvsfoxes.Communication.*;

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
    private final int initialCarrots;
    private MessageGroup foxesGroup;
    private MessageGroup rabbitsGroup;

    public Environment(int size, int r, int f, int c, int b, RVFGui gui) {
        this.size = size;
        this.agents = new LinkedHashSet();
        this.envObjects = new LinkedHashSet();
        this.carrots = new ArrayList<>();
        this.gui = gui;
        this.initialCarrots = c;
        this.foxesGroup = new MessageGroup();
        this.rabbitsGroup = new MessageGroup();

        populateMap(r, f, c, b);//creates agents, carrots, bombs in the env
    }

    private void populateMap(int r, int f, int c, int b) {
        int newX, newY;
        Random random = new Random();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
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
            RabbitAgent rabbit = new RabbitAgent(newX, newY, this, rabbitsGroup);
            
            final float hue = random.nextFloat();
            rabbit.setMyColor(Color.getHSBColor(hue, saturation, luminance));
            
            this.addEnvironmentObject(rabbit);
        }
        for (int i = 0; i < f; i++) {//create foxes
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            FoxAgent fox = new FoxAgent(newX, newY, this, foxesGroup);
            final float hue = random.nextFloat();
            fox.setMyColor(Color.getHSBColor(hue, saturation, luminance));
            this.addEnvironmentObject(fox);
        }
        System.out.println(envObjects.toString());
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
            // if (!agents.contains(a)) {
            agents.add(a);
            if (a instanceof RabbitAgent) {
                rabbitsGroup.addMember(a);
            } else {
                foxesGroup.addMember(a);
            }
            //}
        } else if (eo instanceof Carrot) {
            Carrot c = (Carrot) eo;
            //if (!carrots.contains(c)) {
            carrots.add(c);
            //}
        }
    }

    public void removeEnvironmentObject(EnvironmentObject eo) {
        envObjects.remove(eo);
        //agents.remove(eo);
        carrots.remove(eo);
    }

    public void step() {
        for (Agent a : getAgents()) {
            a.makeAStep();
        }
        if (this.getGui().getCarrotsRegenCheck()) {
            regenerateCarrots();
        }
        cleanup();
    }

    private void regenerateCarrots() {
        int newX, newY;
        for (int i = carrots.size(); i < initialCarrots; i++) {//create carrots
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            this.addEnvironmentObject(new Carrot(newX, newY));
        }
    }

    public void cleanup() {//remove dead agents from the collection
        Iterator<Agent> iter = getAgents().iterator();
        while (iter.hasNext()) {
            Agent eo = iter.next();
            if (!eo.isAlive()) {
                iter.remove();
            }
        }
        rabbitsGroup.removeEatenCarrots();

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
