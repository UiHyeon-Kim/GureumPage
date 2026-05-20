# Phase 3 적용 상태 리포트

상단 요약

현재 Phase 3 플랜은 전체 기준 약 20% 내외로 적용된 상태로 판단됨

실제 구현 흔적은 F-4 알림 설정에 집중되어 있고, F-5는 플랜과 다른 방식으로 일부만 들어가 있음

F-6, F-7, F-8은 기존 구현 또는 무관 기능을 제외하면 Phase 3 기능 요구사항 적용으로 보기 어려움

아키텍처 컨벤션 7번은 현재 브랜치에서 Presentation `UiState`의 실제 `Loading`/`Content`/`Error` 분기, 주요 one-off `Effect`, lifecycle collect/type-safe navigation 검증까지 적용됨

빌드는 `:presentation:compileDebugKotlin`, `:domain:test`, `:app:assembleDebug` 기준 통과함

## 0. 검토 기준

| 기준 | 확인 방식 |
|---|---|
| 파일 생성 여부 | 플랜의 File Creation Checklist와 실제 `rg --files`, `find` 결과 비교 |
| 코드 반영 여부 | 관련 ViewModel, Repository, Screen, Worker, Navigation 파일 직접 확인 |
| 아키텍처 일치 여부 | 플랜의 sealed UiState, Effect Channel, lifecycle collect, type-safe route 기준과 비교 |
| 빌드 가능성 | `./gradlew :presentation:assembleDebug`, `./gradlew :presentation:compileDebugKotlin` 실행 |

## 1. 전체 적용률

| 기능 | 적용률 추정 | 상태 | 핵심 판단 |
|---|---:|---|---|
| F-4 Notification Settings | 약 70~75% | 부분 적용 | 도메인·데이터·화면·네비게이션과 월간 토글은 있음, 요일 설정 등 일부 플랜 필드는 없음 |
| F-5 Persistent Timer FAB | 약 20% | 방향 불일치 | 전역 타이머 상태 Flow는 있음, FAB 컴포넌트와 Scaffold FAB는 없음 |
| F-6 Bookshelf View | 약 5~10% | 거의 미적용 | 기존 서재가 Grid를 쓰지만 List ↔ Shelf 전환과 저장은 없음 |
| F-7 Reading Heatmap | 0% | 미적용 | 모델, UseCase, Repository 확장, UI 컴포넌트 없음 |
| F-8 Reading Notes | 0% | 미적용 | Note 도메인·데이터·상세 탭 구현 없음 |

> 결론적으로 Phase 3 전체는 “F-4 중심의 부분 구현 단계”이며, 플랜 완료 상태로 보기는 어려움

## 2. F-4 Notification Settings

### 적용된 부분

| 플랜 항목 | 현재 상태 | 근거 |
|---|---|---|
| `NotificationSettings` 모델 | 부분 적용 | `domain/model/NotificationSettings.kt` 존재, 단 필드가 플랜보다 축소됨 |
| 알림 설정 Repository | 부분 적용 | 플랜의 `UserPreferencesRepository` 확장이 아니라 별도 `NotificationPreferencesRepository`로 구현됨 |
| Get/Update UseCase | 적용 | `GetNotificationSettingsUseCase`, `UpdateNotificationSettingsUseCase` 존재 |
| DataStore 저장 | 적용 | `notification_prefs` DataStore와 daily/hour/minute/goal/weekly/monthly 키 존재 |
| DI 연결 | 적용 | `RepositoryModule`, `DataSourceModule`에 알림 설정 바인딩/프로바이딩 존재 |
| 설정 화면 | 적용 | daily, goal 80%, weekly, monthly toggle UI 존재 |
| 네비게이션 연결 | 적용 | `NotificationSettings` route와 MyPage 진입점 존재 |

### 누락 또는 불일치

