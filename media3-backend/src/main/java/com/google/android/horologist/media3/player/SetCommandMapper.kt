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

package com.google.android.horologist.media3.player

import androidx.media3.common.Player
import com.google.android.horologist.media.model.Command

/**
 * Maps [Player.Commands] into a [Set] of [Command].
 */
public object SetCommandMapper {

    public fun map(commands: Player.Commands): Set<Command> = buildSet {
        for (i in 0 until commands.size()) {
            try {
                add(CommandMapper.map(commands.get(i)))
            } catch (e: IllegalArgumentException) {
                // no action needed, command is not yet mapped into our domain.
            }
        }
    }
}
