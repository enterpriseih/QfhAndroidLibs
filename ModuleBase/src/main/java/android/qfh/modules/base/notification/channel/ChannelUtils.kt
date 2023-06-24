package android.qfh.modules.base.notification.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build


object ChannelUtils {
    // 通用的前台服务通知
    const val ID_CHANNEL_COMMON_FOREGROUND_SERVICE = "common_foreground_service"
    private const val NAME_CHANNEL_COMMON_FOREGROUND_SERVICE = "common_foreground_service"

    fun checkCommonForegroundServiceNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    ID_CHANNEL_COMMON_FOREGROUND_SERVICE,
                    NAME_CHANNEL_COMMON_FOREGROUND_SERVICE,
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }
}