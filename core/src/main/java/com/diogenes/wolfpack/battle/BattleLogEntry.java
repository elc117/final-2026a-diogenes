package com.diogenes.wolfpack.battle;

import java.util.List;

public class BattleLogEntry {

    public enum EffectType {
        DAMAGE,
        HEAL,
        STATUS_APPLIED
    }

    private final String userName;
    private final String skillName;
    private final String targetName;
    private final EffectType effectType;
    private final int amount;
    private final String statusName;

    public BattleLogEntry(String userName, String skillName, String targetName,
                          EffectType effectType, int amount, String statusName) {
        this.userName = userName;
        this.skillName = skillName;
        this.targetName = targetName;
        this.effectType = effectType;
        this.amount = amount;
        this.statusName = statusName;
    }

    public String format() {
        switch (effectType) {
            case DAMAGE:
                return String.format("%s usou %s e causou %d de dano em %s!",
                    userName, skillName, amount, targetName);

            case HEAL:
                return String.format("%s usou %s e curou %d de HP em %s!",
                    userName, skillName, amount, targetName);

            case STATUS_APPLIED:
                return String.format("%s aplicou %s em %s!",
                    userName, statusName, targetName);

            default:
                return String.format("%s usou %s.", userName, skillName);
        }
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public int getAmount() {
        return amount;
    }

    public String getTargetName() {
        return targetName;
    }
}
