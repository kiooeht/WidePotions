package com.evacipated.cardcrawl.mod.widepotions.extensions

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.potions.AbstractPotion

val AbstractPlayer.widepotions: MutableList<AbstractPotion>
    get() = WidePotionSlot.Field.widepotions.get(this)
