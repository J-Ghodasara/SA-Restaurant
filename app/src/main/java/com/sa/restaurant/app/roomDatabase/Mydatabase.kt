package com.sa.restaurant.app.roomDatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Table::class,FavoritesTable::class),version = 1)
abstract class Mydatabase : RoomDatabase() {


    abstract fun myDao():MyDao

}