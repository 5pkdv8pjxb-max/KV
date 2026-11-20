package com.example.kursovik.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

fun interface InitialRatesSource { fun readXml(): String }

class AssetInitialRatesSource @Inject constructor(@ApplicationContext private val context: Context) : InitialRatesSource {
    override fun readXml(): String = context.assets.open("initial_rates.xml")
        .bufferedReader(Charsets.UTF_8).use { it.readText() }
}
