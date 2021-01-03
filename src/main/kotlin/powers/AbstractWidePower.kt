package com.evacipated.cardcrawl.mod.widepotions.powers

import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.localization.PowerStrings
import com.megacrit.cardcrawl.powers.AbstractPower

abstract class AbstractWidePower(
    id: String,
    regionName: String
) : AbstractPower(), IsWidePotion {
    protected val powerStrings: PowerStrings
    protected val NAME: String
    protected val DESCRIPTIONS: Array<String>

    init {
        ID = id
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID)
        NAME = powerStrings.NAME
        name = NAME
        DESCRIPTIONS = powerStrings.DESCRIPTIONS
        loadRegion(regionName)
    }
}
