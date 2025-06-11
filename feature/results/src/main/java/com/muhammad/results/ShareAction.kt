package com.muhammad.results

import android.content.Context
import android.net.Uri
import androidx.core.app.ShareCompat

fun shareImage(context: Context, uri: Uri) {
    ShareCompat.IntentBuilder(context).setType("image/jpeg").addStream(uri)
        .setChooserTitle(context.getString(R.string.share_text)).startChooser()
}