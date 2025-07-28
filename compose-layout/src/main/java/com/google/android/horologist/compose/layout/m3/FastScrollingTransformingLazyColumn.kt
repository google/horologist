/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.compose.layout.m3

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.RotaryScrollEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.MotionScheme.Companion.expressive
import androidx.wear.compose.material3.MotionScheme.Companion.standard
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Modification of the TransformingLazyColumn that allows for fast scrolling to objects with
 * headers. This is done by on navigation via RSB and scrolling to past a specific speed threshold,
 * the list will begin scrolling directly to each section header instead of scrolling through each
 *
 * @param state The scroll state of the list.
 * @param headers The headers within the list, which includes the content of the header and the
 *   index. This is used by the FastScrollingTransformingLazyColumn to display a header over the
 *   given information and snap to the speicified header.
 * @property modifier The modifier(s) to apply to the list.
 * @property sectionIndictatorTopPadding The top padding to apply to the section indicator. This
 *   should only be needed to align with the header scrolled to when the scrollToOffset is NOT 0.
 * @property content The content within the list. This can be used the exact same way as the
 *   TransformingLazyColumn with content, though do note that any items that you do not want to
 *   scroll to need to be considered when passing in the HeaderInfo's index to the
 *   FastScrollingTransformingLazyColumn
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun FastScrollingTransformingLazyColumn(
    state: TransformingLazyColumnState,
    headers: SnapshotStateList<HeaderInfo>,
    modifier: Modifier = Modifier,
    sectionIndictatorTopPadding: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(),
    content: TransformingLazyColumnScope.() -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val screenHeight = LocalWindowInfo.current.containerSize.height

    val coroutineScope = rememberCoroutineScope()
    var fadingOutJob: Job? by remember { mutableStateOf(null) }
    var animationJob: Job? by remember { mutableStateOf(null) }

    // Total scroll-to offset for the list. This is the sum of the remaining letter height and the
    // section indicator top padding, with whatever extra top padding is passed in from the composable.
    val scrollToOffset =
        with(LocalDensity.current) {
            (
                Constants.REMAINING_LETTER_HEIGHT +
                    Constants.SECTION_INDICATOR_TOP_PADDING +
                    sectionIndictatorTopPadding
                )
                .roundToPx()
        }

    var currentSectionIndex by remember { mutableIntStateOf(0) }

    var firstSkimTime by remember { mutableLongStateOf(0L) }
    var verticalScrollPixels by remember { mutableFloatStateOf(0f) }

    var isSkimming by remember { mutableStateOf(false) }
    var rsbScrollCount by remember { mutableIntStateOf(0) }
    var isFirstFastScroll by remember { mutableStateOf(false) }

    val indicatorTransition = updateTransition(isSkimming)
    var indicatorState by remember { mutableStateOf(IndicatorState.START) }
    var pixelsScrolledBy by remember { mutableFloatStateOf(0f) }

    val indicatorOpacity by
        indicatorTransition.animateFloat(
            // show immediately the indicator and fade it out slowly
            transitionSpec = { standard().defaultEffectsSpec() },
        ) { isShowing ->
            if (isShowing) {
                1f
            } else {
                0f
            }
        }

    var currentAnchorItemIndex by remember { mutableStateOf(state.anchorItemIndex) }
    var currentAnchorItemOffset by remember { mutableStateOf(state.anchorItemScrollOffset) }

    val transition = updateTransition(indicatorState)

    val indicatorWidthScale by
        transition.animateFloat(
            transitionSpec = {
                when {
                    IndicatorState.START isTransitioningTo IndicatorState.SPRING ->
                        standard().defaultEffectsSpec()
                    IndicatorState.SPRING isTransitioningTo IndicatorState.END ->
                        expressive().fastSpatialSpec()
                    else -> standard().defaultEffectsSpec()
                }
            },
            label = "width",
        ) {
            when (it) {
                IndicatorState.START -> 1f
                IndicatorState.SPRING -> 1.25f
                IndicatorState.END -> 1f
            }
        }

    val indicatorTextPositionY by
        transition.animateDp(
            transitionSpec = {
                when {
                    IndicatorState.START isTransitioningTo IndicatorState.END -> standard().fastEffectsSpec()
                    else -> standard().fastEffectsSpec()
                }
            },
            label = "positionY",
        ) {
            when (it) {
                IndicatorState.START -> 5.dp
                IndicatorState.SPRING -> 2.5.dp
                IndicatorState.END -> 0.dp
            }
        }

    val indicatorTextOpacity by
        transition.animateFloat(
            transitionSpec = {
                when {
                    IndicatorState.START isTransitioningTo IndicatorState.END -> standard().fastEffectsSpec()
                    else -> standard().fastEffectsSpec()
                }
            },
            label = "opacity",
        ) {
            when (it) {
                IndicatorState.START -> 0f
                IndicatorState.SPRING -> .5f
                IndicatorState.END -> 1f
            }
        }

    val animationValues by remember {
        derivedStateOf {
            IndicatorAnimationValues(
                indicatorOpacity,
                indicatorWidthScale,
                indicatorTextPositionY,
                indicatorTextOpacity,
            )
        }
    }

    fun setCurrentSectionIndex(firstItemIndex: Int) {
        if (currentSectionIndex != firstItemIndex) {
            currentSectionIndex = firstItemIndex
        }
    }

    fun scrollListToSection() {
        val headerOffset = scrollToOffset + (headers[currentSectionIndex].extraScrollToOffset ?: 0)

        val offset = headerOffset + (screenHeight * -.5).toInt()

        coroutineScope.launch {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            // We run animateScrollBy with a movement of 0 just to remove the timeText from the screen and
            // show the position indicators, as animateScrollToItem will fling from each section.
            state.animateScrollBy(0f)
            state.scrollToItem(headers[currentSectionIndex].index, offset)
        }
    }

    fun skimSections(target: Int) {
        currentSectionIndex = target

        scrollListToSection()
        // Start the animation, and cancel the previous animations if any were running
        animationJob?.cancel()
        animationJob =
            coroutineScope.launch {
                if (indicatorState != IndicatorState.START) {
                    indicatorState = IndicatorState.START
                }
                delay(50)
                indicatorState = IndicatorState.SPRING
                delay(50)
                indicatorState = IndicatorState.END
            }

        // After every skim, we will run a job that will fade out the indicator and reset the flags
        // once the timeout is reached. This will continuously allow the skim to keep running if
        // skimming keeps being performed.

        fadingOutJob?.cancel()
        fadingOutJob =
            coroutineScope.launch {
                delay(Constants.RSB_SKIMMING_TIMEOUT)
                // Skim has finally ended, as another skim did not happen to reset the skim flag.
                isSkimming = false
            }
    }

    fun RotaryScrollEvent.handleSkim(currentTime: Long, isScrollingDown: Boolean) {
        if (!isFirstFastScroll) {
            // If we fast scroll in two different directions, we will reset the pixels scrolled
            // by to 0 to make sure skims in the opposite direction will be performed as intended.
            if (
                (this.verticalScrollPixels > 0f && pixelsScrolledBy < 0f) ||
                (this.verticalScrollPixels < 0f && pixelsScrolledBy > 0f)
            ) {
                pixelsScrolledBy = 0f
            }

            // If it has been more than the timeout since the last skim, we will begin taking in
            // the fast scrolling pixels. This is to prevent the case where a user starts
            // skimming mode by scrolling rapidly, but only wants to move a single section.
            if (currentTime - firstSkimTime > Constants.FIRST_SCROLL_TIMEOUT) {
                pixelsScrolledBy += this.verticalScrollPixels
            }
            val sectionsToSkimBy =
                (Math.abs(pixelsScrolledBy) / Constants.VERTICAL_SCROLL_BY_THRESHOLD).toInt()
            pixelsScrolledBy %= Constants.VERTICAL_SCROLL_BY_THRESHOLD
            for (i in 0..<sectionsToSkimBy) {
                val newSectionIndex = (currentSectionIndex + (if (isScrollingDown) 1 else -1))
                skimSections(newSectionIndex.coerceIn(0, headers.size - 1))
            }
        } else {
            // Perform the fast scroll skim once. The first skim should always perform a ton of scrolls to
            // get into fast-scrolling mode, so we can do this to make sure we don't skim multiple
            // sections accidentally.
            firstSkimTime = System.currentTimeMillis()
            isFirstFastScroll = false
            val newSectionIndex = (currentSectionIndex + (if (isScrollingDown) 1 else -1))
            skimSections(newSectionIndex.coerceIn(0, headers.size - 1))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TransformingLazyColumn(
            state = state,
            modifier =
                modifier
                    .fillMaxWidth()
                    .onRotaryScrollEvent {
                        // Track current time to ensure that we should ingest the rotary scroll event.
                        val currentTime = System.currentTimeMillis()

                        verticalScrollPixels = it.verticalScrollPixels
                        val canFastScroll =
                            headers.isNotEmpty() &&
                                (currentSectionIndex >= 0 && currentSectionIndex < headers.size)
                        val scrollCount = (abs(verticalScrollPixels) / Constants.RSB_SPEED_THRESHOLD).toInt()

                        if (!isSkimming && scrollCount > 0 && canFastScroll) {
                            rsbScrollCount += scrollCount
                            if (rsbScrollCount > 5) {
                                isFirstFastScroll = true
                                pixelsScrolledBy = 0f
                                isSkimming = true
                                rsbScrollCount = 0
                            }
                        }

                        if (isSkimming) {
                            it.handleSkim(
                                currentTime = currentTime,
                                isScrollingDown = verticalScrollPixels > 0f,
                            )
                        } else {
                            haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                            coroutineScope.launch {
                                // Here, we animate the scroll by 0f to remove the timeText from the screen and
                                // show the position indicators. Running animateScrollBy by the verticalScrollPixels
                                // does not scroll as much as scrollBy for some reason.
                                state.animateScrollBy(0f)
                                state.scrollBy(verticalScrollPixels)
                            }
                        }
                        true
                    }
                    .focusable(),
            contentPadding = remember { contentPadding },
        ) {
            content()
        }

        SectionIndicator(
            animationValues,
            verticalScrollPixels,
            { currentSectionIndex },
            headers,
            sectionIndictatorTopPadding,
        )

        LaunchedEffect(key1 = Unit) {
            snapshotFlow {
                currentAnchorItemIndex != state.anchorItemIndex ||
                    currentAnchorItemOffset != state.anchorItemScrollOffset
            }
                .filter { it == true }
                .collect {
                    // We determine if we are not skimming if the item index is the same with
                    // a different scroll offset (caused by swiping up or down with your finger). Although
                    // this is only for a couple of frames, it is enough to determine that we are not
                    // skimming and disable fast scrolling.
                    if (
                        isSkimming &&
                        currentAnchorItemIndex == state.anchorItemIndex &&
                        currentAnchorItemOffset != state.anchorItemScrollOffset
                    ) {
                        isSkimming = false
                    }
                    currentAnchorItemIndex = state.anchorItemIndex
                    currentAnchorItemOffset = state.anchorItemScrollOffset
                }
        }

        LaunchedEffect(key1 = Unit) {
            snapshotFlow { (state.layoutInfo.visibleItems.firstOrNull()?.index ?: 0) }
                .collect { visibleItemIndex ->
                    if (!isSkimming && headers.isNotEmpty()) {
                        val searchResult = headers.binarySearchBy(visibleItemIndex) { it.index }
                        val sectionIndex =
                            if (searchResult >= 0) {
                                // Exact match found
                                searchResult
                            } else {
                                // No exact match, visibleItemIndex is between header indices.
                                // binarySearchBy returns (-insertion point - 1).
                                // The section index is the item before the insertion point.
                                val insertionPoint = -searchResult - 1
                                (insertionPoint - 1).coerceIn(0, headers.size - 1)
                            }
                        setCurrentSectionIndex(sectionIndex)
                    }
                }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SectionIndicator(
    animationValues: IndicatorAnimationValues,
    verticalScrollPixels: Float,
    currentSection: () -> Int,
    headerValues: SnapshotStateList<HeaderInfo>,
    sectionIndictatorTopPadding: Dp,
) {
    val currentSectionHeader = headerValues.getOrNull(currentSection())
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
            Modifier.graphicsLayer(alpha = animationValues.indicatorOpacity)
                .fillMaxWidth()
                .padding(top = sectionIndictatorTopPadding),
    ) {
        Box(
            modifier =
                Modifier.graphicsLayer { this.scaleX = animationValues.indicatorWidthScale }
                    .clip(shape = RoundedCornerShape(24.dp))
                    .requiredHeight(Constants.INDICATOR_HEIGHT)
                    .sizeIn(minWidth = Constants.INDICATOR_WIDTH)
                    .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier =
                    Modifier.graphicsLayer {
                        this.translationY =
                            animationValues.indicatorTextPositionY.toPx() *
                            (if (verticalScrollPixels > 0f) -1 else 1)
                        this.alpha = animationValues.indicatorTextOpacity
                    }
                        .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                text =
                    if (currentSectionHeader != null) {
                        val inlineContent = currentSectionHeader.inlineContent
                        if (inlineContent.isNotEmpty()) {
                            buildAnnotatedString {
                                appendInlineContent(inlineContent.keys.first())
                                append(currentSectionHeader.value)
                            }
                        } else {
                            buildAnnotatedString { append(currentSectionHeader.value) }
                        }
                    } else {
                        buildAnnotatedString { append("") }
                    },
                inlineContent =
                    if (currentSectionHeader != null) {
                        currentSectionHeader.inlineContent
                    } else {
                        mapOf()
                    },
            )
        }
    }
}

/**
 * Class for storing the index and value of a header used for the FastScrollingTransformingLazyColumn.
 * This is used to properly display and snap to the specified header during fast scrolling.
 *
 * @property index The index of the header in the list.
 * @property value The value of the header's text in the list.
 * @property inlineContent The optional inline content to be used in the header's text. The string
 *   key for this would be dependant on the user, but will be added within the appendInlineContent
 *   function when building an annotated string (see
 *   https://developer.android.com/reference/kotlin/androidx/compose/ui/text/AnnotatedString for
 *   more information).
 * @property extraScrollToOffset The optional extra offset added to the default offset.
 */
public class HeaderInfo(
    val index: Int,
    val value: String,
    val inlineContent: Map<String, InlineTextContent> = mapOf(),
    val extraScrollToOffset: Int? = null,
)

// Mini class used to store the state of the animation at a given time
private data class IndicatorAnimationValues(
    val indicatorOpacity: Float,
    val indicatorWidthScale: Float,
    val indicatorTextPositionY: Dp,
    val indicatorTextOpacity: Float,
)

// Indicatior's animation state used to modify the animation values during the animation.
private enum class IndicatorState {
    START,
    SPRING,
    END,
}

private object Constants {
    val INDICATOR_WIDTH = 52.dp
    val INDICATOR_HEIGHT = 32.dp

    // The remaining height of the letter in the header text. When scrolled to just 6.dp, the text
    // will be fully visible.
    val REMAINING_LETTER_HEIGHT = 6.dp

    // The inside padding of the section indicator. Text inside is 20.dp with the height being 32, so
    // the rest would be 6.dp for the top and bottom (though we only care for the bottom if we are
    // attempting to align)
    val SECTION_INDICATOR_TOP_PADDING = 6.dp

    // Threshold for the number of pixels the list must scroll before we skim to the next section.
    const val VERTICAL_SCROLL_BY_THRESHOLD = 65
    const val FIRST_SCROLL_TIMEOUT = 500L
    const val RSB_SPEED_THRESHOLD = 40
    const val RSB_THROTTLE = 150
    const val RSB_SKIMMING_TIMEOUT = 2500L
}
