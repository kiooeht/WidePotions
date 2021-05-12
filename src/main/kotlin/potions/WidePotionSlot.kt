package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.modthespire.lib.SpireField
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.PotionSlot

class WidePotionSlot(
    slot: Int
) : PotionSlot(slot), IsWidePotion {
    init {
        val wideStrings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID("WidePotion"))
        val strings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID(ID))
        name = wideStrings.NAME.format(name)
        description += strings.DESCRIPTIONS[0]
        tips.clear()
        tips.add(PowerTip(name, description))

        hb.resize(hb.width * 2, hb.height)
    }

    override fun adjustPosition(slot: Int) {
        super.adjustPosition((AbstractDungeon.player?.potions?.size ?: 0) + (-slot - 1) * 2)
    }

    @SpirePatch(
        clz = AbstractPlayer::class,
        method = SpirePatch.CLASS
    )
    object Field {
        @JvmField
        val widepotions: SpireField<MutableList<AbstractPotion>> = SpireField { mutableListOf() }
    }
}
