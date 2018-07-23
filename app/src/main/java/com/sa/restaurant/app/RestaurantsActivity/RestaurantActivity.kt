package com.sa.restaurant.app.RestaurantsActivity

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.sa.restaurant.R
import com.sa.restaurant.adapters.ViewPagerAdapter
import com.sa.restaurant.adapters.restaurantadapter
import com.sa.restaurant.app.Favorites.FavoriteRestaurants

import com.sa.restaurant.app.RestaurantsActivity.model.POJO
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.app.RestaurantsActivity.view.RestaurantView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.utils.Fragmentutils
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.app_bar_restaurant.*
import kotlinx.android.synthetic.main.content_restaurant.*
import kotlinx.android.synthetic.main.restaurants_list.*


class RestaurantActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RestaurantView {


    private var dotscount: Int = 0
    private var dots: Array<ImageView?>? = null
    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var loc: Location
    lateinit var pojo: POJO
    private lateinit var mMap: GoogleMap
    lateinit var url: String
    var list: ArrayList<RestaurantData> = ArrayList()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var adapter: restaurantadapter
    var RestaurantsList: ArrayList<String> = ArrayList()
    var isfav:Boolean = false
    lateinit var mydb: Mydatabase
    var favRestros:FavoriteRestaurants= FavoriteRestaurants()
    companion object {
        var count: Int = 0
        lateinit var locationreq: LocationRequest
        lateinit var locationcallback: LocationCallback
        var googleClient: GoogleApiClient? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        setSupportActionBar(toolbar)



        var RestaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
        googleClient = RestaurantPresenter.createClient(this)
        googleClient!!.connect()

        mydb= Room.databaseBuilder(this, Mydatabase::class.java,"Database").allowMainThreadQueries().build()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        var view: View = nav_view.getHeaderView(0)
        var tv_headerUsername = view.findViewById<TextView>(R.id.tv_header_UserName)
        var tv_headeremail = view.findViewById<TextView>(R.id.tv_header_email)


        //getting userdetails to show in header navigation bar ->> Start
        var sharedpref: SharedPreferences = this.getSharedPreferences("UserInfo", 0)
        var Username: String = sharedpref.getString("username", null)
        var Email: String = sharedpref.getString("email", null)
        var Number: String = sharedpref.getString("number", null)

        tv_headerUsername.text = Username
        tv_headeremail.text = Email
        name.text = Username
        //getting userdetails to show in header navigation bar ->> End

        // handling the favorite btn -->Start



        //setting the viewpager ->> Start
        var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        dotscount = viewPagerAdapter.count
        dots = arrayOfNulls(dotscount)

        for (i in 0 until dotscount) {

            dots!![i] = ImageView(this)
            dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.non_active))

            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            params.setMargins(8, 0, 8, 0)

            SliderDots.addView(dots!![i], params)

        }
        dots!![0]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.active_dot));

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotscount) {
                    dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.non_active))
                }

                dots!![position]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.active_dot))

            }

        })
        // setting viewpager->> End


        //checking location permission --> Start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
            restaurantPresenter.checklocationpermission(this)
        }
        //checking location permission-->End

        val viewGroup = (this.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

        var dividerItemDecoration:com.sa.restaurant.adapters.DividerItemDecoration= com.sa.restaurant.adapters.DividerItemDecoration(this)
        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = restaurantadapter(this, list)
        recyclerview.addItemDecoration(dividerItemDecoration)
        recyclerview.adapter = adapter


        // Getting our current location ->> Start
        iGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)
        var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
        Log.i("location req", "called")
        locationreq = restaurantPresenter.BuildLocationreq()
        locationcallback = restaurantPresenter.Buildlocationcallback(iGoogleApiServices, viewGroup, this, adapter)
//        var thread:Thread= Thread(Runnable {
//            Thread.sleep(2000)
//            Log.i("Restaurants",RestaurantPresenterImpl.list.size.toString())
//            RestaurantsList = RestaurantPresenterImpl.list
//        })
//        thread.start()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }
        // Getting our current location ->> End


    }

//    fun getmMap(): GoogleMap {
//        return mMap
//    }


//    fun checklocationpermission(): Boolean {
//
//        return if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
//            } else {
//                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
//            }
//            false
//
//        } else
//            true
//
//    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            99 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleClient != null) {
                            var RestaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
                            googleClient = RestaurantPresenter.createClient(this)
                            googleClient!!.connect()
                        }
//                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.restaurant, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.maps ->{

                return true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.home -> {
                 Fragmentutils.removeFragment(favRestros,fragmentManager)
            }
            R.id.fav -> {

                Fragmentutils.addFragment(this,favRestros,fragmentManager,R.id.content)
            }
            R.id.weather -> {

            }
            R.id.logout -> {

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


//    fun geturl(lat: Double, lng: Double, nearbyplace: String): String {
//
//        var googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
//        googleplaceurl.append("location=" + lat + "," + lng)
//        googleplaceurl.append("&radius=" + 10000)
//        googleplaceurl.append("&type=" + nearbyplace)
//        googleplaceurl.append("&sensor=true")
//        googleplaceurl.append("&key=" + "AIzaSyB0_n9UBObCELuk4pLP8XL1kIKghrPNdks")
//
//        return googleplaceurl.toString()
//    }
//
//    fun geturl(): String {
//        return url
//    }


//    fun nearByplace(typeplace: String) {
//        mMap.clear()
//        val url = geturl(loc.latitude, loc.longitude, typeplace)
//        iGoogleApiServices.getnearbyplaces(url).enqueue(object : retrofit2.Callback<POJO> {
//            override fun onFailure(call: Call<POJO>?, t: Throwable?) {
//                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onResponse(call: Call<POJO>?, response: Response<POJO>?) {
//                pojo = response!!.body()!!
//                var latlng2: LatLng? = null
//                if (response!!.isSuccessful) {
//                    for (i in 0 until response.body()!!.results!!.size) {
////                        val markerOptions: MarkerOptions = MarkerOptions()
//                        val googlePlace = response.body()!!.results[i]
//                        val lat = googlePlace.geometry.location.lat
//                        val lng = googlePlace.geometry.location.lng
//                        val placename = googlePlace.name
//                        list.add(placename)
//                        latlng2 = LatLng(lat, lng)
//
////                        markerOptions.position(latlng2)
////                        markerOptions.title(placename)
////                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
////                        mMap.addMarker(markerOptions)
//
//                    }
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng2))
//                }
//            }
//
//
//        })
//    }

    override fun getcurrentlatlng(location: Location, iGoogleApiServices: IGoogleApiServices, context: ViewGroup, activity: Context, adapter: restaurantadapter) {

          //  var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
         //   list = restaurantPresenter.nearbyplaces(this, "Restaurants", location, iGoogleApiServices)

           // Log.i("list $count", "${list.size}")
        if(list!=null){
            for (l in list.indices){
                adapter.array.add(list[l])
                Log.i("list passed", "success $count")
            }
            adapter.notifyDataSetChanged()

        }else{
            Log.i("list is null", "trying again")
        }
    }

    override fun restaurantslist(list: ArrayList<RestaurantData>, activity: Context, adapter: restaurantadapter) {
        if(list!=null){
            for (l in list.indices){
                adapter.array.add(list[l])
                Log.i("list passed", "success $count")
            }
            adapter.notifyDataSetChanged()

        }else{
            Log.i("list is null", "trying again")
        }
    }
}
