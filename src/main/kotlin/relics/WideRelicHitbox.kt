package com.evacipated.cardcrawl.mod.widepotions.relics

import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.relics.AbstractRelic

internal class WideRelicHitbox : Hitbox {
    constructor(hb: Hitbox) : this(hb.x, hb.y, hb.width, hb.height)
    constructor(width: Float, height: Float) : super(width, height)
    constructor(x: Float, y: Float, width: Float, height: Float) : super(x, y, width, height)

    override fun move(cX: Float, cY: Float) {
        super.move(cX + AbstractRelic.PAD_X / 2f, cY)
    }
}
