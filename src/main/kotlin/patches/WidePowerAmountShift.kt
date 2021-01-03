package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.extensions.getPrivate
import com.evacipated.cardcrawl.modthespire.lib.ByRef
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.powers.AbstractPower

@SpirePatch(
    clz = AbstractPower::class,
    method = "renderAmount"
)
object WidePowerAmountShift {
    @JvmStatic
    fun Prefix(__instance: AbstractPower, sb: SpriteBatch, @ByRef(type = "float") x: Array<Float>, y: Float, c: Color) {
        if (RenderWide.isWide(__instance)) {
            x[0] += AbstractCreature::class.getPrivate<Float>("POWER_ICON_PADDING_X") * 0.5f
        }
    }
}
