package com.intercam.inversiones.vistas.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intercam.inversiones.R
import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.data.database.model.Divisas
import com.intercam.inversiones.data.database.model.Serie
import com.intercam.inversiones.databinding.FragmentCompraBinding
import com.intercam.inversiones.dto.UsuarioResponseBody
import com.intercam.inversiones.dto.compraDivisas
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.activities.MainActivity
import com.intercam.inversiones.vistas.activities.MenuActivity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class compraFragment : Fragment() {

    private lateinit var binding: FragmentCompraBinding
    private val TAG = "CompraFragment"
    private lateinit var bottomNavigationView: BottomNavigationView


    var divisaBase = "MXN"
    var divisaDestino = ""
    var montoCompra = 0.0f
    var tasaBase = 0.0f

    var startDate=""
    var endDate=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCompraBinding.inflate(layoutInflater)

        val user = MainActivity.user

        if (user != null) {
            Log.i(TAG,"usuario no encontrado")
        }
        else{
            Log.i(TAG,"usuario  encontrado"+ user)
        }
        binding.navView.background = null
        bottomNavigationView = binding.navView

        val navController: NavController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_main
        )

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        NavigationUI.setupActionBarWithNavController(requireActivity() as AppCompatActivity, navController);
        //hay que mandar llamar al servicio y que llene horizontalscrollview




        if (Network.verifyMetwork(requireContext())) {
            CoroutineScope(Dispatchers.IO).launch {
                downloadImage()
            }

        }

        divisaBase = "MXN"

        Log.i("quien: ",""+binding.layBpound.getChildAt(0))

        binding.layBpound.setOnClickListener{
            Log.i("-->>:","click")
            Log.i("-->>:","click"+binding.layBpound.get(1).id)

            // tendria que cambiar de color el seleccionado
            //hacer la peticion para traer el json con los datos para la serie de tiempo


        }

        binding.imgBpound.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "GBP"
            binding.layBpound.background = resources.getDrawable(R.drawable.gradient_bottom_nav)
            if (Network.verifyMetwork(requireContext())) {
                if(Network.typeNetwork(requireContext()) == "datos"){
                    AlertDialog.Builder(requireContext())
                        .setTitle("Estas usando datos celulares")
                        .setMessage("Es necesario usar wifi")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
                else if(Network.typeNetwork(requireContext()) == "wifi") {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "GBP"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()


                        withContext(Dispatchers.Main) {

                            delay(6000)
                            drawFunction(listEntradas2)
                            drawTimeSerie(listValues)


                        }

                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgbrasilreep.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "BRL"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
            if (Network.verifyMetwork(requireContext())) {

                CoroutineScope(Dispatchers.IO).launch {
                    divisaDestino = "BRL"

                    var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                    var listEntradas2: MutableList<Entry> =
                        async { downloadSerie2(divisaDestino) }.await()

                    withContext(Dispatchers.Main) {
                        delay(6000)
                        drawTimeSerie(listValues)
                        drawFunction(listEntradas2)

                    }

                }//corutina1
            }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El servidor esta fuera de servicio")
                    .setMessage("Comunicate con tu operador")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener


        binding.imgcanadianDollar.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "CAD"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
            if (Network.verifyMetwork(requireContext())) {

                CoroutineScope(Dispatchers.IO).launch {

                    divisaDestino = "CAD"
                    var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                    var listEntradas2: MutableList<Entry> =
                        async { downloadSerie2(divisaDestino) }.await()

                    withContext(Dispatchers.Main) {
                        delay(6000)
                        drawTimeSerie(listValues)
                        drawFunction(listEntradas2)

                    }

                }//corutina1
            }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El servidor esta fuera de servicio")
                    .setMessage("Comunicate con tu operador")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener
        binding.imgdolar.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "USD"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "USD"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }

                    }//corutina1

                }//fin if network
            }
            else{

            }
        }//fin onclick listener

        binding.imgegyptPound.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "EGP"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "EGP"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }

                    }//corutina1

                }//fin if network
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgeuro.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "EUR"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "EUR"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgindianRuppe.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "INR"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "INR"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgnkWon.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "KPW"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "KPW"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgskWon.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "KRW"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "KRW"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgpesoColombia.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "COP"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        divisaDestino = "COP"
                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgpesoCuba.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "CUP"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {


                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgkrona.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet

            divisaDestino = "SEK"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {

                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)

                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgrusian.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "RUB"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {


                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)


                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener

        binding.imgyuan.setOnClickListener {
            Log.i("-->>:", "click en la imagen")
            //hay que obtener la divisa que escogio el usuario
            //ir una por una que chafa
            //verificar si hay internet
            divisaDestino = "CNY"
            if(Network.typeNetwork(requireContext()) == "datos"){
                AlertDialog.Builder(requireContext())
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(requireContext()) == "wifi") {
                if (Network.verifyMetwork(requireContext())) {

                    CoroutineScope(Dispatchers.IO).launch {


                        var listValues: List<Float> = async { getSerieKpi(divisaDestino) }.await()
                        var listEntradas2: MutableList<Entry> =
                            async { downloadSerie2(divisaDestino) }.await()

                        withContext(Dispatchers.Main) {
                            delay(6000)
                            drawTimeSerie(listValues)
                            drawFunction(listEntradas2)


                        }
                    }//corutina1
                }//fin if network
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }//fin onclick listener




        binding.buttonCompra.setOnClickListener {
            Log.i(TAG, "click"+divisaDestino+"--"+divisaBase)
            //insertar la copra de la divisa
            //el monto no debe ser vacio y debe ser mayor a cero
             montoCompra = (binding.cantidad.text.toString()).toFloat()

            if (Network.verifyMetwork(requireContext())) {
                val cd = user?.let { it1 ->
                    compraDivisas(divisaBase, divisaDestino,
                        it1, montoCompra, tasaBase)
                }
                Log.i("buscarUsr", "hay internet"+divisaBase+" - "+ divisaDestino)

                val call = cd?.let { it1 ->
                    Manager.generateService().create(ManagerRest::class.java)
                        .sendInsertaCompraDivisa(it1)
                }

                if (call != null) {
                    call.enqueue(object : Callback<UsuarioResponseBody> {
                        override fun onResponse(
                            call: Call<UsuarioResponseBody>,
                            response: Response<UsuarioResponseBody>
                        ) {
                            Log.i("buscarUsr", "" + (response.code()))
                            if (response.body()?.response == "OK") {
                                //ir a la pagina de home

                                Log.i("Main", "ir al homefragment")
                                activity?.runOnUiThread(Runnable {

                                    AlertDialog.Builder(requireContext())
                                        .setTitle("La compra ha sido exitosa")
                                        .setMessage("Ir a home para ver detalle de la compra")
                                        .setCancelable(false)
                                        .setPositiveButton("Aceptar", null)
                                        .show()
                                    hideKeyboard()

                                })


                            }
                        }//fin corutina

                        override fun onFailure(call: Call<UsuarioResponseBody>, t: Throwable) {
                            Log.e("fail", t.localizedMessage)
                            ///mm algo raro con mongo
                            activity?.runOnUiThread(Runnable {

                                AlertDialog.Builder(requireContext())
                                    .setTitle("La compra ha sido exitosa")
                                    .setMessage("Ir a home para ver detalle de la compra")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", null)
                                    .show()
                                hideKeyboard()

                            })

                        }
                    })
                }
            }
        }


        return binding.root
    }


   private suspend fun downloadImage(){

       withContext(Dispatchers.IO) {

            val call = Manager.generateServiceDivisas().create(ManagerRest::class.java)
                .getDivisas()

            call.enqueue(object : Callback<ArrayList<Divisas>> {
                override fun onResponse(
                    call: Call<ArrayList<Divisas>>,
                    response: Response<ArrayList<Divisas>>
                ) {
                    Log.d(TAG, "respuesta del server: ${response.toString()}")
                    Log.d(TAG, "datos del server: ${response.body().toString()}")

                    var divisas = response.body()
                    if (divisas != null) {
                        for(item in divisas){
                            if(item.id == 1){
                                Glide.with(requireActivity().applicationContext)
                                    .load(item.imagen)
                                    .into(binding.imgBpound)
                                binding.bpound.text = item.divisa
                                binding.spound.text = item.simbolo
                            }
                            if(item.id==2){
                                Glide.with(requireActivity().applicationContext)
                                    .load(item.imagen)
                                    .into(binding.imgbrasilreep)
                                binding.tbrasilReep.text = item.divisa
                                binding.sbrasilReep.text = item.simbolo
                            }
                            if(item.id==3){
                                Glide.with(requireActivity().applicationContext)
                                    .load(item.imagen)
                                    .into(binding.imgcanadianDollar)
                                binding.tcanDolar.text = item.divisa
                                binding.scanadianDollar.text = item.simbolo
                            }
                            if(item.id==4){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgdolar)
                                binding.tdolar.text = item.divisa
                                binding.sdolar.text = item.simbolo
                            }
                            if(item.id==5){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgegyptPound)
                                binding.tegyptPound.text = item.divisa
                                binding.segyptPound.text = item.simbolo
                            }
                            if(item.id==6){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgeuro)
                                binding.teuro.text = item.divisa
                                binding.seuro.text = item.simbolo
                            }
                            if(item.id==7){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgindianRuppe)
                                binding.tindianRupee.text = item.divisa
                                binding.sindianRuppe.text = item.simbolo
                            }
                            if(item.id==8){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgnkWon)
                                binding.tnkWon.text = item.divisa
                                binding.snkWon.text = item.simbolo
                            }
                            if(item.id==9){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgskWon)
                                binding.tskWon.text = item.divisa
                                binding.sskWon.text = item.simbolo
                            }
                            if(item.id==10){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgpesoColombia)
                                binding.tpesoColomb.text = item.divisa
                                binding.spesoColombia.text = item.simbolo
                            }
                            if(item.id==11){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgpesoCuba)
                                binding.tpesoCuba.text = item.divisa
                                binding.spesoCuba.text = item.simbolo
                            }
                            if(item.id==12){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgkrona)
                                binding.tkrona.text = item.divisa
                                binding.skrona.text = item.simbolo
                            }
                            if(item.id==13){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgrusian)
                                binding.trusian.text = item.divisa
                                binding.srusian.text = item.simbolo
                            }
                            if(item.id==14){
                                Glide.with(requireContext())
                                    .load(item.imagen)
                                    .into(binding.imgyuan)
                                binding.tyuan.text = item.divisa
                                binding.syuan.text = item.simbolo
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ArrayList<Divisas>>, t: Throwable) {

                    Log.e(TAG,"error: ${t}")
                    Toast.makeText(
                        requireContext(),
                        "No hay conexion error: ${t}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }
    }




    private suspend fun downloadSerie2(rate: String): MutableList<Entry>  {

        divisaDestino = rate

        var listEntradas: MutableList<Entry> = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {

            val call =
                Manager.generateServiceAPI().create(ManagerRest::class.java).getSerie2("2023-03-01","2023-03-13",divisaBase, rate)

            call.enqueue(object : Callback<Serie> {
                override fun onResponse(
                    call: Call<Serie>,
                    response: Response<Serie>
                ) {
                    Log.i("respuesta serie22222", "" + response.body())
                    Log.i(" download serie divisa", "destino" + rate+ " - base:"+ divisaBase)

                    startDate = response.body()?.start_date.toString()
                    endDate = response.body()?.end_date.toString()

                    var count = 0
                    response.body()?.rates?.forEach {
                        Log.i("que hay", ""+it.value+" - "+ count +" - "+ divisaDestino)
                        if (rate == "GBP") {
                            listEntradas.add(Entry(count.toFloat(), it.value.GBP.toFloat()))
                        }
                        if (rate == "BRL") {
                            listEntradas.add(Entry(count.toFloat(), it.value.BRL.toFloat()))
                        }
                        if (rate == "CAD") {
                            listEntradas.add(Entry(count.toFloat(), it.value.CAD.toFloat()))
                        }
                        if (rate == "USD") {
                            listEntradas.add(Entry(count.toFloat(), it.value.USD.toFloat()))
                        }
                        if (rate == "EGP") {
                            listEntradas.add(Entry(count.toFloat(), it.value.EGP.toFloat()))
                        }
                        if (rate == "EUR") {
                            listEntradas.add(Entry(count.toFloat(), it.value.EUR.toFloat()))
                        }
                        if (rate == "INR") {
                            listEntradas.add(Entry(count.toFloat(), it.value.INR.toFloat()))
                        }
                        if (rate == "KPW") {
                            listEntradas.add(Entry(count.toFloat(), it.value.KPW.toFloat()))
                        }
                        if (rate == "KRW") {
                            listEntradas.add(Entry(count.toFloat(), it.value.KRW.toFloat()))
                        }
                        if (rate == "COP") {
                            listEntradas.add(Entry(count.toFloat(), it.value.COP.toFloat()))
                        }
                        if (rate == "CUP") {
                            listEntradas.add(Entry(count.toFloat(), it.value.CUP.toFloat()))
                        }
                        if (rate == "SEK") {
                            listEntradas.add(Entry(count.toFloat(), it.value.SEK.toFloat()))
                        }
                        if (rate == "RUB") {
                            listEntradas.add(Entry(count.toFloat(), it.value.RUB.toFloat()))
                        }
                        if (rate == "CNY") {
                            listEntradas.add(Entry(count.toFloat(), it.value.CNY.toFloat()))
                        }
                        count += 1
                    }




                    Log.i("entradas desde downloadSerie2", "" + listEntradas.size)
                }

                override fun onFailure(call: Call<Serie>, t: Throwable) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("El servidor esta fuera de servicio")
                        .setMessage("Comunicate con tu operador")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", null)
                        .show()

                }
            })
        }
        Log.i("entradas desde downloadSerie2", "" + listEntradas.toString())
        return listEntradas
    }




    private suspend fun getSerieKpi(dDestino: String): List<Float> {

        var listEntradas: MutableList<Float> = mutableListOf()
        Log.i("getSerieKPI -- Divisas:", dDestino+" - "+divisaBase)
        CoroutineScope(Dispatchers.IO).launch {
            val date = Date()
            val format = SimpleDateFormat("yyyy-MM-dd")
            val hoy = format.format(date)
            Log.i("Hoy", hoy)
            var cad = hoy.split("-")
            var y= cad[2].toInt()-1
            var ayer = cad[0]+"-"+ cad[1]+"-"+y.toString()
            Log.i("Ayer", hoy)

            Log.i("Divisas", dDestino+" - "+ divisaBase)

            val call =
                Manager.generateServiceAPI().create(ManagerRest::class.java).getSerie2(ayer,hoy.toString(),divisaBase, dDestino)

            //para hacer menos peticiones desde apiary
           /* val call =
                Manager.generateServiceDivisas().create(ManagerRest::class.java).getSerieKPI()*/


            call.enqueue(object : Callback<Serie> {
                override fun onResponse(
                    call: Call<Serie>,
                    response: Response<Serie>
                ) {



                    startDate = response.body()?.start_date.toString()
                    endDate = response.body()?.end_date.toString()
                    var r = response.body()?.rates
                    var count = 0
                    if (r != null) {
                        r.forEach {
                            if (dDestino == "GBP") {

                                    listEntradas.add(it.value.GBP.toFloat())

                            }
                            if (dDestino == "BRL") {

                                    listEntradas.add(it.value.BRL.toFloat())

                            }
                            if (dDestino == "CAD") {
                                listEntradas.add( it.value.CAD.toFloat())
                            }
                            if (dDestino == "USD") {
                                listEntradas.add( it.value.USD.toFloat())
                            }
                            if (dDestino == "EGP") {
                                listEntradas.add(it.value.EGP.toFloat())
                            }
                            if (dDestino == "EUR") {
                                listEntradas.add( it.value.EUR.toFloat())
                            }
                            if (dDestino == "INR") {
                                listEntradas.add( it.value.INR.toFloat())
                            }
                            if (dDestino == "KPW") {
                                listEntradas.add( it.value.KPW.toFloat())
                            }
                            if (dDestino == "KRW") {
                                listEntradas.add(it.value.KRW.toFloat())
                            }
                            if (dDestino == "COP") {
                                listEntradas.add( it.value.COP.toFloat())
                            }
                            if (dDestino == "CUP") {
                                listEntradas.add( it.value.CUP.toFloat())
                            }
                            if (dDestino == "SEK") {
                                listEntradas.add( it.value.SEK.toFloat())
                            }
                            if (dDestino == "RUB") {
                                listEntradas.add( it.value.RUB.toFloat())
                            }
                            if (dDestino == "CNY") {
                                listEntradas.add(it.value.CNY.toFloat())
                            }
                            count= count+1
                        }
                    }



                   // Log.i("entradas", "" + listEntradas.toString())
                }

                override fun onFailure(call: Call<Serie>, t: Throwable) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("El servidor esta fuera de servicio")
                        .setMessage("Comunicate con tu operador")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", null)
                        .show()

                }
            })
        }
       // Log.i("SERIEHEREEEE", "" + listEntradas)
        return listEntradas
    }

    private suspend fun drawTimeSerie(listValues: List<Float>){

        Log.i("drawTimeSeries","->"+ listValues.size)


        if (listValues != null) {

            tasaBase= listValues.get(1)

            binding.rtkAyer.text = "" + listValues.get(0)
            binding.rtkHoy.text = "" + listValues.get(1)
           // Log.i("drawserie:",""+listValues.await().get(0))
            var promedio: Double =
                (listValues.get(0).toDouble() + listValues.get(1)
                    .toDouble()) / 2
            binding.rtkNext.text = "" + promedio.toString().substring(0,8)

            if(listValues.get(1).toDouble() < listValues.get(0).toDouble()){
                binding.rtAyer.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_up_24)
                binding.rtHoy.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_down_24)
                if(listValues.get(1).toDouble() > promedio){
                    binding.rtNext.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_down_24)
                }
                else{
                    binding.rtNext.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_up_24)
                }
            }
            else{
                binding.rtAyer.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_down_24)
                binding.rtHoy.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_up_24)
                if(listValues.get(1).toDouble() > promedio){
                    binding.rtNext.setImageResource(com.intercam.inversiones.R.drawable.ic_baseline_arrow_drop_down_24)
                }
                else{
                    binding.rtNext.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                }
            }

            binding.tx.visibility = View.VISIBLE
            binding.ratesKpi.visibility = View.VISIBLE
            binding.kpi.visibility = View.VISIBLE
            binding.buttonCompra.visibility = View.VISIBLE
            binding.asignaMonto.visibility = View.VISIBLE
           // Log.i("corutine", "1.-corutina2" )

        }
    }


    private suspend fun drawFunction(listEntradas: MutableList<Entry>){

Log.i("DRAWFunction","--"+listEntradas)
            val lineDataSet = LineDataSet(listEntradas, "Serie de tiempo GBP")
            lineDataSet.color = Color.BLUE
            lineDataSet.valueTextColor = Color.BLACK

            val lineData = LineData(lineDataSet)
            binding.lineChart.data = lineData

            binding.lineChart.setTouchEnabled(true)
            binding.lineChart.setPinchZoom(true)
              binding.lineChart.description.text =
                "serie: " + startDate + " - " + endDate
            binding.lineChart.animateY(1500)



    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}