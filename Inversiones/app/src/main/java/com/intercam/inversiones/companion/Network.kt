package com.intercam.inversiones.companion

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.util.Log

class Network {
    companion object {
        private val TAG: String =
            Network::class.java.getSimpleName()
        fun verifyMetwork(context: Context): Boolean {
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
                return getNetworkCapabilities(activeNetwork)?.run {

                    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                   // Log.i(TAG,"si es vpn : "+ NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                    when {

                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        activeNetwork?.isConnectedOrConnecting == true -> true
                        else -> false
                    }
                } ?: false
            }
            Log.i("NETWORK","conectando")
        }

        fun typeNetwork(context: Context): String {
            var res ="No hay conexion"

            val cm: ConnectivityManager
            val ni: NetworkInfo?
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ni = cm.activeNetworkInfo
            var tipoConexion1 = false
            var tipoConexion2 = false

            if (ni != null) {
                val connManager1 =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val connManager2 =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (mWifi!!.isConnected) {
                    res = "wifi"
                }
                if (mMobile!!.isConnected) {
                    res = "datos"
                }

            } else {
                /* No estas conectado a internet */
                res = "No tienes internet"
            }
            return res
        }

    }
}
