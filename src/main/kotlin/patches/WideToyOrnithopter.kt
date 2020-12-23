package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnUsePotionRelic
import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.actions.common.HealAction
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.relics.ToyOrnithopter
import javassist.CtBehavior
import javassist.CtNewMethod
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import javassist.expr.NewExpr

object WideToyOrnithopter {
    @SpirePatch(
        clz = ToyOrnithopter::class,
        method = SpirePatch.CONSTRUCTOR
    )
    object AddBetterOnUsePotion {
        @Suppress("unused")
        @JvmStatic
        fun getHealAmt(potion: AbstractPotion, heal: Int): Int =
            if (potion.isWide()) {
                heal * 2
            } else {
                heal
            }

        @JvmStatic
        fun Raw(ctBehavior: CtBehavior) {
            val ctClass = ctBehavior.declaringClass
            val pool = ctClass.classPool

            val betterOnUsePotionInterface = pool.get(BetterOnUsePotionRelic::class.qualifiedName)
            ctClass.addInterface(betterOnUsePotionInterface)

            val method = CtNewMethod.make(
                "public void betterOnUsePotion(${AbstractPotion::class.qualifiedName} potion) {}",
                ctClass
            )

            val origMethod = ctClass.getDeclaredMethod("onUsePotion", arrayOf())
            method.setBody(origMethod, null)
            origMethod.setBody(null)

            ctClass.addMethod(method)

            method.addLocalVariable("potion", pool.get(AbstractPotion::class.qualifiedName))
            method.instrument(
                object : ExprEditor() {
                    override fun edit(e: NewExpr) {
                        if (e.className == HealAction::class.qualifiedName) {
                            e.replace(
                                "\$3 = ${AddBetterOnUsePotion::class.qualifiedName}.getHealAmt(potion, \$3);" +
                                        "\$_ = \$proceed(\$\$);"
                            )
                        }
                    }

                    override fun edit(m: MethodCall) {
                        if (m.methodName == "heal") {
                            m.replace(
                                "\$1 = ${AddBetterOnUsePotion::class.qualifiedName}.getHealAmt(potion, \$1);" +
                                        "\$_ = \$proceed(\$\$);"
                            )
                        }
                    }
                }
            )
        }
    }

    @SpirePatch(
        clz = ToyOrnithopter::class,
        method = "getUpdatedDescription"
    )
    object Description {
        private val strings by lazy { CardCrawlGame.languagePack.getRelicStrings(WidePotionsMod.makeID(ToyOrnithopter.ID)) }

        @JvmStatic
        fun Postfix(__result: String, __instance: ToyOrnithopter): String {
            return __result + strings.DESCRIPTIONS[0].format(ToyOrnithopter.HEAL_AMT)
        }
    }
}
