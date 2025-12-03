<img width="1440" height="510" alt="Surface Pro 8 - 6" src="https://github.com/user-attachments/assets/e4633938-9b03-49aa-93e5-894f3ab9a9ac" />

# ☁️구름한장(GureumPage)
> 전자책이나 종이책 상관없이 독서를 하며 기록을 남기고
<br>필사를 작성하거나 마인드맵으로 생각을 구조화 할 수 있는 독서 기록 앱

<br>

# 📖개요
> 구름한장은 독서 기록을 더 깊이, 더 오래 남기고 습관 형성을 도와주기 위해 개발한 앱입니다.
<br>전자책이 많아진 요즘 플로팅 윈도우로 편하게 독서 기록을 남길 수 있습니다.
<br>독서 기록을 더 오래, 마인드맵과 필사로 더 깊이 남기고,
<br>알림과 통계로 꾸준한 습관 형성을 도와줄 수 있도록 설계한 서비스입니다.

**🗓️ 기간**: 2025.07.28 ~ 2025.09.02

**👥 인원**: Android 개발자 4명

**👨‍💻 역할**: 부팀장 / Android 개발자
- Notion 기반 협업 환경 구성 & GitHub Project로 이슈 관리
- 앱 공통 테마 및 디자인 시스템 구축
- 온보딩 화면, 마인드맵 기능, 통계 등 주요 화면 개발
- 로컬 알림 기능 구현
- 코드 리뷰 및 품질 관리

**🏆 수상**: LIKELION Android Bootcamp 최우수 프로젝트

**🔗 GitHub**: [https://github.com/LIKELION-Android-Bootcamp-4th/GureumPage](https://github.com/LIKELION-Android-Bootcamp-4th/FinalProject-GureumPage-HIHIHIHI)

**🔗 Play Store**: [https://play.google.com/store/apps/gureumpage](https://play.google.com/store/apps/details?id=com.hihihihi.gureumpage)

<br>

# 📱주요 기능

| 섹션 | 주요 기능 |
|---|---|
| 인증 (Firebase Auth) | • 소셜 로그인: 네이버 / 카카오 / 구글 |
| 홈 | • 현재 읽는 중 책 목록과 정보 제공<br>• 하루 독서 목표 설정 & 도넛 차트로 달성도 확인<br>• 작성한 필사 문장 랜덤 노출 |
| 도서 검색 | • 키워드 검색: 제목 / 저자 / 출판사 / ISBN<br>• 결과: 표지·제목·저자·페이지 수 등 상세 정보 표시 |
| 서재 | • 상태별 목록 관리: 읽을 책 / 읽는 중 / 읽은 책<br>• 항목 클릭 시 책 상세 화면으로 이동 |
| 책 상세 화면 | • 독서 진행도·독서 기간·누적 시간·하루 평균 시간<br>• 기록: 타이머 또는 직접 기입<br>• 책당 1개의 마인드맵 생성·관리<br> |
| 독서 타이머 | • 책 상세/위젯에서 진입, 시작·정지·재개<br>• 플로팅 윈도우 제공(드래그/스와이프 조절, 다른 앱 위 동작) |
| 인상 깊은 문장(필사) | • 타이머 중 또는 책 상세에서 작성<br>• 앱 내 전체 필사 목록에서 도서 구분 없이 열람 |
| 마인드맵 | • 책별 1개 마인드맵 생성<br>• 아이디어/개념/인물/사건 흐름 시각화<br>• 노드 추가·삭제·이동·편집<br>• 스냅샷 기반 undo/redo 등 |
| 통계 | • 읽은 책 장르: 도넛 차트<br>• 시간대/요일별 읽은 페이지: 바/라인 차트<br>• 주간·월간·연간 필터 |
| 마이페이지 | • 닉네임 변경<br>• 총 독서 통계 요약<br>• 라이트/다크 테마 전환<br>• 로그아웃, 탈퇴 |
| 온보딩 | • 닉네임 설정<br>• 앱 사용 목적 조사 & 앱 소개<br>• 테마(라이트/다크) 선택 |

<br>

# 🛠️기술 스택
| 카테고리 | 기술 |
| ----- | ----- |
| **언어/빌드** | Kotlin, Gradle(KTS) |
| **UI/UX** | Jetpack Compose, Glance |
| **아키텍처** | MVVM + Clean Architecture, Multi Module |
| **DI** | Hilt |
| **비동기/데이터** | Coroutine, Flow, DataStore |
| **백엔드** | Firebase Auth, Functions, Firestore, FCM |
| **API** | 책 검색 - 알라딘 API |
| **네트워크** | Retrofit2, OkHttp3, Gson |
| **이미지/애니메이션** | Coil, Lottie |
| **기타** | MPAndroidChart, kizitonwose/Calendar, android-thinkmap-treeview |

<br>

# 🧱아키텍처

### 시스템 구조도
<img width="1531" height="1042" alt="Frame 120" src="https://github.com/user-attachments/assets/03cf276a-ac97-49fc-94e6-f0de09ac10ed" />

- 백엔더가 없는 팀으로, 클라우드 서비스인 **`Firebase`** 를 중심으로 백엔드 구성
    - **`Auth`** 로 소셜 로그인을 사용하며 **`Functions`** 로 Custom Token을 반환하여 카카오, 네이버 로그인 지원
    - **`FireStore`** 로 데이터 저장 및 조회 기능 구현
    - 기본적으로 로컬 알림을 사용하되, 실시간 알림 필요 시를 대비해 FCM 사용
- 도서 검색을 위한 알라딘 API 사용

### CA + MVVM + Multi Module
<img width="2434" height="524" alt="Frame 121" src="https://github.com/user-attachments/assets/d8e5a821-5d8f-4f3d-bb35-32e13af2126f" />

- **`MVVM`** 으로 UI 와 비즈니스 로직을 분리
- **`Clean Architecture`** 로 계층간 의존성을 줄이고 확장성과 유지보수성을 높임
- **`Multi Module`** 을 적용해 계층 간 구분을 더 확실하게 만듦

<br>

# 주요 화면 스크린샷
<table>
  <tr>
    <td align="center">
      홈 화면
    </td>
    <td align="center">
      책 상세
    </td>
    <td align="center">
      필사 추가
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="454" height="936" alt="Group 392" src="https://github.com/user-attachments/assets/5aee2309-75cf-48e0-b1d1-b42ed4e03a21" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 391" src="https://github.com/user-attachments/assets/4e1887e5-36e5-45e0-a7f7-5f63ebfebf3d" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 390" src="https://github.com/user-attachments/assets/c936b1ca-af8b-41ca-97d4-1d4d5329e4b6" />
    </td>
  </tr>
  <tr>
    <td align="center">
      마인드맵
    </td>
    <td align="center">
      스톱워치
    </td>
    <td align="center">
      통계
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="454" height="936" alt="Group 389" src="https://github.com/user-attachments/assets/4a833334-74a8-44f4-8bc7-8ababdefc7c3" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 388" src="https://github.com/user-attachments/assets/6ae474f8-f8c2-4378-9a4b-f5dc9d9dd218" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 387" src="https://github.com/user-attachments/assets/fc7d645d-1ea2-4e73-ae9b-507e24d1503e" />
    </td>
  </tr>
</table>

<br>

## 시연 영상
<p align="center">
  <a href="https://www.youtube.com/watch?v=mC706rSxEHk">
    <img src="https://img.youtube.com/vi/2eFPaFB1gIA/0.jpg" alt="[시연 영상](https://youtu.be/mC706rSxEHk)" width="800">
  </a>
</p>

<br>
