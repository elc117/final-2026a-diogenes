package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.effects.AttackUp;
import com.diogenes.wolfpack.entities.Unit;

import java.util.List;

public class Howl extends Skill {

    public Howl() {
        super("Uivo", "Aumenta o ATQ de toda a matilha por 3 turnos.", TargetingType.ALL_ALLIES);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.addStatusEffect(new AttackUp(3));
    }

    @Override
    protected void execute(Unit user, List<? extends Unit> targets) {
        for (Unit target : targets) {
            target.addStatusEffect(new AttackUp(3));
        }
    }
}
