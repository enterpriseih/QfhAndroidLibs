package android.qfh.libs.logs.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

internal interface BaseDao<E> {

    @Insert
    suspend fun insert(vararg entry: E)

    @Delete
    suspend fun delete(vararg entry: E)

    @Update
    suspend fun update(vararg entry: E)

}