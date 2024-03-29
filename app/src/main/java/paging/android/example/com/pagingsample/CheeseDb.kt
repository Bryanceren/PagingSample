/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paging.android.example.com.pagingsample

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.*
import android.content.Context

/**
 * Singleton database object. Note that for a real app, you should probably use a Dependency
 * Injection framework or Service Locator to create the singleton database.
 */
@Database(entities = arrayOf(Cheese::class), version = 1)
abstract class CheeseDb : RoomDatabase() {
    abstract fun cheeseDao(): CheeseDao

    companion object {
        private var instance: CheeseDb? = null
        @Synchronized
        fun get(context: Context): CheeseDb {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                        CheeseDb::class.java, "CheeseDatabase")
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                fillInDb(context.applicationContext)
                            }
                        }).build()
            }
            return instance!!
        }

        /**
         * fill database with list of cheeses
         */
        private fun fillInDb(context: Context) {
            // inserts in Room are executed on the current thread, so we insert in the background
            ioThread {
                get(context).cheeseDao().insert(
                        CHEESE_DATA.map { Cheese(id = 0, name = it) })
            }
        }
    }
}


private val CHEESE_DATA = arrayListOf(
        "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam");
