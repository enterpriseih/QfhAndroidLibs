package android.qfh.libs.logs.printer

import android.util.Log

internal class DebugLogPrinter private constructor(private val tag: String) : LogPrinter {

    override fun logD(msg: String?, data: String?) {
        Log.d(tag, msg ?: "")
    }

    override fun logI(msg: String?, data: String?) {
        Log.i(tag, msg ?: "")
    }

    override fun logW(msg: String?, data: String?) {
        Log.w(tag, msg ?: "")
    }

    override fun logE(msg: String?, data: String?) {
        Log.e(tag, msg ?: "")
    }

    class Factory(private val tag: String) : LogPrinter.Factory<DebugLogPrinter> {
        override fun create(): DebugLogPrinter {
            return DebugLogPrinter(tag)
        }
    }
}