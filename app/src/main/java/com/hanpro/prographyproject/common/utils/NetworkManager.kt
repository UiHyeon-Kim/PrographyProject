package com.hanpro.prographyproject.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableStateFlow(false)
    val networkState = _networkState.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            _networkState.value = false
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            updateNetworkState()
        }
    }

    init {
        registerNetworkCallback()
        updateNetworkState()
    }

    /**
     * 네트워크 상태를 모니터링하기 위한 ConnectivityManager의 네트워크 콜백을 등록
     */
    private fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * 연결 가능 여부를 확인해 네트워크 상태를 업데이트
     */
    private fun updateNetworkState() {
        _networkState.value = checkNetworkConnection()
    }

    /**
     * 네트워크 사용 가능 여부 반환터
     */
    fun checkNetworkConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        val isConnected =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return isConnected
    }
}