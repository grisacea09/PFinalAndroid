package com.intercam.inversiones.companion


import android.util.Log
import com.google.gson.GsonBuilder
import com.intercam.inversiones.BuildConfig
import com.intercam.inversiones.dto.UsuarioBody
import com.intercam.inversiones.dto.UsuarioResponseBody
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.fragments.RegisterFragment
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit


class Manager {

    companion object {
        private val TAG = "Resposnse service"
        var responseService: String = ""

         fun generateService(): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
                .addInterceptor ( Interceptor {chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("Content-Type", "application/json")
                    builder.header("Accept", "application/json");
                    return@Interceptor chain.proceed(builder.build())
                })
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

            return retrofit
        }

        fun generateServiceDivisas(): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
                .addInterceptor ( Interceptor {chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("Content-Type", "application/json")
                    builder.header("Accept", "application/json")
                    return@Interceptor chain.proceed(builder.build())
                })


                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.DIVISAS_API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

            return retrofit
        }

        fun generateServiceAPI(): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

                .addInterceptor ( Interceptor {chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("Accept", "application/json")
                    builder.header("apikey", "xnbdxk7XVYtqUOm3TnJtSnaUUESc4ayI")

                    return@Interceptor chain.proceed(builder.build())
                })

                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.apilayer.com/exchangerates_data/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()

            return retrofit
        }







    }


}