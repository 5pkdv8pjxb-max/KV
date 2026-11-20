package com.example.kursovik.data.di

import com.example.kursovik.data.remote.ConnectivityObserver
import com.example.kursovik.data.remote.NetworkMonitor
import com.example.kursovik.data.local.InitialRatesSource
import com.example.kursovik.data.local.AssetInitialRatesSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OfflineModule {
    @Binds abstract fun bindConnectivity(impl: NetworkMonitor): ConnectivityObserver
    @Binds abstract fun bindInitialRates(impl: AssetInitialRatesSource): InitialRatesSource
}
