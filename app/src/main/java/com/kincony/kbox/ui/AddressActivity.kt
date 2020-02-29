package com.kincony.kbox.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.kincony.kbox.R
import com.kincony.kbox.net.data.database.KBoxDatabase
import com.kincony.kbox.net.data.NetAddress
import com.kincony.kbox.net.data.RefreshAddressEvent
import com.kincony.kbox.ui.adapter.AddressAdapter
import com.kincony.kbox.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_address.*
import org.greenrobot.eventbus.EventBus

class AddressActivity : BaseActivity() {
    var list = ArrayList<NetAddress>()
    var adapter = AddressAdapter()

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, AddressActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_address
    }

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        var address = KBoxDatabase.getInstance(this).addressDao.allAddress
        list.addAll(address)
        adapter.setNewData(list)

        adapter.setOnItemLongClickListener { adapter, view, position ->
            var address = list[position]
            getLoadingDialog(this@AddressActivity, address)?.show()
            false
        }
    }

    private fun deleteAddress(address: NetAddress) {
        KBoxDatabase.getInstance(this).addressDao.delete(address)
        list.remove(address)
        adapter.notifyDataSetChanged()
        EventBus.getDefault().post(RefreshAddressEvent())
    }

    private fun getLoadingDialog(context: Context?, address: NetAddress): AlertDialog? {
        var dialog: AlertDialog? = null
        if (context != null) {
            dialog = AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(resources.getString(R.string.Message))
                    .setMessage(resources.getString(R.string.message_delete))
                    .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                    }
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        deleteAddress(address)
                    }
                    .create()
        }
        return dialog
    }


}

