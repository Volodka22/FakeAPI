package com.example.fakeapi.retrofit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FakeService {
    @GET("posts")
    suspend fun listPosts(): Response<List<Post>>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<ResponseBody>

    @POST("posts")
    suspend fun createPost(@Body post: Post) : Response<ResponseBody>
}