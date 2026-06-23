package com.diogenes.wolfpack.effects;

import com.diogenes.wolfpack.entities.Unit;

public class Marked extends StatusEffect {

    public Marked() {
        // does not expire through tick, so we add a big value to avoid unexpected behavior
        super("Marcado", Integer.MAX_VALUE);
    }

    @Override
    public void onApply(Unit target) {}
    @Override
    public void onTurnStart(Unit target) {}
    @Override
    public void onRemove(Unit target) {}

    @Override
    public int onIncomingDamage(int damage, Unit target) {
        target.removeStatusEffect(this);

        return (int) (damage * 1.5);
    }
}
