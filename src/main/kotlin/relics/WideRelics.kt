package com.evacipated.cardcrawl.mod.widepotions.relics

import basemod.BaseMod
import basemod.helpers.RelicType
import com.megacrit.cardcrawl.helpers.RelicLibrary
import com.megacrit.cardcrawl.relics.AbstractRelic
import com.megacrit.cardcrawl.relics.PotionBelt
import com.megacrit.cardcrawl.relics.WhiteBeast

object WideRelics {
    private val wideRelics = mapOf<String, Pair<() -> AbstractRelic, () -> AbstractRelic>>(
        PotionBelt.ID to Pair(::PotionBelt, ::WidePotionBelt),
    )

    fun replaceRelic(id: String, wide: Boolean): Boolean {
        wideRelics[id]?.let {
            val (relic, wideRelic) = it
            BaseMod.removeRelic(RelicLibrary.getRelic(id), RelicType.SHARED)
            BaseMod.addRelic(
                if (wide) { wideRelic() } else { relic() },
                RelicType.SHARED
            )
            return true
        }
        return false
    }
}
