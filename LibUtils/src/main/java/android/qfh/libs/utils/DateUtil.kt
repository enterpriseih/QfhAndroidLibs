package android.qfh.libs.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间相关
 */
object DateUtil {
    /**
     * 获取指定的时间格式化器
     */
    fun getDateFormat(
        formatType: String = FORMAT_DEFAULT,
        local: Locale = Locale.getDefault()
    ): SimpleDateFormat {
        return SimpleDateFormat(formatType, local)
    }

    private const val FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss"
}