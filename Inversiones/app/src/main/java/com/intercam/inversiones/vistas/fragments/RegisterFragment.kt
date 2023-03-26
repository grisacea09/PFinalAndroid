package com.intercam.inversiones.vistas.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.intercam.inversiones.R


import com.intercam.inversiones.companion.Manager
import com.intercam.inversiones.companion.Network
import com.intercam.inversiones.databinding.FragmentRegisterBinding
import com.intercam.inversiones.dto.UsuarioBody
import com.intercam.inversiones.dto.UsuarioResponseBody
import com.intercam.inversiones.rest.ManagerRest
import com.intercam.inversiones.vistas.activities.MainActivity
import com.intercam.inversiones.vistas.activities.MenuActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    private val TAG: String =
        RegisterFragment::class.java.getSimpleName()

    companion object {
       var servResponse = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentRegisterBinding.inflate(inflater,container, false)

       this.binding!!.buttonLogin.setOnClickListener {

           Log.i(TAG,"click  enviar")
           var username = this.binding!!.editUserLogin.editText?.text.toString()
           var password = this.binding!!.editPasswordLogin.editText?.text.toString()
           var cuenta = this.binding!!.tvCuenta.editText?.text.toString()
           var telefono = this.binding!!.tvTelefono.editText?.text.toString()

           //1.- validar si todos los campos van llenos si es vacio mandar el error a la eqieuta de error

           if(username.isEmpty()){
               this.binding!!.editUserLogin.editText?.setError("El usuario no puede ser vacío")
           }
           else if(password.isEmpty()){
               this.binding!!.editPasswordLogin.editText?.setError("La contraseña no puede ser vacío")
           }
           else if(cuenta.isEmpty()){
               this.binding!!.tvCuenta.editText?.setError("La cuenta debe tener entre 13 y 18 digitos")
           }
           else if(telefono.isEmpty()){
               this.binding!!.tvTelefono.editText?.setError("El telefono debe tener 10 digitos")
           }
           else if(!telefono.isEmpty() && !cuenta.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
               //2-validar si hay internet para poder enviar la peticion si no enviar
                Log.i(TAG, "hay que enviar los datos")
               hideKeyboard()
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



                       val call = Manager.generateService().create(ManagerRest::class.java)
                           .sendInsertaUsuario(
                               UsuarioBody(username, password, cuenta, telefono, 5000, "MXN")
                           )

                       call.enqueue(object : Callback<UsuarioResponseBody> {
                           override fun onResponse(
                               call: Call<UsuarioResponseBody>,
                               response: Response<UsuarioResponseBody>
                           ) {
                               Log.i("insertaUsr response code", "" + (response.body()?.response))

                               if (response.code() == 200) {


                                           Log.i(
                                               TAG,
                                               "se insertó correctamente  "
                                           )
                                           //c) último paso ir al login



                                       AlertDialog.Builder(requireContext())
                                           .setTitle("El usuario ha sido insertado")
                                           .setMessage("Ir al login")
                                           .setCancelable(false)
                                           .setPositiveButton("Aceptar", DialogInterface.OnClickListener{dialog: DialogInterface, which: Int ->
                                               Log.i("Register","quiero ir al login ")
                                               val fragmentManager = getActivity()?.getSupportFragmentManager()
                                               val fragmentTransaction = fragmentManager?.beginTransaction()
                                               if (fragmentTransaction != null) {
                                                   fragmentTransaction.remove(this@RegisterFragment).commit()
                                               }



                                           })
                                           .show()

                                   }


                           }

                           override fun onFailure(call: Call<UsuarioResponseBody>, t: Throwable) {

                               Log.i("homefragment", t.localizedMessage)
                               //algo pasa con mongo
                               //java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $
                               AlertDialog.Builder(requireContext())
                                   .setTitle("El servidor esta fuera de servicio")
                                   .setMessage("Comunicate con tu operador")
                                   .setCancelable(false)
                                   .setPositiveButton("Aceptar", null)
                                   .show()

                           }

                       })

               }
                   else{
                   AlertDialog.Builder(requireContext())
                       .setTitle("El celular no cuenta con internet")
                       .setMessage("Asegurate de tener datos o wifi")
                       .setCancelable(false)
                       .setPositiveButton("Aceptar", null)
                       .show()
                   }
               }//fin if
               else {
                   AlertDialog.Builder(requireContext())
                       .setTitle("El celular no cuenta con internet")
                       .setMessage("Asegurate de tener datos o wifi")
                       .setCancelable(false)
                       .setPositiveButton("Aceptar", null)
                       .show()

               }
           }
       }



        return this.binding!!.root
    }

    fun displayAlert(){

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