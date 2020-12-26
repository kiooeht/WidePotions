package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.mod.widepotions.patches.UpgradedDiscovery.upgradeCards
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.potions.ColorlessPotion

class WideColorlessPotion : WidePotion(ColorlessPotion()) {
    override fun use(target: AbstractCreature?) {
        addToBot(DiscoveryAction(true, potency).apply { upgradeCards = true })
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return potion.getPotency(ascensionLevel) * 2
    }
}
