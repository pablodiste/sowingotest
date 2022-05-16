package com.pablodiste.sowingoapp.repository

import com.pablodiste.sowingoapp.data.model.FavoritesResponse
import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.data.model.ProductsResponse
import com.pablodiste.sowingoapp.data.remote.SowingoApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val sowingoApi: SowingoApi
) {

    suspend fun getProducts(): Response<ProductsResponse> {
        return sowingoApi.getProducts()
    }

    suspend fun addToFavorites(product: Product): Response<FavoritesResponse> {
        return sowingoApi.addToFavorites()
    }

    suspend fun removeFromFavorites(product: Product): Response<FavoritesResponse> {
        return sowingoApi.removeFromFavorites()
    }
}