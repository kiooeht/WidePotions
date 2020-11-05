package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.PotionSlot
import com.megacrit.cardcrawl.ui.panels.TopPanel

@SpirePatch(
    clz = TopPanel::class,
    method = "destroyPotion"
)
object DestroyBothWide {
    @JvmStatic
    fun Prefix(__instance: TopPanel, slot: Int) {
        val potion = AbstractDungeon.player.potions[slot]
        if (potion.isWide()) {
            val otherSlot = potion.slot + 1
            if (otherSlot >= 0) {
                AbstractDungeon.player.potions[otherSlot] = PotionSlot(otherSlot)
            }
        }
    }
}
