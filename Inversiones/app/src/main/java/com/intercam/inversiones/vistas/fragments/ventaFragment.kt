package com.intercam.inversiones.vistas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.intercam.inversiones.databinding.FragmentVentaBinding


class ventaFragment : Fragment() {

    private lateinit var  binding: FragmentVentaBinding

    companion object{
        private const val TITLE_AGR = "title.string"

        fun newInstance(strTitle:String):ventaFragment{

            return ventaFragment().apply {
                this.arguments = Bundle().apply {
                    this.putString(TITLE_AGR,strTitle)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentVentaBinding.inflate(inflater,container,false)

        //crear la grafica pie.

        val db   = arguments?.getString("base")
        val dd = arguments?.getString("destino")
        val monto = arguments?.getString("monto")
        val tbase = arguments?.getString("tasaBase")

        val entries: MutableList<PieEntry> = ArrayList()

        entries.add(PieEntry(18.5f, "Green"))
        entries.add(PieEntry(26.7f, "Yellow"))
        entries.add(PieEntry(24.0f, "Red"))
        entries.add(PieEntry(30.8f, "Blue"))

        val set = PieDataSet(entries, "Election Results")
        val data = PieData(set)
        binding.piechart.setData(data)
        binding.piechart.invalidate() // refresh


        return this.binding.root
    }



}