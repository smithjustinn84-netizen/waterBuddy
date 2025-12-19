package com.example.demometro

import androidx.compose.ui.window.ComposeUIViewController
import com.example.demometro.di.IosAppComponent
import dev.zacsweers.metro.createGraph

fun MainViewController() = ComposeUIViewController {
    val component = createGraph<IosAppComponent.Factory>().create()
    App(component)
}
