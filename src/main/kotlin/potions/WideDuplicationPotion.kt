package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.powers.WideTriplicationPower
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.DuplicationPotion

class WideDuplicationPotion : WidePotion(DuplicationPotion()) {
    override fun use(target: AbstractCreature?) {
        addToBot(ApplyPowerAction(
            AbstractDungeon.player,
            AbstractDungeon.player,
            WideTriplicationPower(AbstractDungeon.player, potency),
            potency
        ))
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return 2
    }
}
