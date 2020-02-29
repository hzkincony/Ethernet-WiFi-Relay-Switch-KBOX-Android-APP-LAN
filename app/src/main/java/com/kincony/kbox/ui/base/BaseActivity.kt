package com.kincony.kbox.ui.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Toast
import com.kincony.kbox.R

open abstract class BaseActivity : AppCompatActivity() {
    var mHandler = Handler()
    val sp by lazy { getSharedPreferences(getString(R.string.key_sp), Context.MODE_PRIVATE) }

    var canLoad = true
    var loadingCreate = false
    val loading by lazy {
        loadingCreate = true
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        AlertDialog.Builder(this, R.style.AppTheme_Transparent)
                .setCancelable(true)
                .setView(view)
                .create()
    }

    open fun showLoading() {
        loading?.show()
    }

    open fun closeLoading() {
        loading?.dismiss()
    }

    @Synchronized
    fun setValue(key: String, value: String?) {
        sp.edit().putString(key, value).commit()
    }

    @Synchronized
    fun getValue(key: String, dfValue: String = ""): String {
        return sp.getString(key, dfValue)
    }


    fun showToast(msg: String?) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(this@BaseActivity, msg, Toast.LENGTH_SHORT).show()
        } else {
            mHandler.post {
                Toast.makeText(this@BaseActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (canLoad) {
            if (getLayoutId() != 0) {
                setContentView(getLayoutId())
            }
            initView()
        }
    }

    abstract fun getLayoutId(): Int
    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
        if (loadingCreate) {
            if (loading?.isShowing == true) {
                loading?.cancel()
            }
        }
    }
}