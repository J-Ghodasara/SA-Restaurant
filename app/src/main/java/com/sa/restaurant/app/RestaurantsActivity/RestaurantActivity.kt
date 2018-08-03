package com.sa.restaurant.app.RestaurantsActivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.FragmentManager
import android.app.ProgressDialog
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
import android.support.design.widget.BottomNavigationView
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
import android.widget.Toast
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
import com.sa.restaurant.app.MapsActivity.Weather.weatherFragment
import com.sa.restaurant.app.RestaurantsActivity.model.POJO
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.app.RestaurantsActivity.view.RestaurantView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.helpers.BottomNavigationViewHelper
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


class RestaurantActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RestaurantView, SwipeRefreshLayout.OnRefreshListener {


    private var dotscount: Int = 0
    private var dots: Array<ImageView?>? = null

    lateinit var loc: Location
    lateinit var pojo: POJO
    private lateinit var mMap: GoogleMap
    lateinit var url: String
    var list: ArrayList<RestaurantData> = ArrayList()
    var isEnd: Boolean = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var adapter: RestaurantAdapter
    var RestaurantsList: ArrayList<String> = ArrayList()
    var isfav: Boolean = false
    lateinit var mydb: Mydatabase
    var favRestros: FavoriteRestaurants = FavoriteRestaurants()
    var mapsFragment: MapsFragment = MapsFragment()
    var restaurantInfoFragment: RestaurantInfoFragment = RestaurantInfoFragment()

    var weatherIsVisibletouser: Boolean = false

    var GEOFENCE_RADIUS_IN_METERS: Int = 1000
    var weatherfragment: weatherFragment = weatherFragment()
    lateinit var geofencingClient: GeofencingClient
    var PROX_ALERT_INTENT = "com.sa.restaurant.proximity"
    lateinit var locationManager: LocationManager
    lateinit var location: Location
    var mapsisVisibletouser: Boolean = false
    lateinit var navigationView: NavigationView
    lateinit var paginate: NoPaginate
    var countdown: Int = 10
    var showMore: Boolean = true
    var noMoreItems: Boolean = false


    companion object {
        var favIsVisibletouser: Boolean = false
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


    var mOnNavigationItemSelected: BottomNavigationView.OnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.home -> {
                    supportActionBar!!.title = "Restaurants"
                    favIsVisibletouser = false
                    weatherIsVisibletouser = false
                    mapsisVisibletouser = false
                    if (RestaurantInfoFragment.isInfoVisible) {
                        Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                        RestaurantInfoFragment.isInfoVisible = false
                    }
                    Fragmentutils.removeFragment(favRestros, fragmentManager)
                    Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                    Fragmentutils.removeFragment(weatherfragment, fragmentManager)

                    invalidateOptionsMenu()
                    if (!homeIsVisible!!) {
                        list.clear()
                        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mcount = 1
                            var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
                            restaurantPresenter.nearbyplaces(applicationContext, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)
                            homeIsVisible = true
                            return true
                        }
                    }
                }
                R.id.favorite -> {
                    list.clear()
                    supportActionBar!!.title = "Favorites"
                    weatherIsVisibletouser = false
                    mapsisVisibletouser = false
                    homeIsVisible = false
                    RestaurantInfoFragment.isInfoVisible = false
                    if (!favIsVisibletouser) {
                        Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                        Fragmentutils.removeFragment(weatherfragment, fragmentManager)
                        Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                        Fragmentutils.replaceFragment(applicationContext, favRestros, fragmentManager, R.id.content)

                        favIsVisibletouser = true
                        return true
                    } else {
                        return true
                    }
                }
                R.id.weather_info -> {
                    list.clear()
                    supportActionBar!!.title = "Weather"
                    favIsVisibletouser = false
                    mapsisVisibletouser = false
                    homeIsVisible = false
                    RestaurantInfoFragment.isInfoVisible = false
                    if (!weatherIsVisibletouser) {
                        Fragmentutils.removeFragment(favRestros, fragmentManager)
                        Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                        Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                        Fragmentutils.replaceFragment(applicationContext, weatherfragment, fragmentManager, R.id.content)
                        weatherIsVisibletouser = true
                        return true
                    } else {
                        return true
                    }
                }
                R.id.logout -> {
                    favIsVisibletouser = false
                    MainActivity.isVisible = false
                    var sharedpref: SharedPreferences = getSharedPreferences("UserInfo", 0)
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
                    var intent: Intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    Toastutils.showToast(applicationContext, "Logged out")
                    startActivity(intent)
                    return true
                }
            }
            return false
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        setSupportActionBar(toolbar)

       var sharedPreferences=getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
        var isPermissionAvailable:Boolean=sharedPreferences.getBoolean("permission",false)

        if(isPermissionAvailable==true){

            homeIsVisible=true

            var bottomNavigationViewHelper: BottomNavigationViewHelper = BottomNavigationViewHelper()
            bottomNavigationViewHelper.disableShiftMode(bottom_navigation)

            bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelected)

            MyfragmentManager = fragmentManager
            dialog = ProgressDialog(this)
            dialog.setMessage("Please wait")
            dialog.setCancelable(false)
            dialog.isIndeterminate = true
            dialog.show()
