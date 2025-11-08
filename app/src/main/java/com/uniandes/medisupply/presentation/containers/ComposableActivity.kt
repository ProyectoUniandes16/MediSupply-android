package com.uniandes.medisupply.presentation.containers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.common.InternalNavigator
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ComposableActivity : BaseActivity(), KoinComponent {

    private val internalNavigator: InternalNavigator by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composableContent = composableContent
        setContent {
            val navController = rememberNavController()
            internalNavigator.init(navController, this)
            if (composableContent != null) {
                composableContent(navController)
            } else {
                Text("Error: No Composable content provided.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            composableContent = null
        }
    }

    companion object {

        var composableContent: (@Composable (navController: NavController?) -> Unit)? = null

        fun createIntent(context: Context, content: @Composable (navController: NavController?) -> Unit): Intent {
            composableContent = content
            return Intent(context, ComposableActivity::class.java)
        }
    }
}
