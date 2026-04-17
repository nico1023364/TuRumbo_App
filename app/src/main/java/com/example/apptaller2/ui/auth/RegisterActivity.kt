package com.example.apptaller2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.apptaller2.ui.MainActivity
import com.example.apptaller2.R
import com.example.apptaller2.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etConfirmarContrasena: EditText
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnRegistrar: Button
    private lateinit var tvCuenta: TextView

    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val rootView = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }


        etNombres = findViewById(R.id.input_names)
        etApellidos = findViewById(R.id.input_lastName)
        etCorreo = findViewById(R.id.input_email_register)
        etContrasena = findViewById(R.id.input_password_register)
        etConfirmarContrasena = findViewById(R.id.input_confirm_password_register)
        cbTerminos = findViewById(R.id.checkBox_terminos_register)
        btnRegistrar = findViewById(R.id.button_register)
        tvCuenta = findViewById(R.id.tv_registrate_login)

        //Escuchar el boton de registro
        btnRegistrar.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val confirmarContrasena = etConfirmarContrasena.text.toString().trim()

            //Validaciones
            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (contrasena.length < 8) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cbTerminos.isChecked) {
                Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            //Registrar en Supabase
            lifecycleScope.launch {
                try {
                    //Paso 1: Registrar el usuario en supabase Auth
                    val resultado = SupabaseClient.client.auth.signUpWith(Email) {
                        email = correo
                        password = contrasena
                    }

                    //Paso 2: Guardar los datos adicionales del usuario en supabase
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
                    SupabaseClient.client.postgrest["usuarios"].insert(
                        UsuarioData(
                            id = userId,
                            nombres = nombres,
                            apellidos = apellidos
                        )
                    )

                    //Paso 3: Redirigir al usuario a Login
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error en el registro: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }


        }
        tvCuenta.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}