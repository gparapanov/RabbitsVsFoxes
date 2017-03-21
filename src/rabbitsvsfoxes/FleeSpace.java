package rabbitsvsfoxes;

import rabbitsvsfoxes.Objects.EnvironmentObject;

/**
 *
 * @author Georgi
 */
public class FleeSpace extends EnvironmentObject{
    public FleeSpace (int x, int y) {
        super(x,y);
    }

    @Override
    public String toString() {
        return this.getX()+" "+this.getY();
    }
}
