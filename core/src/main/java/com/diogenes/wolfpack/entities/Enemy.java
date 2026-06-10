package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;

import java.util.List;

abstract public class Enemy extends Unit{

    protected int xpReward;
    protected int foodReward;

    public Enemy(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.xpReward = 10;
        this.foodReward = 3;
    }

    abstract BattleAction chooseAction(List<Unit> targets);
}
