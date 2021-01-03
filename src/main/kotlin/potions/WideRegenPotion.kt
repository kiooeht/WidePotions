package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.powers.WideRegenPower
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.RegenPotion
import com.megacrit.cardcrawl.rooms.AbstractRoom

class WideRegenPotion : WidePotion(RegenPotion()) {
    init {
        if (tips.size > 1) {
            val strings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID(ID))
            tips[tips.lastIndex] =
                PowerTip(strings.DESCRIPTIONS[1], strings.DESCRIPTIONS[2].format(WideRegenPower.REDUCE))
        }
    }

    override fun use(target: AbstractCreature?) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, WideRegenPower(AbstractDungeon.player, potency)))
        }
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return 10
    }
}
