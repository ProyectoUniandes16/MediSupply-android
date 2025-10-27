package com.uniandes.medisupply.data.remote.model.common

import kotlinx.serialization.Serializable

@Serializable
data class DataResponse<T>(val data: T)
