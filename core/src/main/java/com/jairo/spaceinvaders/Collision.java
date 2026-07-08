package com.jairo.spaceinvaders;

import com.badlogic.gdx.math.Rectangle;

public class Collision {
    protected static boolean checkCollision(Rectangle a, Rectangle b){
        return (
            (a.x + a.width) > b.x  && 
            a.x < (b.x + b.width)  && 
            (a.y + a.height) > b.y &&
            a.y < (b.y + b.height)
        );
    }

    protected static boolean checkCollisionEnemyAttack(Rectangle player, Rectangle enemyShoot){

        float left = player.x + player.width * 0.15f;
        float right = left + player.width * 0.70f;
        float height = player.y + player.height * 0.90f;

        return (
            right > enemyShoot.x  && 
            left < enemyShoot.x + enemyShoot.width  && 
            height > enemyShoot.y &&
            player.y < enemyShoot.y + enemyShoot.height
        );
    }

}
