package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

public class Maul extends Skill {

    public Maul() {
        super("Dilacerar", "Ataque poderoso que causa 120% do ATQ em dano.", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.takeDamage((int)(user.getAttack() * 1.2));
    }
}
