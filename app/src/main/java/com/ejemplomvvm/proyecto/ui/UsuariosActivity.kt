package com.ejemplomvvm.proyecto.ui

import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplomvvm.proyecto.databinding.ActivityUsuariosBinding
import com.ejemplomvvm.proyecto.model.database.dao.UserDao
import com.ejemplomvvm.proyecto.model.database.entities.UserEntity
import com.ejemplomvvm.proyecto.model.database.providers.UsuarioDatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuariosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsuariosBinding
    private lateinit var usuarioAdapter: UsuarioAdapter
    private var usuarioSeleccionado: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = UsuarioDatabaseProvider.getDatabase(applicationContext)
        val usuarioDao = db.getUserDao()

        // Inicializar adaptador para el ListView
        usuarioAdapter = UsuarioAdapter(this, mutableListOf())
        binding.lvUsers.adapter = usuarioAdapter

        // Cargar los usuarios desde la base de datos
        cargarUsuarios(usuarioDao)

        // Configurar clic en el ListView para seleccionar usuario
        binding.lvUsers.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            usuarioSeleccionado = usuarioAdapter.getItem(position)
            usuarioSeleccionado?.let {
                binding.editNombre.setText(it.nombre)
                binding.editApellido.setText(it.apellido)
                Toast.makeText(this, "Seleccionado: ${it.nombre} ${it.apellido}", Toast.LENGTH_SHORT).show()
            }
        }

        // Registrar nuevo usuario
        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.editNombre.text.toString().trim()
            val apellido = binding.editApellido.text.toString().trim()

            if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
                val nuevoUsuario = UserEntity(nombre = nombre, apellido = apellido)
                registrarUsuario(usuarioDao, nuevoUsuario)
            } else {
                Toast.makeText(this, "Completa los campos para registrar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Actualizar usuario seleccionado
        binding.btnActualizar.setOnClickListener {
            usuarioSeleccionado?.let { usuario ->
                val nuevoNombre = binding.editNombre.text.toString().trim()
                val nuevoApellido = binding.editApellido.text.toString().trim()

                if (nuevoNombre.isNotEmpty() && nuevoApellido.isNotEmpty()) {
                    val usuarioActualizado = usuario.copy(nombre = nuevoNombre, apellido = nuevoApellido)
                    actualizarUsuario(usuarioDao, usuarioActualizado)
                } else {
                    Toast.makeText(this, "Completa los campos para actualizar.", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Selecciona un usuario primero.", Toast.LENGTH_SHORT).show()
        }

        // Eliminar todos los usuarios
        binding.btnEliminar.setOnClickListener {
            eliminarTodosLosUsuarios(usuarioDao)
        }
    }

    private fun cargarUsuarios(usuarioDao: UserDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuarios = usuarioDao.getAllUsers()
            withContext(Dispatchers.Main) {
                usuarioAdapter.clear()
                usuarioAdapter.addAll(usuarios)
                usuarioAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun registrarUsuario(usuarioDao: UserDao, usuario: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioDao.insertar(usuario)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UsuariosActivity, "Usuario registrado", Toast.LENGTH_SHORT).show()
                cargarUsuarios(usuarioDao)
                limpiarCampos()
            }
        }
    }

    private fun actualizarUsuario(usuarioDao: UserDao, usuario: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioDao.actualizarUsuario(usuario) // Ahora usamos el método @Update
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UsuariosActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                cargarUsuarios(usuarioDao) // Refrescar la lista en el ListView
                limpiarCampos() // Limpiar los campos después de actualizar
            }
        }
    }


    private fun eliminarTodosLosUsuarios(usuarioDao: UserDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioDao.eliminarTodos()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UsuariosActivity, "Todos los usuarios eliminados", Toast.LENGTH_SHORT).show()
                cargarUsuarios(usuarioDao)
                limpiarCampos()
            }
        }
    }

    private fun limpiarCampos() {
        binding.editNombre.text.clear()
        binding.editApellido.text.clear()
        usuarioSeleccionado = null
    }
}
