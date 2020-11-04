package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.modthespire.lib.ByRef
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.rewards.RewardItem

@SpirePatch(
    clz = RewardItem::class,
    method = SpirePatch.CONSTRUCTOR,
    paramtypez = [AbstractPotion::class]
)
object WideRewards {
    @JvmStatic
    fun Prefix(__instance: RewardItem, @ByRef potion: Array<AbstractPotion>) {
        if (WidePotion.whitelist.contains(potion[0].ID)) {
            potion[0] = WidePotion(potion[0])
        }
    }
}
