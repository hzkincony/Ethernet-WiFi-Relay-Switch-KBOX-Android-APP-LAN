package com.kincony.kbox.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kincony.kbox.R
import com.kincony.kbox.ui.base.ImageLoader

/**
 * 图标适配器
 */
class IconAdapter : BaseQuickAdapter<Int, BaseViewHolder> {

    constructor() : super(R.layout.item_icon)

    override fun convert(helper: BaseViewHolder, item: Int) {
        ImageLoader.load(mContext, item, helper.getView(R.id.icon))
    }
}