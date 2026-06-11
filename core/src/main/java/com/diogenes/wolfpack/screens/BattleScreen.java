package com.diogenes.wolfpack.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.utils.ScreenUtils;
import com.diogenes.wolfpack.WolfPack;
import com.diogenes.wolfpack.battle.BattleManager;
import com.diogenes.wolfpack.entities.*;

import java.util.ArrayList;
import java.util.List;

public class BattleScreen implements Screen {

    final WolfPack game;
    private BattleManager battleManager;
    private List<Wolf> wolves;
    private List<Enemy> enemies;

    public BattleScreen(final WolfPack game) {
        this.game = game;
    }

    @Override
    public void show() {
        initUnits();

        battleManager = new BattleManager(wolves, enemies);

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.BLACK);

        game.batch.begin();

        game.font.draw(game.batch, "placeholder", 100, 100);

        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    // test setup
    public void initUnits(){
        wolves = new ArrayList<>();
        enemies = new ArrayList<>();

        wolves.add(new Alpha());
        wolves.add(new Healer());
        wolves.add(new Scout());
        enemies.add(new Boar());
    }
}
