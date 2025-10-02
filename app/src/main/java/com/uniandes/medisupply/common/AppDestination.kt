package com.uniandes.medisupply.common

import androidx.core.bundle.Bundle

sealed class AppDestination(val extras: Bundle = Bundle()) {
    class HomeClient(bundle: Bundle = Bundle()) : AppDestination(bundle)
}