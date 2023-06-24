package android.qfh.libs.logs.start

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.MainScope

object LibLogsInit {

    fun init(context: Context) {
        logContext = context.applicationContext
    }

    @SuppressLint("StaticFieldLeak")
    internal lateinit var logContext: Context
    internal val logScope by lazy {
        MainScope()
    }
}