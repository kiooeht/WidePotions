package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.relics.WideRelicRightHalf
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.helpers.RelicLibrary
import com.megacrit.cardcrawl.relics.AbstractRelic

@SpirePatch(
    clz = RelicLibrary::class,
    method = "getRelic"
)
object LoadWideRelicRightHalf {
    @JvmStatic
    fun Prefix(key: String): SpireReturn<AbstractRelic> {
        return if (key == WideRelicRightHalf.ID) {
            SpireReturn.Return(WideRelicRightHalf())
        } else {
            SpireReturn.Continue()
        }
    }
}
