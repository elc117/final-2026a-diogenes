package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.skills.Mend;
import com.diogenes.wolfpack.skills.Nip;

public class Healer extends Wolf {

    public Healer() {
        super("Espiritualista", 30, 4, 4, 6);
        addSkill(new Nip());
        addSkill(new Mend());
    }
}
