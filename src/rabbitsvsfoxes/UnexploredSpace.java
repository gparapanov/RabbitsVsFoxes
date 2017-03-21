package rabbitsvsfoxes;

import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class UnexploredSpace extends EnvironmentObject {
    
    public UnexploredSpace(int x, int y) {
        super(x,y);
    }

    @Override
    public String toString() {
        return this.getX()+" "+this.getY();
    }

    
}
