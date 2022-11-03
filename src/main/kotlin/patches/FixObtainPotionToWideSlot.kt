package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.AbstractPotion

@SpirePatch2(
    clz = AbstractPlayer::class,
    method = "obtainPotion",
    paramtypez = [Int::class, AbstractPotion::class]
)
object FixObtainPotionToWideSlot {
    @JvmStatic
    fun Prefix(__instance: AbstractPlayer, slot: Int, potionToObtain: AbstractPotion): SpireReturn<Void> {
        if (slot < 0) {
            val index = -slot - 1
            if (index >= 0 && index < __instance.widepotions.size) {
                __instance.widepotions[index] = potionToObtain
                potionToObtain.setAsObtained(slot)
            }
            return SpireReturn.Return()
        }
        return SpireReturn.Continue()
    }
}
