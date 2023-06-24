package android.qfh.libs.logs.room.converter

import androidx.room.TypeConverter
import java.util.Date

internal class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}