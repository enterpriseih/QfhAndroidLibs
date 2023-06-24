package android.qfh.libs.logs.room.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common_log")
data class CommonLog(
    @PrimaryKey
    @ColumnInfo(name = "common_log_id")
    val commonLogId: String,
    var message: String?,
    var data: String?
) : BaseLog()