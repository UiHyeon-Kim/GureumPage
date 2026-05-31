package com.hihihihi.composemindmap.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.hihihihi.composemindmap.layout.MindMapLayoutEngine
import com.hihihihi.composemindmap.layout.MindMapLayoutInput
import com.hihihihi.composemindmap.layout.MindMapLayoutNode
import com.hihihihi.composemindmap.layout.MindMapLayoutResult
import com.hihihihi.composemindmap.layout.MindMapRootAlignment
import com.hihihihi.composemindmap.layout.TopDownTreeLayoutEngine
import com.hihihihi.composemindmap.model.MindMapBehavior
import com.hihihihi.composemindmap.model.MindMapNode
import com.hihihihi.composemindmap.model.MindMapStyle
import com.hihihihi.composemindmap.model.MindMapValidationResult
import com.hihihihi.composemindmap.model.defaultNodeSize
import com.hihihihi.composemindmap.model.validateMindMapNodes
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun MindMapCanvas(
    nodes: List<MindMapNode>,
    modifier: Modifier = Modifier,
    state: MindMapCanvasState = rememberMindMapCanvasState(),
    style: MindMapStyle = MindMapStyle(),
    behavior: MindMapBehavior = MindMapBehavior(),
    selectedNodeId: String? = null,
    editMode: Boolean = false,
    layoutEngine: MindMapLayoutEngine = TopDownTreeLayoutEngine,
    nodeSize: (MindMapNode) -> DpSize = { style.defaultNodeSize },
    canvasNodeRenderer: MindMapCanvasNodeRenderer = DefaultMindMapCanvasNodeRenderer,
    edgeRenderer: MindMapEdgeRenderer = CurvedMindMapEdgeRenderer,
    nodeContent: (@Composable (MindMapNode, MindMapNodeVisualState) -> Unit)? = null,
    onValidationError: (MindMapValidationResult.Invalid) -> Unit = {},
    onNodeClick: (nodeId: String) -> Unit = {},
    onNodeLongClick: (nodeId: String) -> Unit = {},
    onCanvasClick: () -> Unit = {},
    onAddChildClick: (parentId: String) -> Unit = {},
    onNodeMove: (nodeId: String, newParentId: String) -> Unit = { _, _ -> },
) {
    val density = LocalDensity.current
    val validation = remember(nodes) { validateMindMapNodes(nodes) }
    val layoutResult = remember(nodes, style, nodeSize, validation, layoutEngine) {
        if (validation is MindMapValidationResult.Valid) {
            layoutEngine.layout(MindMapLayoutInput(nodes, style, density, nodeSize))
        } else {
            MindMapLayoutResult()
        }
    }
    val layoutedNodes = layoutResult.nodes
    val textMeasurer = rememberTextMeasurer()
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var initiallyCentered by remember { mutableStateOf(false) }
    var centeredLayoutEngine by remember { mutableStateOf<MindMapLayoutEngine?>(null) }

    LaunchedEffect(validation) {
        if (validation is MindMapValidationResult.Invalid) onValidationError(validation)
    }
    LaunchedEffect(canvasSize, state.centerVersion, layoutedNodes.isNotEmpty(), layoutEngine) {
        val shouldCenter = state.centerVersion > 0 ||
            (behavior.autoCenterOnFirstLayout && (!initiallyCentered || centeredLayoutEngine !== layoutEngine))
        if (shouldCenter && canvasSize.width > 0 && layoutedNodes.isNotEmpty()) {
            val root = layoutedNodes.firstOrNull { it.node.parentId == null } ?: return@LaunchedEffect
            val rootCenter = Offset(root.offset.x + root.size.width / 2f, root.offset.y + root.size.height / 2f)
            state.offset = when (layoutEngine.rootAlignment) {
                MindMapRootAlignment.TOP_CENTER -> Offset(
                    x = canvasSize.width / 2f - rootCenter.x * state.scale,
                    y = with(density) { behavior.centerTopPadding.toPx() },
                )
                MindMapRootAlignment.CENTER_START -> Offset(
                    x = with(density) { behavior.centerStartPadding.toPx() },
                    y = canvasSize.height / 2f - rootCenter.y * state.scale,
                )
                MindMapRootAlignment.CENTER -> Offset(
                    x = canvasSize.width / 2f - rootCenter.x * state.scale,
                    y = canvasSize.height / 2f - rootCenter.y * state.scale,
                )
            }
            initiallyCentered = true
            centeredLayoutEngine = layoutEngine
        }
    }

    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        if (behavior.zoomEnabled) {
            state.scale = (state.scale * zoomChange).coerceIn(behavior.minScale, behavior.maxScale)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .transformable(transformableState)
            .pointerInput(layoutedNodes, editMode, selectedNodeId, behavior) {
                val touchSlop = viewConfiguration.touchSlop
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val localStart = (down.position - state.offset) / state.scale
                    val plusAreas = if (editMode && behavior.addChildButtonsVisible) {
                        layoutedNodes.map { it.plusButtonArea(style, density.density) }
                    } else {
                        emptyList()
                    }
                    val hitPlus = plusAreas.firstOrNull { it.contains(localStart) }
                    val hitNode = layoutedNodes.firstOrNull { it.contains(localStart) }
                    var dragging = false
                    var longPressed = false
                    var totalDrag = Offset.Zero
                    var pressed = true
                    var lastUptimeMillis = down.uptimeMillis

                    while (pressed) {
                        val event = if (!dragging && !longPressed && hitNode != null) {
                            val elapsed = lastUptimeMillis - down.uptimeMillis
                            val remaining = (viewConfiguration.longPressTimeoutMillis - elapsed).coerceAtLeast(1L)
                            withTimeoutOrNull(remaining) { awaitPointerEvent() }
                                ?: run {
                                    longPressed = true
                                    onNodeLongClick(hitNode.node.id)
                                    continue
                                }
                        } else {
                            awaitPointerEvent()
                        }
                        val change = event.changes.firstOrNull() ?: break
                        lastUptimeMillis = change.uptimeMillis
                        pressed = event.changes.any { it.pressed }
                        if (!change.pressed) continue

                        val delta = change.positionChange()
                        totalDrag += delta
                        if (!dragging && (abs(totalDrag.x) > touchSlop || abs(totalDrag.y) > touchSlop)) {
                            dragging = true
                        }
                        if (!dragging) continue

                        if (
                            behavior.nodeDraggingEnabled && editMode && hitNode != null &&
                            hitNode.node.parentId != null && hitNode.node.id == selectedNodeId
                        ) {
                            val localPos = (change.position - state.offset) / state.scale
                            val dropTarget = layoutedNodes.firstOrNull {
                                it.node.id != hitNode.node.id && it.contains(localPos)
                            }
                            state.dragging = NodeDragState(hitNode.node.id, change.position, dropTarget?.node?.id)
                        } else if (behavior.panEnabled && hitNode == null && hitPlus == null) {
                            state.offset += delta
                        }
                        change.consume()
                    }

                    val dragState = state.dragging
                    if (dragState != null) {
                        dragState.dropTargetId?.let { onNodeMove(dragState.nodeId, it) }
                        state.dragging = null
                    } else if (!dragging && !longPressed) {
                        when {
                            hitPlus != null -> onAddChildClick(hitPlus.nodeId)
                            hitNode != null -> onNodeClick(hitNode.node.id)
                            else -> onCanvasClick()
                        }
                    }
                }
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            withTransform({
                scale(state.scale, state.scale, pivot = Offset.Zero)
                translate(state.offset.x / state.scale, state.offset.y / state.scale)
            }) {
                layoutResult.edges.forEach { edge ->
                    with(edgeRenderer) { draw(edge, style) }
                }
                layoutedNodes.forEach { layouted ->
                    val visualState = layouted.visualState(state, selectedNodeId)
                    if (nodeContent == null) {
                        with(canvasNodeRenderer) {
                            draw(layouted.node, layouted.offset, layouted.size, visualState, style, textMeasurer)
                        }
                    }
                    if (visualState.isDropTarget) drawDropTargetHighlight(layouted, style)
                    if (editMode && behavior.addChildButtonsVisible) drawPlusButton(layouted, textMeasurer, style)
                }
                val dragState = state.dragging
                val dragged = layoutedNodes.firstOrNull { it.node.id == dragState?.nodeId }
                if (dragState != null && dragged != null && nodeContent == null) {
                    val localPos = (dragState.screenPos - state.offset) / state.scale
                    with(canvasNodeRenderer) {
                        draw(
                            dragged.node,
                            localPos - Offset(dragged.size.width / 2f, dragged.size.height / 2f),
                            dragged.size,
                            MindMapNodeVisualState(isDragGhost = true),
                            style,
                            textMeasurer,
                        )
                    }
                }
            }
        }

        if (nodeContent != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = state.offset.x
                        translationY = state.offset.y
                        scaleX = state.scale
                        scaleY = state.scale
                        transformOrigin = TransformOrigin(0f, 0f)
                    },
            ) {
                layoutedNodes.forEach { layouted ->
                    Box(
                        Modifier
                            .offset {
                                IntOffset(layouted.offset.x.roundToInt(), layouted.offset.y.roundToInt())
                            }
                            .size(
                                width = with(density) { layouted.size.width.toDp() },
                                height = with(density) { layouted.size.height.toDp() },
                            ),
                    ) {
                        nodeContent(layouted.node, layouted.visualState(state, selectedNodeId))
                    }
                }

                val dragState = state.dragging
                val dragged = layoutedNodes.firstOrNull { it.node.id == dragState?.nodeId }
                if (dragState != null && dragged != null) {
                    val localPos = (dragState.screenPos - state.offset) / state.scale
                    Box(
                        Modifier
                            .offset {
                                IntOffset(
                                    x = (localPos.x - dragged.size.width / 2f).roundToInt(),
                                    y = (localPos.y - dragged.size.height / 2f).roundToInt(),
                                )
                            }
                            .size(
                                width = with(density) { dragged.size.width.toDp() },
                                height = with(density) { dragged.size.height.toDp() },
                            ),
                    ) {
                        nodeContent(dragged.node, MindMapNodeVisualState(isDragGhost = true))
                    }
                }
            }
        }
    }
}

private fun MindMapLayoutNode.visualState(
    state: MindMapCanvasState,
    selectedNodeId: String?,
): MindMapNodeVisualState = MindMapNodeVisualState(
    isSelected = node.id == selectedNodeId,
    isDragging = node.id == state.dragging?.nodeId,
    isDropTarget = node.id == state.dragging?.dropTargetId,
)

private fun MindMapLayoutNode.contains(point: Offset): Boolean =
    point.x in offset.x..(offset.x + size.width) && point.y in offset.y..(offset.y + size.height)

private fun PlusButtonArea.contains(point: Offset): Boolean {
    val dx = point.x - center.x
    val dy = point.y - center.y
    return sqrt(dx * dx + dy * dy) <= radius
}

private fun MindMapLayoutNode.plusButtonArea(style: MindMapStyle, density: Float): PlusButtonArea {
    val radius = style.addButtonRadius.value * density
    val cx = offset.x + size.width / 2f
    val cy = offset.y + size.height + radius + style.addButtonSpacing.value * density
    return PlusButtonArea(node.id, Offset(cx, cy), radius + style.addButtonTouchPadding.value * density)
}
