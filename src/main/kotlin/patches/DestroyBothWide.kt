package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.unWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionRightHalf
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.PotionSlot
import com.megacrit.cardcrawl.ui.panels.TopPanel

@SpirePatch(
    clz = TopPanel::class,
    method = "destroyPotion"
)
object DestroyBothWide {
    @JvmStatic
    fun Prefix(__instance: TopPanel, slot: Int): SpireReturn<Void> {
        if (slot < 0) {
            val index = -slot - 1
            if (index >= 0 && index < AbstractDungeon.player.widepotions.size) {
                AbstractDungeon.player.widepotions[index] = WidePotionSlot(slot)
            }
            return SpireReturn.Return()
        }

        val potion = AbstractDungeon.player.potions[slot]
        if (potion.isWide()) {
            val otherSlot = potion.slot + 1
            if (otherSlot >= 0 && otherSlot < AbstractDungeon.player.potions.size) {
                AbstractDungeon.player.potions[otherSlot] = PotionSlot(otherSlot)
            }
        } else if (potion is WidePotionRightHalf) {
            potion.otherHalf.unWide()?.let {
                AbstractDungeon.player.obtainPotion(potion.otherHalf.slot, it)
            }
        }

        return SpireReturn.Continue()
    }
}
