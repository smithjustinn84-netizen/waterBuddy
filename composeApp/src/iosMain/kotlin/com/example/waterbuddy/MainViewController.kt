package com.example.waterbuddy

import androidx.compose.ui.window.ComposeUIViewController
import com.example.waterbuddy.di.IosAppGraph
import dev.zacsweers.metro.createGraphFactory

fun MainViewController() = ComposeUIViewController {
    val component = createGraphFactory<IosAppGraph.Factory>().create()
    App(component)
}
