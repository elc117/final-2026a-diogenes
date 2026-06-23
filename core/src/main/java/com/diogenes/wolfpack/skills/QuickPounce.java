package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.effects.AttackUp;
import com.diogenes.wolfpack.entities.Unit;

public class QuickPounce extends Skill {

    private static final int SELF_BUFF_DURATION = 2;

    public QuickPounce() {
        super("Salto Rápido", "Causa dano igual ao ATQ e aumenta o próprio ATQ por 2 turnos.", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.takeDamage(user.getAttack());

        user.addStatusEffect(new AttackUp(SELF_BUFF_DURATION));
    }
}
