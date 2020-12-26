package com.evacipated.cardcrawl.mod.widepotions

import basemod.*
import basemod.abstracts.CustomSavable
import basemod.helpers.RelicType
import basemod.interfaces.EditStringsSubscriber
import basemod.interfaces.PostInitializeSubscriber
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.makeWide
import com.evacipated.cardcrawl.mod.widepotions.helpers.AssetLoader
import com.evacipated.cardcrawl.mod.widepotions.relics.WidePotionBelt
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.helpers.ImageMaster
import com.megacrit.cardcrawl.helpers.RelicLibrary
import com.megacrit.cardcrawl.localization.PotionStrings
import com.megacrit.cardcrawl.localization.RelicStrings
import com.megacrit.cardcrawl.relics.PotionBelt
import java.io.IOException
import java.lang.Integer.min
import java.util.*

@SpireInitializer
class WidePotionsMod :
    PostInitializeSubscriber,
    EditStringsSubscriber
{
    companion object Statics {
        val ID: String
        val NAME: String

        val assets = AssetLoader()
        private var config: SpireConfig? = null

        init {
            var tmpID = "widepotions"
            var tmpNAME = "Wide Potions"
            val properties = Properties()
            try {
                properties.load(WidePotionsMod::class.java.getResourceAsStream("/META-INF/" + tmpID + "_version.prop"))
                tmpID = properties.getProperty("id")
                tmpNAME = properties.getProperty("name")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ID = tmpID
            NAME = tmpNAME
        }

        @Suppress("unused")
        @JvmStatic
        fun initialize() {
            BaseMod.subscribe(WidePotionsMod())

            try {
                val defaults = Properties()
                defaults["WideChance"] = 0.4f.toString()
                defaults["WidePotionBelt"] = true.toString()
                config = SpireConfig(ID, "config", defaults)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun makeID(id: String) = "$ID:$id"
        fun assetPath(path: String) = "${ID}Assets/$path"

        fun wideChance(): Float {
            return if (config != null) {
                config!!.getFloat("WideChance")
            } else {
                0.4f
            }
        }

        fun widePotionBelt(): Boolean {
            return config?.getBool("WidePotionBelt") ?: true
        }
    }

    override fun receivePostInitialize() {
        val settingsPanel = ModPanel()
        ModLabel("Wide Chance", 400f, 730f, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel) {}.let {
            settingsPanel.addUIElement(it)
        }
        val chanceSlider = ModMinMaxSlider(
            "",
            400f,
            700f,
            5f,
            100f,
            wideChance() * 100f,
            "%.0f%%",
            settingsPanel
        ) { slider ->
            if (config != null) {
                config!!.setFloat("WideChance", slider.value / 100f)
                try {
                    config!!.save()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        settingsPanel.addUIElement(chanceSlider)

        val potionBeltToggle = ModLabeledToggleButton(
            "Wide Potion Belt",
            400f,
            620f,
            Settings.CREAM_COLOR,
            FontHelper.charDescFont,
            widePotionBelt(),
            settingsPanel,
            {}
        ) { toggle ->
            config?.let {
                replacePotionBelt(toggle.enabled)
                it.setBool("WidePotionBelt", toggle.enabled)
                try {
                    it.save()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        settingsPanel.addUIElement(potionBeltToggle)

        BaseMod.registerModBadge(
            ImageMaster.loadImage(assetPath("images/modBadge.png")),
            NAME,
            "kiooeht",
            "TODO",
            settingsPanel
        )

        if (widePotionBelt()) {
            replacePotionBelt(true)
        }

        BaseMod.addSaveField<List<Boolean>?>(makeID("IsWide"), object : CustomSavable<List<Boolean>?> {
            override fun onSave(): List<Boolean>? {
                return AbstractDungeon.player?.potions
                    ?.map { p -> p.isWide() }
            }

            override fun onLoad(save: List<Boolean>?) {
                if (save != null) {
                    for (i in 0 until min(save.size, AbstractDungeon.player.potions.size)) {
                        if (save[i]) {
                            AbstractDungeon.player.potions[i] = AbstractDungeon.player.potions[i].makeWide()
                            AbstractDungeon.player.potions[i].setAsObtained(i)
                            AbstractDungeon.player.potions[i].initializeData()
                        }
                    }
                }
            }
        })
    }

    private fun replacePotionBelt(wide: Boolean) {
        BaseMod.removeRelic(RelicLibrary.getRelic(PotionBelt.ID), RelicType.SHARED)
        if (wide) {
            BaseMod.addRelic(WidePotionBelt(), RelicType.SHARED)
        } else {
            BaseMod.addRelic(PotionBelt(), RelicType.SHARED)
        }
    }

    private fun makeLocPath(language: Settings.GameLanguage, filename: String): String {
        val langPath = when (language) {
            else -> "eng"
        }
        return assetPath("localization/$langPath/$filename.json")
    }

    private fun loadLocFile(language: Settings.GameLanguage, stringType: Class<*>) {
        BaseMod.loadCustomStringsFile(stringType, makeLocPath(language, stringType.simpleName))
    }

    private fun loadLocFiles(language: Settings.GameLanguage) {
        loadLocFile(language, PotionStrings::class.java)
        loadLocFile(language, RelicStrings::class.java)
    }

    override fun receiveEditStrings() {
        loadLocFiles(Settings.GameLanguage.ENG)
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocFiles(Settings.language)
        }
    }
}
