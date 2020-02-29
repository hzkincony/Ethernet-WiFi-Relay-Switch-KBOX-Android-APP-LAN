package com.kincony.kbox.ui.fragment

import com.kincony.kbox.R
import com.kincony.kbox.ui.AddressActivity
import com.kincony.kbox.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_setting

    override fun initView() {
        device.setOnClickListener {
            AddressActivity.start(context)
        }
    }
}