import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.data.model.ProductsResponse
import com.pablodiste.sowingoapp.repository.ProductsRepository
import com.pablodiste.sowingoapp.ui.products.ProductsViewModel
import com.pablodiste.sowingoapp.util.Resource
import com.pablodiste.sowingoapp.utils.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class ProductsViewModelTest {

    lateinit var viewModel: ProductsViewModel
    lateinit var productsRepository: ProductsRepository
    lateinit var mockProductsResponse: ProductsResponse

    @Before
    fun init() {
        val context = RuntimeEnvironment.application
        productsRepository = mockk(relaxed = true)
        mockProductsResponse = products()
        coEvery { productsRepository.getProducts() } returns Response.success(mockProductsResponse)
        viewModel = ProductsViewModel(productsRepository, context)
    }

    @Test
    fun `happy path`() {
        val resource = viewModel.productsLiveData.getOrAwaitValue()
        Assert.assertTrue(resource is Resource.Success)
        Assert.assertEquals(resource.data?.size, mockProductsResponse.hits.size)
    }

    private fun products(): ProductsResponse {
        return ProductsResponse(mutableListOf(Product(
            name = "Product Name"
        )))
    }
}