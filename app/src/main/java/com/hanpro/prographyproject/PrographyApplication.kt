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

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoaderProvider.get()
}