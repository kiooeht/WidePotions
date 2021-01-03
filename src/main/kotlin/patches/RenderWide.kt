package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.powers.AbstractPower
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.vfx.combat.GainPowerEffect
import com.megacrit.cardcrawl.vfx.combat.SilentGainPowerEffect
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

@SpirePatches(
    // Wide potions
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
    // Wide relics
    SpirePatch(
        clz = AbstractRelic::class,
        method = "render",
        paramtypez = [SpriteBatch::class]
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "render",
        paramtypez = [SpriteBatch::class, Boolean::class, Color::class]
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "renderInTopPanel"
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "renderWithoutAmount"
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "renderOutline",
        paramtypez = [SpriteBatch::class, Boolean::class]
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "renderOutline",
        paramtypez = [Color::class, SpriteBatch::class, Boolean::class]
    ),
    SpirePatch(
        clz = AbstractRelic::class,
        method = "renderFlash"
    ),
    // Wide powers
    SpirePatch(
        clz = AbstractPower::class,
        method = "renderIcons"
    ),
    SpirePatch(
        clz = GainPowerEffect::class,
        method = "render",
        paramtypez = [SpriteBatch::class, Float::class, Float::class]
    ),
    SpirePatch(
        clz = SilentGainPowerEffect::class,
        method = "render",
        paramtypez = [SpriteBatch::class, Float::class, Float::class]
    ),
)
object RenderWide {
    @JvmStatic
    fun Instrument(): ExprEditor =
        object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.methodName == "draw") {
                    val offset = when (m.enclosingClass.name) {
                        AbstractRelic::class.qualifiedName -> 0.5f
                        AbstractPower::class.qualifiedName -> 0.75f
                        else -> 0.75f
                    }
                    m.replace(
                        "if (${RenderWide::class.qualifiedName}.isWide(this)) {" +
                                "\$2 += (\$4 * $offset) * ${Settings::class.qualifiedName}.scale;" +
                                "\$8 *= 2;" +
                                "}" +
                                "\$_ = \$proceed(\$\$);"
                    )
                }
            }
        }

    @JvmStatic
    fun isWide(thing: Any?): Boolean =
        when (thing) {
            is AbstractPotion -> thing.isWide()
            is AbstractRelic -> thing is IsWidePotion
            is AbstractPower -> thing is IsWidePotion
            is GainPowerEffect, is SilentGainPowerEffect -> GainPowerEffectIsWide.Field.isWide.get(thing)
            else -> false
        }
}
