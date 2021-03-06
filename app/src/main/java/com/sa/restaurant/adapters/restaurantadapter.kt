package com.sa.restaurant.adapters

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.plus.PlusShare
import com.paginate.recycler.LoadingListItemCreator
import com.sa.restaurant.R
import com.sa.restaurant.app.Favorites.FavoriteRestaurants
import com.sa.restaurant.app.MapsActivity.MapsFragment
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.RestaurantInfoFragment
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import com.squareup.picasso.Picasso


class restaurantadapter(var context: Context, var array: ArrayList<RestaurantData>, var myView: View) : RecyclerView.Adapter<restaurantadapter.Vholder>() {


    var sheetView: View? = null
    lateinit var mydb: Mydatabase
    var Username: String? = null
    var uid: Int? = null
    var list: ArrayList<String> = ArrayList()
    val mBottomSheetDialog = BottomSheetDialog(context)
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var geofencingClient: GeofencingClient
    var GEOFENCE_RADIUS_IN_METERS: Int = 1000
    lateinit var imgUrl: String
    lateinit var referencePhoto: String
    lateinit var imgUrlForFb: String

    init {
        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vholder {

        geofencingClient = LocationServices.getGeofencingClient(context)

        var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sheetView = layoutInflater.inflate(R.layout.custom_chooser, null)
        mBottomSheetDialog.setContentView(sheetView)

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


    fun getGeofence(latitude: Double, longitude: Double, placename: String): Geofence? {


        var geofence: Geofence = Geofence.Builder()
                .setRequestId(placename)
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        return geofence

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
        if (array[position].image == "NotAvailable") {
            referencePhoto = "NotAvailable"
            imgUrl = "https://vignette.wikia.nocookie.net/citrus/images/6/60/No_Image_Available.png/revision/latest?cb=20170129011325"
        } else {
            referencePhoto = array[position].image.toString()
            imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referencePhoto&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"

        }
        holder.textView.setOnLongClickListener(View.OnLongClickListener {
            mBottomSheetDialog.show()
            var referenceImg = array[position].image
            if (array[position].image == "NotAvailable") {
                imgUrlForFb = "https://vignette.wikia.nocookie.net/citrus/images/6/60/No_Image_Available.png/revision/latest?cb=20170129011325"
            } else {
                imgUrlForFb = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referenceImg&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"
            }
            return@OnLongClickListener true

        })

        holder.textView.setOnClickListener(View.OnClickListener {

            var restaurantInfoFragment: RestaurantInfoFragment = RestaurantInfoFragment()
            var bundle: Bundle = Bundle()
            var referenceImg = array[position].image
            bundle.putString("restroName", holder.textView.text.toString())
            bundle.putString("restroAddress", array[position].Address)
            if (array[position].image == "NotAvailable") {
                imgUrl = "https://vignette.wikia.nocookie.net/citrus/images/6/60/No_Image_Available.png/revision/latest?cb=20170129011325"
            } else {

                imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$referenceImg&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"

            }
            bundle.putString("restroImg", imgUrl)

            restaurantInfoFragment.arguments = bundle
            Fragmentutils.addFragmentwithBackStack(context, restaurantInfoFragment, RestaurantActivity.MyfragmentManager, R.id.content)

        })
        fb_Share.setOnClickListener(View.OnClickListener {


            var shareLinkContent: ShareLinkContent = ShareLinkContent.Builder()
                    .setQuote(holder.textView.text.toString())
                    .setContentDescription(holder.subtitle.text.toString())
                    .setContentUrl(Uri.parse(imgUrlForFb))
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, imgUrlForFb)
            sendIntent.type = "text/plain"


            startActivity(context, Intent.createChooser(sendIntent, "Select Where to Share"), null)

        })

        var result: List<FavoritesTable> = mydb.myDao().checkFavorites(array[position].Name!!, uid!!)
        Picasso.get().load(imgUrl).into(holder.img)
        Log.i("Result of db favs", result.toString())
        if (result.isNotEmpty()) {

            for (i in result.indices) {
                var favRestaurant = result[i].restaurantName
                var favRestaurantAddress = result[i].restaurantAddress
                if (holder.textView.text == favRestaurant && array[position].Address == favRestaurantAddress) {
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
                favoritesTable.restaurantAddress = array[position].Address
                favoritesTable.restaurantPhoto = array[position].image
                list.add(referencePhoto)
                mydb.myDao().addfav(favoritesTable)
                this.notifyDataSetChanged()

                var location = RestaurantPresenterImpl.hashMap[holder.textView.text.toString()]
                Log.i("LatLng", location!!.latitude.toString() + "  " + location.longitude)
                var mapsFragment: MapsFragment = MapsFragment()
                mapsFragment.callgeofence(context)


                Toastutils.showsSnackBar(context as Activity, "Added to fav")
            } else {


                var restrauntName = holder.textView.text.toString()
                var ID: Int = uid!!
                mydb.myDao().deletefromfav(restrauntName, ID)
                this.notifyDataSetChanged()
                var list: ArrayList<String> = ArrayList<String>()
                list.add(holder.textView.text.toString())
                LocationServices.GeofencingApi.removeGeofences(MapsFragment.gclient, list)
                var favoriteRestaurants: FavoriteRestaurants = FavoriteRestaurants()
                this.array.clear()
                favoriteRestaurants.reload(context, myView)
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

        var img: ImageView = itemView.findViewById(R.id.imageView)
        var subtitle: TextView = itemView.findViewById(R.id.price)


    }
}