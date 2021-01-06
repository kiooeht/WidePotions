package com.evacipated.cardcrawl.mod.widepotions.powers

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.scale
import com.megacrit.cardcrawl.actions.common.ReducePowerAction
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction
import com.megacrit.cardcrawl.actions.utility.UseCardAction
import com.megacrit.cardcrawl.cards.AbstractCard
import com.megacrit.cardcrawl.cards.CardQueueItem
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.monsters.AbstractMonster

class WideTriplicationPower(
    owner: AbstractCreature,
    amount: Int
) : AbstractWidePower(POWER_ID, "doubleTap") {
    companion object {
        @JvmField
        val POWER_ID = WidePotionsMod.makeID("TriplicationPower")
    }

    init {
        this.owner = owner
        this.amount = amount
        updateDescription()
    }

    override fun updateDescription() {
        description = if (amount == 1) {
            DESCRIPTIONS[0]
        } else {
            DESCRIPTIONS[1].format(amount)
        }
    }

    override fun onUseCard(card: AbstractCard, action: UseCardAction) {
        if (!card.purgeOnUse && amount > 0) {
            flash()
            var m: AbstractMonster? = null
            if (action.target != null) {
                m = action.target as AbstractMonster
            }

            repeat(2) {
                val tmp = card.makeSameInstanceOf()
                AbstractDungeon.player.limbo.addToBottom(tmp)
                tmp.current_x = card.current_x
                tmp.current_y = card.current_y
                tmp.target_x = Settings.WIDTH / 2f - 300f.scale()
                tmp.target_y = Settings.HEIGHT / 2f
                if (m != null) {
                    tmp.calculateCardDamage(m)
                }
                tmp.purgeOnUse = true
                AbstractDungeon.actionManager.addCardQueueItem(
                    CardQueueItem(tmp, m, card.energyOnUse, true, true),
                    true
                )
            }
            addToTop(ReducePowerAction(owner, owner, this, 1))
        }
    }

    override fun atEndOfTurn(isPlayer: Boolean) {
        addToBot(RemoveSpecificPowerAction(owner, owner, this))
    }
}
