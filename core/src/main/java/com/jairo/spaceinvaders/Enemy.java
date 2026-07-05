package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class Enemy {

    private int ID;
    public Texture enemyTexture;
    public Texture enemyShootTexture;
    //Color color = Color.YELLOW;
    public Rectangle enemyRect;
    public Rectangle enemyShootRect;
    private static Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

    private static int mostCloseBeginID;
    private static int mostCloseEndID;

    private float enemyShootCoolDown = 0f;
    private float shootSpeed = 60.0f;
    private static float moveSideCoolDown = 1.0f;

    private static boolean moveSideRight = true;
    private boolean shooting = false;

    Enemy(){
        //this.enemyQnt = enemyQnt;
    }

    protected void enemiesLogic(DelayedRemovalArray<Enemy> enemyParty, float delta){
        enemyMoveLogic(enemyParty, delta);
        enemyAttackLogic(enemyParty, delta);
        
    }

    protected void spawnEnemies(DelayedRemovalArray<Enemy> enemyParty){

        int enemyID = 0;

        float enemySize = 50f;
        float xPos = enemySize * 1.1f;
        float yPos = Gdx.graphics.getHeight() -( enemySize * 1.6f);

        for(int i = 0; i < 20; i++){
            if(i % 10 == 0){
                yPos -= enemySize * 2.3f;
                xPos = enemySize * 1.7f;
            }

            Enemy enemy = new Enemy();
            enemy.ID = enemyID;
            pixmap.setColor(Color.YELLOW);
            pixmap.fill();
            enemy.enemyTexture = new Texture(pixmap);
            enemy.enemyRect = new Rectangle(xPos, yPos, enemySize, enemySize);
            
            pixmap.setColor(Color.PURPLE);
            pixmap.fill();
            enemy.enemyShootTexture = new Texture(pixmap);

            enemy.enemyShootRect = new Rectangle(enemy.enemyRect.x ,enemy.enemyRect.y, 10f, 10f);
            enemy.enemyShootRect.setCenter(enemy.enemyRect.x + (enemy.enemyRect.width / 2), enemy.enemyRect.y + (enemy.enemyRect.height / 2));
            enemy.enemyShootCoolDown = MathUtils.random( 6.0f, 18.0f);
            
            enemyParty.add(enemy);

            enemyID++;
            xPos += enemySize * 1.7f;
        }

        mostCloseBeginID = enemyParty.get(0).ID;
        mostCloseEndID = enemyParty.get(enemyParty.size - 1).ID;
    }

    private void enemyAttackLogic(DelayedRemovalArray<Enemy> enemyParty, float delta){

        for(Enemy enemy : enemyParty){

            if(enemy.shooting){
                enemy.enemyShootRect.y -= shootSpeed * delta;
                if(enemy.enemyShootRect.y + enemy.enemyShootRect.height < 0f){
                    enemy.enemyShootCoolDown = MathUtils.random( 6.0f, 18.0f);
                    enemy.shooting = false;
                }
            }

            if(!enemy.shooting){
                enemy.enemyShootRect.setCenter(enemy.enemyRect.x + (enemy.enemyRect.width / 2), enemy.enemyRect.y + (enemy.enemyRect.height / 2));
                enemy.enemyShootCoolDown -= 1.0f * delta;
                    if(enemy.enemyShootCoolDown <= 0.0f){
                    enemy.shooting = true;
                    System.out.println("ENEMY SHOOTING");
                }
            }

        }
    }

    private void enemyMoveLogic(DelayedRemovalArray<Enemy> enemyParty, float delta){
        if(enemyParty.get(Enemy.mostCloseEndID).enemyRect.x + (enemyParty.get(Enemy.mostCloseEndID).enemyRect.width * 2) >= Gdx.graphics.getWidth()) {
            moveSideRight = !moveSideRight;
        }
        if(enemyParty.get(Enemy.mostCloseBeginID).enemyRect.x - enemyParty.get(Enemy.mostCloseBeginID).enemyRect.width <= 0) moveSideRight = !moveSideRight;

        
        for(int i = enemyParty.size - 1; i >= 0; i--){
            if(moveSideRight) enemyParty.get(i).enemyRect.x += 2f;
            if(!moveSideRight) enemyParty.get(i).enemyRect.x -= 2f;

            pixmap.setColor(Color.YELLOW);
            pixmap.fill();
            enemyParty.get(i).enemyTexture = new Texture(pixmap);

            if(Collision.checkCollision(Player.playerRect, enemyParty.get(i).enemyRect) && !FirstScreen.resetingWorld){
                System.out.println("VOCE MORREU!@");
                FirstScreen.resetingWorld = true;
            }

            if(Collision.checkCollision(Player.playerShootRect, enemyParty.get(i).enemyRect)){
                Player.shootCoolDown = false;
                System.out.println("MATOU O INIMIGO: " + (enemyParty.get(i).ID));
                enemyParty.removeValue(enemyParty.get(i), true);

                for(int j = 0; j < enemyParty.size; j++){
                    enemyParty.get(j).ID = j;
                }

                if(enemyParty.size > 1){
                    Enemy.mostCloseBeginID = enemyParty.get(0).ID;
                    Enemy.mostCloseEndID = enemyParty.get(enemyParty.size - 1).ID;
                } else {
                    Enemy. mostCloseBeginID = 0;
                    Enemy.mostCloseEndID = 0;
                }
                
                Player.playerScore++;
                Player.layoutScoreFont.setText(Player.scoreFont, "SCORE: " + String.valueOf(Player.playerScore));
            }
        }

        //CHECK CLOSEST TO BEGIN/END
        for(int i = 0; i < enemyParty.size; i++){
            if(enemyParty.get(i).enemyRect.x <= enemyParty.get(Enemy.mostCloseBeginID).enemyRect.x){
                Enemy.mostCloseBeginID = enemyParty.get(i).ID;
            }

            if(enemyParty.get(i).enemyRect.x >= enemyParty.get(Enemy.mostCloseEndID).enemyRect.x){
                Enemy.mostCloseEndID = enemyParty.get(i).ID;
            }
            //enemiesList += enemyParty.get(i).ID + " ";
        }

        if(enemyParty.size > 1){
            pixmap.setColor(Color.BLUE);
            pixmap.fill();
            enemyParty.get(Enemy.mostCloseBeginID).enemyTexture = new Texture(pixmap);

            pixmap.setColor(Color.RED);
            pixmap.fill();
            enemyParty.get(Enemy.mostCloseEndID).enemyTexture = new Texture(pixmap);
        }
    }
}