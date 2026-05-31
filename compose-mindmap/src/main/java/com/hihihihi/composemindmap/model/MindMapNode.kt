package com.hihihihi.composemindmap.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MindMapNode(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val parentId: String? = null,
    val color: Color? = null,
    val icon: String? = null,
    val imageUrl: String? = null,
)
