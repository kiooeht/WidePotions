package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.patches.WidePotency
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.localization.PotionStrings
import com.megacrit.cardcrawl.potions.*
import com.megacrit.cardcrawl.ui.panels.TopPanel
import kotlin.math.ceil
import kotlin.math.floor

class WidePotion(
    val potion: AbstractPotion
) : AbstractPotion(
    "[MISSING NAME]",
    makeID(potion.ID),
    potion.rarity,
    potion.size,
    potion.color
) {
    private var initialized = false
    private val strings: PotionStrings
    val otherHalf: WidePotionRightHalf = WidePotionRightHalf(this)

    init {
        initialized = true
        strings = CardCrawlGame.languagePack.getPotionString("wide:WidePotion")
        name = strings.NAME.format(potion.name)
        initializeData()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        if (initialized) {
            WidePotency.isWidePotion = this
            potion.initializeData()
            WidePotency.isWidePotion = null
            description = potion.description
            tips.clear()
            tips.addAll(potion.tips)
            if (tips.isNotEmpty() && tips[0].header == potion.name) {
                tips[0].header = name
            }
        }
    }

    override fun use(target: AbstractCreature?) {
        potion.use(target)
    }

    override fun canUse(): Boolean {
        return potion.canUse()
    }

    override fun canDiscard(): Boolean {
        return potion.canDiscard()
    }

    override fun getPotency(ascensionLevel: Int): Int =
        if (initialized) {
            val origPotency = potion.getPotency(ascensionLevel)
            val double = origPotency * 2
            val roundUp = ceil(origPotency * 2.5f).toInt()
            val roundDown = floor(origPotency * 2.5f).toInt()
            if (roundDown == double) {
                roundUp
            } else {
                roundDown
            }
        } else {
            0
        }

    override fun makeCopy(): AbstractPotion =
        WidePotion(potion.makeCopy())

    override fun setAsObtained(potionSlot: Int) {
        super.setAsObtained(potionSlot)

        potion.slot = slot

        AbstractDungeon.player.obtainPotion(potionSlot + 1, otherHalf)
    }

    override fun adjustPosition(slot: Int) {
        super.adjustPosition(slot)

        hb.move(TopPanel.potionX + (slot + 0.5f) * Settings.POTION_W, posY)
    }

    companion object {
        private fun makeID(id: String): String =
            "wide:$id"

        val whitelist = listOf(
            StrengthPotion.POTION_ID,
            DexterityPotion.POTION_ID,
            BlockPotion.POTION_ID,
            FairyPotion.POTION_ID,
            FirePotion.POTION_ID,
            ExplosivePotion.POTION_ID
        )
    }
}
