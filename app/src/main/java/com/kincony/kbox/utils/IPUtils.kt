package com.kincony.kbox.utils

import com.kincony.kbox.net.data.Device
import com.kincony.kbox.net.data.NetAddress
import java.util.regex.Pattern

object IPUtils {

    /** * 判断是否为合法IP * @return the ip */
    fun isIp(ipAddress: String): Boolean {
        var ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"
        var pattern = Pattern.compile(ip)
        var matcher = pattern.matcher(ipAddress)
        return matcher.matches()
    }

    fun getDefaultName(address: NetAddress, number: Int): String {
        var result = ""
        var ipArray = address.ip?.split(".")
        if (ipArray?.size != 4) {
            result = "null:${address?.port}_${number}"
        } else {
            result = "${ipArray[3]}:${address?.port}_${number}"
        }
        return result
    }
}