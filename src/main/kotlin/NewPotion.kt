package com.evacipated.cardcrawl.mod.widepotions

import com.megacrit.cardcrawl.potions.AbstractPotion

@FunctionalInterface
interface NewPotion {
    fun newPotion(): AbstractPotion
}
