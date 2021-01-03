package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.modthespire.lib.SpireField
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches
import com.megacrit.cardcrawl.powers.AbstractPower
import com.megacrit.cardcrawl.vfx.AbstractGameEffect
import com.megacrit.cardcrawl.vfx.combat.GainPowerEffect
import com.megacrit.cardcrawl.vfx.combat.SilentGainPowerEffect

object GainPowerEffectIsWide {
    @SpirePatch(
        clz = AbstractGameEffect::class,
        method = SpirePatch.CLASS
    )
    object Field {
        @JvmField
        val isWide: SpireField<Boolean> = SpireField { false }
    }

    @SpirePatches(
        SpirePatch(
            clz = GainPowerEffect::class,
            method = SpirePatch.CONSTRUCTOR
        ),
        SpirePatch(
            clz = SilentGainPowerEffect::class,
            method = SpirePatch.CONSTRUCTOR
        ),
    )
    object SetIsWide {
        @JvmStatic
        fun Prefix(__instance: AbstractGameEffect, power: AbstractPower) {
            if (RenderWide.isWide(power)) {
                Field.isWide.set(__instance, true)
            }
        }
    }
}
