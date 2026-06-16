package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

public class Mend extends Skill {

    public Mend() {
        super("Remendar", "Restaura 8 HP de um aliado.", TargetingType.SINGLE_ALLY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.heal(8);
    }
}
