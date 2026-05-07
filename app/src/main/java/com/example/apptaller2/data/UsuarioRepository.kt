package com.example.apptaller2.data


import com.example.apptaller2.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object UsuarioRepository {
    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String,
        val correo: String? = null,
        val rol: String = "cliente",
        val foto_url: String? = null
    )

    suspend fun existeUsuario(userId: String): Boolean {
        return try{
            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select(Columns.raw("id")){
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<Map<String, String>>()
            resultado.isNotEmpty()
        }catch (e: Exception){
            false
        }
    }

    suspend fun obtenerUsuarioActual(): UsuarioData? {
        val userId = SupabaseClient.client.auth
            .currentUserOrNull()?.id ?: return null
        return try {
            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<UsuarioData>()

            android.util.Log.d("DEBUG_QUERY", "Resultado completo: $resultado")

            resultado.firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("DEBUG_QUERY", "Error: ${e.message}")
            null
        }
    }

    suspend fun insertarUsuario(id: String, nombres: String, apellidos: String, correo: String) {
        SupabaseClient.client.postgrest["usuarios"].insert(
            UsuarioData(id, nombres, apellidos, correo)
        )
    }

    suspend fun obtenerRolActual(): String {
        return try {
            val userId = SupabaseClient.client.auth
                .currentUserOrNull()?.id ?: return "cliente"

            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<UsuarioData>()

            resultado.firstOrNull()?.rol ?: "cliente"

        } catch (e: Exception) {
            "cliente"
        }
    }

    // Actualiza los datos del perfil en la tabla usuarios
    suspend fun actualizarPerfil(
        nombres: String,
        apellidos: String,
        correo: String,
        fotoUrl: String? = null
    ) {
        val userId = SupabaseClient.client.auth
            .currentUserOrNull()?.id ?: return

        val datos = buildJsonObject {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("correo", correo)
            if (fotoUrl != null) put("foto_url", fotoUrl)
        }

        SupabaseClient.client.postgrest["usuarios"]
            .update(datos) {
                filter { eq("id", userId) }
            }
    }

    // Sube la foto al bucket avatars y devuelve la URL pública
    suspend fun subirFotoPerfil(
        contexto: android.content.Context,
        uri: android.net.Uri
    ): String {
        val userId = SupabaseClient.client.auth
            .currentUserOrNull()?.id ?: return ""

        android.util.Log.d("DEBUG_FOTO", "Uri scheme: ${uri.scheme}")
        android.util.Log.d("DEBUG_FOTO", "Uri path: ${uri.path}")

        val bytes = if (uri.scheme == "content") {
            contexto.contentResolver
                .openInputStream(uri)?.readBytes()
        } else {
            java.io.File(uri.path!!).readBytes()
        } ?: return ""

        android.util.Log.d("DEBUG_FOTO", "Bytes leídos: ${bytes.size}")

        val rutaArchivo = "perfil_$userId.jpg"

        SupabaseClient.client.storage["avatars"]
            .upload(
                path = rutaArchivo,
                data = bytes,
                options = { upsert = true }
            )

        val url = SupabaseClient.client.storage["avatars"]
            .publicUrl(rutaArchivo)

        android.util.Log.d("DEBUG_FOTO", "URL generada: $url")

        return url
    }

}