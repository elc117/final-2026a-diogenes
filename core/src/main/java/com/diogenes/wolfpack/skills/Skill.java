package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

import java.util.List;

abstract public class Skill {

    protected String name;
    protected String description;
    protected TargetingType targetingType;

    public Skill(String name, String description, TargetingType targetingType){
        this.name = name;
        this.description = description;
        this.targetingType = targetingType;
    }

    public boolean use(Unit user, Unit target) {
        execute(user, target);
        return true;
    }

    public boolean use(Unit user, List<? extends Unit> targets) {
        execute(user, targets);
        return true;
    }

    protected abstract void execute(Unit user, Unit target);

    protected void execute(Unit user, List<? extends Unit> targets) {
        for (Unit target : targets) {
            execute(user, target);
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public TargetingType getTargetingType() { return targetingType; }
}
