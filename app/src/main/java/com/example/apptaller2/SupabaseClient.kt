package com.example.apptaller2

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://lwufrilmmwlkuxtptegs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx3dWZyaWxtbXdsa3V4dHB0ZWdzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU2MDE3OTIsImV4cCI6MjA5MTE3Nzc5Mn0.h5RR-u-uz3vNOcO_968ojNVyCEMRugIVrkrFH9MfATU"

    ){
        install(Postgrest)
        install(Auth)
    }
}