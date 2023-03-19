package com.intercam.inversiones.vistas.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intercam.inversiones.R
import com.intercam.inversiones.adapters.DivisasAdapter
import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.databinding.FragmentHomeBinding
import com.intercam.inversiones.dto.ResponseDiv
import com.intercam.inversiones.dto.UsuarioPwd
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class homeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        //hay que hacer el servicio para traer los registros que haya en la compra de divisas
        // listar las compraDivisas
        val fragment: Fragment? =  fragmentManager?.findFragmentByTag("compra")
        if (fragment != null) fragmentManager?.beginTransaction()?.remove(fragment)
            ?.commit()


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

                if (MainActivity.user != null || MainActivity.user != "") {
                    CoroutineScope(Dispatchers.Main).launch {

                        val call = Manager.generateService().create(ManagerRest::class.java)
                            .sendBuscaCompraDivisas(
                                UsuarioPwd(MainActivity.user.toString(), "")
                            )

                        call.enqueue(object : Callback<ArrayList<ResponseDiv>> {
                            override fun onResponse(
                                call: Call<ArrayList<ResponseDiv>>,
                                response: Response<ArrayList<ResponseDiv>>
                            ) {
                                Log.i("home", "--> " + response.body())
                                var compradivisas = response.body()

                                Log.i("responseDiv", "--> " + compradivisas)
                                if (compradivisas != null) {
                                    binding.lymenu.visibility = View.VISIBLE
                                    binding.tvinit.visibility = View.GONE
                                    binding.rvMenu.layoutManager =
                                        LinearLayoutManager(requireContext())
                                    binding.rvMenu.adapter =
                                        DivisasAdapter(requireContext(), response.body()!!)
                                }
                            }

                            override fun onFailure(
                                call: Call<ArrayList<ResponseDiv>>,
                                t: Throwable
                            ) {
                                Log.i("HOMEFRAGMENT", "error salio algo mal" + t)
                            }
                        })
                    }
                } else {
                    Log.i("homefragment", "el usuario no existe")
                }
            }
        }
        else{
            AlertDialog.Builder(requireContext())
                .setTitle("El celular no cuenta con internet")
                .setMessage("Asegurate de tener datos o wifi")
                .setCancelable(false)
                .setPositiveButton("Aceptar", null)
                .show()
        }





        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
    }


    fun selectedCompra(rd: ResponseDiv) {

        val parametros = Bundle().apply {
            putString("base",rd.divisaBase)
            putString("destino", rd.divisaDestino)
            putLong("monto", rd.montoCompra)
            putDouble("tasaBase",rd.tasaBase)
        }
        //fragment de detalle

        val pf = perfilFragment()
        pf.setArguments(parametros)

        val fragmentManager = this.parentFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

        fragmentTransaction.replace(R.id.layouthome, pf)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.show(pf)



    }


}