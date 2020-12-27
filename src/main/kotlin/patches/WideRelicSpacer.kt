package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.ui.panels.TopPanel
import javassist.CtBehavior
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess

object WideRelicSpacer {
    @JvmStatic
    fun wideOffset(relic: AbstractRelic): Float {
        val index = AbstractDungeon.player?.relics?.indexOf(relic) ?: -1
        if (index <= 0) {
            return 0f
        }

        return AbstractDungeon.player.relics
            .take(index)
            .count { r -> r is IsWidePotion } * AbstractRelic.PAD_X
    }

    @SpirePatches(
        SpirePatch(
            clz = AbstractRelic::class,
            method = "instantObtain",
            paramtypez = [AbstractPlayer::class, Int::class, Boolean::class]
        ),
        SpirePatch(
            clz = AbstractRelic::class,
            method = "reorganizeObtain"
        ),
    )
    object Obtain1 {
        @JvmStatic
        fun Instrument(): ExprEditor =
            object : ExprEditor() {
                override fun edit(f: FieldAccess) {
                    if (f.isWriter && f.className == AbstractRelic::class.qualifiedName && f.fieldName == "currentX") {
                        f.replace(
                            "\$1 += ${WideRelicSpacer::class.qualifiedName}.wideOffset(this);" +
                                    "\$proceed(\$1);"
                        )
                    }
                }
            }
    }

    @SpirePatch(
        clz = AbstractRelic::class,
        method = "instantObtain",
        paramtypez = []
    )
    object Obtain2 {
        @JvmStatic
        @SpireInsertPatch(
            locator = Locator::class
        )
        fun Insert(__instance: AbstractRelic) {
            __instance.currentX += wideOffset(__instance)
            __instance.targetX = __instance.currentX
        }
    }

    @SpirePatch(
        clz = AbstractRelic::class,
        method = "obtain"
    )
    object Obtain3 {
        @JvmStatic
        fun Postfix(__instance: AbstractRelic) {
            __instance.targetX += wideOffset(__instance)
        }
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior): IntArray {
            val finalMatcher = Matcher.MethodCallMatcher(TopPanel::class.java, "adjustRelicHbs")
            return LineFinder.findInOrder(ctBehavior, finalMatcher)
        }
    }
}
