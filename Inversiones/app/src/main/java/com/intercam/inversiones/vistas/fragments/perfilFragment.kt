package com.intercam.inversiones.vistas.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intercam.inversiones.R
import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.databinding.FragmentPerfilBinding
import com.intercam.inversiones.dto.ResponseDiv
import com.intercam.inversiones.dto.UsuarioPwd
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.activities.MainActivity
import com.intercam.inversiones.vistas.activities.MenuActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class perfilFragment : Fragment(), OnChartValueSelectedListener {
   private lateinit var binding: FragmentPerfilBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    var info = ArrayList<String>()
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
        this.binding = FragmentPerfilBinding.inflate(inflater, container, false)

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
                        var total = 0L
                        val entries: MutableList<PieEntry> = ArrayList()
                        Log.i("responseDiv", "--> " + compradivisas)
                        entries.add(PieEntry(500f, "MXN"))
                        if (compradivisas != null) {
                            compradivisas.forEach {
                                info.add(it.divisaDestino)
                                entries.add(PieEntry(it.montoCompra.toFloat(), it.divisaDestino))
                            }
                            val set = PieDataSet(entries, " Compra de divisas")

                            val data = PieData(set)
                            binding.piechart.setData(data)
                            binding.piechart.invalidate()
                            val colors = intArrayOf(
                                Color.GRAY,
                                Color.GREEN,
                                Color.CYAN,
                                Color.BLUE,
                                Color.MAGENTA,
                                Color.DKGRAY
                            )
                            data.setValueTextColor(Color.BLUE);
                            set.setColors(ColorTemplate.createColors(colors))
                            set.setSliceSpace(3f)
                            set.setValueTextColor(Color.BLACK);
                            set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                            binding.piechart.setEntryLabelColor(Color.BLACK);
                            binding.piechart.setExtraOffsets(5f, 10f, 5f, 5f);
                            binding.piechart.setOnChartValueSelectedListener(this)
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<ResponseDiv>>, t: Throwable) {
                        Log.i("HOMEFRAGMENT", "error salio algo mal" + t)
                    }
                })
            }
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


        binding.navView.background = null

        bottomNavigationView = binding.navView

        val navController: NavController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_main
        )

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        NavigationUI.setupActionBarWithNavController(requireActivity() as AppCompatActivity, navController);

        binding.navView.selectedItemId = R.id.navigation_detalle

        return this.binding.root
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null || h == null)
            return;
        Toast.makeText(requireContext(), "Value: " + e.getY() + ", index: " + h.getX()
                + "data: " + info[h.getDataIndex() + 1], Toast.LENGTH_SHORT).show();
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }


}

private fun PieChart.setOnChartValueSelectedListener(callback: Callback<ArrayList<ResponseDiv>>) {

    //Toast.makeText(this.context, "Value: " + this.data.dataSetLabels, Toast.LENGTH_SHORT).show();
}
