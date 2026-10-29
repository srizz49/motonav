package com.example.motonav.data.repository

import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val dataStoreManager: DataStoreManager) {

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    val isLoggedIn: Flow<Boolean> = dataStoreManager.isLoggedInFlow

    suspend fun login(email: String, password: String): Result<User> {
        delay(1000) // Simulate network call
        if (email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Invalid credentials. Password must be at least 6 characters."))
        }
        val user = User(
            email = email,
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        )
        _currentUser.value = user
        dataStoreManager.setLoggedIn(true, user.name, user.email)
        return Result.success(user)
    }

    suspend fun signUp(name: String, email: String, password: String): Result<User> {
        delay(1000) // Simulate network call
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Please provide valid registration details."))
        }
        val user = User(name = name, email = email)
        _currentUser.value = user
        dataStoreManager.setLoggedIn(true, user.name, user.email)
        return Result.success(user)
    }

    suspend fun loginDemoMode(): User {
        val demoUser = User(name = "Demo Rider", email = "demo@motonav.com")
        _currentUser.value = demoUser
        dataStoreManager.setLoggedIn(true, demoUser.name, demoUser.email)
        return demoUser
    }

    suspend fun logout() {
        dataStoreManager.setLoggedIn(false)
        _currentUser.value = User()
    }
}
