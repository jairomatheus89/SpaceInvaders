package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DelayedRemovalArray;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    SpriteBatch batch;

    FreeTypeFontGenerator generator;
    FreeTypeFontParameter parameter;

    BitmapFont basicFont;
    BitmapFont endGameFont;

    GlyphLayout layoutBasicFont = new GlyphLayout();
    GlyphLayout layoutEngGame = new GlyphLayout();

    float coolDownReset = 0.5f;


    float piscaCD = 1.0f;
    boolean piscaBool = false;
    float piscaColor = 1.0f;

    protected static boolean resetingWorld = false;

    Enemy enemy = new Enemy();
    DelayedRemovalArray<Enemy> enemyParty = new DelayedRemovalArray<>();
    DelayedRemovalArray<EnemyShoot> enemyShootsParty = new DelayedRemovalArray<>();
    
    @Override
    public void show() {
        this.batch = new SpriteBatch();

        //FONT GENERATOR AND DEFAULT PARAMETER
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        this.parameter = new FreeTypeFontParameter();
        this.parameter.size = 36;
        this.parameter.borderWidth = 1.0f;
        this.parameter.borderColor = Color.RED;
        this.parameter.color = Color.WHITE;
        
        //TITLE TEXT
        this.basicFont = generator.generateFont(parameter);
        layoutBasicFont.setText(basicFont, "Space Invaders!");

        this.endGameFont = generator.generateFont(parameter);
        layoutEngGame.setText(endGameFont, "Your Died!\nPress 'SPACE' to Restart...", Color.WHITE, 0f, Align.center, false);

        //SET INITIAL OF RECs
        Player.playerRect.x = (Gdx.graphics.getWidth() / 2) - (Player.playerRect.width / 2);
        Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /2)));

        //SETTING ENEMIES PARTYS TO FUCKING ALL NIGHTTT!!!
        enemy.spawnEnemies(enemyParty);
        
    }

    public void update(float delta){

        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("QUANTIDADE DE SHOOTS NO ARRAY: " + enemyShootsParty.size);


        if(Player.hurted){

            piscaCD -= 6.0f * delta;
            if(piscaCD <= 0.0f){
                piscaBool = !piscaBool; 
                piscaCD = 1.0f;
            }

            if(piscaBool) piscaColor = 1.0f;
            else piscaColor = 0.0f;

            Player.pixmap.setColor(Color.rgba8888(1.0f, piscaColor, piscaColor, 1.0f));
            Player.pixmap.fill();
            Player.playerTexture = new Texture(Player.pixmap);

            Player.damageTaken -= 1.0 * delta;
            if(Player.damageTaken < 0.0f){
                Player.hurted = false;
                Player.pixmap.setColor(Color.WHITE);
                Player.pixmap.fill();
                Player.playerTexture = new Texture(Player.pixmap);
            } 
        }

        if(coolDownReset >= 0.0f) coolDownReset -= 1.0f * delta;

        if(enemyParty.isEmpty() && !resetingWorld){
            enemy.spawnEnemies(enemyParty);
        }

        //SHOOT FOLLOWING PLAYER
        if(!Player.shootCoolDown) Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /2)));
        else {
            Player.playerShootRect.y += Player.playerShootSpeed * delta;

            if(Player.playerShootRect.y > Gdx.graphics.getHeight()){
                Player.shootCoolDown = false;
                Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /2)));
            }
        }

        if(!resetingWorld && coolDownReset < 0f){
            enemy.enemiesLogics(enemyParty, enemyShootsParty, delta);

            if(Gdx.input.isKeyPressed(Input.Keys.D) && (Player.playerRect.x < Gdx.graphics.getWidth() - Player.playerRect.width)) Player.playerRect.x += Player.playerSpeed * delta;
            if(Gdx.input.isKeyPressed(Input.Keys.A) && Player.playerRect.x > 0) Player.playerRect.x -= Player.playerSpeed * delta;
            if(Gdx.input.isKeyPressed(Input.Keys.W)) Player.playerRect.y += Player.playerSpeed * delta;
            if(Gdx.input.isKeyPressed(Input.Keys.S)) Player.playerRect.y -= Player.playerSpeed * delta;

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !Player.shootCoolDown){
                Player.shootCoolDown = true;
            }
        }

        if(resetingWorld && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            resetWorld();
            resetingWorld = false;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //DRAW AREA
        batch.begin();

            if(resetingWorld)endGameFont.draw(batch, layoutEngGame, (Gdx.graphics.getWidth() / 2), (Gdx.graphics.getHeight() / 2) + layoutEngGame.height);

            if(!resetingWorld){
                basicFont.draw(batch, layoutBasicFont, (Gdx.graphics.getWidth() / 2) - (layoutBasicFont.width / 2), Gdx.graphics.getHeight() - basicFont.getAscent());
                Player.scoreFont.draw(batch, Player.layoutScoreFont, 10f,  Gdx.graphics.getHeight() - Player.scoreFont.getAscent());

                batch.draw(Player.playerShootTexture, Player.playerShootRect.x, Player.playerShootRect.y, Player.playerShootRect.width, Player.playerShootRect.height);
                batch.draw(Player.playerTexture, Player.playerRect.x, Player.playerRect.y, Player.playerRect.width, Player.playerRect.height);
                Player.lifesFont.draw(batch, Player.layoutLifesFont, (Gdx.graphics.getWidth() - Player.layoutLifesFont.width) - Player.lifesFont.getSpaceXadvance(), Gdx.graphics.getHeight() - Player.lifesFont.getAscent());
            }
            
            if(!enemyParty.isEmpty() && !resetingWorld){
                for(Enemy enemy : enemyParty){
                    batch.draw(enemy.enemyTexture, enemy.enemyRect.x, enemy.enemyRect.y, enemy.enemyRect.width, enemy.enemyRect.height);
                }

                for(EnemyShoot eShoot : enemyShootsParty){
                    batch.draw(EnemyShoot.enemyShootTexture, eShoot.enemyShootRect.x, eShoot.enemyShootRect.y, eShoot.enemyShootRect.width, eShoot.enemyShootRect.height);
                }
            }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }

    void resetWorld(){
        enemyShootsParty.clear();
        Player.playerScore = 0;
        Player.playerLifes = 3;
        Player.hurted = false;
        Player.layoutScoreFont.setText(Player.scoreFont, "SCORE: " + String.valueOf(Player.playerScore));
        Player.layoutLifesFont.setText(Player.lifesFont, "LIFES: " + String.valueOf(Player.playerLifes));
        Player.pixmap.setColor(Color.WHITE);
        Player.pixmap.fill();
        Player.playerTexture = new Texture(Player.pixmap);

        enemyParty.begin();
        for(Enemy enemy : enemyParty){
            Player.playerRect.setCenter(Gdx.graphics.getWidth() / 2, 50f);
            Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /2)));
            enemyParty.removeValue(enemy, true);
        }
        enemyParty.end();
        coolDownReset = 0.5f;
    }
}