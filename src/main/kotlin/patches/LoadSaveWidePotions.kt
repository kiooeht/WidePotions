package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.helpers.PotionHelper
import com.megacrit.cardcrawl.potions.AbstractPotion

@SpirePatch(
    clz = PotionHelper::class,
    method = "getPotion"
)
object LoadSaveWidePotions {
    @JvmStatic
    fun Prefix(name: String?): SpireReturn<AbstractPotion?> {
        if (name != null) {
            if (name.startsWith("wide:")) {
                val realPotion = PotionHelper.getPotion(name.removePrefix("wide:"))
                if (realPotion != null) {
                    return SpireReturn.Return(WidePotion(realPotion))
                }
            }
        }
        return SpireReturn.Continue()
    }
}
