package com.pablodiste.sowingoapp.ui.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.data.model.ProductsResponse
import com.pablodiste.sowingoapp.repository.ProductsRepository
import com.pablodiste.sowingoapp.util.NetworkUtil.Companion.hasInternetConnection
import com.pablodiste.sowingoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val productsLiveData: MutableLiveData<Resource<List<Product>>> = MutableLiveData()
    lateinit var productsResponse: ProductsResponse
    var showOnlyFavorites: Boolean = false

    init {
        getProducts()
    }

    private fun getProducts() = viewModelScope.launch {
        safeProductsCall()
    }

    private suspend fun safeProductsCall(){
        productsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(context)) {
                val response = productsRepository.getProducts()
                productsLiveData.postValue(handleProductsResponse(response))
            } else {
                productsLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (ex : Exception){
            when (ex) {
                is IOException -> productsLiveData.postValue(Resource.Error("Network Failure"))
                else -> productsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun applyFilter(products: List<Product>): List<Product> {
        return if (showOnlyFavorites) products.filter { it.isFavorite } else products
    }

    private fun handleProductsResponse(response: Response<ProductsResponse>): Resource<List<Product>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                productsResponse = resultResponse
                return Resource.Success(applyFilter(resultResponse.hits))
            }
        }
        return Resource.Error(response.message())
    }

    fun setFavorite(product: Product, favorite: Boolean) {
        product.isFavorite = favorite
        viewModelScope.launch {
            try {
                if (hasInternetConnection(context)) {
                    when (favorite) {
                        true -> productsRepository.addToFavorites(product)
                        false -> productsRepository.removeFromFavorites(product)
                    }
                } else {
                    productsLiveData.postValue(Resource.Error("No Internet Connection"))
                }
            }
            catch (ex : Exception) {
                productsLiveData.postValue(Resource.Error("Error updating favorite"))
            }
            refresh()
        }
    }

    fun refresh() {
        productsLiveData.postValue(Resource.Success(applyFilter(productsResponse.hits)))
    }
}