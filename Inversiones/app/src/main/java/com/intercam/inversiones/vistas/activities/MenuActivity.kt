package com.intercam.inversiones.vistas.activities


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.intercam.inversiones.R
import com.intercam.inversiones.databinding.ActivityMenuBinding


class MenuActivity : AppCompatActivity() {




    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        // Obtienes el texto
        val username = bundle!!.getString("usuario")
        if (username != null) {
            Log.i("MENU ACTV", username)
        }

        binding.navView.background = null

       val navController = findNavController(R.id.nav_host_fragment_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                com.intercam.inversiones.R.id.navigation_home,
                R.id.navigation_compra,
                R.id.navigation_mapa,
                R.id.navigation_detalle
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        Log.i("clcik en ", "item: "+binding.navView.getSelectedItemId())

      Log.i("clcik en ", "item: "+binding.navView.menu.findItem(binding.navView.getSelectedItemId()))

        binding.fab.setOnClickListener{
            val image = ImageView(this)
            image.setImageResource(com.intercam.inversiones.R.drawable.emoticonos)
            //gracias por participar
            val builder: android.app.AlertDialog.Builder? = android.app.AlertDialog.Builder(this).setMessage(" Gracias por usar nuestra app")
                .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                    }
                }).setView(image)
            if (builder != null) {
                builder.create().show()
            }

        }


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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
    }


}