package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

public class BearHug extends Skill {

    public BearHug() {
        super("Abraço de Urso", "Ataque poderoso que causa 150% do ATQ em dano.", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.takeDamage((int)(user.getAttack() * 1.5));
    }
}
