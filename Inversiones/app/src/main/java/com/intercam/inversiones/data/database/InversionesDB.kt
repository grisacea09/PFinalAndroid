package com.intercam.inversiones.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intercam.inversiones.data.database.dao.EnrolamientoDao
import com.intercam.inversiones.data.database.entities.EnrolamientoEntity

@Database(entities = [EnrolamientoEntity::class], version = 1)
abstract class InversionesDB: RoomDatabase() {

    abstract fun getRolledDao():EnrolamientoDao

}