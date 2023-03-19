package com.intercam.inversiones.dto

data class compraDivisas(

    val divisaBase: String,
    val divisaDestino: String,
    val usuario: String,
    val montoCompra: Float,
    val tasaBase: Float

)
