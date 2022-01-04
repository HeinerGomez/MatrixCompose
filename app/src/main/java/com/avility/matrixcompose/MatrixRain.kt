package com.avility.matrixcompose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MatrixRain(stripCount: Int = 20) {
    Row {
        repeat(stripCount) {
            MatrixColumn(
                yStartDelay = Random.nextInt(8) * 1000L,
                crawlSpeed = (Random.nextInt(10) * 10L) + 100
            )
        }
    }
}

@Composable
fun RowScope.MatrixColumn(crawlSpeed: Long, yStartDelay: Long) {

    BoxWithConstraints(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
    ) {
        val maxWithDp = maxWidth

        val matrixStrip = remember {
            Array((maxHeight / maxWidth).toInt())
            { characters.random() }
        }
        var lettersToDraw by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize()) {
            repeat(lettersToDraw) {
                MatrixChar(
                    fontSize = with(LocalDensity.current) {
                        maxWithDp.toSp()
                    },
                    char = matrixStrip[it],
                    crawlSpeed = crawlSpeed
                ) {
                    if (it >= matrixStrip.size * 0.6) {
                        lettersToDraw = 0
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            delay(yStartDelay)
            while (true) {
                if (lettersToDraw < matrixStrip.size) {
                    lettersToDraw += 1
                }

                if (lettersToDraw > matrixStrip.size * 0.5) {
                    matrixStrip[Random.nextInt(lettersToDraw)] = characters.random()
                }

                delay(crawlSpeed)
            }
        }
    }
}

@Composable
fun MatrixChar(fontSize: TextUnit, char: String, crawlSpeed: Long, onFinished: () -> Unit) {
    var textColor by remember { mutableStateOf(Color(0xffcefbe4)) }
    var startFade by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startFade) 0f else 1f,
        animationSpec = tween(
            durationMillis = 4_000,
            easing = LinearEasing
        ),
        finishedListener = {
            onFinished()
        }
    )

    Text(
        text = char,
        color = textColor.copy(alpha = alpha),
        fontSize = fontSize
    )

    LaunchedEffect(Unit) {
        textColor = Color(0xff43c728)
        startFade = true
    }
}