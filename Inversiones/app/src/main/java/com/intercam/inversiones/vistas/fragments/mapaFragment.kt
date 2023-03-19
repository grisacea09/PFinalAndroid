package com.intercam.inversiones.vistas.fragments


import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ThumbnailUtils
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intercam.inversiones.R
import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.databinding.FragmentMapaBinding
import com.intercam.inversiones.dto.Locations
import com.intercam.inversiones.rest.ManagerRest
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class mapaFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private lateinit var binding: FragmentMapaBinding
    //Para Google Maps
    private lateinit var map: GoogleMap
    //Para los permisos
    private var coarseLocationPermissionGranted = false
    private var fineLocationPermissionGranted = false
    private lateinit var bottomNavigationView: BottomNavigationView

    companion object{
        const val PERMISO_LOCALIZACION = 1
        var mapFragment : SupportMapFragment?=null
        val TAG: String = mapaFragment::class.java.simpleName
        fun newInstance() = mapaFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentMapaBinding.inflate(inflater,container,false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.navView.background = null
        bottomNavigationView = binding.navView

        val navController: NavController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_main
        )

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        NavigationUI.setupActionBarWithNavController(requireActivity() as AppCompatActivity, navController);

        binding.navView.visibility = View.GONE

        return this.binding.root



    }

    private fun updateOrRequestPermissions() {

        //Revisando los permisos
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        coarseLocationPermissionGranted = hasCoarseLocationPermission
        fineLocationPermissionGranted = hasFineLocationPermission

        //Solicitando los permisos

        val permissionsToRequest = mutableListOf<String>()

        if (!hasCoarseLocationPermission)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (!hasFineLocationPermission)
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)


        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                PERMISO_LOCALIZACION
            )
        } else {

            //Tenemos los permisos
            map.isMyLocationEnabled = true

            val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10F,
                this
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISO_LOCALIZACION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Se obtuvo el permiso
                    updateOrRequestPermissions()
                } else {
                    if (shouldShowRequestPermissionRationale(permissions[0])) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Permiso requerido")
                            .setMessage("Se necesita acceder a la ubicación para esta funcionalidad")
                            .setPositiveButton(
                                "Entendido",
                                DialogInterface.OnClickListener { _, _ ->
                                    updateOrRequestPermissions()
                                })
                            .setNegativeButton(
                                "Salir",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.dismiss()

                                })
                            .create()
                            .show()
                    } else {
                        //Si el usuario no quiere que nunca se le vuelva a preguntar por el permiso
                        Toast.makeText(
                            requireContext(),
                            "El permiso a la localización se ha negado permanentemente",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        updateOrRequestPermissions()
        if (Network.verifyMetwork(requireContext())) {
            CoroutineScope(Dispatchers.IO).launch {

                var listValues: ArrayList<LatLng> = async{ getBanks() }.await()

                Log.i("DEsDE context",""+listValues)

                withContext(Dispatchers.Main) {
                    delay(4000)
                    drawMarkers(listValues)
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        if(!::map.isInitialized) return
        if(!fineLocationPermissionGranted){
            updateOrRequestPermissions()
        }
    }

    fun createMarker(){
        //19.464089018712155, -99.14044295836308
        val coordinates = LatLng(19.322326, -99.184592)
        val icon = BitmapFactory.decodeResource(requireActivity().resources,R.drawable.marcador2)

        val thumbnail = ThumbnailUtils.extractThumbnail(icon, 80, 80)
        val marker = MarkerOptions()
            .position(coordinates)
            .title("My House")
            .snippet("Linda casita")
            .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))

        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    fun createMarkersBank(latLng: LatLng){

        val coordinates = latLng  //LatLng(19.322326, -99.184592)
        val icon = BitmapFactory.decodeResource(requireActivity().resources,R.drawable.banking)

        val thumbnail = ThumbnailUtils.extractThumbnail(icon, 80, 80)
        val marker = MarkerOptions()
            .position(coordinates)
            .title("Sucursal")
            .snippet("InterBAnk")
            .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))

        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 5f),
            4000,
            null
        )
    }

    private suspend fun getBanks():ArrayList<LatLng>{

        delay(5000)
        var listValues: ArrayList<LatLng> = ArrayList()

        if(Network.typeNetwork(requireContext()) == "datos"){
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Estas usando datos celulares")
                .setMessage("Es necesario usar wifi")
                .setCancelable(false)
                .setPositiveButton("Aceptar", null)
                .show()
        }
        else if(Network.typeNetwork(requireContext()) == "wifi") {
            if (Network.verifyMetwork(requireContext())) {

                CoroutineScope(Dispatchers.IO).launch {
                    val call2 =
                        Manager.generateService().create(ManagerRest::class.java).getLocations()

                    call2.enqueue(object : Callback<ArrayList<Locations>> {
                        override fun onResponse(
                            call: Call<ArrayList<Locations>>,
                            response: Response<ArrayList<Locations>>
                        ) {
                            Log.i("respuesta serie", "" + response.body())
                            var mark = response.body()

                            response.body()?.forEach {
                                val l = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
                                listValues.add(l)
                            }


                        }

                        override fun onFailure(call: Call<ArrayList<Locations>>, t: Throwable) {
                            android.app.AlertDialog.Builder(requireContext())
                                .setTitle("El servidor esta fuera de servicio")
                                .setMessage("Comunicate con tu operador")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", null)
                                .show()

                        }

                    })
                }
            }
            else{
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }

        }
        Log.i("SERIEHEREEEE", "" + listValues)
        return listValues
    }



    override fun onLocationChanged(location: Location) {
        map.clear()
        val coordinates = LatLng(location.latitude, location.longitude)
        val icon = BitmapFactory.decodeResource(requireActivity().resources,R.drawable.user)
        val thumbnail = ThumbnailUtils.extractThumbnail(icon, 80, 80)
        val marker = MarkerOptions()
            .position(coordinates)
            .icon(BitmapDescriptorFactory.fromBitmap(thumbnail))
        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f))
    }

    private suspend fun drawMarkers(listValues: ArrayList<LatLng>) {
        Log.i("drawMarkers", "->" + listValues.size)
        if (listValues != null) {
            for (item in listValues){
                var latlong = LatLng(item.latitude, item.longitude)
                createMarkersBank(latlong)
            }
        }
    }


}