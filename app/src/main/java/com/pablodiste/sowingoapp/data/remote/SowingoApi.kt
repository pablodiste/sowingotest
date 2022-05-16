package com.pablodiste.sowingoapp.data.remote

import com.pablodiste.sowingoapp.data.model.FavoritesResponse
import com.pablodiste.sowingoapp.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface SowingoApi {

    @GET("products")
    suspend fun getProducts(): Response<ProductsResponse>

    @POST("favorites")
    suspend fun addToFavorites(): Response<FavoritesResponse>

    @DELETE("favorites")
    suspend fun removeFromFavorites(): Response<FavoritesResponse>

}