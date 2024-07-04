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

import kotlinx.serialization.Serializable

interface AuditNavigation {
    val parent: AuditNavigation?
        get() = null

    val title: String
        get() = javaClass.simpleName

    abstract class AuditSection<C: SingleAuditConfig, S: SingleAuditScreen<C>>: AuditNavigation, ScreenList {
        abstract val configs: List<C>

        abstract fun screen(config: C): S

        override val screens: List<S>
            get() = configs.map { screen(it) }

        override val parent: AuditNavigation = MainMenu
    }

    interface SingleAuditScreen<C: SingleAuditConfig>: AuditNavigation {
        val config: C

        override val title: String
            get() = config.title
    }

    interface SingleAuditConfig {
        val title: String
            get() = (this as Enum<*>).name
    }

    @Serializable
    data object Lists: AuditSection<Lists.Config, Lists.Audit>() {
        enum class Config: SingleAuditConfig {
            OneBottomChip, OneBottomButton, TwoBottomRound, NoBottomButton
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Dialogs: AuditSection<Dialogs.Config, Dialogs.Audit>() {
        enum class Config: SingleAuditConfig {
            IconAndTitle, Title, OneButtonChip, OneBottomButton, TwoBottomButtons, NoBottomButton
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Confirmations: AuditSection<Confirmations.Config, Confirmations.Audit>() {
        enum class Config: SingleAuditConfig {
            OneBottomChip, OneBottomButton, TwoBottomRound, NoBottomButton
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Pickers: AuditSection<Pickers.Config, Pickers.Audit>() {
        enum class Config: SingleAuditConfig {
            Time12h, Time12hWithSeconds, Time24Hour, Date
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Stepper: AuditSection<Stepper.Config, Stepper.Audit>() {
        enum class Config: SingleAuditConfig {
            ButtonAndIcon, ButtonOnly, TextOnly, VolumeIndicator
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object ProgressIndicator: AuditSection<ProgressIndicator.Config, ProgressIndicator.Audit>() {
        enum class Config: SingleAuditConfig {
            GapAtTop, GapAtBottom, WithoutGap
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object PageIndicator: AuditSection<PageIndicator.Config, PageIndicator.Audit>() {
        enum class Config: SingleAuditConfig {
            TwoDots, FourDots, Left5Plus, Right5Plus
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object PositionIndicator: AuditSection<PositionIndicator.Config, PositionIndicator.Audit>() {
        enum class Config: SingleAuditConfig {
            TopShort, MiddleShort, BottomShort, TopLong, MiddleLong, BottomLong
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object VolumeRsb: AuditSection<VolumeRsb.Config, VolumeRsb.Audit>() {
        enum class Config: SingleAuditConfig {
            TopShort, MiddleShort, BottomShort, TopLong, MiddleLong, BottomLong
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object CurvedTimeText: AuditSection<CurvedTimeText.Config, CurvedTimeText.Audit>() {
        enum class Config: SingleAuditConfig {
            H12, H24, LongerTextString
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    @Serializable
    data object Cards: AuditSection<Cards.Config, Cards.Audit>() {
        enum class Config: SingleAuditConfig {
            BackgroundImage
        }

        @Serializable
        data class Audit(override val config: Config) : SingleAuditScreen<Config> {
            override val parent: AuditNavigation = Lists
        }

        override val configs: List<Config>
            get() = Config.entries

        override fun screen(config: Config): Audit = Audit(config)
    }

    data object MainMenu: AuditNavigation

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
