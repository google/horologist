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

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalWearFoundationApi::class)

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorDefaults
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PageIndicatorStyle
import androidx.wear.compose.material.PositionIndicatorDefaults
import androidx.wear.compose.material.PositionIndicatorState
import androidx.wear.compose.material.PositionIndicatorVisibility
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.pager.ClippedBox
import com.google.android.horologist.compose.pager.HorizontalPagerDefaults
import com.google.android.horologist.compose.pager.PageScreenIndicatorState
import com.google.android.horologist.compose.rotaryinput.RotaryHapticsType
import com.google.android.horologist.compose.rotaryinput.RotaryInputConfigDefaults.DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import com.google.android.horologist.compose.rotaryinput.rememberDefaultRotaryHapticFeedback
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import kotlinx.coroutines.launch

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var discreteRsb: Boolean by remember { mutableStateOf(false) }
    var usePositionIndicator: Boolean by remember { mutableStateOf(false) }
    var screenSize: Boolean by remember { mutableStateOf(true) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val navController = rememberSwipeDismissableNavController()
    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController, startDestination = "menu"
        ) {
            composable("menu") {
                ScreenScaffold {
                    ScalingLazyColumn(columnState = rememberColumnState()) {
                        item {
                            Button(
                                onClick = {
                                    navController.navigate(
                                        "pager"
                                    )
                                },
                                imageVector = Icons.Default.Pages,
                                contentDescription = "Open Pager"
                            )
                        }
                        item {
                            ToggleChip(
                                checked = discreteRsb,
                                onCheckedChanged = { discreteRsb = !discreteRsb },
                                label = "Discrete RSB",
                                toggleControl = ToggleChipToggleControl.Checkbox
                            )
                        }
                        item {
                            ToggleChip(
                                checked = usePositionIndicator,
                                onCheckedChanged = { usePositionIndicator = !usePositionIndicator },
                                label = "Position Indicator",
                                toggleControl = ToggleChipToggleControl.Checkbox
                            )
                        }
                        item {
                            ToggleChip(
                                checked = screenSize,
                                onCheckedChanged = { screenSize = !screenSize },
                                label = "Single Screen Content",
                                toggleControl = ToggleChipToggleControl.Checkbox
                            )
                        }
                    }
                }
            }

            composable("pager") {
                val pagerState = rememberPagerState {
                    4
                }
                ScreenScaffold {
                    VerticalPagerScreen(
                        state = pagerState,
                        discreteRsb = discreteRsb,
                        usePositionIndicator = usePositionIndicator,
                        clip = screenSize
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .background(if (page % 2 == 0) Color.DarkGray else Color.Black)
                                .fillMaxWidth()
                                .height(screenHeight * if (screenSize) 1 else 2),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Page $page")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun VerticalPageIndicator(
    pageIndicatorState: PageIndicatorState,
    modifier: Modifier = Modifier,
    indicatorStyle: PageIndicatorStyle = PageIndicatorDefaults.style(),
    selectedColor: Color = MaterialTheme.colors.onBackground,
    unselectedColor: Color = selectedColor.copy(alpha = 0.3f),
    indicatorSize: Dp = 6.dp,
    spacing: Dp = 4.dp,
    indicatorShape: Shape = CircleShape
) {
    HorizontalPageIndicator(
        pageIndicatorState = pageIndicatorState,
        modifier = modifier
            .rotate(90f)
            .scale(scaleY = -1f, scaleX = 1f),
        indicatorStyle = indicatorStyle,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        indicatorSize = indicatorSize,
        spacing = spacing,
        indicatorShape = indicatorShape
    )
}

class PagerStatePositionIndicatorState(val state: PagerState) : PositionIndicatorState {
    override val positionFraction: Float
        get() = if (state.pageCount <= 1) {
            1f
        } else {
            (state.currentPage + state.currentPageOffsetFraction) / (state.pageCount - 1)
        }

    override fun sizeFraction(scrollableContainerSizePx: Float): Float = 1f / state.pageCount

    override fun visibility(scrollableContainerSizePx: Float): PositionIndicatorVisibility = PositionIndicatorVisibility.Show
}

@Composable
public fun PositionIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    reverseDirection: Boolean = false,
    fadeInAnimationSpec: AnimationSpec<Float> = PositionIndicatorDefaults.visibilityAnimationSpec,
    fadeOutAnimationSpec: AnimationSpec<Float> = PositionIndicatorDefaults.visibilityAnimationSpec,
    positionAnimationSpec: AnimationSpec<Float> = PositionIndicatorDefaults.positionAnimationSpec
) = androidx.wear.compose.material.PositionIndicator(
    state = PagerStatePositionIndicatorState(
        state = pagerState
    ),
    indicatorHeight = 50.dp,
    indicatorWidth = 4.dp,
    paddingHorizontal = 5.dp,
    modifier = modifier,
    reverseDirection = reverseDirection,
    fadeInAnimationSpec = fadeInAnimationSpec,
    fadeOutAnimationSpec = fadeOutAnimationSpec,
    positionAnimationSpec = positionAnimationSpec
)

@Composable
public fun VerticalPagerScreen(
    state: PagerState,
    discreteRsb: Boolean,
    usePositionIndicator: Boolean,
    modifier: Modifier = Modifier,
    clip: Boolean = true,
    content: @Composable ((Int) -> Unit),
) {
    ScreenScaffold(modifier = modifier.fillMaxSize(), positionIndicator = {
        if (usePositionIndicator) {
            PositionIndicator(pagerState = state)
        } else {
            VerticalPageIndicator(
                pageIndicatorState = PageScreenIndicatorState(state),
                modifier = Modifier.padding(6.dp)
            )
        }
    }) {
        VerticalPager(
            modifier = if (discreteRsb)
                Modifier
                    .fillMaxSize()
                    .rotaryWithPager(state, rememberActiveFocusRequester())
            else
                Modifier
                    .fillMaxSize()
                    .rotaryWithScroll(state),
            state = state,
            flingBehavior = HorizontalPagerDefaults.flingParams(state),
        ) { page ->
            if (clip) {
                ClippedBox(state) {
                    content(page)
                }
            } else {
                content(page)
            }
        }
    }
}

private fun Modifier.rotaryWithPager(
    state: PagerState,
    focusRequester: FocusRequester
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val haptics = rememberDefaultRotaryHapticFeedback()

    onRotaryInputAccumulated(minValueChangeDistancePx = DEFAULT_MIN_VALUE_CHANGE_DISTANCE_PX * 3) {
        val pageChange = if (it > 0f) 1 else -1

        if ((pageChange == 1 && state.currentPage >= state.pageCount - 1) || (pageChange == -1 && state.currentPage == 0)) {
            haptics.performHapticFeedback(RotaryHapticsType.ScrollLimit)
        } else {
            haptics.performHapticFeedback(RotaryHapticsType.ScrollItemFocus)

            coroutineScope.launch {
                state.animateScrollToPage(state.currentPage + pageChange)
            }
        }
    }
        .focusRequester(focusRequester)
        .focusable()
}