//        progress.visibility=View.GONE
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
//        var tv_weatherType = view.findViewById<TextView>(R.id.tv_header_WeatherType)
//        var tv_temperature = view.findViewById<TextView>(R.id.tv_header_temperature)
//        var image_weather = view.findViewById<ImageView>(R.id.weather_image)

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
                Log.i("UserInfo","Updated")
                mydb.myDao().update(table)


            } else {

                var table: Table = Table()
                table.name = Username
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                Log.i("UserInfo","Added")
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


//        //checking location permission --> Start
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
//            restaurantPresenter.checklocationpermission(this)
//        }
//        //checking location permission-->End

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
        }else{
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
                Log.i("UserInfo","Updated")

            } else {

                var table: Table = Table()
                table.name = Username
                table.email = Email
                table.mobilenumber = Number
                table.password = "Fb Password"
                table.loginStatus = "yes"
                mydb.myDao().adduser(table)
                Log.i("UserInfo","Added")

            }
            var errorIntent:Intent= Intent(this,ErrorActivity::class.java)
            startActivity(errorIntent)

        }




    }


    override fun onDestroy() {
//        paginate.unbind()
        super.onDestroy()

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
        //  endHasBeenReached = false
        showMore = true
        noMoreItems = false
        isEnd = false
//         isEnd = false
//        paginate.showLoading(showMore)
        // paginate.setNoMoreItems(noMoreItems)
        list.clear()
        Toastutils.showsSnackBar(this, "List Updated")
        var handler: Handler? = Handler()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mcount = 1


            var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
            restaurantPresenter.nearbyplaces(this, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)


        }

        handler!!.postDelayed(Runnable {
            swipe_refresh.isRefreshing = false
            handler.removeCallbacksAndMessages(null)
        }, 2000)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            Toastutils.showToast(this, "Shared")
        }
    }


//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when (requestCode) {
//
//            99 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//
//                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        if (googleClient == null) {
//                            var RestaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
//                            googleClient = RestaurantPresenter.createClient(this)
//                            googleClient!!.connect()
//                        }
//
//                    }
//                } else {
//                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (RestaurantInfoFragment.isInfoVisible) {
            RestaurantInfoFragment.isInfoVisible = false
            super.onBackPressed()
        } else if (favIsVisibletouser || weatherIsVisibletouser || mapsisVisibletouser) {
            supportActionBar!!.title = "Restaurants"
            if (favIsVisibletouser) {
                list.clear()
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mcount = 1
                    var restaurantPresenter: RestaurantPresenterImpl = RestaurantPresenterImpl()
                    restaurantPresenter.nearbyplaces(this, "restaurant", RestaurantPresenterImpl.loc, iGoogleApiServices, adapter, recyclerview)


                }
            }
            favIsVisibletouser = false
            weatherIsVisibletouser = false
            Fragmentutils.removeFragment(favRestros, fragmentManager)
            Fragmentutils.removeFragment(mapsFragment, fragmentManager)
            Fragmentutils.removeFragment(weatherfragment, fragmentManager)
            navigationView.menu.getItem(0).isChecked = true
            this.invalidateOptionsMenu()
        } else {
            RestaurantInfoFragment.isInfoVisible = false
            finishAffinity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                }
                return true
            }
            R.id.share -> {

                var alertdialogbuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                alertdialogbuilder.setMessage("Long press on any restaurant name to share")
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
                Log.i("isHomeVisible", homeIsVisible.toString())
                Log.i("isHomeVisible","fav"+ favIsVisibletouser.toString())
                Log.i("isHomeVisible", "weather"+weatherIsVisibletouser.toString())
                Log.i("isHomeVisible", "maps"+mapsisVisibletouser.toString())
                supportActionBar!!.title = "Restaurants"
                favIsVisibletouser = false
                weatherIsVisibletouser = false
                mapsisVisibletouser = false
                if (RestaurantInfoFragment.isInfoVisible) {
                    Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                    RestaurantInfoFragment.isInfoVisible = false
                }
                Fragmentutils.removeFragment(favRestros, fragmentManager)
                Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                Fragmentutils.removeFragment(weatherfragment, fragmentManager)

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

                    favIsVisibletouser = true
                } else {

                }
            }
            R.id.weather -> {
                list.clear()
                supportActionBar!!.title = "Weather"
                favIsVisibletouser = false
                mapsisVisibletouser = false
                homeIsVisible = false
                RestaurantInfoFragment.isInfoVisible = false
                if (!weatherIsVisibletouser) {
                    Fragmentutils.removeFragment(favRestros, fragmentManager)
                    Fragmentutils.removeFragment(mapsFragment, fragmentManager)
                    Fragmentutils.removeFragment(restaurantInfoFragment, fragmentManager)
                    Fragmentutils.replaceFragment(this, weatherfragment, fragmentManager, R.id.content)
                    weatherIsVisibletouser = true

                } else {

                }
            }
            R.id.logout -> {
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
                // Log.i("list passed", "success $count")
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
                //  Log.i("list passed", "success $count")
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
