package com.lex.placesdiary.models

data class PlacesDiaryModel(
    var id: Int,
    var title: String,
    var description: String,
    var image: String,
    var date: String,
    var location: String,
    var latitude: Double,
    var longitude: Double
)
