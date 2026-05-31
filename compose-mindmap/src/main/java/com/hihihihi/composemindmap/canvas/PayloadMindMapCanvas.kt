package com.hihihihi.composemindmap.canvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import com.hihihihi.composemindmap.layout.MindMapLayoutEngine
import com.hihihihi.composemindmap.layout.TopDownTreeLayoutEngine
import com.hihihihi.composemindmap.model.MindMapBehavior
import com.hihihihi.composemindmap.model.MindMapNodeWithPayload
import com.hihihihi.composemindmap.model.MindMapStyle
import com.hihihihi.composemindmap.model.MindMapValidationResult
import com.hihihihi.composemindmap.model.defaultNodeSize

@Composable
fun <T> PayloadMindMapCanvas(
    nodes: List<MindMapNodeWithPayload<T>>,
    modifier: Modifier = Modifier,
    state: MindMapCanvasState = rememberMindMapCanvasState(),
    style: MindMapStyle = MindMapStyle(),
    behavior: MindMapBehavior = MindMapBehavior(),
    selectedNodeId: String? = null,
    editMode: Boolean = false,
    layoutEngine: MindMapLayoutEngine = TopDownTreeLayoutEngine,
    nodeSize: (MindMapNodeWithPayload<T>) -> DpSize = { style.defaultNodeSize },
    canvasNodeRenderer: MindMapCanvasNodeRenderer = DefaultMindMapCanvasNodeRenderer,
    edgeRenderer: MindMapEdgeRenderer = CurvedMindMapEdgeRenderer,
    nodeContent: (@Composable (MindMapNodeWithPayload<T>, MindMapNodeVisualState) -> Unit)? = null,
    onValidationError: (MindMapValidationResult.Invalid) -> Unit = {},
    onNodeClick: (nodeId: String) -> Unit = {},
    onNodeLongClick: (nodeId: String) -> Unit = {},
    onCanvasClick: () -> Unit = {},
    onAddChildClick: (parentId: String) -> Unit = {},
    onNodeMove: (nodeId: String, newParentId: String) -> Unit = { _, _ -> },
) {
    val payloadById = nodes.associateBy { it.node.id }
    MindMapCanvas(
        nodes = nodes.map { it.node },
        modifier = modifier,
        state = state,
        style = style,
        behavior = behavior,
        selectedNodeId = selectedNodeId,
        editMode = editMode,
        layoutEngine = layoutEngine,
        nodeSize = { node -> nodeSize(payloadById.getValue(node.id)) },
        canvasNodeRenderer = canvasNodeRenderer,
        edgeRenderer = edgeRenderer,
        nodeContent = nodeContent?.let { content ->
            { node, visualState -> content(payloadById.getValue(node.id), visualState) }
        },
        onValidationError = onValidationError,
        onNodeClick = onNodeClick,
        onNodeLongClick = onNodeLongClick,
        onCanvasClick = onCanvasClick,
        onAddChildClick = onAddChildClick,
        onNodeMove = onNodeMove,
    )
}
