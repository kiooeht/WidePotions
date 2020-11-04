package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.AbstractPotion
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import javassist.expr.MethodCall

@SpirePatch(
    clz = AbstractPlayer::class,
    method = "damage"
)
object WideFairyPotionFix {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.methodName == "hasPotion") {
                    m.replace("\$_ = \$proceed(\$\$) || \$proceed(\"wide:\" + \$1);")
                }
            }

            override fun edit(f: FieldAccess) {
                if (f.isReader && f.className == AbstractPotion::class.qualifiedName && f.fieldName == "ID") {
                    f.replace(
                        "\$_ = \$proceed(\$\$);" +
                                "if (\$_.equals(\"wide:FairyPotion\")) {" +
                                "\$_ = \"FairyPotion\";" +
                                "}"
                    )
                }
            }
        }
}
