package com.intercam.inversiones.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rolled_table")
data class EnrolamientoEntity(
                            @PrimaryKey(autoGenerate = true)
                            @ColumnInfo(name="id")val id: Int = 0,
                            @ColumnInfo(name="enrolado")val enrolado:String,
                            @ColumnInfo(name="username")val username:String
                            //@ColumnInfo(name="cuenta")val idUsr:String
                            )
