package com.example.apptaller2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.apptaller2.R
import com.example.apptaller2.SupabaseClient
import com.example.apptaller2.ui.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.apptaller2.data.CredencialesManager

class LoginActivity : AppCompatActivity() {

    private lateinit var tvIngresarConHuella: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_login)

        tvIngresarConHuella = findViewById(R.id.login_huella)

        configurarVisibilidadHuella()

        //Inicio de sesion con huella
        tvIngresarConHuella.setOnClickListener {
            mostrarDialogHuella()
        }


        // Manejo del teclado para Android 15/16
        val rootView = findViewById<android.view.ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
            v.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                bottomPadding
            )
            insets
        }

        // Listeners de los botones
        findViewById<android.widget.Button>(R.id.btn_inicio_sesion)
            .setOnClickListener { iniciarSesion() }
        findViewById<android.widget.TextView>(R.id.in_tv_registrate)
            .setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        findViewById<android.widget.TextView>(R.id.in_recuperar_contrasena)
            .setOnClickListener {
                Toast.makeText(this, "Proximamente", Toast.LENGTH_SHORT).show()
            }
        findViewById<android.widget.LinearLayout>(R.id.btn_google)
            .setOnClickListener { iniciarSesionConGoogle() }


    }

    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun configurarVisibilidadHuella() {
        //Verificar si hay credenciales guardadas localmente
        val huellaActiva = CredencialesManager.huellaActiva(this)

        //Verificar si el disposituvi tiene sensor de huella
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        val huellaDisponible = biometricManager.canAuthenticate() == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
        tvIngresarConHuella.visibility = if (huellaActiva && huellaDisponible) {
            View.VISIBLE
        }else{
            View.GONE
        }
    }

    private fun iniciarSesion() {
        val correo = findViewById<android.widget.EditText>(R.id.in_correo)
            .text.toString().trim()
        val contrasena = findViewById<android.widget.EditText>(R.id.in_contrasena)
            .text.toString()
        // Validaciones locales
        if (correo.isEmpty() || contrasena.isEmpty()) {

            Toast.makeText(
                this, "Por favor completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (contrasena.length < 6) {
            Toast.makeText(
                this, "La contrasena debe tener minimo 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        // Llamada a Supabase Auth
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = correo
                    password = contrasena
                }
                CredencialesManager.guardarCredenciales(
                    this@LoginActivity, correo, contrasena, true)
                irAPantallaPrincipal()

                finishAffinity()
            } catch (e: Exception) {
                val mensaje = when {
                    e.message?.contains("Invalid login credentials") == true ->
                        "Correo o contrasena incorrectos"

                    else -> "Error al iniciar sesion: ${e.message}"
                }
                Toast.makeText(
                    this@LoginActivity, mensaje,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                // 1. Configurar la solicitud de Google
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("602344744135-2pgrmvra61dcol7hunua6m5eb96f9287.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                // 2. Mostrar el selector de cuentas de Google
                val credentialManager =
                    CredentialManager.create(this@LoginActivity)
                val result = credentialManager.getCredential(
                    this@LoginActivity, request
                )
                // 3. Obtener el token de Google
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                // 4. Enviar el token a Supabase
                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error al iniciar con Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarDialogHuella() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = androidx.biometric.BiometricPrompt(
            this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val correo = CredencialesManager.obtenerCorreo(this@LoginActivity)
                    val contrasena = CredencialesManager.obtenerContrasena(this@LoginActivity)
                    if (correo != null && contrasena != null) {
                        //Sifinin credenciales normales
                        lifecycleScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    email = correo
                                    password = contrasena
                                }
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finishAffinity()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Error al iniciar sesion: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Inicia sesion con tu email",
                            Toast.LENGTH_SHORT
                        ).show()
                        CredencialesManager.limpiarCredenciales(this@LoginActivity)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error biometrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(this@LoginActivity, "Autenticacion fallida", Toast.LENGTH_SHORT)
                        .show()
                }

            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactular para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finishAffinity()
        }
    }


}