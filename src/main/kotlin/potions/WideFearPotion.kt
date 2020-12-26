package com.evacipated.cardcrawl.mod.widepotions.potions

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.FearPotion
import com.megacrit.cardcrawl.powers.VulnerablePower

class WideFearPotion : WidePotion(FearPotion()) {
    init {
        targetRequired = false
    }

    override fun use(target: AbstractCreature?) {
        for (m in AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped) {
                addToBot(ApplyPowerAction(m, AbstractDungeon.player, VulnerablePower(m, potency, false), potency))
            }
        }
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return potion.getPotency(ascensionLevel) * 2
    }
}
