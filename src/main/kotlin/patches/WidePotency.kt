package com.evacipated.cardcrawl.mod.widepotions.patches

import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.AbstractPotion
import javassist.CtBehavior

@SpirePatch(
    clz = AbstractPotion::class,
    method = "getPotency",
    paramtypez = []
)
object WidePotency {
    var isWidePotion: WidePotion? = null

    @JvmStatic
    @SpireInsertPatch(
        locator = Locator::class,
        localvars = ["potency"]
    )
    fun Insert(__instance: AbstractPotion, @ByRef(type = "int") potency: Array<Int>) {
        val wide = isWidePotion
        if (wide != null) {
            potency[0] = wide.getPotency(AbstractDungeon.ascensionLevel)
        }
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior): IntArray {
            val finalMatcher = Matcher.MethodCallMatcher(AbstractPlayer::class.java, "hasRelic")
            return LineFinder.findInOrder(ctBehavior, finalMatcher)
        }
    }
}
