package android.qfh.modules.base.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.qfh.libs.utils.ImageUtil
import android.qfh.libs.utils.ScreenUtils
import android.qfh.modules.base.notification.NotificationUtils
import android.qfh.modules.base.notification.channel.ChannelUtils
import android.qfh.modules.base.systemManager.MediaProjectionPermissionUtil
import androidx.core.app.NotificationCompat

/**
 * 用于录屏的前台服务，建议配合 [MediaProjectionPermissionUtil] 使用
 * 启动该服务后，需要调用 [setMediaProjection] 传入用户权限请求结果（[MediaProjectionPermissionUtil.bindMediaProjectionService] 方法已处理），而后才能
 * 使用 [startCaptureScreen] 方法进行截屏
 */
class MediaProjectionService : Service() {

    private val binder by lazy {
        LocalBinder()
    }
    private val mHandler = Handler(Looper.getMainLooper())

    internal inner class LocalBinder : Binder() {
        fun getService(): MediaProjectionService {
            return this@MediaProjectionService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        ChannelUtils.checkCommonForegroundServiceNotificationChannel(this)
        startForeground(
            NotificationUtils.ID_NOTIFICATION_MEDIA_PROJECTION_SERVICE,
            NotificationCompat.Builder(this, ChannelUtils.ID_CHANNEL_COMMON_FOREGROUND_SERVICE)
                .build()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }


    private var mMediaProjection: MediaProjection? = null

    internal fun mediaProjectionIsAlive() = mMediaProjection != null
    internal fun setMediaProjection(mediaProjection: MediaProjection) {
        mMediaProjection = mediaProjection
    }

    @SuppressLint("WrongConstant")
    fun startCaptureScreen(
        onGetBitmap: (Bitmap) -> Unit
    ) {
        val mediaProjection = mMediaProjection ?: return
        val imageReader = ImageReader.newInstance(
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight(),
            PixelFormat.RGBA_8888,
            1
        )
        val virtualDisplay = mediaProjection.createVirtualDisplay(
            TAG,
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight(),
            ScreenUtils.getScreenDensityDpi(),
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null, null
        )

        mHandler.postDelayed({
            val image = imageReader.acquireLatestImage()
            virtualDisplay.release()
            if (image == null) {
                MediaProjectionPermissionUtil.logW("未捕获到任何图像")
            } else {
                onGetBitmap(ImageUtil.imageToBitmap(image))
            }
        }, 1000)
    }

    companion object {
        private const val TAG = "MediaProjectionService"
    }
}