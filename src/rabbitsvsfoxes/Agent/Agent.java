package rabbitsvsfoxes.Agent;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Agenda;
import rabbitsvsfoxes.Carrot;
import rabbitsvsfoxes.Goals.CatchRabbit;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.EnvironmentObject;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Goals.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.Goals.Explore;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class Agent extends EnvironmentObject {

    private final int radius = 7;

    protected Agenda agenda;
    protected Environment env;
    protected ArrayList<EnvironmentObject> objAround;
    protected ArrayList<UnexploredSpace> toExplore;
    protected MessageGroup myGroup;
    protected int messagesReadIndex=0;

    public Agent(int x, int y, Environment env,MessageGroup mg) {
        super(x, y);
        this.env = env;
        this.agenda = new Agenda();
        this.objAround = new ArrayList<>();
        this.toExplore = new ArrayList<>();
        myGroup=mg;
        discoverExplorationSpaces();
    }

    public Agent() {

    }

    protected void discoverExplorationSpaces() {
        for (int i = radius; i < env.getSize(); i += (radius * 2) + 1) {
            for (int j = radius; j < env.getSize(); j += (radius * 2) + 1) {
                toExplore.add(new UnexploredSpace(i, j));
                //System.out.println("will explore x:"+i+",y:"+j);
            }
        }
        //System.out.println(toExplore.toString());
    }

    public void move(Direction d) {
        switch (d) {
            case UP:
                setY(getY() - 1);
                break;
            case DOWN:
                setY(getY() + 1);
                break;
            case LEFT:
                setX(getX() - 1);
                break;
            case RIGHT:
                setX(getX() + 1);
                break;
            case UPLEFT:
                setY(getY() - 1);
                setX(getX() - 1);
                break;
            case DOWNLEFT:
                setY(getY() + 1);
                setX(getX() - 1);
                break;
            case UPRIGHT:
                setX(getX() + 1);
                setY(getY() - 1);
                break;
            case DOWNRIGHT:
                setX(getX() + 1);
                setY(getY() + 1);
                break;
        }
    }

    public int manhattanDistance(EnvironmentObject eo1, EnvironmentObject eo2) {
        //calculates manhattan distance between agent and objects nearby
        return Math.abs(eo2.getX() - eo1.getX()) + Math.abs(eo2.getY() - eo1.getY());
    }

    public int diagonalDistance(EnvironmentObject eo1, EnvironmentObject eo2) {
        int xRad = Math.abs(eo1.getX() - eo2.getX());
        int yRad = Math.abs(eo1.getY() - eo2.getY());
        return Math.max(xRad, yRad);
    }

    public int evaluationFunction(Agent ag, EnvironmentObject eo) {
        return manhattanDistance(ag, eo);
    }

    public void findGoal() {

    }

    public void lookAround(int radius) {

    }

    public void makeAStep() {
        lookAround(radius);
        findGoal();
        if (agenda.getTop() != null) {
            moveTowardsGoal(agenda.getTop());
        } else {
            //System.out.println("Game Over!");
        }

    }

    public void moveTowardsGoal(Goal g) {
        int goalX = g.getGoalObject().getX();
        int goalY = g.getGoalObject().getY();
        int differenceX = Math.abs(goalX - getX());
        int differenceY = Math.abs(goalY - getY());
        EnvironmentObject desiredObject;
        if (this instanceof FoxAgent) {
            desiredObject = new RabbitAgent();
            //System.out.println("fox roing to:" + goalX + " " + goalY);
        } else {
            desiredObject = new Carrot();
            //System.out.println("rabbit roing to:" + goalX + " " + goalY);
        }
        if (differenceX == 1 && differenceY == 1) {
            if (goalX > getX() && goalY > getY()) {
                move(Direction.DOWNRIGHT);
            } else if (goalX > getX() && goalY < getY()) {
                move(Direction.UPRIGHT);
            } else if (goalX < getX() && goalY > getY()) {
                move(Direction.DOWNLEFT);
            } else {
                move(Direction.UPLEFT);
            }
        } else if (differenceX > differenceY) {//avoids 'dancing'
            if (goalX > getX()) {//if goal is on the right
                EnvironmentObject neighbour = env.spaceOccupied(getX() + 1, getY());
                if (neighbour == null
                        || neighbour.getClass().equals(desiredObject.getClass())) {
                    move(Direction.RIGHT);
                } else if (goalY < getY() && env.spaceOccupied(getX(), getY() - 1) == null
                        && getY() - 1 >= 0) {
                    move(Direction.UP);
                } else {
                    if (env.spaceOccupied(getX(), getY() + 1) == null
                            && getY() + 1 < env.getSize()) {
                        move(Direction.DOWN);
                    } else if (getX() - 1 >= 0 && env.spaceOccupied(getX() - 1, getY()) == null) {
                        move(Direction.LEFT);
                    }
                }
            } else if (goalX < getX()) {//if goal is on the left
                if (env.spaceOccupied(getX() - 1, getY()) == null
                        || env.spaceOccupied(getX() - 1, getY()).getClass().equals(desiredObject.getClass())) {
                    move(Direction.LEFT);
                } else if (goalY < getY() && env.spaceOccupied(getX(), getY() - 1) == null
                        && getY() - 1 >= 0) {
                    move(Direction.UP);
                } else {
                    if (env.spaceOccupied(getX(), getY() + 1) == null
                            && getY() + 1 < env.getSize()) {
                        move(Direction.DOWN);
                    } else if (getX() + 1 < env.getSize() && env.spaceOccupied(getX() + 1, getY()) == null) {
                        move(Direction.RIGHT);
                    }
                }
            }
        } else {
            if (goalY > getY()) {//if goal is up
                if (env.spaceOccupied(getX(), getY() + 1) == null
                        || env.spaceOccupied(getX(), getY() + 1).getClass().equals(desiredObject.getClass())) {
                    move(Direction.DOWN);
                } else if (goalX > getX() && env.spaceOccupied(getX() + 1, getY()) == null
                        && getX() + 1 < env.getSize()) {
                    move(Direction.RIGHT);
                } else {
                    if (env.spaceOccupied(getX() - 1, getY()) == null
                            && getX() - 1 >= 0) {
                        move(Direction.LEFT);
                    } else if (getY() - 1 >= 0 && env.spaceOccupied(getX(), getY() - 1) == null) {
                        move(Direction.UP);
                    }
                }
            } else if (goalY < getY()) {//if goal is down
                if (env.spaceOccupied(getX(), getY() - 1) == null
                        || env.spaceOccupied(getX(), getY() - 1).getClass().equals(desiredObject.getClass())) {
                    move(Direction.UP);
                } else if (goalX > getX() && env.spaceOccupied(getX() + 1, getY()) == null
                        && getX() + 1 < env.getSize()) {
                    move(Direction.RIGHT);
                } else {
                    if (env.spaceOccupied(getX() - 1, getY()) == null
                            && getX() - 1 >= 0) {
                        move(Direction.LEFT);
                    } else if (getY() + 1 < env.getSize() && env.spaceOccupied(getX(), getY() + 1) == null) {
                        move(Direction.DOWN);
                    }
                }
            }
        }
        if (goalX == getX() && goalY == getY()) {//if on goal
            g.getGoalObject().setAlive(false);
            g.setCompleted(true);
            agenda.removeTask(g);
            if (g instanceof CatchRabbit) {
                //System.out.println("fox ate rabbit");
            } else if (g instanceof Explore) {
                this.toExplore.remove(g.getGoalObject());
            }
            env.removeEnvironmentObject(g.getGoalObject());

            //System.out.println("eaten" + agents.toString());
        }

    }
    
    public boolean checkMove(Direction d){
        switch (d) {
            case UP:
                if(getY()-1>=0 && env.spaceOccupied(getX(), getY()-1)==null){
                    //setY(getY() - 1);
                    return true;
                }
                break;
            case DOWN:
                if(getY()+1<env.getSize() && env.spaceOccupied(getX(), getY()+1)==null){
                    //setY(getY() + 1);
                    return true;
                }
                break;
            case LEFT:
                if(getX()-1>=0 && env.spaceOccupied(getX()-1, getY())==null){
                    //setY(getX() - 1);
                    return true;
                }
                break;
            case RIGHT:
                if(getX()+1<env.getSize() && env.spaceOccupied(getX()+1, getY())==null){
                    //setY(getX() + 1);
                    return true;
                }
                break;
        }
        return false;
    }
    
    public void addGoal(Goal g) {
        this.agenda.addTask(g);
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void sendMessage(Agent target, Message msg) {

    }

    public void broadcastToAgents(List<Agent> agents, Message msg) {

    }

    public void receiveMessage(Message msg) {

    }

    @Override
    public String toString() {
        return "Agent{" + this.isAlive() + " " + this.getX() + " , " + this.getY();
    }

}
