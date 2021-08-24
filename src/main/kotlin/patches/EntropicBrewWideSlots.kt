package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.extensions.canBeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.makeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.EntropicBrew
import com.megacrit.cardcrawl.rooms.AbstractRoom
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect

@SpirePatch2(
    clz = EntropicBrew::class,
    method = "use"
)
object EntropicBrewWideSlots {
    @JvmStatic
    fun Postfix() {
        repeat(AbstractDungeon.player.widepotions.size) {
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                var potion: AbstractPotion
                do {
                    potion = AbstractDungeon.returnRandomPotion(true)
                } while (!potion.canBeWide())
                AbstractDungeon.actionManager.addToBottom(ObtainPotionAction(potion.makeWide()))
            } else {
                var potion: AbstractPotion
                do {
                    potion = AbstractDungeon.returnRandomPotion()
                } while (!potion.canBeWide())
                AbstractDungeon.effectsQueue.add(ObtainPotionEffect(potion.makeWide()))
            }
        }
    }
}
