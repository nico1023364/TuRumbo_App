package com.example.apptaller2.data

import android.content.Context

object CredencialesManager {
    private const val PREFS_NAME = "auth"
    private const val KEY_CORREO = "correo"
    private const val KEY_CONTRASENA = "contrasena"
    private const val KEY_HUELLA = "huella_activa"

    fun guardarCredenciales(
        context: Context,
        correo: String,
        contrasena: String,
        huellaActiva: Boolean
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CORREO, correo)
            .putString(KEY_CONTRASENA, contrasena)
            .putBoolean(KEY_HUELLA, huellaActiva)
            .apply()
    }

    fun limpiarCredenciales(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun huellaActiva(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HUELLA, false)


    fun obtenerCorreo(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CORREO, null)

    fun obtenerContrasena(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CONTRASENA, null)
}