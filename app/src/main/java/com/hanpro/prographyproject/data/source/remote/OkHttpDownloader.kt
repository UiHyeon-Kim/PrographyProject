package com.hanpro.prographyproject.data.source.remote

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import com.hanpro.prographyproject.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okio.IOException
import java.io.File
import java.io.FileOutputStream

// Android 9 ↓
suspend fun downloadPublicDCIM(
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
// Android 10 ↑
suspend fun downloadMediaStore(
    context: Context,
    imageUrl: String,
    fileName: String,
    client: OkHttpClient = OkHttpClient()
): Boolean = withContext(Dispatchers.IO) {
    try {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext false
        val request = okhttp3.Request.Builder()
            .url(imageUrl)
            .header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_KEY}")
            .build()

        resolver.openOutputStream(uri)?.use { output ->
            client.newCall(request)
                .execute().use { response ->
                    if (!response.isSuccessful) throw IOException("response.code")
                    response.body!!.byteStream().use { input ->
                        input.copyTo(output)
                        output.flush()
                    }
                }
        } ?: throw IOException("output error")

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
