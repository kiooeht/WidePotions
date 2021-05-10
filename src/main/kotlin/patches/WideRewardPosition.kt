package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.rewards.RewardItem
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import javassist.expr.MethodCall

@SpirePatch(
    clz = RewardItem::class,
    method = "move"
)
object WideRewardPosition {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.className == AbstractPotion::class.qualifiedName && m.methodName == "move") {
                    m.replace(
                        "if (${RenderWide::class.qualifiedName}.isWide(\$0)) {" +
                                "\$1 -= 24 * ${Settings::class.qualifiedName}.scale;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                    )
                }
            }

            override fun edit(f: FieldAccess) {
                if (f.isWriter && f.className == AbstractRelic::class.qualifiedName && (f.fieldName == "currentX" || f.fieldName == "targetX")) {
                    f.replace(
                        "if (${RenderWide::class.qualifiedName}.isWide(\$0)) {" +
                                "\$1 -= 10 * ${Settings::class.qualifiedName}.scale;" +
                                "}" +
                                "\$proceed(\$\$);"
                    )
                }
            }
        }
}
