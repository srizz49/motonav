package com.example.motonav.data.remote

import com.example.motonav.data.model.Bike
import com.example.motonav.data.model.PhotoPost
import com.example.motonav.data.model.RideHistory
import com.example.motonav.data.model.ScenicPlace
import com.example.motonav.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MotoNavApiService {

    @POST("api/auth/login.php")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @POST("api/auth/register.php")
    suspend fun register(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @GET("api/profile/get.php")
    suspend fun getProfile(@Query("user_id") userId: String): Response<ApiResponse<User>>

    @POST("api/profile/update.php")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<ApiResponse<Unit>>

    @GET("api/bikes/index.php")
    suspend fun getBikes(@Query("user_id") userId: String): Response<ApiResponse<List<Bike>>>

    @POST("api/bikes/index.php")
    suspend fun addBike(
        @Query("user_id") userId: String,
        @Body bike: Bike
    ): Response<ApiResponse<Bike>>

    @DELETE("api/bikes/index.php")
    suspend fun deleteBike(
        @Query("user_id") userId: String,
        @Query("id") bikeId: String
    ): Response<ApiResponse<Unit>>

    @GET("api/places/index.php")
    suspend fun getScenicPlaces(
        @Query("user_id") userId: String,
        @Query("category") category: String = "All",
        @Query("q") query: String = ""
    ): Response<ApiResponse<List<ScenicPlace>>>

    @POST("api/places/toggle_save.php")
    suspend fun toggleSavePlace(@Body body: Map<String, String>): Response<ApiResponse<ToggleSaveResponse>>

    @GET("api/routes/index.php")
    suspend fun getRoutes(): Response<ApiResponse<RouteResponse>>

    @GET("api/rides/index.php")
    suspend fun getRideHistory(@Query("user_id") userId: String): Response<ApiResponse<List<RideHistory>>>

    @POST("api/rides/index.php")
    suspend fun recordRide(
        @Query("user_id") userId: String,
        @Body ride: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<RideHistory>>

    @GET("api/photos/index.php")
    suspend fun getPhotos(): Response<ApiResponse<List<PhotoPost>>>

    @Multipart
    @POST("api/photos/upload.php")
    suspend fun uploadPhoto(
        @Part("user_id") userId: RequestBody,
        @Part("caption") caption: RequestBody,
        @Part("locationName") locationName: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<PhotoPost>>

    @GET("api/fuel/stations.php")
    suspend fun getFuelData(): Response<ApiResponse<FuelResponse>>
}

