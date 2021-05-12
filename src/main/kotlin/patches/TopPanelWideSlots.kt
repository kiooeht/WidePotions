package com.evacipated.cardcrawl.mod.widepotions.patches

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.evacipated.cardcrawl.mod.widepotions.extensions.scale
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.MathHelper
import com.megacrit.cardcrawl.helpers.TipHelper
import com.megacrit.cardcrawl.helpers.input.InputHelper
import com.megacrit.cardcrawl.ui.panels.TopPanel
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess

object TopPanelWideSlots {
    @SpirePatch2(
        clz = AbstractPlayer::class,
        method = "adjustPotionPositions"
    )
    object AdjustPotionPositions {
        @JvmStatic
        fun Postfix(__instance: AbstractPlayer) {
            for (i in 0 until __instance.widepotions.size) {
                __instance.widepotions[i].adjustPosition(-i - 1)
            }
        }
    }

    @SpirePatch2(
        clz = TopPanel::class,
        method = "updatePotions"
    )
    object Update {
        @JvmStatic
        fun Postfix(__instance: TopPanel) {
            for (p in AbstractDungeon.player.widepotions) {
                p.hb.update()

                if (!p.isObtained) {
                    continue
                }

                if (p is WidePotionSlot) {
                    if (p.hb.hovered) {
                        p.scale = 1.3f.scale()
                        continue
                    }
                    p.scale = 1.scale()
                    continue
                }

                if (p.hb.justHovered) {
                    if (MathUtils.randomBoolean()) {
                        CardCrawlGame.sound.play("POTION_1", 0.1f)
                    } else {
                        CardCrawlGame.sound.play("POTION_3", 0.1f)
                    }
                }

                if (p.hb.hovered) {
                    p.scale = 1.4f.scale()
                    if (AbstractDungeon.player.hoveredCard == null && InputHelper.justClickedLeft) {
                        InputHelper.justClickedLeft = false
                        __instance.potionUi.open(p.slot, p)
                    }
                    continue
                }

                p.scale = MathHelper.scaleLerpSnap(p.scale, Settings.scale)
            }
        }
    }

    @SpirePatch2(
        clz = TopPanel::class,
        method = "renderPotions"
    )
    object Render {
        @JvmStatic
        fun Postfix(sb: SpriteBatch) {
            for (p in AbstractDungeon.player.widepotions) {
                if (p.isObtained) {
                    p.renderOutline(sb)
                    p.render(sb)
                    if (p.hb.hovered) {
                        p.renderShiny(sb)
                    }
                }
                p.hb.render(sb)
            }
        }

        @JvmStatic
        fun Instrument(): ExprEditor =
            object : ExprEditor() {
                override fun edit(f: FieldAccess) {
                    if (f.isReader && f.className == AbstractPlayer::class.qualifiedName && f.fieldName == "potionSlots") {
                        f.replace(
                                "\$_ = \$proceed(\$\$) + ${Render::class.qualifiedName}.widepotionSlots() * 2;"
                        )
                    }
                }
            }

        @JvmStatic
        fun widepotionSlots(): Int {
            return AbstractDungeon.player.widepotions.size
        }
    }

    @SpirePatch2(
        clz = TopPanel::class,
        method = "renderPotionTips"
    )
    object RenderTips {
        @JvmStatic
        fun Postfix(__instance: TopPanel, ___TIP_OFF_X: Float, ___TIP_Y: Float) {
            if (!Settings.hideTopBar && __instance.potionUi.isHidden) {
                AbstractDungeon.player.widepotions
                    .filter { it.hb.hovered }
                    .forEach { TipHelper.queuePowerTips(InputHelper.mX - ___TIP_OFF_X, ___TIP_Y, it.tips) }
            }
        }
    }
}
