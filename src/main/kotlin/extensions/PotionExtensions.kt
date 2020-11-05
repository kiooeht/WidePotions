package com.evacipated.cardcrawl.mod.widepotions.extensions

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.megacrit.cardcrawl.potions.AbstractPotion

fun AbstractPotion.isWide(): Boolean {
    return this is WidePotion
}

fun AbstractPotion.makeWide(): AbstractPotion {
    return WidePotion(this)
}
