package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.rewards.RewardItem
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

@SpirePatch(
    clz = RewardItem::class,
    method = "render"
)
object WideRewardTextPosition {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.className == FontHelper::class.qualifiedName && m.methodName == "renderSmartText") {
                    m.replace(
                        "if (type == ${RewardItem.RewardType::class.qualifiedName}.POTION" +
                                "&& ${RenderWide::class.qualifiedName}.isWide(potion)) {" +
                                "\$4 += 10 * ${Settings::class.qualifiedName}.scale;" +
                                "} else if (type == ${RewardItem.RewardType::class.qualifiedName}.RELIC" +
                                "&& ${RenderWide::class.qualifiedName}.isWide(relic)) {" +
                                "\$4 += 48 * ${Settings::class.qualifiedName}.scale;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                    )
                }
            }
        }
}
