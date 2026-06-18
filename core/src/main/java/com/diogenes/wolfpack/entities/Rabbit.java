package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.skills.Scratch;

import java.util.List;
import java.util.Random;

public class Rabbit extends Enemy {

    private static final double FLEE_CHANCE = 0.4;
    private static final Random RANDOM = new Random();

    private int turnsTaken;

    public Rabbit() {
        super("Coelho", 8, 2, 1, 12);
        this.foodReward = 3;

        addSkill(new Scratch());
        this.turnsTaken = 0;
    }

    @Override
    public BattleAction chooseAction(List<? extends Unit> targets) {
        turnsTaken++;

        if (turnsTaken > 1 && RANDOM.nextDouble() < FLEE_CHANCE) {
            hasFled = true;
            return null;
        }

        Unit randomTarget = targets.get(RANDOM.nextInt(targets.size()));
        return new BattleAction(getSkills().get(0), randomTarget);
    }
}
