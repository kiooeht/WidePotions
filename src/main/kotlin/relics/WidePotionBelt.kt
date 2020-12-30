package com.evacipated.cardcrawl.mod.widepotions.relics

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.getPrivate
import com.evacipated.cardcrawl.mod.widepotions.extensions.privateMethod
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivate
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivateFinal
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.PotionSlot
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.PotionBelt
import com.megacrit.cardcrawl.ui.panels.TopPanel

class WidePotionBelt : PotionBelt(), IsWidePotion {
    init {
        if (!strings.NAME.isNullOrEmpty()) {
            setPrivateFinal("name", strings.NAME, clazz = AbstractRelic::class.java)
        }
        if (!strings.FLAVOR.isNullOrEmpty()) {
            flavorText = strings.FLAVOR
        }
        description = strings.DESCRIPTIONS[0].format(EXTRA_SLOTS)
        tips[0] = PowerTip(name, description)
        privateMethod("initializeTips", clazz = AbstractRelic::class).invoke<Unit>()

        hb = MyHitbox(hb)
        hb.resize(PAD_X * 2, PAD_X)
    }

    override fun onEquip() {
        repeat(EXTRA_SLOTS) {
            ++AbstractDungeon.player.potionSlots
            AbstractDungeon.player.potions.add(PotionSlot(AbstractDungeon.player.potionSlots - 1))
        }

        val floorX = TopPanel::class.getPrivate<Float>("floorX")
        TopPanel::class.setPrivate("floorX", floorX + 40 * Settings.scale)
        AbstractDungeon.topPanel.setupAscensionMode()
    }

    override fun update() {
        if (hb !is MyHitbox) {
            hb = MyHitbox(hb)
            hb.resize(PAD_X * 2, PAD_X)
        }
        super.update()
    }

    override fun makeCopy(): AbstractRelic = WidePotionBelt()

    companion object {
        private val strings by lazy { CardCrawlGame.languagePack.getRelicStrings(WidePotionsMod.makeID(ID)) }
        private const val EXTRA_SLOTS = 4
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
