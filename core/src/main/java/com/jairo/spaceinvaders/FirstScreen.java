package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DelayedRemovalArray;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    protected static Music mainMusic = Gdx.audio.newMusic(Gdx.files.internal("space.ogg"));
    protected static Music loseMusic = Gdx.audio.newMusic(Gdx.files.internal("lose.wav"));

    protected static Texture backgroundTex = new Texture(Gdx.files.internal("cemeterybackground.png"));
    protected static TextureRegion[] backgroundRegion = TextureRegion.split(backgroundTex, 572, 322)[0];
    protected static Animation<TextureRegion> backgroundAnimation = new  Animation<>(0.15f, backgroundRegion);
    private static float backgroundStateTime = 0f;

    SpriteBatch batch;

    FreeTypeFontGenerator generator;
    FreeTypeFontParameter parameter;

    BitmapFont basicFont;
    BitmapFont endGameFont;

    GlyphLayout layoutBasicFont = new GlyphLayout();
    GlyphLayout layoutEngGame = new GlyphLayout();

    float coolDownReset = 0.5f;

    protected static boolean resetingWorld = false;
    protected static float resetingCoolDown = 1.0f;

    Enemy enemy = new Enemy();
    DelayedRemovalArray<Enemy> enemyParty = new DelayedRemovalArray<>();
    DelayedRemovalArray<EnemyShoot> enemyShootsParty = new DelayedRemovalArray<>();

    protected static final Pixmap pixmapDebug = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    protected Texture playerDebugRec;
    float leftDebug = Player.playerRect.x + Player.playerRect.width * 0.15f;
    float widthDebug = Player.playerRect.x + Player.playerRect.width * 0.85f;
    float heightDebug = Player.playerRect.y + Player.playerRect.height * 0.85f;
    float bottomDebug = Player.playerRect.y;
    
    @Override
    public void show() {

        pixmapDebug.setColor(Color.ORANGE);
        pixmapDebug.fill();
        playerDebugRec = new Texture(pixmapDebug);

        this.batch = new SpriteBatch();

        //FONT GENERATOR AND DEFAULT PARAMETER
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("meltedMonster.ttf"));
        this.parameter = new FreeTypeFontParameter();
        this.parameter.size = 38;
        this.parameter.shadowOffsetX = 3;
        this.parameter.shadowOffsetY = 3;
        this.parameter.shadowColor = Color.BLACK;
        this.parameter.color = Color.GREEN;
        
        //TITLE TEXT
        this.basicFont = generator.generateFont(parameter);
        layoutBasicFont.setText(basicFont, "Exorcism Invaders!");

        this.endGameFont = generator.generateFont(parameter);
        layoutEngGame.setText(endGameFont, "Your Dead!\nSCORE:" + Player.playerScore + "\nPress 'SPACE' to Restart...", Color.WHITE, 0f, Align.center, false);


        Player.layoutScoreFont.setText(Player.scoreFont, "SCORE: " + String.valueOf(Player.playerScore));
        Player.layoutLifesFont.setText(Player.lifesFont, "LIFES: " + String.valueOf(Player.playerLifes));

        //SET INITIAL OF RECs
        Player.playerRect.x = (Gdx.graphics.getWidth() / 2) - (Player.playerRect.width / 2);
        Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /3)));

        //SETTING ENEMIES PARTYS TO FUCKING ALL NIGHTTT!!!
        enemy.spawnEnemies(enemyParty);

        mainMusic.setLooping(true);
        mainMusic.setVolume(0.3f);
        mainMusic.play();
    }

    public void update(float delta){


        // leftDebug = Player.playerRect.x + Player.playerRect.width * 0.15f;
        // widthDebug = Player.playerRect.width * 0.70f;
        // heightDebug = Player.playerRect.height * 0.90f;
        // bottomDebug = Player.playerRect.y;

        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        // System.out.println("QUANTIDADE DE SHOOTS NO ARRAY: " + enemyParty.size);
        // System.out.println("QUANTIDADE DE SHOOTS NO ARRAY: " + enemyShootsParty.size);

        if(Player.hurted){

            Player.damageTaken -= 1.0 * delta;
            if(Player.damageTaken < 0.0f){
                Player.hurted = false;
            }
        }

        if(coolDownReset >= 0.0f) coolDownReset -= 1.0f * delta;

        if(enemyParty.isEmpty() && !resetingWorld){
            Enemy.sideSpeed *= 1.4f;
            Enemy.downSpeed *= 1.2f;
            Enemy.shootCoolDownMin *= 0.8f;
            if(Enemy.shootCoolDownMin < 0.1f) Enemy.shootCoolDownMin = 0.1f;
            Enemy.shootCoolDownMax *= 0.6f;
            if(Enemy.shootCoolDownMax < 1.2f) Enemy.shootCoolDownMax = 1.2f;
            enemy.spawnEnemies(enemyParty);
        }

        //SHOOT FOLLOWING PLAYER
        if(!Player.shootCoolDown) Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /3)));
        else {
            Player.playerShootRect.y += Player.playerShootSpeed * delta;

            if(Player.playerShootRect.y > Gdx.graphics.getHeight()){
                Player.shootCoolDown = false;
                Player.shootStateTime = 0f;
                Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /3)));
            }
        }

        if(!resetingWorld && coolDownReset < 0f){
            enemy.enemiesLogics(enemyParty, enemyShootsParty, delta);
            Player.playerControll(delta);
        }

        if(resetingWorld && resetingCoolDown >= 0.0f) resetingCoolDown -= 1.0f * delta;
        if(resetingWorld && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && resetingCoolDown < 0f){
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

            if(resetingWorld){
                layoutEngGame.setText(endGameFont, "Your Dead!\n\nSCORE:" + Player.playerScore + "\n\nPress 'SPACE' to Restart...", Color.WHITE, 0f, Align.center, false);
                endGameFont.draw(batch, layoutEngGame, (Gdx.graphics.getWidth() / 2), (Gdx.graphics.getHeight() / 2) + (layoutEngGame.height /2));
            }
            if(!resetingWorld){
                batch.setColor(0.3f, 0.3f, 0.45f, 1f); // escurece
                batch.draw(FirstScreen.getCurrentFrameAnim(delta), 0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                batch.setColor(Color.WHITE);

                basicFont.draw(batch, layoutBasicFont, (Gdx.graphics.getWidth() / 2) - (layoutBasicFont.width / 2), Gdx.graphics.getHeight() - basicFont.getAscent());
                Player.scoreFont.draw(batch, Player.layoutScoreFont, 10f,  Gdx.graphics.getHeight() - Player.scoreFont.getAscent());

                //batch.draw(playerDebugRec, leftDebug, bottomDebug, widthDebug, heightDebug);

                batch.draw(Player.getCurrentFrameShootAnim(delta), Player.playerShootRect.x, Player.playerShootRect.y, Player.playerShootRect.width, Player.playerShootRect.height);
                batch.draw(Player.getCurrentFrameAnim(delta), Player.playerRect.x, Player.playerRect.y, Player.playerRect.width, Player.playerRect.height);
                
                Player.lifesFont.draw(batch, Player.layoutLifesFont, (Gdx.graphics.getWidth() - Player.layoutLifesFont.width) - Player.lifesFont.getSpaceXadvance(), Gdx.graphics.getHeight() - Player.lifesFont.getAscent());
            }
            
            if(!enemyParty.isEmpty() && !resetingWorld){
                for(Enemy obj : enemyParty){
                    if(obj.state == Enemy.EnemyState.ALIVE){
                        batch.draw(obj.getCurrentFrameAnim(obj.idleAnimation, delta), obj.enemyRect.x, obj.enemyRect.y, obj.enemyRect.width, obj.enemyRect.height);
                    }
                    if(obj.state == Enemy.EnemyState.DEATH){
                        batch.draw(obj.getCurrentFrameAnim(obj.dyingAnimation, delta), obj.enemyRect.x, obj.enemyRect.y, obj.enemyRect.width, obj.enemyRect.height);
                    }
                }

                for(EnemyShoot eShoot : enemyShootsParty){
                    if(eShoot.state == EnemyShoot.EnemyShootState.IDLE){
                        batch.draw(eShoot.getCurrentFrameAnim(eShoot.idleAnimation, delta), eShoot.enemyShootRect.x, eShoot.enemyShootRect.y, eShoot.enemyShootRect.width, eShoot.enemyShootRect.height);
                    }
                }
            }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        //if(width <= 0 || height <= 0) return;
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
        mainMusic.dispose();
        loseMusic.dispose();
        batch.dispose();
        generator.dispose();
        basicFont.dispose();
        endGameFont.dispose();
    }

    void resetWorld(){
        enemyShootsParty.clear();
        Player.playerScore = 0;
        Player.playerLifes = 3;
        Player.hurted = false;
        Player.layoutScoreFont.setText(Player.scoreFont, "SCORE: " + String.valueOf(Player.playerScore));
        Player.layoutLifesFont.setText(Player.lifesFont, "LIFES: " + String.valueOf(Player.playerLifes));

        Enemy.shootCoolDownMin = 6.0f;
        Enemy.shootCoolDownMax = 20.0f;
        Enemy.sideSpeed = 20.0f;
        Enemy.downSpeed = 6.0f;

        enemyParty.begin();
        for(Enemy obj : enemyParty){
            Player.playerRect.setCenter(Gdx.graphics.getWidth() / 2, 50f);
            Player.playerShootRect.setCenter((Player.playerRect.x + (Player.playerRect.width /2)), (Player.playerRect.y + (Player.playerRect.height /2)));
            enemyParty.removeValue(obj, true);
        }
        enemyParty.end();
        coolDownReset = 0.5f;
        mainMusic.play();
    }

    public static TextureRegion getCurrentFrameAnim(float delta){
        backgroundStateTime += delta;

        return backgroundAnimation.getKeyFrame(backgroundStateTime, true);
    }
}