package com.jairo.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    protected static final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
    protected static final FreeTypeFontParameter parameter = new FreeTypeFontParameter();

    protected static Texture playerTexture;
    protected static Texture playerShootTexture;

    protected static final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

    protected static final BitmapFont scoreFont;
    protected static final BitmapFont lifesFont;

    protected static final GlyphLayout layoutScoreFont = new GlyphLayout();
    protected static final GlyphLayout layoutLifesFont = new GlyphLayout();

    protected static final Rectangle playerRect = new Rectangle(0f, 50f, 50f, 50f);
    protected static final Rectangle playerShootRect = new Rectangle(playerRect.x, playerRect.y, 10f, 10f);
    
    protected static int playerScore = 0;
    protected static int playerLifes = 3;

    protected static float playerSpeed = 150.0f;
    protected static float playerShootSpeed = 666f;
    protected static float damageTaken = 0.1f;

    protected static boolean hurted = false;
    protected static boolean shootCoolDown = false;

    static {
        parameter.size = 36;
        parameter.borderWidth = 1.0f;
        parameter.borderColor = Color.RED;
        parameter.color = Color.WHITE;

        scoreFont = generator.generateFont(parameter);
        layoutScoreFont.setText(scoreFont, "SCORE: " + String.valueOf(playerScore));

        lifesFont = generator.generateFont(parameter);
        layoutLifesFont.setText(lifesFont, "LIFES: " + String.valueOf(playerLifes));

        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        playerTexture = new Texture(pixmap);

        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        playerShootTexture = new Texture(pixmap);

    }
}
