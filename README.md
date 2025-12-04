# ☁️구름한장(GureumPage)

> 전자책이나 종이책 상관없이 독서 기록을 남기고
<br>필사를 작성하거나 마인드맵으로 생각을 구조화 할 수 있는 독서 기록 앱

<img width="1440" height="510" alt="Surface Pro 8 - 6" src="https://github.com/user-attachments/assets/e4633938-9b03-49aa-93e5-894f3ab9a9ac" />

<br>

# 📖개요

> 구름한장은 독서 기록을 더 깊이, 더 오래 남기고 습관 형성을 도와주기 위해 개발한 앱입니다.
<br>전자책이 많아진 요즘 환경에 맞춘 **플로팅 윈도우 타이머**
<br>독서 기록을 더 오래, **마인드맵**과 **필사**로 더 깊이 남기고,
<br>**알림**과 **통계**로 꾸준한 습관 형성을 도와줄 수 있도록 설계한 서비스입니다.

**🗓️ 기간**: 2025.07.28 ~ 2025.09.02

**👥 인원**: Android 개발자 4명

**👨‍💻 역할**: 부팀장 / Android 개발자
- Notion 기반 협업 환경 구성 & GitHub Project로 이슈 관리
- 앱 공통 테마 및 디자인 시스템 구축
- 온보딩 화면, 마인드맵 기능, 통계 등 주요 화면 개발
- 로컬 알림 기능 구현
- 코드 리뷰 및 품질 관리

**🏆 수상**: LIKELION Android Bootcamp 최우수 프로젝트

**🔗 Team GitHub**: [https://github.com/LIKELION-Android-Bootcamp-4th/GureumPage](https://github.com/LIKELION-Android-Bootcamp-4th/FinalProject-GureumPage-HIHIHIHI)

**🔗 Play Store**: [https://play.google.com/store/apps/gureumpage](https://play.google.com/store/apps/details?id=com.hihihihi.gureumpage)

<br>

# 📱주요 기능 

| 섹션 | 주요 기능 |
|---|---|
| 인증 (Firebase Auth) | • 소셜 로그인: 네이버 / 카카오 / 구글 |
| 홈 | • 현재 읽는 중 책 목록과 정보 제공<br>• 하루 독서 목표 설정 & 도넛 차트로 달성도 확인<br>• 작성한 필사 문장 랜덤 노출 |
| 도서 검색 | • 키워드 검색: 제목 / 저자 / 출판사 / ISBN<br>• 결과: 표지·제목·저자·페이지 수 등 상세 정보 표시 |
| 서재 | • 상태별 목록 관리: 읽을 책 / 읽는 중 / 읽은 책<br>• 항목 클릭 시 책 상세 화면으로 이동 |
| 책 상세 화면 | •용

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

# 트러블슈팅

<details>
    <summary>마인드맵 라이브러리 선택 & Undo/Redo 불안정 문제</summary>
    내용내용
</details> 

<details>
    <summary>OAuth 로그인 릴리즈 빌드 오류</summary>
</details> 

<details>
    <summary>시간 선택 커스텀 피커 무한 롤링 문제</summary>
</details> 

<br>

# 📷주요 화면 스크린샷

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

# 📹시연 영상

<p align="center">
  <a href="https://www.youtube.com/watch?v=mC706rSxEHk">
    <img src="https://img.youtube.com/vi/2eFPaFB1gIA/0.jpg" alt="[시연 영상](https://youtu.be/mC706rSxEHk)" width="800">
  </a>
</p>

<br>

# 🚀향후 개선 계획

- 테스트 코드 작성 및 UI 테스트 도입
- Firebase Analytics, Performance 추가
- 마인드맵 AI 도입 - 자동 마인드맵 생성 기능
- 위젯 기능 확장
- 성능 측정 후 개선

<br>
