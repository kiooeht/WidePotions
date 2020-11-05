package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.BlessingOfTheForge
import com.megacrit.cardcrawl.rooms.AbstractRoom
import com.megacrit.cardcrawl.ui.panels.TopPanel

class WideBlessingOfTheForge : BlessingOfTheForge(), IsWidePotion {
    init {
        name = strings.NAME

        initializeData()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        super.initializeData()

        description = strings.DESCRIPTIONS[0]
        tips[0].body = description
    }

    override fun use(target: AbstractCreature?) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(ApotheosisAction())
        }
    }

    override fun makeCopy(): AbstractPotion =
        WideBlessingOfTheForge()

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
