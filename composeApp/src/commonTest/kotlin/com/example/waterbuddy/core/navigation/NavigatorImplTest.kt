package com.example.waterbuddy.core.navigation

import app.cash.turbine.test
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class NavigatorImplTest : ShouldSpec({
    val navigator = NavigatorImpl()

    should("emit NavigateTo command when navigate is called") {
        navigator.commands.test {
            navigator.navigate(WaterTracker)
            awaitItem() shouldBe NavigationCommand.NavigateTo(WaterTracker, clearBackStack = false)
        }
    }

    should("emit NavigateTo command with clearBackStack true when navigate is called with clearBackStack = true") {
        navigator.commands.test {
            navigator.navigate(HydrationInsights, clearBackStack = true)
            awaitItem() shouldBe NavigationCommand.NavigateTo(
                HydrationInsights,
                clearBackStack = true
            )
        }
    }

    should("emit NavigateUp command when goBack is called") {
        navigator.commands.test {
            navigator.goBack()
            awaitItem() shouldBe NavigationCommand.NavigateUp
        }
    }
})
