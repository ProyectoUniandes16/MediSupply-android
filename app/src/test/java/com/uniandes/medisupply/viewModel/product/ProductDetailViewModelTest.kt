package com.uniandes.medisupply.viewModel.product

import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.model.PRODUCT
import com.uniandes.medisupply.model.PRODUCT_UI
import com.uniandes.medisupply.presentation.navigation.ProductDestination
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailViewModel
import io.mockk.coEvery
import io.mockk.every
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProductDetailViewModel
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val internalNavigator: InternalNavigator = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        initViewModel()
    }

    private fun initViewModel(
        result: Result<Product> = Result.success(PRODUCT)
    ) {
        every { internalNavigator.getParam(ProductDestination.ProductDetail.PRODUCT) } returns PRODUCT_UI
        coEvery { productRepository.getProductById(any()) } returns result
        viewModel = ProductDetailViewModel(
            productRepository,
            internalNavigator
        )
    }

    @Test
    fun `on Event Back Clicked SHOULD navigate back`() {
        // when
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnBackClicked)

        // then
        internalNavigator.stepBack()
    }

    @Test
    fun `init SHOULD load product detail`() {
        // given
        // when init view model
        initViewModel()
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assert(viewModel.uiState.value.product == PRODUCT_UI)

    }

    @Test
    fun `init SHOULD show error on failure`() {
        // given
        val errorMessage = "Error loading product"
        // when init view model
        initViewModel(
            Result.failure(Exception(errorMessage))
        )
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
}