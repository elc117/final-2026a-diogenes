package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.skills.FoxNip;
import com.diogenes.wolfpack.skills.QuickPounce;

import java.util.List;
// TODO: make so quickpouncec does like 80% of atk dmg but buffs and nip does 100%
public class Fox extends Enemy {

    public Fox() {
        super("Raposa", 18, 5, 2, 9);
        this.foodReward = 1;

        addSkill(new FoxNip());
        addSkill(new QuickPounce());
    }

    @Override
    public BattleAction chooseAction(List<? extends Unit> targets) {
        Unit lowestHpTarget = targets.get(0);

        for (Unit u : targets) {
            if (u.getHp() < lowestHpTarget.getHp()) {
                lowestHpTarget = u;
            }
        }

        return new BattleAction(getSkills().get(1), lowestHpTarget);
    }
}
