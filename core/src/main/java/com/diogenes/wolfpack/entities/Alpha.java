package com.diogenes.wolfpack.entities;

import com.diogenes.wolfpack.skills.Howl;
import com.diogenes.wolfpack.skills.Maul;

public class Alpha extends Wolf {

    public Alpha() {
        super("Líder", 45, 8, 7, 4);
        addSkill(new Maul());
        addSkill(new Howl());
    }
}
