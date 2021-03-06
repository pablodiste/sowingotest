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

/**
 * Holds the logic for getting the products and storing the favorites
 * @author pablodiste
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val productsLiveData: MutableLiveData<Resource<List<Product>>> = MutableLiveData()
    private lateinit var productsResponse: ProductsResponse

    var showOnlyFavorites: Boolean = false
    var textFilter: String = ""

    init {
        getProducts()
    }

    /**
     * Requests a list of products. The products are returned via productsLiveData
     */
    fun getProducts() = viewModelScope.launch {
        fetchProducts()
    }

    /**
     * Requests a change of the favorite state for a product.
     * @param product Product to favorite/unfavorite
     * @param favorite true when adding to favorites, false otherwise
     */
    fun setFavorite(product: Product, favorite: Boolean) {
        product.isFavorite = favorite
        viewModelScope.launch {
            addOrRemoveToFavorites(product, favorite)
        }
    }

    /**
     * Applies filters to current list of products and sends them to the UI
     */
    fun refresh() {
        productsLiveData.postValue(Resource.Success(applyFilters(productsResponse.hits)))
    }

    private suspend fun fetchProducts(){
        productsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(context)) {
                val response = productsRepository.getProducts()
                handleProductsResponse(response)
            } else {
                productsLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (ex : Exception) {
            sendErrorMessage(ex)
        }
    }

    private suspend fun addOrRemoveToFavorites(product: Product,favorite: Boolean) {
        try {
            if (hasInternetConnection(context)) {
                when (favorite) {
                    true -> productsRepository.addToFavorites(product)
                    false -> productsRepository.removeFromFavorites(product)
                }
            } else {
                productsLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (ex: Exception) {
            sendErrorMessage(ex)
        }
        refresh()
    }

    private fun sendErrorMessage(ex: Exception) = when (ex) {
        is IOException -> productsLiveData.postValue(Resource.Error("Network Failure"))
        else -> productsLiveData.postValue(Resource.Error("Conversion Error"))
    }

    private fun handleProductsResponse(response: Response<ProductsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                productsResponse = resultResponse
                refresh()
                return
            }
        }
        productsLiveData.postValue(Resource.Error(response.message()))
    }

    /**
     * The list of products are filtered by the favorites toggle and can also be filtered by the search text
     * We can include additional filter conditions here, like SKU, keywords, description
     */
    private fun applyFilters(products: List<Product>): List<Product> {
        return products.filter {
            (!showOnlyFavorites || (showOnlyFavorites && it.isFavorite)) &&
            (textFilter.isEmpty() || (textFilter.isNotEmpty() && it.name!!.contains(textFilter, ignoreCase = true)))
        }
    }

}