package com.evacipated.cardcrawl.mod.widepotions.relics

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.privateMethod
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivateFinal
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.PotionSlot
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.PotionBelt

class WidePotionBelt : PotionBelt(), IsWidePotion {
    init {
        if (!strings.NAME.isNullOrEmpty()) {
            setPrivateFinal("name", strings.NAME, clazz = AbstractRelic::class.java)
        }
        if (!strings.FLAVOR.isNullOrEmpty()) {
            flavorText = strings.FLAVOR
        }
        description = strings.DESCRIPTIONS[0].format(EXTRA_SLOTS)
        tips[0] = PowerTip(name, description)
        privateMethod("initializeTips", clazz = AbstractRelic::class).invoke<Unit>()
    }

    override fun onEquip() {
        repeat(EXTRA_SLOTS) {
            ++AbstractDungeon.player.potionSlots
            AbstractDungeon.player.potions.add(PotionSlot(AbstractDungeon.player.potionSlots - 1))
        }
    }

    override fun update() {
        hb.resize(PAD_X * 2, PAD_X)
        super.update()
    }

    override fun makeCopy(): AbstractRelic = WidePotionBelt()

    companion object {
        private val strings by lazy { CardCrawlGame.languagePack.getRelicStrings(WidePotionsMod.makeID(ID)) }
        private const val EXTRA_SLOTS = 4
    }
}
