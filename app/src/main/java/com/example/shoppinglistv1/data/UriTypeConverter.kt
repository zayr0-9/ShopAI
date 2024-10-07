package com.example.shoppinglistv1.data

import android.net.Uri
import androidx.room.TypeConverter

class UriTypeConverter {

    @TypeConverter
    fun fromUriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun fromStringToUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}