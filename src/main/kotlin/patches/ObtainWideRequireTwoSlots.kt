package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.PotionSlot

@SpirePatch(
    clz = AbstractPlayer::class,
    method = "obtainPotion",
    paramtypez = [AbstractPotion::class]
)
object ObtainWideRequireTwoSlots {
    @JvmStatic
    fun Prefix(__instance: AbstractPlayer, potionToObtain: AbstractPotion): SpireReturn<Boolean> {
        // No slots
        if (__instance.potions.isEmpty() && __instance.widepotions.isEmpty()) {
            return SpireReturn.Continue()
        }
        if (!potionToObtain.isWide()) {
            // No wide slots, so no need to check if a wide potion can be moved to a wide slot
            if (__instance.widepotions.isEmpty()) {
                return SpireReturn.Continue()
            }
            // No empty wide slots
            if (__instance.widepotions.none { it is WidePotionSlot }) {
                return SpireReturn.Continue()
            }
            // No wide potions that could be moved to a wide slot
            if (__instance.potions.none { it.isWide() }) {
                return SpireReturn.Continue()
            }

            if (__instance.potions.none { it is PotionSlot }) {
                // Move wide potion to wide slot to make room for this new potion
                val index = __instance.potions.indexOfFirst { it.isWide() }
                val wideSlot = __instance.widepotions.indexOfFirst { it is WidePotionSlot }
                val widePotion = __instance.potions[index]
                AbstractDungeon.topPanel.destroyPotion(index)
                __instance.widepotions[wideSlot] = widePotion
                widePotion.slot = -wideSlot - 1
                __instance.adjustPotionPositions()
            }

            // Let normal code handle obtaining new potion
            return SpireReturn.Continue()
        }

        // Check for empty wide potion slots
        for (i in 0 until __instance.widepotions.size) {
            if (__instance.widepotions[i] is WidePotionSlot) {
                __instance.widepotions[i] = potionToObtain
                potionToObtain.setAsObtained(-i - 1)
                potionToObtain.flash()
                AbstractPotion.playPotionSound()

                return SpireReturn.Return(true)
            }
        }

        var emptySlots = if (__instance.potions[0] is PotionSlot) {
            1
        } else {
            0
        }
        var index = -1
        for (i in 1 until __instance.potions.size) {
            if (__instance.potions[i] is PotionSlot && __instance.potions[i-1] is PotionSlot) {
                index = i - 1
                break
            } else if (__instance.potions[i] is PotionSlot) {
                ++emptySlots
            }
        }

        if (index >= 0) {
            __instance.potions[index] = potionToObtain
            potionToObtain.setAsObtained(index)
            potionToObtain.flash()
            AbstractPotion.playPotionSound()

            return SpireReturn.Return(true)
        }

        if (emptySlots >= 2) {
            // Move potions around to make space
            val newPotions = ArrayList<AbstractPotion>(__instance.potions.size)
            for (p in __instance.potions) {
                if (p !is PotionSlot) {
                    newPotions.add(p)
                }
            }
            for (i in 0 until (__instance.potions.size - newPotions.size)) {
                newPotions.add(PotionSlot(newPotions.size))
            }
            if (__instance.potions.size != newPotions.size) {
                println("SOMETHING IS VERY WRONG")
            } else {
                __instance.potions.clear()
                __instance.potions.addAll(newPotions)
                __instance.potions.forEachIndexed { i, p ->
                    p.slot = i
                    if (p is WidePotion) {
                        p.potion.slot = i
                    }
                }
                __instance.adjustPotionPositions()

                return Prefix(__instance, potionToObtain)
            }
        }

        AbstractDungeon.topPanel.flashRed()
        return SpireReturn.Return(false)
    }
}
