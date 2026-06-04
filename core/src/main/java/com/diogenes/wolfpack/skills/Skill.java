package com.diogenes.wolfpack.skills;

import com.diogenes.wolfpack.entities.Unit;

abstract public class Skill {

    protected String name;
    protected String description;

    public Skill(String name, String description){
        this.name = name;
        this.description = description;
    }

    abstract public void execute(Unit user, Unit target);

    public String getName() { return name; }
    public String getDescription() { return description; }
}
