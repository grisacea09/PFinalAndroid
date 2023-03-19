package com.intercam.inversiones.rest

import android.location.Location
import com.intercam.inversiones.data.database.model.Divisas
import com.intercam.inversiones.data.database.model.Serie
import com.intercam.inversiones.dto.*
import retrofit2.Call
import retrofit2.http.*


interface ManagerRest {


        @POST("sendInsertaUsuario")
        fun sendInsertaUsuario(@Body String : UsuarioBody) : Call<UsuarioResponseBody>

        @POST("sendBuscaUsuario")
        fun sendBuscaUsuario(@Body pwd: UsuarioPwd): Call<UsuarioResponseBody>

        @POST("sendInsertaCompraDivisas")
        fun sendInsertaCompraDivisa(@Body compraDivisa: compraDivisas): Call<UsuarioResponseBody>


        @GET("vestimenta/divisas_list")
        fun getDivisas(): Call<ArrayList<Divisas>>

        //deberia ir tambien la fecha inicio y final
        @GET("vestimenta/series_list")
        fun getSerie(): Call<Serie>

        @GET("vestimenta/serie_kpi")
        fun getSerieKPI(): Call<Serie>

        @POST("sendBuscaCompraDivisas")
        fun sendBuscaCompraDivisas(@Body upwd : UsuarioPwd): Call<ArrayList<ResponseDiv>>

        @POST("getLocations")
        fun getLocations(): Call<ArrayList<Locations>>

        @GET("timeseries")
        fun getSerie2(@Query("start_date") start_date: String, @Query("end_date") end_date: String, @Query("base") base: String,
                      @Query("rates") rates: String): Call<Serie>


    }


