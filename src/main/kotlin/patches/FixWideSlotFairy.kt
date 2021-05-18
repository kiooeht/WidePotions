package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.AbstractPotion
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess

@SpirePatch2(
    clz = AbstractPlayer::class,
    method = "damage"
)
object FixWideSlotFairy {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(f: FieldAccess) {
                if (f.isReader && f.className == AbstractPlayer::class.qualifiedName && f.fieldName == "potions") {
                    f.replace(
                        "\$_ = new ${ArrayList::class.java.name}();" +
                                "\$_.addAll(\$proceed(\$\$));" +
                                "\$_.addAll(${FixWideSlotFairy::class.qualifiedName}.getWidePotions(this));"
                    )
                }
            }
        }

    @JvmStatic
    fun getWidePotions(__instance: AbstractPlayer): MutableList<AbstractPotion> {
        return __instance.widepotions
    }
}
