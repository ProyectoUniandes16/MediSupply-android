package com.uniandes.medisupply.presentation.containers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.model.UserRole
import com.uniandes.medisupply.presentation.navigation.navhost.HomeClientNavHost
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class HomeClientActivity : BaseActivity() {

    private val snackbarHostState = SnackbarHostState()
    private val userDataProvider: UserDataProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val role: UserRole
        try {
            role = UserRole.fromDisplayName(userDataProvider.getRole()) ?: throw Exception("Invalid role")
        } catch (e: Exception) {
            userDataProvider.clearAll()
            finish()
            return
        }
        setContent {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                HomeClientNavHost(
                    modifier = Modifier.padding(paddingValues),
                    isVendor = role == UserRole.VENDOR
                )
            }
        }
    }

    // Show a snackbar when a child activity returns a result (e.g. NewClient)
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var textToShow: String? = null
        when (requestCode) {
            AppDestination.NewClient.REQUEST_CODE -> if (resultCode == RESULT_OK) {
                textToShow = getString(R.string.client_created)
            }
            AppDestination.NewOrder.REQUEST_CODE -> if (resultCode == RESULT_OK) {
                textToShow = getString(R.string.order_created)
            }
            else -> {}
        }

        textToShow?.let { message ->
            lifecycleScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    companion object {
        const val USER_KEY = "user"
        fun createIntent(context: Context): Intent {
            return Intent(context, HomeClientActivity::class.java)
        }
    }
}
