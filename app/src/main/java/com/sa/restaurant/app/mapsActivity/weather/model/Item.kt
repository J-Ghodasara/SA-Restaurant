package com.sa.restaurant.app.mapsActivity.weather.model

data class Item(
	val condition: Condition? = null,
	val link: String? = null,
	val description: String? = null,
	val guid: Guid? = null,
	val forecast: List<ForecastItem?>? = null,
	val title: String? = null,
	val pubDate: String? = null,
	val lat: String? = null,
	val jsonMemberLong: String? = null
)
