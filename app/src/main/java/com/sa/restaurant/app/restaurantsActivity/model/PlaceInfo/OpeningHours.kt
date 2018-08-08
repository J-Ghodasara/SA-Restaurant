package com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo

data class OpeningHours(
    val open_now: Boolean,
    val periods: List<Period>,
    val weekday_text: List<String>
)