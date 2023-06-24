package android.qfh.libs.fileDownloader

import android.app.Application
import android.qfh.libs.fileDownloader.FileDownloaderInit.init
import kotlinx.coroutines.MainScope
import retrofit2.Retrofit

/**
 * 下载组件的初始化类
 * @suppress 使用下载组件前，最少需要调用一次 [init] 方法初始化相关参数
 */
object FileDownloaderInit {

    internal lateinit var retrofit: Retrofit

    internal lateinit var appContext: Application

    internal val downloadScope by lazy {
        MainScope()
    }
    fun init(retrofit: Retrofit, application: Application) {
        if (this::retrofit.isInitialized) {
            logW("FileDownloaderInit.init 不应该被重复调用")
            return
        }
        this.retrofit = retrofit
        this.appContext = application
    }
}