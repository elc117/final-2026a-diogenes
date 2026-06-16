package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.effects.Marked;
import com.diogenes.wolfpack.entities.Unit;

public class MarkPrey extends Skill {

    public MarkPrey() {
        super("Marcar Presa", "Marca o alvo. O próximo ataque contra o alvo causa 50% a mais de dano.", TargetingType.SINGLE_ENEMY);
    }

    @Override
    protected void execute(Unit user, Unit target) {
        target.addStatusEffect(new Marked());
    }
}
