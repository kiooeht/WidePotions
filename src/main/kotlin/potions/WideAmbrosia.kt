package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.powers.WideDivinityPower
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.Ambrosia

class WideAmbrosia : WidePotion(Ambrosia()) {
    override fun use(target: AbstractCreature?) {
        super.use(target)
        addToBot(ApplyPowerAction(
            AbstractDungeon.player,
            AbstractDungeon.player,
            WideDivinityPower(AbstractDungeon.player)
        ))
    }
}
