package com.kincony.kbox.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kincony.kbox.R
import com.kincony.kbox.net.data.NetAddress

/**
 * 地址适配器
 */
class AddressAdapter : BaseQuickAdapter<NetAddress, BaseViewHolder> {

    constructor() : super(R.layout.item_address)

    override fun convert(helper: BaseViewHolder, item: NetAddress) {
        helper.setText(R.id.ip, "IP:${item.ip}")
        helper.setText(R.id.port, "Port:${item.port}")
    }
}