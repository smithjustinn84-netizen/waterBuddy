package com.example.demometro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform