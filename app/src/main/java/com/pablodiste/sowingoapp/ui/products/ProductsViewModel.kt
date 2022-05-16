package com.pablodiste.sowingoapp.ui.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val products: MutableLiveData<Resource<ProductsResponse>> = MutableLiveData()
    var productsResponse: ProductsResponse? = null

    init {
        getProducts()
    }

    fun getProducts() = viewModelScope.launch {
        safeProductsCall()
    }

    private suspend fun safeProductsCall(){
        products.postValue(Resource.Loading())
        try{
            if(hasInternetConnection(context)){
                val response = productsRepository.getProducts()
                products.postValue(handleProductsResponse(response))
            }
            else{
                products.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (ex : Exception){
            when (ex) {
                is IOException -> products.postValue(Resource.Error("Network Failure"))
                else -> products.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleProductsResponse(response: Response<ProductsResponse>): Resource<ProductsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                productsResponse = resultResponse
                return Resource.Success(productsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}