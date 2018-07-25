package com.sa.restaurant.app.roomDatabase

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "WeatherInfoTable" ,foreignKeys = arrayOf(ForeignKey(entity = Table::class,parentColumns = arrayOf("id"),childColumns = arrayOf("userID"),onDelete = ForeignKey.CASCADE)))
class WeatherInfoTable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="ID")
    var ID:Int?=null

    @ColumnInfo(name="userID")
    var uid:Int?=null

    @ColumnInfo(name="place")
    var place:String?=null

    @ColumnInfo(name="city")
    var city:String?=null

    @ColumnInfo(name="temperature")
    var temperature:String?=null

    @ColumnInfo(name="humidity")
    var humidity:String?=null

}