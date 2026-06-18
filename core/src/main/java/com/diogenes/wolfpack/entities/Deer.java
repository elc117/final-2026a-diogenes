package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.skills.Kick;

import java.util.List;
import java.util.Random;

public class Deer extends Enemy {

    private static final int FLEE_ON_TURN = 4;
    private static final Random RANDOM = new Random();

    private int turnsTaken;

    public Deer() {
        super("Cervo", 22, 3, 2, 8);
        this.foodReward = 4;

        addSkill(new Kick());
        this.turnsTaken = 0;
    }

    @Override
    public BattleAction chooseAction(List<? extends Unit> targets) {
        turnsTaken++;

        if (turnsTaken >= FLEE_ON_TURN) {
            hasFled = true;
            return null;
        }

        Unit randomTarget = targets.get(RANDOM.nextInt(targets.size()));
        return new BattleAction(getSkills().get(0), randomTarget);
    }
}
