package com.sa.restaurant.app.roomDatabase

import android.arch.persistence.room.*

@Dao
interface MyDao{

    @Insert
    fun adduser(table: Table)

    @Delete
    fun deleteuser(table: Table)

    @Update
    fun updateuser(table: Table)

     @Query("select * from Mytable")
    fun getusers():List<Table>

    @Update
    fun update(table: Table)

    @Query("select * from Mytable Where name=:username or Email=:email ")
    fun checkuser(username:String,email:String):List<Table>

    @Query("select * from Mytable Where Password=:password and name=:username or Email=:username")
    fun userLogin(username:String,password:String):List<Table>

    @Query("select * from FavoritesTable Where RestaurantName=:restaurantName And userid=:uid")
    fun checkFavorites(restaurantName:String,uid:Int):List<FavoritesTable>

    @Query("select id from Mytable Where name=:username or Email=:username")
    fun getUserId(username:String):Int

    @Query("select id from Mytable Where id=:userid")
    fun getUserdetails(userid:Int):List<Table>


    @Insert
    fun addfav(table: FavoritesTable)

    @Query("Delete from FavoritesTable where RestaurantName=:restaurantName And userid=:uid and RestaurantAddress=:address")
    fun deletefromfav(restaurantName:String,uid:Int,address:String)

    @Query("select * from FavoritesTable Where userid=:userid")
    fun getFavorites(userid:Int):List<FavoritesTable>

    @Insert
    fun addWeatherInfo(table: WeatherInfoTable)

    @Query("select * from WeatherInfoTable Where userID=:UserId ")
    fun checkUserId(UserId:Int):List<WeatherInfoTable>

    @Update
    fun updateWeatherInfo(table: WeatherInfoTable)

    @Query("select * from Mytable Where id=:userid")
    fun getUserInfo(userid:Int):List<Table>





}