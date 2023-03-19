package com.intercam.inversiones.dto

import com.google.gson.annotations.SerializedName

data class UsuarioBody(
                        val username: String,
                       val password:String,
                       val cuenta: String,
                       val telefono: String,
                       val montoMaximo:Int,
                       val moneda:String
                       )

