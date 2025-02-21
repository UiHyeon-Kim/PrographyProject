package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

suspend fun downloadImage(
    imageUrl: String,
    imageFile: File,
    client: OkHttpClient = OkHttpClient()
): Boolean = withContext(Dispatchers.IO) {
    try {
        imageFile.parentFile?.let { if (!it.exists()) it.mkdirs() }
        val request = okhttp3.Request.Builder()
            .url(imageUrl)
            .header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_KEY}")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("예상치 못한 코드입니다. $response")
            response.body?.byteStream()?.use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    outputStream.flush()
                }
            } ?: throw IOException("응답 코드가 비어있습니다.")
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}