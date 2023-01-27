package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token


private const val BASE_URL = BuildConfig.BASE_URL
private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
private val authInterceptor =  Interceptor{ chain->
    val request = AppAuth.getInstance().data.value?.token?.let {
        chain.request()
            .newBuilder()
            .addHeader("Authorization", it)
            .build()
    } ?: chain.request()

    chain.proceed(request)
}
private val client=OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor(authInterceptor)
    .build()

private val retrofit=Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface ApiService {

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken): Response<Unit>

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id:Long):Response<List<Post>>

    @GET("posts/{id}")
    fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long):Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id:Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id:Long): Response<Post>

    @FormUrlEncoded
    @POST ("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass:String): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(@Field("login") login: String, @Field("pass") pass: String, @Field("name") name: String): Response<Token>
}
object PostsApi {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}