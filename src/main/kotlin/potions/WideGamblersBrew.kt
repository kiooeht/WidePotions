package com.evacipated.cardcrawl.mod.widepotions.potions

import com.megacrit.cardcrawl.actions.utility.ScryAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.helpers.PowerTip
import com.megacrit.cardcrawl.potions.GamblersBrew

class WideGamblersBrew : WidePotion(GamblersBrew()) {
    override fun initializeData() {
        potency = getPotency()
        description = customDescription?.format(potency) ?: "[MISSING_TEXT]"
        tips.clear()
        tips.add(PowerTip(name, description))
    }

    override fun use(target: AbstractCreature?) {
        addToBot(ScryAction(potency))
        super.use(target)
    }

    override fun getPotency(ascensionLevel: Int): Int {
        return 10
    }
}
