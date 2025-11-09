package com.uniandes.medisupply.viewModel.product

import android.net.Uri
import com.uniandes.medisupply.common.ContextProvider
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
import kotlinx.coroutines.test.runTest
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
    private val contextProvider: ContextProvider = mockk(relaxed = true)
    private val testUri: Uri = mockk()

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
            internalNavigator,
            contextProvider
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
    fun `init SHOULD load product detail`() = runTest {
        // given
        // when init view model
        initViewModel()
        // then
        assertFalse(viewModel.uiState.value.isLoading)
        assert(viewModel.uiState.value.product == PRODUCT_UI)
    }

    @Test
    fun `init SHOULD show error on failure`() = runTest {
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

    @Test
    fun `on Event Video Selected SHOULD update state with video info`() {
        // given
        mockUri()
        // when
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnVideoSelected(testUri))

        // then
        val uiState = viewModel.uiState.value
        assert(uiState.showVideoUploadDialog)
        assertEquals(testUri, uiState.videoUri)
        assertEquals(TEST_FILE_NAME, uiState.videoFileName)
    }

    @Test
    fun `onEvent OnVideoUploadConfirmed SHOULD upload video and show confirmation message on success`() = runTest {
        // given
        mockUri()
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnVideoSelected(testUri))
        coEvery { productRepository.uploadProductVideo(
            PRODUCT_UI.id,
            TEST_FILE_NAME,
            ByteArray(0),
            null,
            "Video upload"
        ) } returns Result.success(Unit)
        // when
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnVideoUploadConfirmed)

        // then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.showSuccessMessage)
    }

    @Test
    fun `onEvent OnVideoUploadCanceled SHOULD reset video upload state`() {
        // given
        mockUri()
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnVideoSelected(testUri))
        // when
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnVideoUploadCanceled)

        // then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.showVideoUploadDialog)
        assertEquals(null, uiState.videoUri)
    }

    @Test
    fun `onEvent OnDismissSuccessMessage SHOULD hide success message`() {
        // given
        viewModel.onEvent(ProductDetailViewModel.UserEvent.OnDismissSuccessMessage)

        // then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.showSuccessMessage)
    }

    private fun mockUri() {
        val testFileName = "test_video.mp4"
        coEvery { contextProvider.resolveFileFromUri(testUri) } returns Result.success(ByteArray(0) to testFileName)
        every { testUri.lastPathSegment } returns testFileName
    }

    companion object {
        private const val TEST_FILE_NAME = "test_video.mp4"
    }
}
