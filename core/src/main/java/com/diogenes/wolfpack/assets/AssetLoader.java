package com.diogenes.wolfpack.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

    private Texture wolfSheet;
    private Texture boarSheet;
    private Texture foxSheet;
    private Texture deerSheet;
    private Texture bearSheet;
    private Texture bossBearSheet;
    private Texture rabbitSheet;
    private Texture iconSheet;

    public TextureRegion wolfScout;
    public TextureRegion wolfHealer;
    public TextureRegion wolfAlpha;

    public TextureRegion enemyBoar;
    public TextureRegion enemyFox;
    public TextureRegion enemyDeer;
    public TextureRegion enemyBear;
    public TextureRegion enemyBossBear;
    public TextureRegion enemyRabbit;

    public TextureRegion iconBleed;
    public TextureRegion iconAttackUp;
    public TextureRegion iconAttackDown;
    public TextureRegion iconMarked;
    public TextureRegion iconSelected;

    public TextureRegion whitePixel;
    public Texture winterBackground;

    public void load() {
        wolfSheet = new Texture(Gdx.files.internal("sprites/wolf_tailwag_full.png"));
        TextureRegion[][] wolfGrid = TextureRegion.split(wolfSheet, 32, 32);
        wolfScout = wolfGrid[1][0];
        wolfHealer = wolfGrid[1][3];
        wolfAlpha = wolfGrid[1][6];

        boarSheet = new Texture(Gdx.files.internal("sprites/Boar_Idle.png"));
        enemyBoar = TextureRegion.split(boarSheet, 64, 40)[0][0];

        foxSheet = new Texture(Gdx.files.internal("sprites/Fox_Idle.png"));
        enemyFox = TextureRegion.split(foxSheet, 48, 36)[0][0];

        deerSheet = new Texture(Gdx.files.internal("sprites/Deer_Idle.png"));
        enemyDeer = TextureRegion.split(deerSheet, 72, 52)[0][0];

        bearSheet = new Texture(Gdx.files.internal("sprites/Bear_Idle.png"));
        enemyBear = TextureRegion.split(bearSheet, 64, 33)[0][0];

        rabbitSheet = new Texture(Gdx.files.internal("sprites/Rabbit_Idle.png"));
        enemyRabbit = TextureRegion.split(rabbitSheet, 32, 26)[0][0];

        bossBearSheet = new Texture(Gdx.files.internal("sprites/black_bear.png"));
        TextureRegion[][] bossBearGrid = TextureRegion.split(bossBearSheet, 76, 64);
        enemyBossBear = bossBearGrid[3][0];

        iconSheet = new Texture(Gdx.files.internal("ui/16x16.png"));
        TextureRegion[][] iconGrid = TextureRegion.split(iconSheet, 16, 16);

        // creating white pixel 1x1 through code and stored in memory
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Texture rawTexture = new Texture(pixmap);
        whitePixel = new TextureRegion(rawTexture);
        pixmap.dispose();

        winterBackground = new Texture(Gdx.files.internal("backgrounds/battlebg.png"));

        iconBleed = iconGrid[46][5];
        iconAttackUp = iconGrid[68][9];
        iconAttackDown = iconGrid[68][13];
        iconMarked = iconGrid[40][15];
        iconSelected = iconGrid[39][7];
        // campfire icon = iconGrid[230][907]
        // forge icon =  [91][922]
    }

    public void dispose() {
        if (wolfSheet != null) wolfSheet.dispose();
        if (boarSheet != null) boarSheet.dispose();
        if (foxSheet != null) foxSheet.dispose();
        if (deerSheet != null) deerSheet.dispose();
        if (bearSheet != null) bearSheet.dispose();
        if (bossBearSheet != null) bossBearSheet.dispose();
        if (rabbitSheet != null) rabbitSheet.dispose();
        if (iconSheet != null) iconSheet.dispose();
        if (winterBackground != null) winterBackground.dispose();
        if (whitePixel != null && whitePixel.getTexture() != null) whitePixel.getTexture().dispose();
    }
}
