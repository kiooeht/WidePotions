package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.canBeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.makeWide
import com.evacipated.cardcrawl.modthespire.lib.ByRef
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
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
        if (potion[0].canBeWide() && AbstractDungeon.potionRng.randomBoolean(WidePotionsMod.wideChance())) {
            potion[0] = potion[0].makeWide()
        }
    }
}
