package com.hihihihi.composemindmap.canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

data class NodeDragState(
    val nodeId: String,
    val screenPos: Offset,
    val dropTargetId: String?,
)

@Stable
class MindMapCanvasState(initialScale: Float = 1f) {
    var scale by mutableFloatStateOf(initialScale)
    var offset by mutableStateOf(Offset.Zero)
    var dragging by mutableStateOf<NodeDragState?>(null)

    internal var centerVersion by mutableIntStateOf(0)

    fun center() {
        centerVersion++
    }
}

@Composable
fun rememberMindMapCanvasState(initialScale: Float = 1f): MindMapCanvasState =
    remember { MindMapCanvasState(initialScale) }
