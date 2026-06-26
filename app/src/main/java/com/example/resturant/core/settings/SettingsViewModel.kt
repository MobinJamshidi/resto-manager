package com.example.resturant.core.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val store = SettingsStore(application)

    val onboardingDone: StateFlow<Boolean?> =
        store.onboardingDone.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val setupDone: StateFlow<Boolean?> =
        store.setupDone.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val restaurantName: StateFlow<String> =
        store.restaurantName.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val pin: StateFlow<String> =
        store.pin.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun setOnboardingDone() = viewModelScope.launch { store.setOnboardingDone() }
    fun completeSetup(name: String, pin: String) =
        viewModelScope.launch { store.completeSetup(name.trim(), pin) }
    fun updateRestaurantName(name: String) = viewModelScope.launch { store.updateRestaurantName(name.trim()) }
    fun updatePin(newPin: String) = viewModelScope.launch { store.updatePin(newPin) }
}