package com.hanpro.prographyproject.di

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.hanpro.prographyproject.BuildConfig
import com.hanpro.prographyproject.data.source.remote.UnsplashApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("apiClient")
    fun provideApiOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cache = Cache(
            File(context.cacheDir, "api_http_cache"),
            50L * 1024 * 1024
        )

        return OkHttpClient.Builder()
//            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(
                        "Authorization",
                        "Client-ID ${BuildConfig.UNSPLASH_KEY}"
                    )
                    .build()
                chain.proceed(request)
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=21600")
                    .build()
            }
            .cache(cache)
            .build()
    }

    /**
     * 이미지 캐시를 위한 OkHttpClient
     * - 이미지 요청에 API Key가 섞이지 않아 보안 문제 없음
     */
    @Provides
    @Singleton
    @Named("imageClient")
    fun provideImageOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        val cache = Cache(
            File(context.cacheDir, "image_http_cache"),
            50L * 1024 * 1024
        )

        return OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @Named("imageClient") imageClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(imageClient))
            }
            // 메모리 캐시 설정
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // 앱 가용 메모리의 25% (Coil 권장 크기)
                    .build()
            }
            // Coil 디스크 캐시
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(200L * 1024 * 1024) // 200 MB
                    .build()
            }
//            .logger(DebugLogger())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("apiClient") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApiService {
        return retrofit.create(UnsplashApiService::class.java)
    }
}