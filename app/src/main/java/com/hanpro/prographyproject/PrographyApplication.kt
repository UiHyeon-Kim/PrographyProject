package com.hanpro.prographyproject

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class PrographyApplication : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader>

    /**
 * Coil의 SingletonImageLoader에 사용할 ImageLoader 인스턴스를 제공한다.
 *
 * @param context Coil 플랫폼 컨텍스트
 * @return 주입된 Provider에서 제공한 `ImageLoader` 인스턴스
 */
override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoaderProvider.get()
}