package com.evacipated.cardcrawl.mod.widepotions.potions

import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.monsters.AbstractMonster
import com.megacrit.cardcrawl.potions.SmokeBomb
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect

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
}
