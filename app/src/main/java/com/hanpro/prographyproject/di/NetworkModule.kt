package com.hanpro.prographyproject.di

import android.content.Context
import com.hanpro.prographyproject.common.utils.NetworkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideNetworkManager(@ApplicationContext context: Context) = NetworkManager(context)
}