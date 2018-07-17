package com.amulp.justweather.utils

import com.amulp.justweather.MyApp
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

//awesome toast function, applies to everything
fun Any.toast(context: Context = MyApp.getAppContext(), duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun Any.toastError(context: Context = MyApp.getAppContext(), duration: Int = Toast.LENGTH_SHORT): Toast {
    Log.d("ToastError", this.toString())
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}