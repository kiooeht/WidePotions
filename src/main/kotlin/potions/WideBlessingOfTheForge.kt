package com.evacipated.cardcrawl.mod.widepotions.potions

import com.megacrit.cardcrawl.actions.unique.ApotheosisAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.BlessingOfTheForge
import com.megacrit.cardcrawl.rooms.AbstractRoom

class WideBlessingOfTheForge : WidePotion(BlessingOfTheForge()) {
    override fun use(target: AbstractCreature?) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(ApotheosisAction())
        }
    }
}
