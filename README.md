# Prography Project
> Prography 10기 모바일 과제

## 📝 Overview
- **기간**: 2025.02.12 ~ 2025.02.21
- **목적**: Unsplash Api를 활용한 이미지 뷰 애플리케이션
- **주요 기능**
  - Unsplash의 최신 이미지 조회 및 랜덤 이미지 조회
  - 이미지 북마크 및 다운로드 기능

</br>

## 🛠 Tech Stack & Libraries
- **Language**: Kotlin  
- **UI Framework**: Jetpack Compose  
- **Architecture**: MVVM + Clean Architecture  
- **Networking**: Retrofit2, OkHttp3  
- **Image Loading**: Coil  
- **Dependency Injection**: Hilt  
- **Database**: Room  
- **Navigation**: Compose Navigation  
- **Coroutines**: StateFlow, Flow

</br>

## 📂 Package Structure
```
🗂️com.hanpro.prographyproject
├─📂data
│  ├─📂model
│  │  └─📄PhotoDetail.kt           # Unsplash API JSON 매핑 데이터 클래스
│  └─📂source
│     ├─📂local
│     │  ├─📄AppDatabase.kt        # Room Database
│     │  ├─📄Bookmark.kt           # 북마크 정보 데이터 클래스
│     │  └─📄BookmarkDao.kt        # Room을 이용한 북마크 CRUD 작업
│     └─📂remote
│        ├─📄OkHttpDownloader.kt   # OkHttp 다운로드 로직
│        └─📄UnsplashApi.kt        # Retrofit을 이용한 Unsplash API 정의
├─📂di
│  ├─📄AppModule.kt                # 앱 의존성 제공 DI 모듈
│  └─📄DatabaseModule.kt           # Database 관련 DI 모듈
├─📂domain
│  └─📂repository
│     └─📄BookmarkRepository.kt    # DAO 캡슐화한 북마크 Repository
├─📂ui
│  ├─📂components
│  │  ├─📄BottomNavigation.kt      # 바텀 내비게이션 컴포넌트
│  │  └─📄TopBar.kt                # 상단 탑바 컴포넌트
│  ├─📂dialog
│  │  └─📄PhotoDetailDialog.kt     # 포토 상세 정보 다이얼로그 UI
│  ├─📂navigation
│  │  ├─📄AppNavHost.kt            # 앱 내비게이션 
│  │  └─📄NavigationItem.kt        # 내비게이션 아이템 sealed class
│  ├─📂screens
│  │  ├─📄HomeScreen.kt            # 홈 화면 UI (북마크, 최신 이미지)
│  │  └─📄RandomPhotoScreen.kt     # 랜덤 포토 UI
│  ├─📂theme
│  │  └─📄CustomTypography.kt      # 앱 내 사용하는 커스텀 폰트 설정
│  └─📂viewmodel
│     ├─📄PhotoDetailViewModel.kt  # 포토 상세 정보 ViewModel
│     └─📄PhotoViewModel.kt        # 홈, 랜덤 포토 화면 ViewModel
├─📄MainActivity.kt                # 앱 진입점
└─📄PrographyApplication.kt        # Hilt 적용한 Application 클래스
```

</br>

## 🔍 Improvements
- HomeScreen 북마크 이미지 LazyVerticalStaggeredGrid의 padding에 가려지지 않도록 하기
  - [x] LazyColumn + LazyVerticalStaggeredGrid 크래시.
  - [ ] 해결법 모색 필요
- 스켈레톤 뷰
  - [x] Accompanist Placeholder
  - [x] [shindonghwi-Skeleton View](https://github.com/shindonghwi/android_jetpack_compose_skeleton_view?source=post_page-----cef0ee2d2052---------------------------------------)
  - [ ] FaceBook Shimmer [(참고링크1)](https://reco-dy.tistory.com/7) [(참고링크2)](https://onlyfor-me-blog.tistory.com/792)
- RandomPhotoScreen
  - 좌측 스와이프 후, 우측 스와이프 시 이전 사진이 북마크 되는 오류.
- 다운로드 기능 이미지 저장 안되는 오류
  - [x] [DownloadManager](https://developer.android.com/reference/android/app/DownloadManager): [보안관련 문제](https://developer.android.com/privacy-and-security/risks/unsafe-download-manager?hl=ko)
  - [x] OkHttp: 다른 링크를 넣었을 땐 오류. 다운로드 링크 넣었을 땐 통신 성공 but, 저장되진 않음
  - [ ] [Cronet](https://developer.android.com/develop/connectivity/cronet?hl=ko): DownloadManager 보완
- 백 버튼 딜레이
  - 백 버튼 2번 누를 경우 나가지도록 딜레이 넣기

