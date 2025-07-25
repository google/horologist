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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TitleCard
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class FastScrollingTransformingLazyColumnTest(override val device: WearDevice) :
    WearScreenshotTest() {

        override fun testName(suffix: String): String =
            "src/test/screenshots/${this.javaClass.simpleName}_${testInfo.methodName}_" + "${device.id}$suffix.png"

        override val tolerance: Float
            get() = if (device == WearDevice.Companion.GooglePixelWatchLargeFont) 0.05f else 0.0f

        @Composable
        override fun TestScaffold(content: @Composable (() -> Unit)) {
            content()
        }

        data class Person(val name: String)

        val peopleString = """
            Olivia Smith, Liam Johnson, Emma Williams, Noah Brown, Ava Jones, Isabella Garcia, 
            Sophia Miller, James Davis, William Rodriguez, Benjamin Martinez, Lucas Hernandez, 
            Henry Lopez, Alexander Gonzalez, Mia Wilson, Charlotte Anderson, Amelia Thomas, 
            Evelyn Taylor, Abigail Moore, Daniel Jackson, Harper Martin, Ella Lee, Grace Perez, 
            Aiden Thompson, Jackson White, Scarlett Harris, Emily Sanchez, Michael Clark, 
            Elizabeth Ramirez, David Lewis, Mila Robinson, Joseph Walker, Chloe Hall, 
            Samuel Allen, Aubrey Young, Julian King, Zoey Wright, Leo Scott, Layla Green, 
            Gabriel Baker, Nora Adams, Anthony Nelson, Luna Hill, Christopher Rivera, 
            Victoria Campbell, Ryan Mitchell, Hannah Roberts, Nathan Carter, Natalie Phillips, 
            Caleb Parker, Leah Evans, Isaac Edwards, Zoe Collins, Joshua Stewart, Stella Morris, 
            Matthew Rogers, Aurora Reed, Andrew Cook, Dylan Morgan, John Bell, Genesis Murphy, 
            Luke Bailey, Sarah Cooper, Gabriel Richardson, Eva Cox, Nathan Howard, Penelope Ward, 
            Jacob Torres, Alexander Peterson, Mason Gray, Ethan Ramirez, Oliver James, 
            Elijah Watson, Sebastian Brooks, Owen Kelly, Logan Sanders, Caleb Price, 
            Dylan Bennett, Isaac Wood, Liam Barnes, Noah Ross, Lucas Henderson, Aiden Coleman, 
            Jack Jenkins, Daniel Perry, Joseph Powell, Samuel Long, Benjamin Patterson, Leo Hughes, 
            Julian Flores, Chloe Washington, Zoe Butler, Stella Simmons, Layla Foster, 
            Nora Gonzales, Luna Bryant, Harper Alexander, Mila Russell, Charlotte Griffin, 
            Amelia Diaz, Evelyn Hayes, Abigail Myers, Michael Ford, Elizabeth Hamilton, 
            David Graham, Ella Sullivan, Grace Wallace, Jackson Woods, Scarlett Cole, Emily West, 
            William Jordan, Benjamin Owens, Lucas Reynolds, Henry Kennedy, Alexander Stone, 
            Mia Shaw, Charlotte Snyder, Amelia Burke, Evelyn Spencer, Abigail Walsh, Daniel Dean,
            Harper Fisher, Ella Lane, Grace Boyd, Aiden Fuller, Jackson Fields, Scarlett Black, 
            Emily Ryan, Michael Olsen, Elizabeth Pierce, David Porter, Mila Freeman, 
            Joseph Cunningham, Chloe Lawrence, Samuel Newman, Aubrey Hunt, Julian Meyer, 
            Zoey Marshall, Leo Stevens, Layla Dixon, Gabriel Arnold, Nora Boyd, Anthony Fuller, 
            Luna Hayes, Christopher Cox, Victoria Ward, Ryan Gray, Hannah Bailey, Nathan Brooks,
            Natalie Kelly, Caleb Price, Leah Bennett, Isaac Barnes, Zoe Henderson, Joshua Coleman, 
            Stella Jenkins, Matthew Perry, Aurora Powell, Andrew Long, Dylan Patterson, John Hughes, 
            Genesis Flores, Luke Washington, Sarah Butler, Gabriel Simmons, Eva Foster, 
            Nathan Gonzales, Penelope Bryant, Jacob Alexander, Alexander Russell, Mason Griffin, 
            Ethan Diaz, Oliver Hayes, Elijah Myers, Sebastian Ford, Owen Hamilton, Logan Graham, 
            Caleb Sullivan, Dylan Wallace, Isaac Woods, Liam Cole, Noah West, Lucas Jordan, 
            Aiden Owens, Jack Reynolds, Daniel Kennedy, Joseph Stone, Samuel Shaw, Benjamin Snyder, 
            Leo Burke, Julian Spencer, Chloe Walsh, Zoe Dean, Stella Fisher, Layla Lane, Nora Boyd, 
            Luna Fuller, Harper Fields, Mila Black, Charlotte Ryan, Amelia Olsen, Evelyn Pierce, 
            Abigail Porter, Michael Freeman, Elizabeth Cunningham, David Lawrence, Ella Newman, 
            Grace Hunt, Jackson Meyer, Scarlett Marshall, Emily Stevens, William Dixon, 
            Benjamin Arnold, Lucas Boyd, Henry Fuller, Alexander Hayes, Mia Cox, Charlotte Ward, 
            Amelia Gray, Evelyn Bailey, Abigail Brooks, Daniel Kelly, Harper Price, Ella Bennett, 
            Grace Barnes, Aiden Henderson, Jackson Coleman, Scarlett Jenkins, Emily Perry, 
            Michael Powell, Elizabeth Long, David Patterson, Mila Hughes, Joseph Flores, 
            Chloe Washington, Samuel Butler, Aubrey Simmons, Julian Foster, Zoey Gonzales, 
            Leo Bryant, Layla Alexander, Nora Russell, Luna Griffin, Christopher Diaz, 
            Victoria Hayes, Ryan Myers, Hannah Ford, Nathan Hamilton, Natalie Graham, 
            Caleb Sullivan, Leah Wallace, Isaac Woods, Zoe Cole, Joshua West, Stella Jordan, 
            Matthew Owens, Aurora Reynolds, Andrew Kennedy, Dylan Stone, John Shaw, Genesis Snyder, 
            Luke Burke, Sarah Spencer, Gabriel Walsh, Eva Dean, Nathan Fisher, Penelope Lane, 
            Jacob Boyd, Alexander Fuller, Mason Fields, Ethan Black, Oliver Ryan, Elijah Olsen, 
            Sebastian Pierce, Owen Porter, Logan Freeman, Caleb Cunningham, Dylan Lawrence, 
            Isaac Newman, Liam Hunt, Noah Meyer, Lucas Marshall, Aiden Stevens, Jack Dixon, 
            Daniel Arnold, Joseph Boyd, Samuel Fuller, Benjamin Hayes, Leo Cox, Julian Ward, 
            Chloe Gray, Zoe Bailey, Stella Brooks, Layla Kelly, Nora Price, Luna Bennett, 
            Harper Barnes, Mila Henderson, Charlotte Coleman, Amelia Jenkins, Evelyn Perry, 
            Abigail Powell         
        """.trimIndent()

        val people = peopleString.split(",").map {
            Person(it.trim())
        }.sortedBy { it.name }

        @Test
        fun BasicExample() {
            lateinit var columnState: TransformingLazyColumnState
            runTest {
                AppScaffold(
                    timeText = {
                        TimeText(timeSource = FixedTimeSource3)
                    },
                    // Why black needed here
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                ) {
                    columnState = rememberTransformingLazyColumnState()
                    ScreenScaffold(
                        scrollState = columnState,
                        contentPadding = rememberResponsiveColumnPadding(
                            first = ColumnItemType.Card,
                            last = ColumnItemType.Card,
                        ),
                    ) { contentPadding ->
                        val transformationSpec = rememberTransformationSpec()

                        val headers = remember {
                            val letterIndexes = people.mapIndexed { index, person ->
                                HeaderInfo(
                                    index,
                                    person.name.take(1),
                                )
                            }.distinctBy { it.value }
                            letterIndexes.toMutableStateList()
                        }

                        FastScrollingTransformingLazyColumn(
                            scrollState = columnState,
                            contentPadding = contentPadding,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("TransformingLazyColumn"),
                            headers = headers,
                        ) {
                            items(people) { person ->
                                TitleCard(
                                    onClick = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                    transformation = SurfaceTransformation(transformationSpec),
                                    title = { Text(person.name) },
                                ) {
                                    Text("Visits to the ISS:")
                                }
                            }
                        }
                    }
                }
            }

            composeRule.waitForIdle()

            runBlocking {
                columnState.scroll {
                    scrollBy(200_000f)
                }
            }

            captureScreenshot("_end")
        }

        companion object {
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters
            fun devices() = WearDevice.entries
        }
    }
