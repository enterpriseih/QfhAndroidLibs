package android.qfh.modules.base.systemManager

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 录屏服务管理工具类
 *
 * 想要启动录屏服务时调用 [bindMediaProjectionService] 方法，该方法内部处理了用户权限的请求和缓存，
 * 前台录屏服务的启动和绑定，且自动监听了 activity 的生命周期用于自动解绑服务，调用段无需做任何处理；
 * 调用段只需要关注 [onServiceConnect] 的回调即可
 *
 * @param activity 用于权限请求的 activity
 * @param onServiceConnect 当绑定到前台录屏服务时的回调，能拿到 service 实例
 * @param onServiceDisconnected 当和前台录屏服务意外断开时的回调
 *
 * @suppress 前台服务会一直再在后台运行，如果想关闭服务，请手动调用 [Context.stopService] 方法关闭服务
 */
class MediaProjectionPermissionUtil(
    private val activity: FragmentActivity,
    private val onServiceConnect: (android.qfh.modules.base.service.MediaProjectionService) -> Unit,
    private val onServiceDisconnected: () -> Unit
) {
    private val mMediaProjectionManager =
        activity.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private val mRequestUserPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val mediaProjection =
                    mMediaProjectionManager.getMediaProjection(it.resultCode, it.data!!)
                mService!!.run {
                    setMediaProjection(mediaProjection)
                    onServiceConnect(this)
                }
            } else {
                logD("请求截屏被拒绝")
            }
        }
    private var mService: android.qfh.modules.base.service.MediaProjectionService? = null

    fun bindMediaProjectionService() {
        val intent = Intent(activity, android.qfh.modules.base.service.MediaProjectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent)
        } else {
            activity.startService(intent)
        }
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                val mediaProjectionService =
                    (service as android.qfh.modules.base.service.MediaProjectionService.LocalBinder).getService()
                mService = mediaProjectionService
                if (!mediaProjectionService.mediaProjectionIsAlive()) {
                    mRequestUserPermissionLauncher.launch(mMediaProjectionManager.createScreenCaptureIntent())
                } else {
                    onServiceConnect(mediaProjectionService)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                onServiceDisconnected()
            }

        }
        activity.bindService(
            Intent(activity, android.qfh.modules.base.service.MediaProjectionService::class.java),
            connection,
            BIND_AUTO_CREATE
        )
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    activity.unbindService(connection)
                }
            }
        })
    }

    companion object {
        private const val TAG = "MediaProjection"

        internal fun logD(msg: String) {
            Log.w(TAG, msg)
        }

        internal fun logW(msg: String) {
            Log.w(TAG, msg)
        }

    }

}