package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.relics.PotionBelt
import com.megacrit.cardcrawl.ui.panels.TopPanel
import javassist.CtBehavior

@SpirePatch(
    clz = TopPanel::class,
    method = "setPlayerName"
)
object AdjustFloorNumberOnLoad {
    @JvmStatic
    @SpireInsertPatch(
        locator = Locator::class
    )
    fun Insert(__instance: TopPanel, @ByRef(type = "float") ___floorX: Array<Float>) {
        if (AbstractDungeon.player.getRelic(PotionBelt.ID) is IsWidePotion) {
            ___floorX[0] += 40 * Settings.scale
        }
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior): IntArray {
            val finalMatcher = Matcher.FieldAccessMatcher(TopPanel::class.java, "dailyModX")
            return LineFinder.findInOrder(ctBehavior, finalMatcher)
        }
    }
}
