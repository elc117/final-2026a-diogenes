package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;

import java.util.List;

public abstract class Enemy extends Unit{

    protected int foodReward;
    protected boolean hasFled;

    public Enemy(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.foodReward = 3;
        this.hasFled = false;
    }

    public abstract BattleAction chooseAction(List<? extends Unit> targets);

    public boolean hasFled() {
        return hasFled;
    }

    public int getFoodReward() {
        return foodReward;
    }
}
