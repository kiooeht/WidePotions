package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.modthespire.lib.SpireField
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.vfx.FlashPotionEffect
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

object WideFlashPotionEffect {
    @SpirePatch(
        clz = FlashPotionEffect::class,
        method = SpirePatch.CLASS
    )
    object Field {
        @JvmField
        val isWide: SpireField<Boolean> = SpireField { false }
    }

    @SpirePatch(
        clz = FlashPotionEffect::class,
        method = "render",
        paramtypez = [
            SpriteBatch::class,
            Float::class,
            Float::class
        ]
    )
    object RenderWide {
        @JvmStatic
        fun Instrument(): ExprEditor =
            object : ExprEditor() {
                override fun edit(m: MethodCall) {
                    if (m.methodName == "draw") {
                        m.replace(
                            "if (((Boolean) ${Field::class.qualifiedName}.isWide.get(this)).booleanValue()) {" +
                                    "\$2 += 24 * ${Settings::class.qualifiedName}.scale;" +
                                    "\$8 *= 2;" +
                                    "}" +
                                    "\$_ = \$proceed(\$\$);"
                        )
                    }
                }
            }
    }

    @SpirePatch(
        clz = FlashPotionEffect::class,
        method = SpirePatch.CONSTRUCTOR
    )
    object WideCtor {
        @JvmStatic
        fun Postfix(__instance: FlashPotionEffect, p: AbstractPotion) {
            if (p.isWide()) {
                Field.isWide[__instance] = true
            }
        }
    }
}
