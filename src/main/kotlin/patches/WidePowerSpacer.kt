package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.extensions.getPrivate
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.powers.AbstractPower
import javassist.CtBehavior

@SpirePatch(
    clz = AbstractCreature::class,
    method = "renderPowerIcons"
)
object WidePowerSpacer {
    @JvmStatic
    @SpireInsertPatch(
        locator = Locator::class,
        localvars = ["p", "offset"]
    )
    fun Insert(__instance: AbstractCreature, sb: SpriteBatch, x: Float, y: Float, p: AbstractPower, @ByRef(type = "float") offset: Array<Float>) {
        if (p is IsWidePotion) {
            offset[0] += AbstractCreature::class.getPrivate<Float>("POWER_ICON_PADDING_X") * 0.5f
        }
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior): IntArray {
            val finalMatcher = Matcher.FieldAccessMatcher(AbstractCreature::class.java, "POWER_ICON_PADDING_X")
            return LineFinder.findAllInOrder(ctBehavior, finalMatcher)
        }
    }
}
