package com.evacipated.cardcrawl.mod.widepotions.relics

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnUsePotionRelic
import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.privateMethod
import com.evacipated.cardcrawl.mod.widepotions.extensions.setPrivateFinal
import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.megacrit.cardcrawl.actions.common.HealAction
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.ToyOrnithopter
import com.megacrit.cardcrawl.rooms.AbstractRoom

class WideToyOrnithopter : ToyOrnithopter(), BetterOnUsePotionRelic, IsWidePotion {
    init {
        if (!strings.NAME.isNullOrEmpty()) {
            setPrivateFinal("name", strings.NAME, clazz = AbstractRelic::class.java)
        }
        if (!strings.FLAVOR.isNullOrEmpty()) {
            flavorText = strings.FLAVOR
        }
        if (!strings.DESCRIPTIONS.isNullOrEmpty()) {
            description = updatedDescription
        }
        tips[0] = PowerTip(name, description)
        privateMethod("initializeTips", clazz = AbstractRelic::class).invoke<Unit>(this)

        hb = MyHitbox(hb)
        hb.resize(PAD_X * 2, PAD_X)
    }

    override fun getUpdatedDescription(): String {
        return super.getUpdatedDescription() + strings.DESCRIPTIONS[0].format(HEAL_AMT)
    }

    override fun onUsePotion() {
        // NOP
    }

    override fun betterOnUsePotion(potion: AbstractPotion?) {
        val healAmount = HEAL_AMT * if (potion?.isWide() == true) { 2 } else { 1 }
        flash()
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(RelicAboveCreatureAction(AbstractDungeon.player, this))
            addToBot(HealAction(AbstractDungeon.player, AbstractDungeon.player, healAmount))
        } else {
            AbstractDungeon.player.heal(healAmount)
        }
    }

    override fun update() {
        if (hb !is MyHitbox) {
            hb = MyHitbox(hb)
            hb.resize(PAD_X * 2, PAD_X)
        }
        super.update()
    }

    override fun makeCopy(): AbstractRelic = WideToyOrnithopter()

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
