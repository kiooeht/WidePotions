package com.evacipated.cardcrawl.mod.widepotions.potions

import com.megacrit.cardcrawl.potions.AbstractPotion

interface IsWidePotion {
    fun unWide(): AbstractPotion? {
        return null
    }
}
