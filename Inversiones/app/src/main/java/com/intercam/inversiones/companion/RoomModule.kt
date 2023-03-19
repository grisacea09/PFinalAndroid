package com.intercam.inversiones.companion

import android.content.Context
import androidx.room.Room
import com.intercam.inversiones.data.database.InversionesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {


    companion object {
        const val INV_DATABASE_NAME = "inversion_database"
    }

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(context,InversionesDB::class.java,
        INV_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideEnrolamientoDao(db:InversionesDB) = db.getRolledDao()
}