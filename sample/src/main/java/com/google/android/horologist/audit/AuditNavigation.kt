/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.audit

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavType
import kotlinx.serialization.Serializable

interface AuditNavigation {
    val parent: AuditNavigation?
        get() = null

    val title: String
        get() = javaClass.simpleName

    val id: String
        get() = buildString {
            append(this@AuditNavigation.javaClass.name.split('$').dropLast(1).last())
            append("_")
            append(title.lowercase().replace(" ", ""))
        }

    abstract class AuditSection<C : SingleAuditConfig, S : SingleAuditScreen<C>> :
        AuditNavigation,
        ScreenList {
        abstract val configs: List<C>

        abstract fun screen(config: C): S

        override val screens: List<S>
            get() = configs.map { screen(it) }

        override val parent: AuditNavigation = MainMenu
    }

    interface SingleAuditScreen<C : SingleAuditConfig> : AuditNavigation {
        @Composable
        fun compose()

        val config: C

        override val title: String
            get() = config.title.toWords()
    }

    interface SingleAuditConfig {
        val title: String
            get() = (this as Enum<*>).name
    }

    data object Lists : AuditSection<Lists.Config, Lists.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            OneBottomChip, OneBottomButton, TwoBottomRound, NoBottomButton
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Lists

            @Composable
            override fun compose() {
                ListsAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Dialogs : AuditSection<Dialogs.Config, Dialogs.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            IconAndTitle, Title, OneButtonChip, OneBottomButton, TwoBottomButtons, NoBottomButton, NonScrollable
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Dialogs

            @Composable
            override fun compose() {
                DialogsAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Confirmations : AuditSection<Confirmations.Config, Confirmations.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            IconAnd1Line, IconAnd3Line
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Confirmations

            @Composable
            override fun compose() {
                ConfirmationsAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Pickers : AuditSection<Pickers.Config, Pickers.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            Time12h, Time24hWithSeconds, Time24Hour, Date
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Pickers

            @Composable
            override fun compose() {
                PickersAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Stepper : AuditSection<Stepper.Config, Stepper.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            ButtonAndIcon, ButtonOnly, TextOnly, VolumeIndicator
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Stepper

            @Composable
            override fun compose() {
                StepperAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object ProgressIndicator :
        AuditSection<ProgressIndicator.Config, ProgressIndicator.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            GapAtTop, GapAtBottom, WithoutGap, Indeterminate
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = ProgressIndicator

            @Composable
            override fun compose() {
                ProgressIndicatorAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object PageIndicator : AuditSection<PageIndicator.Config, PageIndicator.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            TwoDots, FourDots, Left5Plus, Right5Plus
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = PageIndicator

            @Composable
            override fun compose() {
                PageIndicatorAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object PositionIndicator :
        AuditSection<PositionIndicator.Config, PositionIndicator.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            TopShort, MiddleShort, BottomShort, TopLong, MiddleLong, BottomLong
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = PositionIndicator

            @Composable
            override fun compose() {
                PositionIndicatorAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object VolumeRsb : AuditSection<VolumeRsb.Config, VolumeRsb.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            TopShort, MiddleShort, BottomShort, TopLong, MiddleLong, BottomLong
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = VolumeRsb

            @Composable
            override fun compose() {
                VolumeRsbAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object CurvedTimeText : AuditSection<CurvedTimeText.Config, CurvedTimeText.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            H12, H24, LongerTextString, Tall
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = CurvedTimeText

            @Composable
            override fun compose() {
                CurvedTimeTextAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Cards : AuditSection<Cards.Config, Cards.Audit>() {
        @Serializable
        enum class Config : SingleAuditConfig {
            BackgroundImage,
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation
                get() = Cards

            @Composable
            override fun compose() {
                CardsAudit(this)
            }
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object MainMenu : AuditNavigation {
        override val id: String
            get() = "MainMenu"
    }

    interface ScreenList {
        val screens: List<AuditNavigation>
    }

    companion object {
        val screens = Lists.Config.entries.map { Lists.Audit(it) } +
            Dialogs.Config.entries.map { Dialogs.Audit(it) } +
            Confirmations.Config.entries.map { Confirmations.Audit(it) } +
            Pickers.Config.entries.map { Pickers.Audit(it) } +
            Stepper.Config.entries.map { Stepper.Audit(it) } +
            ProgressIndicator.Config.entries.map { ProgressIndicator.Audit(it) } +
            PageIndicator.Config.entries.map { PageIndicator.Audit(it) } +
            PositionIndicator.Config.entries.map { PositionIndicator.Audit(it) } +
            VolumeRsb.Config.entries.map { VolumeRsb.Audit(it) } +
            VolumeRsb.Config.entries.map { VolumeRsb.Audit(it) } +
            CurvedTimeText.Config.entries.map { CurvedTimeText.Audit(it) } +
            Cards.Config.entries.map { Cards.Audit(it) }
    }
}

private fun String.toWords(): String {
    // https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
    return this.split("(?<=[a-z])(?=[A-Z])".toRegex())
        .joinToString(" ") { it.capitalize(Locale.current) }.also {
            println(it)
        }
}

inline fun <reified T : Enum<T>> enumType(
    isNullableAllowed: Boolean = false,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) = parseValue(bundle.getString(key)!!)

    override fun parseValue(value: String): T = enumValueOf(value)

    override fun serializeAsValue(value: T): String = value.name

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putString(key, serializeAsValue(value))
}
