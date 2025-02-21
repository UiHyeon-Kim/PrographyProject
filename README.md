# Prography Project
> Prography 10기 모바일 과제

## 📝 Overview
- 기간: 2025.02.12 ~ 2025.02.21
- 목적: Unsplash Api를 활용한 이미지 뷰 애플리케이션
- 주요 기능
  - Unsplash에 올라오는 최신 이미지 조회 및 랜덤 이미지 조회
  - 
## 🚀 Implementation
- 
## 🛠 Tech Stack
- 
## Libraries
- compoase navigation
- Retrofit2
- OkHttp3
- coil
- Hilt
- Room
## Pakage Struct
```
com.hanpro.prographyproject
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
## 🔍 아쉬운점 개선방향


