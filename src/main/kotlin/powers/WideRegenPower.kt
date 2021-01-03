package com.evacipated.cardcrawl.mod.widepotions.powers

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.megacrit.cardcrawl.actions.common.HealAction
import com.megacrit.cardcrawl.actions.common.ReducePowerAction
import com.megacrit.cardcrawl.core.AbstractCreature

class WideRegenPower(
    owner: AbstractCreature,
    heal: Int
) : AbstractWidePower(POWER_ID, "regen") {
    companion object {
        @JvmField
        val POWER_ID = WidePotionsMod.makeID("WideRegen")
        const val REDUCE = 2
    }

    init {
        this.owner = owner
        this.amount = heal
        updateDescription()
    }

    override fun updateDescription() {
        description = DESCRIPTIONS[0].format(amount, REDUCE)
    }

    override fun atEndOfTurn(isPlayer: Boolean) {
        flashWithoutSound()
        addToTop(ReducePowerAction(owner, owner, this, REDUCE))
        addToTop(HealAction(owner, owner, amount))
    }
}
