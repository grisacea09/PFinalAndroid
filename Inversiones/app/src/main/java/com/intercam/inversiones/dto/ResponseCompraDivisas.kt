package com.intercam.inversiones.dto

import com.google.gson.annotations.SerializedName

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*



@Serializable
data class ResponseDiv (
    val id: String,
    val divisaBase: String,
    val divisaDestino: String,
    val usuario: String,
    val montoCompra: Long,
    val tasaBase: Double
)

