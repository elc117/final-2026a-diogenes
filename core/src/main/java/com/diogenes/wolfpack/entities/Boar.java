package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.skills.Bite;

import java.util.List;

public class Boar extends Enemy{

    public Boar() {
        super("Boar", 18, 4, 4, 3);

        addSkill(new Bite());
    }

    @Override
    BattleAction chooseAction(List<Unit> targets) {
        Unit lesserHpTarget = targets.get(0);

        for(Unit u : targets){
             if(lesserHpTarget.getHp() > u.getHp()){
                lesserHpTarget = u;
             }
        }
        return new BattleAction(getSkills().get(0), lesserHpTarget);
    }
}
