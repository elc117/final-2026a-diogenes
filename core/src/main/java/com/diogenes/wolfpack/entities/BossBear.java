package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.effects.AttackDown;
import com.diogenes.wolfpack.skills.BossMaul;
import com.diogenes.wolfpack.skills.Claw;
import com.diogenes.wolfpack.skills.FerociousRoar;

import java.util.List;

//TODO: same problem, think of better atk pattern, maybe use only Claw in phase 1 and phase2 only BossMaul
public class BossBear extends Enemy {

    private static final double ENRAGE_HP_THRESHOLD = 0.3;
    private static final int ENRAGE_ATTACK = 24;

    private boolean enraged;

    public BossBear() {
        super("Urso Maior", 75, 12, 7, 6);
        this.foodReward = 8;

        addSkill(new Claw());
        addSkill(new FerociousRoar());
        addSkill(new BossMaul());

        this.enraged = false;
    }

    @Override
    public BattleAction chooseAction(List<? extends Unit> targets) {
        checkEnrage();

        if (!anyTargetHasAttackDown(targets)) {
            return new BattleAction(getSkills().get(1), null);
        }

        Unit lowestHpTarget = getLowestHpTarget(targets);
        return new BattleAction(getSkills().get(2), lowestHpTarget);
    }

    private void checkEnrage() {
        if (enraged) return;

        if (getHp() <= getMaxHp() * ENRAGE_HP_THRESHOLD) {
            modifyAttack(ENRAGE_ATTACK - getAttack());
            enraged = true;
        }
    }

    private boolean anyTargetHasAttackDown(List<? extends Unit> targets) {
        for (Unit u : targets) {
            if (u.hasStatusEffect(AttackDown.class)) {
                return true;
            }
        }
        return false;
    }

    private Unit getLowestHpTarget(List<? extends Unit> targets) {
        Unit lowestHpTarget = targets.get(0);
        for (Unit u : targets) {
            if (u.getHp() < lowestHpTarget.getHp()) {
                lowestHpTarget = u;
            }
        }
        return lowestHpTarget;
    }
}
