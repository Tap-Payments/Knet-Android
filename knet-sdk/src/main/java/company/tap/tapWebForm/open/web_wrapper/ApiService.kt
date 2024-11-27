package company.tap.tapWebForm.open.web_wrapper

import androidx.annotation.RestrictTo
import company.tap.tapWebForm.open.web_wrapper.model.TapButtonSDKConfigUrlResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
object ApiService {
  const val BASE_URL = "https://mw-sdk.dev.tap.company/v2/checkout/"
   var BASE_URL_1 = "https://mw-sdk.dev.tap.company/v2/checkout/"

  interface TapButtonSDKConfigUrls {
    @GET("/mobile/card/1.0.0/base_url.json")
    suspend fun getButtonSDKConfigUrl(): TapButtonSDKConfigUrlResponse


  }

  object RetrofitClient {
    var BASE_URL = "https://tap-assets.b-cdn.net"
    val okHttpClient = OkHttpClient()
      .newBuilder()
      .connectTimeout(2, TimeUnit.SECONDS)
      .writeTimeout(2, TimeUnit.SECONDS)
      .readTimeout(2, TimeUnit.SECONDS)
      .addInterceptor(RequestInterceptor)
      .build()

    fun getClient(): Retrofit =
      Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL_1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    object RequestInterceptor : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        println("Outgoing request to ${request.url}")
        return chain.proceed(request)
      }
    }
  }
}