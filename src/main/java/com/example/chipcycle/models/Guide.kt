package com.example.chipcycle.models

data class GuideResponse(
    val guides: List<Guide>,
    val popular_guides: List<PopularGuide>
)


data class Guide(
    val id: Int,
    val title: String,
    val iconResourceId: Int,
    val link: String
)

data class PopularGuide(
    val id: Int,
    val title: String,
    val description: String,
    val steps_count: Int
)
