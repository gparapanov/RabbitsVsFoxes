package rabbitsvsfoxes.Agent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import rabbitsvsfoxes.Agenda;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Goals.CatchRabbit;
import rabbitsvsfoxes.Direction;
import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Communication.Message;
import rabbitsvsfoxes.Communication.MessageGroup;
import rabbitsvsfoxes.Communication.MessageType;
import rabbitsvsfoxes.Goals.EatCarrot;
import rabbitsvsfoxes.Environment;
import rabbitsvsfoxes.Goals.DistractFox;
import rabbitsvsfoxes.Goals.Explore;
import rabbitsvsfoxes.Goals.Flee;
import rabbitsvsfoxes.Goals.Goal;
import rabbitsvsfoxes.Search.*;
import rabbitsvsfoxes.UnexploredSpace;

/**
 *
 * @author Georgi
 */
public class Agent extends EnvironmentObject {

    protected int radius = 7;
    protected int rabbitRadius = 11;
    protected int foxRadius = 7;
    /*
     Agent character is a random value 1-10,
     if the value is <8 the agent is team-working,
     >=8 it is more self interested.
     This is only used when a decision has to be made between helping a friend,
     or doing more individually beneficial things.
     */
    protected final int agentCharacter = (int) (Math.random() * (10)) + 1;
    protected final int characterSeparator = 8;

    protected ArrayList<String> lastLogs;
    protected ArrayList<EnvironmentObject> objAround;
    protected ArrayList<UnexploredSpace> toExplore;

    protected double health = 100;
    protected String myName;
    protected Agenda agenda;
    protected Color myColor;
    protected Color teamColor;
    protected Environment env;

    protected MessageGroup myGroup;
    protected int messagesReadIndex;
    protected Message lastMessageRead;

    public Agent(int x, int y, Environment env, MessageGroup mg) {
        super(x, y);
        this.env = env;
        this.agenda = new Agenda(this);
        this.objAround = new ArrayList<>();
        this.toExplore = new ArrayList<>();
        this.myGroup = mg;
        this.lastLogs = new ArrayList<>();
        this.myName = env.getName(this);
        this.messagesReadIndex = 0;
        radius = (this instanceof RabbitAgent) ? rabbitRadius : foxRadius;
        discoverExplorationSpaces();
    }

    public Agent() {

    }

