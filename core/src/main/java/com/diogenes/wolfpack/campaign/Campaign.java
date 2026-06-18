package com.diogenes.wolfpack.campaign;

import com.diogenes.wolfpack.entities.Wolf;

import java.util.List;

public class Campaign {

    public static final int TOTAL_DAYS = 7;
    private static final int STARTING_FOOD = 8;

    private final List<Wolf> wolves;
    private int currentDay;
    private int food;

    public Campaign(List<Wolf> wolves) {
        this.wolves = wolves;
        this.currentDay = 1;
        this.food = STARTING_FOOD;
    }

    public void addFood(int amount) {
        if (amount < 0) return;
        this.food += amount;
    }

    public boolean canAfford(int amount) {
        return food >= amount;
    }

    // spend food is used only if player has the food to actually spend(camp actions)
    public boolean spendFood(int amount) {
        if (!canAfford(amount)) return false;
        this.food -= amount;
        return true;
    }

    // this is for the "debt" of daily food, even tho player doesnt have sufficient it will take away still, but clamp to 0
    public void consumeFoodUpToZero(int amount) {
        this.food = Math.max(0, this.food - amount);
    }

    public void advanceDay() {
        this.currentDay++;
    }

    public boolean isFinalDay() {
        return currentDay >= TOTAL_DAYS;
    }

    public int getFood() {
        return food;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public List<Wolf> getWolves() {
        return wolves;
    }
}
