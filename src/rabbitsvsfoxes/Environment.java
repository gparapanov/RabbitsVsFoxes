/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Objects.Bomb;
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
    private ArrayList<Bomb> bombs;

    /*
     This is an array of names and each agent is assigned one.
     */
    private String[] names = {"Donald", "Nick", "John", "Mick", "Peter", "Albus",
        "Joe", "Francis", "Logan", "Ryan", "Jerry", "Jeb", "Harry", "Chris", "Ted", "Barry",
        "Mason", "Ben", "Rey", "Fin", "Luke", "Poe", "Han", "Paul", "Vin", "Adam", "Chad",
        "Jeff", "Rick", "Bernie", "Jimmy", "Jesus", "Bruce", "Bill", "Ian", "Pip", "Ron"};
    private int rabbitNameIndex = 0;
    private int foxNameIndex = names.length - 1;

    private int size;
    private RVFGui gui;
    private final int initialCarrots;
    private MessageGroup foxesGroup;
    private MessageGroup rabbitsGroup;
    private char[][] world;

    public Environment(int size, int r, int f, int c, int b, RVFGui gui) {
        this.size = size;
        this.agents = new LinkedHashSet();
        this.envObjects = new LinkedHashSet();
        this.carrots = new ArrayList<>();
        this.gui = gui;
        this.initialCarrots = c;
        this.foxesGroup = new MessageGroup();
        this.rabbitsGroup = new MessageGroup();
        this.world = new char[size][size];

        populateMap(r, f, c, b);//creates agents, carrots, bombs in the env
    }

    private void populateMap(int r, int f, int c, int b) {
        for (int y = 0; y < world.length; y++) {
            for (int x = 0; x < world[0].length; x++) {
                world[y][x] = '0';
            }
        }
        
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
            world[newY][newX] = 'C';
        }
        for (int i = 0; i < b; i++) {//create bombs
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY) != null);
            this.addEnvironmentObject(new Bomb(newX, newY));
            world[newY][newX] = 'B';
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
            world[newY][newX] = 'R';
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
            world[newY][newX] = 'F';
        }
        System.out.println(envObjects.toString());
    }
    public void refreshArrayMap(){
        for (int y = 0; y < world.length; y++) {
            for (int x = 0; x < world[0].length; x++) {
                world[y][x] = '0';
            }
        }
        for(EnvironmentObject eo:envObjects){
            if(eo instanceof RabbitAgent){
                world[eo.getY()][eo.getX()]='R';
            }else if(eo instanceof FoxAgent){
                world[eo.getY()][eo.getX()]='F';
            }else if(eo instanceof Carrot){
                world[eo.getY()][eo.getX()]='C';
            }else if(eo instanceof Bomb){
                world[eo.getY()][eo.getX()]='B';
            }
        }
    }

    public void addAgent(Agent a) {
        addEnvironmentObject(a);
    }

    public void removeAgent(Agent a) {
        removeEnvironmentObject(a);
    }

    /**
     * Method, which gives a name to an agent. Foxes are assigned names starting
     * from the last name of the array of names, whereas rabbits are assigned
     * ones from the beginning.
     *
     * @param ag The agent that requests a name.
     * @return Name for that agent.
     */
    public String getName(Agent ag) {
        if (ag instanceof FoxAgent) {
            if (foxNameIndex < 0) {
                foxNameIndex = names.length - 1;
            }
            return names[foxNameIndex--];
        } else {
            if (rabbitNameIndex >= names.length) {
                rabbitNameIndex = 0;
            }
            return names[rabbitNameIndex++];
        }
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
            refreshArrayMap();
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

    public char[][] getWorld() {
        return world;
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

    @Override
    public String toString() {
        String result = "";

        for (int y = 0; y < this.world.length; y++) //loop through the rows
        {
            for (int x = 0; x < this.world[y].length; x++) //loop through the columns in each row
            {

                result += this.world[y][x];		//barrier is an X

            }
            result += "\n";				//add a newline at the end of each row
        }
        return result;
    }
}
