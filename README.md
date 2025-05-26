# Prography Project
> Prography 10기 모바일 과제

## 📝 Overview
- **기간**: 2025.02.12 ~ 2025.02.21
- **설명**: Unsplash Api를 활용한 이미지 뷰 애플리케이션
- **주요 기능**
  - Unsplash의 최신 이미지 조회 및 랜덤 이미지 조회
  - 이미지 북마크 및 다운로드 기능
- **개발 중 목표**
  - Room DB와 Retrofit을 다시 사용해보면서 익숙해지기
  - Hilt 사용 전 후 비교하며 학습
  - 완전 Compose한 앱 만들기
  - 아키텍처 연습 MVVM + Clean Architecture
  - 성능 개선 경험
 
<!-- 앱 실행 화면 넣기 -->
![HomeScreen](https://github.com/user-attachments/assets/f3b2e875-4fff-48c8-adb5-57c032e13342) ![randomScreen](https://github.com/user-attachments/assets/5a7e2498-048b-4986-85ce-83a135684145)


</br>
<!--
## 📱How To Use
1. [Unsplash developer](https://unsplash.com/developers) 회원가입 및 로그인
2. [귀하의 앱 -> New Application] 에서 access key 발급
3. 앱의 local.properties 에 `Unsplash_Api_Key="access key"` 작성

</br>
-->


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

