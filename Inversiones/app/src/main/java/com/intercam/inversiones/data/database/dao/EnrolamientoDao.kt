package com.intercam.inversiones.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intercam.inversiones.data.database.model.EnrolamientoModel


@Dao
interface EnrolamientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertEnrolamiento(rolled: EnrolamientoModel)
    @Query("SELECT * from rolled_table")
    suspend fun getEnrolamiento(): EnrolamientoModel

}