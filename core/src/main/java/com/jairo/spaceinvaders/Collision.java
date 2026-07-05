package com.jairo.spaceinvaders;

import com.badlogic.gdx.math.Rectangle;

public class Collision {
    protected static boolean checkCollision(Rectangle a, Rectangle b){
        if(
            (a.x + a.width) > b.x  && 
            a.x < (b.x + b.width)  && 
            (a.y + a.height) > b.y &&
            a.y < (b.y + b.height)
        ){
            return true;
        }
        return false;
    }
}
