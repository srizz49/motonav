package com.example.motonav.data.repository

import com.example.motonav.data.local.DataStoreManager
import com.example.motonav.data.model.User
import com.example.motonav.data.remote.ApiClient
import com.example.motonav.data.remote.MotoNavApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val dataStoreManager: DataStoreManager,
    private val apiService: MotoNavApiService = ApiClient.apiService
) {

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    val isLoggedIn: Flow<Boolean> = dataStoreManager.isLoggedInFlow
    val userIdFlow: Flow<String> = dataStoreManager.userIdFlow

    suspend fun login(email: String, password: String): Result<User> {
        if (email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Invalid credentials. Password must be at least 6 characters."))
        }
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val user = response.body()!!.data!!
                _currentUser.value = user
                dataStoreManager.setLoggedIn(true, user.name, user.email, user.id)
                Result.success(user)
            } else {
                val msg = response.body()?.message ?: "Login failed. Please check credentials."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Server connection failed. Ensure backend server is running."
            Result.failure(Exception(msg))
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<User> {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Please provide valid registration details (min 6 chars password)."))
        }
        return try {
            val response = apiService.register(mapOf("name" to name, "email" to email, "password" to password))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val user = response.body()!!.data!!
                _currentUser.value = user
                dataStoreManager.setLoggedIn(true, user.name, user.email, user.id)
                Result.success(user)
            } else {
                val msg = response.body()?.message ?: "Sign up failed."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Server connection failed. Ensure backend server is running."
            Result.failure(Exception(msg))
        }
    }

    suspend fun logout() {
        dataStoreManager.setLoggedIn(false)
        _currentUser.value = User()
    }
}
