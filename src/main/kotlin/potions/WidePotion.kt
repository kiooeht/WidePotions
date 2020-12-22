package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.getPrivate
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivateFinal
import com.evacipated.cardcrawl.mod.widepotions.patches.WidePotency
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.localization.LocalizedStrings
import com.megacrit.cardcrawl.localization.PotionStrings
import com.megacrit.cardcrawl.potions.*
import com.megacrit.cardcrawl.ui.panels.TopPanel
import kotlin.math.ceil
import kotlin.math.floor

class WidePotion(
    val potion: AbstractPotion
) : AbstractPotion(
    "[MISSING_NAME]",
    makeID(potion.ID),
    potion.rarity,
    potion.size,
    potion.color
), IsWidePotion {
    private var initialized = false
    val otherHalf: WidePotionRightHalf = WidePotionRightHalf(this)
    private var customDescription: String? = null

    init {
        setPrivateFinal("color", potion.color, AbstractPotion::class.java)
        setPrivateFinal("p_effect", potion.p_effect, AbstractPotion::class.java)
        liquidColor = potion.liquidColor
        hybridColor = potion.hybridColor
        spotsColor = potion.spotsColor

        initialized = true
        val potionStrings = LocalizedStrings::class.getPrivate<Map<String, PotionStrings>>("potions")
        val strings = potionStrings[WidePotionsMod.makeID(ID)]
        var customName = false
        if (strings != null) {
            if (strings.NAME != null) {
                name = strings.NAME
                customName = true
            }
            if (strings.DESCRIPTIONS != null) {
                customDescription = strings.DESCRIPTIONS[0]
            }
        }
        if (!customName) {
            val wideStrings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID("WidePotion"))
            name = wideStrings.NAME.format(potion.name)
        }

        isThrown = potion.isThrown
        targetRequired = potion.targetRequired

        initializeData()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        if (initialized) {
            WidePotency.isWidePotion = this
            potion.initializeData()
            WidePotency.isWidePotion = null
            if (customDescription != null) {
                description = customDescription!!.format(potion.getPrivate<Int>("potency", AbstractPotion::class.java))
                potion.description = description
            } else {
                description = potion.description
            }
            tips.clear()
            tips.addAll(potion.tips)
            if (tips.isNotEmpty() && tips[0].header == potion.name) {
                tips[0].header = name
                if (customDescription != null) {
                    tips[0].body = description
                }
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
            id

        val whitelist = listOf(
            StrengthPotion.POTION_ID,
            DexterityPotion.POTION_ID,
            BlockPotion.POTION_ID,
            FairyPotion.POTION_ID,
            FirePotion.POTION_ID,
            ExplosivePotion.POTION_ID,
            SneckoOil.POTION_ID,
            AncientPotion.POTION_ID,
            EnergyPotion.POTION_ID,
            FruitJuice.POTION_ID,
            BloodPotion.POTION_ID,
            PoisonPotion.POTION_ID,
            FocusPotion.POTION_ID,
            BottledMiracle.POTION_ID,
            HeartOfIron.POTION_ID,
            GhostInAJar.POTION_ID,
            RegenPotion.POTION_ID,
            SpeedPotion.POTION_ID,
            SteroidPotion.POTION_ID,
            CultistPotion.POTION_ID,
            EssenceOfSteel.POTION_ID,
            LiquidBronze.POTION_ID,
            DistilledChaosPotion.POTION_ID,
            LiquidMemories.POTION_ID,
            SwiftPotion.POTION_ID,
            DuplicationPotion.POTION_ID,
        )

        val whitemap = mapOf<String, () -> AbstractPotion>(
            WeakenPotion.POTION_ID to ::WideWeakenPotion,
            FearPotion.POTION_ID to ::WideFearPotion,
            BlessingOfTheForge.POTION_ID to ::WideBlessingOfTheForge,
            AttackPotion.POTION_ID to ::WideAttackPotion,
            SkillPotion.POTION_ID to ::WideSkillPotion,
            PowerPotion.POTION_ID to ::WidePowerPotion,
        )
    }
}
