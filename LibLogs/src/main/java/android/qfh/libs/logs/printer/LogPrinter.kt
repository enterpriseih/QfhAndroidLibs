package android.qfh.libs.logs.printer

internal interface LogPrinter {
    fun logD(msg: String?, data:String?)
    fun logI(msg: String?, data:String?)
    fun logW(msg: String?, data:String?)
    fun logE(msg: String?, data:String?)
    interface Factory<P : LogPrinter> {
        fun create(): P
    }
}