package com.hihihihi.presentation.ui.mindmap.mapper

import androidx.compose.ui.graphics.Color
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.composemindmap.model.MindMapNode

fun MindmapNode.toLibraryModel(): MindMapNode = MindMapNode(
    id = mindmapNodeId,
    title = nodeTitle,
    subtitle = nodeEx,
    parentId = parentNodeId,
    color = color?.toLongOrNull()?.let { Color(it) },
    icon = icon,
    imageUrl = bookImage,
)

fun MindMapNode.toDomain(mindmapId: String, userId: String): MindmapNode = MindmapNode(
    userId = userId,
    mindmapNodeId = id,
    mindmapId = mindmapId,
    nodeTitle = title,
    nodeEx = subtitle,
    parentNodeId = parentId,
    color = color?.value?.toString(),
    icon = icon,
    deleted = false,
    bookImage = imageUrl,
)
