package com.example.demometro

import dev.zacsweers.metro.Inject

@Inject
class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}