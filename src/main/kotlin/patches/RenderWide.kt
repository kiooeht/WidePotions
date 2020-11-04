package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.potions.AbstractPotion
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

@SpirePatches(
    SpirePatch(
        clz = AbstractPotion::class,
        method = "render"
    ),
    SpirePatch(
        clz = AbstractPotion::class,
        method = "renderShiny"
    ),
    SpirePatch(
        clz = AbstractPotion::class,
        method = "renderOutline",
        paramtypez = [SpriteBatch::class]
    ),
    SpirePatch(
        clz = AbstractPotion::class,
        method = "renderOutline",
        paramtypez = [SpriteBatch::class, Color::class]
    ),
    SpirePatch(
        clz = AbstractPotion::class,
        method = "renderLightOutline"
    ),
)
object RenderWide {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.methodName == "draw") {
                    m.replace(
                        "if (this instanceof ${WidePotion::class.qualifiedName}) {" +
                                "\$2 += 24 * ${Settings::class.qualifiedName}.scale;" +
                                "\$8 *= 2;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                    )
                }
            }
        }
}
