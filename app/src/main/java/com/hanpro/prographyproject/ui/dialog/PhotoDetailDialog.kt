package com.hanpro.prographyproject.ui.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.data.source.remote.downloadMediaStore
import com.hanpro.prographyproject.data.source.remote.downloadPublicDCIM
import com.hanpro.prographyproject.ui.components.DetailTopBar
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel

/**
 * 사진의 상세 정보를 전체화면 다이얼로그로 표시하고 사용자가 이미지 다운로드와 북마크를 할 수 있도록 한다.
 *
 * 다이얼로그가 표시되는 동안 상태 표시줄 색상을 반투명 검정으로 변경하고, 닫힐 때 원래 색상으로 복구한다.
 * 저장 동작은 필요 시 런타임 저장 권한을 요청하며, 다운로드 성공 여부를 토스트로 알린다.
 *
 * @param photoId 표시할 사진의 고유 식별자.
 * @param onClose 다이얼로그를 닫을 때 호출되는 콜백.
 */
@Composable
fun PhotoDetailDialog(
    modifier: Modifier = Modifier,
    photoId: String = "",
    onClose: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (!it) {
            Toast.makeText(context, "저장 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(photoId) {
        viewModel.loadPhotoDetail(photoId)
    }

    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(Color.Black.copy(alpha = 0.7f), darkIcons = false)
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color(0x00000000),
                darkIcons = true
            )
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .testTag("dialog")
        ) {
            if (uiState.isLoading) {
                PrographyProgressIndicator()
            } else if (uiState.error != null) {
                Log.e("PhotoDetailScreen", "Error: ${uiState.error}")
            } else {
                uiState.photo?.let { photo ->
                    // 디테일 이미지
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card {
                            AsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                model = photo.urls.regular,
                                contentDescription = "",
                            )
                        }
                    }

                    DetailTopBar(
                        modifier = Modifier.align(Alignment.TopCenter),
                        userName = photo.user.username,
                        isBookmarked = uiState.isBookmarked,
                        onClose = onClose,
                        onDownloadClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                                != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                return@DetailTopBar
                            }
                            val fileName = "unsplash_${photo.id}.jpg"
                            val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                downloadMediaStore(context, photo.links.download, fileName)
                            } else {
                                downloadPublicDCIM(photo.links.download, fileName)
                            }
                            val msg = if (success) "이미지를 저장했습니다." else "이미지를 저장하지 못했습니다."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onBookmarkClick = {
                            if (!uiState.isBookmarked) viewModel.addBookmark(photo)
                            else viewModel.deleteBookmark(photo)
                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 10.dp)
                    ) {
                        val description = photo.description ?: ""
                        Text(
                            text = photo.tags?.first()?.title ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = photo.tags?.joinToString(" ") { "#${it.title}" } ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