    protected void discoverExplorationSpaces() {
        for (int i = radius; i < env.getSize(); i += radius) {
            for (int j = radius; j < env.getSize(); j += radius) {
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

    public static int manhattanDistance(EnvironmentObject eo1, EnvironmentObject eo2) {
        //calculates manhattan distance between agent and objects nearby
        return Math.abs(eo2.getX() - eo1.getX()) + Math.abs(eo2.getY() - eo1.getY());
    }

    public int diagonalDistance(EnvironmentObject eo1, EnvironmentObject eo2) {
        return Math.max(Math.abs(eo1.getX() - eo2.getX()), Math.abs(eo1.getY() - eo2.getY()));
    }

    public void findGoal() {
    }

    public Goal openPostbox() {
        return null;
    }

    public void lookAround(int radius) {

    }

    public AgentState convertAgentToState() {
        return new AgentState(this, getX(), getY(), env.getWorld());
    }

    public AgentState convertAgentToGoalState() {
        return new AgentState(this, agenda.getTop().getGoalObject().getX(), agenda.getTop().getGoalObject().getY(), env.getWorld());
    }

    public void makeAStep() {
        lookAround(radius);
        findGoal();
        if (agenda.getTop() != null) {
            moveTowardsGoal();
        } else {
            //System.out.println("Game Over!");
        }

    }

    public void moveTowardsGoal() {
        Goal goal = agenda.getTop();
        int differenceX = Math.abs(goal.getGoalObject().getX() - getX());
        int differenceY = Math.abs(goal.getGoalObject().getY() - getY());
        if (goal.getTeamColor() == null) {
            this.setTeamColor(myColor);
        } else {
            this.setTeamColor(agenda.getTop().getTeamColor());
        }
        //moveTowardsGoal(agenda.getTop());
        SearchProblem problem = new AgentRoutingAStar(convertAgentToState(), convertAgentToGoalState(), env.getWorld());
        Path path = problem.search();//perform search, get result
        if (path != null) {
            if (path.size() > 0) {
                move(((AgentAction) path.get(0).action).movement);
            } else {
                System.out.println("at x"+getX()+" y"+getY() +"going to x"+goal.getGoalObject().getX()+" y"+goal.getGoalObject().getY()+" no path found");
//                if (differenceX <= 1 && differenceY <= 1 && goal instanceof Flee) {
//                    this.setX(goal.getGoalObject().getX());
//                    this.setY(goal.getGoalObject().getY());
//                }
            }

            if (differenceX == 1 && differenceY == 1
                    && (goal instanceof EatCarrot
                    || goal instanceof CatchRabbit)) {
                this.setX(goal.getGoalObject().getX());
                this.setY(goal.getGoalObject().getY());
            }

            if (goal.getGoalObject().getX() == getX() && goal.getGoalObject().getY() == getY()) {//if on goal
                goal.getGoalObject().setAlive(false);
                goal.setCompleted(true);
                agenda.removeTask(goal);
                if (goal instanceof CatchRabbit) {
                    //System.out.println("fox ate rabbit");
                    replenishHealth();
                    ((RabbitAgent) goal.getGoalObject()).unclaimAllCarrots();
                    env.getGui().writeLogToGui("Rabbit: " + ((RabbitAgent) goal.getGoalObject()).getName() + " is about to get eaten, unclaiming all of his carrots.");
                    env.getGui().writeLogToGui("Fox: " + myName + " has eaten " + ((RabbitAgent) goal.getGoalObject()).getName());
                } else if (goal instanceof Explore) {
                    this.toExplore.remove(goal.getGoalObject());
                } else if (goal instanceof EatCarrot) {
                    replenishHealth();
                    env.getGui().writeLogToGui("Rabbit: " + getName() + " has eaten carrot at x:" + goal.getGoalObject().getX() + " y:" + goal.getGoalObject().getY());
                }
                env.removeEnvironmentObject(goal.getGoalObject());
            }
           
        }
        //decreaseHealth();
        if (health <= 0) {
            env.getGui().writeLogToGui(myName +" HAS DIED OUT OF STARVATION!");
            this.setAlive(false);
            env.removeEnvironmentObject(this);
        }
        while (lastLogs.size() > 5) {
            lastLogs.remove(lastLogs.size() - 1);
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
                replenishHealth();
                ((RabbitAgent) g.getGoalObject()).unclaimAllCarrots();
            } else if (g instanceof Explore) {
                this.toExplore.remove(g.getGoalObject());
            }
            env.removeEnvironmentObject(g.getGoalObject());

        }
        if (g instanceof DistractFox && manhattanDistance(this, g.getGoalObject()) <= 4) {
            g.setCompleted(true);
            agenda.removeTask(g);
            System.out.println("i have distracted the fox");
            myGroup.broadcastMessage(new Message(MessageType.DisengageInDistraction, g.getGoalObject(), this));
            lastLogs.add(0, "I have distracted the fox, running away!");
        }
        decreaseHealth();
        if (health <= 0) {
            env.getGui().writeLogToGui(myName +" has died out of starvation.");
            this.setAlive(false);
            env.removeEnvironmentObject(this);
        }
        while (lastLogs.size() > 5) {
            lastLogs.remove(lastLogs.size() - 1);
        }

    }

    public boolean compareObjects(EnvironmentObject eo1, EnvironmentObject eo2) {
        if (eo1.getX() == eo2.getX() && eo1.getY() == eo2.getY()) {
            return true;
        }
        return false;
    }

    public ArrayList<RabbitAgent> rabbitsAtArea(int x, int y, int r) {
        ArrayList<RabbitAgent> rabbits = new ArrayList<>();
        for (EnvironmentObject ag : env.getAgents()) {
            if (ag instanceof RabbitAgent) {
                if (ag.getX() < x + r && ag.getX() > x - r && ag.getY() < y + r
                        && ag.getY() > y - r) {//means a fox is a threat
                    rabbits.add((RabbitAgent) ag);
                }
            }
        }
        return rabbits;
    }

    public void decreaseHealth() {
        this.health -= 0.7;
    }

    public void replenishHealth() {
        this.health = 100;
    }

    public boolean checkMove(Direction d) {
        switch (d) {
            case UP:
                if (getY() - 1 >= 0 && env.spaceOccupied(getX(), getY() - 1) == null) {
                    //setY(getY() - 1);
                    return true;
                }
                break;
            case DOWN:
                if (getY() + 1 < env.getSize() && env.spaceOccupied(getX(), getY() + 1) == null) {
                    //setY(getY() + 1);
                    return true;
                }
                break;
            case LEFT:
                if (getX() - 1 >= 0 && env.spaceOccupied(getX() - 1, getY()) == null) {
                    //setY(getX() - 1);
                    return true;
                }
                break;
            case RIGHT:
                if (getX() + 1 < env.getSize() && env.spaceOccupied(getX() + 1, getY()) == null) {
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

    public Color getMyColor() {
        return myColor;
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public String getName() {
        return myName;
    }

    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void sendMessage(Agent target, Message msg) {

    }

    public void broadcastToAgents(List<Agent> agents, Message msg) {

    }

    public void receiveMessage(Message msg) {

    }

    @Override
    public String toString() {
        String logsString = "";
        for (String itemString : lastLogs) {
            logsString += itemString + "<br>";
        }
        String output = super.toString() + "<br>Name: " + this.myName + "<br>Health: " + Double.toString(getHealth()).substring(0, 2)
                + "<br>Character: " + (agentCharacter < 8 ? "Community-helper" : "Self-oriented")
                + "<br>Agenda:<br>" + agenda + "<br>Last action taken:<br>"
                + logsString;
        return output;
    }

}
