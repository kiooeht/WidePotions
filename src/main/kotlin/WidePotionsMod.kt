package com.evacipated.cardcrawl.mod.widepotions

import basemod.*
import basemod.abstracts.CustomSavable
import basemod.abstracts.CustomSavableRaw
import basemod.devcommands.ConsoleCommand
import basemod.interfaces.EditStringsSubscriber
import basemod.interfaces.PostDungeonInitializeSubscriber
import basemod.interfaces.PostInitializeSubscriber
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves.modPotionSaves
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap
import com.codedisaster.steamworks.SteamUser
import com.evacipated.cardcrawl.mod.widepotions.extensions.canBeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.isWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.makeWide
import com.evacipated.cardcrawl.mod.widepotions.extensions.widepotions
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotion
import com.evacipated.cardcrawl.mod.widepotions.potions.WidePotionSlot
import com.evacipated.cardcrawl.mod.widepotions.relics.WideRelics
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.helpers.ImageMaster
import com.megacrit.cardcrawl.helpers.PotionHelper
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration
import com.megacrit.cardcrawl.localization.PotionStrings
import com.megacrit.cardcrawl.localization.PowerStrings
import com.megacrit.cardcrawl.localization.RelicStrings
import com.megacrit.cardcrawl.localization.UIStrings
import com.megacrit.cardcrawl.relics.PotionBelt
import com.megacrit.cardcrawl.relics.Sozu
import com.megacrit.cardcrawl.relics.ToyOrnithopter
import com.megacrit.cardcrawl.relics.WhiteBeast
import com.megacrit.cardcrawl.saveAndContinue.SaveFileObfuscator
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.lang.Integer.min
import java.util.*

