package com.hihihihi.composemindmap.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import com.hihihihi.composemindmap.model.MindMapNode
import com.hihihihi.composemindmap.model.MindMapStyle
import com.hihihihi.composemindmap.model.defaultNodeSize

internal data class LayoutedNode(
    val node: MindMapNode,
    val offset: Offset,
    val size: Size,
)

internal object TreeLayoutEngine {

    fun layout(
        nodes: List<MindMapNode>,
        style: MindMapStyle,
        density: Density,
        nodeSize: (MindMapNode) -> DpSize = { style.defaultNodeSize },
    ): List<LayoutedNode> {
        if (nodes.isEmpty()) return emptyList()
        return with(density) {
            val hGap = style.horizontalGap.toPx()
            val vGap = style.verticalGap.toPx()
            val sizes = nodes.associate { node ->
                val size = nodeSize(node)
                node.id to Size(size.width.toPx(), size.height.toPx())
            }

            val childrenMap: Map<String?, List<MindMapNode>> = nodes.groupBy { it.parentId }
            val root = childrenMap[null]?.firstOrNull() ?: return@with emptyList()
            val result = mutableListOf<LayoutedNode>()
            val subtreeWidths = mutableMapOf<String, Float>()

            fun subtreeWidth(nodeId: String): Float {
                subtreeWidths[nodeId]?.let { return it }
                val nodeWidth = sizes.getValue(nodeId).width
                val children = childrenMap[nodeId]
                if (children.isNullOrEmpty()) return nodeWidth.also { subtreeWidths[nodeId] = it }
                val childrenTotal = children.sumOf { subtreeWidth(it.id).toDouble() }.toFloat()
                return maxOf(nodeWidth, childrenTotal + hGap * (children.size - 1))
                    .also { subtreeWidths[nodeId] = it }
            }

            fun place(node: MindMapNode, startX: Float, y: Float) {
                val size = sizes.getValue(node.id)
                val allocated = subtreeWidth(node.id)
                val nodeX = startX + (allocated - size.width) / 2f
                result += LayoutedNode(node, Offset(nodeX, y), size)

                val children = childrenMap[node.id] ?: return
                val childY = y + size.height + vGap
                var childX = startX
                for (child in children) {
                    place(child, childX, childY)
                    childX += subtreeWidth(child.id) + hGap
                }
            }

            place(root, 0f, 0f)
            result
        }
    }
}
