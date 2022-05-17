package com.pablodiste.sowingoapp.data.model

data class Product(
    var objectID: String? = null,
    val name: String? = null,
    val main_image: String? = null,
    val advertising_badges: AdvertisingBadges? = null,
    val vendor_inventory: MutableList<VendorInventory>? = null,
    var isFavorite: Boolean = false
)

data class AdvertisingBadges(val has_badge: Boolean?, val badges: MutableList<Badge>)

data class Badge(val badge_type: String?, val badge_image_url: String?)

data class VendorInventory(val list_price: Float, val price: Float)
