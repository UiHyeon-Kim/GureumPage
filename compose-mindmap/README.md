# compose-mindmap

Compose 기반 내부 마인드맵 라이브러리입니다. 트리 검증, Canvas 렌더링, 줌과 이동, 노드 선택 및 이동,
편집 이력, 레이아웃 전략, 연결선 렌더러를 제공합니다.

## Setup

소비 모듈에 프로젝트 의존성을 추가합니다.

```kotlin
dependencies {
    implementation(project(":compose-mindmap"))
}
```

## Basic Usage

`MindMapNode` 목록에는 루트 노드가 정확히 하나 있어야 합니다. 각 `id`는 고유해야 하며, 루트 외 노드는
존재하는 부모의 `id`를 `parentId`로 가져야 합니다.

```kotlin
val nodes = listOf(
    MindMapNode(id = "root", title = "Reading"),
    MindMapNode(id = "fiction", title = "Fiction", parentId = "root"),
    MindMapNode(id = "essay", title = "Essay", parentId = "root"),
)

MindMapCanvas(nodes = nodes)
```

검증 실패 시 노드는 그려지지 않습니다. 오류를 처리하려면 `onValidationError`를 전달합니다.

## Style And Behavior

`MindMapStyle`은 노드 크기, 간격, 색상, 텍스트 스타일, 편집 UI 크기를 제어합니다.
`MindMapBehavior`는 줌, 이동, 노드 드래그, 자식 추가 버튼, 최초 중앙 정렬을 제어합니다.

```kotlin
MindMapCanvas(
    nodes = nodes,
    style = MindMapStyle(horizontalGap = 32.dp, verticalGap = 64.dp),
    behavior = MindMapBehavior(zoomEnabled = true, panEnabled = true),
)
```

노드별 크기가 필요하면 `nodeSize`를 전달합니다.

## Custom Node Content

Compose 슬롯을 사용하면 앱의 디자인 시스템으로 노드를 완전히 교체할 수 있습니다.

```kotlin
MindMapCanvas(
    nodes = nodes,
    nodeContent = { node, visualState ->
        BookNode(node = node, selected = visualState.isSelected)
    },
)
```

Canvas 기반 커스텀 렌더링이 필요하면 `MindMapCanvasNodeRenderer`를 구현합니다.

## Typed Payload

도메인 데이터를 노드와 함께 렌더링해야 할 때는 `withPayload`와 `PayloadMindMapCanvas`를 사용합니다.
편집 컨트롤러는 기본 `MindMapNode` 목록을 계속 다루므로 도메인 저장 정책은 앱에 남습니다.

```kotlin
data class Book(val coverUrl: String)

val items = nodes.map { node -> node.withPayload(Book(coverUrl = "...")) }

PayloadMindMapCanvas(
    nodes = items,
    nodeContent = { item, _ ->
        BookCover(url = item.payload.coverUrl)
    },
)
```

## Layout And Edges

기본 배치는 `TopDownTreeLayoutEngine`, 기본 연결선은 `CurvedMindMapEdgeRenderer`입니다.

```kotlin
MindMapCanvas(
    nodes = nodes,
    layoutEngine = LeftToRightTreeLayoutEngine,
    edgeRenderer = StraightMindMapEdgeRenderer,
)
```

특수 배치가 필요하면 `MindMapLayoutEngine`, 특수 연결선이 필요하면 `MindMapEdgeRenderer`를 구현합니다.

## Editing

`editMode = true`이면 자식 추가 버튼과 선택 노드 이동을 사용할 수 있습니다. UI 콜백으로 변경 요청을 받고,
실제 목록 갱신은 앱 상태에서 처리합니다. Undo와 redo가 필요하면 `MindMapEditController`를 사용합니다.

```kotlin
val controller = remember { MindMapEditController() }

MindMapCanvas(
    nodes = nodes,
    editMode = true,
    selectedNodeId = selectedId,
    onAddChildClick = { parentId -> addChild(parentId) },
    onNodeMove = { nodeId, parentId ->
        nodes = controller.moveNode(nodes, nodeId, parentId)
    },
)
```

## Debug Previews

Android Studio에서 `MindMapSamplePreviews.kt`를 열면 기본형, 수평 직선형, payload 슬롯형을 확인할 수 있습니다.
