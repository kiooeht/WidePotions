package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.screens.compendium.RelicViewScreen
import javassist.CtBehavior
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

object WideRelicCompendium {
    @SpirePatch(
        clz = RelicViewScreen::class,
        method = "renderList"
    )
    object PositionRelics {
        @JvmStatic
        @SpireInsertPatch(
            locator = Locator::class,
            localvars = ["r"]
        )
        fun Insert(
            __instance: RelicViewScreen,
            sb: SpriteBatch,
            msg: String?,
            desc: String?,
            list: ArrayList<AbstractRelic>?,
            @ByRef(type = "int") ___col: Array<Int>,
            r: AbstractRelic
        ) {
            if (r is IsWidePotion) {
                ++___col[0]
            }
        }

        private class Locator : SpireInsertLocator() {
            override fun Locate(ctBehavior: CtBehavior): IntArray {
                val finalMatcher = Matcher.FieldAccessMatcher(AbstractRelic::class.java, "currentY")
                return LineFinder.findInOrder(ctBehavior, finalMatcher)
            }
        }
    }

    @SpirePatch(
        clz = RelicViewScreen::class,
        method = "updateList"
    )
    object FixHitboxPosition {
        @JvmStatic
        fun Instrument(): ExprEditor =
            object : ExprEditor() {
                override fun edit(m: MethodCall) {
                    if (m.className == Hitbox::class.qualifiedName && m.methodName == "move") {
                        m.replace(
                        "if (r instanceof ${IsWidePotion::class.qualifiedName}) {" +
                                "\$1 += ${AbstractRelic::class.qualifiedName}.PAD_X / 2f + 4 * ${Settings::class.qualifiedName}.scale;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                        )
                    }
                }
            }
    }
}
