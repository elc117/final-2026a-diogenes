package com.diogenes.wolfpack.battle;

import com.diogenes.wolfpack.effects.StatusEffect;
import com.diogenes.wolfpack.entities.Enemy;
import com.diogenes.wolfpack.entities.Unit;
import com.diogenes.wolfpack.entities.Wolf;
import com.diogenes.wolfpack.skills.TargetingType;

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
        }while(!isActive(turnOrder.get(currentTurnIndex)));
    }

    private boolean isActive(Unit unit){
        if(!unit.isAlive()) return false;
        if(unit instanceof Enemy && ((Enemy) unit).hasFled()) return false;
        return true;
    }

    public boolean playerWon(){
        for(Enemy e : enemies){
            if(e.isAlive() && !e.hasFled())
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
    // the order here is: save the infos in arrays of "before" hp and statusEffects
    // 1. save the infos in arrays of "before" hp and statusEffects
    // 2. execute the actual skill
    // 3. see the diffs based on comparating the "before" arrays with the now post-skill-use
    public List<BattleLogEntry> executeAction(Unit user, BattleAction action){
        List<Unit> targets = resolveTargets(user, action);

        // snapshot the state before the skill runs so we can diff after
        List<Integer> hpBefore = new ArrayList<>();
        List<List<Class<? extends StatusEffect>>> statusClassesBefore = new ArrayList<>();
        for(Unit target : targets){
            hpBefore.add(target.getHp());
            statusClassesBefore.add(getStatusEffectClasses(target));
        }

        action.skill.use(user, targets);

        return buildLogEntries(user, action.skill.getName(), targets, hpBefore, statusClassesBefore);
    }

    private List<Unit> resolveTargets(Unit user, BattleAction action){
        TargetingType targetingType = action.skill.getTargetingType();
        List<Unit> targets = new ArrayList<>();

        switch (targetingType) {
            case SINGLE_ENEMY:
            case SINGLE_ALLY:
                targets.add(action.target);
                break;
            case ALL_ENEMIES:
                targets.addAll((user instanceof Wolf) ? enemies : wolves);
                break;
            case ALL_ALLIES:
                targets.addAll((user instanceof Wolf) ? wolves : enemies);
                break;
            case SELF:
                targets.add(user);
                break;
        }

        return targets;
    }
    // this function returns the CLASSES of the status effects of a given unit
    // used before and after in the buildLogEntries so we just compare them
    private List<Class<? extends StatusEffect>> getStatusEffectClasses(Unit unit){
        List<Class<? extends StatusEffect>> classes = new ArrayList<>();
        for(StatusEffect effect : unit.getStatusEffects()){
            classes.add(effect.getClass());
        }
        return classes;
    }

    // like explained: this function compares the before arrays values and the after-the-skill-ran values
    // if it sees a difference it will build a battlelog
    private List<BattleLogEntry> buildLogEntries(Unit user, String skillName, List<Unit> targets,
                                                 List<Integer> hpBefore,
                                                 List<List<Class<? extends StatusEffect>>> statusClassesBefore){
        List<BattleLogEntry> entries = new ArrayList<>();

        for(int i = 0; i < targets.size(); i++){
            Unit target = targets.get(i);
            int hpDelta = target.getHp() - hpBefore.get(i);

            if(hpDelta < 0){
                entries.add(new BattleLogEntry(user.getName(), skillName, target.getName(),
                    BattleLogEntry.EffectType.DAMAGE, -hpDelta, null));
            } else if(hpDelta > 0){
                entries.add(new BattleLogEntry(user.getName(), skillName, target.getName(),
                    BattleLogEntry.EffectType.HEAL, hpDelta, null));
            }

            StatusEffect newEffect = findNewlyAppliedStatus(target, statusClassesBefore.get(i));
            if(newEffect != null){
                entries.add(new BattleLogEntry(user.getName(), skillName, target.getName(),
                    BattleLogEntry.EffectType.STATUS_APPLIED, 0, newEffect.getName()));
            }
        }

        return entries;
    }

    private StatusEffect findNewlyAppliedStatus(Unit target, List<Class<? extends StatusEffect>> classesBefore){
        for(StatusEffect effect : target.getStatusEffects()){
            if(!classesBefore.contains(effect.getClass())){
                return effect;
            }
        }
        return null;
    }

    // manages the status effects of a given unit
    // should be called at the start(before action handling) of the unit's turn
    public void processTurnStart(Unit unit){
        List<StatusEffect> effects = new ArrayList<>(unit.getStatusEffects());

        for(StatusEffect effect : effects){
            effect.onTurnStart(unit);
            effect.tick();
            if(effect.isExpired()){
                unit.removeStatusEffect(effect);
            }
        }
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
