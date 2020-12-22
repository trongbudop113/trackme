package com.example.trackme.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trackme.database.modelDAO.AllTrackDAO
import com.example.trackme.model.Tracks


@Database(
    entities = [Tracks::class],
    version = 1,
    exportSchema = false
)

abstract class ProjectDatabase : RoomDatabase() {

    abstract fun allTrackDao() : AllTrackDAO

    companion object {
        val DATABASE_NAME = "database.db"
        @Volatile
        private var instance: ProjectDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context): ProjectDatabase {
            if (instance == null) {
                synchronized(ProjectDatabase::class.java)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext, ProjectDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
            }

            return instance!!
        }
    }


}