@SpireInitializer
class WidePotionsMod :
    PostInitializeSubscriber,
    EditStringsSubscriber,
    PostDungeonInitializeSubscriber
{
    companion object Statics {
        val ID: String
        val NAME: String

        val logger = LogManager.getLogger(WidePotionsMod::class.java)

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
                defaults["DisableSozu"] = false.toString()
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

        fun disableSozu(): Boolean {
            return config?.getBool("DisableSozu") ?: false
        }

        @Suppress("unused")
        @JvmStatic
        fun whitelistSimplePotion(potionID: String) {
            WidePotion.whitelist.add(potionID)
        }

        @Suppress("unused")
        @JvmStatic
        fun whitelistComplexPotion(potionID: String, potion: WidePotion) {
            WidePotion.whitemap.putIfAbsent(potionID, potion)
        }
    }

    override fun receivePostInitialize() {
        val TEXT = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenu"))?.TEXT ?: Array(20) { "[MISSING_TEXT]" }

        val settingsPanel = ModPanel()
        ModLabel(TEXT[0], 400f, 700f, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel) {}.let {
            settingsPanel.addUIElement(it)
        }

        val textWidth = FontHelper.getWidth(FontHelper.charDescFont, TEXT[0], 1f / Settings.scale)
        val chanceSlider = ModMinMaxSlider(
            "",
            400f + 40f + textWidth,
            707f,
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
            TEXT[1],
            400f - 40f,
            640f,
            Settings.CREAM_COLOR,
            FontHelper.charDescFont,
            widePotionBelt(),
            settingsPanel,
            {}
        ) { toggle ->
            config?.let {
                WideRelics.replaceRelic(PotionBelt.ID, toggle.enabled)
                it.setBool("WidePotionBelt", toggle.enabled)
                try {
                    it.save()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        settingsPanel.addUIElement(potionBeltToggle)

        val sozuToggle = ModLabeledToggleButton(
            TEXT[2],
            400f - 40f,
            590f,
            Settings.CREAM_COLOR,
            FontHelper.charDescFont,
            disableSozu(),
            settingsPanel,
            {}
        ) { toggle ->
            config?.let {
                it.setBool("DisableSozu", toggle.enabled)
                try {
                    it.save()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        settingsPanel.addUIElement(sozuToggle)

        // hello
        try {
            val user = ReflectionHacks.getPrivateStatic<SteamUser>(SteamIntegration::class.java, "steamUser")
            if (setOf(36371698, 86912414, 102867723).contains(user?.steamID?.accountID)) {
                val xor = ReflectionHacks.privateStaticMethod(
                    SaveFileObfuscator::class.java,
                    "xorWithKey",
                    ByteArray::class.java,
                    ByteArray::class.java
                )
                var bytes = Gdx.files.internal(assetPath("hello")).readBytes()
                bytes = xor(*arrayOf(bytes, "W I D E".toByteArray()))
                val img = ModImage(
                    880f,
                    400f,
                    Texture(Pixmap(Gdx2DPixmap(bytes, 0, bytes.size, 0))).apply {
                        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                    }
                )
                settingsPanel.addUIElement(img)
            }
        } catch (e: Exception) {}

        BaseMod.registerModBadge(
            ImageMaster.loadImage(assetPath("images/modBadge.png")),
            NAME,
            "kiooeht",
            "TODO",
            settingsPanel
        )

        ConsoleCommand.addCommand("widepotion", WidePotionCommand::class.java)

        if (widePotionBelt()) {
            WideRelics.replaceRelic(PotionBelt.ID, true)
        }
        WideRelics.replaceRelic(WhiteBeast.ID, true)
        WideRelics.replaceRelic(ToyOrnithopter.ID, true)

        BaseMod.addSaveField<List<Boolean>?>(makeID("IsWide"), object : CustomSavable<List<Boolean>?> {
            override fun onSave(): List<Boolean>? {
                return AbstractDungeon.player?.potions
                    ?.map { p -> p.isWide() }
            }

            override fun onLoad(save: List<Boolean>?) {
                if (save != null) {
                    val modPotionSaves: ModSaves.ArrayListOfJsonElement? = modPotionSaves[CardCrawlGame.saveFile]
                    for (i in 0 until min(save.size, AbstractDungeon.player.potions.size)) {
                        if (save[i]) {
                            AbstractDungeon.player.potions[i] = AbstractDungeon.player.potions[i].makeWide()
                            val wide = AbstractDungeon.player.potions[i]
                            if (WidePotion.whitemap.containsKey(wide.ID)) {
                                // is complex potion, check for CustomSavable
                                if (wide is CustomSavableRaw) {
                                    wide.onLoadRaw(
                                        if (modPotionSaves == null || i >= modPotionSaves.size) {
                                            null
                                        } else {
                                            modPotionSaves[i]
                                        }
                                    )
                                }
                            }
                            wide.setAsObtained(i)
                            wide.initializeData()
                        }
                    }
                }
            }
        })

        BaseMod.addSaveField<List<String?>?>(makeID("widepotions"), object : CustomSavable<List<String?>?> {
            override fun onSave(): List<String?>? {
                return AbstractDungeon.player?.widepotions
                    ?.map { p ->
                        if (p is WidePotionSlot) {
                            null
                        } else {
                            p.ID
                        }
                    }
            }

            override fun onLoad(save: List<String?>?) {
                if (save != null) {
                    for (i in save.indices) {
                        val potionId = save[i]
                        if (potionId != null && PotionHelper.isAPotion(potionId)) {
                            val potion = PotionHelper.getPotion(potionId)
                            if (potion.canBeWide()) {
                                val widePotion = potion.makeWide()
                                AbstractDungeon.player.widepotions.add(widePotion)
                                widePotion.setAsObtained(-i - 1)
                                continue
                            }
                        }
                        // fallback
                        AbstractDungeon.player.widepotions.add(WidePotionSlot(-i - 1))
                    }
                    AbstractDungeon.player.adjustPotionPositions()
                }
            }
        })
    }

    private fun makeLocPath(language: Settings.GameLanguage, filename: String): String {
        val langPath = when (language) {
            Settings.GameLanguage.ZHS -> "zhs"
            else -> "eng"
        }
        return assetPath("localization/$langPath/$filename.json")
    }

    private fun loadLocFile(language: Settings.GameLanguage, stringType: Class<*>) {
        BaseMod.loadCustomStringsFile(stringType, makeLocPath(language, stringType.simpleName))
    }

    private fun loadLocFiles(language: Settings.GameLanguage) {
        loadLocFile(language, UIStrings::class.java)
        loadLocFile(language, PotionStrings::class.java)
        loadLocFile(language, RelicStrings::class.java)
        loadLocFile(language, PowerStrings::class.java)
    }

    override fun receiveEditStrings() {
        loadLocFiles(Settings.GameLanguage.ENG)
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocFiles(Settings.language)
        }
    }

    override fun receivePostDungeonInitialize() {
        if (disableSozu()) {
            if (AbstractDungeon.bossRelicPool.removeIf { it == Sozu.ID }) {
                logger.info("${Sozu.ID} removed from pool.")
            }
        }
    }
}
