/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class RabbitAgent extends Agent {
private final ImageIcon rabbitIcon
        =new ImageIcon("images/rabbit1.png","Rabbit icon");

    public RabbitAgent(int x, int y) {
        super(x, y);
    }
    
    
    public ImageIcon getIcon() {
        return rabbitIcon;
    }
}
