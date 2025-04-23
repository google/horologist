package com.google.android.horologist.media.ui.material3.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class MessageMediaDisplayTest:  WearLegacyComponentTest() {
    @Test
    fun default() {
        runComponentTest {
            MessageMediaDisplay(modifier = Modifier.alpha(0.5f), "Test")
        }
    }

    @Test
    fun messageMediaDisplay_whenEmptyMessage() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f), "")
            }
        }
    }

    @Config(
        qualifiers = "+w192dp-h192dp",
    )
    @Test
    fun messageMediaDisplay_whenSmallScreen() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f), "David Mode")
            }
        }
    }

    @Test
    fun messageMediaDisplay_whenLargeScreen() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f), "David Mode")
            }
        }
    }

    @Composable
    private fun DisplayArea(content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }

}