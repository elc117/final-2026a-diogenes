package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

public class Nip extends Skill {

    public Nip() {
        super("Mordiscar", "Ataque básico que causa dano igual ao ATQ.", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.takeDamage(user.getAttack());
    }
}
