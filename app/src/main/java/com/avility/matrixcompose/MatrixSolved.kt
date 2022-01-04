package com.avility.matrixcompose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlin.random.Random

val characters = listOf(
    "ジ",
    "ェ",
    "ッ",
    "ト",
    "パ",
    "Z",
    "A",
    "R",
    "Q",
    "ッ",
    "ク",
    "構",
    "成",
    "I",
    "L",
    "N",
    "K",
    "8",
    "7",
    "C",
    "6"
)

@Composable
fun MatrixText(
    stripCount: Int = 20,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(Color.Black)
    ) {
        for (column in 0..stripCount) {
            MatrixColumn(
                Random.nextInt(8) * 1000L,
                (Random.nextInt(10) * 10L) + 100
            )
        }
    }
}

@Composable
fun RowScope.MatrixColumn2(
    yStartDelay: Long,
    crawlSpeed: Long
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
    ) {
        val pxWidth = with(LocalDensity.current) { maxWidth.toPx() }
        val pxHeight = with(LocalDensity.current) { maxHeight.toPx() }

        val matrixStrip =
            remember { Array((pxHeight / pxWidth).toInt() + 1) { characters.random() } }
        val lettersToDraw = remember { mutableStateOf(0) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0 until lettersToDraw.value) {
                MatrixChar(
                    textSizePx = pxWidth,
                    char = matrixStrip[row],
                    crawlSpeed
                ) {
                    // When the 60% of chars have faded restart the loop
                    if (row >= (matrixStrip.size * 0.6).toInt()) {
                        lettersToDraw.value = 0
                    }
                }
            }
        }

        LaunchedEffect(key1 = yStartDelay) {
            delay(yStartDelay)
            while (true) {
                if (lettersToDraw.value <= matrixStrip.size - 1) {
                    lettersToDraw.value += 1
                }
                if (lettersToDraw.value > matrixStrip.size * 0.5) {
                    // If we've drawn over half the strip, we can randomly change letters.
                    matrixStrip[Random.nextInt(lettersToDraw.value)] = characters.random()
                }
                delay(crawlSpeed)
            }
        }
    }
}

@Composable
fun MatrixChar(
    textSizePx: Float,
    char: String,
    crawlSpeed: Long,
    onFinished: () -> Unit
) {
    val startFade = remember { mutableStateOf(false) }
    val textSizeSp = with(LocalDensity.current) { textSizePx.toSp() }
    val textColor = remember { mutableStateOf(Color(0xffcefbe4)) }
    val alpha = animateFloatAsState(
        targetValue = if (startFade.value) 0f else 1f,
        animationSpec = tween(
            durationMillis = 4000, // animation duration
            easing = LinearEasing // animation easing
        ),
        finishedListener = {
            onFinished()
        }
    )

    Text(
        text = char,
        fontSize = textSizeSp,
        color = textColor.value.copy(alpha = alpha.value),
    )

    LaunchedEffect(Unit) {
        delay(crawlSpeed)
        textColor.value = Color(0xff43c728)
        startFade.value = true
    }
}
