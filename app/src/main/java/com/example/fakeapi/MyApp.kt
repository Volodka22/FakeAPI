package com.example.fakeapi

import android.app.Application
import android.widget.Toast
import com.example.fakeapi.retrofit.FakeService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit


class MyApp : Application() {

    lateinit var postService: FakeService

    override fun onCreate() {
        super.onCreate()
        instance = this

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .build()


        val retrofitPosts = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))

            .client(client)
            .build()

        postService = retrofitPosts.create(FakeService::class.java)
    }

    companion object {
        lateinit var instance: MyApp
            private set
    }
}