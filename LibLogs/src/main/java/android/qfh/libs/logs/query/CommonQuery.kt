package android.qfh.libs.logs.query

import android.qfh.libs.logs.room.LogsDatabase
import android.qfh.libs.logs.room.entry.CommonLog
import android.qfh.libs.logs.start.LibLogsInit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CommonQuery(private val tag: String, private val level: String?, private val module: String?) {

    private val commonLogDao = LogsDatabase.logsDatabase.getCommonDao()

    fun updateCommonLog(vararg commonLog: CommonLog) {
        LibLogsInit.logScope.launch {
            commonLogDao.update(*commonLog)
        }
    }

    fun deleteCommonLog(vararg commonLog: CommonLog) {
        LibLogsInit.logScope.launch {
            commonLogDao.delete(*commonLog)
        }
    }

    /**
     * 如果对应字段设为 null，则表示全查询
     */
    fun queryCommonLog(
        tag: String? = this.tag,
        level: String? = this.level,
        module: String? = this.module
    ): Flow<List<CommonLog>> {
        return commonLogDao.query(tag,level,module)
    }
}