package com.example.demometro

import androidx.compose.ui.window.ComposeUIViewController
import com.example.demometro.di.IosAppComponent
import dev.zacsweers.metro.createGraphFactory

fun MainViewController() = ComposeUIViewController {
    val component = createGraphFactory<IosAppComponent.Factory>().create()
    App(component)
}
