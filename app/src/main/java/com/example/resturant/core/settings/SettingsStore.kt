package com.example.resturant.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    private val KEY_ONBOARDING = booleanPreferencesKey("onboarding_done")
    private val KEY_SETUP = booleanPreferencesKey("setup_done")
    private val KEY_RESTAURANT = stringPreferencesKey("restaurant_name")
    private val KEY_PIN = stringPreferencesKey("pin")

    val onboardingDone: Flow<Boolean> =
        context.settingsDataStore.data.map { it[KEY_ONBOARDING] ?: false }
    val setupDone: Flow<Boolean> =
        context.settingsDataStore.data.map { it[KEY_SETUP] ?: false }
    val restaurantName: Flow<String> =
        context.settingsDataStore.data.map { it[KEY_RESTAURANT] ?: "" }
    val pin: Flow<String> =
        context.settingsDataStore.data.map { it[KEY_PIN] ?: "" }

    suspend fun setOnboardingDone() {
        context.settingsDataStore.edit { it[KEY_ONBOARDING] = true }
    }

    suspend fun completeSetup(restaurantName: String, pin: String) {
        context.settingsDataStore.edit {
            it[KEY_RESTAURANT] = restaurantName
            it[KEY_PIN] = pin
            it[KEY_SETUP] = true
        }
    }

    suspend fun updateRestaurantName(name: String) {
        context.settingsDataStore.edit { it[KEY_RESTAURANT] = name }
    }

    suspend fun updatePin(newPin: String) {
        context.settingsDataStore.edit { it[KEY_PIN] = newPin }
    }
}