package com.uniandes.medisupply.viewModel.product

import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Product
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
import com.uniandes.medisupply.model.PRODUCT_LIST
import com.uniandes.medisupply.presentation.navigation.ProductDestination
import io.mockk.verify
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class ProductListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProductListViewModel
    private val productRepository: ProductRepository = mockk(relaxed = true)
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
        coEvery { internalNavigator.getParam(ProductDestination.ProductList.IS_STANDALONE) } returns false
        viewModel = ProductListViewModel(
            productRepository,
            internalNavigator
        )
    }

    @Test
    fun `OnLoadProducts SHOULD fetch products from repository successfully`() = runTest {
        // given
        val productList = PRODUCT_LIST.map {
            ProductUI.fromDomain(it)
        }
        // when init view model
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.displayedProducts.isNotEmpty())
        assertEquals(productList, viewModel.uiState.value.displayedProducts)
    }

    @Test
    fun `OnLoadProducts SHOULD handle error when repository fails`() = runTest {
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
    fun `OnFilterQueryChange SHOULD filter products based on query`() = runTest {
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
    fun `OnDismissErrorDialog SHOULD hide error state`() = runTest {
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
    fun `OnBackClicked SHOULD request internal navigator to step back`() = runTest {
        // when
        viewModel.onEvent(ProductListViewModel.UserEvent.OnBackClicked)
        // then
        verify { internalNavigator.stepBack() }
    }

    @Test
    fun `OnProductClicked SHOULD navigate to product detail screen`() = runTest {
        viewModel.onEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        // when
        viewModel.onEvent(
            ProductListViewModel.UserEvent.OnProductClicked(viewModel.uiState.value.displayedProducts.first())
        )
        // then
        verify {
            internalNavigator.navigateTo(ProductDestination.ProductDetail,
                mapOf(ProductDestination.ProductDetail.PRODUCT to viewModel.uiState.value.displayedProducts.first())
            )
        }
    }
}
