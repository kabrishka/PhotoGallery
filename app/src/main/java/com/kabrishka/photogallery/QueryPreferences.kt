package com.kabrishka.photogallery

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_SEARCH_QUERY = "searchQuery"

object QueryPreferences {

    fun getStoreQuery(context: Context): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(PREF_SEARCH_QUERY, "")!!
    }

    fun setStoreQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_QUERY, query)
            .apply()
    }
}