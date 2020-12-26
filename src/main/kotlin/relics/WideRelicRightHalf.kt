package com.evacipated.cardcrawl.mod.widepotions.relics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.megacrit.cardcrawl.relics.AbstractRelic

class WideRelicRightHalf : AbstractRelic(
    WidePotionsMod.makeID("RightHalf"),
    "",
    RelicTier.SPECIAL,
    LandingSound.FLAT
) {
    init {
        hb.resize(0f, 0f)
    }

    override fun playLandingSFX() {}

    override fun render(sb: SpriteBatch?) {}
    override fun render(sb: SpriteBatch?, renderAmount: Boolean, outlineColor: Color?) {}
    override fun renderBossTip(sb: SpriteBatch?) {}
    override fun renderCounter(sb: SpriteBatch?, inTopPanel: Boolean) {}
    override fun renderFlash(sb: SpriteBatch?, inTopPanel: Boolean) {}
    override fun renderInTopPanel(sb: SpriteBatch?) {}
    override fun renderLock(sb: SpriteBatch?, outlineColor: Color?) {}
    override fun renderOutline(sb: SpriteBatch?, inTopPanel: Boolean) {}
    override fun renderOutline(c: Color?, sb: SpriteBatch?, inTopPanel: Boolean) {}
    override fun renderTip(sb: SpriteBatch?) {}
    override fun renderWithoutAmount(sb: SpriteBatch?, c: Color?) {}

    override fun makeCopy(): AbstractRelic =
        WideRelicRightHalf()
}
