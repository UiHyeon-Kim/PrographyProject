package com.hanpro.prographyproject.data.source.remote

import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import com.hanpro.prographyproject.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// TODO: 삭제 다른 기능으로 변경
suspend fun downloadImage(
    imageUrl: String,
    fileName: String,
    client: OkHttpClient = OkHttpClient()
): Boolean = withContext(Dispatchers.IO) {
    try {
        val dcimDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!dcimDir.exists()) dcimDir.mkdirs()
        val imageFile = File(dcimDir, fileName)
        val request = okhttp3.Request.Builder()
            .url(imageUrl)
            .header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_KEY}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext false
            response.body?.byteStream()?.use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    outputStream.flush()
                }
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
