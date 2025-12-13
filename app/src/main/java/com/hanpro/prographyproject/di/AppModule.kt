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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Unsplash API 호출용으로 구성된 OkHttpClient를 생성합니다.
     *
     * 생성된 클라이언트는 요청에 `Authorization: Client-ID <키>` 헤더를 추가하고,
     * 네트워크 응답에 `Cache-Control: public, max-age=21600`(6시간) 캐시 정책을 적용하며
     * 애플리케이션 캐시 디렉터리 내에 50MB의 HTTP 캐시를 사용하도록 설정됩니다.
     *
     * @param context HTTP 및 이미지 디스크 캐시를 생성하기 위해 사용할 Application Context.
     * @return 구성된 OkHttpClient 인스턴스.
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cacheDirectory = File(context.cacheDir, "http_cache")
        val cacheSize = 50L * 1024 * 1024 // 50 MB
        val cache = Cache(cacheDirectory, cacheSize)

        return OkHttpClient.Builder()
//            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Client-ID ${BuildConfig.UNSPLASH_KEY}")
                    .build()
                chain.proceed(request)
            }
            // 네트워크 응답에 캐시 헤더 추가
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=21600") // 6시간
                    .removeHeader("Pragma")
                    .removeHeader("Expires")
                    .build()
            }
            .cache(cache)
            .build()
    }

    /**
     * 이미지를 로드하고 캐싱하도록 구성된 Coil ImageLoader를 생성해 반환한다.
     *
     * 네트워크 요청에 전달된 OkHttpClient를 사용하며, 메모리 캐시는 앱 가용 메모리의 25%로 제한하고 디스크 캐시는 context.cacheDir/image_cache 경로에 최대 200MB로 설정한다.
     *
     * @return 구성된 ImageLoader 인스턴스.
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
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
    fun provideRetrofit(client: OkHttpClient): Retrofit {
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