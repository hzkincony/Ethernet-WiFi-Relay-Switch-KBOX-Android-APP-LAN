package com.kincony.kbox.ui.base

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.request.RequestOptions
import com.kincony.kbox.R

object ImageLoader {

    fun load(context: Context?, url: Any?, img: ImageView?) {
        if (context != null && img != null) {
            Glide.with(context).setDefaultRequestOptions(RequestOptions().centerCrop().placeholder(R.drawable.icon16).error(R.drawable.icon16)).load(url).into(img)
        }
    }

}