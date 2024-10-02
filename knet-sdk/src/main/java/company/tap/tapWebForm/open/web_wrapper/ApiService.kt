package company.tap.tapWebForm.open.web_wrapper

import androidx.annotation.RestrictTo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
object ApiService {
  const val BASE_URL = "https://mw-sdk.dev.tap.company/v2/checkout/"

  interface TapKnetSDKConfigUrls {
    @GET("/mobile/benefitpay/1.0.0/base_url.json")
    suspend fun getSDKConfigUrl(): TapKnetSDKConfigUrlResponse


  }

  object RetrofitClient {
    private const val BASE_URL1 = "https://tap-sdks.b-cdn.net"

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
        .baseUrl(BASE_URL1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

  }
  object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      println("Outgoing request to>>> ${request.url}")
      return chain.proceed(request)
    }
  }
}