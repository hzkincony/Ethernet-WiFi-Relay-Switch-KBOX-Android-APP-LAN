package com.kincony.kbox.ui.base

import android.support.v4.app.Fragment
import android.view.View

class FragmentHolder {
    var mFragment: Fragment? = null
    var mClx: Class<*>? = null
    var mTag: String = ""
    var view: View? = null
}