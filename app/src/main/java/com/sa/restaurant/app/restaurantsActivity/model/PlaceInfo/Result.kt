package com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo

data class Result(
    val address_components: List<AddressComponent>,
    val adr_address: String,
    val formatted_address: String,
    val formatted_phone_number: String,
    val geometry: Geometry,
    val icon: String,
    val id: String,
    val international_phone_number: String,
    val name: String,
    val opening_hours: OpeningHours,
    val photos: List<Photo>,
    val place_id: String,
    val plus_code: PlusCode,
    val rating: Double,
    val reference: String,
    val reviews: List<Review>,
    val scope: String,
    val types: List<String>,
    val url: String,
    val utc_offset: Int,
    val vicinity: String
)