package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.relics.SacredBark

@SpirePatch2(
    clz = SacredBark::class,
    method = "onEquip"
)
object SacredBarkWideSlots {
    @JvmStatic
    fun Postfix() {
        AbstractDungeon.player.widepotions.forEach { p ->
            p.initializeData()
        }
    }
}
