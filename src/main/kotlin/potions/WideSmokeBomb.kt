package com.evacipated.cardcrawl.mod.widepotions.potions

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch
import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.monsters.AbstractMonster
import com.megacrit.cardcrawl.potions.AbstractPotion
import com.megacrit.cardcrawl.potions.SmokeBomb
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect
import javassist.CtBehavior
import javassist.CtMethod

class WideSmokeBomb : WidePotion(SmokeBomb()) {
    override fun use(target: AbstractCreature?) {
        super.use(target)
        AbstractDungeon.getCurrRoom().smoked = false
        addToBot(object : AbstractGameAction() {
            override fun update() {
                AbstractDungeon.getMonsters().monsters
                    .filterNot { it.isDeadOrEscaped }
                    .forEach { AbstractDungeon.effectList.add(SmokeBombEffect(it.hb.cX, it.hb.cY)) }
                isDone = true
            }
        })
    }

    override fun canUse(): Boolean {
        if (AbstractPotion_canUse()) {
            return AbstractDungeon.getMonsters().monsters.none { it.type == AbstractMonster.EnemyType.BOSS }
        }
        return false
    }

    private fun AbstractPotion_canUse(): Boolean {
        throw NotImplementedError()
    }

    @SpirePatch(
        clz = WideSmokeBomb::class,
        method = "AbstractPotion_canUse"
    )
    object Patch {
        @JvmStatic
        fun Raw(ctBehavior: CtBehavior) {
            if (ctBehavior is CtMethod) {
                val pool = ctBehavior.declaringClass.classPool
                val ctPotion = pool.get(AbstractPotion::class.qualifiedName)
                val canUse = ctPotion.getDeclaredMethod("canUse", arrayOf())

                ctBehavior.setBody(canUse, null)
            }
        }
    }
}
