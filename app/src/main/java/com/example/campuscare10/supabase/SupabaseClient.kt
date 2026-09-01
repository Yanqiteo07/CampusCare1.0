package com.example.campuscare10.supabase

import android.net.http.HttpResponseCache.install
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

const val SUPABASE_URL = "https://xphmrjqtuzdujfdpdrzs.supabase.co"
const val SUPABASE_KEY = "sb_publishable_5k90Ly4DmjpnJlU9Pvu6hA_FSxMlgwb"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Postgrest)
}