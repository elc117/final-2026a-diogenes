package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.effects.AttackDown;
import com.diogenes.wolfpack.skills.BearHug;
import com.diogenes.wolfpack.skills.Claw;
import com.diogenes.wolfpack.skills.Roar;

import java.util.List;
// TODO: bear should alternate between claw and bearhug for balancing issues prob
public class Bear extends Enemy {

    public Bear() {
        super("Urso", 55, 10, 6, 5);

        addSkill(new Claw());
        addSkill(new Roar());
        addSkill(new BearHug());
    }

    @Override
    public BattleAction chooseAction(List<? extends Unit> targets) {
        if (!anyTargetHasAttackDown(targets)) {
            return new BattleAction(getSkills().get(1), null);
        }

        Unit highestHpTarget = getHighestHpTarget(targets);
        return new BattleAction(getSkills().get(2), highestHpTarget);
    }

    private boolean anyTargetHasAttackDown(List<? extends Unit> targets) {
        for (Unit u : targets) {
            if (u.hasStatusEffect(AttackDown.class)) {
                return true;
            }
        }
        return false;
    }

    private Unit getHighestHpTarget(List<? extends Unit> targets) {
        Unit highestHpTarget = targets.get(0);
        for (Unit u : targets) {
            if (u.getHp() > highestHpTarget.getHp()) {
                highestHpTarget = u;
            }
        }
        return highestHpTarget;
    }
}
