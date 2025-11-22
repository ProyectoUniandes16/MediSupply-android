package com.uniandes.medisupply.presentation.viewmodel.product

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.ContextProvider
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.navigation.ProductDestination.ProductDetail.PRODUCT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val showError: Boolean = false,
    val product: ProductUI,
    val showVideoUploadDialog: Boolean = false,
    val videoUri: Uri? = null,
    val videoFileName: String? = null,
    val isUploading: Boolean = false,
    val description: String? = null,
    val showSuccessMessage: Boolean = false,
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val internalNavigator: InternalNavigator,
    private val contextProvider: ContextProvider,
) : ViewModel() {

    private val product: ProductUI = internalNavigator.getParam(PRODUCT) as? ProductUI ?: run {
        throw IllegalArgumentException("ProductDetailViewModel requires a ProductUI parameter")
    }
    private val _uiState = MutableStateFlow(
        ProductDetailState(
            isLoading = true,
            product = product
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadProductDetail()
    }

    private fun loadProductDetail() {
        _uiState.update {
            it.copy(isLoading = true, error = null)
        }
        viewModelScope.launch {
            val result = productRepository.getProductById(product.id)
            result.onSuccess {
                val productUI = ProductUI.fromDomain(it)
                _uiState.update { state ->
                    state.copy(isLoading = false, product = productUI)
                }
            }
            result.onFailure {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = it.message)
                }
            }
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnBackClicked -> {
                internalNavigator.stepBack()
            }
            is UserEvent.OnVideoSelected -> {
                _uiState.update { state ->
                    state.copy(
                        showVideoUploadDialog = true,
                        videoUri = event.uri,
                        videoFileName = getFileName(event.uri)
                    )
                }
            }
            is UserEvent.OnVideoUploadConfirmed -> {
                uploadVideo()
            }
            is UserEvent.OnVideoUploadCanceled -> {
                _uiState.update { state ->
                    state.copy(
                        showVideoUploadDialog = false,
                        videoUri = null
                    )
                }
            }
            is UserEvent.OnDismissError -> {
                _uiState.update { state ->
                    state.copy(
                        showError = false,
                        error = null
                    )
                }
            }
            is UserEvent.OnDismissSuccessMessage -> {
                _uiState.update { state ->
                    state.copy(
                        showSuccessMessage = false
                    )
                }
            }
            is UserEvent.OnDescriptionChanged -> {
                _uiState.update { state ->
                    state.copy(
                        description = event.description
                    )
                }
            }
        }
    }

    private fun uploadVideo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showVideoUploadDialog = false,
                    isUploading = true
                )
            }
            uiState.value.videoUri?.let { videoUri ->
                val result = contextProvider.resolveFileFromUri(videoUri)
                result.onSuccess {
                    val uploadResult = productRepository.uploadProductVideo(
                        id = product.id,
                        fileName = uiState.value.videoFileName ?: "video_file",
                        fileBytes = it.first,
                        mediaType = it.second,
                        description = uiState.value.description ?: "Video upload"
                    )
                    if (uploadResult.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                videoUri = null,
                                showSuccessMessage = true
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isUploading = false,
                                showError = true,
                                error = uploadResult.exceptionOrNull()?.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        return uri.lastPathSegment?.substringAfterLast('/') ?: "unknown_file"
    }

    sealed class UserEvent {
        data object OnBackClicked : UserEvent()
        data class OnVideoSelected(val uri: Uri) : UserEvent()
        data object OnVideoUploadConfirmed : UserEvent()
        data object OnVideoUploadCanceled : UserEvent()
        data object OnDismissError : UserEvent()
        data object OnDismissSuccessMessage : UserEvent()
        data class OnDescriptionChanged(val description: String) : UserEvent()
    }
}
