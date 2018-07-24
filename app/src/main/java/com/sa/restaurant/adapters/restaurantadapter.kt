package com.sa.restaurant.adapters

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.utils.Toastutils
import com.squareup.picasso.Picasso
import retrofit2.http.Url

class restaurantadapter(var context: Context, var array: ArrayList<RestaurantData>) : RecyclerView.Adapter<restaurantadapter.Vholder>() {

    lateinit var mydb: Mydatabase
    var Username: String?=null
    var uid:Int?=null
    var list:ArrayList<String> = ArrayList()


    init {
        mydb= Room.databaseBuilder(context, Mydatabase::class.java,"Database").allowMainThreadQueries().build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vholder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.restaurants_list, parent, false)
        Log.i("oncreateviewholder", "in")
        var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        Username = sharedpref.getString("username", null)
       uid=  mydb.myDao().getUserId(Username!!)
        return Vholder(v)
    }


    override fun getItemCount(): Int {
        return array.size
    }


    override fun onBindViewHolder(holder: Vholder, position: Int) {
        Log.i("onbindviewholder", "in")
        holder.textView.text = array[position].Name
        holder.subtitle.text=array[position].Address
        var referencePhoto=array[position].image

        val imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"
        var result:List<FavoritesTable> =  mydb.myDao().checkFavorites(array[position].Name!!,uid!!)
        Picasso.get().load(imgUrl).into(holder.img)

        if(result.isNotEmpty()){

           for(i in result.indices){
              var favRestaurant= result[i].restaurantName
               if(holder.textView.text==favRestaurant){
                   holder.add_to_fav.isChecked=true
               }
           }


        }

        holder.add_to_fav.setOnClickListener(View.OnClickListener {

            if (holder.add_to_fav.isChecked) {

//                var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
//                var Username: String = sharedpref.getString("username", null)
//              var uid:Int=  mydb.myDao().getUserId(Username)
//                var result:List<FavoritesTable> =  mydb.myDao().checkFavorites(array[position],uid)
//                if(result.isNotEmpty()){
//
//                    Toastutils.showsSnackBar(context as Activity,"Already added as fav")
//
//
//                }else {
//
//
//                }
               var restroName:String= holder.textView.text.toString()
                var favoritesTable:FavoritesTable= FavoritesTable()
                favoritesTable.restaurantName=restroName
                favoritesTable.uid=uid
                favoritesTable.restaurantAddress=holder.subtitle.text.toString()
                favoritesTable.restaurantPhoto=array[position].image
                list.add(referencePhoto!!)
                mydb.myDao().addfav(favoritesTable)
                this.notifyDataSetChanged()
                Toastutils.showsSnackBar(context as Activity, "Added to fav")
            } else {


               var restrauntName = holder.textView.text.toString()
                var ID:Int = uid!!
                mydb.myDao().deletefromfav(restrauntName,ID)
                this.notifyDataSetChanged()
                Toastutils.showsSnackBar(context as Activity, "Removed from fav")
            }


        })


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class Vholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textView: TextView = itemView.findViewById(R.id.restaurant_names)
        var add_to_fav: ToggleButton = itemView.findViewById(R.id.add_to_fav)
        var img:ImageView = itemView.findViewById(R.id.imageView)
        var subtitle: TextView = itemView.findViewById(R.id.price)

    }
}