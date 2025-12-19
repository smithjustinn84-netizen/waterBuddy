package com.example.demometro

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
class HomeViewModel(
    private val greeting: Greeting
) : ViewModel() {
    private val _greetingText = MutableStateFlow("")
    val greetingText = _greetingText.asStateFlow()

    fun loadGreeting() {
        _greetingText.value = greeting.greet()
    }
}

