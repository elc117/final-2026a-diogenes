package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.effects.Marked;
import com.diogenes.wolfpack.effects.StatusEffect;
import com.diogenes.wolfpack.skills.Skill;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {

    protected String name;

    protected int maxHp;
    private int hp;
    protected int attack;
    protected int defense;
    protected int speed;

    protected List<StatusEffect> statusEffects;
    protected List<Skill> skills;

    public Unit(String name, int maxHp, int attack, int defense, int speed){
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.statusEffects = new ArrayList<>();
        this.skills = new ArrayList<>();
    }

    public boolean isAlive(){
        return hp > 0;
    }

    public int takeDamage(int damage) {
        // cause the statuseffects handled this way remove themselves in middle of the loop
        // we have to make loop backwards
        for (int i = statusEffects.size() - 1; i >= 0; i--) {
            damage = statusEffects.get(i).onIncomingDamage(damage, this);
        }

        int realDamage = Math.max(1, damage - defense);
        hp = Math.max(0, hp - realDamage);
        return realDamage;
    }

    public int applyTrueDamage(int damage){
        int realDamage = Math.max(0, damage);
        int newHp = Math.max(0, hp - realDamage);
        realDamage = hp - newHp;
        hp = newHp;
        return realDamage;
    }

    public int heal(int healAmount){
        int newHp = Math.min(maxHp, hp + healAmount);
        int realHeal = newHp - hp;
        hp = newHp;
        return realHeal;
    }

    public void addStatusEffect(StatusEffect newEffect) {
        for (StatusEffect existing : this.statusEffects) {
            if (existing.getClass() == newEffect.getClass()) {
                existing.setDuration(Math.max(existing.getDuration(), newEffect.getDuration()));
                return;
            }
        }
        this.statusEffects.add(newEffect);
        newEffect.onApply(this);
    }

    public void removeStatusEffect(StatusEffect effect){
        if(effect == null) return;
        effect.onRemove(this);
        this.statusEffects.remove(effect);
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> type){
        return getStatusEffect(type) != null;
    }

    public StatusEffect getStatusEffect(Class<? extends StatusEffect> type){
        for(StatusEffect effect : statusEffects){
            if(type.isInstance(effect)){
                return effect;
            }
        }
        return null;
    }

    public List<StatusEffect> getStatusEffects() {
        return this.statusEffects;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getSpeed(){
        return this.speed;
    }

    public int getDefense(){
        return this.defense;
    }

    public List<Skill> getSkills(){
        return this.skills;
    }

    public void addSkill(Skill skill){
        this.skills.add(skill);
    }

    public void modifyAttack(int amount) { this.attack += amount; }

    public void modifyDefense(int amount) { this.defense += amount; }

    public void modifySpeed(int amount) { this.speed += amount; }
}
