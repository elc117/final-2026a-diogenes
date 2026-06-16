package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.skills.MarkPrey;
import com.diogenes.wolfpack.skills.QuickSnap;

public class Scout extends Wolf {

    public Scout() {
        super("Rastreador", 25, 7, 3, 8);
        addSkill(new QuickSnap());
        addSkill(new MarkPrey());
    }
}
