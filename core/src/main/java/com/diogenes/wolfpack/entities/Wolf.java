package com.diogenes.wolfpack.entities;

public abstract class Wolf extends Unit {

    private boolean hasTrained;

    public Wolf(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.hasTrained = false;
    }

    public boolean trainAttack() {
        if (hasTrained) return false;
        this.attack++;
        hasTrained = true;
        return true;
    }

    public boolean trainDefense() {
        if (hasTrained) return false;
        this.defense++;
        hasTrained = true;
        return true;
    }

    public boolean trainMaxHp() {
        if (hasTrained) return false;
        this.maxHp += 5;
        this.heal(5);
        hasTrained = true;
        return true;
    }

    public boolean hasTrained() {
        return hasTrained;
    }
}
