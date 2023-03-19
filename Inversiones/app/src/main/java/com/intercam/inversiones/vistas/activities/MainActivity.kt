package com.intercam.inversiones.vistas.activities


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


import com.intercam.inversiones.R
import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.databinding.ActivityMainBinding
import com.intercam.inversiones.dto.UsuarioPwd
import com.intercam.inversiones.dto.UsuarioResponseBody
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.fragments.RegisterFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityMainBinding
    companion object {
        var user: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.buttonLogin.setOnClickListener{
        //verificar si hay internet...
            var username = this.binding!!.editUserLogin.editText?.text.toString()
            var password = this.binding!!.editPasswordLogin.editText?.text.toString()

            if(username.isEmpty()){
                this.binding!!.editUserLogin.editText?.setError("El usuario no puede ser vacío")
            }
            else if(password.isEmpty()){
                this.binding!!.editPasswordLogin.editText?.setError("La contraseña no puede ser vacío")
            }

            Log.i("mainActivty", username+ "  -  "+ password)
            //si no hay internet
            //mandar alert para que se conecte al wifi o datos
            //si tiene datos y no hay respuesta del server..
            //entones o no tiene datos o el server se cayó
            hideKeyboard()
            if(Network.typeNetwork(this) == "datos"){
                AlertDialog.Builder(this)
                    .setTitle("Estas usando datos celulares")
                    .setMessage("Es necesario usar wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            else if(Network.typeNetwork(this) == "wifi") {

                if (Network.verifyMetwork(this)) {

                    Log.i("buscarUsr", "hay internet--> " + username)
                    val call = Manager.generateService().create(ManagerRest::class.java)
                        .sendBuscaUsuario(
                            UsuarioPwd(username, password)
                        )

                    call.enqueue(object : Callback<UsuarioResponseBody> {
                        override fun onResponse(
                            call: Call<UsuarioResponseBody>,
                            response: Response<UsuarioResponseBody>
                        ) {
                            Log.i("buscarUsr", "" + (response.code()))
                            if (response.body()?.response == "OK") {
                                //ir a la pagina de home
                                Log.i("Main", "ir al homefragment" + username)
                                user = username
                                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                                //este es un dato sencible, no se debe hacer pero si no no termino :(
                                //deberia ir en base de datos
                                intent.putExtra("usuario", username);
                                startActivity(intent)

                            } else {
                                Log.i("Main", "" + response.body()?.response)
                                AlertDialog.Builder(this@MainActivity)
                                    .setTitle("El usuario no existe")
                                    .setMessage("usuario o pasword incorrecto")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", null)
                                    .show()
                            }
                        }//fin corutina

                        override fun onFailure(call: Call<UsuarioResponseBody>, t: Throwable) {
                            Log.d("fail", t.localizedMessage)
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("El servidor esta fuera de servicio")
                                .setMessage("Comunicate con tu operador")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", null)
                                .show()
                        }
                    })
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("El celular no cuenta con internet")
                        .setMessage("Asegurate de tener datos o wifi")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("El celular no cuenta con internet")
                    .setMessage("Asegurate de tener datos o wifi")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }

        }

        binding.buttonRegistrarse.setOnClickListener{
            Log.i("main", "click vamos al fragment de registro")
            supportFragmentManager.beginTransaction().add(R.id.layoutMain, RegisterFragment())
                //si la añade pero quizas la destruye por eso se ve blanco
                // .addToBackStack("FirstFragment")
                .commit()
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


