package com.sa.restaurant.app.mapsActivity.weather.model

data class Channel(
	val atmosphere: Atmosphere? = null,
	val image: Image? = null,
	val item: Item? = null,
	val lastBuildDate: String? = null,
	val link: String? = null,
	val description: String? = null,
	val language: String? = null,
	val units: Units? = null,
	val title: String? = null,
	val astronomy: Astronomy? = null,
	val ttl: String? = null,
	val location: Location? = null,
	val wind: Wind? = null
)
