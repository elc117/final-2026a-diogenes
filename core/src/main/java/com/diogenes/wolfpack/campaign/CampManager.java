package com.diogenes.wolfpack.campaign;

import com.diogenes.wolfpack.entities.Enemy;
import com.diogenes.wolfpack.entities.Wolf;

import java.util.List;

public class CampManager {

    private static final int DAILY_CONSUMPTION = 3;
    private static final int STARVATION_DAMAGE = 5;

    public static final int FEED_COST = 2;
    public static final int FEED_HEAL_AMOUNT = 10;

    public static final int TRAIN_ATTACK_COST = 3;
    public static final int TRAIN_DEFENSE_COST = 3;
    public static final int TRAIN_MAX_HP_COST = 4;

    // TODO: tweak the values above based on tests, later

    // add food based on battle just finished and apply starvation damage based on the conditions
    // only called at the start of the day, just before showing camp ui and start camp phase
    public boolean resolveDayEnd(List<Enemy> battleEnemies, Campaign campaign) {
        for (Enemy enemy : battleEnemies) {
            if (!enemy.isAlive() && !enemy.hasFled()) {
                campaign.addFood(enemy.getFoodReward());
            }
        }

        boolean starved = campaign.getFood() < DAILY_CONSUMPTION;
        campaign.consumeFoodUpToZero(DAILY_CONSUMPTION);

        if (starved) {
            for (Wolf wolf : campaign.getWolves()) {
                wolf.applyTrueDamage(STARVATION_DAMAGE);
            }
        }

        return starved;
    }

    public boolean feed(Wolf target, Campaign campaign) {
        if (!campaign.canAfford(FEED_COST)) return false;

        campaign.spendFood(FEED_COST);
        target.heal(FEED_HEAL_AMOUNT);
        return true;
    }

    public boolean trainAttack(Wolf target, Campaign campaign) {
        if (target.hasTrained() || !campaign.canAfford(TRAIN_ATTACK_COST)) return false;

        campaign.spendFood(TRAIN_ATTACK_COST);
        target.trainAttack();
        return true;
    }

    public boolean trainDefense(Wolf target, Campaign campaign) {
        if (target.hasTrained() || !campaign.canAfford(TRAIN_DEFENSE_COST)) return false;

        campaign.spendFood(TRAIN_DEFENSE_COST);
        target.trainDefense();
        return true;
    }

    public boolean trainMaxHp(Wolf target, Campaign campaign) {
        if (target.hasTrained() || !campaign.canAfford(TRAIN_MAX_HP_COST)) return false;

        campaign.spendFood(TRAIN_MAX_HP_COST);
        target.trainMaxHp();
        return true;
    }
}
