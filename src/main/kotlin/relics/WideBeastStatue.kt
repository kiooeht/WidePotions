package com.evacipated.cardcrawl.mod.widepotions.relics

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.privateMethod
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivateFinal
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.WhiteBeast

class WideBeastStatue : WhiteBeast(), IsWidePotion {
    init {
        if (!strings.NAME.isNullOrEmpty()) {
            setPrivateFinal("name", strings.NAME, clazz = AbstractRelic::class.java)
        }
        if (!strings.FLAVOR.isNullOrEmpty()) {
            flavorText = strings.FLAVOR
        }
        if (!strings.DESCRIPTIONS.isNullOrEmpty()) {
            description = strings.DESCRIPTIONS[0]
        }
        tips[0] = PowerTip(name, description)
        privateMethod("initializeTips", clazz = AbstractRelic::class).invoke<Unit>()

        hb = MyHitbox(hb)
        hb.resize(PAD_X * 2, PAD_X)
    }

    override fun update() {
        if (hb !is MyHitbox) {
            hb = MyHitbox(hb)
            hb.resize(PAD_X * 2, PAD_X)
        }
        super.update()
    }

    override fun makeCopy(): AbstractRelic = WideBeastStatue()

    companion object {
        private val strings by lazy { CardCrawlGame.languagePack.getRelicStrings(WidePotionsMod.makeID(ID)) }
    }

    private class MyHitbox : Hitbox {
        constructor(hb: Hitbox) : this(hb.x, hb.y, hb.width, hb.height)
        constructor(width: Float, height: Float) : super(width, height)
        constructor(x: Float, y: Float, width: Float, height: Float) : super(x, y, width, height)

        override fun move(cX: Float, cY: Float) {
            super.move(cX + AbstractRelic.PAD_X / 2f, cY)
        }
    }
}
