package com.intercam.inversiones.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.intercam.inversiones.R
import com.intercam.inversiones.databinding.ActivityMenuBinding
import com.intercam.inversiones.databinding.DivisaElementBinding
import com.intercam.inversiones.dto.ResponseDiv
import com.intercam.inversiones.vistas.activities.MenuActivity
import com.intercam.inversiones.vistas.fragments.homeFragment


class DivisasAdapter(private val context:Context, private val cdivs: ArrayList<ResponseDiv>):
    RecyclerView.Adapter<DivisasAdapter.ViewHolder>() {



    class ViewHolder(view: DivisaElementBinding): RecyclerView.ViewHolder(view.root) {

            val tvB = view.tvBase
            val tvBD = view.tvDestino
            val tvmonto = view.tvMonto
            val tvtb = view.tvtasabase
            val img = view.ivThumbnail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisasAdapter.ViewHolder {
        val binding = DivisaElementBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DivisasAdapter.ViewHolder, position: Int) {

        holder.tvB.text = "Divisa Base: "+cdivs[position].divisaBase
        holder.tvBD.text = "Divisa Destino: "+cdivs[position].divisaDestino
        holder.tvmonto.text = "Monto: "+cdivs[position].montoCompra.toString()
        holder.tvtb.text = "Tasa Base: "+cdivs[position].tasaBase.toString()
        holder.img.setImageResource(R.drawable.cambiar)


        holder.itemView.setOnClickListener {
            Log.i("click", "-->")
            (context as MenuActivity).supportFragmentManager.fragments.forEach {
                it.childFragmentManager.fragments.forEach { fragment ->
                    if (fragment is homeFragment) {
                        //do something
                        fragment.selectedCompra(cdivs[position])
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return cdivs.size
    }


}