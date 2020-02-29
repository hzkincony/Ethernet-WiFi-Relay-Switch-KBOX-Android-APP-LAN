package com.kincony.kbox.ui

import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import com.kincony.kbox.R
import com.kincony.kbox.net.data.IconEvent
import com.kincony.kbox.ui.adapter.IconAdapter
import com.kincony.kbox.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_icon_select.*
import org.greenrobot.eventbus.EventBus

class IconSelectActivity : BaseActivity() {
    var icons = arrayListOf(R.drawable.icon5, R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9,
            R.drawable.icon10, R.drawable.icon11, R.drawable.icon12, R.drawable.icon13, R.drawable.icon14, R.drawable.icon15, R.drawable.icon16,
            R.drawable.set1, R.drawable.set2, R.drawable.set3, R.drawable.set4, R.drawable.set5, R.drawable.set6, R.drawable.set7)

    companion object {
        fun start(context: Context?, icon: Int) {
            val intent = Intent(context, IconSelectActivity::class.java)
            intent.putExtra("icon", icon)
            context?.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_icon_select
    }

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        var adapter = IconAdapter()
        adapter.setNewData(icons)
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this, 4)
        adapter.setOnItemClickListener { adapter, view, position ->
            var icon = icons[position]
            EventBus.getDefault().post(IconEvent(icon))
            finish()
        }
    }


}