| 플랜 요구 | 현재 상태 |
|---|---|
| `weeklySummaryDayOfWeek` | 모델과 DataStore에 없음 |
| 명시적 `NotificationSettingsEvent` | 화면 콜백을 ViewModel 함수로 직접 연결하고 있어 별도 Event 타입은 없음 |
| Worker 시작 시 설정을 읽고 disabled면 early return | Daily, Goal80 등 일부 Worker는 새 설정 Repository를 직접 읽지 않음 |
| `UpdateNotificationSettingsUseCase`의 `runSuspendCatching` | 적용되지 않음 |
| type-safe `@Serializable NotificationSettingsRoute` | 적용됨, `@Serializable data object NotificationSettings`와 `composable<NotificationSettings>` 사용 |

### 주요 리스크

| 리스크 | 설명 |
|---|---|
| 알림 설정과 실제 Worker 동작 불일치 | 설정 화면에서 끈 토글이 이미 예약된 Worker 실행 시점에 다시 검증되지 않음 |
| Daily Reminder 재예약 시간 불일치 | Worker가 설정값 대신 `22:00`으로 재예약함 |
| Weekly off 처리 부작용 | 주간 요약을 끄면 `cancelAll()` 후 월간/연간을 재예약하는 방식이라 요약 전체 예약 정책이 복잡해짐 |
| 목표 80% 알림 저장소 분리 | 설정 DataStore의 `goal_enabled`와 `Goal80ReminderWorker`의 `goal_progress` SharedPreferences가 분리되어 있음 |

## 3. F-5 Persistent Timer FAB

### 적용된 부분

| 플랜 항목 | 현재 상태 |
|---|---|
| 전역 타이머 running 상태 | `TimerRepository.isTimerRunning` 존재 |
| top-level에서 타이머 상태 관찰 | `MainActivity`에서 `isTimerRunning` collect |

### 누락 또는 불일치

| 플랜 요구 | 현재 상태 |
|---|---|
| `domain/repository/TimerStateRepository.kt` | 없음, `TimerRepository`가 presentation 패키지에 있음 |
| `ui/common/PersistentTimerFab.kt` | 없음 |
| `TimerViewModel.isRunning`, `elapsedFormatted` 직접 노출 | 없음 |
| Scaffold `floatingActionButton` | 없음 |
| 타이머 실행 중 다른 화면에서 FAB 표시 | 대신 다른 화면에서 Timer 화면으로 자동 이동함 |

현재 구현은 “persistent FAB”가 아니라 “타이머 실행 중 Timer 화면 강제 이동”에 가까움

## 4. F-6 Bookshelf View

현재 서재 화면은 `LazyVerticalGrid(GridCells.Fixed(3))`를 사용하고 있으나, 플랜의 핵심인 List ↔ Shelf 전환 기능은 없음

| 플랜 요구 | 현재 상태 |
|---|---|
| `LibraryViewMode.kt` | 없음 |
| `BookshelfGrid.kt` | 없음 |
| `GetLibraryViewModeUseCase`, `SaveLibraryViewModeUseCase` | 없음 |
| DataStore `library_view_mode` | 없음 |
| `LibraryUiState.viewMode` | 없음 |
| AppBar 전환 아이콘 | 없음 |
| List/Shelf 조건부 렌더링 | 없음 |

## 5. F-7 Reading Heatmap

F-7은 적용 흔적이 없음

| 플랜 요구 | 현재 상태 |
|---|---|
| `DailyReadCount` 모델 | 없음 |
| `GetYearlyReadingHeatmapUseCase` | 없음 |
| `HistoryRepository.getHistoriesForYear()` | 없음 |
| `HistoryRepositoryImpl.getHistoriesForYear()` | 없음 |
| `HeatmapUiModel`, `HeatmapCanvas`, `HeatmapDayDetailSheet` | 없음 |
| `StatisticsScreen` Heatmap section/tab | 없음 |

현재 통계 화면은 장르 분포, 시간 분포, 페이지 차트만 렌더링함

## 6. F-8 Reading Notes

F-8은 적용 흔적이 없음

기존 `MemoViewModel`은 Note 기능이 아니라 Timer 화면에서 Quote를 추가하는 구조임

