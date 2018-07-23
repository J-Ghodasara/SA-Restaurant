package com.sa.restaurant.app.roomDatabase

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "Mytable")
 class Table{

   @PrimaryKey(autoGenerate = true)
    var id:Int?= null

    @ColumnInfo(name="Name")
    var name:String?=null

    @ColumnInfo(name="Email")
    var email:String?=null

    @ColumnInfo(name="Password")
    var password:String?=null

    @ColumnInfo(name="MobileNumber")
    var mobilenumber:String?=null




}