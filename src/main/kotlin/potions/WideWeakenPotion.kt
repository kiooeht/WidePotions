package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.WeakenPotion
import com.megacrit.cardcrawl.powers.WeakPower
import com.megacrit.cardcrawl.ui.panels.TopPanel

class WideWeakenPotion : WeakenPotion(), IsWidePotion {
    init {
        val strings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID("WidePotion"))
        name = strings.NAME.format(name)

        isThrown = true
        targetRequired = false

        initializeData()

        hb.resize(hb.width * 2, hb.height)
    }

    override fun initializeData() {
        super.initializeData()

        description = strings.DESCRIPTIONS[0].format(potency)
        tips[0].body = description
    }

    override fun use(target: AbstractCreature?) {
        for (m in AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped) {
                addToBot(ApplyPowerAction(m, AbstractDungeon.player, WeakPower(m, potency, false), potency))
            }
        }
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return super.getPotency(ascensionLevel) * 2
    }

    override fun makeCopy(): AbstractPotion =
        WideWeakenPotion()

    override fun setAsObtained(potionSlot: Int) {
        super.setAsObtained(potionSlot)

        AbstractDungeon.player.obtainPotion(potionSlot + 1, WidePotionRightHalf(this))
    }

    override fun adjustPosition(slot: Int) {
        super.adjustPosition(slot)

        hb.move(TopPanel.potionX + (slot + 0.5f) * Settings.POTION_W, posY)
    }

    companion object {
        private val strings = CardCrawlGame.languagePack.getPotionString(WidePotionsMod.makeID(POTION_ID))
    }
}
