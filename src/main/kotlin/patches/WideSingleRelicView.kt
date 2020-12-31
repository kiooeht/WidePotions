package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

@SpirePatch(
    clz = SingleRelicViewPopup::class,
    method = "renderRelicImage"
)
object WideSingleRelicView {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.methodName == "draw") {
                    m.replace(
                        "if (relic instanceof ${IsWidePotion::class.qualifiedName}) {" +
                                "\$8 *= 2f;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                    )
                }
            }
        }
}
