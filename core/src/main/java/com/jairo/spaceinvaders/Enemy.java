package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DelayedRemovalArray;



class EnemyShoot{

    public Rectangle enemyShootRect;
    public static Texture enemyShootTexture;

    private static final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

    public EnemyShoot(float xPos, float yPos){
        enemyShootRect = new Rectangle(xPos, yPos, 10f, 10f);
        enemyShootRect.setCenter(xPos, yPos);
    }

    static {
        pixmap.setColor(Color.PURPLE);
        pixmap.fill();
        enemyShootTexture = new Texture(pixmap);
    }
}

public class Enemy {

    public static enum EnemyState {
        ALIVE,
        DEATH
    }

    protected static Music enemyDieSfx = Gdx.audio.newMusic(Gdx.files.internal("enemydie.wav"));
    protected static Music enemyShootSfx = Gdx.audio.newMusic(Gdx.files.internal("enemyShoot.wav"));

    private int ID;
    public EnemyState state = EnemyState.ALIVE;;
    public float stateTime;

    private static final Texture enemyTexture = new Texture(Gdx.files.internal("spritesheets/enemy/enemy.png"));
    private static final TextureRegion[][] regions = TextureRegion.split(enemyTexture, 50, 50); 
    private static final TextureRegion[] idle = {
        regions[0][0],
        regions[0][1],
        regions[0][2]
    };
    private static final TextureRegion[] dyingAnim = {
        regions[1][0],
        regions[1][1],
        regions[1][2],
        regions[1][3],
        regions[1][4],
        regions[1][5],
        regions[1][6],
    };

    Animation<TextureRegion> idleAnimation = new Animation<>(0.1f, idle);
    Animation<TextureRegion> dyingAnimation = new Animation<>(0.1f, dyingAnim);

    public Rectangle enemyRect;
    
    private static final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

    private static int mostCloseBeginID;
    private static int mostCloseEndID;

    private float enemyShootCoolDown = 3f;
    private final float shootSpeed = 100.0f;
    //private static float moveSideCoolDown = 1.0f;

    private static boolean moveSideRight = true;

    Enemy(){
        //this.enemyQnt = enemyQnt;
    }

    void enemiesLogics(DelayedRemovalArray<Enemy> enemyParty, DelayedRemovalArray<EnemyShoot> enemyShootsParty, float delta){
        enemyLogic(enemyParty, enemyShootsParty, delta);
        enemyAttackLogic(enemyShootsParty, delta); 
    }

    protected void spawnEnemies(DelayedRemovalArray<Enemy> enemyParty){

        int enemyID = 0;

        float enemySize = 50f;
        float xPos = enemySize * 1.1f;
        float yPos = Gdx.graphics.getHeight();

        for(int i = 0; i < 40; i++){
            if(i % 10 == 0){
                yPos -= enemySize * 1.8f;
                xPos = enemySize * 1.7f;
            }

            Enemy enemy = new Enemy();
            enemy.ID = enemyID;

            dyingAnimation.setPlayMode(Animation.PlayMode.NORMAL);

            enemy.enemyRect = new Rectangle(xPos, yPos, enemySize, enemySize);
            enemy.enemyShootCoolDown = MathUtils.random( 3.0f, 18.0f);
            
            enemyParty.add(enemy);

            enemyID++;
            xPos += enemySize * 1.7f;
        }

        mostCloseBeginID = enemyParty.get(0).ID;
        mostCloseEndID = enemyParty.get(enemyParty.size - 1).ID;
    }

    public TextureRegion getCurrentFrameAnim(Animation<TextureRegion> animation, float delta){
        stateTime += delta;
        if(state == EnemyState.ALIVE){
            return animation.getKeyFrame(stateTime, true);
        }
        return animation.getKeyFrame(stateTime, false);
    }

    private void enemyAttackLogic(DelayedRemovalArray<EnemyShoot> enemyShootsParty, float delta){

        for(int i = 0; i < enemyShootsParty.size; i++){

            enemyShootsParty.get(i).enemyShootRect.y -= shootSpeed * delta;

            if(Collision.checkCollision(Player.playerRect, enemyShootsParty.get(i).enemyShootRect) && !Player.hurted){
                Player.hurted = true;
                Player.playerLifes--;
                if(Player.playerLifes <= 0){
                    FirstScreen.mainMusic.stop();
                    FirstScreen.resetingWorld = true;
                    FirstScreen.loseMusic.play();
                    Player.playerdieSfx.play();
                }else {
                    Player.hurtEffectSfx.play();
                }
                Player.layoutLifesFont.setText(Player.lifesFont, "LIFES: " + String.valueOf(Player.playerLifes));
                Player.damageTaken = 3.0f;
            }

            if(enemyShootsParty.get(i).enemyShootRect.y + enemyShootsParty.get(i).enemyShootRect.height < 0f) enemyShootsParty.removeValue(enemyShootsParty.get(i), true);
        }
    }

    private void enemyLogic(DelayedRemovalArray<Enemy> enemyParty, DelayedRemovalArray<EnemyShoot> enemyShootsParty, float delta){
        if(enemyParty.get(Enemy.mostCloseEndID).enemyRect.x + (enemyParty.get(Enemy.mostCloseEndID).enemyRect.width * 2) >= Gdx.graphics.getWidth()) {
            moveSideRight = !moveSideRight;
        }
        if(enemyParty.get(Enemy.mostCloseBeginID).enemyRect.x - enemyParty.get(Enemy.mostCloseBeginID).enemyRect.width <= 0) moveSideRight = !moveSideRight;

       


        for(int i = enemyParty.size - 1; i >= 0; i--){
            
            if(enemyParty.get(i).state == EnemyState.ALIVE){
                //IF ALIVE SHOOTING SETUP
                if(enemyParty.get(i).enemyShootCoolDown >= 0.0f){
                    enemyParty.get(i).enemyShootCoolDown -= 0.5f * delta;
                } else {
                    EnemyShoot shoot = new EnemyShoot(enemyParty.get(i).enemyRect.x, enemyParty.get(i).enemyRect.y);
                    enemyShootsParty.add(shoot);
                    enemyParty.get(i).enemyShootCoolDown = MathUtils.random( 3.0f, 18.0f);
                    enemyShootSfx.play();
                }

                //IF ALIVE MOVE SETUP
                if(moveSideRight) enemyParty.get(i).enemyRect.x += 2f;
                if(!moveSideRight) enemyParty.get(i).enemyRect.x -= 2f;

                if(Collision.checkCollision(Player.playerRect, enemyParty.get(i).enemyRect) && !FirstScreen.resetingWorld){
                    System.out.println("VOCE MORREU!@");
                    FirstScreen.mainMusic.stop();
                    FirstScreen.resetingWorld = true;
                    Player.playerdieSfx.play();
                    FirstScreen.loseMusic.play();
                }

                if(Collision.checkCollision(Player.playerShootRect, enemyParty.get(i).enemyRect)){
                    Player.shootCoolDown = false;
                    enemyParty.get(i).state = EnemyState.DEATH;
                    enemyParty.get(i).stateTime = 0;
                    System.out.println("MATOU O INIMIGO: " + (enemyParty.get(i).ID));
                    enemyDieSfx.play();
                    
                    Player.playerScore++;
                    Player.layoutScoreFont.setText(Player.scoreFont, "SCORE: " + String.valueOf(Player.playerScore));
                }
            }

            if(enemyParty.get(i).state == EnemyState.DEATH && enemyParty.get(i).dyingAnimation.isAnimationFinished(enemyParty.get(i).stateTime)){
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

            pixmap.setColor(Color.RED);
            pixmap.fill();
        }
    }
}