package com.diogenes.wolfpack.entities;

abstract public class Wolf extends Unit {

    protected int currentXp;
    protected int xpThreshold;

    public Wolf(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.currentXp = 0;
        this.xpThreshold = 100;

    }

    public void gainXp(int xp){
        this.currentXp += xp;
        levelUp();
    }

    public void levelUp(){
        while(currentXp >= xpThreshold) {
            currentXp -= xpThreshold;
            this.level++;
            xpThreshold = (int) (xpThreshold * 1.5);
            onLevelUp();
        }
    }

    abstract void onLevelUp();
}
