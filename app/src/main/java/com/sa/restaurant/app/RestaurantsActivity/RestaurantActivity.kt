package com.sa.restaurant.app.RestaurantsActivity

import android.annotation.SuppressLint
import android.app.*
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.sa.restaurant.ErrorActivity
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.adapters.ViewPagerAdapter
import com.sa.restaurant.adapters.RestaurantAdapter
import com.sa.restaurant.app.Favorites.FavoriteRestaurants
import com.sa.restaurant.app.MapsActivity.MapsFragment
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenter
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenterImpl
import com.sa.restaurant.app.MapsActivity.Weather.WeatherFragment
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.app.RestaurantsActivity.view.RestaurantView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.services.MyBroadcastReceiverService
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.app_bar_restaurant.*
import kotlinx.android.synthetic.main.content_restaurant.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener
import ru.alexbykov.nopaginate.paginate.NoPaginate

/**
 * RestaurantActivity class that shows all the restaurants fetched from the api
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

class RestaurantActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RestaurantView, SwipeRefreshLayout.OnRefreshListener {


    private var dotscount: Int = 0
    private var dots: Array<ImageView?>? = null
    lateinit var myMenu: Menu

    private lateinit var mMap: GoogleMap
    lateinit var url: String
    var list: ArrayList<RestaurantData> = ArrayList()
    var isEnd: Boolean = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var adapter: RestaurantAdapter

    lateinit var mydb: Mydatabase
    var favRestros: FavoriteRestaurants = FavoriteRestaurants()
    var mapsFragment: MapsFragment = MapsFragment()
    var isFromRestaurant: Boolean = true

    var weatherIsVisibletouser: Boolean = false

    var GEOFENCE_RADIUS_IN_METERS: Int = 1000
    var weatherfragment: WeatherFragment = WeatherFragment()
    lateinit var geofencingClient: GeofencingClient

    lateinit var locationManager: LocationManager
    lateinit var location: Location
    var mapsisVisibletouser: Boolean = false
    lateinit var navigationView: NavigationView
    lateinit var paginate: NoPaginate

    var showMore: Boolean = true
    var noMoreItems: Boolean = false


    companion object {
        var favIsVisibletouser: Boolean = false
        var restaurantInfoFragment: RestaurantInfoFragment = RestaurantInfoFragment()
        var endHasBeenReached: Boolean = false
        var restrolist: ArrayList<RestaurantData> = ArrayList()
        var homeIsVisible: Boolean? = null
        var count: Int = 0
        lateinit var locationreq: LocationRequest
        lateinit var locationcallback: LocationCallback
        var googleClient: GoogleApiClient? = null
        lateinit var iGoogleApiServices: IGoogleApiServices
        lateinit var itemaction: MenuItem
        var mcount: Int = 0
        var oneTime: Int = 0
        lateinit var dialog: ProgressDialog
        var scrollCount: Int = 0
        lateinit var MyfragmentManager: FragmentManager

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        setSupportActionBar(toolbar)

        var sharedPreferences = getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
        var isPermissionAvailable: Boolean = sharedPreferences.getBoolean("permission", false)

        if (isPermissionAvailable == true) {


            homeIsVisible = true
            Log.i("Context", this.toString())

            var mySharedPreferences: SharedPreferences = this.getSharedPreferences("RestaurantsOnMaps", android.content.Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor = mySharedPreferences.edit()
            editor.putString("WhatToShow", "all")
            editor.apply()
            val intent = Intent(this, MyBroadcastReceiverService::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                    this.applicationContext, 234324243, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, 5000, pendingIntent)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 60000 * 60, pendingIntent)




            MyfragmentManager = fragmentManager
            dialog = ProgressDialog(this)
            dialog.setMessage("Please wait")
            dialog.setCancelable(false)
            dialog.isIndeterminate = true
            dialog.show()

            MainActivity.isVisible = false
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mydb = Room.databaseBuilder(this, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
            var sharedpref2: SharedPreferences = this.getSharedPreferences("UserInfo", 0)
            var Username2 = sharedpref2.getString("username", null)
            var uid = mydb.myDao().getUserId(Username2!!)


            geofencingClient = LocationServices.getGeofencingClient(this)


            var RestaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
            googleClient = RestaurantPresenter.createClient(this)
            googleClient!!.connect()


            val toggle = ActionBarDrawerToggle(
                    this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()

            nav_view.setNavigationItemSelectedListener(this)

            var view: View = nav_view.getHeaderView(0)
            var tv_headerUsername = view.findViewById<TextView>(R.id.tv_header_UserName)
            var tv_headeremail = view.findViewById<TextView>(R.id.tv_header_email)

            //fetching weather info
            var weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
            weatherPresenter.createClient(this)


            iGoogleApiServices = RetrofitnearbyClient.getClient("https://query.yahooapis.com/").create(IGoogleApiServices::class.java)

            locationreq = weatherPresenter.BuildLocationreq()
            locationcallback = weatherPresenter.Buildlocationcallback(iGoogleApiServices, this, view, 1)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
            }


            //getting userdetails to show in header navigation bar ->> Start
            var sharedpref: SharedPreferences = this.getSharedPreferences("UserInfo", 0)
            var Username: String = sharedpref.getString("username", null)
            var Email: String = sharedpref.getString("email", null)
            var Number: String = sharedpref.getString("number", null)

            var result2: List<Table> = mydb.myDao().checkuser(Username, Email)

            if (result2.isNotEmpty()) {

                var uid = mydb.myDao().getUserId(Username)
                var table: Table = Table()
                table.name = Username
                table.id = uid
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                Log.i("UserInfo", "Updated")
                mydb.myDao().update(table)


            } else {

                var table: Table = Table()
                table.name = Username
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                Log.i("UserInfo", "Added")
                mydb.myDao().adduser(table)

            }
            tv_headerUsername.text = Username
            tv_headeremail.text = Email
            name.text = Username
            //getting userdetails to show in header navigation bar ->> End


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


            val viewGroup = (this.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

            var dividerItemDecoration: com.sa.restaurant.adapters.DividerItemDecoration = com.sa.restaurant.adapters.DividerItemDecoration(this)
            recyclerview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            adapter = RestaurantAdapter(this, list, view, 1)
            recyclerview.addItemDecoration(dividerItemDecoration)
            recyclerview.adapter = adapter


            recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    Log.i("ScrollCount", scrollCount.toString())
                    if (scrollCount == 0) {

                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView!!.layoutManager)
                        val childcount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val lastVisible = layoutManager.findLastVisibleItemPosition()

                        Log.i("TotalItemCount", totalItemCount.toString())
                        Log.i("last+child", (lastVisible + childcount).toString())
                        endHasBeenReached = lastVisible + childcount == totalItemCount
                        if (totalItemCount > 0 && endHasBeenReached) {
                            Log.i("Onscrolled", "inside")
                            scrollCount++
                            isEnd = true
                            paginate = NoPaginate.with(recyclerView).setOnLoadMoreListener(object : OnLoadMoreListener {
                                override fun onLoadMore() {
                                    if (scrollCount == 1) {
                                        if (isEnd == true && restrolist.isNotEmpty()) {


                                            //   dialog.dismiss()
                                            Log.i("RestrolistSize", restrolist.size.toString())
                                            paginate.showLoading(showMore)

                                            launch(UI) {
                                                oneTime++
                                                delay(3000)
                                                if (oneTime == 1) {
                                                    for (l in ((restrolist.size / 2) + 1)..((restrolist.size) - 1)) {
                                                        adapter.array.add(restrolist[l])
                                                        Log.i("list passed", "success $l")
                                                    }

                                                    var restroListSize = list.size
                                                    Log.i("ArrayAdapterSize", adapter.array.size.toString())
                                                    adapter.notifyDataSetChanged()
                                                    if (restroListSize == list.size) {
                                                        Log.i("loaded", "list")
                                                        showMore = false
                                                        noMoreItems = true
                                                        paginate.showLoading(showMore)
                                                        paginate.setNoMoreItems(noMoreItems)
                                                    }
                                                }


                                            }


                                        }
                                    }


                                }


                            }).setLoadingTriggerThreshold(5).build()
                            paginate.setNoMoreItems(noMoreItems)
                            // paginate.showLoading(showMore)

                        }
                    }
                }

            })


            // Getting our current location ->> Start
            iGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)
            var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
            Log.i("location req", "called")
            locationreq = restaurantPresenter.BuildLocationreq()
            locationcallback = restaurantPresenter.Buildlocationcallback(iGoogleApiServices, viewGroup, this, adapter, recyclerview)


            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
            }
            // Getting our current location ->> End

            navigationView = findViewById<NavigationView>(R.id.nav_view)
            swipe_refresh.setOnRefreshListener(this)
        } else {
            mydb = Room.databaseBuilder(this, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
            var sharedpref: SharedPreferences = this.getSharedPreferences("UserInfo", 0)
            var Username: String = sharedpref.getString("username", null)
            var Email: String = sharedpref.getString("email", null)
            var Number: String = sharedpref.getString("number", null)

            var result2: List<Table> = mydb.myDao().checkuser(Username, Email)

            if (result2.isNotEmpty()) {

                var uid = mydb.myDao().getUserId(Username)
                var table: Table = Table()
                table.name = Username
                table.id = uid
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                mydb.myDao().update(table)
                Log.i("UserInfo", "Updated")

            } else {

                var table: Table = Table()
                table.name = Username
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                mydb.myDao().adduser(table)
                Log.i("UserInfo", "Added")

            }

            var errorIntent: Intent = Intent(this, ErrorActivity::class.java)
            Log.i("Cleared", "top")
            errorIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(errorIntent)

        }


    }


    fun getGeofence(latLng: LatLng, placename: String): Geofence? {
        var latlon: LatLng = latLng

        var geofence: Geofence = Geofence.Builder()
                .setRequestId(placename)
                .setCircularRegion(latlon.latitude, latlon.longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(1000000)
                .build()
        return geofence

    }

    fun setWeatherInfo(view: View, bundle: Bundle, context: Context) {


        var code = bundle["code"]
        var temperatureValue = bundle["temperature"]
        var text = bundle["text"]


        var tv_weatherType = view.findViewById<TextView>(R.id.tv_header_WeatherType)
        var tv_temperature = view.findViewById<TextView>(R.id.tv_header_temperature)
        var image_weather = view.findViewById<ImageView>(R.id.weather_image)

        tv_weatherType.text = text.toString()
        tv_temperature.text = temperatureValue.toString() + "F"
        var resources: Int = context.resources.getIdentifier("drawable/icon$code", null, "com.sa.restaurant")
        var icon: Drawable = context.resources.getDrawable(resources)
        image_weather.setImageDrawable(icon)

    }

    override fun onRefresh() {
        scrollCount = 0
        oneTime = 0
        showMore = true
        noMoreItems = false
        isEnd = false
        list.clear()

        var handler: Handler? = Handler()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mcount = 1


            var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
            restaurantPresenter.nearbyplaces(this, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)
            Toastutils.showsSnackBar(this, "List Updated")

        }

        handler!!.postDelayed(Runnable {
            swipe_refresh.isRefreshing = false
            handler.removeCallbacksAndMessages(null)
        }, 2000)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (RestaurantInfoFragment.isInfoVisible && isFromRestaurant) {
            RestaurantInfoFragment.isInfoVisible = false
            super.onBackPressed()
        } else if (RestaurantInfoFragment.isInfoVisible && !isFromRestaurant) {
            RestaurantInfoFragment.isInfoVisible = false
            favIsVisibletouser = true
            myMenu.findItem(R.id.share).isVisible = true
            myMenu.findItem(R.id.showonmaps).isVisible = true
            supportActionBar!!.title = "Favorites"
            super.onBackPressed()
        } else if (mapsisVisibletouser) {
            if (isFromRestaurant) {
                supportActionBar!!.title = "Restaurants"
                myMenu.findItem(R.id.share).isVisible = true
                myMenu.findItem(R.id.showonmaps).isVisible = true
                mapsisVisibletouser = false
                homeIsVisible = true
            } else {
                favIsVisibletouser = true
                myMenu.findItem(R.id.share).isVisible = true
                myMenu.findItem(R.id.showonmaps).isVisible = true
                supportActionBar!!.title = "Favorites"
                mapsisVisibletouser = false
                favIsVisibletouser = true

            }
            super.onBackPressed()
        } else if (favIsVisibletouser || weatherIsVisibletouser) {
            supportActionBar!!.title = "Restaurants"
            isFromRestaurant = true
            homeIsVisible = true
            if (favIsVisibletouser) {
                list.clear()
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mcount = 1
                    var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
                    restaurantPresenter.nearbyplaces(this, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)

                    var mySharedPreferences: SharedPreferences = this.getSharedPreferences("RestaurantsOnMaps", android.content.Context.MODE_PRIVATE)
                    var editor: SharedPreferences.Editor = mySharedPreferences.edit()
                    editor.putString("WhatToShow", "all")
                    editor.apply()

                }
            }
            favIsVisibletouser = false
            weatherIsVisibletouser = false
            Fragmentutils.removeFragment(favRestros, fragmentManager)
            Fragmentutils.removeFragment(mapsFragment, fragmentManager)
            Fragmentutils.removeFragment(weatherfragment, fragmentManager)
            navigationView.menu.getItem(0).isChecked = true
            isFromRestaurant = false
            this.invalidateOptionsMenu()
        } else {
            RestaurantInfoFragment.isInfoVisible = false
            finishAffinity()


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        myMenu = menu
        menuInflater.inflate(R.menu.restaurant, menu)
        Log.i("inside", "menu")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.showonmaps -> {
                mapsisVisibletouser = true
                if (homeIsVisible!!) {
                    isFromRestaurant = true
                }
                if (favIsVisibletouser) {
                    isFromRestaurant = false
                }
                supportActionBar!!.title = "Locations"
                dialog = ProgressDialog(this)
                dialog.setMessage("Please wait")
                dialog.setCancelable(false)
                dialog.isIndeterminate = true
                dialog.show()
                if (item.isVisible) {
                    favIsVisibletouser = false
                    Fragmentutils.addFragmentwithBackStack(this, mapsFragment, fragmentManager, R.id.content)
                    itemaction = item
                    item.isVisible = false
                    myMenu.findItem(R.id.share).isVisible = false
                }
                return true
            }
            R.id.share -> {

                var alertdialogbuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                alertdialogbuilder.setMessage("To Share Just Long Press On Any Restaurant")
                alertdialogbuilder.setPositiveButton("Ok") { dialog, which -> }
                val alertDialog = alertdialogbuilder.create()
                alertDialog.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.home -> {
                isFromRestaurant = true
                Log.i("isHomeVisible", homeIsVisible.toString())
                Log.i("isHomeVisible", "fav" + favIsVisibletouser.toString())
                Log.i("isHomeVisible", "weather" + weatherIsVisibletouser.toString())
                Log.i("isHomeVisible", "maps" + mapsisVisibletouser.toString())
                supportActionBar!!.title = "Restaurants"
                myMenu.findItem(R.id.share).isVisible = true
                myMenu.findItem(R.id.showonmaps).isVisible = true
                favIsVisibletouser = false
                weatherIsVisibletouser = false
                mapsisVisibletouser = false
                if (RestaurantInfoFragment.isInfoVisible) {
                    RestaurantInfoFragment.isInfoVisible = false
                    Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                    Log.i("Info", "Removed")
                }
                Fragmentutils.removeFragment(favRestros, fragmentManager)
                Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                Fragmentutils.removeFragment(weatherfragment, fragmentManager)

                var mySharedPreferences: SharedPreferences = this.getSharedPreferences("RestaurantsOnMaps", android.content.Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor = mySharedPreferences.edit()
                editor.putString("WhatToShow", "all")
                editor.apply()
                this.invalidateOptionsMenu()
                if (!homeIsVisible!!) {
                    list.clear()
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mcount = 1
                        var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
                        restaurantPresenter.nearbyplaces(this, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)

                    }
                }
            }
            R.id.fav -> {
                list.clear()
                isFromRestaurant = false
                supportActionBar!!.title = "Favorites"
                weatherIsVisibletouser = false
                mapsisVisibletouser = false
                homeIsVisible = false
                RestaurantInfoFragment.isInfoVisible = false
                if (!favIsVisibletouser) {
                    Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                    Fragmentutils.removeFragment(weatherfragment, fragmentManager)
                    Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                    Fragmentutils.replaceFragment(this, favRestros, fragmentManager, R.id.content)
                    myMenu.findItem(R.id.share).isVisible = true
                    myMenu.findItem(R.id.showonmaps).isVisible = true
                    favIsVisibletouser = true
                } else {

                }
            }
            R.id.weather -> {
                list.clear()
                supportActionBar!!.title = "Weather"
                favIsVisibletouser = false
                isFromRestaurant = true
                mapsisVisibletouser = false
                homeIsVisible = false
                RestaurantInfoFragment.isInfoVisible = false
                if (!weatherIsVisibletouser) {
                    Fragmentutils.removeFragment(favRestros, fragmentManager)
                    Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                    Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                    Fragmentutils.replaceFragment(this, weatherfragment, fragmentManager, R.id.content)
                    myMenu.findItem(R.id.share).isVisible = false
                    myMenu.findItem(R.id.showonmaps).isVisible = false
                    weatherIsVisibletouser = true

                } else {

                }
            }
            R.id.logout -> {
                var alertdialogbuilder: AlertDialog.Builder = AlertDialog.Builder(this)

                alertdialogbuilder.setMessage("Are you sure you want to Logout?")
                alertdialogbuilder.setPositiveButton("Logout") { dialog, which ->
                    favIsVisibletouser = false
                    MainActivity.isVisible = false

                    var sharedpref: SharedPreferences = this.getSharedPreferences("UserInfo", 0)
                    var username = sharedpref.getString("username", null)
                    var email = sharedpref.getString("email", null)
                    var password = sharedpref.getString("password", null)
                    var number = sharedpref.getString("number", null)
                    var uid = mydb.myDao().getUserId(username)
                    var table: Table = Table()
                    table.id = uid
                    table.name = username
                    table.email = email
                    table.password = password
                    table.mobilenumber = number
                    table.loginStatus = "no"
                    mydb.myDao().update(table)

                    LoginManager.getInstance().logOut()
                    var intent: Intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    Toastutils.showToast(this, "Logged out")
                    startActivity(intent)
                }
                alertdialogbuilder.setNegativeButton("Cancel") { dialog, which ->

                }
                val alertDialog = alertdialogbuilder.create()
                alertDialog.show()

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun getcurrentlatlng(location: Location, iGoogleApiServices: IGoogleApiServices, context: ViewGroup, activity: Context, adapter: RestaurantAdapter) {


        if (list != null) {
            restrolist = list

            Log.i("RestroList", restrolist.size.toString())

            for (l in 0..list.size / 2) {
                adapter.array.add(list[l])

            }
            adapter.notifyDataSetChanged()

        } else {
            Log.i("list is null", "trying again")
        }
    }


    override fun restaurantslist(list: ArrayList<RestaurantData>, activity: Context, adapter: RestaurantAdapter, recyclerView: RecyclerView) {
        if (list.isNotEmpty()) {

            restrolist = list

            Log.i("RestroList", restrolist.size.toString())
            adapter.array.clear()
            for (l in 0..list.size / 2) {
                adapter.array.add(list[l])

            }
            adapter.notifyDataSetChanged()
              dialog.dismiss()


            var mapsFragment: MapsFragment = MapsFragment()
            mapsFragment.callgeofence(activity)


        } else {
            Log.i("list is null", "trying again")
        }
    }


}
