package com.sa.restaurant.adapters

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat.startActivity
import android.widget.LinearLayout
import com.facebook.share.R.id.image
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.plus.PlusShare
import com.sa.restaurant.MainActivity
import java.net.URL


class restaurantadapter(var context: Context, var array: ArrayList<RestaurantData>) : RecyclerView.Adapter<restaurantadapter.Vholder>() {

    var sheetView: View? = null
    lateinit var mydb: Mydatabase
    var Username: String? = null
    var uid: Int? = null
    var list: ArrayList<String> = ArrayList()
    val mBottomSheetDialog = BottomSheetDialog(context)
    lateinit var mGoogleSignInClient: GoogleSignInClient

    init {
        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vholder {


        var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sheetView = layoutInflater.inflate(R.layout.custom_chooser, null)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.restaurants_list, parent, false)
        Log.i("oncreateviewholder", "in")
        var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        Username = sharedpref.getString("username", null)
        uid = mydb.myDao().getUserId(Username!!)

        return Vholder(v)
    }


    override fun getItemCount(): Int {
        return array.size
    }


    override fun onBindViewHolder(holder: Vholder, position: Int) {

        var fb_Share: LinearLayout = sheetView!!.findViewById(R.id.share_on_fb)
        var google_Share: LinearLayout = sheetView!!.findViewById(R.id.share_on_google)
        var other_Share: LinearLayout = sheetView!!.findViewById(R.id.share_to_other)

        Log.i("onbindviewholder", "in")
        holder.textView.text = array[position].Name
        holder.subtitle.text = array[position].Address
        var referencePhoto = array[position].image
        val imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"

        fb_Share.setOnClickListener(View.OnClickListener {


            var shareLinkContent: ShareLinkContent = ShareLinkContent.Builder()
                    .setContentTitle(holder.textView.text.toString())
                    .setContentDescription(holder.subtitle.text.toString())

                    .setContentUrl(Uri.parse("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"))
                    .build()
            ShareDialog.show(context as Activity, shareLinkContent)
        })

        google_Share.setOnClickListener(View.OnClickListener {
            var shareIntent: Intent = PlusShare.Builder(context).setType("text/plain")
                    .setText(holder.textView.text.toString())
                    .setContentUrl(Uri.parse("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"))
                    .intent
            startActivityForResult(context as Activity, shareIntent, 1, null)
        })

        other_Share.setOnClickListener(View.OnClickListener {
            Toastutils.showToast(context, "Other share")

            var sendIntent: Intent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, holder.textView.text.toString())
            sendIntent.type = "text/plain"
//            sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"))
//            sendIntent.type = "image/jpg"

            startActivity(context, Intent.createChooser(sendIntent, "Select Where to Share"), null)

        })

        var result: List<FavoritesTable> = mydb.myDao().checkFavorites(array[position].Name!!, uid!!)
        Picasso.get().load(imgUrl).into(holder.img)
        Log.i("Result of db favs", result.toString())
        if (result.isNotEmpty()) {

            for (i in result.indices) {
                var favRestaurant = result[i].restaurantName
                if (holder.textView.text == favRestaurant) {
                    holder.add_to_fav.isChecked = true
                }
            }


        } else {
            holder.add_to_fav.isChecked = false
        }

        holder.add_to_fav.setOnClickListener(View.OnClickListener {

            if (holder.add_to_fav.isChecked) {


                var restroName: String = holder.textView.text.toString()
                var favoritesTable: FavoritesTable = FavoritesTable()
                favoritesTable.restaurantName = restroName
                favoritesTable.uid = uid
                favoritesTable.restaurantAddress = holder.subtitle.text.toString()
                favoritesTable.restaurantPhoto = array[position].image
                list.add(referencePhoto!!)
                mydb.myDao().addfav(favoritesTable)
                this.notifyDataSetChanged()
                Toastutils.showsSnackBar(context as Activity, "Added to fav")
            } else {


                var restrauntName = holder.textView.text.toString()
                var ID: Int = uid!!
                mydb.myDao().deletefromfav(restrauntName, ID)
                this.notifyDataSetChanged()
                Toastutils.showsSnackBar(context as Activity, "Removed from fav")
            }


        })


    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class Vholder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        override fun onLongClick(v: View?): Boolean {

            mBottomSheetDialog.setContentView(sheetView)
            mBottomSheetDialog.show()
            return true
        }

        init {
            itemView.setOnLongClickListener(this)

        }

        var textView: TextView = itemView.findViewById(R.id.restaurant_names)
        var add_to_fav: ToggleButton = itemView.findViewById(R.id.add_to_fav)

        var img: ImageView = itemView.findViewById(R.id.imageView)
        var subtitle: TextView = itemView.findViewById(R.id.price)


    }
}