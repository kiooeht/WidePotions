package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
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
        if (potion is WidePotion) {
            val otherSlot = AbstractDungeon.player.potions.indexOf(potion.otherHalf)
            if (otherSlot >= 0) {
                AbstractDungeon.player.potions[otherSlot] = PotionSlot(otherSlot)
            }
        }
    }
}
