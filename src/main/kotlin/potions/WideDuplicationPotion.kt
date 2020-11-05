package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.DuplicationPotion
import com.megacrit.cardcrawl.powers.DuplicationPower
import com.megacrit.cardcrawl.rooms.AbstractRoom
import com.megacrit.cardcrawl.ui.panels.TopPanel

class WideDuplicationPotion : DuplicationPotion(), IsWidePotion {
    init {
        val strings = CardCrawlGame.languagePack.getPotionString("wide:WidePotion")
        name = strings.NAME.format(name)

        isThrown = true
        targetRequired = false

        initializeData()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        super.initializeData()

        description = strings.DESCRIPTIONS[0].format(potency)
        tips[0].body = description
    }

    override fun use(target: AbstractCreature?) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(ApplyPowerAction(
                AbstractDungeon.player,
                AbstractDungeon.player,
                DuplicationPower(AbstractDungeon.player, potency),
                potency
            ))
        }
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return super.getPotency(ascensionLevel) * 3
    }

    override fun makeCopy(): AbstractPotion =
        WideDuplicationPotion()

    override fun setAsObtained(potionSlot: Int) {
        super.setAsObtained(potionSlot)

        AbstractDungeon.player.obtainPotion(potionSlot + 1, WidePotionRightHalf(this))
    }

    override fun adjustPosition(slot: Int) {
        super.adjustPosition(slot)

        hb.move(TopPanel.potionX + (slot + 0.5f) * Settings.POTION_W, posY)
    }

    companion object {
        private val strings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID(POTION_ID))
    }
}
