package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

public class QuickSnap extends Skill {

    public QuickSnap() {
        super("Bote Rápido", "Ataque que causa 90% do ATQ em dano", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.takeDamage((int)(user.getAttack() * 0.9));
    }
}
