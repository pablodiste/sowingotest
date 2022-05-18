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
    fun `loading products`() {
        val resource = viewModel.productsLiveData.getOrAwaitValue()
        Assert.assertTrue(resource is Resource.Success)
        Assert.assertEquals(mockProductsResponse.hits.size, resource.data?.size)
    }

    @Test
    fun `add to favorites`() {
        var resource = viewModel.productsLiveData.getOrAwaitValue()

        viewModel.setFavorite(mockProductsResponse.hits[0], true)
        resource = viewModel.productsLiveData.getOrAwaitValue()

        Assert.assertTrue(resource is Resource.Success)
        val favoriteCount = resource.data?.filter { it.isFavorite }?.size
        Assert.assertEquals(1, favoriteCount)
    }

    @Test
    fun `remove from favorites`() {
        var resource = viewModel.productsLiveData.getOrAwaitValue()

        viewModel.setFavorite(mockProductsResponse.hits[0], true)
        viewModel.setFavorite(mockProductsResponse.hits[0], false)
        resource = viewModel.productsLiveData.getOrAwaitValue()

        Assert.assertTrue(resource is Resource.Success)
        val favoriteCount = resource.data?.filter { it.isFavorite }?.size
        Assert.assertEquals(0, favoriteCount)
    }

    @Test
    fun `text search`() {
        var resource = viewModel.productsLiveData.getOrAwaitValue()

        viewModel.textFilter = "Number 2"
        viewModel.refresh()
        resource = viewModel.productsLiveData.getOrAwaitValue()

        Assert.assertTrue(resource is Resource.Success)
        Assert.assertEquals(1, resource.data?.size)
    }

    private fun products(): ProductsResponse {
        return ProductsResponse(mutableListOf(
            Product(name = "Product Number 1"),
            Product(name = "Product Number 2"),
            Product(name = "Product Number 3"),
        ))
    }
}