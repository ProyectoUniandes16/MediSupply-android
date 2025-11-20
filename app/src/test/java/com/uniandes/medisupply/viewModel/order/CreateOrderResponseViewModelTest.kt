package com.uniandes.medisupply.viewModel.order

import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.model.toUi
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateOrderResponseViewModelTest {
    private lateinit var viewModel: CreateOrderViewModel
    private val internalNavigator: InternalNavigator = mockk(relaxed = true)
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        every { internalNavigator.getParam(Destination.CreateOrder.CLIENT) } returns CLIENT
        viewModel = CreateOrderViewModel(
            internalNavigator = internalNavigator,
            productRepository = productRepository,
            orderRepository = orderRepository
        )
    }

    @Test
    fun `onAddProductClicked SHOULD load products`() = runTest {
        // GIVEN
        mockProducts()

        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnAddProductClicked)

        // THEN
        assertEquals(true, viewModel.uiState.value.showProductBottomSheet)
        assertEquals(PRODUCT_UI_LIST.sortedBy { p -> p.name }, viewModel.uiState.value.productList)
    }

    @Test
    fun `onBackClicked SHOULD navigate back`() = runTest {
        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnBackClicked)

        // THEN
        coEvery { internalNavigator.stepBack() }
    }

    @Test fun `OnDismissProductBottomSheet SHOULD hide product bottom sheet`() = runTest {
        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnDismissProductBottomSheet)

        // THEN
        assertEquals(false, viewModel.uiState.value.showProductBottomSheet)
    }

    @Test
    fun `OnConfirmClicked SHOULD call confirmOrder`() = runTest {
        // GIVEN
        val order = Order(
            clientId = CLIENT.id,
            products = PRODUCT_LIST.filter { it.id % 2 == 0 }.map {
                it to 1
            },
            total = 0.0
        )
        coEvery { orderRepository.placeOrder(order) } returns Result.success(Unit)

        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnConfirmClicked)

        // THEN
        verify { internalNavigator.finishCurrentDestination(success = true) }
    }

    @Test
    fun `OnConfirmClicked SHOULD change state to confirmation`() {
        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnCompleteClicked)

        // THEN
        assertTrue(viewModel.uiState.value.isConfirmation)
    }

    @Test
    fun `OnEditOrderClicked SHOULD change state to not confirmation`() {
        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnEditOrderClicked)

        // THEN
        assertEquals(false, viewModel.uiState.value.isConfirmation)
    }

    @Test
    fun `OnProductSelected SHOULD add product to order`() = runTest {
        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(PRODUCT_UI))

        // THEN
        assertTrue(viewModel.uiState.value.productOrder.contains(Pair(PRODUCT_UI, 1)))
    }

    @Test
    fun `OnIncreaseQuantityClicked SHOULD sum 1 to product quantity in order`() = runTest {
        // GIVEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(PRODUCT_UI))

        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnIncreaseQuantityClicked(PRODUCT_UI))

        // THEN
        val quantity = viewModel.uiState.value.productOrder.first { it.first.id == PRODUCT_UI.id }.second
        assertEquals(2, quantity)
    }

    @Test
    fun `OnDecreaseQuantityClicked SHOULD substract 1 to product quantity in order`() = runTest {
        // GIVEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(PRODUCT_UI))
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnIncreaseQuantityClicked(PRODUCT_UI))

        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnDecreaseQuantityClicked(PRODUCT_UI))

        // THEN
        val quantity = viewModel.uiState.value.productOrder.first { it.first.id == PRODUCT_UI.id }.second
        assertEquals(1, quantity)
    }

    @Test
    fun `OnDecreaseQuantityClicked SHOULD remove product from order if quantity is 1`() = runTest {
        // GIVEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(PRODUCT_UI))

        // WHEN
        viewModel.onEvent(CreateOrderViewModel.UserEvent.OnDecreaseQuantityClicked(PRODUCT_UI))

        // THEN
        val containsProduct = viewModel.uiState.value.productOrder.any { it.first.id == PRODUCT_UI.id }
        assertEquals(false, containsProduct)
    }

    private fun mockProducts(result: Result<List<Product>> = Result.success(PRODUCT_LIST)) {
        coEvery { productRepository.getProducts() } returns result
    }

    companion object {
        private val CLIENT = Client(
            id = 1,
            name = "Client 1",
            contactInfo = ClientContactInfo(
                email = "",
                phone = "",
                position = "-",
                name = ""
            ),
            address = "Address 1",
            email = ""
        )
        private val PRODUCT = Product(
            id = 1,
            name = "Product 1",
            price = 10.0,
            stock = 100,
            category = "Category 1",
            stockStatus = StockStatus.IN_STOCK
        )

        private val PRODUCT_UI = PRODUCT.toUi()

        private val PRODUCT_LIST = List(10) {
            PRODUCT.copy(id = it + 1, name = "Product ${it + 1}")
        }

        private val PRODUCT_UI_LIST = PRODUCT_LIST.map { it.toUi() }
    }
}
