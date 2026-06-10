package com.diogenes.wolfpack.effects;

import com.diogenes.wolfpack.entities.Unit;

abstract public class StatusEffect {
    protected String name;
    protected int duration;

    public StatusEffect(String name, int duration){
        this.name = name;
        this.duration = duration;
    }

    public boolean isExpired(){
        return duration <= 0;
    }

    public void tick(){
        duration--;
        if(duration < 0) {
            duration = 0;
        }
    }

    abstract public void onTurn(Unit target);

    abstract public void onRemove(Unit target);
}
