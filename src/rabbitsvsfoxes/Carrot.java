package rabbitsvsfoxes;

import javax.swing.ImageIcon;

/**
 *
 * @author Georgi
 */
public class Carrot extends EnvironmentObject {
    
    private boolean claimed=false;
    private String claimedBy;
    
    public Carrot(int x, int y) {
        super(x, y);
        this.setIcon(new ImageIcon("images/carrot.png","Carrot icon"));
    }
    public Carrot(){
        
    }
    
    public void claim(String name){
        this.claimed=true;
        this.claimedBy=name;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    @Override
    public String toString() {
        String outputString=super.toString()+"<br>";
        outputString+=claimed?"Claimed by: "+claimedBy+".":"Not claimed yet.";
        return outputString; 
    }

    
    
}
