@file:Suppress("OPT_IN_USAGE", "DEPRECATION")

package android.qfh.modules.base.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.qfh.modules.base.start.ModuleBaseInitializer
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

// 无网络
const val STATUS_NO_NET = 0

// 移动网络
const val STATUS_MOBILE = 1

// wifi 网络
const val STATUS_WIFI = 2

// 其他类型网络
const val STATUS_OTHER = 3

/**
 * 获取当前网络是否可用
 */
suspend fun getCurrentNetStatusIsAlive(): Boolean {
    return netStatusChangeFlow.first() > STATUS_NO_NET
}

/**
 * 注册网络状态变化监听
 */
fun getNetworkStatusChangeListener(): Flow<Int> {
    return netStatusChangeFlow
}

private val netStatusChangeFlow = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            logD("正在回调网络变化标志")
            val networkInfo =
                (intent?.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as? NetworkInfo)!!
            if (networkInfo.isConnected) {
                trySend(
                    when (networkInfo.type) {
                        ConnectivityManager.TYPE_WIFI -> STATUS_WIFI
                        ConnectivityManager.TYPE_MOBILE -> STATUS_MOBILE
                        else -> STATUS_OTHER
                    }
                )
            } else {
                trySend(STATUS_NO_NET)
            }
        }
    }
    ModuleBaseInitializer.appContext.registerReceiver(
        broadcastReceiver,
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    )
    logD("正在注册网络变化监听器")
    awaitClose {
        ModuleBaseInitializer.appContext.unregisterReceiver(broadcastReceiver)
        logD("正在注销网络变化监听器")
    }
}.debounce(1000).distinctUntilChanged()
    .shareIn(ModuleBaseInitializer.mainScope, SharingStarted.Lazily, 1)

private const val DEBUG = false
private const val TAG = "NetStateObserver"
private fun logD(msg: String) {
    if (DEBUG) {
        Log.d(TAG, msg)
    }
}
