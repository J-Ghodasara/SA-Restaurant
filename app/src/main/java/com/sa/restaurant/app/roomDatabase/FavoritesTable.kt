package com.sa.restaurant.app.roomDatabase

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "FavoritesTable" ,foreignKeys = arrayOf(ForeignKey(entity = Table::class,parentColumns = arrayOf("id"),childColumns = arrayOf("userid"),onDelete = ForeignKey.CASCADE)))
class FavoritesTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="ID")
    var ID:Int?=null

    @ColumnInfo(name="userid")
    var uid:Int?=null

    @ColumnInfo(name="RestaurantName")
    var restaurantName:String?=null

    @ColumnInfo(name="RestaurantAddress")
    var restaurantAddress:String?=null

    @ColumnInfo(name="RestaurantPhoto")
    var restaurantPhoto:String?=null

}
