package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.PotionSlot

@SpirePatch(
    clz = AbstractPlayer::class,
    method = "removePotion"
)
object FixRemovePotion {
    @JvmStatic
    fun Prefix(__instance: AbstractPlayer, potionOption: AbstractPotion) {
        if (potionOption.isWide()) {
            val slot = __instance.potions.indexOf(potionOption)
            if (slot >= 0) {
                __instance.potions[slot + 1] = PotionSlot(slot + 1)
            } else {
                val wideSlot = __instance.widepotions.indexOf(potionOption)
                if (wideSlot >= 0) {
                    __instance.widepotions[wideSlot] = WidePotionSlot(potionOption.slot)
                }
            }
        }
    }
}
