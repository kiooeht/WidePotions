package com.evacipated.cardcrawl.mod.widepotions.potions

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.potions.AbstractPotion

class WidePotionRightHalf(
    val otherHalf: AbstractPotion
) : AbstractPotion(
    "",
    "",
    PotionRarity.PLACEHOLDER,
    PotionSize.T,
    PotionColor.NONE
) {
    init {
        description = ""

        hb.resize(0f, 0f)
        hb.move(-1000f, -1000f)
    }

    override fun use(target: AbstractCreature?) {}

    override fun getPotency(ascensionLevel: Int): Int = 0

    override fun makeCopy(): AbstractPotion =
        WidePotionRightHalf(otherHalf)

    override fun adjustPosition(slot: Int) {}

    override fun flash() {}

    override fun renderLightOutline(sb: SpriteBatch?) {}
    override fun renderOutline(sb: SpriteBatch?) {}
    override fun renderOutline(sb: SpriteBatch?, c: Color?) {}
    override fun renderShiny(sb: SpriteBatch?) {}
    override fun render(sb: SpriteBatch?) {}
    override fun shopRender(sb: SpriteBatch?) {}
    override fun labRender(sb: SpriteBatch?) {}
}
