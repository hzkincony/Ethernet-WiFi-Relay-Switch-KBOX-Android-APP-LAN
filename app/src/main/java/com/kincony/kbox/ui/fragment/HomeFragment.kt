package com.kincony.kbox.ui.fragment

import android.content.Context
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import com.kincony.kbox.R
import com.kincony.kbox.net.BoxCommand
import com.kincony.kbox.net.data.NetAddress
import com.kincony.kbox.net.Worker
import com.kincony.kbox.net.data.Device
import com.kincony.kbox.net.data.DeviceChange
import com.kincony.kbox.net.data.RefreshAddressEvent
import com.kincony.kbox.net.data.database.KBoxDatabase
import com.kincony.kbox.ui.DeviceEditActivity
import com.kincony.kbox.ui.adapter.DeviceAdapter
import com.kincony.kbox.ui.base.BaseFragment
import com.kincony.kbox.utils.IPUtils
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {
    private val KEY = "devices"

    private var optionsInt = 2
    var worker = Worker(Handler())
    var deviceList = ArrayList<Device>()
    var adapter: DeviceAdapter? = null

    override fun getLayoutId() = R.layout.fragment_home

    override fun initView() {
        refresh.setOnRefreshListener {
            readState()
        }
        recycler.layoutManager = LinearLayoutManager(context)
        adapter = DeviceAdapter()
        recycler.adapter = adapter


        var itemTouchHelper = ItemTouchHelper(Callback(adapter, deviceList))
        itemTouchHelper.attachToRecyclerView(recycler)

        adapter?.setNewData(deviceList)

        adapter?.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.mSwitchClick -> {
                    var device = deviceList[position]
                    changeState(device)
                }
            }
        }
        adapter?.setOnItemClickListener { adapter, view, position ->
            var device = deviceList[position]
            DeviceEditActivity.start(context, device.deviceId, device.name, device.icon)
            false
        }

        add.setOnClickListener {
            var dialog = getLoadingDialog(context)
            dialog?.show()
        }
        EventBus.getDefault().register(this)
        loadDevice()
    }

    @Subscribe
    public fun refreshDevice(event: RefreshAddressEvent) {
        loadDevice()
    }

    @Subscribe
    public fun setDeviceName(event: DeviceChange) {
        var d: Device? = null
        for (i in deviceList) {
            if (i.deviceId == event.id) {
                d = i;
            }
        }
        d?.name = event.name
        d?.icon = event.icon
        if (d != null) {
            KBoxDatabase.getInstance(context).deviceDao.updateDevice(d)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    /**
     * 从数据库读取设备
     */
    private fun loadDevice() {
        deviceList.clear()
        adapter?.notifyDataSetChanged()

        var devices = KBoxDatabase.getInstance(context).deviceDao.allDevice
        var address = KBoxDatabase.getInstance(context).addressDao.allAddress
        for (d in devices) {
            for (a in address) {
                if (a.id == d.addressId) {
                    d.address = a
                }
            }
        }

        deviceList.addAll(devices)
        adapter?.notifyDataSetChanged()
        refresh.isRefreshing = true
        readState()
    }

    /**
     * 插入设备
     * model:2,4,8,16,32
     */
    private fun addDevice(address: NetAddress, model: Int) {
        var allAddress = KBoxDatabase.getInstance(context).addressDao.allAddress
        var t: NetAddress? = null
        for (a in allAddress) {
            if (a == address) {
                t = a
            }
        }
        if (t != null) {
            showToast(resources.getString(R.string.add_already))
            return
        }

        KBoxDatabase.getInstance(context).addressDao.insertAddress(address)
        var temp = KBoxDatabase.getInstance(context).addressDao.getAddress(address.ip, address.port)

        var size = deviceList.size
        var index = 0
        var list = ArrayList<Device>()
        for (i in 1..model) {
            list.add(Device(temp, i, size + index++))
        }
        KBoxDatabase.getInstance(context).deviceDao.insertDevice(list)

        var readList = KBoxDatabase.getInstance(context).deviceDao.allDevice
        var address = KBoxDatabase.getInstance(context).addressDao.allAddress
        for (d in readList) {
            for (a in address) {
                if (a.id == d.addressId) {
                    d.address = a
                }
            }
        }

        deviceList.addAll(readList)
        adapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        changeDate()
        super.onPause()
    }

    private fun changeDate() {
        var index = 0
        for (i in deviceList) {
            i.index = index++
        }
        KBoxDatabase.getInstance(context).deviceDao.updateDevice(deviceList)
    }

    private fun readState() {
        var address = KBoxDatabase.getInstance(context).addressDao.allAddress
        for (i in address) {
            worker.sendCommand(i, BoxCommand.readAll(), {
                var result = it.split(",")
                if (result[result.size - 1] == "OK") {
                    var first = it.indexOf(",")
                    var last = it.lastIndexOf(",")
                    if ((first != last) and (first != -1)) {
                        var sub = it.substring(first + 1, last)
                        var subArray = sub.split(",")
                        var devices = findDeviceByAddress(i)
                        when (subArray.size) {
                            1 -> {//2,4,8
                                var r0 = Integer.valueOf(subArray[0])
                                for (d in devices) {
                                    d.open = readIfOpen(r0, d.number)
                                }
                            }
                            2 -> {//16
                                var r0 = Integer.valueOf(subArray[0])
                                var r1 = Integer.valueOf(subArray[1])
                                for (d in devices) {
                                    if (d.number > 8) {
                                        d.open = readIfOpen(r0, d.number)
                                    } else {
                                        d.open = readIfOpen(r1, d.number)
                                    }
                                }
                            }
                            3 -> {//32
                                var r0 = Integer.valueOf(subArray[0])
                                var r1 = Integer.valueOf(subArray[1])
                                var r2 = Integer.valueOf(subArray[2])
                                var r3 = Integer.valueOf(subArray[3])
                                for (d in devices) {
                                    if (d.number > 24) {
                                        d.open = readIfOpen(r0, d.number)
                                    } else if (d.number > 16) {
                                        d.open = readIfOpen(r1, d.number)
                                    } else if (d.number > 8) {
                                        d.open = readIfOpen(r2, d.number)
                                    } else {
                                        d.open = readIfOpen(r3, d.number)
                                    }
                                }
                            }
                        }
                        Log.e("TAG", "readAll:${sub}")
                    }
                } else {
                    showToast("BoxCommand.readAll() + \"->ERROR\"")
                    Log.e("TAG", BoxCommand.readAll() + "->ERROR")
                }
                adapter?.notifyDataSetChanged()
                refresh.isRefreshing = false
            }, { fail ->
                showToast(fail.message)
                refresh.isRefreshing = false
            })
        }


        if (address.size == 0) {
            refresh.isRefreshing = false
        }
    }

    private fun readIfOpen(number: Int, index: Int): Boolean {
        var tempIndex = index
        when (index) {
            in 1..8 -> {
                tempIndex = index
            }
            in 9..16 -> {
                tempIndex = index - 8
            }
            in 17..24 -> {
                tempIndex = index - 16
            }
            in 25..32 -> {
                tempIndex = index - 24
            }
        }
        var result = false
        when (tempIndex) {
            1 -> {
                result = (number and 0b00000001) == 0b00000001
            }
            2 -> {
                result = (number and 0b00000010) == 0b00000010
            }
            3 -> {
                result = (number and 0b00000100) == 0b00000100
            }
            4 -> {
                result = (number and 0b00001000) == 0b00001000
            }
            5 -> {
                result = (number and 0b00010000) == 0b00010000
            }
            6 -> {
                result = (number and 0b00100000) == 0b00100000
            }
            7 -> {
                result = (number and 0b01000000) == 0b01000000
            }
            8 -> {
                result = (number and 0b10000000) == 0b10000000
            }
        }
        return result
    }

    fun findDeviceByAddress(address: NetAddress): ArrayList<Device> {
        var result = ArrayList<Device>()
        for (d in deviceList) {
            if (d.address == address) {
                result.add(d)
            }
        }
        return result
    }

    private fun changeState(device: Device) {
        worker.sendCommand(device.address, BoxCommand.set(device.number, if (device.open) 0 else 1), { it ->
            Log.e("TAG", BoxCommand.set(device.number, if (device.open) 0 else 1) + "->" + it)
            var result = it.split(",")
            if (result[result.size - 1] == "OK") {
                device.open = Integer.valueOf(result[result.size - 2]) == 1
            } else {
                device.open = device.open
                Log.e(KEY, "${device.address}_${device.number}--->ERROR")
            }
            adapter?.notifyDataSetChanged()
        }, {})
    }

    private fun getLoadingDialog(context: Context?): AlertDialog? {
        var dialog: AlertDialog? = null
        if (context != null) {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null)
            var ip = view.findViewById<EditText>(R.id.ip)
            var port = view.findViewById<EditText>(R.id.port)
            var model = view.findViewById<AppCompatSpinner>(R.id.model)
            model.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            optionsInt = 2
                        }
                        1 -> {
                            optionsInt = 4
                        }
                        2 -> {
                            optionsInt = 8
                        }
                        3 -> {
                            optionsInt = 16
                        }
                        4 -> {
                            optionsInt = 32
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
            dialog = AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setView(view)
                    .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                    }
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        if (!IPUtils.isIp(ip.text.toString())) {
                            showToast(resources.getString(R.string.ip_alert))
                            return@setPositiveButton
                        }
                        if (port.text.toString().isEmpty()) {
                            showToast(resources.getString(R.string.port_alert))
                            return@setPositiveButton
                        }
                        addDevice(NetAddress(ip.text.toString(), Integer.decode(port.text.toString()), optionsInt), optionsInt)
                    }
                    .create()
        }
        return dialog
    }

    class Callback(var adapter: DeviceAdapter?, var list: ArrayList<Device>) : ItemTouchHelper.Callback() {
        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            var swipFlag = 0;
            var dragflag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragflag, swipFlag)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition);
            Collections.swap(list, viewHolder.adapterPosition, target.adapterPosition);
            return true;
        }


        override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true;
        }

    }

}