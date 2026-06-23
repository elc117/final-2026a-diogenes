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

    abstract public void onApply(Unit target);

    abstract public void onTurnStart(Unit target);

    abstract public void onRemove(Unit target);

    public int onIncomingDamage(int damage, Unit target) {
        return damage;
    }

    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }

    public int getDuration() {
        return duration;
    }

    public String getName() { return name; }


}
