package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

abstract public class Skill {

    protected String name;
    protected String description;

    protected int baseCooldown;
    protected int currentCooldown;

    public Skill(String name, String description, int baseCooldown){
        this.name = name;
        this.description = description;
        this.baseCooldown = baseCooldown;
        this.currentCooldown = 0;
    }

    public boolean use(Unit user, Unit target) {
        if (currentCooldown > 0) {
            return false;
        }


        execute(user, target);

        // Coloca o golpe em recarga com o valor máximo
        currentCooldown = baseCooldown;
        return true;
    }

    protected abstract void execute(Unit user, Unit target);

    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return currentCooldown > 0; }
}
