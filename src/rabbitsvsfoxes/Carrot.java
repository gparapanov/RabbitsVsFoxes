package rabbitsvsfoxes;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class Carrot extends EnvironmentObject {
    
    public Carrot(int x, int y) {
        super(x, y);
        this.setIcon(new ImageIcon("images/carrot.png","Carrot icon"));
    }

    @Override
    public String toString() {
        return "Carrot{"+this.isAlive() + " "+this.getX()+" , "+this.getY();
    }
    
}
