package com.example.motonav.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "motonav_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_PETROL_PRICE_INR = doublePreferencesKey("petrol_price_inr")
        val KEY_DEFAULT_BIKE_ID = stringPreferencesKey("default_bike_id")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] ?: ""
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: ""
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val petrolPriceFlow: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[KEY_PETROL_PRICE_INR] ?: 104.50
    }

    val defaultBikeIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_BIKE_ID] ?: ""
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, name: String = "", email: String = "", userId: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = isLoggedIn
            if (name.isNotEmpty()) prefs[KEY_USER_NAME] = name
            if (email.isNotEmpty()) prefs[KEY_USER_EMAIL] = email
            if (userId.isNotEmpty()) prefs[KEY_USER_ID] = userId
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setPetrolPrice(price: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PETROL_PRICE_INR] = price
        }
    }

    suspend fun setDefaultBikeId(bikeId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_BIKE_ID] = bikeId
        }
    }
}
