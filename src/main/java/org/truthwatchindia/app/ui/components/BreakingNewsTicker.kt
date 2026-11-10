package org.truthwatchindia.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke

@Composable
fun BreakingNewsTicker(
    headlines: List<String>,
    onHeadlineClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (headlines.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(headlines.size) {
        if (headlines.size > 1) {
            while (true) {
                delay(4000) // 4 seconds per headline
                currentIndex = (currentIndex + 1) % headlines.size
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Red badge (#970347) with white bold text "ब्रेकिंग न्यूज़"
            Surface(
                color = CrimsonMaroon,
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(
                    text = "ब्रेकिंग न्यूज़",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Headline ticker cycling
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn())
                            .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    },
                    label = "BreakingNewsAnimation"
                ) { targetIndex ->
                    val headline = headlines.getOrNull(targetIndex) ?: ""
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkCharcoal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHeadlineClick?.invoke(targetIndex) }
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = DividerStroke,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
