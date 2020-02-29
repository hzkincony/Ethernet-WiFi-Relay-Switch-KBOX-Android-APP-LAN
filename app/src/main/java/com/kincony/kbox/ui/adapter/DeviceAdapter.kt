package com.kincony.kbox.ui.adapter

import android.support.v7.widget.SwitchCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kincony.kbox.R
import com.kincony.kbox.net.data.Device
import com.kincony.kbox.ui.base.ImageLoader

/**
 * 设备适配器
 */
class DeviceAdapter : BaseQuickAdapter<Device, BaseViewHolder> {

    constructor() : super(R.layout.item_device)

    override fun convert(helper: BaseViewHolder, item: Device) {
        helper.setText(R.id.value, item.name)
        helper.getView<SwitchCompat>(R.id.mSwitch).isChecked = item.open
        helper.getView<SwitchCompat>(R.id.mSwitch).isClickable = false

        ImageLoader.load(mContext, item.icon, helper.getView(R.id.icon))

        helper.addOnClickListener(R.id.mSwitchClick)

    }
}