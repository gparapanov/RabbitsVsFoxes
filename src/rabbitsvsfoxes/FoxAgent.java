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
public class FoxAgent extends Agent{

    public FoxAgent(int x, int y) {
        super(x, y);
        this.setIcon(new ImageIcon("images/fox (1).png", "Fox icon"));
    }

    
}
