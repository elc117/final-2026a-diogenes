package com.diogenes.wolfpack.battle;

import com.diogenes.wolfpack.entities.Unit;
import com.diogenes.wolfpack.skills.Skill;

public class BattleAction {
    public Skill skill;
    public Unit target;

    public BattleAction(Skill skill, Unit target){
        this.skill = skill;
        this.target = target;
    }

}
