/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

/**
 *
 * @author Georgi
 */
public abstract class EnvironmentObject {
    private int x,y;

    public EnvironmentObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public  void setPosition(int x,int y){
        this.x=x;
        this.y=y;
    }
    public void setX(int x){
        this.x=x;
    }
    public void setY(int y){
        this.y=y;
    }
    public int getPositionX(){
        return x;
    }
    public int getPositionY(){
        return y;
    }
}
