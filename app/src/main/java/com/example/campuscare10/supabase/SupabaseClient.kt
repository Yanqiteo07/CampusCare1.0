package com.example.campuscare10.supabase

import android.net.http.HttpResponseCache.install
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

const val SUPABASE_URL = "https://xphmrjqtuzdujfdpdrzs.supabase.co"
const val SUPABASE_KEY = "sb_secret_sxK9x-x_JigtIVtMI6aiBA_HI0HYf5v"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Postgrest)
}