package android.qfh.libs.logs.printer

import android.qfh.libs.logs.room.LogsDatabase
import android.qfh.libs.logs.room.entry.BaseLog
import android.qfh.libs.logs.room.entry.CommonLog
import android.qfh.libs.logs.start.LibLogsInit
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

internal class CommonLogPrinter(
    private val tag: String,
    private val module: String?
) : LogPrinter {

    private val commonLogDao = LogsDatabase.logsDatabase.getCommonDao()

    override fun logD(msg: String?, data: String?) {
        log(BaseLog.LEVEL_DEBUG, msg, data)
    }

    override fun logI(msg: String?, data: String?) {
        log(BaseLog.LEVEL_INFO, msg, data)
    }

    override fun logW(msg: String?, data: String?) {
        log(BaseLog.LEVEL_WARN, msg, data)
    }

    override fun logE(msg: String?, data: String?) {
        log(BaseLog.LEVEL_ERROR, msg, data)
    }

    private fun log(level: String, msg: String?, data: String?) {
        LibLogsInit.logScope.launch {
            commonLogDao.insert(
                CommonLog(UUID.randomUUID().toString(), msg, data).also {
                    it.time = Date()
                    it.level = level
                    it.module = module
                    it.tag = tag
                }
            )
        }
    }

    class Factory(
        private val tag: String,
        private val module: String?,
    ) : LogPrinter.Factory<CommonLogPrinter> {
        override fun create(): CommonLogPrinter {
            return CommonLogPrinter(tag, module)
        }
    }


}