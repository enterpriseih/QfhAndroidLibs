package android.qfh.libs.logs.room.dao

import android.qfh.libs.logs.room.entry.CommonLog
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CommonLogDao : BaseDao<CommonLog> {

    @Query("select * from common_log where (tag=:tagParam or tag is null) and (level=:levelParam or level is null) and (module=:moduleParam or module is null)")
    fun query(tagParam: String?, levelParam: String?, moduleParam: String?): Flow<List<CommonLog>>
}