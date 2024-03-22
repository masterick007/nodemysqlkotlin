package com.example.phpcrudteste

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phpcrudteste.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {

    lateinit var binding: ActivityMainBinding
    lateinit var adaptador: UsuarioAdapter

    var listaUsuarios = arrayListOf<Usuario>()
    var usuario = Usuario(-1,"","")
    var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()
    }


    fun setupRecyclerView() {
        adaptador = UsuarioAdapter(this, listaUsuarios)
        adaptador.setOnClick(this@MainActivity)
        binding.rvUsuarios.adapter = adaptador
        obtenerUsuarios()

        binding.btnAddUpdate.setOnClickListener {
            var valido = validarCampos()
            if (valido){
                if (!isEditando){
                    agregarUsuario()
                }else{
                    actualizarUsuario()
                }
            }
        }
    }

    fun agregarUsuario() {
        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.email = binding.etEmail.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if (call.isSuccessful){
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_SHORT).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarOjeto()
                }else{
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun actualizarUsuario(){
        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.email = binding.etEmail.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.actualizarUsuario(usuario.idUsuario, usuario)
            runOnUiThread {
                if (call.isSuccessful){
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_SHORT).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarOjeto()

                    binding.btnAddUpdate.setText("Agregar Usuario")
                    binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.green)
                    isEditando = false
                }else{
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etEmail.setText("")

    }
    fun limpiarOjeto() {
        this.usuario.idUsuario = -1;
        this.usuario.nombre = ""
        this.usuario.email = ""
    }
    fun validarCampos(): Boolean{
        return !(binding.etNombre.text.isNullOrEmpty() || binding.etEmail.text.isNullOrEmpty())
    }
    fun  obtenerUsuarios(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerUsuarios()
            runOnUiThread {
                if(call.isSuccessful){
                    listaUsuarios = call.body()!!.listaUsuarios
                    setupRecyclerView()
                }else{
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.nombre)
        binding.etEmail.setText(usuario.email)
        binding.btnAddUpdate.setText("ACTUALIZAR USUARIO")
        binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.red)
        this.usuario = usuario
        isEditando = true

    }

    override fun borrarUsuario(idUsuario: Int) {
       CoroutineScope(Dispatchers.IO).launch {
           val call = RetrofitClient.webService.borrarUsuario(idUsuario)
           runOnUiThread {
               if (call.isSuccessful){
                   Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_SHORT).show()
                   obtenerUsuarios()
               }
           }
       }
    }
}