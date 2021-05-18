package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.characters.AbstractPlayer

object FixHasPotionWide {
    @SpirePatch2(
        clz = AbstractPlayer::class,
        method = "hasPotion"
    )
    object HasPotion {
        @JvmStatic
        fun Postfix(__result: Boolean, __instance: AbstractPlayer, id: String): Boolean {
            if (!__result) {
                return __instance.widepotions.any { it.ID == id }
            }
            return __result
        }
    }

    @SpirePatch2(
        clz = AbstractPlayer::class,
        method = "hasAnyPotions"
    )
    object HasAnyPotions {
        @JvmStatic
        fun Postfix(__result: Boolean, __instance: AbstractPlayer): Boolean {
            if (!__result) {
                return __instance.widepotions.any { it !is WidePotionSlot }
            }
            return __result
        }
    }
}
