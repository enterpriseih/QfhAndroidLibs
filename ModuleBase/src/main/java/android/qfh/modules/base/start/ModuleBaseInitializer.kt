package android.qfh.modules.base.start

import android.app.Application
import android.content.Context
import android.qfh.libs.fileDownloader.FileDownloaderInit
import android.qfh.libs.logs.start.LibLogsInit
import android.qfh.modules.base.network.baseOkHttpClient
import androidx.startup.Initializer
import kotlinx.coroutines.MainScope
import retrofit2.Retrofit

class ModuleBaseInitializer : Initializer<ModuleBaseInitializer> {
    override fun create(context: Context): ModuleBaseInitializer {
        init(context)
        return ModuleBaseInitializer()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private fun init(context: Context) {

        appContext = context.applicationContext

        // 文件下载器初始化
        FileDownloaderInit.init(
            Retrofit.Builder()
                .baseUrl("https://127.0.0.1")
                .client(baseOkHttpClient)
                .build(),
            context.applicationContext as Application
        )
        // 日志库初始化
        LibLogsInit.init(context)
    }

    companion object {
        internal lateinit var appContext: Context
        internal val mainScope by lazy {
            MainScope()
        }
    }
}