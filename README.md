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
│  │  └─📄PhotoDetail.kt
│  └─📂source
│     ├─📂local
│     │  ├─📄AppDatabase.kt
│     │  ├─📄Bookmark.kt
│     │  └─📄BookmarkDao.kt
│     └─📂remote
│        ├─📄OkHttpDownloader.kt
│        └─📄UnsplashApi.kt
├─📂di
│  ├─📄AppModule.kt
│  └─📄DatabaseModule.kt
├─📂domain
│  └─📂repository
│     └─📄BookmarkRepository.kt
├─📂ui
│  ├─📂components
│  │  ├─📄BottomNavigation.kt
│  │  └─📄TopBar.kt
│  ├─📂dialog
│  │  └─📄PhotoDetailDialog.kt
│  ├─📂navigation
│  │  ├─📄AppNavHost.kt
│  │  └─📄NavigationItem.kt
│  ├─📂screens
│  │  ├─📄HomeScreen.kt
│  │  └─📄RandomPhotoScreen.kt
│  ├─📂theme
│  │  └─📄CustomTypography.kt
│  └─📂viewmodel
│     ├─📄PhotoDetailViewModel.kt
│     └─📄PhotoViewModel.kt
├─📄MainActivity.kt
└─📄PrographyApplication.kt
```

</br>

## 🔍 Improvements
- HomeScreen 북마크 이미지 LazyVerticalStaggeredGrid의 padding에 가려지지 않도록 하기
  - ~~LazyColumn + LazyVerticalStaggeredGrid 조합 크래시~~
- 스켈레톤 뷰
- RandomPhotoScreen

