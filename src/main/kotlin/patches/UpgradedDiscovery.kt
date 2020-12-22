package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction
import com.megacrit.cardcrawl.cards.AbstractCard

@SpirePatch(
    clz = DiscoveryAction::class,
    method = "generateCardChoices"
)
object UpgradedDiscovery {
    private val upgradeCardsMap = mutableSetOf<DiscoveryAction>()

    var DiscoveryAction.upgradeCards: Boolean
        get() = upgradeCardsMap.contains(this)
        set(value) {
            if (value) {
                upgradeCardsMap.add(this)
            } else {
                upgradeCardsMap.remove(this)
            }
        }

    @JvmStatic
    fun Postfix(__result: ArrayList<AbstractCard>, __instance: DiscoveryAction, type: AbstractCard.CardType): ArrayList<AbstractCard> {
        if (__instance.upgradeCards) {
            __result.forEach(AbstractCard::upgrade)
            __instance.upgradeCards = false
        }
        return __result
    }
}
