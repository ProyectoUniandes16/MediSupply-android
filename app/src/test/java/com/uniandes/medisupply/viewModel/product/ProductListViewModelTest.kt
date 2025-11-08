package com.uniandes.medisupply.viewModel.product

import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.viewmodel.product.ProductListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.uniandes.medisupply.R
import io.mockk.verify

@ExperimentalCoroutinesApi
class ProductListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProductListViewModel
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val resourcesProvider: ResourcesProvider = mockk(relaxed = true)
    private val internalNavigator: InternalNavigator = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        initViewModel()
    }

    private fun initViewModel(
        result: Result<List<Product>> = Result.success(PRODUCT_LIST)
    ) {
        coEvery { productRepository.getProducts() } returns result
        coEvery { resourcesProvider.getString(R.string.low_stock) } returns "Low Stock"
        coEvery { resourcesProvider.getString(R.string.in_stock) } returns "In Stock"
        coEvery { resourcesProvider.getString(R.string.out_stock) } returns "Out Stock"
        viewModel = ProductListViewModel(
            productRepository,
            resourcesProvider,
            internalNavigator
        )
    }

    @Test
    fun `OnLoadProducts SHOULD fetch products from repository successfully`() {
        // given
        val productList = PRODUCT_LIST.map {
            ProductUI.fromDomain(
                it
            ).copy(
                stockStatus = when (it.stockStatus) {
                    StockStatus.IN_STOCK -> "In Stock"
                    StockStatus.LOW_STOCK -> "Low Stock"
                    StockStatus.OUT_OF_STOCK -> "Out Stock"
                }
            )
        }
        // when init view model
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.displayedProducts.isNotEmpty())
        assertEquals(productList, viewModel.uiState.value.displayedProducts)
    }

    @Test
    fun `OnLoadProducts SHOULD handle error when repository fails`() {
        // given
        val errorMessage = "Failed to fetch products"
        initViewModel(Result.failure(Exception(errorMessage)))
        // when init view model
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.displayedProducts.isEmpty())
        assertTrue(viewModel.uiState.value.showError)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }

    @Test
    fun `OnFilterQueryChange SHOULD filter products based on query`() {
        // given
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        val query = "Product 1"
        // when
        viewModel.onEvent(ProductListViewModel.UserEvent.OnFilterQueryChange(query))
        // then
        val filteredProducts = viewModel.uiState.value.displayedProducts
        assertEquals(1, filteredProducts.size)
        assertTrue(filteredProducts.all { it.name.contains(query) })
    }

    @Test
    fun `OnDismissErrorDialog SHOULD hide error state`() {
        // given
        val errorMessage = "Failed to fetch products"
        initViewModel(Result.failure(Exception(errorMessage)))
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        assertTrue(viewModel.uiState.value.showError)
        // when
        viewModel.onEvent(ProductListViewModel.UserEvent.OnDismissErrorDialog)
        // then
        assertFalse(viewModel.uiState.value.showError)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `OnBackClicked SHOULD request internal navigator to step back`() {
        // when
        viewModel.onEvent(ProductListViewModel.UserEvent.OnBackClicked)
        // then
        verify { internalNavigator.stepBack() }
    }

    private companion object {
        val PRODUCT_LIST = List(5) {
            Product(
                id = it,
                name = "Product $it",
                price = 10.0,
                stock = 100,
                stockStatus = StockStatus.IN_STOCK,
                category = "category"
            )
        }
    }
}