| 플랜 요구 | 현재 상태 |
|---|---|
| `Note` 모델 | 없음 |
| `NoteRepository` | 없음 |
| Note UseCase 4종 | 없음 |
| `NoteDataSource`, `NoteDataSourceImpl` | 없음 |
| `NoteRepositoryImpl` | 없음 |
| DI 바인딩 | 없음 |
| `NoteViewModel`, `NoteListScreen`, `NoteEditBottomSheet` | 없음 |
| `BookDetailScreen` 메모 탭 | 없음 |

## 7. 아키텍처 컨벤션 적용 상태

| 컨벤션 | 현재 상태 |
|---|---|
| `collectAsStateWithLifecycle()` | Presentation 화면 기준 직접 `collectAsState()` 사용 없음 |
| sealed `UiState` + `Effect Channel` | 모든 Presentation `*UiState`를 sealed interface + `Loading`/`Content`/`Error` contract로 전환하고, 기존 `UiState()` factory와 공통 `override val` 호환 레이어 제거 |
| Screen 상태 분기 | 주요 Screen은 `when (state)` 또는 `contentOrDefault()`로 상태를 명시적으로 분기하고, 렌더링에는 `Content` 필드만 전달 |
| one-off Effect | 알림 설정, 검색, 책 상세, 로그인, 마이페이지, 스플래시, 탈퇴의 toast/snackbar/navigation을 Effect로 분리 |
| UiModel 분리 | 기존 UiModel 기반 화면은 유지, 알림 설정은 도메인 설정값을 화면 상태 `Content`로 렌더링 |
| Type-safe Navigation | `@Serializable` route, `composable<T>`, `toRoute<T>`, `hasRoute` 기반으로 적용됨 |
| ViewModel에서 FirebaseAuth 제거 | Presentation 계층 `FirebaseAuth` 직접 import 없음, 인증 정보는 UseCase를 통해 접근 |

정적 확인 결과

| 명령 | 결과 |
|---|---|
| `rg "collectAsState\\(" presentation/src/main/java/com/hihihihi/presentation -g "*.kt"` | 0건 |
| `rg "^data class .*UiState\|[A-Za-z]+UiState\\(" presentation/src/main/java/com/hihihihi/presentation/ui -g "*.kt"` | 0건 |
| `rg "addBookMessage\|successMessage\|navEvent" presentation/src/main/java/com/hihihihi/presentation -g "*.kt"` | 0건 |
| `rg "navigate\\(\\\"" presentation/src/main/java/com/hihihihi/presentation -g "*.kt"` | 0건 |
| `rg "composable\\(\\\"" presentation/src/main/java/com/hihihihi/presentation -g "*.kt"` | 0건 |
| `rg "FirebaseAuth" presentation/src/main/java/com/hihihihi/presentation -g "*.kt"` | 0건 |

## 8. 빌드 검증

실행한 명령

```bash
./gradlew :presentation:compileDebugKotlin
./gradlew :domain:test
./gradlew :app:assembleDebug
```

결과

| 명령 | 결과 |
|---|---|
| `:presentation:compileDebugKotlin` | 통과 |
| `:domain:test` | 통과 |
| `:app:assembleDebug` | 통과 |

남은 경고는 deprecated Gradle/Android 옵션, 기존 `TabRow`, deprecated `hiltViewModel` import, 기존 Google Sign-In API deprecation 등이며 이번 컨벤션 변경으로 인한 컴파일 실패는 없음

## 9. 우선순위 제안

| 우선순위 | 작업 |
|---:|---|
| 1 | F-4의 요일 UI, Worker early return 등 남은 알림 정책을 플랜과 맞추기 |
| 2 | Screen callback을 별도 Event 타입으로 표준화할지 결정하기 |
| 3 | 기존 Google Sign-In, `hiltViewModel`, `TabRow` deprecated API를 후속 브랜치에서 정리하기 |
| 4 | F-5를 자동 이동 방식에서 `PersistentTimerFab` 방식으로 전환하기 |
| 5 | F-6~F-8은 별도 브랜치 또는 기능 단위로 순차 구현하기 |

## 10. 한 줄 결론

Phase 3 기능 전체는 아직 부분 구현 단계지만, 7번 아키텍처 컨벤션은 현재 브랜치에서 타입 골격을 넘어 실제 화면 상태 분기와 주요 Effect 흐름까지 적용됨
