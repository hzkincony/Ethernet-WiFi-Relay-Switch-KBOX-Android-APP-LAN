package com.kincony.kbox.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.kincony.kbox.R
import com.kincony.kbox.net.data.Device
import com.kincony.kbox.net.data.DeviceChange
import com.kincony.kbox.net.data.IconEvent
import com.kincony.kbox.ui.base.BaseActivity
import com.kincony.kbox.ui.base.ImageLoader
import kotlinx.android.synthetic.main.activity_device_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DeviceEditActivity : BaseActivity() {
    var iIcon: Int = 0

    companion object {
        fun start(context: Context?, id: Int, name: String, icon: Int) {
            val intent = Intent(context, DeviceEditActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            intent.putExtra("icon", icon)
            context?.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_device_edit
    }

    override fun initView() {
        var iId = intent.getIntExtra("id", 0)
        var sName = intent.getStringExtra("name")
        iIcon = intent.getIntExtra("icon", 0)
        name.setText(sName)
        ImageLoader.load(this, iIcon, icon)
        back.setOnClickListener {
            finish()
        }
        iconLay.setOnClickListener {
            IconSelectActivity.start(this, 0)
        }

        ok.setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString())) {
                showToast(resources.getString(R.string.name_not_empty))
                return@setOnClickListener
            }
            EventBus.getDefault().post(DeviceChange(iId, name.text.toString(), iIcon))
            finish()
        }
        EventBus.getDefault().register(this)
    }

    @Subscribe
    fun receiveIcon(event: IconEvent) {
        this.iIcon = event.icon
        ImageLoader.load(this, iIcon, icon)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
