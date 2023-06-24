package android.qfh.libs.logs.room

import android.qfh.libs.logs.room.converter.DateConverter
import android.qfh.libs.logs.room.dao.CommonLogDao
import android.qfh.libs.logs.room.entry.CommonLog
import android.qfh.libs.logs.start.LibLogsInit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CommonLog::class], version = 1)
@TypeConverters(DateConverter::class)
internal abstract class LogsDatabase : RoomDatabase() {

    abstract fun getCommonDao(): CommonLogDao

    companion object {
        private const val NAME_DATABASE = "logs.db"
        val logsDatabase by lazy {
            Room.databaseBuilder(
                LibLogsInit.logContext,
                LogsDatabase::class.java,
                NAME_DATABASE
            ).build()
        }
    }
}