package com.uniandes.medisupply.presentation.containers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.presentation.navigation.ProductDestination
import com.uniandes.medisupply.presentation.navigation.navhost.ProductNavHost
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ComposableActivity : BaseActivity(), KoinComponent {

    private val internalNavigator: InternalNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flow = intent.getParcelableExtra(KEY_FLOW, ComposableFlow::class.java)
        if (flow == null) {
            finish()
            return
        }
        setContent {
            val navController = rememberNavController()
            internalNavigator.init(navController, this)
            when (flow) {
                is ComposableFlow.ProductFlow -> {
                    internalNavigator.addParams(mapOf(
                        ProductDestination.ProductList.IS_STANDALONE to true
                    ))
                    ProductNavHost(navHostController = navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        internalNavigator.clear()
    }

    companion object {
        private const val KEY_FLOW = "KEY_FLOW"
        fun createIntent(context: Context, flow: ComposableFlow): Intent {
            return Intent(context, ComposableActivity::class.java).apply {
                putExtra(KEY_FLOW, flow)
            }
        }
    }
}

@Parcelize
sealed class ComposableFlow : Parcelable {
    data object ProductFlow : ComposableFlow()
}
