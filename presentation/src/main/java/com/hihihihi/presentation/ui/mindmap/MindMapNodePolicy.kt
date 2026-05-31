package com.hihihihi.presentation.ui.mindmap

import com.hihihihi.composemindmap.model.MindMapNode

internal fun canDeleteMindMapNode(node: MindMapNode): Boolean = node.parentId != null
