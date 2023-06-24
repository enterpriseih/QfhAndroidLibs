package android.qfh.libs.logs.room.entry

import java.util.Date

open class BaseLog {

    var time: Date? = null

    var level: String? = null

    // 日志的所属 module，方便批量查询
    var module: String? = null

    // 日志的 tag ，方便区分输出源
    var tag: String? = null

    companion object {

        const val LEVEL_DEBUG = "debug"
        const val LEVEL_INFO = "info"
        const val LEVEL_WARN = "warn"
        const val LEVEL_ERROR = "error"

    }
}