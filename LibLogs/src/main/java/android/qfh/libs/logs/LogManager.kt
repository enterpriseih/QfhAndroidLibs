package android.qfh.libs.logs

import android.qfh.libs.logs.LogManager.Builder
import android.qfh.libs.logs.printer.CommonLogPrinter
import android.qfh.libs.logs.printer.DebugLogPrinter
import android.qfh.libs.logs.printer.LogPrinter
import android.qfh.libs.logs.query.CommonQuery
import android.qfh.libs.logs.room.entry.BaseLog

/**
 * 日志输出管理
 * 通过 [Builder] 进行参数构建
 */
class LogManager private constructor(
    private val tag: String,
    private val level: String?,
    private val module: String?,
    private val logPrinterUse: IntArray,
) {

    private val logPrinterDelegate = mutableListOf<LogPrinter>()

    init {
        logPrinterUse.forEach {
            logPrinterDelegate.add(
                when (it) {
                    USE_DEBUG -> DebugLogPrinter.Factory(tag).create()
                    USE_COMMON -> CommonLogPrinter.Factory(tag, module).create()
                    else -> throw RuntimeException("error logPrinterUse for $logPrinterUse")
                }
            )
        }
    }

    fun log(msg: String, data: String? = null) {
        logPrinterDelegate.forEach {
            when (level) {
                BaseLog.LEVEL_DEBUG -> it.logD(msg, data)
                BaseLog.LEVEL_WARN -> it.logW(msg, data)
                BaseLog.LEVEL_ERROR -> it.logE(msg, data)
                else -> it.logI(msg, data)
            }
        }
    }

    fun query() = CommonQuery(tag, level, module)

    fun newBuilder(
        newTag: String = tag,
        newLogPrinterUse: IntArray = logPrinterUse,
        newLevel: String? = level,
        newModule: String? = module
    ) = Builder(
        newTag, newLogPrinterUse
    ).apply {
        setLevel(newLevel)
        setModule(newModule)
    }

    companion object {
        const val USE_DEBUG = 1
        const val USE_COMMON = 2
    }

    /**
     * @param tag 日志输出的 tag
     * @param logPrinterUse 日志输出的处理用途，目前支持两种（可以同时设置多种）：
     * 1、[USE_DEBUG] debug 的日志输出，转发 logCat 实时日志
     * 2、[USE_COMMON] 通用的日志信息记录，会将日志信息持久化到数据库
     *
     */
    class Builder(
        var tag: String,
        var logPrinterUse: IntArray
    ) {

        private var level: String? = null
        private var module: String? = null

        /**
         * 设置日志输出 level，默认为 [BaseLog.LEVEL_INFO]
         */
        fun setLevel(level: String?): Builder {
            this.level = level
            return this
        }

        /**
         * 设置日志输出 type，该参数为自定义字符串，默认为空
         */
        fun setModule(module: String?): Builder {
            this.module = module
            return this
        }

        fun build(): LogManager {
            return LogManager(
                tag,
                level,
                module,
                logPrinterUse,
            )
        }
    }

}