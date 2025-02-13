package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.domain.model.HomeUiState
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PhotoViewModel = PhotoViewModel(),
    uiState: HomeUiState = HomeUiState(),
    onImageClick: (PhotoDetail) -> Unit = {},
) {
    val photos by viewModel.latestPhotos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLatestPhotos(page = 1)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
    ) {

        // TODO: isNotEmpty로 바꾸기
        if (uiState.bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 9.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "북마크",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF070707),
                        // 볼드체?
                    )
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
            ) {
                items(
                    items = uiState.bookmarks,
                    key = { bookmark -> bookmark.id }
                ) { bookmark ->
                    Card(
                        modifier = Modifier
                            .height(128.dp)
                            .clickable { onImageClick },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        // TODO: 가로 스크롤 북마크 리스트
                    }

                }

            }
        }

        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 9.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "최신 이미지",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF070707)
                )
            )
        }

        // TODO: 최신 이미지 리스트
        Surface(modifier = Modifier.fillMaxSize()) {
            if (photos.isEmpty()) {
                CircularProgressIndicator()
            } else {
                // TODO: 세로 무한 스크롤

            }
        }
    }
}
