package com.evacipated.cardcrawl.mod.widepotions

import basemod.BaseMod
import basemod.DevConsole
import basemod.devcommands.ConsoleCommand
import com.evacipated.cardcrawl.mod.widepotions.extensions.canBeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.makeWide
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.PotionHelper
import com.megacrit.cardcrawl.potions.AbstractPotion
import java.util.*

class WidePotionCommand : ConsoleCommand() {
    init {
        minExtraTokens = 1
        maxExtraTokens = 1
        requiresPlayer = true
    }

    override fun execute(tokens: Array<out String>, depth: Int) {
        if (PotionHelper.potions == null || PotionHelper.potions.isEmpty()) {
            DevConsole.log("cannot use widepotion command when potions are not initialized")
            return
        }

        var potionID = ""
        for (i in 1 until tokens.size) {
            potionID += tokens[i]
            if (i != tokens.size - 1) {
                potionID += " "
            }
        }

        if (BaseMod.underScorePotionIDs.containsKey(potionID)) {
            potionID = BaseMod.underScorePotionIDs.get(potionID)!!
        }

        val allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true)
        var p: AbstractPotion? = null
        if (allPotions.contains(potionID)) {
            p = PotionHelper.getPotion(potionID)
        } else if (allPotions.contains("$potionID Potion")) {
            p = PotionHelper.getPotion("$potionID Potion")
        }

        if (p == null) {
            DevConsole.log("invalid potion id")
            return
        }

        if (!p.canBeWide()) {
            DevConsole.log("$potionID cannot be wide")
            return
        }

        AbstractDungeon.player.obtainPotion(p.makeWide())
    }

    override fun extraOptions(tokens: Array<out String>, depth: Int): ArrayList<String> {
        val result = arrayListOf<String>()

        val allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true)
        result.addAll(allPotions.map { it.replace(' ', '_') })

        if (result.contains(tokens[depth])) {
            complete = true
        }

        return result
    }
}
