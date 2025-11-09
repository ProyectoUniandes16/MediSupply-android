package com.uniandes.medisupply.common

import android.content.Context
import android.net.Uri

interface ContextProvider {
    suspend fun resolveFileFromUri(uri: Uri): Result<Pair<ByteArray, String?>>
}

class  ContextProviderImpl(
    private val context: Context
) : ContextProvider {

    override suspend fun resolveFileFromUri(uri: Uri): Result<Pair<ByteArray, String?>> {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val fileBytes = inputStream.readBytes()
            val mimeType = context.contentResolver.getType(uri)
            return Result.success(fileBytes to mimeType)
        }
        return Result.failure(Exception("Unable to open URI: $uri"))
    }
}