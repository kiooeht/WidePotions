package com.evacipated.cardcrawl.mod.widepotions.potions

import basemod.patches.whatmod.WhatMod
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

open class WidePotion(
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
    protected var customDescription: String? = null

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
        fixDoubleWhatMod()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        if (initialized) {
            WidePotency.isWidePotion = this
            potion.initializeData()
            WidePotency.isWidePotion = null
            potency = potion.getPrivate("potency", AbstractPotion::class.java)
            if (customDescription != null) {
                description = customDescription!!.format(potency)
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

    protected fun AbstractPotion_canUse(): Boolean {
        return super.canUse()
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
        if (this::class == WidePotion::class) {
            WidePotion(potion.makeCopy())
        } else {
            try {
                this::class.java.newInstance()
            } catch (e: Exception) {
                when (e) {
                    is InstantiationException, is IllegalAccessException -> throw NotImplementedError("Custom Wide Potion must implement makeCopy()")
                    else -> throw e
                }
            }
        }

    override fun setAsObtained(potionSlot: Int) {
        super.setAsObtained(potionSlot)

        potion.slot = slot

        if (potionSlot + 1 < AbstractDungeon.player.potions.size) {
            AbstractDungeon.player.obtainPotion(potionSlot + 1, otherHalf)
        }
    }

    override fun adjustPosition(slot: Int) {
        super.adjustPosition(slot)

        hb.move(TopPanel.potionX + (slot + 0.5f) * Settings.POTION_W, posY)
    }

    private fun fixDoubleWhatMod() {
        if (WhatMod.enabled && this::class != WidePotion::class && tips[0].body != description) {
            tips[0].body = description
        }
    }

    override fun unWide(): AbstractPotion? {
        return potion.makeCopy()
    }

    companion object {
        private fun makeID(id: String): String =
            id

        internal val whitelist = mutableSetOf(
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
            SpeedPotion.POTION_ID,
            SteroidPotion.POTION_ID,
            CultistPotion.POTION_ID,
            EssenceOfSteel.POTION_ID,
            LiquidBronze.POTION_ID,
            DistilledChaosPotion.POTION_ID,
            LiquidMemories.POTION_ID,
            SwiftPotion.POTION_ID,
            CunningPotion.POTION_ID,
            EssenceOfDarkness.POTION_ID,
            PotionOfCapacity.POTION_ID,
        )

        internal val whitemap = mutableMapOf<String, AbstractPotion>(
            WeakenPotion.POTION_ID to WideWeakenPotion(),
            FearPotion.POTION_ID to WideFearPotion(),
            BlessingOfTheForge.POTION_ID to WideBlessingOfTheForge(),
            AttackPotion.POTION_ID to WideAttackPotion(),
            SkillPotion.POTION_ID to WideSkillPotion(),
            PowerPotion.POTION_ID to WidePowerPotion(),
            ColorlessPotion.POTION_ID to WideColorlessPotion(),
            Ambrosia.POTION_ID to WideAmbrosia(),
            SmokeBomb.POTION_ID to WideSmokeBomb(),
            GamblersBrew.POTION_ID to WideGamblersBrew(),
            RegenPotion.POTION_ID to WideRegenPotion(),
            DuplicationPotion.POTION_ID to WideDuplicationPotion(),
        )
    }
}
