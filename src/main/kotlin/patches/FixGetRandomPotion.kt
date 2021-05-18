package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionRightHalf
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.PotionSlot
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import javassist.expr.Instanceof

@SpirePatch(
    clz = AbstractPlayer::class,
    method = "getRandomPotion"
)
object FixGetRandomPotion {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(i: Instanceof) {
                if (i.type.name == PotionSlot::class.qualifiedName) {
                    i.replace("\$_ = \$proceed(\$\$) || \$1 instanceof ${WidePotionRightHalf::class.qualifiedName};")
                }
            }

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
}
