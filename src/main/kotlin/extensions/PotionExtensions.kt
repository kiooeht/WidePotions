@file:JvmName("PotionExtensions")

package com.evacipated.cardcrawl.mod.widepotions.extensions

import com.evacipated.cardcrawl.mod.widepotions.potions.IsWidePotion
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.megacrit.cardcrawl.potions.AbstractPotion

fun AbstractPotion.isWide(): Boolean {
    return this is IsWidePotion
}

fun AbstractPotion.canBeWide(): Boolean {
    return WidePotion.whitelist.contains(ID) || WidePotion.whitemap.containsKey(ID)
}

fun AbstractPotion.makeWide(): AbstractPotion {
    if (WidePotion.whitelist.contains(ID)) {
        return WidePotion(this)
    } else if (WidePotion.whitemap.containsKey(ID)) {
        return WidePotion.whitemap[ID]?.makeCopy() ?: throw NullPointerException()
    }
    throw NullPointerException()
}
