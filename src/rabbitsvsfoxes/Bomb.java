package rabbitsvsfoxes;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class Bomb extends EnvironmentObject{
    
    public Bomb(int x, int y) {
        super(x, y);
        this.setIcon(new ImageIcon("images/bomb.png","Bomb icon"));
    }
    
}
