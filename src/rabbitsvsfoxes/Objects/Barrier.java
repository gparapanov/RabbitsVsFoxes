package rabbitsvsfoxes.Objects;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class Barrier extends EnvironmentObject{
    
    public Barrier(int x, int y) {
        super(x, y);
        this.setIcon(new ImageIcon("images/wall2.png","Barrier icon"));
    }
    
}
