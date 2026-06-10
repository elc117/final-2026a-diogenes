package com.diogenes.wolfpack.battle;

import com.diogenes.wolfpack.entities.Enemy;
import com.diogenes.wolfpack.entities.Unit;
import com.diogenes.wolfpack.entities.Wolf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BattleManager {

    private List<Wolf> wolves;
    private List<Enemy> enemies;
    private List<Unit> turnOrder;
    private int currentTurnIndex;

    public BattleManager(List<Wolf> wolves, List<Enemy> enemies){
        this.wolves = wolves;
        this.enemies = enemies;
        this.currentTurnIndex = 0;
        buildTurnOrder();
    }

    public void buildTurnOrder(){
        turnOrder = new ArrayList<>();
        turnOrder.addAll(enemies);
        turnOrder.addAll(wolves);
        turnOrder.sort(Comparator.comparingInt(Unit::getSpeed).reversed());
    }

    public Unit getCurrentUnit(){
        return turnOrder.get(currentTurnIndex);
    }

    public void nextTurn(){
        do{
            currentTurnIndex++;
            if(currentTurnIndex >= turnOrder.size()){
                currentTurnIndex = 0;
            }
        }while(!turnOrder.get(currentTurnIndex).isAlive());
    }

    public boolean playerWon(){
        for(Enemy e : enemies){
            if(e.isAlive())
                return false;
        }

        return true;

    }

    public boolean playerLost(){
        for(Wolf w : wolves){
            if(w.isAlive())
                return false;
        }

        return true;

    }

    public void executeAction(Unit user, BattleAction action){
        action.skill.use(user, action.target);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Wolf> getWolves() {
        return wolves;
    }

    public List<Unit> getTurnOrder() {
        return turnOrder;
    }
}
