/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.sample.wear.prompt.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.get
import androidx.wear.compose.navigation.SwipeDismissableNavHostState
import androidx.wear.compose.navigation.WearNavigator
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
public fun SwipeDismissableNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    userSwipeEnabled: Boolean = true,
    state: SwipeDismissableNavHostState = rememberSwipeDismissableNavHostState(),
    route: KClass<*>? = null,
    builder: NavGraphBuilder.() -> Unit
) = androidx.wear.compose.navigation.SwipeDismissableNavHost(
    navController,
    remember(route, startDestination, builder) {
        navController.createGraph(startDestination = startDestination, route = route, builder = builder)
    },
    modifier,
    userSwipeEnabled,
    state = state,
)

public inline fun <reified T : Any> NavGraphBuilder.composable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable (NavBackStackEntry) -> Unit
) {
    destination(
        WearComposeNavigatorDestinationBuilder(
            wearNavigator = provider[WearNavigator::class],
            route = T::class,
            typeMap = typeMap,
            content = content
        ).apply {
            deepLinks.forEach { deepLink ->
                deepLink(deepLink)
            }
        }
    )
}

class WearComposeNavigatorDestinationBuilder(
    val wearNavigator: WearNavigator,
    route: KClass<*>,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    private val content: @Composable (NavBackStackEntry) -> Unit
): NavDestinationBuilder<WearNavigator.Destination>(wearNavigator, route, typeMap) {

    override fun instantiateDestination(): WearNavigator.Destination {
        return WearNavigator.Destination(wearNavigator, content)
    }
}