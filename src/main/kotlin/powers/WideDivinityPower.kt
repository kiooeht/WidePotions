package com.evacipated.cardcrawl.mod.widepotions.powers

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.stances.DivinityStance
import com.megacrit.cardcrawl.vfx.stance.DivinityParticleEffect
import com.megacrit.cardcrawl.vfx.stance.StanceAuraEffect
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

class WideDivinityPower(
    owner: AbstractCreature
) : AbstractWidePower(POWER_ID, "mantra") {
    companion object {
        @JvmField
        val POWER_ID = WidePotionsMod.makeID("WideDivinity")
    }

    init {
        this.owner = owner
        updateDescription()
    }

    override fun updateDescription() {
        description = DESCRIPTIONS[0]
    }

    @SpirePatch(
        clz = DivinityStance::class,
        method = "atStartOfTurn"
    )
    object Patch {
        @JvmStatic
        fun Postfix(__instance: DivinityStance) {
            if (AbstractDungeon.player?.hasPower(POWER_ID) == true) {
                AbstractDungeon.actionManager.addToBottom(
                    RemoveSpecificPowerAction(
                        AbstractDungeon.player,
                        AbstractDungeon.player,
                        POWER_ID
                    )
                )
                AbstractDungeon.actionManager.addToBottom(
                    ChangeStanceAction(DivinityStance.STANCE_ID)
                )
            }
        }
    }

    @SpirePatches(
        SpirePatch(
            clz = StanceAuraEffect::class,
            method = "render"
        ),
        SpirePatch(
            clz = DivinityParticleEffect::class,
            method = "render"
        )
    )
    object WideVFX {
        @JvmStatic
        fun Instrument(): ExprEditor =
            object : ExprEditor() {
                override fun edit(m: MethodCall) {
                    if (m.methodName == "draw") {
                        m.replace(
                            "if (${AbstractDungeon::class.qualifiedName}.player.hasPower(${WideDivinityPower::class.qualifiedName}.POWER_ID)) {" +
                                    "\$8 *= 2f;" +
                                    "\$9 *= 2f;" +
                                    "}" +
                                    "\$_ = \$proceed(\$\$);"
                        )
                    }
                }
            }
    }
}
