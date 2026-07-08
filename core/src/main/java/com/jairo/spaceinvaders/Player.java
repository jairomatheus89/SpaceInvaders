package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public static enum PlayerState {
        IDLE,
        ATTACK,
        HURT
    }

    public static float stateTime;
    public static float shootStateTime;
    public static PlayerState playerState = PlayerState.IDLE;

    protected static Music hurtEffectSfx = Gdx.audio.newMusic(Gdx.files.internal("hurt.wav"));
    protected static Music playerShootSfx = Gdx.audio.newMusic(Gdx.files.internal("shoot.wav"));
    protected static Music playerdieSfx = Gdx.audio.newMusic(Gdx.files.internal("playerdie.wav"));

    protected static final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
    protected static final FreeTypeFontParameter parameter = new FreeTypeFontParameter();

    protected static final Texture playerTexture = new Texture(Gdx.files.internal("spritesheets/player/father.png"));
    protected static final TextureRegion[][] regions = TextureRegion.split(playerTexture, 50, 50);
    protected static final TextureRegion[] idle = {
        regions[0][0],
        regions[0][1],
        regions[0][2],
        regions[0][3]
    };

    protected static final TextureRegion[] attack = {
        regions[1][0],
        regions[1][1],
        regions[1][2],
        regions[1][3],
        regions[1][4],
        regions[1][5]
    };

    protected static final TextureRegion[] hurt = {
        regions[2][0],
        regions[2][1],
    };

    protected static final Animation<TextureRegion> idleAnimation = new Animation<>(0.2f, idle);
    protected static Animation<TextureRegion> attackAnimation = new Animation<>(0.06f, attack);
    protected static Animation<TextureRegion> hurtAnimation = new Animation<>(0.1f, hurt);

    protected static final Texture playerShootTexture = new Texture(Gdx.files.internal("spritesheets/player/fatherShoot.png"));
    protected static final TextureRegion[] shootRegions = TextureRegion.split(playerShootTexture, 24, 36)[0];
    protected static final Animation<TextureRegion> shootAnim = new Animation<>(0.1f, shootRegions);

    protected static final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

    protected static final BitmapFont scoreFont;
    protected static final BitmapFont lifesFont;

    protected static final GlyphLayout layoutScoreFont = new GlyphLayout();
    protected static final GlyphLayout layoutLifesFont = new GlyphLayout();

    protected static final Rectangle playerRect = new Rectangle(0f, 0f, 110f, 110f);
    protected static final Rectangle playerShootRect = new Rectangle(playerRect.x, playerRect.y, 34f, 48f);
    
    protected static int playerScore = 0;
    protected static int playerLifes = 3;

    protected static float playerSpeed = 150.0f;
    protected static float playerShootSpeed = 666f;
    protected static float damageTaken = 0.1f;

    protected static boolean hurted = false;
    protected static boolean shootCoolDown = false;

    static {
        parameter.size = 36;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = Color.GREEN;
        parameter.color = Color.WHITE;

        scoreFont = generator.generateFont(parameter);
        lifesFont = generator.generateFont(parameter);

        idleAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    protected static void playerControll(float delta){
        if(Gdx.input.isKeyPressed(Input.Keys.D) && (playerRect.x < Gdx.graphics.getWidth() - playerRect.width)) playerRect.x += playerSpeed * delta;
        if(Gdx.input.isKeyPressed(Input.Keys.A) && playerRect.x > 0) playerRect.x -= playerSpeed * delta;


        if(playerState == PlayerState.ATTACK && attackAnimation.isAnimationFinished(Player.stateTime)){
            Player.stateTime = 0f;
            playerState = PlayerState.IDLE;
        }

        if(playerState == PlayerState.HURT && hurtAnimation.isAnimationFinished(Player.stateTime)){
            Player.stateTime = 0f;
            playerState = PlayerState.IDLE;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !shootCoolDown){
            Player.shootStateTime = 0f;
            Player.stateTime = 0f;
            playerState = PlayerState.ATTACK;
            playerShootSfx.play();
            shootCoolDown = true;
        }
    }

    public static TextureRegion getCurrentFrameAnim(float delta){
        Player.stateTime += delta;

        if(playerState == PlayerState.ATTACK){
            return attackAnimation.getKeyFrame(Player.stateTime, false);
        }

        if(playerState == PlayerState.HURT){
            return hurtAnimation.getKeyFrame(Player.stateTime, false);
        }

        System.out.println("STATETIME: " + Player.stateTime);
        return idleAnimation.getKeyFrame(Player.stateTime, true);
    }

    public static TextureRegion getCurrentFrameShootAnim(float delta){
        Player.shootStateTime += delta;
        return shootAnim.getKeyFrame(Player.shootStateTime, true);
    }
}
