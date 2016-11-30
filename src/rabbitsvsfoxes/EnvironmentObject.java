package rabbitsvsfoxes;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public abstract class EnvironmentObject {

    private int x, y;
    private ImageIcon icon = null;
    private boolean alive;

    public EnvironmentObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.alive=true;
    }
    
    public EnvironmentObject(){
        
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
