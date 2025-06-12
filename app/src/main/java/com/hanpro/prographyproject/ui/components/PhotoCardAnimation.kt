package com.hanpro.prographyproject.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PhotoCardAnimation(
    currentPhoto: PhotoDetail,
    viewModel: PhotoViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var offsetX = remember { Animatable(0f) }
    var offsetY = remember { Animatable(0f) }
    val threshold = 200f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer { rotationZ = (offsetX.value / threshold) * 10f }
            .zIndex(2f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > threshold -> { // 오른쪽 스와이프
                                coroutineScope.launch {
                                    viewModel.addBookmark(currentPhoto)
                                    animateOutCard(offsetX, offsetY, toRight = true) {
                                        viewModel.incrementIndex()
                                    }
                                }
                            }

                            offsetX.value < -threshold -> { // 왼쪽 스와이프
                                coroutineScope.launch {
                                    animateOutCard(offsetX, offsetY, toRight = false) {
                                        viewModel.incrementIndex()
                                    }
                                }
                            }

                            else -> {
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, tween(durationMillis = 300))
                                    offsetY.animateTo(0f, tween(durationMillis = 300))
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                        offsetY.snapTo(offsetY.value + dragAmount.y)
                    }
                }
            }
    ) { content() }
}

suspend fun animateOutCard(
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    toRight: Boolean,
    onComplete: () -> Unit,
) {
    val targetX = if (toRight) 1000f else -1000f
    offsetX.animateTo(targetValue = targetX, animationSpec = tween(durationMillis = 300))
    offsetY.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 300))
    offsetX.snapTo(0f)
    offsetY.snapTo(0f)
    onComplete()
}